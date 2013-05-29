/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import edu.umd.cs.guitar.model.GWindow;

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.XTopWindow;
import com.sun.star.awt.XTopWindowListener;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.awb.SimpleOffice;
import edu.umd.cs.guitar.event.GThreadEvent;
import edu.umd.cs.guitar.event.OOExploreElement;
import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.OOXComponent;
import edu.umd.cs.guitar.model.OOXWindow;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.ripper.Debugger;
//import edu.umd.cs.guitar.ripper.UNORipperMonitor.OOWindowListener;
import edu.umd.cs.guitar.util.OOConstants;
import edu.umd.cs.guitar.replayer.GReplayerMonitor;

import edu.umd.cs.guitar.event.OOEventHandler;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GComponent;
import com.sun.star.util.XCloseable;
import com.sun.star.awt.XWindow;
import com.sun.star.util.CloseVetoException;
import edu.umd.cs.guitar.event.OOActionHandler;
import com.sun.star.accessibility.AccessibleRole;

//import java.util.Arrays;
import java.util.LinkedList;

//import com.sun.star.accessibility.AccessibleStateType;
//import com.sun.star.accessibility.XAccessibleAction;
//import com.sun.star.accessibility.XAccessibleStateSet;
import com.sun.star.awt.XExtendedToolkit;
import com.sun.star.lang.EventObject;
//import com.sun.star.lang.IndexOutOfBoundsException;

/**
 * @author Bao Nguyen
 * @author Wikum Dinalankara
 * 
 */
public class OOReplayerMonitor extends GReplayerMonitor {

	/**
	 * Time out for the replayer
	 */
	protected int nTimeOut = OOConstants.OO_REPLAYER_TIMEOUT;
	
	/**
	 * Delay for widget searching loop 
	 */
	private static final int DELAY_STEP = 50;
	
	int nPort = 5678;
	SimpleOffice office;

	XExtendedToolkit xExtendedToolkit;
	OOWindowListener windowListener;
	volatile LinkedList<XTopWindow> tempWinStack = new LinkedList<XTopWindow>();
	boolean discardSwitch = false;
	
	XTopWindow lastOpened;

	/**
	 * 
	 * Connect to Open Office using a certain port
	 * 
	 * <p>
	 * 
	 * @param port
	 */
	public OOReplayerMonitor(int port) {
		super();
		System.out.println("Setting up monitor");
		nPort = port;
		lastOpened = null;
	}

	public void closeLastOpened(){
		if (lastOpened != null){
			closeWindow(lastOpened);
			lastOpened = null;
		}else{
			System.out.println("No opened windows");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.replayer.AbsReplayerMonitor#setUp()
	 */
	@Override
	public void setUp() {

	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.replayer.AbsReplayerMonitor#cleanUp()
	 */
	@Override
	public void cleanUp() {
		//System.out.println("Cleanup");
		//closeApplication();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getAction(java.lang.String)
	 */
	@Override
	public GThreadEvent getAction(String actionName) {

		GThreadEvent retAction = null;

		EventData event = new EventData(actionName);

		try {
			System.err.println("sClass " + event.getName());

			Class<?> c = Class.forName(event.getName());

			Object action = c.newInstance();

			retAction = (GThreadEvent) action;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return retAction;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getArguments(java.lang.
	 * String)
	 */
	@Override
	public Object getArguments(String action) {
		EventData event = new EventData(action);
		return event.parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getComponent(java.lang.
	 * String, edu.umd.cs.guitar.model.GXWindow)
	 */
	public OOXComponent getComponent(String componentID, OOXWindow window) {
		OOXComponent retComp;

		OOXWindow ooWindow = (OOXWindow) window;
		XTopWindow xTopWin = ooWindow.getXWindow();
		XAccessible xWindow = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xTopWin);

		OOComponentID compID = new OOComponentID(componentID);

		XAccessible component = null;
		XAccessibleContext xContext= null;

		while (xContext== null) {
			component = OOExploreElement.getFirstXAccessibleFromNameRole(xWindow,
					compID.getName(), compID.getRole());
			if(component!=null)
				xContext=component.getAccessibleContext();
			
			try {
				Thread.sleep(DELAY_STEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		retComp = new OOXComponent(component, window);

		return retComp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getWindow(java.lang.String)
	 */
	@Override
	public OOXWindow getWindow(String windowID) {
		OOXWindow retWindow;
		int max_attempts = 5;

		OOComponentID winID = new OOComponentID(windowID);

		Debugger.println("Finding window : [" + windowID + "]");

		XTopWindow window = null;
		int attempt = 0;
		while (window == null && attempt < max_attempts) {
		        //System.out.println("Getting window [" + winID.getName() + "]");
			//window = OOExploreElement.getTopWindowFromName(winID.getName(), office);
			window = OOExploreElement.getTopWindowFromName(windowID, office);
			/*
			try{
				window.toFront();
			} catch(Exception ex){}
			*/
			try {
				Thread.sleep(DELAY_STEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			attempt++;
		}
		
		retWindow = new OOXWindow(window);

		return retWindow;
	}

	/* (non-Javadoc)
	 * @see edu.umd.cs.guitar.replayer.AbsReplayerMonitor#connectToApplication()
	 */
	@Override
	public void connectToApplication() {
		if (this.office == null){
			this.office = new SimpleOffice(nPort);		
		
			windowListener = new OOWindowListener();
			xExtendedToolkit = this.office.getExtendedToolkit();
			xExtendedToolkit.addTopWindowListener(windowListener);
		}
	}

	@Override
	public void delay(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PropertyType> selectIDProperties(ComponentType comp) {
		// TODO Auto-generated method stub
		if (comp == null)
			return new ArrayList<PropertyType>();

		List<PropertyType> retIDProperties = new ArrayList<PropertyType>();

		AttributesType attributes = comp.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();
		for (PropertyType p : lProperties) {
			if (OOConstants.ID_PROPERTIES.contains(p.getName()))
				retIDProperties.add(p);
		}
		return retIDProperties;
		//return null;
	}

	public void closeWindow(XTopWindow xWindow) {
		
		System.err.println("Close window called for [" + xWindow.toString() + "]");
		
		//OOXWindow ooWindow = (OOXWindow) window;
		//XTopWindow xWindow = ooWindow.getXWindow();

		if (xWindow == null)
			return;

		XWindow xWin = (XWindow) UnoRuntime.queryInterface(XWindow.class,
				xWindow);

		if (xWin != null)
			xWin.setFocus();
		// xWin.dispose();

		XAccessible xaccessible = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xWindow);
		
		if (xaccessible == null) {
			System.err.println("Window cannot be access");
			return;
		}

		XAccessibleContext xWindowContext = xaccessible.getAccessibleContext();

		if (xWindowContext == null) {
			System.err.println("Window cannot be access");
			return;
		}

		if ("".equals(xWindowContext.getAccessibleName()))
			return;

		System.out.println("Closing Window: "
				+ xWindowContext.getAccessibleName());

		// Check if XCloseable is supported
		XCloseable xCloseable = (XCloseable) UnoRuntime.queryInterface(
				XCloseable.class, xaccessible);

		if (xCloseable != null) {
			try {
				xCloseable.close(true);
			} catch (CloseVetoException e) {
				e.printStackTrace();
			}
		} else {
			System.err
					.println("xCloseable is not supported. Trying to click on terminal button");
			
			if (clickTerminal(xWindow)) {
				System.out.println("Click terminal button sucess");

			} else {

				pause("Unnable to automatically close window *"
						+ xWindowContext.getAccessibleName() + "* !!!! "
						+ "Pls do it manually ");
			}
			
		}

		try {
			Thread.sleep(OOConstants.DELAY);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Closing done");

	}

	/**
	 * Try to find and click on a terminal button
	 * 
	 * @param xNewWindow
	 * @return
	 */
	private boolean clickTerminal(XTopWindow xNewWindow) {
		boolean result = false;

		XAccessible xRoot = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xNewWindow);

//		AbsOOEventHandler eAction = OOActionHandler.getInstance();
		OOEventHandler eAction = new OOActionHandler();

		for (String sTerminalNameFull : OOConstants.sTerminalWidgetList) {

			String[] sTerminalComp = sTerminalNameFull
					.split(GUITARConstants.NAME_SEPARATOR);

			String sTerminalName = (sTerminalComp[0] != null) ? sTerminalComp[0]
					: "";
			int nRole;
			if (sTerminalComp[1] != null)
				nRole = Integer.parseInt(sTerminalComp[1]);
			else
				nRole = AccessibleRole.PUSH_BUTTON;

			System.out.print("Finding " + sTerminalName + " button ...");
			XAccessible xTerminalWidget = OOExploreElement
					.getFirstXAccessibleFromNameRole(xRoot, sTerminalName,
							nRole);
			if (xTerminalWidget != null) {
				System.out.println("FOUND");

				try {
					
					GComponent gTerminal = new OOXComponent(xTerminalWidget, new OOXWindow(xNewWindow));
					eAction.perform(gTerminal, null);
					
					result = true;
					break;
				} catch (Exception e) {
					System.err
							.println("Terminal widget found but unable to click ");
					e.printStackTrace();
				}

			} else {
				System.out.println("FAIL");

			}
		}

		return result;
	}

	private void pause(String msg) {
		System.err.println(msg);
		// Pause
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			stdin.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class OOWindowListener implements XTopWindowListener {

		//UNORipperMonitor ooRipperMonitor;

		volatile LinkedList<XTopWindow> openedWindows = new java.util.LinkedList<XTopWindow>();

		public void reset() {
			openedWindows.clear();
		}

		public boolean isEmpty() {
			return (openedWindows.size() == 0);
		}

		@Override
		public void windowActivated(EventObject arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(EventObject arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(EventObject arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(EventObject arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowMinimized(EventObject arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowNormalized(EventObject arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(final EventObject aEvent) {

			synchronized (tempWinStack) {
				System.out.println("-----------------------------");
				System.out.println("WINDOW OPEN FIRED!!!!");

				XTopWindow xTopWin = (XTopWindow) UnoRuntime.queryInterface(
						XTopWindow.class, aEvent.Source);

				XAccessible xAccessible = (XAccessible) UnoRuntime
						.queryInterface(XAccessible.class, xTopWin);

				if (xAccessible == null) {
					System.err.println("Window unaccessible");
				}

				XAccessibleContext xContext = xAccessible
						.getAccessibleContext();

				if (xAccessible == null) {
					System.err.println("Window doesn't have context");
				}

				String sWinName = xContext.getAccessibleName().trim();

				
				
				if ("".equals(sWinName))
					return;
				
				if ("OpenOffice.org 3.3".equals(sWinName) ){
					if (discardSwitch){
						closeWindow(xTopWin);
					}
				}
				
				if (!(tempWinStack.contains(xTopWin)))
					tempWinStack.addLast(xTopWin);
				
				lastOpened = xTopWin;
			}
		}

		@Override
		public void disposing(EventObject arg0) {
			// TODO Auto-generated method stub

		}

		/**
		 * @return the openedWindows
		 */
		public LinkedList<XTopWindow> getOpenedWindows() {
			return openedWindows;
		}

	}
	
	public void closeApplication(){
		
		System.err.println("Closing app");
		discardSwitch = true;
		office.getDesktop().terminate();
		
		//closeWindow( xExtendedToolkit.getActiveTopWindow() );
		/*
		System.err.println("Here");
		if (lastOpened != null){
			closeWindow(lastOpened);
			lastOpened = null;
		}
		*/
		//Scanner sc = new Scanner(System.in);
		//sc.next();
		//closeWindow( xExtendedToolkit.getActiveTopWindow() );
	}

   /**
    * Placeholder function. See GReplayerMonitor for details.
    */
   @Override
   public GWindow
   waitForWindow(String sWindowTitle,
                 String sImageFilepath)
   throws IOException
   {
      return null;
   }
	
}
