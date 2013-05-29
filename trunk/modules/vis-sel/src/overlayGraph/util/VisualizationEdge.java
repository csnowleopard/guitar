package overlayGraph.util;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import java.awt.Color;
import java.awt.geom.Point2D;

public class VisualizationEdge extends PPath{
	private static final long serialVersionUID = 1L;
	Point2D startPt, endPt;
	Color webEventColor, submitEventColor, textBoxEventColor, webTextBoxColor, defaultColor;

	public VisualizationEdge(PNode srcNode, PNode destNode){

		this.surveyPoints(srcNode, destNode);
		this.drawEdge();		
	}
	
	//Load start/endPt's with coordinates
	private void surveyPoints(PNode srcNode, PNode destNode){
		
		this.startPt= srcNode.getFullBoundsReference().getCenter2D();
		this.endPt= destNode.getFullBoundsReference().getCenter2D();
	}
	
	private void drawEdge(){
		
		this.reset();
		this.moveTo((float)startPt.getX(), (float)startPt.getY());
		this.lineTo((float)endPt.getX(), (float)endPt.getY());
	}
	
	public void paintEdge(PNode srcNode){
		Component srcComp = (Component) srcNode.getAttribute("component");
		String eventType = srcComp.getEventType();
		
		if(eventType.contains("WebEvent")){

			setStrokePaint(webEventColor);
		}else if(eventType.contains("SubmitEvent")){

			setStrokePaint(submitEventColor);
		}else if(eventType.contains("TextBoxEvent")){

			setStrokePaint(textBoxEventColor);
		}else if(eventType.contains("WebTextBox")){

			setStrokePaint(webTextBoxColor);
		}else{

			setStrokePaint(defaultColor);
		}
	}
}
