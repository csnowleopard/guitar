import edu.umd.cs.guitar.model.* ;
import edu.umd.cs.guitar.model.data.* ;
import edu.umd.cs.guitar.model.wrapper.* ;

import java.util.LinkedList ;
import java.util.EventListener ;
import java.util.Arrays ;

import java.awt.Frame ;
import java.awt.Button ;
import java.awt.Component ;
import java.awt.Container ;
import java.awt.Window ;
import java.awt.event.ContainerEvent ;
import java.awt.event.ContainerListener ;
import java.awt.event.MouseEvent ;
import java.awt.event.MouseListener ;
import java.awt.event.ActionListener ;
import java.awt.event.ActionEvent ;
import java.awt.event.WindowListener ;
import java.awt.event.WindowEvent ;

import java.net.URLClassLoader ;
import java.net.URL ;

import javax.swing.JRadioButtonMenuItem ;
import javax.swing.AbstractButton ;
import javax.swing.JMenu ;
import javax.swing.event.MenuListener ;
import javax.swing.event.MenuEvent ;
import javax.swing.WindowConstants ;
import javax.swing.JFrame ;

import java.lang.reflect.Method ;

public class CaptureAsYouGo {
	/* This is a list of frames that should be ignored */
	private LinkedList<Window> locked_frames ;

	/* This is a list of frames that have been visited */
	private LinkedList<Window> visited_frames ;

	private MouseListener defaultMouseListener ;

	private boolean rootWindowRipped = false ;

	public interface ComponentInspector {
		public boolean inspect(Component component) ;
		public String getReplayableActionType() ;
	}

	private LinkedList<ComponentInspector> componentInspectors = new LinkedList<ComponentInspector>() ;

	public void addComponentInspector(ComponentInspector check) {
		this.componentInspectors.add(check) ;
	}

	public CaptureAsYouGo(MouseListener mouseListener) {
		this.defaultMouseListener = mouseListener ;
	}

	public static PropertyType makePropertyType(String name, String value) {
		PropertyType property = new PropertyType() ;
		property.setName(name) ;
		property.setValue(Arrays.asList(new String[]{value})) ;
		return property ;
	}

	public CaptureAsYouGo() {
		this.defaultMouseListener = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("MouseClicked!") ;
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		} ;
	}

	private class LockedFrameWindowListener implements WindowListener {
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {
			// System.out.println("WindowDeactivated") ;
			// JFK: Might use this to keep locked frames from being
			//      taken over by Modals/Dialogs
			// System.out.println("Deactivated, moving to front") ;
			// e.getWindow().toFront() ;
		}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
	}
	/* Call this before opening the application to be tested
	 * It determines which frames currently exist, so that none of them
	 * are included when adding the listeners */
	public void lockFrames() {
		this.locked_frames = new LinkedList<Window>() ;
		this.visited_frames = new LinkedList<Window>() ;
		Window frames[] = Window.getWindows() ;
		for(int i=0; i<frames.length; ++i) {
			frames[i].addWindowListener(new LockedFrameWindowListener()) ;
			this.locked_frames.add(frames[i]) ;
		}
	}

	/*public GUIType ripFrame(Frame frame) {
	  JFCXWindow window = new JFCXWindow(frame) ;
	  ContainerType topContainer = this.ripContainer(frame, window) ;
	  GUIType guiType = new GUIType() ;

	//guiType.setWindow(window.extractProperties()) ;
	guiType.setContainer(topContainer) ;

	return guiType ;
	}

	private ContainerType ripContainer(Container container, JFCXWindow window) {
	Component[]components = container.getComponents() ;
	ContainerType containerType = new ContainerType() ;

	for(Component component : components) {
	JFCXComponent jfcXComponent = new JFCXComponent(component, window) ;
	ComponentType componentType = jfcXComponent.extractProperties() ;

	if(jfcXComponent.hasChildren()) {
	ContainerType childContainer = this.ripContainer((Container)component, window) ;
	childContainer.setAttributes(componentType.getAttributes()) ;
	containerType.getContents().getWidgetOrContainer().add(childContainer) ;
	} else {
	containerType.getContents().getWidgetOrContainer().add(componentType) ;
	}
	}

	return containerType ;
	}*/

	private GUIStructure structure = new GUIStructure() ;

	public GUIStructure getStructure() {
		return this.structure ;
	}

	/*static private PropertyType replayableProperty = new PropertyType() ;

	{
		replayableProperty.setName("ReplayableAction") ;
		replayableProperty.setValue("edu.umd.cs.guitar.JFCActionHandler") ;
	}*/

	public PropertyType addDefaultListener(GComponent gcomponent) {
		Component component = ((JFCXComponent)gcomponent).getComponent() ;
		for(ComponentInspector inspector : this.componentInspectors) {
			if(inspector.inspect(component))
				return CaptureAsYouGo.makePropertyType("ReplayableAction", inspector.getReplayableActionType());
		}
		//if(this.defaultMouseListener != null)
			component.addMouseListener(this.defaultMouseListener) ;
		return null ;

		//this.defaultVariableListener.addListener(component);
		/*
		   Component jfcComponent = ((JFCXComponent)component).getComponent() ;

		   if(jfcComponent instanceof JMenu) {
		   JMenu jMenu = (JMenu) jfcComponent ;
		   System.out.println(jMenu.getMenuListeners().length) ;
		   jMenu.addMenuListener(new MenuListener() {
		   public void menuCanceled(MenuEvent e) { }
		   public void menuDeselected(MenuEvent e) { }
		   public void menuSelected(MenuEvent e) {
		   System.out.println("menuSelected") ;
		   }
		   }) ;
		   if(jfcComponent.getListeners(MenuListener.class).length > 0) {
		   System.out.println("\tHas MenuListener") ;
		   System.out.println("\t"+jMenu.getMenuListeners().length) ;
		   }

		   } else if(jfcComponent instanceof AbstractButton) {
		   AbstractButton abstractButton = (AbstractButton)jfcComponent ;
		   abstractButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
		   System.out.println("ActionPerformed") ;
		   } 
		   }) ;
		   } else if(jfcComponent instanceof Button) {
		   Button button = (Button) jfcComponent ;
		   button.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
		   System.out.println("ActionPerformed") ;
		   } 
		   }) ;
		   }
		/*else {
		jfcComponent.addVariableListener(this.defaultVariableListener) ;
		} */
	}

	private class CaptureContainerListener implements ContainerListener {
		private Component component ;
		private ContainerType containerType ;
		private JFCXWindow window ;
		CaptureContainerListener(Component component, ContainerType containerType, JFCXWindow window) {
			this.component = component ;
			this.containerType = containerType ;
			this.window = window ;
		}
		public void componentAdded(ContainerEvent event) {
			if(event.getChild() instanceof Window) {
				System.out.println("Component Added") ;
				ComponentType addedComponent = CaptureAsYouGo.this.ripComponent(new JFCXComponent(event.getChild(), window), window) ;
				containerType.getContents().getWidgetOrContainer().add(addedComponent) ;
			}
		}

		public void componentRemoved(ContainerEvent event) {}
	}

	private void addContainerListener(Component component, ContainerType containerType, JFCXWindow window) {
		if(component instanceof Container) {
			((Container)component).addContainerListener(new CaptureContainerListener(component, containerType, window)) ;
		}
	}

	// Essentially DFS ripper
	private ComponentType ripComponent(GComponent component, JFCXWindow window) {
		// First see if we need to add a property to the component
		PropertyType property  = this.addDefaultListener(component) ;
		// Do two things, first check if it is a container

		if(component.hasChildren()) {
			// Create the ContainerType object to return (ContainerType extends ComponentType)
			ContainerType container = new ContainerType() ;
			// Initialize the list of Contents
			container.setContents(new ContentsType()) ;

			// this.addContainerListener(((JFCXComponent)component).getComponent(), container, window) ;

			// Recursively call this function for each child of this component
			for(GComponent child : component.getChildren()) {
				// Get the GUITAR XML version of the child
				ComponentType temp = this.ripComponent(child, window) ;
				// And add to the contents list of the parent
				container.getContents().getWidgetOrContainer().add(temp) ;
			}

			// Finally need to extract the component Properties of the container
			container.setAttributes(component.extractProperties().getAttributes()) ;
			// And return
			if(property != null) {
				container.getAttributes().getProperty().add(property) ;
			}
			return container ;
		}
		// Otherwise it is a basic component
		else {
			// Extract the ComponentType from the component, and return it
			ComponentType type = component.extractProperties() ;
			if(property != null) {
				type.getAttributes().getProperty().add(property) ;
			}
			return type ;
		}
	}

	private class RipWindowListener implements WindowListener {
		CaptureAsYouGo capture ;

		RipWindowListener(CaptureAsYouGo capture) {
			this.capture = capture ;
		}

		public void windowActivated(WindowEvent e) {System.out.println("WindowActivated") ;}
		public void windowClosed(WindowEvent e) {System.out.println("WindowClosed");}
		public void windowClosing(WindowEvent e) {System.out.println("WindowClosing") ;}
		public void windowDeactivated(WindowEvent e) {
			System.out.println("WindowDeactivated") ;
			if(e.getOppositeWindow() != null)
				this.capture.structure.getGUI().add(this.capture.ripFrame(e.getOppositeWindow())) ;
		}
		public void windowDeiconified(WindowEvent e) {System.out.println("Deicon") ;}
		public void windowIconified(WindowEvent e) {System.out.println("Icon") ;}
		public void windowOpened(WindowEvent e) { System.out.println("WindowOpened") ;}
	} ;

	public GUIType ripFrame(Window frame) {
		// Build the GUITAR replica of the Frame - a GWindow
		// Replace this with the whatever platform specific code you have

		if(frame instanceof JFrame && ((JFrame)frame).getDefaultCloseOperation() == WindowConstants.EXIT_ON_CLOSE) {
			((JFrame)frame).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE) ;
		}

		JFCXWindow jfcWindow = new JFCXWindow(frame) ;
		// Get the container part of the GWindow
		GComponent jfcXContainer = jfcWindow.getContainer() ;

		// Recursively build the GUITAR replica of the GUI Structure
		ComponentType container = this.ripComponent(jfcXContainer, jfcWindow) ;

		// Get the properties of the window
		// GUIType has ComponentType and ContainerType components
		GUIType guiType = jfcWindow.extractWindow() ;
		// Add the top-level container to the GUIType container
		guiType.getContainer().getContents().getWidgetOrContainer().add(container) ;

		/*GUIStructure structure = new GUIStructure() ;
		structure.getGUI().add(guiType) ;
		IO.writeObjToFile(structure, "temp.xml") ;*/

		// this.addContainerListener(frame, (ContainerType)container, jfcWindow) ;
		// frame.addWindowListener(new RipWindowListener(this)) ;
		// JFK: As of now treating every window like a root window
		//      because we love hacky hacks
		//      Seriously this is here because as long as no new tests will
		//      be generated using the automated test generator, the
		//      rootWindow tag is useless, and it means we don't need to designate
		//      reaching actions
		if(!this.rootWindowRipped) {
			for(PropertyType propertyType : guiType.getWindow().getAttributes().getProperty()) {
				if(propertyType.getName().equals(GUITARConstants.ROOTWINDOW_TAG_NAME)) {
					propertyType.setValue(Arrays.asList(new String[]{"true"})) ;
				}
			}
			// this.rootWindowRipped = true ;
		}

		// Return this frames GUIType structure
		return guiType ;
	}

	public static void main(String[]args) throws Exception {
		/*Frame frame = new Frame("testing") ;
		  frame.setSize(100,100) ;
		  Button button1 = new Button("button1") ;
		  Button button2 = new Button("button2") ;
		  frame.add(button1) ;
		  frame.add(button2) ;
		  frame.add(new Button("button3")) ; */

		CaptureAsYouGo capture = new CaptureAsYouGo() ;

		//GUIStructure structure = new GUIStructure() ;
		//structure.getGUI().add(capture.ripFrame(frame)) ;

		capture.beginCapture() ;
		capture.loadApplication("GuitarJFCTest", new URL[]{new URL("file:./")}, new String[]{}) ;
	}

	/* loads and connects the application */
	public void loadApplication(String mainClass, URL[]urls, String[]args) throws Exception {

		//Load application using class loader.
		URLClassLoader loader = new URLClassLoader(urls) ;
		Class main = loader.loadClass(mainClass) ;
		Method mainMethod = main.getMethod("main", (new String[0]).getClass()) ;
		mainMethod.invoke(null, (Object)(new String[0])) ;
	}

	private Thread capture ;

	private boolean capturing = false ;

	public synchronized boolean isCapturing() {
		return this.capturing ;
	}

	public synchronized void stopCapture() {
		this.capturing = false ;
	}

	private Boolean ripping = false ;

	public  void enableRipping() {
		synchronized(this.ripping) {
			this.ripping = true ;
		}
	}

	public void disableRipping() {
		synchronized(this.ripping) {
			this.ripping = false ;
		}
	}

	public boolean isRipping() {
		synchronized(this.ripping) {
			return this.ripping ;
		}
	}

	public synchronized void beginCapture() {
		this.lockFrames() ;
		this.capturing = true ;
		this.ripping = true ;
		Runnable captureRunnable = new Runnable() {
			public void run() {
				try {
					while(CaptureAsYouGo.this.isCapturing()) {
						CaptureAsYouGo.this.checkAndRip(250) ;
					}
				} catch(InterruptedException ie) {
					System.out.println("Unable to delay quarter second in capture.run()") ;
					ie.printStackTrace() ;
				}
				System.out.println("stoppedCapture") ;
			}
		} ;
		this.capture = new Thread(captureRunnable, "capture") ;
		this.capture.start() ;
	}

	public synchronized void checkAndRip(int delay) throws InterruptedException {
		Thread.sleep(delay) ;
		synchronized(this.ripping) {
			if(this.ripping) {
				for(Window frame : CaptureAsYouGo.this.checkFrames()) {
					GUIType type = CaptureAsYouGo.this.ripFrame(frame) ;
					CaptureAsYouGo.this.structure.getGUI().add(type) ;
				}
			}
		}
	}

	public Thread getCaptureThread() {
		return this.capture ;
	}

	/* get the currently loaded frames
	 * if there are any new ones, add them to visited
	 */
	public LinkedList<Window> checkFrames() {
		//Frame frames[] = Frame.getFrames() ;
		Window frames[] = Window.getWindows() ;
		LinkedList<Window> check = new LinkedList<Window>() ;
		for(int i=0; i<frames.length; ++i) {
			if(!this.locked_frames.contains(frames[i]) && !this.visited_frames.contains(frames[i]) && frames[i].isVisible()) {
				System.out.println("Adding Frame") ;
				this.visited_frames.add(frames[i]) ;
				check.add(frames[i]) ;
			}
		}
		return check ;
	}
}
