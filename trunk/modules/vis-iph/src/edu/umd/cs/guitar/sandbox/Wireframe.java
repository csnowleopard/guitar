package edu.umd.cs.guitar.sandbox;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import edu.umd.cs.guitar.gen.ComponentType;
import edu.umd.cs.guitar.graphbuilder.EFGBuilder;
import edu.umd.cs.guitar.graphbuilder.GUIBuilder;
import edu.umd.cs.guitar.gui.InputException;
import edu.umd.cs.guitar.helper.iGUITARHelper;

public class Wireframe extends JComponent {

	private static final long serialVersionUID = 1L;

	public void paint(Graphics g){
		 
		GUIBuilder gui;
		try {
			gui = new GUIBuilder("file/calculator_gui.xml");
		
			for (String c: gui.getWidgetList().keySet()){
				ComponentType comp = gui.getWidgetList().get(c);
				String x = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "x_absolute");
				String y = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "y_absolute");
				String height = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "height");
				String width = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "width");
				g.drawRect(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(width), Integer.parseInt(height));
				//g.drawChars(data, offset, length, x, y)
			}
		} catch (InputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  /*
		  g.setColor(Color.red);
		  g.drawRect(10,10,height,width);
		  g.setColor(Color.gray);
		  g.fillRect(11,11,height,width); 
		  g.setColor(Color.red);
		  g.drawOval(250,20, height,width);
		  g.setColor(Color.magenta);
		  g.fillOval(249,19,height,width); 
		  */
	}
}
