import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Intent {
	public String action;
	public String activity;  //activity, class, component
	public String category;
	public String data;	
	public List<String> extras;	// list of strings of the arguments to intent.putExtra()	
	public boolean startActivity;  // true of startActivity() was invoked, otherwise false
	public List<String> other;  // other strings that we don't know how to use (i.e. restartActivity())
	public String uri;
	
	
	/*
	 * Testing
	 */
	public static HashMap<String, String> fullName = new HashMap<String, String>();
	public static ArrayList<String> permission = new ArrayList<String>();
	public static String packageName;
	public static String mainActivity;

	public Intent() {
		this.action = null;
		this.activity = null;  
		this.category = null;
		this.data = null;

		this.extras = new ArrayList<String>();

		this.startActivity = false;

		this.other = new ArrayList<String>();

		this.uri = null;
	}

	public Intent(String action, String activity, String category, String data, List<String> extras, boolean startActivity, 
			List<String> other, String uri) {
		this.action = action;
		this.activity = activity;  
		this.category = category;
		this.data = data;

		this.extras = new ArrayList<String>();
		this.extras.addAll(0, extras);

		this.startActivity = startActivity;

		this.other = new ArrayList<String>();
		this.other.addAll(0, other);

		this.uri = uri;
	}

	public String toString() {
		String result = "Intent: ( action: " + action + ", activity: " + activity + ", category: " + category + ", data: " + data + 
				", startActivity: " + startActivity + ", uri:" + uri + ", \n extras: [";
		for (String t: extras) 
			result = result + t + ", "; 
		result = result + "], \n other: [";
		for (String t: other) 
			result = result + t + ", "; 
		result = result + "]) \n";
		return result;
	}

	/* Process the json info stored in obj 
	/* need to check weird cases
	 * */
	public static HashMap<String, List<Intent>> getIntentsMap(DataObject obj) {
		HashMap<String, List<Intent>> intentsMap = new HashMap<String, List<Intent>>(); 

		/* parse the list of IntentObjects */
		for (IntentObject i: obj.intent) {
			List<String> decl = i.declaration;
			String curActivity = i.activity;
			String curPackage = i.packageName;

			String key = null;
			key = fullName.get(curActivity);

			List<Intent> targets = new ArrayList<Intent>();  //list of possible intents that can be fired from the current Activity

			String firstNewIntent = null;
			String secondNewIntent = null;

			/* for each new Intent declaration and its corresponding set methods */
			for (String d: decl) {
				/* if there's 2 new Intent declarations */
				if (d.indexOf("new Intent(") != d.lastIndexOf("new Intent(")) {		
					firstNewIntent = d.substring(0, d.lastIndexOf("new Intent(")-1);
					System.out.println("firstNewIntent: " + firstNewIntent);

					secondNewIntent = d.substring(d.lastIndexOf("new Intent("));
					System.out.println("secondNewIntent: " + secondNewIntent);
				}

				String temp[] = d.split(";");	/* split each line from the source code separated by ; */

				int secondIntent = 0;

				for (int index = 0; index < temp.length; index++) {
					if (temp[index].contains("new Intent"))
						secondIntent = index;
				}

				System.out.println("d: " + d + "\n");

				List<String> extras = new ArrayList<String>();
				List<String> other = new ArrayList<String>();
				String action = null;
				String activity = null;  
				String category = null;
				String data = null;		
				boolean startActivity = false;  // true of startActivity() was invoked, otherwise false
				String uri = null;
				boolean newIntent = false;

				for (int j = 0; j < temp.length; j++) {		/* s = line from source code */
					String s = temp[j];
					System.out.println("s: " + s);
					String curr = null; 

					if (s.contains("new Intent(")) {	/* declare new Intent */
						newIntent = true;
						/* parse the activity/component/class */
						if (s.contains(".class")) {
							//curr = s.substring(s.indexOf(',') + 2);
							curr = s.substring(s.indexOf(',') + 1);
							curr = curr.trim();
							System.out.println("==> activity: " + curr + "\n");	

							/*if(curPackage != null && (!curPackage.equals(""))) {
								//activity = curPackage + "/." + curr.substring(0, curr.indexOf('.'));
								activity = curr.substring(0, curr.indexOf('.'));
								activity = fullName.get(activity);
							} else {
								activity = curr.substring(0, curr.indexOf('.'));
							}*/
							activity = curr.substring(0, curr.indexOf('.'));
							activity = fullName.get(activity);

							System.out.println("==> activity: " + activity + "\n");	
						}
						/* parse the uri info */
						if (s.contains("Uri.parse(")) {			
							uri = s.substring(s.indexOf(',') + 1, s.length() - 1);
							uri = uri.trim();
							uri = uri.substring(uri.indexOf('(') + 1, uri.length() - 2);
							uri = uri.trim();
							System.out.print("==> uri: " + uri + "\n");
						}
						/* parse the action */
						if (s.contains("ACTION")) {			
							action = s.substring(s.indexOf('(') + 1, s.indexOf(','));
							System.out.print("==> action: " + action + "\n");
						}
						/* parse the action */
						if (s.contains("action")) {			
							action = s.substring(s.indexOf('\"') + 1, s.length() - 2);
							System.out.print("==> action: " + action + "\n");
						}
					} else if (s.contains(".setClass(")) { 
						curr = s.substring(s.indexOf(',') + 1);
						curr = curr.trim();
						activity = curr.substring(0, curr.indexOf('.'));

						/*if(curPackage != null && (!curPackage.equals("")))
							activity = curPackage + "/." + curr.substring(0, curr.indexOf('.'));
						else
							activity = curr.substring(0, curr.indexOf('.'));
						*/
						activity = fullName.get(activity);
						
						System.out.println("==> activity: " + activity + "\n");
					} else if (s.contains(".setAction(")) {	/* parse for action */
						action = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
						System.out.println("==> action: " + action + "\n");
					} else if (s.contains(".addCategory(")) {		/* parse for categories */
						category = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
						System.out.println("==> category: " + category + "\n");
					} else if (s.contains(".setData(")) {	/* parse for data */
						data = s.substring(s.indexOf('(') + 1, s.length() - 1);
						System.out.println("==> data: " + data + "\n");
					} else if (s.contains(".putExtra(")) {		/* parse for extras */
						curr = s.substring(s.indexOf('(') + 1, s.indexOf(','));
						System.out.print("==> extras: " + curr + ", ");
						extras.add(curr);

						curr = s.substring(s.indexOf(',') + 1, s.length() - 1);
						curr = curr.trim();
						System.out.println(curr + "\n");
						extras.add(curr);
					} else if (s.contains("startActivity(") && !s.contains("restartActivity(")) {		/* parse for startActivity() */
						startActivity = true;
						System.out.print("==> startActivity: " + startActivity + "\n");
					} else {
						other.add(s);	//miscellaneous lines of code (i.e. restartActivity(), etc..)
					}
					if (action == null && activity == null && category == null && data == null && startActivity == false && uri == null && !s.contains("new Intent(")) {
						newIntent = false;
						//System.out.println("newIntent set to false:" + action + activity + category + data + startActivity + uri + "\n");
					} else {
						newIntent = true;
						//System.out.println("newIntent set to true \n");
					}
					if (firstNewIntent != null && j == secondIntent - 1) {	//if there's 2 new Intent declarations in d and this is the last line before the second intent
						// create each new Intent 
						Intent curIntent = new Intent(action, activity, category, data, extras, startActivity, other, uri);

						// add new Intent to intents list 
						targets.add(curIntent);

						extras.clear();
						other.clear();
						action = null;
						activity = null;  
						category = null;
						data = null;		
						startActivity = false;  
						uri = null;
						firstNewIntent = null;
					} 
				} 
				System.out.println("\n");
				System.out.println("newIntent: " + newIntent + "\n");

				if (newIntent == true) {
					/* create each new Intent */
					Intent curIntent = new Intent(action, activity, category, data, extras, startActivity, other, uri);

					/* add new Intent to intents list */
					targets.add(curIntent);
				}
			} 

			/* check if the key already exists => if exists, just add the new intents from intents list to the existing intent list 
			 * if doesn't exist, add new key-value pair to hashmap*/
			if (intentsMap.containsKey(key)) {		
				intentsMap.get(key).addAll(targets);
			} else {
				intentsMap.put(key, targets);
			}

		} 

		System.out.println("intentsMap: " + intentsMap);

		return intentsMap;
	}

	/* this method process the intent object and returns string 
	 * representation that can trigger the intent from "adb -am" 
	 */
	public String intentToCmd(){
		String cmd = "am start ";
		//String temp = null;
		if((activity != null) && (!activity.equals(""))){
			cmd += "-n " + activity.replace(" ", "") + " ";
		}
		if((action != null) && (!action.equals(""))){
			/* temp = action.replace(" ", "");
			temp = temp.substring(temp.indexOf("_") + 1);
			cmd += "-a " + "android.intent.action." + temp + " "; */
			cmd += "-a " + action.replace(" ", "") + " "; 
		}
		if((category != null) && (!category.equals(""))){
			/* temp = category.replace(" ", "");
			temp = temp.substring(temp.indexOf("_") + 1);
			cmd += "-c " + "android.intent.category." + temp + " "; */
			cmd += "-a " + category.replace(" ", "") + " "; 
		}
		/*if((data != null) && (!data.equals(""))){
			cmd += "-d " + data.replace(" ", "") + " ";
		}*/
		if((uri != null) && (!uri.equals(""))){
			cmd += "-d " + uri.replace(" ", "") + " ";
		}
		for(String p: permission){
			cmd += p.trim() + " ";
		}
		return cmd;
	}
	
	public static void parse(String filename) throws IOException{
		FileReader f = new FileReader(new File(filename));
		BufferedReader b = new BufferedReader(f);
		String line;
		line = b.readLine();
		while(line != null){
			if(line.contains("package:") && (line.indexOf("package:") == 0))
				packageName = line.replace("package:", "").trim();
			else if(line.contains("activity:") && (line.indexOf("activity:") == 0)){
				if(line.contains("(main launcher activity)")){
					String temp = line.replace("activity:", "").replace("(main launcher activity)", "").trim();
					mainActivity = packageName + "/" + temp;
					if(temp.contains(".")){
						temp = temp.substring(temp.lastIndexOf(".")+1, temp.length());
					}
					System.out.println("temp: " + temp);
					System.out.println("mainActivity: " + mainActivity);
					fullName.put(temp, mainActivity);
				} else {
					String temp = line.replace("activity:", "").trim();
					String fullActName = packageName + "/" + temp;
					if(temp.contains(".")){
						temp = temp.substring(temp.lastIndexOf(".")+1, temp.length());
					}
					System.out.println("temp: " + temp);
					System.out.println("fullActName: " + fullActName);
					fullName.put(temp, fullActName);
				}
			} else if(line.contains("permission:") && (line.indexOf("permission:") == 0)){
				String temp = line.replace("permission:", "");
				System.out.println("permission: " + temp);
				permission.add(temp);
			}
			line = b.readLine();
		}
		b.close();
		f.close();
	}
	
	public static String getMainActivity(String filename) throws IOException{
		FileReader f = new FileReader(new File(filename));
		BufferedReader b = new BufferedReader(f);
		String line;
		line = b.readLine();
		while(line != null){
			if(line.contains("package:") && (line.indexOf("package:") == 0))
				packageName = line.replace("package:", "").trim();
			else if(line.contains("activity:") && (line.indexOf("activity:") == 0) &&
				line.contains("(main launcher activity)")){
					String temp = line.replace("activity:", "").replace("(main launcher activity)", "").trim();
					mainActivity = packageName + "/" + temp;
					b.close();
					f.close();
					return mainActivity;
				} 
			line = b.readLine();
		}
		b.close();
		f.close();
		return null;
	}

}
