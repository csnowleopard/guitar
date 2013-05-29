package edu.umd.cs.guitar.proxy;

import android.app.Activity;

public class ADRActivity {
	public String type;
	public int id;
	public String title;

	public ADRActivity(Activity act) {
		this.type = act.getClass().getName();
		this.id = extractID(act);
		this.title = this.type;
	}
	
	public static int extractID(Activity act) {
		String str = act.toString();
		int at = str.indexOf('@');
		return Integer.parseInt(str.substring(at+1), 16);
	}
	
	public String toString() {
		String ret = type + "@" + Integer.toHexString(id);
		return ret;
	}
}
