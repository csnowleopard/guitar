// This is mutant program.
// Author : ysma

package com.aut;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


public class Shape extends android.view.View
{

    final boolean isRect;

    final java.lang.String color;

    public Shape( android.content.Context context, boolean isRect, java.lang.String color )
    {
        super( context );
        this.isRect = isRect;
        this.color = color.toLowerCase();
    }

    final android.graphics.Paint paint = new android.graphics.Paint( Paint.ANTI_ALIAS_FLAG );

    static final java.lang.String bk = "black";

    static final java.lang.String bl = "blue";

    static final java.lang.String cy = "cyan";

    static final java.lang.String gy = "gray";

    static final java.lang.String gr = "green";

    static final java.lang.String ma = "magenta";

    static final java.lang.String rd = "red";

    static final java.lang.String wh = "white";

    static final java.lang.String yl = "yellow";

    private void setColor()
    {
        if (color.equals( "" ) || color.equals( bk )) {
            paint.setColor( Color.BLACK );
        } else {
            if (color.equals( bl )) {
                paint.setColor( Color.BLUE );
            } else {
                if (color.equals( cy )) {
                    paint.setColor( Color.CYAN );
                } else {
                    if (color.equals( gy )) {
                        paint.setFlags( Color.GRAY );
                    } else {
                        if (color.equals( gr )) {
                            paint.setColor( Color.GREEN );
                        } else {
                            if (color.equals( ma )) {
                                paint.setColor( Color.MAGENTA );
                            } else {
                                if (color.equals( rd )) {
                                    paint.setColor( Color.RED );
                                } else {
                                    if (color.equals( wh )) {
                                        paint.setColor( Color.WHITE );
                                    } else {
                                        if (color.equals( yl )) {
                                            paint.setColor( Color.YELLOW );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static final float sz = 100;

    public void onDraw( android.graphics.Canvas canvas )
    {
        super.onDraw( canvas );
        setColor();
        if (isRect) {
            canvas.drawRect( (float) 0.5 * sz, (float) 0.5 * sz, (float) 1.5 * sz, (float) 1.5 * sz, paint );
        } else {
            canvas.drawCircle( sz, sz, (float) 0.71 * sz, paint );
        }
    }

}
