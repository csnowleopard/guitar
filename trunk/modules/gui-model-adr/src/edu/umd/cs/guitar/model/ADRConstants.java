package edu.umd.cs.guitar.model;

import java.util.Arrays;
import java.util.List;

import edu.umd.cs.guitar.event.ADRActionHandler;
import edu.umd.cs.guitar.event.ADREditableTextHandler;
import edu.umd.cs.guitar.event.ADREventHandler;
import edu.umd.cs.guitar.event.ADRSelectFromParent;
import edu.umd.cs.guitar.event.ADRSelectionHandler;

public class ADRConstants {
	public static final String TITLE_TAG = "Title";
	public static final String ICON_TAG = "Icon";
	public static final String INDEX_TAG = "Index";
	public static final String ID_TAG = "Id";
	
	@SuppressWarnings("unchecked")
	public static List<Class<? extends ADREventHandler>> DEFAULT_SUPPORTED_EVENTS = Arrays
	.asList(ADRActionHandler.class, ADREditableTextHandler.class,
			ADRSelectFromParent.class, ADRSelectionHandler.class);
	
	/**
	 * List of properties used to identify a widget on the GUI
	 */
	public static List<String> ID_PROPERTIES = Arrays.asList("Class", "Title");
}
