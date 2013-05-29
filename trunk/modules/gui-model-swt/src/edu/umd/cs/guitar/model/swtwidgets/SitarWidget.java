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
/* Copyright (c) 2010
 * Matt Kirn (mattkse@gmail.com) and Alex Loeb (atloeb@gmail.com)
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model.swtwidgets;

import java.util.Collections;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.SitarDefaultAction;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.SitarConstants;
import edu.umd.cs.guitar.model.SitarGUIInteraction;
import edu.umd.cs.guitar.model.SitarWindow;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.EventWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The parent class of the Sitar widget adapter hierarchy. Instead of
 * subclassing this class, consider using a more appropriate wrapper type for
 * the widget in question such as {@code SitarControl}.
 * 
 * @author Gabe Gorelick
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 * 
 */
public abstract class SitarWidget extends GComponent {
	
	private final Widget widget;
	private final SitarWindow window;
	static int widgetCounter = 0;
	static ArrayList<Widget> list = new ArrayList<Widget>();
	private static boolean terminate = true;
	
	private SitarGUIInteraction lastInteraction;
	
	private int sync_flag;
	private int close_flag;
	private long old_handle;
	
	/**
	 * Wrap the given widget that lives in the given window.
	 * @param widget the widget to wrap
	 * @param window the window the widget lives in
	 */
	protected SitarWidget(Widget widget, SitarWindow window) {
		super(window);
		this.widget = widget;
		this.window = window;
		lastInteraction = null;
		sync_flag = 0;
		close_flag = 0;
		setData();
	}

    public void setData()
    {
        widget.getDisplay().syncExec(new Runnable() {
            public void run() {
                if(!list.contains(widget)){
                    String data = Integer.toString(widgetCounter);
                   
                    if(widget instanceof org.eclipse.swt.widgets.Control)
                    {
                        if(((Control)widget).getParent() != null)
                                data = data + " " + (String)((Control)widget).getParent().getData();
                    }
                    else if(widget instanceof org.eclipse.swt.widgets.MenuItem)
                    {
                        if(((MenuItem)widget).getParent() != null)
                            data = data + " " + (String)((MenuItem)widget).getParent().getData();
                    }
                    else if(widget instanceof org.eclipse.swt.widgets.Menu)
                    {
                        if(((Menu)widget).getParentItem() != null)
                        	data = data + " " + (String)((Menu)widget).getParentItem().getData();
			else if(((Menu)widget).getParentMenu() != null)
                        	data = data + " " + (String)((Menu)widget).getParentMenu().getData();
			else
                            data = data + " " + (String)((Menu)widget).getParent().getData();
		
                    }
                    else if(widget instanceof org.eclipse.swt.widgets.TabItem)
                    {
                        if(((TabItem)widget).getParent() != null)
                            data = data + " " + (String)((TabItem)widget).getParent().getData();
                    }
                    else if(widget instanceof org.eclipse.swt.widgets.TableItem)
                    {
                        if(((TableItem)widget).getParent() != null)
                            data = data + " " + (String)((TableItem)widget).getParent().getData();
                    }
                    else if(widget instanceof org.eclipse.swt.widgets.TableColumn)
                    {
                        if(((TableColumn)widget).getParent() != null)
                            data = data + " " + (String)((TableColumn)widget).getParent().getData();
                    }
                    else if(widget instanceof org.eclipse.swt.widgets.ToolItem)
                    {
                        if(((ToolItem)widget).getParent() != null)
                            data = data + " " + (String)((ToolItem)widget).getParent().getData();
                    }
                    else if(widget instanceof org.eclipse.swt.widgets.TreeItem)
                    {
                        if(((TreeItem)widget).getParent() != null)
                            data = data + " " + (String)((TreeItem)widget).getParent().getData();
                    }
                   
                    widget.setData(data);
                    widgetCounter++;

                    list.add(widget);
                    //getParent().get
                }
            }
        });
    }
	protected boolean isDisposed() {
		Pattern p = Pattern.compile("disposed", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(widget.toString());
		boolean b = m.find();

		if(b) {
			GUITARLog.log.debug("!!!! widget disposed");
			return true;
		}
		
		final AtomicBoolean is_disposed = new AtomicBoolean(false);
		window.getShell().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if(widget.isDisposed()) {
					is_disposed.set(true);
				}
			}
		});

		return is_disposed.get();
	}
	
	public void setRecorderListener(final List<EventWrapper> wEventList, final ComponentType retComp, final Map<EventWrapper, String> eventToIDMap, final List<StepType> stepList)
	{
		widget.getDisplay().syncExec(new Runnable() {
			public void run() {
				//add listener
				RecorderListener listen = new RecorderListener(wEventList,retComp,eventToIDMap,stepList, SitarWidget.this);
	/*				for (int i : SitarConstants.SWT_EVENT_LIST) {
					//exceptions that we dont' want to listen to
					if(i!=SWT.MouseMove&&i!=SWT.MouseEnter&&i!=SWT.MouseExit&&i!=SWT.MouseUp&&i!=SWT.MouseDown)
						widget.addListener(i,listen);
				}*/
				
				//widget.addListener(SWT.None,listen);
				//widget.addListener(SWT.KeyDown,listen);
			    //widget.addListener(SWT.KeyUp,listen);
/*				widget.addListener(SWT.MouseDown,listen);
			    if(widget instanceof MenuItem)
			    {
			    	((MenuItem)widget).addSelectionListener(listen);
			    }*/
			    //widget.addListener(SWT.MouseUp,listen);
			    //widget.addListener(SWT.MouseMove,listen);
			    //widget.addListener(SWT.MouseEnter,listen);
			    //widget.addListener(SWT.MouseExit,listen);
			    //widget.addListener(SWT.MouseDoubleClick,listen);
			    //widget.addListener(SWT.Paint,listen);
			    //widget.addListener(SWT.Move,listen);
			    //widget.addListener(SWT.Resize,listen);
				//widget.addListener(SWT.Dispose,listen);
				widget.addListener(SWT.Selection,listen);
				//widget.addListener(SWT.DefaultSelection,listen);
				//widget.addListener(SWT.FocusIn,listen);
				//widget.addListener(SWT.FocusOut,listen);
				//widget.addListener(SWT.Expand,listen);
				//widget.addListener(SWT.Collapse,listen);
				//widget.addListener(SWT.Iconify,listen);
				//widget.addListener(SWT.Deiconify,listen);
				widget.addListener(SWT.Close,listen);
				widget.addListener(SWT.Show,listen);
				//widget.addListener(SWT.Hide,listen);
				//widget.addListener(SWT.Modify,listen);
				//widget.addListener(SWT.Verify,listen);
				//widget.addListener(SWT.Activate,listen);
				//widget.addListener(SWT.Deactivate,listen);
				//widget.addListener(SWT.Help,listen);
				widget.addListener(SWT.DragDetect,listen);
				//widget.addListener(SWT.Arm,listen);
				//widget.addListener(SWT.Traverse,listen);
				//widget.addListener(SWT.MouseHover,listen);
				//widget.addListener(SWT.HardKeyDown,listen);
				//widget.addListener(SWT.HardKeyUp,listen);
				widget.addListener(SWT.MenuDetect,listen);
			}
		});
	}
	
	
	private class RecorderListener implements Listener
	{
		SitarWidget sWidget;
		List<EventWrapper> eventList;
		ComponentType widget;
		Map<EventWrapper, String> event2ID;
		List<StepType> testCaseSteps;
		public RecorderListener(List<EventWrapper> wEventList, ComponentType retComp, Map<EventWrapper, String> eventToIDMap, List<StepType> stepList, SitarWidget sWidget) {
			eventList=wEventList;
			widget=retComp;
			event2ID=eventToIDMap;
			testCaseSteps=stepList;
			this.sWidget =  sWidget;
		}
	
		public void handleEvent(Event uEvent) {
			
			
			//2)build efg data
			String widgetID = IDGenerator.idMap.get(widget); 
			GUITARLog.log.info(widgetID + " had an event");
			for(EventWrapper e:eventList)
			{
				String eventWidgetID = event2ID.get(e);
				if(eventWidgetID.equals(widgetID))
				{
					StepType newStep = new StepType();
					newStep.setEventId(e.getID());
					//temporarily set to false by default
					newStep.setReachingStep(false);
					testCaseSteps.add(newStep);
					GUITARLog.log.info(e.getID()+" occured");
				//	RecorderControlPanel.addEvent();
				}
			}
			
			//arg0.item.notifyListeners(arg0.type, arg0);
		}
	}

	/**
	 * Get the wrapped widget.
	 * @return the wrapped SWT {@code Widget}
	 */
	public Widget getWidget() {
		return widget;
	}
	
	/**
	 * Get the window the wrapped widget lives in
	 * @return the wrapped widget's window
	 */
	public SitarWindow getWindow() {
		return window;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		if (widget == null) {
			return "";
		}
		
		if(isDisposed()) {
			return "";
		}
		
		final AtomicReference<String> title = new AtomicReference<String>("");
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Object data = widget.getData("name");
				if (data != null && (data instanceof String)) {
					title.set((String) data);
				} else if (widget instanceof Decorations) {
					Decorations shell = (Decorations) widget;
					title.set(shell.getText());
				} else if (widget instanceof Item) {
					Item item = (Item) widget;
					title.set(item.getText());
				} else if (widget instanceof Button) {
					Button button = (Button) widget;
					title.set(button.getText());
				}
				
				// TODO finish once we understand what this method is for
			}
			
		});
				
		return title.get();
	}

	/**
	 * Get the X coordinate of this widget's location.
	 * 
	 * @see #getLocation()
	 */
	@Override
	public final int getX() {
		return getLocation().x;
	}

	/**
	 * Get the Y coordinate of this widget's location.
	 * 
	 * @see getLocation()
	 */
	@Override
	public final int getY() {
		return getLocation().y;
	}
	
	/**
	 * Gets the location of a {@link Control} relative to its ancestor
	 * {@link Shell}. Note that this <code>Shell</code> need not be a direct
	 * parent, e.g. it can be a grandparent. In that case, the position is
	 * relative to the first ancestor <code>Shell</code> encountered.
	 * 
	 * @return location of this widget
	 */
	protected Point getLocation() { // TODO refactor
		final Point[] point = new Point[1];
		point[0] = new Point(0, 0);
		
		if (!(widget instanceof Control)) {
			return point[0];
		}
		
		if(isDisposed()) {
			return point[0];
		}
		
		final Control[] control = new Control[1]; 
		control[0] = (Control) widget;

		if (control[0] == null || control[0] instanceof Shell) {
			return point[0];
		}

		control[0].getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {												
				while (!(control[0] instanceof Shell)) {
					point[0].x += control[0].getLocation().x;
					point[0].y += control[0].getLocation().y;
					control[0] = control[0].getParent();
					if (control[0] == null) {
						break;
					}
				}
			}
		});
		
		return point[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PropertyType> getGUIProperties() { 
		if(isDisposed()) {
			return Collections.emptyList();
		}

		final List<PropertyType> retList = new ArrayList<PropertyType>();
		
		String title = getTitle();
		if (title != null) {
			PropertyType prop = factory.createPropertyType();
			prop.setName(SitarConstants.TITLE_TAG);
			List<String> propertyValue = new ArrayList<String>();
			propertyValue.add(title);
			prop.setValue(propertyValue);
			retList.add(prop);
		}
		
		final Method[] methods = widget.getClass().getMethods();
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(Method m1, Method m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				List<String> propertyNames = new ArrayList<String>();
				
				synchronized (retList) {
					for (Method m : methods) {
						if (m.getParameterTypes().length > 0) {
							continue;
						}

						String sMethodName = m.getName();
						String sPropertyName = sMethodName;

						if (sPropertyName.startsWith("get")) {
							sPropertyName = sPropertyName.substring(3);
						} else if (sPropertyName.startsWith("is")) {
							sPropertyName = sPropertyName.substring(2);
						} else {
							continue;
						}

						// make sure property is in lower case
						sPropertyName = sPropertyName.toLowerCase();

						// we don't want duplicate properties, this happens, e.g. in Shell
						// which has getVisible() and isVisible()
						if (propertyNames.contains(sPropertyName)) {
							GUITARLog.log.debug("Ignoring duplicate property: "
									+ sPropertyName);
							continue;
						}

						if (SitarConstants.WIDGET_PROPERTIES_LIST
								.contains(sPropertyName)) {
							try {
								Object value = m.invoke(widget, new Object[0]);
								if (value != null) {
									PropertyType p = factory
											.createPropertyType();
									List<String> lPropertyValue = new ArrayList<String>();
									lPropertyValue.add(value.toString());
									p.setName(sPropertyName);
									p.setValue(lPropertyValue);
									retList.add(p);

									propertyNames.add(sPropertyName);
								}
							} catch (IllegalArgumentException e) {
								GUITARLog.log.error(e);
							} catch (IllegalAccessException e) {
								GUITARLog.log.error(e);
							} catch (InvocationTargetException e) {
								GUITARLog.log.error(e);
							}
						}
					}
				}
			}
		});
		
		return retList;
	}

	/**
	 * Get the class name of the wrapped widget. 
	 * 
	 * @see Class#getName()
	 */
	@Override
	public String getClassVal() {
		return widget.getClass().getName();
	}

	/**
	 * Get the events this widget supports. By default, {@code SitarWidget}s
	 * support {@link SitarDefaultAction} if they are listening for an event in
	 * {@link SitarConstants#SWT_EVENT_LIST}.
	 * 
	 * @return a list of supported events
	 * 
	 * @see Widget#isListening(int)
	 */
	@Override
	public List<GEvent> getEventList() {
		if(isDisposed()) {
			return Collections.emptyList();
		}
	
		List<GEvent> events = new ArrayList<GEvent>();
		
		final AtomicBoolean listening = new AtomicBoolean(false);
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (int i : SitarConstants.SWT_EVENT_LIST) {
					if (widget.isListening(i)) {
						listening.set(true);
						return;
					}
				}
			}
		});
		
		if (listening.get()) {
			events.add(new SitarDefaultAction());
		}
		
		return events;
	}

	/**
	 * Get the children of this widget.
	 * 
	 * @return a list of children
	 */
	@Override
	public abstract List<GComponent> getChildren();

	/**
	 * Get this widget's parent. By default, {@link Widget Widgets} have no
	 * parent, so this method returns <code>null</code>. Subclasses are
	 * encouraged to override this method if they wrap widgets that do have
	 * parents.
	 * 
	 * @return <code>null</code>
	 */
	@Override
	public GComponent getParent() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return type of windowing interaction. One of: TERMINAL, SYSTEM
	 * 
	 * @see GUITARConstants#TERMINAL
	 * @see GUITARConstants#SYSTEM_INTERACTION
	 */
	@Override
	public String getTypeVal() {
		if (isTerminal()) {
			return GUITARConstants.TERMINAL;
		} else {
			return GUITARConstants.SYSTEM_INTERACTION;
		}
	}

	/**
	 * Check if this widget has children.
	 * 
	 * @return {@code true} if has children
	 * 
	 * @see #getChildren()
	 */
	@Override
	public boolean hasChildren() {
		if(isDisposed()) {
			return false;
		}

		return getChildren().size() > 0;
	}

	/**
	 * Interact with this widget and extract important information. To minimize
	 * side effects, this is the central point to determine whether a widget is
	 * terminal and whether it expands the GUI.
	 * 
	 * @return data about this interaction
	 */
	public SitarGUIInteraction interact() {

		if (lastInteraction != null) {
			return lastInteraction;
		}
		
		SitarGUIInteraction interaction = new SitarGUIInteraction(this);
				
		if(isDisposed()) {
			return interaction;
		}
				
		final AtomicReference<Shell> shell = new AtomicReference<Shell>();
		final List<Shell> closedShells = new ArrayList<Shell>();
		final AtomicBoolean terminal = new AtomicBoolean(false);
		final AtomicReference<String> window_title = new AtomicReference<String>();
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// add filter for shell open
				final Listener showListener = new Listener() {
					@Override
					public void handleEvent(Event event) {
						if (event.widget instanceof Shell) {
							// TODO add to list
							shell.set((Shell) event.widget);
						}
					}
				};
				widget.getDisplay().addFilter(SWT.Show, showListener);
				
				final Shell shell = window.getShell();
				
				final List<Shell> allShells = new ArrayList<Shell>();
				for (Shell s : widget.getDisplay().getShells()) {
					allShells.add(s);
				}
				
				window_title.set(shell.getText());
				
				// Remove existing close listeners so they don't get notified.
				// This may not be necessary on all platforms, but better safe
				// than sorry
				Listener[] closeListeners = shell.getListeners(SWT.Close);
				for (Listener l : closeListeners) {
					shell.removeListener(SWT.Close, l);
				}
				
				ShellListener listener = new ShellAdapter() {
					@Override
					public void shellClosed(ShellEvent e) {
						synchronized (closedShells) {
							closedShells.add(shell);
						}
						
						synchronized (allShells) {
							allShells.remove(shell);
							if (allShells.isEmpty()) {
								terminal.set(true);
							}
						}
						
						e.doit = false; // prevent shell from actually closing
					}
				};
				
				shell.addShellListener(listener);
				
				shell.forceActive();
				sync_flag = 0;
				close_flag = 1;
//				old_handle=OS.GetActiveWindow();

//				GUITARLog.log.debug("!!!! widget getName - "+widget.toString().replaceAll("&",""));
				
				Pattern p = Pattern.compile("\\p{Punct}exit\\p{Punct}|\\p{Punct}quit\\p{Punct}", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(widget.toString().replaceAll("&",""));
				boolean b = m.find();

				if(b) {
//					GUITARLog.log.debug("!!!! widget exit");
					terminal.set(true);
					sync_flag=1;
				}
				else {
//					GUITARLog.log.debug("!!!! widget non-exit");
					notifyAllListeners();
				}
	
				// remove our close listener
				shell.removeShellListener(listener);
				
				// add back the close listeners we removed
				for (Listener l : closeListeners) {
					shell.addListener(SWT.Close, l);
				}
				
				// remove filter for shell open
				widget.getDisplay().removeFilter(SWT.Show, showListener);

				if( widget.getDisplay().getFocusControl () == shell )
				{
					close_flag = 0;
				}
				else
				{
					close_flag = 1;				
				}

			}
		});
		
		int local_counter = 0;

		do{
			try {
			  Thread.sleep(50);
			} catch (InterruptedException e) { }

			if(close_flag == 0) {
				break;
			}

			try
			{
				Robot robot = new Robot( );
				robot.keyPress( KeyEvent.VK_ESCAPE );
				robot.keyRelease( KeyEvent.VK_ESCAPE );
			}
			catch ( AWTException ae )
			{
				ae.printStackTrace( );
			}

			if(local_counter++ > 5 || sync_flag == 1)
				break;
		} while(true);

		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if(window.getShell().getText() != window_title.get())
				{
					window.getShell().setText(window_title.get());
				}
			}
		});

		interaction.setTerminal(terminal.get());
		
		List<Shell> openedShells = new ArrayList<Shell>();
		if (shell.get() != null) {
			openedShells.add(shell.get());
		}
		interaction.setOpenedShells(openedShells);
		
//		List<Shell> closedShells = new ArrayList<Shell>();
		// TODO manage closed shells
		interaction.setClosedShells(closedShells);
		
		// cache result so we only have to interact with GUI once, useful for minimizing side effects
		lastInteraction = interaction;
		
		return interaction;
	}

	/**
	 * Notify all listeners that are listening for an event in
	 * {@link SitarConstants#SWT_EVENT_LIST}. Subclasses should override this
	 * method if sending one of these events would cause harmful side effects,
	 * e.g. widget destruction.
	 */
	protected void notifyAllListeners() {
		widget.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Event event = new Event();
				for (int i : SitarConstants.SWT_EVENT_LIST) {
					event.type = i;
					if (widget.isListening(i)) {
						GUITARLog.log.debug("Notifying " + widget + " event type " + event.type);
						if(i == SWT.Verify || i == SWT.Modify)
						{
							// VerifyEvent ve = new VerifyEvent(event);
							// widget.notifyListeners(i, ve);
						}
						else
						widget.notifyListeners(i, event);
					}
				}
				sync_flag = 1;
			}
		});
	}
	
	/**
	 * <p>
	 * Checks whether a widget is terminal. A widget is terminal if it closes
	 * its parent shell. Note that this is technically different from
	 * terminating the application (although closing the root shell will
	 * terminate the application if it is the only root shell).
	 * </p>
	 * <p>
	 * Instead of merely checking the title of this widget for things like
	 * "Quit" or "Exit", as JFCModel does, this method invokes all actions the
	 * widget is listening on and checks if any of them attempt to close the
	 * shell. Override this method if this will cause undesired side effects.
	 * </p>
	 * 
	 * @return <code>true</code> if widget is terminal, <code>false</code> if
	 *         not
	 */
	@Override
	public boolean isTerminal() {
		//execute and figure out if the component is terminal only if we are not using the capture tool
		if (terminate)
		{
			String sName = getTitle();
			List<AttributesTypeWrapper> termSig = SitarConstants.sTerminalWidgetSignature;
			for (AttributesTypeWrapper sign : termSig) {
				String titleVals = sign.getFirstValByName(SitarConstants.TITLE_TAG);
	
				if (titleVals == null)
					continue;
	
				if (titleVals.equalsIgnoreCase(sName))
					return true;
	
			}
		}
		return false;
		
		//previous code that was removed since it conflicts with the implementation of other GUITAR implementations
/*		if (terminate)
			return interact().isTerminal();
		else
			return false;	*/
	}

	/**
	 * Returns whether this widget is enabled.
	 * 
	 * @deprecated Use {@link #isEnabled()} instead
	 * @return whether the widget is enabled
	 */
	@Override
	@Deprecated
	public final boolean isEnable() {
		return isEnabled();
	}

	/**
	 * <p>
	 * Returns whether this widget is enabled. As {@link Widget Widgets} by
	 * default have no notion of being enabled, this method is abstract.
	 * </p>
	 * <p>
	 * This method is simply the correct spelling of {@link #isEnable()}, but
	 * its use is preferred.
	 * </p>
	 * 
	 * @return whether this widget is enabled
	 */
	public abstract boolean isEnabled(); 
	// TODO concrete implementation that returns true?
	// since if no notion of being enabled, then can't be disabled
	public static void setTerminate (boolean terminate) {
		SitarWidget.terminate = terminate;
	}
}
