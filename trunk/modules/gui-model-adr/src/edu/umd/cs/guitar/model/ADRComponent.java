package edu.umd.cs.guitar.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.data.PropertyType;

public class ADRComponent extends GComponent implements Comparable<ADRComponent> {
	ADRView view = null;
	final static int port = 10737;
	int child_index;
	
	public ADRComponent(ADRView component, GWindow window) {
		super(window);
		this.view = component;
		// this.aComponent = (Accessible) component;
	}
	
	public ADRComponent(ADRView component, GWindow window, int cnt) {
		super(window);
		this.view = component;
		child_index = cnt;
		// this.aComponent = (Accessible) component;
	}

	@Override
	public List<GComponent> getChildren() {
		Set<GComponent> retListset = new TreeSet<GComponent>();
		Socket socket = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		String line;
		int cnt = 0;
		
		// Skip this component if it's not an activity.
		// Widgets cannot have other widgets as its children.
		if (this.view.childCount == 0)
			return new ArrayList<GComponent>();

		try {
			System.out.println("==> getChildren() of " + this.view.type);
			
			socket = new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

			out.write("getChildren");
			out.newLine();
			out.write(String.valueOf(this.view.id));
			out.newLine();
			out.flush();
			while(!in.ready());
			while ((line = in.readLine()) != null) {			
				Type vlst = new TypeToken<ADRView>() {}.getType();		
				Gson gson = new Gson();

				if (line.equals("END")) break;
				
				ADRView  view = gson.fromJson(line, vlst);
				ADRComponent component = new ADRComponent(view, window, 0);
				cnt++;
				retListset.add(component);
				/*
				Type vlst = new TypeToken<ArrayList<ADRView>>() {}.getType();
				ArrayList<ADRView> views = new ArrayList<ADRView>();
				
				Gson gson = new Gson();
				views = gson.fromJson(line, vlst);
				
				for (ADRView a : views) {
					GWindow gWindow = new ADRWindow();
					gWindow.set
					a.printView();
				}
				
				System.out.println(line);
				break;
				*/
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return new ArrayList<GComponent>(retListset);
	}

	@Override
	public String getClassVal() {
		return this.view.type;
	}
	
	private static List<String> IGNORED_CLASS_EVENTS = Arrays.asList(
			"android.app.Activity",
			"android.widget.LinearLayout",
			"android.widget.FrameLayout",
			"android.widget.RadioGroup",
			"android.widget.ScrollView",
			"android.widget.TableLayout",
			"android.widget.TableRow",
			"android.widget.TextView",
			"android.widget.ListView",
			"android.widget.CheckBox",
			"android.widget.Spinner"
			);
	
	private static List<String> NONEXPANDABLE_CLASS_EVENTS = Arrays.asList(
			"android.app.Activity",
			"android.widget.LinearLayout",
			"android.widget.FrameLayout",
			"android.widget.RadioGroup",
			"android.widget.ScrollView",
			"android.widget.TableLayout",
			"android.widget.TableRow",
			"android.widget.TextView",
			"android.widget.ListView",
			"android.widget.Spinner",
			"android.widget.CheckBox",
			"android.widget.EditText"
			);

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

	@Override
	public GComponent getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeVal() {
		String retProperty;

		// if (isActivatedByParent())
		// retProperty = GUITARConstants.ACTIVATED_BY_PARENT;

		// else
		if (isTerminal())
			retProperty = GUITARConstants.TERMINAL;
		else
			retProperty = GUITARConstants.SYSTEM_INTERACTION;
		return retProperty;
	}

	@Override
	public boolean hasChildren() {
		return (this.view.childCount > 0);
	}

	@Override
	public boolean isEnable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isTerminal() {
		String sClass = this.getClassVal();
		
		if (IGNORED_CLASS_EVENTS.contains(sClass))
			return false;
		
		return !(isExpandable());
	}

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
			p.setName(ADRConstants.TITLE_TAG);
			lPropertyValue = new ArrayList<String>();
			lPropertyValue.add(sValue);
			p.setValue(lPropertyValue);
			retList.add(p);
		}
		
		// Id
		sValue = null;
		sValue = GUITARConstants.COMPONENT_ID_PREFIX + String.valueOf(getId());
		if (sValue != null) {
			p = factory.createPropertyType();
			p.setName(GUITARConstants.ID_TAG_NAME);
			lPropertyValue = new ArrayList<String>();
			lPropertyValue.add(sValue);
			p.setValue(lPropertyValue);
			retList.add(p);
		}

/*
		// Icon
		sValue = null;
		sValue = getIconName();
		if (sValue != null) {
			p = factory.createPropertyType();
			p.setName(JFCConstants.ICON_TAG);
			lPropertyValue = new ArrayList<String>();
			lPropertyValue.add(sValue);
			p.setValue(lPropertyValue);
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
			p.setValue(lPropertyValue);
			retList.add(p);

		}
		
		// get ActionListeners
		List<String> actionListener = getActionListenerClasses();
		if (actionListener != null) {
			p = factory.createPropertyType();
			p.setName("ActionListeners");
			p.setValue(actionListener);
			retList.add(p);
		}

		// Get bean properties
		List<PropertyType> lBeanProperties = getGUIBeanProperties();
		retList.addAll(lBeanProperties);
*/
		// Get Screenshot
		return retList;
	}

	private int getId() {
		return this.view.id;
	}

	@Override
	public String getTitle() {
		return this.view.text;
	}

	@Override
	public int getX() {
		return this.view.x;
	}

	@Override
	public int getY() {
		return this.view.y;
	}

	public boolean isActivyty() {
		return false;
	}

	public boolean isClickable() {
		return this.view.isClickable;
	}
	
	public boolean isExpandable() {
		String sClass = this.getClassVal();
		
		if (NONEXPANDABLE_CLASS_EVENTS.contains(sClass))
			return false;
		
		return this.view.isExpandable;
	}
	
	public int getIndex() {
		return child_index;
	}

	@Override
	public int compareTo(ADRComponent arg0) {
		return new Integer(this.getId()).compareTo(new Integer(arg0.getId()));
	}

	public ADRWindow getWindow() {
		// TODO Auto-generated method stub
		return (ADRWindow) this.window;
	}

   /**
    * Hierarchically search "this" component for matching widget.
    * The search-item is provided as an image via a file name.
    *
    * This is a place holder function. This functionality is not
    * supported yet.
    *
    * @param sFilePath Search item's file path.
    */
   public GComponent
   searchComponentByImage(String sFilePath)
   throws IOException
   {
      return null;
   }
}
