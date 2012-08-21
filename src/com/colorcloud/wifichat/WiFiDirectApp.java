package com.colorcloud.wifichat;

import static com.colorcloud.wifichat.Constants.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Message;
import android.util.Log;

public class WiFiDirectApp extends Application {

	private static final String TAG = "PTP_APP";
	
	WifiP2pManager mP2pMan = null;;
	Channel mP2pChannel = null;
	boolean mP2pConnected = false;
	String mMyAddr = null;
	String mDeviceName = null;   // the p2p name that is configurated from UI.
	
	WifiP2pDevice mThisDevice = null;
	WifiP2pInfo mP2pInfo = null;  // set when connection info available, reset when WIFI_P2P_CONNECTION_CHANGED_ACTION
	
	boolean mIsServer = false;
	
	WiFiDirectActivity mHomeActivity = null;
	List<WifiP2pDevice> mPeers = new ArrayList<WifiP2pDevice>();  // update on every peers available
	JSONArray mMessageArray = new JSONArray();		// limit to the latest 50 messages
	
	@Override
    public void onCreate() {
        super.onCreate();
    }
	
	/**
	 * whether p2p is enabled in this device. 
	 * my bcast listener always gets enable/disable intent and persist to shared pref
	 */
	public boolean isP2pEnabled() {
		String state = AppPreferences.getStringFromPref(this, AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED);
		if ( state != null && "1".equals(state.trim())){
			return true;
		}
		return false;
	}
	
	/**
     * upon p2p connection available, group owner start server socket channel
     * start socket server and select monitor the socket
     */
    public void startSocketServer() {
    	Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
    	msg.what = MSG_STARTSERVER;
    	ConnectionService.getInstance().getHandler().sendMessage(msg);
    }
    
    /**
     * upon p2p connection available, non group owner start socket channel connect to group owner.
     */
    public void startSocketClient(String hostname) {
    	Log.d(TAG, "startSocketClient : client connect to group owner : " + hostname);
    	Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
    	msg.what = MSG_STARTCLIENT;
    	msg.obj = hostname;
    	ConnectionService.getInstance().getHandler().sendMessage(msg);
    }
    
    /**
     * check whether there exists a connected peer.
     */
    public WifiP2pDevice getConnectedPeer(){
    	WifiP2pDevice peer = null;
		for(WifiP2pDevice d : mPeers ){ 
    		PTPLog.d(TAG, "getConnectedPeer : device : " + d.deviceName + " status: " + ConnectionService.getDeviceStatus(d.status));
    		if( d.status == WifiP2pDevice.CONNECTED){
    			peer = d;
    		}
    	}
    	return peer;
    }
    
    /**
     * insert a json string msg into messages json array
     */
    public void shiftInsertMessage(String jsonmsg){
    	JSONObject jsonobj = JSONUtils.getJsonObject(jsonmsg);
    	mMessageArray.put(jsonobj);
    	mMessageArray = JSONUtils.truncateJSONArray(mMessageArray, 10);  // truncate the oldest 10.
    }
    
    public String shiftInsertMessage(MessageRow row) {
    	JSONObject jsonobj = MessageRow.getAsJSONObject(row);
    	if( jsonobj != null ){
    		mMessageArray.put(jsonobj);
    	}
    	mMessageArray = JSONUtils.truncateJSONArray(mMessageArray, 10);  // truncate the oldest 10.
    	return jsonobj.toString();
    }
    
    public void clearMessages() {
    	mMessageArray = new JSONArray();
    }
    
    /**
     * get the intent to lauch any activity
     */
    public Intent getLauchActivityIntent(Class<?> cls, String initmsg){
    	Intent i = new Intent(this, cls);
    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	i.putExtra("FIRST_MSG", initmsg);
    	return i;
    }
    
    public void setMyAddr(String addr){
    	mMyAddr = addr;
    }
    
	public static class PTPLog {
		public static void i(String tag, String msg) {
            Log.i(tag, msg);
        }
		public static void d(String tag, String msg) {
            Log.d(tag, msg);
        }
		public static void e(String tag, String msg) {
            Log.e(tag, msg);
        }
	}

}
