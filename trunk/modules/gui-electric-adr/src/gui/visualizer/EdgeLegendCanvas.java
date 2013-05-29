package gui.visualizer;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

/**
 * This class is an extension of the Canvas class used to display types of 
 * strokes of edges in the EdgeLegendPanel class.
 * 
 * @author Chris Carmel
 *
 */
public class EdgeLegendCanvas extends Canvas {

	/**
	 * Edge's stroke on the PVisualizationCanvas.
	 */
	private BasicStroke edgeStroke;
	
	/**
	 * Edge's color on the PVisualizationCanvas.
	 */
	private Color edgeStrokeColor;

	/**
	 * Constructs a basic EdgeLegendCanvas.
	 */
	public EdgeLegendCanvas() {
		super();
		this.setPreferredSize(new Dimension(25, 10));
		this.edgeStroke = new BasicStroke();
		this.edgeStrokeColor = Color.BLACK;
	}

	/**
	 * Constructs an EdgeLegendCanvas with the incoming stroke.
	 * 
	 * @param edgeStroke		stroke to set the constructed EdgeLegendCanvas's stroke to
	 */
	public EdgeLegendCanvas(BasicStroke edgeStroke) {
		super();
		this.setPreferredSize(new Dimension(25, 10));
		this.edgeStroke = edgeStroke;
		this.edgeStrokeColor = Color.BLACK;
	}
	
	/**
	 * Constucts an EdgeLegendCanvas with the incoming stroke color.
	 * 
	 * @param edgeStrokeColor	stroke color to set the constructed EdgeLegendCanvas's color to
	 */
	public EdgeLegendCanvas(Color edgeStrokeColor) {
		super();
		this.setPreferredSize(new Dimension(25, 10));
		this.edgeStrokeColor = Color.BLACK;
		this.edgeStrokeColor = edgeStrokeColor;
	}
	
	/**
	 * Constructs an EdgeLegendCanvas with the incoming stroke and color.
	 * 
	 * @param edgeStroke		stroke to set the constructed EdgeLegendCanvas's stroke to
	 * @param edgeStrokeColor	color to set the constructed EdgeLegendCanvas stroke color to
	 */
	public EdgeLegendCanvas(BasicStroke edgeStroke, Color edgeStrokeColor) {
		super();
		this.setPreferredSize(new Dimension(25, 10));
		this.edgeStroke = edgeStroke;
		this.edgeStrokeColor = edgeStrokeColor;
	}

	/**
	 * Returns the EdgeLegendCanvas's stroke.
	 * 
	 * @return 		the EdgeLegendCanvas's stroke
	 */
	public BasicStroke getEdgeStroke() {
		return edgeStroke;
	}
	
	/**
	 * Returns the EdgesLegendCanvas's stroke color.
	 * 
	 * @return		the EdgesLegendCanvas's stroke color
	 */
	public Color getEdgeStrokeColor() {
		return edgeStrokeColor;
	}
	
	/**
	 * Sets this EdgeLegendCanvas's stroke to the incoming value.
	 * 
	 * @param edgeStroke		value to set this EdgeLegendCanvas's stroke to
	 */
	public void setEdgeStroke(BasicStroke edgeStroke) {
		this.edgeStroke = edgeStroke;
	}
	
	/**
	 * Sets this EdgeLegendCanvas's stroke color to the incoming value.
	 * 
	 * @param color		value to set this EdgeLegendCanvas's stroke color to
	 */
	public void setEdgeStrokeColor(Color color) {
		this.edgeStrokeColor = color;
	}
	
	/**
	 * Paints this EdgeLegendCanvas with a single line with the specified stroke and color.
	 */
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setColor(edgeStrokeColor);
		g2D.setStroke(edgeStroke);
		drawLine(g2D, 0, 5, 25, 5);
	}
	
	/**
	 * Draws a line on the incoming Graphics2D at the specified coordinates.
	 * 
	 * @param g2D		Graphics2D to draw the line on
	 * @param x1		X coordinate of the starting point of the line
	 * @param y1		Y coordinate of the starting point of the line
	 * @param x2		X coordinate of the ending point of the line
	 * @param y2		Y coordinate of the ending point of the line
	 */
	public void drawLine(Graphics2D g2D, int x1, int y1, int x2, int y2) {
		Line2D line = new Line2D.Float(x1, y1, x2, y2);
		g2D.draw(line);
	}

}