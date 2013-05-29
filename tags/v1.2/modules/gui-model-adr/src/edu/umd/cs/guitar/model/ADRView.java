package edu.umd.cs.guitar.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

public class ADRView {
	public String type;
	public int id;
	public boolean isClickable;
	public boolean isLongClickable;
	public String text;
	public int x;
	public int y;
	public int childCount = 0;
	public boolean isExpandable;

	public ADRView(View v) {
		this.type = v.getClass().getName();
		this.id = extractID(v);
		this.isClickable = v.isClickable();
		this.isLongClickable = v.isLongClickable();
		if (v instanceof TextView) {
			this.text = ((TextView)v).getText().toString();
		} else if (v instanceof Button) {
			this.text = ((Button)v).getText().toString();
		} else {
			this.text = this.type;
		}
		final int[] xy = new int[2];
		v.getLocationInWindow(xy);
		this.x = xy[0];
		this.y = xy[1];
		if (v instanceof ViewGroup) {
			this.childCount = ((ViewGroup)v).getChildCount();
		}
		this.isExpandable = this.isClickable;
		if (v instanceof ListView || v instanceof ExpandableListView) {
			// due to trackball issue, it's not expandable even though it's clickable
			this.isExpandable = false;
		} else if (v instanceof TextView &&
				v.getParent() != null && ((View)v.getParent()).isClickable()) {
			this.isExpandable = true;
		}
	}
	
	public static int extractID(View v) {
		String str = v.toString();
		int at = str.indexOf('@');
		return Integer.parseInt(str.substring(at+1), 16);
	}

	public String toString() {
		String ret = type + "@" + Integer.toHexString(id);
		return ret;
	}
}
