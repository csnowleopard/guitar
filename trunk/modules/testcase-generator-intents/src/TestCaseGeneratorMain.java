import java.io.BufferedReader;	
import java.io.BufferedWriter;	
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;


public class TestCaseGeneratorMain {

	static HashMap<String, List<Intent>> intentsMap = null;
	//static HashSet<HashMap<Integer, String>> completePath = new HashSet<HashMap<Integer, String>>();
	static ArrayList<String> completePath = new ArrayList<String>();
	static ArrayList<String> completeSequence = new ArrayList<String>();
	static String start;
	public static void main(String[] args) {
		/* get info from the json file */
		//DataObject obj = getJsonInfo("../../../IntentOutputs/out.txt");	//convert the json text into a DataObject
		DataObject obj = getJsonInfo(args[0]);	//convert the json text into a DataObject

		try {
			//Intent.parse("../../../IntentOutputs/parsedManifest.txt");
			Intent.parse(args[1]);
			//String start = Intent.getMainActivity("../../../IntentOutputs/parsedManifest.txt");
			String start = Intent.getMainActivity(args[1]);

			intentsMap = Intent.getIntentsMap(obj);	//get the hashmap from each activity to list of Intents that are fired from that activity

			/* Find all possible sequence of intents using the hashmap */

			/* Generate the shell commands corresponding to each test case (sequence of intents) 
			 * print to a text file, .sh file, etc... */


			/** Siti changing from here -- trying to get the possible paths **/

			if(start == null){
				System.out.println("can't find main activity");
			} else {
				String path = new String(start);
				String sequence = new String("am start -n " + start);
				iterate(path, start, sequence);
			}

			/*for(String src: intentsMap.keySet()){
					if(src == null)
						continue;
					String path = new String(src);
					iterate(path, src);
				}*/

			try {
				//FileWriter fstream2 = new FileWriter("../../../IntentOutputs/sequence.txt",true);
				FileWriter fstream2 = new FileWriter(args[2],true);
				BufferedWriter out2 = new BufferedWriter(fstream2);
				for(String s: completePath){
					out2.append("\n" + s);
					System.out.println(s);
				}
				int i = 1;
				for(String s: completeSequence){
					out2.append("\n\nSequence " + i + "\n" + s);
					System.out.println("\n\n");
					System.out.println("Sequence " + i++);
					System.out.println(s);
				}
				out2.close();
			} catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			/*for(String src: intentsMap.keySet()){
						if(intentsMap.get(src) == null)
							continue;
						System.out.println("src: " + src);
						for(Intent i: intentsMap.get(src)){
							String cmd = i.intentToCmd();
							System.out.println(cmd);
						}
						System.out.println();
					}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/** Siti edit end **/
	}

	/* convert json formatted text files to DataObjects */
	public static DataObject getJsonInfo(String filename) {
		DataObject obj = null;
		Gson gson = new Gson();
		try {

			BufferedReader br = new BufferedReader(new FileReader(filename)); // filename of text file with json

			//convert the json string to object
			obj = gson.fromJson(br, DataObject.class);
			//System.out.println(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}


	/*this method does depth first search to map between activity
	 *  that launches the intent to the intent's activity*/
	public static void iterate(String pathArg, String curAct, String sequenceArg){
		String path = new String(pathArg);
		String sequence = new String(sequenceArg);
		System.out.println("curAct: " + curAct);
		if((!intentsMap.containsKey(curAct)) || (intentsMap.get(curAct) == null) || (intentsMap.get(curAct)).isEmpty()){
			if(!completePath.contains(path))
				completePath.add(new String(path));
			if(!completeSequence.contains(sequence))
				completeSequence.add(new String(sequence));
			return;
		}
		for(Intent targetIntent: intentsMap.get(curAct)){
			String act = targetIntent.activity;
			if((act == null) || path.contains(act)){
				if(!completePath.contains(path))
					completePath.add(new String(path));
				if(!completeSequence.contains(sequence))
					completeSequence.add(new String(sequence));
			} else {
				iterate(new String(path + " --> " + act), act, new String(sequence + "\n" + targetIntent.intentToCmd()));
			}
		}

	}



}

