// This is mutant program.
// Author : ysma

package com.example.android.contactmanager;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public final class ContactManager extends android.app.Activity
{

    public static final java.lang.String TAG = "ContactManager";

    private android.widget.Button mAddAccountButton;

    private android.widget.ListView mContactList;

    private boolean mShowInvisible;

    private android.widget.CheckBox mShowInvisibleControl;

    public void onCreate( android.os.Bundle savedInstanceState )
    {
        Log.v( TAG, "Activity State: onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.contact_manager );
        mAddAccountButton = (android.widget.Button) findViewById( R.id.addContactButton );
        mContactList = (android.widget.ListView) findViewById( R.id.contactList );
        mShowInvisibleControl = (android.widget.CheckBox) findViewById( R.id.showInvisible );
        mShowInvisible = false;
        mShowInvisibleControl.setChecked( !mShowInvisible );
        mAddAccountButton.setOnClickListener( new android.view.View.OnClickListener(){
            public void onClick( android.view.View v )
            {
                Log.d( TAG, "mAddAccountButton clicked" );
                launchContactAdder();
            }
        } );
        mShowInvisibleControl.setOnCheckedChangeListener( new android.widget.CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged( android.widget.CompoundButton buttonView, boolean isChecked )
            {
                Log.d( TAG, "mShowInvisibleControl changed: " + isChecked );
                mShowInvisible = isChecked;
                populateContactList();
            }
        } );
        populateContactList();
    }

    private void populateContactList()
    {
        android.database.Cursor cursor = getContacts();
        java.lang.String[] fields = new java.lang.String[]{ ContactsContract.Data.DISPLAY_NAME };
        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter( this, R.layout.contact_entry, cursor, fields, new int[]{ R.id.contactEntryText } );
        mContactList.setAdapter( adapter );
    }

    private android.database.Cursor getContacts()
    {
        android.net.Uri uri = ContactsContract.Contacts.CONTENT_URI;
        java.lang.String[] projection = new java.lang.String[]{ ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
        java.lang.String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + (mShowInvisible ? "0" : "1") + "'";
        java.lang.String[] selectionArgs = null;
        java.lang.String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        return managedQuery( uri, projection, selection, selectionArgs, sortOrder );
    }

    protected void launchContactAdder()
    {
        android.content.Intent i = new android.content.Intent( this, com.example.android.contactmanager.ContactAdder.class );
        startActivity( i );
    }

}
