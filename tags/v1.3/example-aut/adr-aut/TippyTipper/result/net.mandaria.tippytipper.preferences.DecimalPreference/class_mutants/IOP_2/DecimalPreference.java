// This is mutant program.
// Author : ysma

package net.mandaria.tippytipper.preferences;


import net.mandaria.tippytipper.widgets.*;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class DecimalPreference extends android.preference.DialogPreference
{

    private static final java.lang.String androidns = "http://schemas.android.com/apk/res/android";

    private static final java.lang.String appns = "http://schemas.android.com/apk/res/net.mandaria.tippytipper";

    private net.mandaria.tippytipper.widgets.NumberPicker mPickInteger;

    private net.mandaria.tippytipper.widgets.NumberPicker mPickDecimal;

    private android.widget.TextView mSplashText;

    private android.widget.TextView mValueText;

    private android.content.Context mContext;

    private java.lang.String mDialogMessage;

    private java.lang.String mSuffix;

    private float mDefault;

    private float mValue = 0;

    private int mInteger;

    private int mDecimal = 0;

    public DecimalPreference( android.content.Context context, android.util.AttributeSet attrs )
    {
        super( context, attrs );
        mContext = context;
        mDialogMessage = attrs.getAttributeValue( androidns, "dialogMessage" );
        mSuffix = attrs.getAttributeValue( androidns, "text" );
        mDefault = attrs.getAttributeIntValue( androidns, "defaultValue", 0 );
    }

    protected android.view.View onCreateDialogView()
    {
        android.widget.TableLayout.LayoutParams params;
        android.widget.TableLayout layout = new android.widget.TableLayout( mContext );
        layout.setPadding( 6, 6, 6, 6 );
        mSplashText = new android.widget.TextView( mContext );
        if (mDialogMessage != null) {
            mSplashText.setText( mDialogMessage );
        }
        android.widget.TableRow row_header = new android.widget.TableRow( mContext );
        row_header.addView( mSplashText );
        mPickInteger = new net.mandaria.tippytipper.widgets.NumberPicker( mContext );
        mPickDecimal = new net.mandaria.tippytipper.widgets.NumberPicker( mContext );
        mPickDecimal.setFormatter( NumberPicker.THREE_DIGIT_FORMATTER );
        android.widget.TextView dot = new android.widget.TextView( mContext );
        dot.setText( "." );
        dot.setTextSize( 32 );
        android.widget.TextView percent = new android.widget.TextView( mContext );
        percent.setText( "%" );
        percent.setTextSize( 32 );
        android.widget.TableRow row_one = new android.widget.TableRow( mContext );
        row_one.setGravity( Gravity.CENTER );
        row_one.addView( mPickInteger );
        row_one.addView( dot );
        row_one.addView( mPickDecimal );
        row_one.addView( percent );
        layout.addView( row_header );
        android.widget.TableLayout table_main = new android.widget.TableLayout( mContext );
        table_main.addView( row_one );
        android.widget.TableRow row_main = new android.widget.TableRow( mContext );
        row_main.setGravity( Gravity.CENTER_HORIZONTAL );
        row_main.addView( table_main );
        layout.addView( row_main );
        if (shouldPersist()) {
            mValue = getPersistedFloat( mDefault );
        }
        bindData();
        return layout;
    }

    private void bindData()
    {
        mInteger = (int) Math.floor( mValue );
        float decimal = mValue * 1000 - mInteger * 1000;
        mDecimal = (int) decimal;
        try {
            mPickInteger.setCurrent( mInteger );
            mPickDecimal.setCurrent( mDecimal );
        } catch ( java.lang.Exception ex ) {
            int test = 0;
            test++;
        }
    }

    protected void onBindDialogView( android.view.View v )
    {
        super.onBindDialogView( v );
        bindData();
    }

    protected void onSetInitialValue( boolean restore, java.lang.Object defaultValue )
    {
        if (restore) {
            try {
                mValue = shouldPersist() ? getPersistedFloat( mDefault ) : 0;
            } catch ( java.lang.Exception ex ) {
                mValue = mDefault;
            }
        } else {
            mValue = (java.lang.Float) defaultValue;
        }
        super.onSetInitialValue( restore, defaultValue );
    }

    protected void onDialogClosed( boolean positiveResult )
    {
        if (positiveResult == true) {
            super.onDialogClosed( positiveResult );
            mPickInteger.onClick( null );
            mPickDecimal.onClick( null );
            java.lang.String value = mPickInteger.getCurrent() + "." + mPickDecimal.getCurrentFormatted();
            mValue = Float.valueOf( value );
            if (shouldPersist()) {
                persistFloat( mValue );
            }
        }
    }

}
