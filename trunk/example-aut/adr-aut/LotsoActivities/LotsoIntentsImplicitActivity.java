package rohan.makes.intents;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class LotsoIntentsImplicitActivity extends Activity implements
		View.OnClickListener {
	Button button1, button2, button3, button4, button5, button6, button7;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		button1 = (Button) findViewById(R.id.button01);
		button2 = (Button) findViewById(R.id.button02);
		button3 = (Button) findViewById(R.id.button03);
		button4 = (Button) findViewById(R.id.button04);
		button5 = (Button) findViewById(R.id.button05);
		button6 = (Button) findViewById(R.id.button06);
		button7 = (Button) findViewById(R.id.button07);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		button5.setOnClickListener(this);
		button6.setOnClickListener(this);
		button7.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.button01:
			i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.vogella.de"));
			startActivity(i);
			break;
		case R.id.button02:
			i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:(+49)12345789"));
			startActivity(i);
			break;
		case R.id.button03:
			i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:(+49)12345789"));
			startActivity(i);
			break;
		case R.id.button04:
			i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("geo:50.123,7.1434?z=19"));
			startActivity(i);
			break;
		case R.id.button05:
			i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=query"));
			startActivity(i);
			break;
		case R.id.button06:
			i = new Intent("android.media.action.IMAGE_CAPTURE");
			startActivity(i);
			break;
		case R.id.button07:
			i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("content://contacts/people/"));
			startActivity(i);
			break;
		}

	}
}