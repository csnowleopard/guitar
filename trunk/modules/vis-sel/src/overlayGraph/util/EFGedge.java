
package overlayGraph.util;
import java.awt.Color;

import edu.umd.cs.piccolo.PNode;

/*
 * Appears thin and dark-colored, to distinguish from TCedge.
 */
public class EFGedge extends VisualizationEdge {
	private static final long serialVersionUID = 1L;

	public EFGedge(PNode srcNode, PNode destNode){
		
		super(srcNode, destNode);
		
		webEventColor= new Color(0, 67, 0);  //Dark Green
		submitEventColor= new Color(0, 0, 102);  //Dark Blue
		textBoxEventColor= new Color(102, 0, 0);  //Dark Red
		webTextBoxColor= new Color(255, 129, 40);  //Dark Orange
		defaultColor= new Color(0, 0, 0);  //Black
		this.paintEdge(srcNode);
	}
}
