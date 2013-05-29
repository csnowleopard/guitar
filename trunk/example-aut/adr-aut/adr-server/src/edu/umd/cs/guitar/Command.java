package edu.umd.cs.guitar;

public enum Command {
	getRootWindows, getContainer, getChildren,
	getViews, getAllViews, getViewAtPoint,
	goBackTo, // followed by the name of target activity
	click, clickLong, // followed by the name of target component
	edit, // followed by the idx of target EditText and any input string
	clear, // followed by the idx of target EditText
	back, down, up,
	finish
}
