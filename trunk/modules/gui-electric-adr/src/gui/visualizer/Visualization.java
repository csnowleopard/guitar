package gui.visualizer;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import dataModels.visualizer.Event;
import dataModels.visualizer.Widget;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visualization extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Visualization(HashMap<String, Event> eventsMap, HashMap<String, Widget> widgetsMap, ArrayList<String> windows) {
		//		setBounds(0, 0, 600, 600);  // grid visualization
		setBounds(0, 0, 800, 550); // image visualization
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container con = this.getContentPane();
		con.setBackground(Color.black);

		VisualizationCanvas canvas = new VisualizationCanvas(eventsMap, widgetsMap, windows);
		con.add(canvas);
	}
}

class VisualizationCanvas extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final Color NORMAL_EDGE_COLOR = Color.red;
	final Color REACHING_EDGE_COLOR = Color.blue;

	final Color TERMINAL_COLOR = Color.red;
	final Color RESTRICTED_FOCUS_COLOR = Color.orange;
	final Color UNRESTRICED_FOCUS_COLOR = Color.green;
	final Color SYSTEM_INTERACTION_COLOR = Color.blue;
	final Color MENU_OPEN_COLOR = Color.yellow;

	static Pattern viewStringClipper = Pattern.compile("^(android.view.|android.widget.)(.+)$");
	
	HashMap<String, Event> eventsMap;
	HashMap<String, Widget> widgetsMap;
	ArrayList<String> windows;

	public VisualizationCanvas(HashMap<String, Event> eventsMapIn, HashMap<String, Widget> widgetsMapIn, ArrayList<String> windowsIn) {
		eventsMap = eventsMapIn;
		widgetsMap = widgetsMapIn;
		windows = windowsIn;
	}

	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
//		paintV1(g2D);
		paintV2(g2D);
	}
	
	public void paintV3(Graphics2D g2D, String imgFile, String window) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(imgFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		drawVisualizationImage(g2D, img, null, 0, 0);
		
		for (Event e : eventsMap.values()) {
			if (widgetsMap.get(e.getWidget().getId()).getWidgetClass().equals(window)) {
				drawPoint(g2D, e, widgetsMap.get(e.getWidget().getId()));
			}
		}
	}
	
	public void paintV2(Graphics2D g2D) {
		
		int offset = 240;
		BufferedImage img = null;
		for (int i=0; i<windows.size(); i++) {
			try {
				img = ImageIO.read(new File("Screenshots/" + windows.get(i) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			drawVisualizationImage(g2D, img, null, offset * i, 0);
		}
		
		for (Event e : eventsMap.values()) {
			drawPoint(g2D, e, widgetsMap.get(e.getWidget().getId()));
		}
		
		Widget currWidget;
		for (Event e : eventsMap.values()) {
			currWidget = widgetsMap.get(e.getWidget().getId());
			drawString(g2D, currWidget.getTitle() + " (" + e.getId() + ")", (int) currWidget.getCoord().getX(), (int) currWidget.getCoord().getY(), currWidget);
		}
		
		for (Event fromE : eventsMap.values()) {
//			try {
//				//Thread.sleep(250);
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
			drawPoint(g2D, fromE, widgetsMap.get(fromE.getWidget().getId()));
			
			for (String k : fromE.getNormalEdgesToSelfFrom()) {
				Event toE = eventsMap.get(k);
				drawLine(g2D, fromE, toE, NORMAL_EDGE_COLOR);
				drawPoint(g2D, toE, widgetsMap.get(toE.getWidget().getId()));
			}
			for (String k : fromE.getReachingEdgesToSelfFrom()) {
				Event toE = eventsMap.get(k);
				drawLine(g2D, fromE, toE, REACHING_EDGE_COLOR);
				drawPoint(g2D, toE, widgetsMap.get(toE.getWidget().getId()));
			}
			drawPoint(g2D, fromE, widgetsMap.get(fromE.getWidget().getId()));
		}
		
//		for (Event e : eventsMap.values()) {
//			drawPoint(g2D, e, widgetsMap.get(e.getWidgetId()));
//		}
		
		
		for (Event e : eventsMap.values()) {
			currWidget = widgetsMap.get(e.getWidget().getId());
			drawString(g2D, currWidget.getTitle() + " (" + e.getId() + ")", (int) currWidget.getCoord().getX(), (int) currWidget.getCoord().getY(), currWidget);
		}
	}
	
	public void paintV1(Graphics2D g2D) {
		int i = 50, j = 50;
		for (Event e : eventsMap.values()) {
			if (i == 650) {
				j += 100;
				i = 50;
			}

			//e.setCoord(i, j);

			i += 100;
		}

		for (Event fromE : eventsMap.values()) {
			for (String k : fromE.getNormalEdgesToSelfFrom()) {
				Event toE = eventsMap.get(k);
				drawQuadCurve(g2D, fromE, toE, NORMAL_EDGE_COLOR);
			}
			for (String k : fromE.getReachingEdgesToSelfFrom()) {
				Event toE = eventsMap.get(k);
				drawQuadCurve(g2D, fromE, toE, REACHING_EDGE_COLOR);
			}
		}

		for (Event e : eventsMap.values()) {
			drawPoint(g2D, e);
		}
		
		Widget currWidget;
		for (Event e : eventsMap.values()) {
			currWidget = widgetsMap.get(e.getWidget().getId());
			drawString(g2D, currWidget.getTitle() + " (" + e.getId() + ")", (int) e.getCoord().getX(), (int) e.getCoord().getY(), e);
		}
	}

	public void drawString(Graphics g2D, String str, int x, int y, Widget w) {
		int offset = 240;
		for (int i=0; i<windows.size(); i++) {
			if (w.getWindow().equals(windows.get(i))) {
				offset *= i;
			}
		}
		
		Matcher m = null;
		if ((m = viewStringClipper.matcher(str)).matches()) { // get event's initial
			str = m.group(2);
		}
		
		g2D.setColor(Color.white);
		g2D.drawString(str, x + offset, y);
	}
	
	public void drawString(Graphics2D g2D, String str, int x, int y, Event e) {
		Matcher m = null;
		if ((m = viewStringClipper.matcher(str)).matches()) { // get event's initial
			str = m.group(1);
		}
		
		g2D.setColor(Color.white);
		g2D.drawString(str, x, y);
	}
	
	public void drawArc(Graphics2D g2D, int x1, int y1, int x2, int y2, int sd, int rd, int cl) {
		Arc2D.Float arc = new Arc2D.Float(x1, y1, x2, y2, sd, rd, cl);

		g2D.draw(arc);
	}

	public void drawCubicCurve(Graphics2D g2D, int x1, int y1, int ctrlx1, int ctrly1, int ctrlx2, int ctrly2, int x2, int y2) {
		CubicCurve2D.Float cubicCurve = new CubicCurve2D.Float(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);

		g2D.draw(cubicCurve);
	}

	public void drawEllipse(Graphics2D g2D, int x, int y, int w, int h) {
		Ellipse2D.Float oval = new Ellipse2D.Float(x, y, w, h);

		g2D.draw(oval);
	}

	public void drawLine(Graphics2D g2D, int x1, int y1, int x2, int y2) {
		Line2D.Float line = new Line2D.Float(x1, y1, x2, y2);

		g2D.draw(line);
	}

	public void drawLine(Graphics2D g2D, Event fromE, Event toE, Color edgeColor) {
		Point2D p1 = widgetsMap.get(fromE.getWidget().getId()).getCoord(), p2 = widgetsMap.get(toE.getWidget().getId()).getCoord();
		
		int windowOffset = 240, fromEOffset = 0, toEOffset = 0;
		for (int i=0; i<windows.size(); i++) {
			if (widgetsMap.get(fromE.getWidget().getId()).getWindow().equals(windows.get(i))) {
				fromEOffset = windowOffset * i;
			}
			if (widgetsMap.get(toE.getWidget().getId()).getWindow().equals(windows.get(i))) {
				toEOffset = windowOffset * i;
			}
			
		}
		
		Line2D.Double line = new Line2D.Double(p1.getX() + fromEOffset + 5, p1.getY() + 5, p2.getX() + toEOffset + 5, p2.getY() + 5);
		
		//g2D.setColor(fromE.get);
		g2D.draw(line);
		g2D.setColor(Color.white);
	}
	
	public void drawLine(Graphics2D g2D, Point2D p1, Point2D p2) {
		Line2D.Float line = new Line2D.Float(p1, p2);

		g2D.draw(line);
	}

	public void drawPoint(Graphics2D g2D, Event e) {
		double x = e.getCoord().getX();
		double y = e.getCoord().getY();
		Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, 10, 10);
		if(e.getType().equals("TEMINAL")){
			g2D.setColor(TERMINAL_COLOR);
		} else if(e.getType().equals("RESTRICTED FOCUS")){
			g2D.setColor(RESTRICTED_FOCUS_COLOR);
		} else if(e.getType().equals("UNRESTRICED FOCUS")){
			g2D.setColor(UNRESTRICED_FOCUS_COLOR);
		} else if(e.getType().equals("SYSTEM INTERACTION")){
			g2D.setColor(SYSTEM_INTERACTION_COLOR);
		} else if(e.getType().equals("MENU OPEN")){
			g2D.setColor(MENU_OPEN_COLOR);
		}
		g2D.fill(ellipse);

		g2D.setColor(Color.white);
	}

	public void drawPoint(Graphics2D g2D, Event e, Widget w) {

		int offset = 240;
		for (int i=0; i<windows.size(); i++) {
			if (w.getWindow().equals(windows.get(i))) {
				offset *= i;
			}
		}
		
		Ellipse2D.Float ellipseB = new Ellipse2D.Float((float) w.getCoord().getX() + offset - 1, (float) w.getCoord().getY() - 1, 12, 12);
		g2D.setColor(Color.white);
		g2D.fill(ellipseB);
		
		Ellipse2D.Float ellipse = new Ellipse2D.Float((float) w.getCoord().getX() + offset, (float) w.getCoord().getY(), 10, 10);
		if(e.getType().equals("TERMINAL")){
			g2D.setColor(TERMINAL_COLOR);
		} else if(e.getType().equals("RESTRICTED FOCUS")){
			g2D.setColor(RESTRICTED_FOCUS_COLOR);
		} else if(e.getType().equals("UNRESTRICED FOCUS")){
			g2D.setColor(UNRESTRICED_FOCUS_COLOR);
		} else if(e.getType().equals("SYSTEM INTERACTION")){
			g2D.setColor(SYSTEM_INTERACTION_COLOR);
		} else if(e.getType().equals("MENU OPEN")){
			g2D.setColor(MENU_OPEN_COLOR);
		}
		g2D.fill(ellipse);
	}

	public void drawPoint(Graphics2D g2D, int x, int y) {
		Ellipse2D.Float ellipse = new Ellipse2D.Float(x-5, y-5, 10, 10);

		g2D.fill(ellipse);
	}

	public void drawQuadCurve(Graphics2D g2D, int x1, int y1, int ctrlx, int ctrly, int x2, int y2) {
		QuadCurve2D.Float quadCurve = new QuadCurve2D.Float(x1, y1, ctrlx, ctrly, x2, y2);

		g2D.draw(quadCurve);
	}

	public void drawQuadCurve(Graphics2D g2D, Event fromE, Event toE, Color edgeColor) {
		Point2D p1 = fromE.getCoord(), p2 = toE.getCoord();
		double offset = Math.sqrt(((p1.getX()-p2.getX())*(p1.getX()-p2.getX()))+((p1.getY()-p2.getY())*((p1.getX()-p2.getX()))));
		offset /= 8;

		QuadCurve2D.Double quadCurve = new QuadCurve2D.Double(p1.getX() + 5, p1.getY() + 5, p2.getX() + offset + 5, p2.getY() - offset + 5, p2.getX() + 5, p2.getY() + 5);

		//g2D.setColor(fromE.getColor());
		g2D.draw(quadCurve);
		g2D.setColor(Color.white);
	}

	public void drawQuadCurveSpecial(Graphics2D g2D, Event fromE, Event toE, Color edgeColor) {
		Point2D p1 = widgetsMap.get(fromE.getWidget().getId()).getCoord(), p2 = widgetsMap.get(toE.getWidget().getId()).getCoord();
		
		int windowOffset = 240, fromEOffset = 0, toEOffset = 0;
		for (int i=0; i<windows.size(); i++) {
			if (widgetsMap.get(fromE.getWidget().getId()).getWindow().equals(windows.get(i))) {
				fromEOffset = windowOffset * i;
			}
			if (widgetsMap.get(toE.getWidget().getId()).getWindow().equals(windows.get(i))) {
				toEOffset = windowOffset * i;
			}	
		}
		
		QuadCurve2D.Double quadCurve = new QuadCurve2D.Double(p1.getX() + fromEOffset, p1.getY(), p2.getX() + 20, p2.getY() + 20, p2.getX() + toEOffset, p2.getY());

		//g2D.setColor(fromE.getColor());
		g2D.draw(quadCurve);
		g2D.setColor(Color.white);
	}

	public void drawRectangle(Graphics2D g2D, int x, int y, int w, int h) {
		Rectangle2D.Float rectangle = new Rectangle2D.Float(x, y, w, h);

		g2D.draw(rectangle);
	}

	public void drawRoundRectangle(Graphics2D g2D, int x, int y, int w, int h, int arcw, int arch) {
		RoundRectangle2D.Float roundRectangle = new RoundRectangle2D.Float(x, y, w, h, arcw, arch);

		g2D.draw(roundRectangle);
	}

	public void drawVisualizationImage(Graphics2D g2D, BufferedImage img, BufferedImageOp op, int x, int y) {
		g2D.drawImage(img, op, x, y);
	}
}