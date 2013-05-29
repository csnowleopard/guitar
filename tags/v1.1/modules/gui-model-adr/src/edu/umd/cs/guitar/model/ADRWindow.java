package edu.umd.cs.guitar.model;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;

import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ADRWindow extends GWindow implements Comparable<ADRWindow> {
	
	ADRActivity window;
	final static int port = 10737;
	
	@Override
	public int compareTo(ADRWindow op){	
		return this.window.title.compareTo(op.window.title);			
	}
	@Override
	public String toString(){
		return getTitle();
	}
	
	public ADRWindow(String view) {
		Type vlst = new TypeToken<ADRActivity>() {}.getType();		
		Gson gson = new Gson();
		
		this.window = gson.fromJson(view, vlst);
	}
	
	public ADRWindow(ADRActivity window) {
		this.window = window;
	}

	@Override
	public boolean equals(Object window) {
		return this.window.title.equals(((ADRWindow)window).window.title);			
	}

	@Override
	public GUIType extractGUIProperties() {
		GUIType retGUI;

		ObjectFactory factory = new ObjectFactory();
		retGUI = factory.createGUIType();

		// Window

		//AccessibleContext wContext = window.getAccessibleContext();
		ComponentType dWindow = factory.createComponentType();
		ComponentTypeWrapper gaWindow = new ComponentTypeWrapper(dWindow);
		dWindow = gaWindow.getDComponentType();

		gaWindow.addValueByName("Size", "737");

		retGUI.setWindow(dWindow);

		// Container

		ComponentType dContainer = factory.createContainerType();
		ComponentTypeWrapper gaContainer = new ComponentTypeWrapper(dContainer);

		gaContainer.addValueByName("Size", "737"); //wContext.getAccessibleComponent()
		//		.getSize().toString());
		dContainer = gaContainer.getDComponentType();

		ContentsType dContents = factory.createContentsType();
		((ContainerType) dContainer).setContents(dContents);

		retGUI.setContainer((ContainerType) dContainer);

		return retGUI;
	}

	@Override
	public GComponent getContainer() {
		ADRComponent ret = null;
		Socket socket = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		String line;
		
		// Skip this component if it's not an activity.
		// Widgets cannot have other widgets as its children.
		if (this.window == null)
			return null;

		try {
			System.out.println("==> getChildren() of " + this.window.type);
			
			socket = new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));

			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

			out.write("getContainer");
			out.newLine();
			out.flush();
			while(!in.ready());
			while ((line = in.readLine()) != null) {			
				Type vlst = new TypeToken<ADRView>() {}.getType();		
				Gson gson = new Gson();

				if (line.equals("END")) break;
				
				ADRView  view = gson.fromJson(line, vlst);
				ret = new ADRComponent(view, this);
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
		
		return ret;
	}

	@Override
	public boolean isModal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<PropertyType> getGUIProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return window.title;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}
	
	public void printInfo() {
		//window.printActivity();
	}
}
