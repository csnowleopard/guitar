package rohan.makes.intents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class B_Activity extends Activity implements
		View.OnClickListener {
	Button buttonB1, buttonB2, buttonB3, buttonB4;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b);

		buttonB1 = (Button) findViewById(R.id.ButtonB1);
		buttonB2 = (Button) findViewById(R.id.ButtonB2);
		buttonB3 = (Button) findViewById(R.id.ButtonB3);
		buttonB4 = (Button) findViewById(R.id.ButtonB4);

		buttonB1.setOnClickListener(this);
		buttonB2.setOnClickListener(this);
		buttonB3.setOnClickListener(this);
		buttonB4.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.ButtonB1:
			i = new Intent(this, B1_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonB2:
			i = new Intent(this, B2_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonB3:
			i = new Intent(this, B3_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonB4:
			i = new Intent(this, B4_Activity.class);
			startActivity(i);
			break;
		}
	}
}