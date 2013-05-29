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
package edu.umd.cs.guitar.ripper;

//import java.awt.Window;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.sun.star.accessibility.AccessibleRole;
import com.sun.star.accessibility.AccessibleStateType;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleStateSet;
import com.sun.star.awt.XExtendedToolkit;
import com.sun.star.awt.XTopWindow;
import com.sun.star.awt.XTopWindowListener;
import com.sun.star.awt.XWindow;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;

import edu.umd.cs.guitar.awb.SimpleOffice;
//mport edu.umd.cs.guitar.event.AbsEventHandler;
import edu.umd.cs.guitar.event.OOEventHandler;
import edu.umd.cs.guitar.event.OOExploreElement;
import edu.umd.cs.guitar.event.OOActionHandler;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
//import edu.umd.cs.guitar.model.JFCXWindow;
//import edu.umd.cs.guitar.model.JFCXWindow;
//import edu.umd.cs.guitar.model.JFCXWindow;
import edu.umd.cs.guitar.model.OOXComponent;
import edu.umd.cs.guitar.model.OOXWindow;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * @author Bao Nguyen
 * @author Wikum Dinalankara
 */
public class UNORipperMonitor extends GRipperMonitor {

	final int nPortNumber = 5678;
	SimpleOffice office;
	
	XExtendedToolkit xExtendedToolkit;
	UNOWindowListener windowListener;
	
	UNORipperConfiguration configuration;

	/**
	 * Temporary list of windows opened. Those windows are added while 
	 * a GUITAR event is perform 
	 * 
	 */
	volatile LinkedList<XTopWindow> tempWinStack = new LinkedList<XTopWindow>();

	/**
	 * Temporary list of windows opened during the expand event is being
	 * performed. Those windows are in a native form to prevent data loss.
	 * 
	 */
	volatile LinkedList<XTopWindow> tempOpenedWinStack = new LinkedList<XTopWindow>();

	volatile LinkedList<XTopWindow> tempClosedWinStack = new LinkedList<XTopWindow>();	
	
	/**
	 * 
	 * List of windows ripped
	 *  
	 */
	volatile List<String> lRippedWindow = new ArrayList<String>();
	List<String> sRootWindows = new ArrayList<String>();

	/**
	 * TODO: Add factory method
	 * 
	 * @param office
	 */
	public UNORipperMonitor(UNORipperConfiguration configuration) {
		super();
		// this.logger = logger;
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#cleanUp()
	 */
	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}	
	
	public void setUp() {
		office = new SimpleOffice(nPortNumber);
		//System.out.println("UNORipperMonitor: created simple office instance");
		windowListener = new UNOWindowListener();
		xExtendedToolkit = office.getExtendedToolkit();
		xExtendedToolkit.addTopWindowListener(windowListener);
	}

	public boolean isWindowClosed() {
		return (tempClosedWinStack.size() > 0);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#isNewWindowOpen()
	 */
	public boolean isNewWindowOpened() {
		return (this.tempOpenedWinStack.size() > 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#getNewWindowOpened()
	 */
	
	@Override
	public LinkedList<GWindow> getOpenedWindowCache() {

		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (XTopWindow xWindow : tempOpenedWinStack) {
			// xWindow = tempWinStack.pollLast();
			GWindow gWindow = new OOXWindow(xWindow);
			//if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}

	@Override
	public LinkedList<GWindow> getClosedWindowCache() {

		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();
		System.out.println("-- In Commented Area - getClosedWindowCache --- ");
		
		for (XTopWindow window : tempClosedWinStack) {
			GWindow gWindow = new OOXWindow(window);
			//if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		
		return retWindows;
	}	 
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#closeWindow(edu.umd.cs.guitar.model.GXWindow)
	 */
	@Override
	// Commented - changed from GXWindow to GWindow
	public void closeWindow(GWindow window) {

		OOXWindow unoWindow = (OOXWindow) window;
		XTopWindow xWindow = unoWindow.getXWindow();
		//XComponent x = unoWindow.getContainer();
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
			System.err.println("Window cannot be accessed");
			return;
		}

		XAccessibleContext xWindowContext = xaccessible.getAccessibleContext();

		if (xWindowContext == null) {
			System.err.println("Window cannot be accessed");
			return;
		}

		if ("".equals(xWindowContext.getAccessibleName()))
			return;

		System.out.println("Closing Window: "
				+ xWindowContext.getAccessibleName());

		// Check if XCloseable is supported
		XCloseable xCloseable = (XCloseable) UnoRuntime.queryInterface(
				XCloseable.class, xWin);

		XComponent xcomp = (XComponent) UnoRuntime.queryInterface(
				XComponent.class, xWin);
		
		if (xcomp != null){
			System.out.println( "XComp supported" );
			/*
			try {
				xcomp.dispose();
				return;
			} catch (Exception e) {
				System.err.println("Couldn't close from XComp");
				e.printStackTrace();
			}
			*/
		} else {
			System.out.println( "XComp not supported" );
		}
		
		// Commented
		if (xCloseable != null) {
		//if ( true ) {
			
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
				/*
				try{
					if (xcomp != null){
						xcomp.dispose();
					}
				} catch(Exception ex)
				{
					ex.printStackTrace();
				}
				*/
				pause("Unnable to automatically close window *"
						+ xWindowContext.getAccessibleName() + "* !!!! "
						+ "Pls do it manually ");
			}

		}

		try {
			Thread.sleep(OOConstants.DELAY);
			//Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Closing done");
		try{
			synchronized (tempClosedWinStack) {
				System.out.println("-----------------------------");
				System.out.println("WINDOW CLOSE FIRED!!!!");

				if (!(tempClosedWinStack.contains(xWindow)))
					tempClosedWinStack.addLast(xWindow);
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}

	}

	
	
	/**
	 * Try to find and click on a terminal button
	 * 
	 * @param xNewWindow
	 * @return
	 */
	private boolean clickTerminal(XTopWindow xNewWindow) {
		boolean result = false;
		System.out.println("clickTerminal : 0");
		XAccessible xRoot = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xNewWindow);
		
//		AbsUNOEventHandler eAction = UNOActionHandler.getInstance();
		OOEventHandler eAction = new OOActionHandler();
		System.out.println("clickTerminal : 1");
		for (String sTerminalNameFull : OOConstants.sTerminalWidgetList) {
		//for (String sTerminalNameFull : wList) {
			String[] sTerminalComp = sTerminalNameFull
					.split(GUITARConstants.NAME_SEPARATOR);

			String sTerminalName = (sTerminalComp[0] != null) ? sTerminalComp[0]
					: "";
			int nRole;
			System.out.println("clickTerminal : 2 " + sTerminalNameFull);
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

	/**
	 * Pause for a while
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#isIgnoredWindow(edu.umd.cs.guitar.model.GXWindow)
	 */
	@Override
	// Commented - changed from GXWindow to GWindow
	public boolean isIgnoredWindow(GWindow window) 
	{
		System.out.println("--- Commented Area : isIgnoredWindow ---- ");
		// Commented
		
		//String sWindow = window.getName();
		String sWindow = window.getTitle();
		return isInIgnoreCollection(sWindow, OOConstants.sIgnoreWindowList);
		
		//return true;
	}

	/**
	 * 
	 * A helper function for matching with wildcard
	 * 
	 * <p>
	 * 
	 * @param sName
	 * @param sCollection
	 * @return
	 */
	private boolean isInIgnoreCollection(String sName, List<String> sCollection) {

		for (String sIgnoreName : sCollection) {
			if (sIgnoreName.equals(sName))
				return true;
			else {
				if (sIgnoreName.endsWith(GUITARConstants.NAME_PATTERN_SUFFIX)) {
					String sWindowPattern = sIgnoreName.substring(0,
							sIgnoreName.length() - 1);
					if (sName.startsWith(sWindowPattern))
						return true;
				}
			}
		}
		return false;
	}

	class UNOWindowListener implements XTopWindowListener {

		UNORipperMonitor unoRipperMonitor;

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
		public void windowClosed(final EventObject aEvent) {
			// TODO Auto-generated method stub
			/*
			synchronized (tempClosedWinStack) {
				System.out.println("-----------------------------");
				System.out.println("WINDOW CLOSE FIRED!!!!");

				XTopWindow xTopWin = (XTopWindow) UnoRuntime.queryInterface(
						XTopWindow.class, aEvent.Source);

				XAccessible xAccessible = (XAccessible) UnoRuntime
						.queryInterface(XAccessible.class, xTopWin);

				if (xAccessible == null) {
					System.err.println("Window unaccessible");
				}

				XAccessibleContext xContext = xAccessible
						.getAccessibleContext();

				if (xContext == null) {
					System.err.println("Window doesn't have context");
				}

				String sWinName = xContext.getAccessibleName().trim();

				if ("".equals(sWinName))
					return;

				if (!(tempClosedWinStack.contains(xTopWin)))
					tempClosedWinStack.addLast(xTopWin);
			}
			*/
		}

		@Override
		public void windowClosing(EventObject aEvent) {
			// TODO Auto-generated method stub
			synchronized (tempClosedWinStack) {
				System.out.println("-----------------------------");
				System.out.println("WINDOW CLOSE FIRED!!!!");

				XTopWindow xTopWin = (XTopWindow) UnoRuntime.queryInterface(
						XTopWindow.class, aEvent.Source);

				XAccessible xAccessible = (XAccessible) UnoRuntime
						.queryInterface(XAccessible.class, xTopWin);

				if (xAccessible == null) {
					System.err.println("Window unaccessible");
				}

				XAccessibleContext xContext = xAccessible
						.getAccessibleContext();

				if (xContext == null) {
					System.err.println("Window doesn't have context");
				}

				String sWinName = xContext.getAccessibleName().trim();

				if ("".equals(sWinName))
					return;

				if (!(tempClosedWinStack.contains(xTopWin)))
					tempClosedWinStack.addLast(xTopWin);
			}
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

			synchronized (tempOpenedWinStack) {
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

				if (xContext == null) {
					System.err.println("Window doesn't have context");
				}

				String sWinName = xContext.getAccessibleName().trim();

				if ("".equals(sWinName))
					return;

				if (!(tempOpenedWinStack.contains(xTopWin)))
					tempOpenedWinStack.addLast(xTopWin);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#isExpandable(edu.umd.cs.guitar.model.GXComponent)
	 */
	@Override
	public boolean isExpandable(GComponent component, GWindow window) {
		
		OOXComponent unoComponent = (OOXComponent)component;
		XAccessible xAccessible = unoComponent.getXAccessible();
		
		if (!isEnable(xAccessible))
			return false;

		if (!isClickable(xAccessible)) {
			return false;
		}
		
		return true;

	}

	// Unclickable widget list
	// Sometimes used for dealing with widget implemented incorrectly
	List<Short> UNCLICKABLE_WIDGET_LIST = Arrays.asList(AccessibleRole.PANEL,
			AccessibleRole.LABEL, AccessibleRole.TEXT,
			AccessibleRole.PASSWORD_TEXT
	// ,AccessibleRole.RADIO_BUTTON
			);

	/**
	 * Check if a XAccessible element is clickable
	 * 
	 * @param child
	 * @return
	 */
	/*
	private boolean isClickable(Component component) {

		XAccessibleContext aContext = component.getAccessibleContext();

		if (aContext == null)
			return false;

		XAccessibleAction action = aContext.getAccessibleAction();

		if (action == null)
			return false;

		return true;
	}
	*/
	private boolean isClickable(XAccessible xAccessible) {

		if (xAccessible == null)
			return false;

		XAccessibleContext xAccessibleContext = xAccessible
				.getAccessibleContext();
		if (xAccessibleContext == null)
			return false;

		short role = xAccessibleContext.getAccessibleRole();
		if (UNCLICKABLE_WIDGET_LIST.contains(role))
			return false;

		// TODO:
		// Commented
		//System.out.println("----------- In Commented Area: isClickable -------");
		
		/*
		return edu.umd.cs.guitar.util.Util.isSupportedInterface(
				XAccessibleAction.class, xAccessibleContext);
		*/
		// Commented
		//return false;
		return true;
	}

	/**
	 * Check if a xAccesible is enable
	 * 
	 * @param xAccessible
	 * @return
	 */
	private boolean isEnable(XAccessible xAccessible) {

		if (xAccessible == null)
			return false;

		XAccessibleContext xAccessibleContext = xAccessible
				.getAccessibleContext();

		if (xAccessibleContext == null)
			return false;

		XAccessibleStateSet xStateSet = xAccessibleContext
				.getAccessibleStateSet();

		if (xStateSet == null)
			return false;
		
		return xStateSet.contains(AccessibleStateType.ENABLED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#expand(edu.umd.cs.guitar.model.GXComponent)
	 */
	// Commented
	
	@Override
	public void expandGUI(GComponent component) {

		OOXComponent unoComponent = (OOXComponent) component;
		XAccessible xAccessible = unoComponent.getXAccessible();

		//String[] avoid = {"File", "New", "Text Document", "Spreadsheet", "Edit", "View", "Insert", "Format", "Table", "Tools", "Window"};
		
		try {

			System.out.println("Clicking "
					+ xAccessible.getAccessibleContext().getAccessibleName());

			OOEventHandler eAction = new OOActionHandler();
			
			//System.out.println("--- In Commented Area : expandGUI ---- " + component.getTitle());
			// Commented
			//GComponent gComponent = new OOXComponent();//(xAccessible); 
			//eAction.actionPerform(gComponent);

			eAction.perform(component, null);

			System.out.println("Waiting  " + OOConstants.DELAY
					+ "ms for a new window to open");
			try {
				Thread.sleep(OOConstants.DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	 
	List<Short> IGNORE_WIDGET_ROLES = Arrays.asList(AccessibleRole.LIST_ITEM,
			AccessibleRole.TABLE_CELL);

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#resetWindowCache()
	 */
	@Override
	public void resetWindowCache() {
		this.tempOpenedWinStack.clear();
		this.tempClosedWinStack.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#isRippedWindow(edu.umd.cs.guitar.model.GXWindow)
	 */
	// Commented
	/*
	@Override
	public boolean isRippedWindow(GXWindow window) {
		String sWindowName = window.getName();
		return (lRippedWindow.contains(sWindowName));
	}
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#addRippedList(edu.umd.cs.guitar.model.GXWindow)
	 */
	// Commented
	/*
	@Override
	public void addRippedList(GXWindow window) {
		this.lRippedWindow.add(window.getName());
	}
	*/
	
	//--------------------------------
	// Root window
	//--------------------------------
	
	
	/**
	 * 
	 * Add a root window to be ripped 
	 * 
	 * <p>
	 * 
	 * @param sWindowName
	 */
	public void addRootWindow(String sWindowName){
		this.sRootWindows.add(sWindowName);
	}
	
	
	/**
	 * Remove a root window to be ripped
	 * 
	 * <p>
	 * 
	 * @param sWindowName
	 */
	public void removeRootWindow(String sWindowName){
		this.sRootWindows.remove(sWindowName);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.RipperMonitor#getRootWindows()
	 */
	// Commented
	
	@Override
	public List<GWindow> getRootWindows() {
		
		List<GWindow> retWindows = new ArrayList<GWindow>();
		
		XExtendedToolkit tk = office.getExtendedToolkit();


		int count = tk.getTopWindowCount();
		XTopWindow xTopWindow = null;

		for (int i = 0; i < count; i++) {
			try {
				xTopWindow = tk.getTopWindow(i);
				
				XAccessible xAccessible = (XAccessible) UnoRuntime
						.queryInterface(XAccessible.class, xTopWindow);
				if (xAccessible == null)
					continue;
				
				XAccessibleContext xAccessibleContext = xAccessible
						.getAccessibleContext();

				if (xAccessibleContext == null)
					continue;
				
				String accName = xAccessibleContext.getAccessibleName().trim();
				
				if("".equals(accName)|| "VCL ImplGetDefaultWindow".equals(accName))
					continue;
				
				// Default is ripping all window available
				if(sRootWindows.size()==0||(sRootWindows.contains(accName))){
					GWindow gWindow = new OOXWindow(xTopWindow);
					retWindows.add(gWindow);
				}
				

			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return retWindows;
	}
	
}
