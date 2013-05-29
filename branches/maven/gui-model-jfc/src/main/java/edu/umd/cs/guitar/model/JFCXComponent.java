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

package edu.umd.cs.guitar.model;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.imageio.ImageIO;
import javax.swing.JTabbedPane;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Implementation for {@link GWindow} for Java Swing
 * 
 * @see GWindow
 * 
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class JFCXComponent extends GComponent {

	Component component;

	/**
	 * @param component
	 */
	public JFCXComponent(Component component, GWindow window) {

		super(window);

		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GComponent#getGUIProperties()
	 */
	@Override
	public List<PropertyType> getGUIProperties() {
		List<PropertyType> retList = new ArrayList<PropertyType>();
		// Other properties

		PropertyType p;
		List<String> lPropertyValue;
		String sValue;

		// Title
		sValue = null;
		sValue = getTitle();
		if (sValue != null) {
			p = factory.createPropertyType();
			p.setName(JFCConstants.TITLE_TAG);
			lPropertyValue = new ArrayList<String>();
			lPropertyValue.add(sValue);
			p.getValue().clear();
			p.getValue().addAll(lPropertyValue);
			retList.add(p);
		}

		// Icon
		sValue = null;
		sValue = getIconName();
		if (sValue != null) {
			p = factory.createPropertyType();
			p.setName(JFCConstants.ICON_TAG);
			lPropertyValue = new ArrayList<String>();
			p.getValue().clear();
			p.getValue().addAll(lPropertyValue);
			retList.add(p);
		}

		// Index in parent
		if (isSelectedByParent()) {

			// Debugger.pause("GOT HERE!!!");

			sValue = null;
			sValue = getIndexInParent().toString();

			p = factory.createPropertyType();
			p.setName(JFCConstants.INDEX_TAG);
			lPropertyValue = new ArrayList<String>();
			lPropertyValue.add(sValue);
			p.getValue().clear();
			p.getValue().addAll(lPropertyValue);
			retList.add(p);

		}

		// get ActionListeners
		List<String> actionListener = getActionListenerClasses();
		if (actionListener != null) {
			p = factory.createPropertyType();
			p.setName("ActionListeners");
			p.getValue().clear();
			p.getValue().addAll(actionListener);
			retList.add(p);
		}

		// Get bean properties
		List<PropertyType> lBeanProperties = getGUIBeanProperties();
		retList.addAll(lBeanProperties);

		// Get Screenshot
		return retList;

	}

	/**
	 * Get component index in its parent
	 * 
	 * @return
	 */
	private Integer getIndexInParent() {

		// AccessibleContext aContext = aComponent.getAccessibleContext();
		AccessibleContext aContext = component.getAccessibleContext();
		if (aContext != null) {
			return aContext.getAccessibleIndexInParent();
		}

		return 0;
	}

	/**
	 * Check if the component is activated by an action in parent
	 * 
	 * @return
	 */
	private boolean isSelectedByParent() {
		// if (aComponent instanceof Component) {
		Container parent = ((Component) this.component).getParent();

		if (parent == null)
			return false;

		if (parent instanceof JTabbedPane)
			return true;
		// }
		return false;
	}

	/**
	 * Get all bean properties of the component
	 * 
	 * @return
	 */
	private List<PropertyType> getGUIBeanProperties() {
		List<PropertyType> retList = new ArrayList<PropertyType>();
		Method[] methods = component.getClass().getMethods();
		PropertyType p;
		List<String> lPropertyValue;

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
			} else
				continue;

			// make sure property is in lower case
			sPropertyName = sPropertyName.toLowerCase();

			if (JFCConstants.GUI_PROPERTIES_LIST.contains(sPropertyName)) {

				Object value;
				try {
					// value = m.invoke(aComponent, new Object[0]);
					value = m.invoke(component, new Object[0]);
					if (value != null) {
						p = factory.createPropertyType();
						lPropertyValue = new ArrayList<String>();
						lPropertyValue.add(value.toString());
						p.setName(sPropertyName);
						p.getValue().clear();
						p.getValue().addAll(lPropertyValue);
						retList.add(p);
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
		return retList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getChildren()
	 */
	@Override
	public List<GComponent> getChildren() {

		// GUITARLog.log.debug(component.getClass().getName());

		// GUITARLog.log.debug("ENTERING getChildren...");
		List<GComponent> retList = new ArrayList<GComponent>();

		if (component instanceof Container) {

			Container container = (Container) component;
			//
			// GUITARLog.log.debug("\t Component Chidren: "
			// + container.getComponentCount());

			try {
				int nChildren = 0;

				nChildren = container.getComponentCount();

				if (nChildren > 0) {
					for (int i = 0; i < nChildren; i++) {
						Component cChild = container.getComponent(i);
						GComponent gChild = new JFCXComponent(cChild, window);
						retList.add(gChild);
					}
				} else {
					AccessibleContext aContext = container
							.getAccessibleContext();
					if (aContext == null)
						return retList;
					nChildren = aContext.getAccessibleChildrenCount();
					for (int i = 0; i < nChildren; i++) {
						Accessible aChild = aContext.getAccessibleChild(i);
						if (aChild instanceof Component) {
							Component cChild = (Component) aChild;
							GComponent gChild = new JFCXComponent(cChild,
									window);
							retList.add(gChild);
						}
					}

				}

			} catch (Exception e) {
				GUITARLog.log.error("getChildren");
				GUITARLog.log.error(e);
			}
		}

		return retList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getParent()
	 */
	@Override
	public GObject getParent() {
		Component parent = this.component.getParent();

		return new JFCXComponent(parent, window);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		// AccessibleContext xContext = aComponent.getAccessibleContext();
		AccessibleContext xContext = component.getAccessibleContext();

		if (xContext == null)
			return false;

		// TODO: Check this
		int nChildren = xContext.getAccessibleChildrenCount();

		if (nChildren > 0)
			return true;

		if (component instanceof Container) {
			Container container = (Container) component;

			if (container.getComponentCount() > 0)
				return true;

		}

		return false;
	}

	/**
	 * @return the component
	 */
	public Component getComponent() {
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		List<PropertyType> lProperties = getGUIProperties();

		for (PropertyType property : lProperties) {
			if (JFCConstants.ID_PROPERTIES.contains(property.getName())) {
				String name = property.getName();

				result = prime * result + (name == null ? 0 : name.hashCode());

				List<String> valueList = property.getValue();

				if (valueList != null)
					for (String value : valueList) {
						result = prime * result
								+ (value == null ? 0 : value.hashCode());

					}
			}
		}

		// Class
		result = prime * result + GUITARConstants.CLASS_TAG_NAME.hashCode();
		result = prime * result + getClassVal().hashCode();

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JFCXComponent other = (JFCXComponent) obj;
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXObject#getName()
	 */
	@Override
	public String getTitle() {

		String sName = "";
		if (component == null)
			return "";
		AccessibleContext aContext = component.getAccessibleContext();

		if (aContext == null)
			return "";

		sName = aContext.getAccessibleName();

		if (sName != null)
			return sName;

		if (sName == null)
			sName = getIconName();

		if (sName != null)
			return sName;

		sName = component.getName();

		// Use the left sibling title instead
		Container parent = component.getParent();
		if (parent != null) {
			int index = getIndex(parent, component);
			if (index > 0) {
				Component rightComponent = parent.getComponent(index - 1);
				JFCXComponent jfcRightComponent = new JFCXComponent(
						rightComponent, window);
				sName = jfcRightComponent.getTitle();
			}
		}

		// In the worst case we must use the screen position to
		// identify the widget
		if (sName == null || "".equals(sName)) {
			sName = "Pos(" + this.getX() + "," + this.getY() + ")";
		}
		// }
		return sName;
	}

	private int getIndex(Container parent, Component component) {
		Component[] children = parent.getComponents();
		if (children == null)
			return -1;

		for (int index = 0; index < children.length; index++) {
			if (component.equals(children[index]))
				return index;
		}
		return -1;
	}

	/**
	 * Parse the icon name of a widget from the resource's absolute path.
	 * 
	 * <p>
	 * 
	 * @param component
	 * @return
	 */
	private String getIconName() {
		String retIcon = null;
		try {
			Class<?> partypes[] = new Class[0];
			Method m = component.getClass().getMethod("getIcon", partypes);

			String sIconPath = null;
			// if (m != null) {
			// Object obj = (m.invoke(aComponent, new Object[0]));
			// if (obj != null)
			// sIconPath = obj.toString();
			// }

			if (m != null) {
				Object obj = (m.invoke(component, new Object[0]));

				if (obj != null) {
					sIconPath = obj.toString();
				}
			}

			if (sIconPath == null || sIconPath.contains("@"))
				return null;

			String[] sIconElements = sIconPath.split(File.separator);
			retIcon = sIconElements[sIconElements.length - 1];

		} catch (SecurityException e) {
			// e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			return null;
		}
		return retIcon;
	}

	private List<String> getActionListenerClasses() {
		List<String> ret = null;
		Class<?> c = component.getClass();
		try {
			Method m = c.getMethod("getActionListeners");
			if (m.getReturnType() == ActionListener[].class) {
				ActionListener[] listeners = (ActionListener[]) m
						.invoke(component);
				if (listeners != null && listeners.length > 0) {
					// it is quite usual that only one listener is registered
					if (listeners.length == 1) {
						ret = new ArrayList<String>();
						ret.add(listeners[0].getClass().getName());
					} else {
						// to avoid duplicates a HashSet is used
						HashSet<String> tmp = new HashSet<String>();
						for (ActionListener al : listeners) {
							tmp.add(al.getClass().getName());
						}
						ret = new ArrayList<String>(tmp);
					}
				}
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return ret;
	}

	// TODO: Move this one to an external configuration file
	// Keeping it here because we don't want to interfere the current GUITAR
	// structure
	private static List<String> IGNORED_CLASS_EVENTS = Arrays.asList(
			"com.jgoodies.looks.plastic.PlasticArrowButton",
			"com.jgoodies.looks.plastic.PlasticComboBoxButton",
			"com.jgoodies.looks.plastic.PlasticSpinnerUI$SpinnerArrowButton",
			"javax.swing.plaf.synth.SynthScrollBarUI$1",
			"javax.swing.plaf.synth.SynthScrollBarUI$2",
			"javax.swing.plaf.synth.SynthArrowButton",
			"javax.swing.plaf.basic.BasicComboPopup$1",
			"javax.swing.JScrollPane$ScrollBar",
			"javax.swing.plaf.metal.MetalScrollButton",
			"javax.swing.plaf.metal.MetalComboBoxButton",
			"sun.awt.X11.XFileDialogPeer$2",
			"javax.swing.JScrollPane$ScrollBar", "sun.swing.FilePane$1",
			"sun.swing.FilePane$2", "sun.swing.FilePane$3",
			"sun.swing.FilePane$4", "sun.swing.FilePane$5"

	);

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getEventList()
	 */
	@Override
	public List<GEvent> getEventList() {

		List<GEvent> retEvents = new ArrayList<GEvent>();
		String sClass = this.getClassVal();

		if (IGNORED_CLASS_EVENTS.contains(sClass))
			return retEvents;

		EventManager em = EventManager.getInstance();

		for (GEvent gEvent : em.getEvents()) {
			if (gEvent.isSupportedBy(this))
				retEvents.add(gEvent);
		}

		return retEvents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getType()
	 */
	@Override
	public String getClassVal() {
		return component.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getTypeVal()
	 */
	@Override
	public String getTypeVal() {

		String retProperty;

		if (isTerminal())
			retProperty = GUITARConstants.TERMINAL;
		else
			retProperty = GUITARConstants.SYSTEM_INTERACTION;
		return retProperty;
	}

	/**
	 * Check if this component is a terminal widget
	 * 
	 * <p>
	 * 
	 * @return
	 */
	@Override
	public boolean isTerminal() {

		AccessibleContext aContext = component.getAccessibleContext();

		if (aContext == null)
			return false;

		AccessibleAction aAction = aContext.getAccessibleAction();

		if (aAction == null)
			return false;

		String sName = getTitle();

		List<AttributesTypeWrapper> termSig = JFCConstants.sTerminalWidgetSignature;
		for (AttributesTypeWrapper sign : termSig) {
			String titleVals = sign.getFirstValByName(JFCConstants.TITLE_TAG);

			if (titleVals == null)
				continue;

			if (titleVals.equalsIgnoreCase(sName))
				return true;

		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GComponent#isEnable()
	 */
	@Override
	public boolean isEnable() {
		try {
			Class[] types = new Class[] {};
			Method method = component.getClass().getMethod("isEnabled", types);
			Object result = method.invoke(component, new Object[0]);

			if (result instanceof Boolean)
				return (Boolean) result;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check if the component is activated by its parent. For example, a tab
	 * panel is enable by a select item call from its parent JTabPanel
	 * 
	 * <p>
	 * 
	 * @return
	 */
	public boolean isActivatedByParent() {

		if (component instanceof Component) {
			Container parent = ((Component) component).getParent();
			if (parent instanceof JTabbedPane) {
				return true;
			}

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GComponent#getX()
	 */
	@Override
	public int getX() {
		Component pointer = component;

		if (pointer == null || pointer instanceof Window)
			return 0;

		// Component pointerParent = component.getParent();

		int x = 0;

		while (!(pointer instanceof Window)) {
			x += pointer.getX();
			pointer = pointer.getParent();
			if (pointer == null)
				break;
		}

		return x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GComponent#getY()
	 */
	@Override
	public int getY() {
		Component pointer = component;

		if (pointer == null || pointer instanceof Window)
			return 0;

		// Component pointerParent = component.getParent();

		int y = 0;

		while (!(pointer instanceof Window)) {
			y += pointer.getY();
			pointer = pointer.getParent();
			if (pointer == null)
				break;
		}

		return y;
	}

	/**
	 * Hierarchically search "this" component for matching widget. The
	 * search-item is provided as an image via a file name.
	 * 
	 * @param sFilePath
	 *            Search item's file path.
	 */
	public GObject searchComponentByImage(String sFilePath)
			throws IOException {
		BufferedImage searchImage = null;

		try {
			searchImage = ImageIO.read(new File(sFilePath));
		} catch (IOException e) {
			// Image is expected
			GUITARLog.log.info("Could not read " + sFilePath);
			throw e;
		}

		return (GObject) searchComponentByImageInt(searchImage);
	}

	private JFCXComponent searchComponentByImageInt(BufferedImage searchImage) {
		JFCXWindow jfcxWindow = (JFCXWindow) this.window;

		BufferedImage thisImage = null;
		if (!this.getComponent().isShowing()) {
			return null;
		}

		Dimension size = this.getComponent().getSize();
		thisImage = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = thisImage.createGraphics();
		this.getComponent().paint(g2);
		g2.dispose();

		//boolean b = SikuliAPI.compare(thisImage, searchImage, 0.01);
		//if (b == true) {
		//	return this;
		//}

		List<GComponent> gChildren = getChildren();
		JFCXComponent result = null;

		for (GObject gChild : gChildren) {
			result = ((JFCXComponent) gChild)
					.searchComponentByImageInt(searchImage);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	@Override
	public List<PropertyType> getIDProperties() {
		ComponentType component = this.extractProperties();
		if (component == null) {
			return new ArrayList<PropertyType>();
		}

		List<PropertyType> retIDProperties = new ArrayList<PropertyType>();

		AttributesType attributes = component.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();

		for (PropertyType p : lProperties) {
			if (JFCConstants.ID_PROPERTIES.contains(p.getName()))
				retIDProperties.add(p);
		}
		return retIDProperties;
	}

} // End of class
