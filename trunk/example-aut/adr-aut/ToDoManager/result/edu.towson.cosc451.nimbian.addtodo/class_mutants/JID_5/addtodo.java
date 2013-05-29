// This is mutant program.
// Author : ysma

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


public class addtodo extends android.app.Activity
{

    private android.widget.Button button1;

    private android.widget.Button button2;

    private android.widget.Button button3;

    private android.widget.RadioButton Rbutton1;

    private android.widget.RadioButton Rbutton2;

    private android.widget.RadioButton Rbutton3;

    private android.widget.RadioButton Rbutton4;

    private android.widget.RadioButton Rbutton5;

    private android.widget.EditText edit;

    private android.widget.TextView priority;

    private android.widget.TextView title;

    private android.widget.CheckBox check;

    public static final java.lang.String ID1 = "TASK NAME";

    public static final java.lang.String ID2 = "DONE";

    public static final java.lang.String ID3 = "PRIORITY";

    private android.view.View.OnClickListener clickListener = new android.view.View.OnClickListener(){
        public void onClick( android.view.View v )
        {
            Rbutton2.setChecked( false );
        }
    };

    private android.view.View.OnClickListener clickListener2 = new android.view.View.OnClickListener(){
        public void onClick( android.view.View v )
        {
            Rbutton1.setChecked( false );
        }
    };

    private android.view.View.OnClickListener clickCancel = new android.view.View.OnClickListener(){
        public void onClick( android.view.View v )
        {
            android.content.Intent addIntent = new android.content.Intent( addtodo.this, edu.towson.cosc451.nimbian.ToDoManagerActivity.class );
            startActivity( addIntent );
        }
    };

    private android.view.View.OnClickListener clickReset = new android.view.View.OnClickListener(){
        public void onClick( android.view.View v )
        {
            Rbutton1.setChecked( true );
            Rbutton2.setChecked( false );
            Rbutton3.setChecked( true );
            Rbutton4.setChecked( false );
            Rbutton5.setChecked( false );
            edit.setText( "" );
        }
    };

    private android.view.View.OnClickListener clickSubmit;

    public void onCreate( android.os.Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.new_todo );
        Rbutton1 = (android.widget.RadioButton) findViewById( R.id.radioButton1 );
        Rbutton2 = (android.widget.RadioButton) findViewById( R.id.radioButton2 );
        Rbutton3 = (android.widget.RadioButton) findViewById( R.id.radioButton3 );
        Rbutton4 = (android.widget.RadioButton) findViewById( R.id.radioButton4 );
        Rbutton5 = (android.widget.RadioButton) findViewById( R.id.radioButton5 );
        button1 = (android.widget.Button) findViewById( R.id.button1 );
        button2 = (android.widget.Button) findViewById( R.id.button2 );
        button3 = (android.widget.Button) findViewById( R.id.button3 );
        edit = (android.widget.EditText) findViewById( R.id.editText1 );
        title = (android.widget.TextView) findViewById( R.id.edit_title );
        priority = (android.widget.TextView) findViewById( R.id.edit_priority );
        check = (android.widget.CheckBox) findViewById( R.id.checkBox1 );
        Rbutton1.setChecked( true );
        Rbutton3.setChecked( true );
        Rbutton1.setOnClickListener( clickListener );
        Rbutton2.setOnClickListener( clickListener2 );
        button1.setOnClickListener( clickCancel );
        button2.setOnClickListener( clickReset );
        button3.setOnClickListener( clickSubmit );
    }

}
