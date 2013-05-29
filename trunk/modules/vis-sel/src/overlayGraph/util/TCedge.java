package overlayGraph.util;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.umd.cs.piccolo.PNode;

/*
 *  Appears thicker and neon-colored, to distinguis from EFGedge.
 */
public class TCedge extends VisualizationEdge {
	private static final long serialVersionUID = 1L;

	public TCedge(PNode srcNode, PNode destNode){
		
		super(srcNode, destNode);
		
		this.setStroke(new  BasicStroke(3));  //thicker than EFGedge
		
		webEventColor= new Color(55, 251, 55);  //Neon green
		submitEventColor= new Color(0, 161, 255);  //Neon blue
		textBoxEventColor= new Color(255, 0, 204);  //Neon red (pink)
		webTextBoxColor= new Color(255, 204, 51);  //Neon orange
		defaultColor= new Color(24, 24, 24);  //Dark Grey
		this.paintEdge(srcNode);
	}
}
