
public class Event {

	private String eventType; //command
	private String target;
	private String value;
	private Boolean reachingType;
	private Component component;
	private String eventID;
	
	public Event(String command, String target, String value, Boolean reachingType,String eventID){
		this.eventType = command;
		this.target = target;
		this.value = value;
		this.reachingType = reachingType;
		this.eventID = eventID;
	}
	
	public String getEventType(){
		return eventType;
	}
	
	public void setEventID(String eventID){
		this.eventID = eventID;
	}
	
	public void setComponent(Component c){
		component = c;
	}
	
	public String getEventId(){
		return eventID;
	}
	
	public String getCommand(){
		return eventType;
	}
	
	public String getTarget(){
		return target;
	}
	
	public String getValue(){
		return value;
	}
	
	public Boolean isFinalEvent(){
		return reachingType;
	}
	
	public Component getComponent(){
		return component;
	}
	
	public String toString(){
		String toRet = "";
		toRet += "Command: " + eventType + "\n";
		toRet += "Target: " + target + "\n";
		toRet += "Value: " + value + "\n";
		toRet += "Final Event? " + reachingType + "\n";
		if (component != null)
			toRet += "ComponentClass name: " + component.getAttributes().get("name") + "\n";
		return toRet;
	}
}
