package com.aut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Shape extends View {

	final boolean isRect;
	final String color;

	public Shape(Context context, boolean isRect, String color) {
		super(context);
		this.isRect = isRect;
		this.color = color.toLowerCase();
	}

	final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	final static String bk = "black";
	final static String bl = "blue";
	final static String cy = "cyan";
	final static String gy = "gray";
	final static String gr = "green";
	final static String ma = "magenta";
	final static String rd = "red";
	final static String wh = "white";
	final static String yl = "yellow";

	private void setColor() {
		if (color.equals("") || color.equals(bk)) {
			paint.setColor(Color.BLACK);
		} else if (color.equals(bl)) {
			paint.setColor(Color.BLUE);
		} else if (color.equals(cy)) {
			paint.setColor(Color.CYAN);
		} else if (color.equals(gy)) {
			paint.setColor(Color.GRAY);
		} else if (color.equals(gr)) {
			paint.setColor(Color.GREEN);
		} else if (color.equals(ma)) {
			paint.setColor(Color.MAGENTA);
		} else if (color.equals(rd)) {
			paint.setColor(Color.RED);
		} else if (color.equals(wh)) {
			paint.setColor(Color.WHITE);
		} else if (color.equals(yl)) {
			paint.setColor(Color.YELLOW);
		}
	}

	final static float sz = 100;

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setColor();
		if (isRect) { // Rect
			canvas.drawRect((float)0.5*sz, (float)0.5*sz, (float)1.5*sz, (float)1.5*sz, paint);
		} else { // Circle
			canvas.drawCircle(sz, sz, (float)0.71*sz, paint);
		}
	}

}
