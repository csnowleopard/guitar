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

public class HelloAUTActivity extends Activity {

	final static String tag = HelloAUTActivity.class.getPackage().getName();

	boolean drawn = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final EditText colorText = (EditText)findViewById(R.id.color_text);

		final RadioGroup.OnCheckedChangeListener lShape = new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				Log.d(tag, "Rg Shape checked " + id);
				if (drawn) draw();
			}
		};

		final RadioGroup.OnCheckedChangeListener lColor = new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				Log.d(tag, "Rg Color checked: " + id);
				if (id == R.id.color_none) {
					colorText.setText("");
				}
				if (drawn) draw();
			}
		};

		final RadioGroup rg1 = (RadioGroup)findViewById(R.id.rg_shape);
		rg1.setOnCheckedChangeListener(lShape);

		final RadioGroup rg2 = (RadioGroup)findViewById(R.id.rg_color);
		rg2.setOnCheckedChangeListener(lColor);

		final Button b1 = (Button)findViewById(R.id.create);
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "Create clicked");
				draw();
				drawn = true;
			}
		});

		final Button b2 = (Button)findViewById(R.id.reset);
		b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(tag, "reset clicked");
				synchronized(this) { drawn = false; }
				FrameLayout canvas = (FrameLayout)findViewById(R.id.canvas);
				colorText.setText("");
				RadioGroup rg_shape = (RadioGroup)findViewById(R.id.rg_shape);
				rg1.setOnCheckedChangeListener(null);
				rg_shape.check(R.id.shape_circle);
				rg1.setOnCheckedChangeListener(lShape);
				RadioGroup rg_color = (RadioGroup)findViewById(R.id.rg_color);
				rg2.setOnCheckedChangeListener(null);
				rg_color.check(R.id.color_none);
				rg2.setOnCheckedChangeListener(lColor);
				canvas.removeAllViews();
			}
		});
	}

	private void draw() {
		RadioGroup rg_color = (RadioGroup)findViewById(R.id.rg_color);
		int co = rg_color.getCheckedRadioButtonId();
		String color = "";
		if (co == R.id.color_color) {
			EditText txt = (EditText)findViewById(R.id.color_text);
			color = txt.getText().toString();
		}
		RadioGroup rg_shape = (RadioGroup)findViewById(R.id.rg_shape);
		int sh = rg_shape.getCheckedRadioButtonId();
		Shape shape = new Shape(HelloAUTActivity.this, sh == R.id.shape_square, color);
		FrameLayout canvas = (FrameLayout)findViewById(R.id.canvas);
		canvas.removeAllViews();
		canvas.addView(shape);
	}

	private void colors() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.color);
		final CharSequence[] colors =
		{Shape.bk, Shape.bl, Shape.cy, Shape.gy, Shape.gr, Shape.ma, Shape.rd, Shape.wh, Shape.yl};
		builder.setSingleChoiceItems(colors, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				final RadioGroup rg2 = (RadioGroup)findViewById(R.id.rg_color);
				if (rg2.getCheckedRadioButtonId() != R.id.color_color)
					rg2.check(R.id.color_color);
				final EditText colorText = (EditText)findViewById(R.id.color_text);
				colorText.setText(colors[item]);
				if (drawn) draw();
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(this);
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(tag, "menu selected: " + item.toString());
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (item.getItemId()) {
		case R.id.m_about:
			builder.setTitle("Hello, AndroidGUITAR!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			break;
		case R.id.m_shape:
			builder.setTitle(R.string.shape);
			final CharSequence[] shapes =
			{getString(R.string.shape1), getString(R.string.shape2)};
			final int[] ids = {R.id.shape_circle, R.id.shape_square};
			builder.setSingleChoiceItems(shapes, -1, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					final RadioGroup rg1 = (RadioGroup)findViewById(R.id.rg_shape);
					rg1.check(ids[item]);
					if (drawn) draw();
					dialog.cancel();
				}
			});
			break;
		case R.id.m_color:
			colors(); return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(this);
		alert.show();
		return true;
	}
}
