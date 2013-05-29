package intents.guitar;

import intents.guitar.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class A_Activity extends Activity implements
		View.OnClickListener {
	Button buttonA1, buttonA2, buttonA3, buttonA4;
	private final String TAG = new String("LotsoIntents16");


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a);

		buttonA1 = (Button) findViewById(R.id.ButtonA1);
		buttonA2 = (Button) findViewById(R.id.ButtonA2);
		buttonA3 = (Button) findViewById(R.id.ButtonA3);
		buttonA4 = (Button) findViewById(R.id.ButtonA4);

		buttonA1.setOnClickListener(this);
		buttonA2.setOnClickListener(this);
		buttonA3.setOnClickListener(this);
		buttonA4.setOnClickListener(this);
	}

	public void onClick(View v) {
		Log.d(TAG, "ButtonID:" + v.getId());
		Intent i;
		switch (v.getId()) {
		case R.id.ButtonA1:
			i = new Intent(this, A1_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonA2:
			i = new Intent(this, A2_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonA3:
			i = new Intent(this, A3_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonA4:
			i = new Intent(this, A4_Activity.class);
			startActivity(i);
			break;
		}
	}
	
}