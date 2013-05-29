package intents.guitar;

import intents.guitar.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class D_Activity extends Activity implements
		View.OnClickListener {
	Button buttonD1, buttonD2, buttonD3, buttonD4;
	private final String TAG = new String("LotsoIntents16");


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.d);

		buttonD1 = (Button) findViewById(R.id.ButtonD1);
		buttonD2 = (Button) findViewById(R.id.ButtonD2);
		buttonD3 = (Button) findViewById(R.id.ButtonD3);
		buttonD4 = (Button) findViewById(R.id.ButtonD4);

		buttonD1.setOnClickListener(this);
		buttonD2.setOnClickListener(this);
		buttonD3.setOnClickListener(this);
		buttonD4.setOnClickListener(this);
	}

	public void onClick(View v) {
		Log.d(TAG, "ButtonID:" + v.getId());
		Intent i;
		switch (v.getId()) {
		case R.id.ButtonD1:
			i = new Intent(this, D1_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonD2:
			i = new Intent(this, D2_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonD3:
			i = new Intent(this, D3_Activity.class);
			startActivity(i);
			break;
		case R.id.ButtonD4:
			i = new Intent(this, D4_Activity.class);
			startActivity(i);
			break;
		}
	}
}