package edu.umd.cs.guitar;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class Server extends IntentService {

	public Server() {
		super(Server.class.getName());
	}

	/**
	 * @param intent description of the activity to test
	 */
	@Override
	protected void onHandleIntent(Intent argv) {
		String actName = argv.getExtras().getString("AUT");
		ComponentName instr = new ComponentName(this, AUTInstrument.class);
		Bundle arg = new Bundle();
		arg.putString("AUT", actName);
		startInstrumentation(instr, null, arg);
	}
}
