package edu.towson.cosc451.nimbian;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnLongClickListener;

public class ToDoManagerActivity extends Activity {
    /** Called when the activity is first created. */
	private Button add_new;
	private WifiManager wifiManager;
	private MyAdapter adapter;
	private ListView listView;
    private OnClickListener clickListener = new OnClickListener(){
    	public void onClick(View v){
    		Intent addIntent = new Intent(ToDoManagerActivity.this,addtodo.class);
    		startActivity(addIntent);
    		
    	}
    };
    
   
   
    //@Override
    //protected void onCreateDialog(int id, Bundle args){
    	//AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	//dialog.setMessage("WiFi is disabled would you like to enable it");
    	//dialog.setCancelable(false);
    	//dialog.setPositiveButton("Enable",new DialogInterface.OnClickListener() {
			
	//		@Override
	//		public void onClick(DialogInterface dialog, int which) {
	//			wifiManager.setWifiEnabled(true);
				
	//		}
	//	});
	//	dialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
			
	//		@Override
	//		public void onClick(DialogInterface dialog, int which) {
	//			// TODO Auto-generated method stub
	//			dialog.cancel();
	//		}
	//	});
	//	AlertDialog alert = dialog.create();
	//	return alert;
    //}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
	wifiManager.setWifiEnabled(true);
        if(!wifiManager.isWifiEnabled()){
        	showDialog(1);
        }
        add_new = (Button) findViewById(R.id.add_new);
        add_new.setOnClickListener(clickListener);
        listView = (ListView) findViewById(R.id.listView1);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        /*listView.setLongClickable(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				int x;
				x = listView.getSelectedItemPosition();
				adapter.delete(x);
				adapter.notifyDataSetChanged();
				return false;
			}
        	
        });*/
        }
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK){
			String task = data.getStringExtra(addtodo.ID1);
			Boolean done = data.getBooleanExtra(addtodo.ID2, false);
			String priority = data.getStringExtra(addtodo.ID3);
			
			adapter.add(task, done, priority);
			adapter.notifyDataSetChanged();
		}
	}
    
    private class MyAdapter extends BaseAdapter {

    	private ArrayList<String> task = new ArrayList<String>();
    	private ArrayList<Boolean> check = new ArrayList<Boolean>();
    	private ArrayList<String> priority = new ArrayList<String>();
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return task.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return task.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		public void add(String name, Boolean done, String priorityStatus){
			task.add(name);
			check.add(done);
			priority.add(priorityStatus);
		}
		
		public void delete(int position) {
			task.remove(position);
			check.remove(position);
			priority.remove(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null) {
				View mInflater;
				convertView = getLayoutInflater().inflate(R.layout.job, parent, false);
			}
			TextView name = (TextView) convertView.findViewById(R.id.edit_title);
			name.setText(task.get(position).toString());
			CheckBox doneStatus = (CheckBox) convertView.findViewById(R.id.checkBox1);
			doneStatus.setChecked(check.get(position).booleanValue());
			TextView priorityLevel = (TextView) convertView.findViewById(R.id.edit_priority);
			priorityLevel.setText(priority.get(position).toString());
			
			return convertView;
		}
    
    
    }
}

