package com.colorcloud.wifichat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.colorcloud.wifichat.WiFiDirectApp.PTPLog;

/**
 * chat fragment attached to main activity.
 */
public class ChatFragment extends ListFragment {
	private static final String TAG = "PTP_ChatFrag";
	
	WiFiDirectApp mApp = null; 
	private static MainActivity mActivity = null;
	
	private ArrayList<MessageRow> mMessageList = null;   // a list of chat msgs.
    private ArrayAdapter<MessageRow> mAdapter= null;
    
    // private String mMyAddr;
    
	/**
     * Static factory to create a fragment object from tab click.
     */
    public static ChatFragment newInstance(Activity activity, String groupOwnerAddr, String msg) {
    	ChatFragment f = new ChatFragment();
    	mActivity = (MainActivity)activity;
    	
        Bundle args = new Bundle();
        args.putString("groupOwnerAddr", groupOwnerAddr);
        args.putString("initMsg", msg);
        f.setArguments(args);
        Log.d(TAG, "newInstance :" + groupOwnerAddr + " : " + msg);
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {  // this callback invoked after newInstance done.  
        super.onCreate(savedInstanceState);
        mApp = (WiFiDirectApp)mActivity.getApplication();
        
        setRetainInstance(true);   // Tell the framework to try to keep this fragment around during a configuration change.
    }
    
    /**
     * the data you place in the Bundle here will be available in the Bundle given to onCreate(Bundle), etc.
     * only works when your activity is destroyed by android platform. If the user closed the activity, no call of this.
     * http://www.eigo.co.uk/Managing-State-in-an-Android-Activity.aspx
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	outState.putParcelableArrayList("MSG_LIST", mMessageList);
    	Log.d(TAG, "onSaveInstanceState. " + mMessageList.get(0).mMsg);
    }
    
    /**
     * no matter your fragment is declared in main activity layout, or dynamically added thru fragment transaction
     * You need to inflate fragment view inside this function. 
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// inflate the fragment's res layout. 
        View contentView = inflater.inflate(R.layout.chat_frag, container, false);  // no care whatever container is.
        final EditText inputEditText = (EditText)contentView.findViewById(R.id.edit_input);
        final Button sendBtn = (Button)contentView.findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// send the chat text in current line to the server
				String inputMsg = inputEditText.getText().toString();
				inputEditText.setText("");
				InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);
				MessageRow row = new MessageRow(mApp.mDeviceName, inputMsg, null);
				appendChatMessage(row);
				String jsonMsg = mApp.shiftInsertMessage(row);
				PTPLog.d(TAG, "sendButton clicked: sendOut data : " + jsonMsg);
				mActivity.pushOutMessage(jsonMsg);
			}
        });
        
        String groupOwnerAddr = getArguments().getString("groupOwnerAddr");
        String msg = getArguments().getString("initMsg");
        PTPLog.d(TAG, "onCreateView : fragment view created: msg :" + msg);
        
    	if( savedInstanceState != null ){
            mMessageList = savedInstanceState.getParcelableArrayList("MSG_LIST");
            Log.d(TAG, "onCreate : savedInstanceState: " + mMessageList.get(0).mMsg);
        }else if( mMessageList == null ){
        	// no need to setContentView, just setListAdapter, but listview must be android:id="@android:id/list"
            mMessageList = new ArrayList<MessageRow>();
            jsonArrayToList(mApp.mMessageArray, mMessageList);
            Log.d(TAG, "onCreate : jsonArrayToList : " + mMessageList.size() );
        }else {
        	Log.d(TAG, "onCreate : setRetainInstance good : ");
        }
        
        mAdapter = new ChatMessageAdapter(mActivity, mMessageList);
        
        setListAdapter(mAdapter);  // list fragment data adapter 
        
        PTPLog.d(TAG, "onCreate chat msg fragment: devicename : " + mApp.mDeviceName + " : " + getArguments().getString("initMsg"));
        return contentView;
    }
    
    @Override 
    public void onDestroyView(){ 
    	super.onDestroyView(); 
    	Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {  // invoked after fragment view created.
        super.onActivityCreated(savedInstanceState);
        
        if( mMessageList.size() > 0){
        	getListView().smoothScrollToPosition(mMessageList.size()-1);
        }
        
        setHasOptionsMenu(true);
        Log.d(TAG, "onActivityCreated: chat fragment displayed ");
    }
    
    /**
     * add a chat message to the list, return the format the message as " sender_addr : msg "
     */
    public void appendChatMessage(MessageRow row) {
    	Log.d(TAG, "appendChatMessage: chat fragment append msg: " + row.mSender + " ; " + row.mMsg);
    	mMessageList.add(row);
    	getListView().smoothScrollToPosition(mMessageList.size()-1);
    	mAdapter.notifyDataSetChanged();  // notify the attached observer and views to refresh.
    	return;
    }
    
    private void jsonArrayToList(JSONArray jsonarray, List<MessageRow> list) {
    	try{
    		for(int i=0;i<jsonarray.length();i++){
    			MessageRow row = MessageRow.parseMesssageRow(jsonarray.getJSONObject(i));
    			PTPLog.d(TAG, "jsonArrayToList: row : " + row.mMsg);
    			list.add(row);
    		}
    	}catch(JSONException e){
    		PTPLog.e(TAG, "jsonArrayToList: " + e.toString());
    	}
    }
    
    /**
     * chat message adapter from list adapter.
     * Responsible for how to show data to list fragment list view.
     */
    final class ChatMessageAdapter extends ArrayAdapter<MessageRow> {

    	public static final int VIEW_TYPE_MYMSG = 0;
    	public static final int VIEW_TYPE_INMSG = 1;
    	public static final int VIEW_TYPE_COUNT = 2;    // msg sent by me, or all incoming msgs
    	private LayoutInflater mInflater;
    	
		public ChatMessageAdapter(Context context, List<MessageRow> objects){
			super(context, 0, objects);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
		
		@Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }
		
		@Override
        public int getItemViewType(int position) {
			MessageRow item = this.getItem(position);
			if ( item.mSender.equals(mApp.mDeviceName )){
				return VIEW_TYPE_MYMSG;
			}
			return VIEW_TYPE_INMSG;			
		}
		
		/**
		 * assemble each row view in the list view.
		 * http://dl.google.com/googleio/2010/android-world-of-listview-android.pdf
		 */
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;  // old view to re-use if possible. Useful for Heterogeneous list with diff item view type.
			MessageRow item = this.getItem(position);
			boolean mymsg = false;
			
			if ( getItemViewType(position) == VIEW_TYPE_MYMSG){
				if( view == null ){
	            	view = mInflater.inflate(R.layout.chat_row_mymsg, null);  // inflate chat row as list view row.
	            }
				mymsg = true;
				// view.setBackgroundResource(R.color.my_msg_background);
			} else {
				if( view == null ){
	            	view = mInflater.inflate(R.layout.chat_row_inmsg, null);  // inflate chat row as list view row.
	            }
				// view.setBackgroundResource(R.color.in_msg_background);
			}
			
            TextView sender = (TextView)view.findViewById(R.id.sender);
            sender.setText(item.mSender);
            
            TextView msgRow = (TextView)view.findViewById(R.id.msg_row);
            msgRow.setText(item.mMsg);
            if( mymsg ){
            	msgRow.setBackgroundResource(R.color.my_msg_background);	
            }else{
            	msgRow.setBackgroundResource(R.color.in_msg_background);
            }
            
            TextView time = (TextView)view.findViewById(R.id.time);
            time.setText(item.mTime);
            
            Log.d(TAG, "getView : " + item.mSender + " " + item.mMsg + " " + item.mTime);
            return view;
		}
    }
}
