package rohan.makes.intents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LotsoIntents16ActivitiesActivity extends Activity implements
		View.OnClickListener {
	Button buttonA, buttonB, buttonC, buttonD;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonA = (Button) findViewById(R.id.ButtonA);
		buttonB = (Button) findViewById(R.id.ButtonB);
		buttonC = (Button) findViewById(R.id.ButtonC);
		buttonD = (Button) findViewById(R.id.ButtonD);

		buttonA.setOnClickListener(this);
		buttonB.setOnClickListener(this);
		buttonC.setOnClickListener(this);
		buttonD.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.ButtonA:
			i = new Intent(this, A_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonB:
			i = new Intent(this, B_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonC:
			i = new Intent(this, C_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonD:
			i = new Intent(this, D_Activity.class);
			startActivity(i);
			break;
		}
	}
}