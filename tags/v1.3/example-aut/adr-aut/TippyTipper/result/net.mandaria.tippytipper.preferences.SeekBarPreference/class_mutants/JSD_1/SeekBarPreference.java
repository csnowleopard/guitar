// This is mutant program.
// Author : ysma

package net.mandaria.tippytipper.preferences;


import net.mandaria.tippytipper.*;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.res.*;


public class SeekBarPreference extends android.preference.DialogPreference implements SeekBar.OnSeekBarChangeListener
{

    private static final java.lang.String androidns = "http://schemas.android.com/apk/res/android";

    private final java.lang.String appns = "http://schemas.android.com/apk/res/net.mandaria.tippytipper";

    private android.widget.SeekBar mSeekBar;

    private android.widget.TextView mSplashText;

    private android.widget.TextView mValueText;

    private android.content.Context mContext;

    private java.lang.String mDialogMessage;

    private java.lang.String mSuffix;

    private int mDefault;

    private int mMax;

    private int mMin;

    private int mValue = 0;

    public SeekBarPreference( android.content.Context context, android.util.AttributeSet attrs )
    {
        super( context, attrs );
        mContext = context;
        mDialogMessage = attrs.getAttributeValue( androidns, "dialogMessage" );
        mSuffix = attrs.getAttributeValue( androidns, "text" );
        mDefault = attrs.getAttributeIntValue( androidns, "defaultValue", 0 );
        mMax = attrs.getAttributeIntValue( androidns, "max", 100 );
        android.content.res.TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.SeekBarPreference );
        mMin = a.getInt( R.styleable.SeekBarPreference_min, 0 );
        mMax = mMax - mMin;
    }

    protected android.view.View onCreateDialogView()
    {
        android.widget.LinearLayout.LayoutParams params;
        android.widget.LinearLayout layout = new android.widget.LinearLayout( mContext );
        layout.setOrientation( LinearLayout.VERTICAL );
        layout.setPadding( 6, 6, 6, 6 );
        mSplashText = new android.widget.TextView( mContext );
        if (mDialogMessage != null) {
            mSplashText.setText( mDialogMessage );
        }
        layout.addView( mSplashText );
        mValueText = new android.widget.TextView( mContext );
        mValueText.setGravity( Gravity.CENTER_HORIZONTAL );
        mValueText.setTextSize( 32 );
        params = new android.widget.LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
        layout.addView( mValueText, params );
        mSeekBar = new android.widget.SeekBar( mContext );
        mSeekBar.setOnSeekBarChangeListener( this );
        layout.addView( mSeekBar, new android.widget.LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ) );
        if (shouldPersist()) {
            mValue = getPersistedInt( mDefault );
        }
        mSeekBar.setMax( mMax );
        mSeekBar.setProgress( mValue );
        return layout;
    }

    protected void onBindDialogView( android.view.View v )
    {
        super.onBindDialogView( v );
        mSeekBar.setMax( mMax );
        mSeekBar.setProgress( mValue - mMin );
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

    public void onProgressChanged( android.widget.SeekBar seek, int value, boolean fromTouch )
    {
        java.lang.String t = String.valueOf( value + mMin );
        mValueText.setText( mSuffix == null ? t : t.concat( mSuffix ) );
        if (shouldPersist()) {
            persistInt( value + mMin );
        }
        callChangeListener( new java.lang.Integer( value + mMin ) );
    }

    public void onStartTrackingTouch( android.widget.SeekBar seek )
    {
    }

    public void onStopTrackingTouch( android.widget.SeekBar seek )
    {
    }

    public void setMax( int max )
    {
        mMax = max;
    }

    public int getMax()
    {
        return mMax;
    }

    public void setMin( int min )
    {
        mMin = min;
    }

    public int getMin()
    {
        return mMin;
    }

    public void setProgress( int progress )
    {
        mValue = progress;
        if (mSeekBar != null) {
            mSeekBar.setProgress( progress );
        }
    }

    public int getProgress()
    {
        return mValue;
    }

}
