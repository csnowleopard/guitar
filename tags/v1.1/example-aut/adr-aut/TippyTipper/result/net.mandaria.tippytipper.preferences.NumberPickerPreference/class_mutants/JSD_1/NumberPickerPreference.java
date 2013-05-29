// This is mutant program.
// Author : ysma

package net.mandaria.tippytipper.preferences;


import net.mandaria.tippytipper.R;
import net.mandaria.tippytipper.widgets.*;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.res.*;


public class NumberPickerPreference extends android.preference.DialogPreference
{

    private static final java.lang.String androidns = "http://schemas.android.com/apk/res/android";

    private final java.lang.String appns = "http://schemas.android.com/apk/res/net.mandaria.tippytipper";

    private net.mandaria.tippytipper.widgets.NumberPicker mPickInteger;

    private android.widget.TextView mSplashText;

    private android.widget.TextView mValueText;

    private android.content.Context mContext;

    private java.lang.String mDialogMessage;

    private java.lang.String mSuffix;

    private int mDefault;

    private int mMin;

    private int mMax;

    private int mValue = 0;

    public NumberPickerPreference( android.content.Context context, android.util.AttributeSet attrs )
    {
        super( context, attrs );
        mContext = context;
        mDialogMessage = attrs.getAttributeValue( androidns, "dialogMessage" );
        mSuffix = attrs.getAttributeValue( androidns, "text" );
        mDefault = attrs.getAttributeIntValue( androidns, "defaultValue", 0 );
        mMax = attrs.getAttributeIntValue( androidns, "max", 100 );
        android.content.res.TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.SeekBarPreference );
        mMin = a.getInt( R.styleable.SeekBarPreference_min, 0 );
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
        mPickInteger.setRange( mMin, mMax );
        android.widget.TextView suffix = new android.widget.TextView( mContext );
        suffix.setText( mSuffix );
        suffix.setTextSize( 32 );
        android.widget.TableRow row_one = new android.widget.TableRow( mContext );
        row_one.setGravity( Gravity.CENTER );
        row_one.addView( mPickInteger );
        row_one.addView( suffix );
        layout.addView( row_header );
        android.widget.TableLayout table_main = new android.widget.TableLayout( mContext );
        table_main.addView( row_one );
        android.widget.TableRow row_main = new android.widget.TableRow( mContext );
        row_main.setGravity( Gravity.CENTER_HORIZONTAL );
        row_main.addView( table_main );
        layout.addView( row_main );
        if (shouldPersist()) {
            mValue = getPersistedInt( mDefault );
        }
        bindData();
        return layout;
    }

    private void bindData()
    {
        try {
            mPickInteger.setCurrent( mValue );
        } catch ( java.lang.Exception ex ) {
        }
    }

    protected void onBindDialogView( android.view.View v )
    {
        super.onBindDialogView( v );
        bindData();
    }

    protected void onSetInitialValue( boolean restore, java.lang.Object defaultValue )
    {
        super.onSetInitialValue( restore, defaultValue );
        if (restore) {
            try {
                mValue = shouldPersist() ? getPersistedInt( mDefault ) : 0;
            } catch ( java.lang.Exception ex ) {
                mValue = mDefault;
            }
        } else {
            mValue = (java.lang.Integer) defaultValue;
        }
    }

    protected void onDialogClosed( boolean positiveResult )
    {
        if (positiveResult == true) {
            super.onDialogClosed( positiveResult );
            mPickInteger.onClick( null );
            mValue = mPickInteger.getCurrent();
            if (shouldPersist()) {
                persistInt( mValue );
            }
        }
    }

}
