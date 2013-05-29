import java.util.*;

public class DataObject {
	public List<IntentObject> intent = new ArrayList<IntentObject>();  //list of all possible intents in the app
	
	public String toString() {
		   return "DataObject: intents = " + intent + "";
	}
}
