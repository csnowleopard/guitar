// This is mutant program.
// Author : ysma

package com.aut;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;


public class HelloAUTActivity extends android.app.Activity
{

    static final java.lang.String tag = (com.aut.HelloAUTActivity.class).getPackage().getName();

    boolean drawn = false;

    public void onCreate( android.os.Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        final android.widget.EditText colorText = (android.widget.EditText) findViewById( R.id.color_text );
        final android.widget.RadioGroup.OnCheckedChangeListener lShape = new android.widget.RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged( android.widget.RadioGroup group, int id )
            {
                Log.d( tag, "Rg Shape checked " + id );
                if (drawn) {
                    draw();
                }
            }
        };
        final android.widget.RadioGroup.OnCheckedChangeListener lColor = new android.widget.RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged( android.widget.RadioGroup group, int id )
            {
                Log.d( tag, "Rg Color checked: " + id );
                if (id == R.id.color_none) {
                    colorText.setText( "" );
                }
                if (drawn) {
                    draw();
                }
            }
        };
        final android.widget.RadioGroup rg1 = (android.widget.RadioGroup) findViewById( R.id.rg_shape );
        rg1.setOnCheckedChangeListener( lShape );
        final android.widget.RadioGroup rg2 = (android.widget.RadioGroup) findViewById( R.id.rg_color );
        rg2.setOnCheckedChangeListener( lColor );
        final android.widget.Button b1 = (android.widget.Button) findViewById( R.id.create );
        b1.setOnClickListener( new android.view.View.OnClickListener(){
            public void onClick( android.view.View v )
            {
                Log.d( tag, "Create clicked" );
                draw();
                drawn = true;
            }
        } );
        final android.widget.Button b2 = (android.widget.Button) findViewById( R.id.reset );
        b2.setOnClickListener( new android.view.View.OnClickListener(){
            public void onClick( android.view.View v )
            {
                Log.d( tag, "reset clicked" );
                synchronized (this)
{
                    drawn = false;
                }
                android.widget.FrameLayout canvas = (android.widget.FrameLayout) findViewById( R.id.canvas );
                colorText.setText( "" );
                android.widget.RadioGroup rg_shape = (android.widget.RadioGroup) findViewById( R.id.rg_shape );
                rg1.setOnCheckedChangeListener( null );
                rg_shape.check( R.id.shape_circle );
                rg1.setOnCheckedChangeListener( lShape );
                android.widget.RadioGroup rg_color = (android.widget.RadioGroup) findViewById( R.id.rg_color );
                rg2.setOnCheckedChangeListener( null );
                rg_color.check( R.id.color_none );
                rg2.setOnCheckedChangeListener( lColor );
                canvas.removeAllViews();
            }
        } );
    }

    private void draw()
    {
        android.widget.RadioGroup rg_color = (android.widget.RadioGroup) findViewById( R.id.rg_color );
        int co = rg_color.getCheckedRadioButtonId();
        java.lang.String color = "";
        if (co == R.id.color_color) {
            android.widget.EditText txt = (android.widget.EditText) findViewById( R.id.color_text );
            color = txt.getText().toString();
        }
        android.widget.RadioGroup rg_shape = (android.widget.RadioGroup) findViewById( R.id.rg_shape );
        int sh = rg_shape.getCheckedRadioButtonId();
        com.aut.Shape shape = new com.aut.Shape( HelloAUTActivity.this, sh == R.id.shape_square, color );
        android.widget.FrameLayout canvas = (android.widget.FrameLayout) findViewById( R.id.canvas );
        canvas.removeAllViews();
        canvas.addView( shape );
    }

    private void colors()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( this );
        builder.setTitle( R.string.color );
        final java.lang.CharSequence[] colors = { Shape.bk, Shape.bl, Shape.cy, Shape.gy, Shape.gr, Shape.ma, Shape.rd, Shape.wh, Shape.yl };
        builder.setSingleChoiceItems( colors, -1, new android.content.DialogInterface.OnClickListener(){
            public void onClick( android.content.DialogInterface dialog, int item )
            {
                final android.widget.RadioGroup rg2 = (android.widget.RadioGroup) findViewById( R.id.rg_color );
                if (rg2.getCheckedRadioButtonId() != R.id.color_color) {
                    rg2.check( R.id.color_color );
                }
                final android.widget.EditText colorText = (android.widget.EditText) findViewById( R.id.color_text );
                colorText.setText( colors[item] );
                if (drawn) {
                    draw();
                }
                dialog.cancel();
            }
        } );
        android.app.AlertDialog alert = builder.create();
        alert.setOwnerActivity( this );
        alert.show();
    }

    public boolean onCreateOptionsMenu( android.view.Menu menu )
    {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu, menu );
        return true;
    }

    public boolean onOptionsItemSelected( android.view.MenuItem item )
    {
        Log.d( tag, "menu selected: " + item.toString() );
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( this );
        switch (item.getItemId()) {
        case R.id.m_about :
            builder.setTitle( "Hello, AndroidGUITAR!" ).setPositiveButton( "OK", new android.content.DialogInterface.OnClickListener(){
                public void onClick( android.content.DialogInterface dialog, int id )
                {
                    dialog.cancel();
                }
            } );
            break;

        case R.id.m_shape :
            builder.setTitle( R.string.shape );
            final java.lang.CharSequence[] shapes = { getString( R.string.shape1 ), getString( R.string.shape2 ) };
            final int[] ids = { R.id.shape_circle, R.id.shape_square };
            builder.setSingleChoiceItems( shapes, -1, new android.content.DialogInterface.OnClickListener(){
                public void onClick( android.content.DialogInterface dialog, int item )
                {
                    final android.widget.RadioGroup rg1 = (android.widget.RadioGroup) findViewById( R.id.rg_shape );
                    rg1.check( ids[item] );
                    if (drawn) {
                        draw();
                    }
                    dialog.cancel();
                }
            } );
            break;

        case R.id.m_color :
            colors();
            return true;

        default  :
            return super.onOptionsItemSelected( item );

        }
        android.app.AlertDialog alert = builder.create();
        alert.setOwnerActivity( this );
        alert.show();
        return true;
    }

}
