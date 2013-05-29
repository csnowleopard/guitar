import java.util.Map;

import org.openqa.selenium.Point;

import edu.umd.cs.guitar.model.GUITARConstants;


public class Component {

	private static int counter = 0;
	private String id;
	private String tag;
	private Point loc;
	private String inputValue; //Unused
	private Map<String, String> attribute;
	//Name-Value or Name-Href if a tag
	
	public Component(String tag, Point p, Map<String, String> attribute){
		
		this.id = GUITARConstants.COMPONENT_ID_PREFIX + this.hashCode() + Integer.toString(counter);
		this.tag = tag;
		this.loc = p;
		this.attribute = attribute;
		this.inputValue = null;
	}
	
	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

	public String getX() {
		return Integer.toString(loc.getX());
	}

	public String getY() {
		return Integer.toString(loc.getY());
	}
	
	public String getId() {
		return id;
	}

	public String getTag() {
		return tag;
	}
	
	public Map<String,String> getAttributes(){
		return attribute;
	}
	
	public String toString(){
		String toRet = "";
		toRet += "Tag: " + tag + "\n";
		for (String key : attribute.keySet()){
			toRet += "\tKey: " + key + "\n";
			toRet += "\tValue: " + attribute.get(key) + "\n";
		}
		
		toRet += "X: " + loc.x + " Y: " + loc.y + "\n";
		return toRet;
	}	
}