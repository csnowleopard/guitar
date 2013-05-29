package utils.guitar.testCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataModels.guitar.testCase.TestCase;
import dataModels.guitar.testCase.TestCaseStep;
import dataModels.visualizer.VisualizationData;


/**
 * This class parses the GUI file and enters the extracted information into the VisualizationData data structure
 * @author Chris Carmel
 *
 */
public class TSTReader {

	/**
	 * Regular expression use to compare against the TestCase tag.
	 */
	static Pattern testCaseTag = Pattern.compile("^<TestCase>$");

	/**
	 * Regular expression use to compare against the Step tag.
	 */
	static Pattern stepTag = Pattern.compile("^<Step>$");

	/**
	 * Regular expression use to compare against the EventID tag.
	 */
	static Pattern eventIDTag = Pattern.compile("^<EventID>(.+)</EventID>$");
	static Pattern eventIdTag = Pattern.compile("^<EventId>(.+)</EventId>$");

	/**
	 * Regular expression use to compare against the ReachingStep tag.
	 */
	static Pattern reachingStepTag = Pattern.compile("^<ReachingStep>(.+)</ReachingStep>$");

	/**
	 * Regular expression use to compare against the closing Step tag.
	 */
	static Pattern closingStepTag = Pattern.compile("^</Step>$");

	/**
	 * Regular expression use to compare against the closing TestCase tag.
	 */
	static Pattern closingTestCaseTag = Pattern.compile("^</TestCase>$");

	/**
	 * Regular expression use to compare against the file path to get the file title.
	 */
	static Pattern fileNameCatcher = Pattern.compile("(.*/)*(.*)");

	/**
	 * FilenameFilter that filters out hidden files.
	 */
	final static FilenameFilter hiddenFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return !name.startsWith(".");
		}
	};

	/**
	 * Processes a given TST file to create a TestCase object.
	 * 
	 * @param filePath		path at which the TST file is located
	 * @param vd			VisualizationData object to populate
	 * 
	 * @return				the newly populated VisualizationData object
	 * 				
	 * @throws IOException
	 */
	public static VisualizationData processTST(String filePath, VisualizationData vd) throws IOException {
		Matcher m  = fileNameCatcher.matcher(filePath);
		m.matches();

		String fileName = m.group(2);

		FileReader fr = new FileReader(filePath); // open GUI
		BufferedReader br = new BufferedReader(fr);
		String lineIn;
		while((lineIn = br.readLine()) != null) {
			lineIn = lineIn.trim();
			if (testCaseTag.matcher(lineIn).matches()) {
				TestCase currTestCase = new TestCase(fileName);
				while ((lineIn = br.readLine()) != null) {
					lineIn = lineIn.trim();
					int stepNumber = 0;
					if (stepTag.matcher(lineIn).matches()) {
						TestCaseStep currStep = new TestCaseStep(currTestCase, stepNumber);
						while ((lineIn = br.readLine()) != null) {
							lineIn = lineIn.trim();
							if ((m = eventIDTag.matcher(lineIn)).matches() ||
									(m = eventIdTag.matcher(lineIn)).matches()) {
								currStep.setEventID(m.group(1));
							} else if ((m = reachingStepTag.matcher(lineIn)).matches()) {
								if (m.group(1).equals("true")) {
									currStep.setReachingStep(true);
								} else if (m.group(1).equals("false")) {
									currStep.setReachingStep(false);
								}
							} else if (closingStepTag.matcher(lineIn).matches()) {
								stepNumber++;
								break;
							}
						}
						currTestCase.getSteps().add(currStep);
					} else if (closingTestCaseTag.matcher(lineIn).matches()) {
						break;
					}
				}
				currTestCase.processValidity(vd);
				vd.getTestCases().add(currTestCase);
			}
		}
		fr.close(); // close TST

		return vd;
	}

	/**
	 * Processes the TST files in a given directory.
	 * 
	 * @param userTestCasesDir		directory to look for TST files in
	 * @param vd					VisualizationData object to be populated
	 * 
	 * @throws IOException
	 */
	public static void processTSTDirectory(String userTestCasesDir, VisualizationData vd) throws IOException {
		File testCasesDirectory = new File(userTestCasesDir);
		String[] fileNames = testCasesDirectory.list(hiddenFileFilter);

		if (fileNames != null) {
			for (String currFileName : fileNames) {
				processTST(userTestCasesDir + currFileName, vd);
			}
		}
	}
}
