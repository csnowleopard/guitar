package utils.guitar.testCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import dataModels.guitar.testCase.TestCaseStep;




/**
 * This class contains a static method that, given a sequence on events, will generate 
 * a .tst file for it. 
 * @author Andrew Guthrie
 */
public class TSTWriter {

	//	This is some example usage of the following functions:
	//
	//	public static void main(String[] args){
	//		ArrayList<String> test = new ArrayList<String>();
	//		test.add("100000");
	//		test.add("100200");
	//		test.add("103000");
	//		test.add("100400");
	//		test.add("100007");
	//		test.add("100080");
	//		test.add("103200");
	//		test.add("199800");
	//		test.add("102738");
	//		test.add("111870");
	//		ArrayList<Boolean> test2 = new ArrayList<Boolean>();
	//		test2.add(true);
	//		test2.add(false);
	//		test2.add(true);
	//		test2.add(false);
	//		test2.add(false);
	//		test2.add(false);
	//		test2.add(true);
	//		test2.add(false);
	//		test2.add(true);
	//		String S = createTST(test, test2);
	//		System.out.println(S);
	//		saveTST(S,"TippyTipper", "001");
	//	}
	//	
	//	
	private static final String startTestCase = "<TestCase>";
	private static final String endTestCase = "</TestCase>";
	private static final String startStep = "<Step>";
	private static final String endStep = "</Step>";
	private static final String startReachingStep = "<ReachingStep>";
	private static final String endReachingStep = "</ReachingStep>";
	private static final String startEventId = "<EventId>";
	private static final String endEventId = "</EventId>";

	/**
	 * Returns a String that corresponds to a .tst file for a given sequence of events.
	 * Here, IDs is an ArrayList of Event IDs, and isReaching is an ArrayList of 
	 * booleans describing whether or not the edge connecting the two events was a reaching edge
	 * @param Events
	 */
	public static String createTST(ArrayList<String> IDs, ArrayList<Boolean> isReaching){
		String tst = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
		tst = tst + "\n";
		tst = tst + startTestCase;
		tst = tst + "\n";
		for(int i = 0; i < IDs.size(); i++){
			tst = tst + "\t";
			tst = tst + startStep;
			tst = tst + "\n\t\t";
			tst = tst + startEventId + IDs.get(i) + endEventId;
			tst = tst + "\n\t\t";
			if(i == isReaching.size()){
				tst = tst + startReachingStep + "false" + endReachingStep;
			} else {
				tst = tst + startReachingStep + isReaching.get(i) + endReachingStep;
			}
			tst = tst + "\n\t";
			tst = tst + endStep;
			tst = tst + "\n";
		}
		tst = tst + endTestCase + "\n";
		return tst;
	}

	/**
	 * Here, S should be the .tst file generated in the createTST() function
	 * appName is the name of the App, so it knows where to save the .tst file
	 * fileName is the name of the new file
	 * @param S
	 */
	public static void saveTST(String S, String appName, String fileName){
		String path = "data/" + appName + "/testcases/" + fileName + ".tst";
		File file = new File(path);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(S);
			writer.close();
		} catch (IOException e) {
			String dirPath = "data/" + appName + "/testcases";
			new File(dirPath).mkdir();
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write(S);
				writer.close();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Same method as the other CreateTST, however this method only takes in an array of testCaseSteps,
	 * generating the same XML that the previous method would
	 * @param testCases
	 * @return
	 */
	public static String createTST(ArrayList<TestCaseStep> testCases){
		String tst = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
		tst = tst + "\n";
		tst = tst + startTestCase;
		tst = tst + "\n";
		for(int i = 0; i < testCases.size(); i++){
			tst = tst + "\t";
			tst = tst + startStep;
			tst = tst + "\n\t\t";
			tst = tst + startEventId + testCases.get(i).getEventID() + endEventId;
			tst = tst + "\n\t\t";
			if(i == testCases.size() - 1){
				tst = tst + startReachingStep + "false" + endReachingStep;
			} else {
				tst = tst + startReachingStep + testCases.get(i).isReachingStep() + endReachingStep;
			}
			tst = tst + "\n\t";
			tst = tst + endStep;
			tst = tst + "\n";
		}
		tst = tst + endTestCase + "\n";
		return tst;
	}

}


