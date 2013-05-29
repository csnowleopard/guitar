package edu.towson.cosc451.nimbian;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class addtodo extends Activity {
	private Button button1;
	private Button button2;
	private Button button3;
	private RadioButton Rbutton1;
	private RadioButton Rbutton2;
	private RadioButton Rbutton3;
	private RadioButton Rbutton4;
	private RadioButton Rbutton5;
	private EditText edit;
	private TextView priority;
	private TextView title;
	private CheckBox check;
	public static final String ID1 = "TASK NAME";
	public static final String ID2 = "DONE";
	public static final String ID3 = "PRIORITY";
	
	
	private OnClickListener clickListener = new OnClickListener(){
    	public void onClick(View v){
    		Rbutton2.setChecked(false);
    		
    		
    	}
    };
    private OnClickListener clickListener2 = new OnClickListener(){
    	public void onClick(View v){
    		Rbutton1.setChecked(false);
    		
    		
    	}
    };
    
    private OnClickListener clickCancel = new OnClickListener(){
    	public void onClick(View v){
    		//finish();
		Intent addIntent = new Intent(addtodo.this,ToDoManagerActivity.class);
                startActivity(addIntent);
    		
    		
    	}
    };
    
    private OnClickListener clickReset = new OnClickListener(){
    	public void onClick(View v){
    		Rbutton1.setChecked(true);
    		Rbutton2.setChecked(false);
    		Rbutton3.setChecked(true);
    		Rbutton4.setChecked(false);
    		Rbutton5.setChecked(false);
    		edit.setText("");
    	}
    };
    
   /* private OnClickListener clickSubmit = new OnClickListener(){
    	public void onClick(View v){
    		title = (TextView)findViewById(R.id.edit_title);
            priority = (TextView)findViewById(R.id.edit_priority);
            check = (CheckBox)findViewById(R.id.checkBox1);
    		
    		title.setText(edit.getText().toString());
    		if (Rbutton2.isChecked()){
    			check.setChecked(true);
    		}
    		else{
    			check.setChecked(false);
    		}
    		if(Rbutton3.isChecked()){
    			priority.setText(R.string.low);
    		}
    		else if(Rbutton4.isChecked()){
    			priority.setText(R.string.medium);
    		}
    		else{
    			priority.setText(R.string.high);
    		}
    	}
    };*/
    
    private OnClickListener clickSubmit = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			String task = edit.getText().toString();
			edit.setText("");
			
			Boolean done = false;
			if (Rbutton1.isChecked()) {
				done = true;
			}
			else{
				done = false;
			}
			Rbutton2.setChecked(false);
			
			
			String priority;
			if (Rbutton3.isChecked()) {
				priority = "Low";
			}
			else if (Rbutton4.isChecked()) {
				priority = "Medium";
			}
			else{
				priority = "High";
			}
		
			
			Intent resultIntent = new Intent();
			
			resultIntent.putExtra(ID1, task);
			resultIntent.getBooleanExtra(ID2, done);
			resultIntent.putExtra(ID3, priority);
			setResult(Activity.RESULT_OK, resultIntent);
			//finish();
			Intent addIntent = new Intent(addtodo.this,ToDoManagerActivity.class);
                	startActivity(addIntent);
		}	
    };
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_todo);
        Rbutton1=(RadioButton)findViewById(R.id.radioButton1);
        Rbutton2=(RadioButton)findViewById(R.id.radioButton2);
        Rbutton3=(RadioButton)findViewById(R.id.radioButton3);
        Rbutton4=(RadioButton)findViewById(R.id.radioButton4);
        Rbutton5=(RadioButton)findViewById(R.id.radioButton5);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        edit=(EditText)findViewById(R.id.editText1);
        title = (TextView)findViewById(R.id.edit_title);
        priority = (TextView)findViewById(R.id.edit_priority);
        check = (CheckBox)findViewById(R.id.checkBox1);
        Rbutton1.setChecked(true);
        Rbutton3.setChecked(true);
        Rbutton1.setOnClickListener(clickListener);
        Rbutton2.setOnClickListener(clickListener2);
        button1.setOnClickListener(clickCancel);
        button2.setOnClickListener(clickReset);
        button3.setOnClickListener(clickSubmit);
        }

}
