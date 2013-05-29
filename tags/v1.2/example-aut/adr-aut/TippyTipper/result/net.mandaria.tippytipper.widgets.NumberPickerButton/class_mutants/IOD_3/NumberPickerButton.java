// This is mutant program.
// Author : ysma

package net.mandaria.tippytipper.widgets;


import net.mandaria.tippytipper.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageButton;


public class NumberPickerButton extends android.widget.ImageButton
{

    private net.mandaria.tippytipper.widgets.NumberPicker mNumberPicker;

    public NumberPickerButton( android.content.Context context, android.util.AttributeSet attrs, int defStyle )
    {
        super( context, attrs, defStyle );
    }

    public NumberPickerButton( android.content.Context context, android.util.AttributeSet attrs )
    {
        super( context, attrs );
    }

    public NumberPickerButton( android.content.Context context )
    {
        super( context );
    }

    public void setNumberPicker( net.mandaria.tippytipper.widgets.NumberPicker picker )
    {
        mNumberPicker = picker;
    }

    public boolean onTouchEvent( android.view.MotionEvent event )
    {
        cancelLongpressIfRequired( event );
        return super.onTouchEvent( event );
    }

    public boolean onTrackballEvent( android.view.MotionEvent event )
    {
        cancelLongpressIfRequired( event );
        return super.onTrackballEvent( event );
    }

    // public boolean onKeyUp( int keyCode, android.view.KeyEvent event ){ ... }

    private void cancelLongpressIfRequired( android.view.MotionEvent event )
    {
        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            cancelLongpress();
        }
    }

    private void cancelLongpress()
    {
        if (R.id.increment == getId()) {
            mNumberPicker.cancelIncrement();
        } else {
            if (R.id.decrement == getId()) {
                mNumberPicker.cancelDecrement();
            }
        }
    }

}
