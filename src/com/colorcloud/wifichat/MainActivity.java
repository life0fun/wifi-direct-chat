package com.colorcloud.wifichat;

import static com.colorcloud.wifichat.Constants.*;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static final String TAG = "PTP_ChatAct";
	
	WiFiDirectApp mApp = null;
	ChatFragment mChatFrag = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		Intent i = getIntent();
		String initMsg = i.getStringExtra("FIRST_MSG");
		
		mApp = (WiFiDirectApp)getApplication(); 
		initFragment(initMsg);
	}
	
	/**
	 * init fragement with possible recvd start up message.
	 */
	public void initFragment(String initMsg) {
    	// to add fragments to your activity layout, just specify which viewgroup to place the fragment.
    	final FragmentTransaction ft = getFragmentManager().beginTransaction();
    	if( mChatFrag == null ){
    		//mChatFrag = ChatFragment.newInstance(this, ConnectionService.getInstance().mConnMan.mServerAddr);
    		mChatFrag = ChatFragment.newInstance(this, null, initMsg);
    	}
    	
    	Log.d(TAG, "initFragment : show chat fragment..." + initMsg);
    	// chat fragment on top, do not do replace, as frag_detail already hard coded in layout.
    	ft.add(R.id.frag_chat, mChatFrag, "chat_frag");
    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	ft.commit();
    }
	
	@Override
	public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: chat activity resume, register activity to connection service !");
        registerActivityToService(true);
    }
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause: chat activity closed, de-register activity from connection service !");
		registerActivityToService(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, " onDestroy: nothing... ");
	}
	
	/**
	 * set listview weight works, only when layout has only one LinearLayout.
	 */
	@Deprecated
	private void testWithListViewWeight() {
		List<String> mMessageList;   // a list of chat msgs.
	    ArrayAdapter<String> mAdapter;
	    
		mMessageList = new ArrayList<String>(200);
		for(int i=0;i<100;i++)
			mMessageList.add("User logged in");
        mAdapter = new ChatMessageAdapter(this, mMessageList);
        
        //setListAdapter(mAdapter);  // list fragment data adapter
        mAdapter.notifyDataSetChanged();  // notify the attached observer and views to refresh.
	}
	
	
    /**
     * list view adapter for this activity when testing without using fragment!
     * deprecated, as we should always use fragment, template.
     */
	@Deprecated
    final class ChatMessageAdapter extends ArrayAdapter<String> {

    	private LayoutInflater mInflater;
    	
		public ChatMessageAdapter(Context context, List<String> objects){
			super(context, 0, objects);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
		
		@Override
        public int getItemViewType(int position) {
			return IGNORE_ITEM_VIEW_TYPE;   // do not care			
		}
		
		/**
		 * assemble each row view in the list view.
		 */
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;  // old view to re-use if possible. Useful for Heterogeneous list with diff item view type.
            String item = this.getItem(position);
            
            if( view == null ){
            	view = mInflater.inflate(R.layout.msg_row, null);
            }
            
            TextView msgRow = (TextView)view.findViewById(R.id.msg_row);
            msgRow.setText(item);
            
            return view;
		}
    }
    
    /**
     * register this activity to service process, so that service later can update list view.
     * In AsyncTask, the activity itself is passed to the constructor of asyncTask, hence later
     * onPostExecute() can do mActivity.mCommentsAdapter.notifyDataSetChanged();
     * Just need to be careful of reference to avoid mem leak. 
     */
    protected void registerActivityToService(boolean register){
    	if( ConnectionService.getInstance() != null ){
	    	Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
	    	msg.what = MSG_REGISTER_ACTIVITY;
	    	msg.obj = this;
	    	msg.arg1 = register ? 1 : 0;
	    	ConnectionService.getInstance().getHandler().sendMessage(msg);
    	}
    }
    
    /**
     * post send msg to service to handle it in background.
     */
    public void pushOutMessage(String jsonstring) {
    	Log.d(TAG, "pushOutMessage : " + jsonstring);
    	Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
    	msg.what = MSG_PUSHOUT_DATA;
    	msg.obj = jsonstring;
    	ConnectionService.getInstance().getHandler().sendMessage(msg);
    }
    
    /**
     * show the msg in chat fragment, update view must be from ui thread.
     */
    public void showMessage(final MessageRow row){
    	runOnUiThread(new Runnable() {
    		@Override public void run() {
    			Log.d(TAG, "showMessage : " + row.mMsg);
    			if( mChatFrag != null ){
    				mChatFrag.appendChatMessage(row);
    			}
    		}
    	});
    }
}
