package edu.umd.cs.guitar;


import java.io.IOException;
import java.util.List;

import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.exception.RipperStateException;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;
import edu.umd.cs.guitar.ripper.GRipperMonitor;
import edu.umd.cs.guitar.ripper.Ripper;
import edu.umd.cs.guitar.util.AppUtil;

/**
 * 
 * @author Andrew Guthrie
 *
 */
public class ADRCaptureTool extends Ripper{

	public ADRCaptureTool(){
		super();
	}

	GUIStructure dGUIStructure = new GUIStructure();

	public GUIStructure getResults() {
		return dGUIStructure;
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
	 * Entry point for beginning the ripping process.
    *
    * The ripping process generates the .GUI file and other
    * artifacts (if any) in the strDataPath directory.
    *
    * Exceptions propagate up to this method as of now. Ideally, this
    * method must propagate it to the caller.
	 */
	public void
   execute(GComponent gcomp)
   {
      try {
			if (monitor == null) {
				throw new RipperStateException();
			}

			// 1. Set Up the environment
			monitor.setUp();

			// 2. Get the list of root window
			List<GWindow> gRootWindows = monitor.getRootWindows();

			if (gRootWindows == null) {
				throw new RipperStateException();
			}
			// 3. Main step: ripping starting from each root window in the list
			for (GWindow xRootWindow : gRootWindows) {
				xRootWindow.setRoot(true);
				monitor.addRippedList(xRootWindow);
	
				GUIType gRoot = ripWindow(xRootWindow, gcomp);
				this.dGUIStructure.getGUI().add(gRoot);
			}

			// 4. Generate ID for widgets
			if (this.idGenerator == null) {
				throw new RipperStateException();
			} else {
				idGenerator.generateID(dGUIStructure);
			}

			// 5. Clean up
			monitor.cleanUp();
		} catch (GException e) {

      } catch (IOException e) {

      } catch (Exception e) {

      }
	}
	
	public GUIType
	ripWindow(GWindow gWindow, GComponent gWinContainer)
			throws Exception
			{

		// 3. Rip all components of this window
		try {
			GUIType retGUI = gWindow.extractWindow();
			//GComponent gWinContainer = gWindow.getContainer();

			ComponentType container = null;

			// Replace window title with pattern if requested (useReg)
			if (gWinContainer != null) {
				container = ripComponent(gWinContainer, gWindow);
			}

			if (container != null) {
				retGUI.getContainer().getContents().getWidgetOrContainer().add(
						container);
			}

			return retGUI;
		} catch (Exception e) {
			throw e;
		}

			}

	/**
	 * method ripComponent has the same core functionality as the ripper's 
	 */
	public ComponentType ripComponent(GComponent component, GWindow window)
	{
		//printComponentInfo(component, window);

		// 2. Rip regular components
		ComponentType retComp = null;
		try {
			retComp = component.extractProperties();
			ComponentTypeWrapper compA = new ComponentTypeWrapper(retComp);

			GUIType guiType = null;

			if (window != null) {
				guiType = window.extractGUIProperties();
			}

			retComp = compA.getDComponentType();

			// TODO: check if the component is still available after ripping
			// its child window
			List<GComponent> gChildrenList = component.getChildren();
			int nChildren = gChildrenList.size();
			int i = 0;
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
			} else {
				// TODO: Must throw exception
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


}