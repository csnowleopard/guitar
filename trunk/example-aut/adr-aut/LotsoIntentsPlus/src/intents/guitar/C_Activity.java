package intents.guitar;

import intents.guitar.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class C_Activity extends Activity implements
		View.OnClickListener {
	Button buttonC1, buttonC2, buttonC3, buttonC4;
	private final String TAG = new String("LotsoIntents16");


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c);

		buttonC1 = (Button) findViewById(R.id.ButtonC1);
		buttonC2 = (Button) findViewById(R.id.ButtonC2);
		buttonC3 = (Button) findViewById(R.id.ButtonC3);
		buttonC4 = (Button) findViewById(R.id.ButtonC4);

		buttonC1.setOnClickListener(this);
		buttonC2.setOnClickListener(this);
		buttonC3.setOnClickListener(this);
		buttonC4.setOnClickListener(this);
	}

	public void onClick(View v) {
		Log.d(TAG, "ButtonID:" + v.getId());
		Intent i;
		switch (v.getId()) {
		case R.id.ButtonC1:
			i = new Intent(this, C1_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonC2:
			i = new Intent(this, C2_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonC3:
			i = new Intent(this, C3_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonC4:
			i = new Intent(this, C4_Activity.class);
			startActivity(i);
			break;
		}
	}
}