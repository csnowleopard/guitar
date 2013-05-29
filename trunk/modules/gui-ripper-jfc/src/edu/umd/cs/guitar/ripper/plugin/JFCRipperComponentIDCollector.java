package edu.umd.cs.guitar.ripper.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.JFCConstants;
import edu.umd.cs.guitar.model.JFCDefaultIDGenerator;
import edu.umd.cs.guitar.model.JFCDefaultIDGeneratorSimple;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;
import edu.umd.cs.guitar.util.AppUtil;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Experimental Ripper plugin to collect component IDs being ripped.
 * <p>
 * This plugin is ONLY USED for random-walk ripping experiments.
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>.
 */
public class JFCRipperComponentIDCollector implements
		GRipperBeforeExpandingComponnent, GRipperAfter {
	public static final String RIPPER_COMP_ID_LOG_FILE_FLAG = "guitar.ripper.compIdLogFile";
	private static final String DEFAULT_COMPONENT_ID_LOG_FILE = RIPPER_COMP_ID_LOG_FILE_FLAG;
	BufferedWriter bw;
	String idFileName;

	public JFCRipperComponentIDCollector() {
		idFileName = System.getProperty(DEFAULT_COMPONENT_ID_LOG_FILE);
		if (idFileName == null)
			idFileName = "component_id.log";

		File file = new File(idFileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw;
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void beforeExpandingComponent(GComponent component, GWindow window) {

		ComponentType componentData = component.extractProperties();
		GUIType windowData = window.extractWindow();

		long windowHashcode = getWindowHashCode(windowData);
		String componentID = getComponentID(componentData, windowHashcode);
		try {
			GUITARLog.log.info(componentID + "\n");
			bw.write(componentID + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is copied from {@link JFCDefaultIDGenerator}
	 * <p>
	 * 
	 * @param gui
	 *            GUIType shows hashcode is to be generated
	 * @return Hashcode for the input GUIType
	 */
	private long getWindowHashCode(GUIType gui) {
		GUITypeWrapper wGUI = new GUITypeWrapper(gui);
		String title = wGUI.getTitle();
		AppUtil appUtil = new AppUtil();

		String fuzzyTitle = appUtil.findRegexForString(title);

		long hashcode = fuzzyTitle.hashCode();

		hashcode = (hashcode * 2) & 0xffffffffL;

		return hashcode;
	}

	private String getComponentID(ComponentType component, long windoHashCode) {
		AttributesType attributes = component.getAttributes();
		final int prime = 31;
		long hashcode = 1;

		if (attributes != null) {

			long localHashCode = getLocalHashcode(component);
			hashcode = windoHashCode * prime + localHashCode;
			hashcode = (hashcode * 2) & 0xffffffffL;

		} else {
			hashcode = windoHashCode;
		}

		String sID = GUITARConstants.COMPONENT_ID_PREFIX + hashcode;
		return sID;
	}

	static List<String> ID_PROPERTIES = new ArrayList<String>(
			JFCConstants.ID_PROPERTIES);

	/**
	 * Those classes are invisible widgets but cause false-positive when
	 * calculating ID
	 */
	static List<String> IGNORED_CLASSES = Arrays.asList("javax.swing.JPanel",
			"javax.swing.JTabbedPane", "javax.swing.JScrollPane",
			"javax.swing.JSplitPane", "javax.swing.Box",
			"javax.swing.JViewport", "javax.swing.JScrollBar",
			"javax.swing.JLayeredPane",
			"javax.swing.JList$AccessibleJList$AccessibleJListChild",
			"javax.swing.JList$AccessibleJList", "javax.swing.JList",
			"javax.swing.JScrollPane$ScrollBar",
			"javax.swing.plaf.metal.MetalScrollButton");

	/**
	 * Those classes are invisible widgets but cause false-positive when
	 * calculating ID
	 */
	static List<String> IS_ADD_INDEX_CLASSES = Arrays
			.asList("javax.swing.JTabbedPane$Page");

	/**
	 * @param component
	 * @return
	 */
	private long getLocalHashcode(ComponentType component) {
		final int prime = 31;

		long hashcode = 1;

		AttributesType attributes = component.getAttributes();
		if (attributes == null) {
			return hashcode;
		}

		// Specially handle titles
		AttributesTypeWrapper wAttribute = new AttributesTypeWrapper(attributes);
		String sClass = wAttribute
				.getFirstValByName(GUITARConstants.CLASS_TAG_NAME);

		if (IGNORED_CLASSES.contains(sClass)) {
			hashcode = (prime * hashcode + (sClass == null ? 0 : (sClass
					.hashCode())));
			return hashcode;
		}

		// Normal cases
		// Using ID_Properties for hash code

		List<PropertyType> lProperty = attributes.getProperty();

		if (lProperty == null) {
			return hashcode;
		}

		for (PropertyType property : lProperty) {

			String name = property.getName();
			if (ID_PROPERTIES.contains(name)) {

				hashcode = (prime * hashcode + (name == null ? 0 : name
						.hashCode()));

				List<String> valueList = property.getValue();
				if (valueList != null) {
					for (String value : valueList) {
						hashcode = (prime * hashcode + (value == null ? 0
								: (value.hashCode())));

					}
				}
			}
		}

		hashcode = (hashcode * 2) & 0xffffffffL;

		return hashcode;

	}

	@Override
	public void afterRipping() {
		try {
			GUITARLog.log.info("Component IDs were saved to:" + idFileName);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
