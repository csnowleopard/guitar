package edu.umd.cs.guitar.graphbuilder;

import java.util.*;

import edu.umd.cs.guitar.gen.StepType;
import edu.umd.cs.guitar.gen.TestCase;
import edu.umd.cs.guitar.helper.PrefixTree;
import edu.umd.cs.guitar.helper.TestCaseNode;
import edu.umd.cs.guitar.parser.XMLParser;

/**
 * Build test cases into appropriate format
 * @author Muhammad Ashraf Ishak
 *
 */

public class TestCaseBuilder{

	private HashMap<String, ArrayList<TestCase>> testCases;
	private HashMap<String, TestCase> testCaseFiles;
	private PrefixTree prefixTree;
	private EFGBuilder efg;
	
	
	/**
	 * Default constructor
	 * @param parser
	 */
	public TestCaseBuilder (XMLParser parser, EFGBuilder efg){
		testCases = parser.getTestCasesByParent();
		testCaseFiles = parser.getTestCases();
		this.efg = efg;
	}
	
	
	/**
	 * Build prefix tree of test cases
	 * @param testCaseFilesInput List of test case file names
	 * @return
	 */
	public PrefixTree getPrefixTree(ArrayList<String> testCaseFilesInput) {
		HashMap<String, ArrayList<TestCase>> tcs = new HashMap<String, ArrayList<TestCase>>();
		for (String f : testCaseFilesInput){
			String event1 = testCaseFiles.get(f).getStep().get(0).getEventId();
			if (tcs.get(event1) == null){
				tcs.put(event1, new ArrayList<TestCase>());
			}
			tcs.get(event1).add(testCaseFiles.get(f));
		}
		return buildPrefixTree (efg, tcs);
	}
	
	/**
	 * Removes all references to the test cases that result from pre-processing the data.
	 * 
	 * @param testCase an array of test cases to delete
	 */
	public void deleteTestCases(ArrayList<String> testCase){
		for(String s : testCase){
			testCaseFiles.remove(s);
			testCases.remove(s);
		}
	}
	
	// Build prefix tree of test cases
	private PrefixTree buildPrefixTree (EFGBuilder efg, HashMap<String, ArrayList<TestCase>> tcs){
		prefixTree = new PrefixTree(efg);
		HashMap<Integer, ArrayList<StepWithParent>> steps = stepListByLevelFile(tcs);
		// dna + prev step ==> root testcasenode
		HashMap <String, TestCaseNode> mapPrev = new HashMap<String, TestCaseNode>();
		for (Integer i: steps.keySet()){
			if (i == 0){
				for (String event: tcs.keySet()){
					TestCaseNode root = prefixTree.addRootNode(efg.getViewByEvent(event), event);
					mapPrev.put(event, root);
				}
				prefixTree.moveDownRow();
			} else {
				// map current step using the previous dna
				ArrayList<StepWithParent> sp = steps.get(i);
				HashMap <String, TestCaseNode> candidate = new HashMap<String, TestCaseNode>();
				// put current steps to the tree
					// and put steps into future root list by combining its dna with its id
				for (StepWithParent swp : sp){
					for (String dna: mapPrev.keySet()){
						if (swp.getDna().equals(dna)){
							TestCaseNode tc = prefixTree.addNode(mapPrev.get(dna), 
									efg.getViewByEvent(swp.getStep().getEventId()), swp.getStep().getEventId());
							candidate.put(swp.getDna() + swp.getStep().getEventId(), tc);
						}
					}
				}
				mapPrev.clear();
				mapPrev = new HashMap <String, TestCaseNode>(candidate);
				prefixTree.moveDownRow();
			}
			
		}
		return prefixTree;
	}
	
	// Build mapping of StepType by their level (or depth)
		// this is used for building prefix tree from chosen testcases
	private HashMap<Integer, ArrayList<StepWithParent>> stepListByLevelFile (HashMap<String, ArrayList<TestCase>> tcs){
		StepType curr = null;
		HashMap<Integer, ArrayList<StepWithParent>> result = new HashMap<Integer, ArrayList<StepWithParent>>();
		for (String event: tcs.keySet()){
			ArrayList<TestCase> test = tcs.get(event);
			for (TestCase t: test){
				ArrayList<StepType> st = (ArrayList<StepType>)t.getStep();
				int level = 0;
				StringBuffer dna = new StringBuffer();
				while (level < st.size()){
					curr = st.get(level);
					
					if (result.get(level) == null){
						result.put(level, new ArrayList<StepWithParent>());
					}
					if (curr != null){
						StepWithParent s = null;
						if (level > 0){
							s = new StepWithParent(st.get(level - 1).getEventId(), dna.toString(), curr);
						} else {
							s = new StepWithParent (null, dna.toString(), curr);
						}
						result.get(level).add(s);
					}
					level += 1;
					dna.append(curr.getEventId());
				}
			}
		}
		return result;
		
	}
	
	public HashMap<String, TestCase> getTestCaseFiles() {
		return testCaseFiles;
	}	
	
	/**
	 * Get test cases mapping {parent id ==> TestCase instance}
	 * @return
	 */
	public HashMap<String, ArrayList<TestCase>> getTestCases() {
		return testCases;
	}
	
	public void setTestCases(HashMap<String, ArrayList<TestCase>> testCases) {
		this.testCases = testCases;
	}
	
}
