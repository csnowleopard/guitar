/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.ripper;

import java.io.IOException;
import java.awt.AWTException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.exception.RipperStateException;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;
import edu.umd.cs.guitar.ripper.filter.GComponentFilter;

import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.util.AppUtil;


/**
 * The core ripping algorithm implementation.
 *
 * Exceptions encountered during the ripping processes are propagated
 * up from the lower level as high as possible. Ideally, the execute()
 * method should propagate it upwards. As of now, the execute() function
 * is as far as the exceptions propagate.
 *
 * Exceptions caused by GUITAR state errors are thrown as
 * RipperStateException. All other exceptions are caused by the AUT.
 *
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class Ripper
{
   /**
    * SECTION: DATA
    *
    * This section contains data structures and accessor functions
    * for the data structures.
    */

	/**
	 * Constructor with logger
	 * <p>
	 * 
	 * @param logger  External logger
	 */
	public
   Ripper(Logger logger)
   {
		super();
		this.log = logger;
		lOpenWindowComps = factory.createComponentListType();
		lCloseWindowComp = factory.createComponentListType();
	}

	/**
	 * Constructor without logger
	 */
	public Ripper()
   {
		this(Logger.getLogger("Ripper"));
	}


	static ObjectFactory factory = new ObjectFactory();

   /**
    * GUIStructure storing the ripped result
	 */
   GUIStructure dGUIStructure = new GUIStructure();

	/**
	 * @return the dGUIStructure
	 */
	public GUIStructure getResult() {
		return dGUIStructure;
	}


   /*
    * Ripper monitor. Monitor performs tasks such as detecting windows.
    */
	GRipperMonitor monitor = null;

	/**
	 * @return the monitor
	 */
	public GRipperMonitor getMonitor() {
		return monitor;
	}

	/**
	 * @param monitor  The monitor to set
	 */
	public void setMonitor(GRipperMonitor monitor) {
		this.monitor = monitor;
	}


   /**
    * Indicates if regular expression patterns will be used for
    * matching window titles.
    */
   boolean useReg = false;

	/**
	 * useReg accessor
	 */
   public void
   setUseRegex()
   {
      this.useReg = true;
   }

	/**
	 * Indicates thay GUI components images are to be ripped and archived.
    */
   boolean useImage = false;

	/**
	 * useImage accessor
	 */
   public void
   setUseImage()
   {
      this.useImage = true;
   }


	/**
	 * Comparator for widgets
	 */
	GIDGenerator idGenerator = null;

	/**
	 * @return the iDGenerator
	 */
	public GIDGenerator getIDGenerator() {
		return idGenerator;
	}

	/**
	 * @param iDGenerator   IDGenerator to use for the Ripper
	 */
	public void setIDGenerator(GIDGenerator iDGenerator) {
		idGenerator = iDGenerator;
	}


   /**
    * Path for storing artifacts
    */
   String strDataPath;

   /**
    * Set the path where the ripper can save artifacts.
    *
    * @param strDataPath   Name of path where the ripper stores artifacts
    */
   public void
   setDataPath(String strDataPath)
   {
      this.strDataPath = strDataPath;
   }


   // Window filter
	LinkedList<GWindowFilter> lWindowFilter =
      new LinkedList<GWindowFilter>();;

	/**
	 * Add a window filter
	 * 
	 * @param filter
	 */
	public void addWindowFilter(GWindowFilter filter) {
		if (this.lWindowFilter == null) {
			lWindowFilter = new LinkedList<GWindowFilter>();
		}

		lWindowFilter.addLast(filter);
		filter.setRipper(this);
	}

	/**
	 * Remove a window filter
	 * 
	 * @param filter
	 */
	public void removeWindowFilter(GWindowFilter filter) {
		lWindowFilter.remove(filter);
		filter.setRipper(null);
	}


   // Component filter
	LinkedList<GComponentFilter> lComponentFilter =
      new LinkedList<GComponentFilter>();

	/**
	 * Add a component filter
	 * 
	 * @param filter
	 */
	public void addComponentFilter(GComponentFilter filter) {
		if (this.lComponentFilter == null) {
			lComponentFilter = new LinkedList<GComponentFilter>();
		}
		lComponentFilter.addLast(filter);
		filter.setRipper(this);
	}

	/**
	 * Remove a component filter
	 * 
	 * @param filter
	 */
	public void removeComponentFilter(GComponentFilter filter) {
		lComponentFilter.remove(filter);
		filter.setRipper(null);
	}


   // Opened / closed window list
	ComponentListType lOpenWindowComps;
	ComponentListType lCloseWindowComp;

	/**
	 * @return the lOpenWindowComps
	 */
	public ComponentListType getlOpenWindowComps() {
		return lOpenWindowComps;
	}

	/**
	 * @return the lCloseWindowComp
	 */
	public ComponentListType getlCloseWindowComp() {
		return lCloseWindowComp;
	}


	// Log
	Logger log;

	/**
	 * @return the log
	 */
	public Logger getLog() {
		return log;
	}

	/**
	 * @param log   The log to set
	 */
	public void setLog(Logger log) {
		this.log = log;
	}


   /**
    * SECTION: LOGIC
    *
    * This section contains methods which implement the ripper
    * logic.
    */

	/**
	 * Entry point for beginning the ripping process.
    *
    * The ripping process generates the .GUI file and other
    * artifacts (if any) in the strDataPath directory.
    *
    * Exceptions propagate up to this method as of now. Ideally, this
    * method must propagate it to the caller.
	 */
	public void
   execute()
   {
      try {
			if (monitor == null) {
				GUITARLog.log.error("No monitor hasn't been assigned");
				throw new RipperStateException();
			}

			// 1. Set Up the environment
			monitor.setUp();

			// 2. Get the list of root window
			List<GWindow> gRootWindows = monitor.getRootWindows();

			if (gRootWindows == null) {
				GUITARLog.log.warn("No root window");
				throw new RipperStateException();
			}

			GUITARLog.log.info("Number of root windows: " + gRootWindows.size());

			// 3. Main step: ripping starting from each root window in the list
			for (GWindow xRootWindow : gRootWindows) {
				xRootWindow.setRoot(true);
				monitor.addRippedList(xRootWindow);
	
				GUIType gRoot = ripWindow(xRootWindow);
				this.dGUIStructure.getGUI().add(gRoot);
			}

			// 4. Generate ID for widgets
			if (this.idGenerator == null) {
				GUITARLog.log.warn("No ID generator assigned");
				throw new RipperStateException();
			} else {
				idGenerator.generateID(dGUIStructure);
			}

			// 5. Clean up
			monitor.cleanUp();
		} catch (GException e) {
			GUITARLog.log.error("GUITAR error while ripping" + e);

      } catch (IOException e) {
			GUITARLog.log.error("IO error while ripping" + e);

      } catch (Exception e) {
			GUITARLog.log.error("Uncaught exception while ripping" + e);
			GUITARLog.log.error("Likely AUT bug. If not, file GUITAR bug");

      }
	}

	/**
	 * Rip a window
	 * <p>
	 * 
	 * @param gWindow
	 * @return
	 */
	public GUIType
   ripWindow(GWindow gWindow)
   throws Exception, IOException
   {
		GUITARLog.log.info("------- BEGIN WINDOW -------");
		GUITARLog.log.info("Ripping window: *" + gWindow.getTitle() + "*");

		// 1. Rip special/customized components
		for (GWindowFilter wf : lWindowFilter) {
			if (wf.isProcess(gWindow)) {
				GUITARLog.log.info("Window filter "
						+ wf.getClass().getSimpleName() + " is applied");
				return wf.ripWindow(gWindow);
			}
		}

      // 2. Save an image of the window
		String sUUID = null;
		if (useImage) {
			try {
				sUUID = captureImage(gWindow.getContainer());
			} catch (AWTException e) {
				// Ignore AWT exceptions sUUID is null
			} catch (IOException e) {
				throw e;
			}
      }

      // 3. Rip all components of this window
		try {
			GUIType retGUI = gWindow.extractWindow();
			GComponent gWinContainer = gWindow.getContainer();

			ComponentType container = null;

			// Replace window title with pattern if requested (useReg)
			if (gWinContainer != null) {
            if (this.useReg) {
	            AppUtil appUtil = new AppUtil();
   	         GUITypeWrapper guiTypeWrapper = new GUITypeWrapper(retGUI);
			   	String sTitle = guiTypeWrapper.getTitle();
         	   String s = appUtil.findRegexForString(sTitle);
	            guiTypeWrapper.setTitle(s);
				}
				container = ripComponent(gWinContainer, gWindow);
         }

			if (container != null) {
				retGUI.getContainer().getContents().getWidgetOrContainer().add(
						container);
         }

			// Add generated UUID for the component
			if (useImage &&
				 sUUID != null) {
	         GUITypeWrapper guiTypeWrapper = new GUITypeWrapper(retGUI);
   	      guiTypeWrapper.addWindowProperty(GUITARConstants.UUID_TAG_NAME,
      	                                    sUUID);
			}

			return retGUI;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Rip a component
    *
    * As of now this method does not propagate exceptions.
    * It needs to be modified to progate exceptions. All callers
    * need to be modified to handle exceptions.
    *
	 * <p>
	 * 
	 * @param component
	 * @return
	 */
	public ComponentType
   ripComponent(GComponent component,
                GWindow window)
   {
		GUITARLog.log.info("");
		GUITARLog.log.info("----------------------------------");
		GUITARLog.log.info("Ripping component: ");
		GUITARLog.log.info("Signature: ");

		printComponentInfo(component, window);

		// 1. Rip special/customized components
		for (GComponentFilter cm : lComponentFilter) {
			if (cm.isProcess(component, window)) {
				GUITARLog.log.info("Filter " + cm.getClass().getSimpleName()
						+ " is applied");

				return cm.ripComponent(component, window);
			}
		}

		// 2. Rip regular components
		ComponentType retComp = null;
		try {
			retComp = component.extractProperties();
			ComponentTypeWrapper compA = new ComponentTypeWrapper(retComp);

      	if (useImage) {
			   String sUUID = null;
				try {
					sUUID = captureImage(component);
				} catch (AWTException e) {
					// Ignore AWTException. sUUID is null.
				} catch (IOException e) {
					throw e;
				}

				if (sUUID != null) {
					compA.addProperty(GUITARConstants.UUID_TAG_NAME, sUUID);
				}
	      }

			GUIType guiType = null;

			if (window != null) {
				guiType = window.extractGUIProperties();
         }

			retComp = compA.getDComponentType();

			// 2.1 Try to perform action on the component
			// to reveal more windows/components

			// clear window opened cache before performing actions
			monitor.resetWindowCache();

			if (monitor.isExpandable(component, window)) {
				monitor.expandGUI(component);
         } else {
				GUITARLog.log.info("Component is Unexpandable");
			}

			// Trigger terminal widget

			LinkedList<GWindow> lClosedWindows = monitor.getClosedWindowCache();

			String sTitle = window.getTitle();

			if (lClosedWindows.size() > 0) {

				GUITARLog.log.debug("!!!!! Window closed");

				for (GWindow closedWin : lClosedWindows) {
					String sClosedWinTitle = closedWin.getTitle();

					// Only consider widget closing the current window
					if (sTitle.equals(sClosedWinTitle)) {

						GUITARLog.log.debug("\t" + sClosedWinTitle);

						List<FullComponentType> lCloseComp = lCloseWindowComp
								.getFullComponent();

						FullComponentType cCloseComp = factory
								.createFullComponentType();
						cCloseComp.setWindow(closedWin.extractWindow()
								.getWindow());
						cCloseComp.setComponent(retComp);
						lCloseComp.add(cCloseComp);
						lCloseWindowComp.setFullComponent(lCloseComp);
					} // if
				} // for
			} // if

			if (monitor.isNewWindowOpened()) {

				List<FullComponentType> lOpenComp = lOpenWindowComps
						.getFullComponent();
				FullComponentType cOpenComp = factory.createFullComponentType();
				cOpenComp.setWindow(window.extractWindow().getWindow());
				cOpenComp.setComponent(retComp);
				lOpenComp.add(cOpenComp);
				lOpenWindowComps.setFullComponent(lOpenComp);

				LinkedList<GWindow> lNewWindows = monitor
						.getOpenedWindowCache();
				monitor.resetWindowCache();
				GUITARLog.log.info(lNewWindows.size()
						+ " new window(s) opened!!!");
				for (GWindow newWins : lNewWindows) {
					GUITARLog.log
							.info("*\t Title:*" + newWins.getTitle() + "*");
				}

				// Process the opened windows in a FIFO order
				while (!lNewWindows.isEmpty()) {

					GWindow gNewWin = lNewWindows.getLast();
					lNewWindows.removeLast();

					GComponent gWinComp = gNewWin.getContainer();

					if (gWinComp != null) {

						// Add invokelist property for the component
						String sWindowTitle = gNewWin.getTitle();
						compA = new ComponentTypeWrapper(retComp);
						compA.addValueByName(
								GUITARConstants.INVOKELIST_TAG_NAME,
								sWindowTitle);

						GUITARLog.log.debug(sWindowTitle + " recorded");

						retComp = compA.getDComponentType();

						// Check ignore window
						if (!monitor.isIgnoredWindow(gNewWin)) {

							if (!monitor.isRippedWindow(gNewWin)) {
								gNewWin.setRoot(false);
								monitor.addRippedList(gNewWin);

								GUIType dWindow = ripWindow(gNewWin);

								if (dWindow != null)
									dGUIStructure.getGUI().add(dWindow);
							} else {
								GUITARLog.log.info("Window is ripped!!!");
							}

						} else {
							GUITARLog.log.info("Window is ignored!!!");
						}
					}

					monitor.closeWindow(gNewWin);
				}
			}

			// TODO: check if the component is still available after ripping
			// its child window
			List<GComponent> gChildrenList = component.getChildren();
			int nChildren = gChildrenList.size();
			int i = 0;

			// Debug

			String lChildren = "[";
			for (int j = 0; j < nChildren; j++) {
				lChildren += gChildrenList.get(j).getTitle() + " - "
						+ gChildrenList.get(j).getClassVal() + "; ";
			}
			lChildren += "]";
			GUITARLog.log.debug("*" + component.getTitle() + "* in window *"
					+ window.getTitle() + "* has " + nChildren + " children: "
					+ lChildren);

			// End debug

			while (i < nChildren) {
				GComponent gChild = gChildrenList.get(i++);
				ComponentType guiChild = ripComponent(gChild, window);

				if (guiChild != null) {
					((ContainerType) retComp).getContents()
							.getWidgetOrContainer().add(guiChild);
				}

				if (nChildren < gChildrenList.size()) {
					nChildren = gChildrenList.size();
				}
			}

		} catch (Exception e) {
			if (e.getClass().getName().contains(
				"StaleElementReferenceException")) {
				/**
             * This can happen when performing an action causes a page
				 * navigation in the current window, for example, when
             * submitting a form.
             */
				GUITARLog.log.warn("Element went away: " + e.getMessage());
			} else {
				// TODO: Must throw exception
				GUITARLog.log.error("ripComponent exception", e);
			}

			/**
			 * We'll return the component we calculated anyway so it
			 * gets added to the GUI map. I'm not entirely sure this
			 * is the right thing to do, but it gets us further anyway.
			 */
			return retComp;
		}

		return retComp;
	}

	/**
	 * Print out debug info for the current component
	 * <p>
	 * 
	 * @param component
	 * @param window
	 */
	private void
   printComponentInfo(GComponent component, GWindow window)
   {
		String sComponentInfo = "\n";

		sComponentInfo += "<FullComponent>" + "\n";
		sComponentInfo += "\t<Window>" + "\n";
		sComponentInfo += "\t\t<Attributes>" + "\n";

		sComponentInfo += "\t\t\t<Property>" + "\n";
		sComponentInfo += "\t\t\t\t<Name>" + GUITARConstants.TITLE_TAG_NAME
				+ "</Name>" + "\n";
		sComponentInfo += "\t\t\t\t<Value>" + window.getTitle() + "</Value>"
				+ "\n";
		sComponentInfo += "\t\t\t</Property> " + "\n";
		sComponentInfo += "\t\t</Attributes>" + "\n";
		sComponentInfo += "\t</Window>" + "\n";
		sComponentInfo += "\n";

		sComponentInfo += "\t<Component>" + "\n";
		sComponentInfo += "\t\t<Attributes>" + "\n";

		sComponentInfo += "\t\t\t<Property>" + "\n";
		sComponentInfo += "\t\t\t\t<Name>" + GUITARConstants.TITLE_TAG_NAME
				+ "</Name>" + "\n";
		sComponentInfo += "\t\t\t\t<Value>" + component.getTitle() + "</Value>"
				+ "\n";
		sComponentInfo += "\t\t\t</Property>" + "\n";
		sComponentInfo += "\n";

		sComponentInfo += "\t\t\t<Property>" + "\n";
		sComponentInfo += "\t\t\t\t<Name>" + GUITARConstants.CLASS_TAG_NAME
				+ "</Name>" + "\n";
		sComponentInfo += "\t\t\t\t<Value>" + component.getClassVal()
				+ "</Value>" + "\n";
		sComponentInfo += "\t\t\t</Property>" + "\n";
		sComponentInfo += "\t\t</Attributes>" + "\n";
		sComponentInfo += "\t</Component>" + "\n";
		sComponentInfo += "</FullComponent>" + "\n";
		sComponentInfo += "\n";

		GUITARLog.log.info(sComponentInfo);
	}


	/**
	 * Capture the image of GWindow
	 *
	 * @param  gWindow   Window whose image to capture.
	 * @return String UUID on success.
	 */
	private String
	captureImage(GComponent gComponent)
	throws IOException, AWTException
	{
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt((int)Math.pow(2, 30));
  	   try {
     	   monitor.captureImage(gComponent, strDataPath + "/" + randomInt);

      } catch (AWTException e) {
  	      GUITARLog.log.error("AWT exception while capturing image.");
			throw e;

      } catch (IOException e) {
  	      GUITARLog.log.error("IO Exception while ripping.");
     	   throw e;
		}

		return Integer.toString(randomInt);
	}

} // End of class
