/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer.experiment;

import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netbeans.jemmy.EventTool;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.JFCEventHandler;
import edu.umd.cs.guitar.exception.ApplicationConnectException;
import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.JFCApplication;
import edu.umd.cs.guitar.model.JFCConstants;
import edu.umd.cs.guitar.model.JFCXWindow;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.PropertyTypeWrapper;
import edu.umd.cs.guitar.replayer.JFCReplayerConfiguration;
import edu.umd.cs.guitar.replayer.experiment.GReplayerMonitorExp;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class JFCReplayerMonitorExp implements GReplayerMonitorExp {

	/**
	 * Delay for widget searching loop
	 */
	private static final int DELAY_STEP = 50;

	boolean isUseReg = false;

	public boolean isUseReg() {
		return isUseReg;
	}

	public void setUseReg(boolean isUseReg) {
		this.isUseReg = isUseReg;
	}

	GApplication application;
	String sMainClass;

	int intialWait = 2000;

	// Replayer configuration
	String[] URLs = new String[0];
	String[] args = new String[0];

	public JFCReplayerMonitorExp(String sMainClass, int intialWait) {
		super();
		this.sMainClass = sMainClass;
		this.intialWait = intialWait;
	}

	@Override
	public void connectToApplication() throws IOException {
		try {

			GUITARLog.log.debug("Loading URL....");

			application = new JFCApplication(sMainClass, false, URLs);

			GUITARLog.log.debug("DONE");

			String[] args;

			if (JFCReplayerConfigurationExp.ARGUMENT_LIST != null)
				args = JFCReplayerConfigurationExp.ARGUMENT_LIST
						.split(GUITARConstants.CMD_ARGUMENT_SEPARATOR);
			else
				args = new String[0];

			application.connect(args);

			GUITARLog.log.info("Initial waiting for " + intialWait + "ms");

			try {
				Thread.sleep(intialWait);
			} catch (InterruptedException e) {
				System.err.println(e);
				throw new ApplicationConnectException();
			}

		} catch (MalformedURLException e) {
			System.err.println(e);
			throw new ApplicationConnectException();
		} catch (ClassNotFoundException e) {
			System.err.println(e);
			throw new ApplicationConnectException();
		}

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUp() {
		GUITARLog.log.info("Setting up JFCReplayer...");
		// -------------------------------------
		// Add handler for all uncaught exceptions
		Thread
				.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						GUITARLog.log.error("Uncaught exception", e);
					}
				});

		// -------------------------------------
		// Disable System.exit() call by changing SecurityManager

		oldSecurityManager = System.getSecurityManager();
		final SecurityManager securityManager = new SecurityManager() {
			// public void checkExit(int status) {
			// //throw new ApplicationTerminatedException(status);
			// }

			@Override
			public void checkPermission(Permission permission, Object context) {
				if ("exitVM".equals(permission.getName())) {
					throw new ExitTrappedException();
				}
			}

			@Override
			public void checkPermission(Permission permission) {
				if ("exitVM".equals(permission.getName())) {
					throw new ExitTrappedException();
				}
			}
		};
		System.setSecurityManager(securityManager);

		// Registering default supported events
		EventManager em = EventManager.getInstance();

		for (Class<? extends JFCEventHandler> event : JFCConstants.DEFAULT_SUPPORTED_EVENTS) {
			try {
				em.registerEvent(event.newInstance());
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Class used to disable System.exit()
	 * 
	 * @author Bao Nguyen
	 * 
	 */
	private static class ExitTrappedException extends SecurityException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}

	SecurityManager oldSecurityManager;

	@Override
	public GComponent getComponentBySign(WidgetMapElementType widgetMapElement) {

		ComponentType window = widgetMapElement.getWindow();
		GWindow gWindow = getWindow(window);

		// -------------------
		// Find window
		ComponentTypeWrapper wWindow = new ComponentTypeWrapper(window);
		GUITARLog.log.info("Looking for window: *"
				+ wWindow.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME)
				+ "*....");

		if (gWindow == null) {
			GUITARLog.log.error("Window not found");
			return null;
		}

		GUITARLog.log.info("FOUND");
		GUITARLog.log.info("");

		// -------------------
		// Find component
		ComponentType component = widgetMapElement.getComponent();
		ComponentTypeWrapper wWidget = new ComponentTypeWrapper(component);

		GUITARLog.log.info("Looking for widget: *"
				+ wWidget.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME)
				+ "*....");
		List<PropertyType> ID = selectIDProperties(component);

		GComponent gComponent = getComponentBySign(gWindow, ID);

		if (gComponent == null) {
			return null;
		}
		GUITARLog.log.info("FOUND");
		GUITARLog.log.info("");

		return gComponent;
	}

	/**
	 * @param gWindow
	 * @param sign
	 * @return
	 */
	private GComponent getComponentBySign(GWindow gWindow,
			List<PropertyType> sign) {
		GComponent containter = gWindow.getContainer();

		List<PropertyTypeWrapper> IDAdapter = new ArrayList<PropertyTypeWrapper>();

		for (PropertyType p : sign)
			IDAdapter.add(new PropertyTypeWrapper(p));

		GComponent gComponent = containter.getFirstChild(IDAdapter);

		return gComponent;
	}

	@Override
	public GWindow getWindow(ComponentType gWindow) {

		ComponentTypeWrapper wWindow = new ComponentTypeWrapper(gWindow);
		String sWindowTitle = wWindow
				.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);

		GUITARLog.log.info("Finding window: " + sWindowTitle);

		GWindow retGXWindow = null;

		while (retGXWindow == null) {

			Frame[] windows = Frame.getFrames();

			if (windows == null)
				continue;

			for (Frame aWindow : windows) {
				Window window = getOwnedWindowByID(aWindow, sWindowTitle);
				if (window != null) {
					retGXWindow = new JFCXWindow(window);
					break;
				}
			}

			new EventTool().waitNoEvent(DELAY_STEP);
		}
		return retGXWindow;
	}

	/**
	 * 
	 * Recursively search a window
	 * 
	 * @param parent
	 * @param sWindowID
	 * @return Window
	 */
	private Window getOwnedWindowByID(Window parent, String sWindowID) {

		if (parent == null)
			return null;

		GWindow gWindow = new JFCXWindow(parent);

		String title = gWindow.getTitle();
		if (title == null)
			return null;

		// Debug
		System.out.println("Title: *" + title + "*");
		System.out.println("sWindowID: *" + sWindowID + "*");

		// if (sWindowID.equals(title))
		// return parent;

		if (isUseReg) {
			if (isRegMatched(title, sWindowID)) {
				return parent;
			}
		} else {
			if (sWindowID.equals(title)) {
				return parent;
			}
		}

		Window retWin = null;
		Window[] wOwnedWins = parent.getOwnedWindows();
		for (Window aOwnedWin : wOwnedWins) {
			retWin = getOwnedWindowByID(aOwnedWin, sWindowID);
			if (retWin != null)
				return retWin;
		}

		return retWin;

	}

	public List<PropertyType> selectIDProperties(ComponentType comp) {
		if (comp == null)
			return new ArrayList<PropertyType>();

		List<PropertyType> retIDProperties = new ArrayList<PropertyType>();

		AttributesType attributes = comp.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();
		for (PropertyType p : lProperties) {
			if (JFCConstants.ID_PROPERTIES.contains(p.getName()))
				retIDProperties.add(p);
		}
		return retIDProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getAction(java.lang.String)
	 */
	@Override
	public GEvent getAction(String actionName) {
		GEvent retAction = null;
		try {
			Class<?> c = Class.forName(actionName);
			Object action = c.newInstance();

			retAction = (GEvent) action;

		} catch (Exception e) {
			GUITARLog.log.error("Error in getting action", e);
		}

		return retAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.smut.replayer.SReplayerMonitor#getApplication()
	 */
	@Override
	public GApplication getApplication() {
		return this.application;
	}

	/**
	 * Check if a string is match by a regular expression temporarily used for
	 * matching window titles. Should move to some more general modules for
	 * future use.
	 * 
	 * <p>
	 * 
	 * @param input
	 * @param regExp
	 * @return
	 */
	private boolean isRegMatched(String input, String regExp) {

		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile(regExp);
		matcher = pattern.matcher(input);
		if (matcher.matches())
			return true;

		return false;
	}
}
