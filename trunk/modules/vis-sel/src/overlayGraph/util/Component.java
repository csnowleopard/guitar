package overlayGraph.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Component {

	private String widgetID;
	private String url;
	private String htmlTag;
	private String nameTag;
	private String eventType;
	private String tagId;
	private String href;
	private List<Component> neighbors;
	public int x;
	public int y;
	
	public Component(String widgetID, String url,int x, int y, String htmlTag, String nameTag, String eventType, String tagId, String href){
		this.widgetID = widgetID;
		this.url = url;
		this.x = x;
		this.y = y;
		this.htmlTag = htmlTag;
		this.nameTag = nameTag;
		this.eventType = eventType;
		this.tagId = tagId;
		this.neighbors = new ArrayList<Component>();
		this.href = href;
	}
	
	public String getWidgetID(){
		return widgetID;
	}
	
	public Point getLocation(){
		return new Point(x,y);
	}
	
	public String getContainingPageURL(){
		return url;
	}
	
	public String getHtmlTag(){
		return htmlTag;
	}
	
	public String getNameTag(){
		return nameTag;
	}
	
	public String getEventType(){
		return eventType;
	}
	
	public String getTagId(){
		return tagId;
	}
	
	@Override
	public String toString() {
	    return widgetID + "(" + x + ", " + y + "): " + htmlTag;   
	}

    public List<Component> getNeighbors() {
        return neighbors;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
