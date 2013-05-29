/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.state;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.state.State;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;

/**
 * Main class of oracle State analyser.
 */
public class StateESI
{
   public static void
   main(String[] args)
   {
      int retVal = 0;
      CmdLineParser parser = 
         new CmdLineParser(new StateESIConfiguration());

      try {
         parser.parseArgument(args);
         checkMandatoryArgs();

			StateESI stateESI = new StateESI();
			stateESI.execute();

      } catch (CmdLineException e) {
         parser.printUsage(System.err);
         retVal = -1;
      } catch (FileNotFoundException e) {
			retVal = -1;
		}

      System.exit(retVal);
   } // End of main

	private void
	execute()
	throws FileNotFoundException
	{
		int totalFiles = 0, validOracle = 0;

		// Compute diff for events in L1 oracle state files
		State state = new State();
		state.readL1(StateESIConfiguration.L1);

		// Process target testsuite oracles
		File f = new File(StateESIConfiguration.INPUT);
		if (!f.exists()) {
			GUITARLog.log.error("Target tesusuite states not found");
			throw new FileNotFoundException();
		}

		// Read all oracle state file names
		String files[] = f.list();
		int diffFound = 0;

		for (String file : files) {
			boolean matchStatus = false;
			totalFiles++;

			// Read oracle state file
			TestCase tcOracle
				= (TestCase) (IO.readObjFromFile(
						StateESIConfiguration.INPUT +
						File.separator + file, TestCase.class));

			// Oracle may be incomplete or testcase may not have completed
			if (tcOracle.getStep().size() == 0) {
				GUITARLog.log.info("Skipping " + file);
				continue;
			}
			validOracle++;
			// Find the two states of interest from the TestCase state
			List<Integer> statesToCompare = determineStatesToCompare(tcOracle);

			int beforeState = statesToCompare.get(0);
			int afterState = statesToCompare.get(1);

			// Compute diff

			GUIStructure beforeStruct
				= (beforeState < 0) ? tcOracle.getGUIStructure()
					: tcOracle.getStep().get(beforeState).getGUIStructure();
			assert(afterState >= 0);
			GUIStructure afterStruct
				= tcOracle.getStep().get(afterState).getGUIStructure();
			List<Object> diffList
				= (new GUIStructureWrapper(beforeStruct)).
					compare(afterStruct);

			// Extract (boolean, GUIStructure) from returned List<Object>
			GUIStructure diffStruct = (GUIStructure)diffList.get(1);

			// Compare with each "event diff" for events in the oracle state
			GUIStructure runningDiff = diffStruct;
			GUIStructure prevRunningDiff = null;

			int iter = 0;
         IO.writeObjToFile(runningDiff, "/tmp/" + iter++ + "." + file);

			List<StepType> steps = tcOracle.getStep();
			boolean error = false;
			for (StepType step : steps) {
				List<Object> runningDiffList;
				String eventID = step.getEventId();
				GUIStructure eventDiff = state.getL1Diff(eventID);

				GUITARLog.log.info("Reading " + file);

				if (eventDiff == null) {
					GUITARLog.log.error("Event diff not found for " + eventID);
					error = true;
					break;
				}
	
				// Compre current event diff with previous event diff
				GUIStructure runningEventDiff =
					(prevRunningDiff == null) ? eventDiff :
					(GUIStructure)
					(new GUIStructureWrapper(eventDiff)).compare(prevRunningDiff).get(1);
				prevRunningDiff = runningEventDiff;
	
				runningDiffList
					= (new GUIStructureWrapper(runningDiff)).
					compare(runningEventDiff);

				// Extract (boolean, GUIStructure) from returned List<Object>
				matchStatus = (Boolean)runningDiffList.get(0);
				runningDiff = (GUIStructure)runningDiffList.get(1);
	         IO.writeObjToFile(runningDiff, "/tmp/" + iter++ + "." + file);
			} // for

			// Continue with next on error
			if (error) {
				continue;
			}

         // Write diff log if mismatch (mismatch=true)
			if (matchStatus) {
            diffFound++;
				
				GUITARLog.log.info("Done " + file +
					" [" + (beforeState + 1) + "-> " + afterState + "]");
            IO.writeObjToFile(runningDiff, "/tmp/" + file);
			}
      }

		GUITARLog.log.info("Total oracles " + totalFiles);
		GUITARLog.log.info("Valid oracles " + validOracle);
		GUITARLog.log.info("Diff found "    + diffFound);
	} // End of function


	/**
	 * Determin which two events to compare. At this time, 
	 * the initial state (-1) and the last event in the oracle are
	 * compared.
	 */
	private List<Integer>
	determineStatesToCompare(TestCase tcOracle)
	{
		List<StepType> steps = tcOracle.getStep();

		// -1 indicates initial state
		int beforeState = -1;
		// Detect beforeState to compare with
		boolean prevReaching = true, curReaching;
		for (StepType step : steps) {
			curReaching = step.isReachingStep();
			if (prevReaching = true && curReaching == false) {
				break;
			}
			// Increment both
			beforeState++;
			prevReaching = curReaching;
		}
	
		List<Integer> list = new ArrayList<Integer>();
		int afterState = steps.size() - 1;
		assert(beforeState < afterState);
		list.add(beforeState);
		list.add(afterState);
		return list;
	}

   private static void
   checkMandatoryArgs()
	throws CmdLineException
   {
      if (StateESIConfiguration.L1 == null) {
         System.err.println("missing -l1 argument");
         throw new CmdLineException("");
      }
   }

} // End of class
