package intents.guitar;

import intents.guitar.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity implements View.OnClickListener {
	Button buttonA, buttonB, buttonC, buttonD, buttonWhit, buttonLy;
	private final String TAG = new String("LotsoIntents16");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonA = (Button) findViewById(R.id.ButtonA);
		buttonB = (Button) findViewById(R.id.ButtonB);
		buttonC = (Button) findViewById(R.id.ButtonC);
		buttonD = (Button) findViewById(R.id.ButtonD);
		buttonWhit = (Button) findViewById(R.id.Whitney);
		buttonLy = (Button) findViewById(R.id.Lyonel);

		buttonA.setOnClickListener(this);
		buttonB.setOnClickListener(this);
		buttonC.setOnClickListener(this);
		buttonD.setOnClickListener(this);
		buttonWhit.setOnClickListener(this);
		buttonLy.setOnClickListener(this);
	}

	public void onClick(View v) {
		Log.d(TAG, "ButtonID:" + v.getId());
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
		case R.id.Whitney:
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			CharSequence text = "MUTE YOUR MIC LYONEL";
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			break;
		case R.id.Lyonel:
			context = getApplicationContext();
			duration = Toast.LENGTH_SHORT;
			text = "everything I say ends with a question mark?";
			toast = Toast.makeText(context, text, duration);
			toast.show();
			break;
		}
	}
}