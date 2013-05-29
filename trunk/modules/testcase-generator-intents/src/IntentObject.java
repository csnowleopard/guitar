import java.util.ArrayList;
import java.util.List;

public class IntentObject {
	public String activity;
	public String packageName;
	public List<String> declaration = new ArrayList<String>();
	
	public String toString() {
		   //return "IntentObject {" + activity + ", " + intent_declarations + "}\n\n";
		String result = "IntentObject {" + activity + ", " + packageName + ", ";
		
		for (String s: declaration) {
			result = result + "[" + s + "]\n";
		}
		
		return result + "\n\n";
	}

}
