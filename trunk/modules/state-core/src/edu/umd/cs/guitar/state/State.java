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
import java.util.HashMap;
import java.util.List;

import java.io.FileNotFoundException;

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
public class State
{
	/**
	 * Stores state difference obtained by diff-ing oracle states for
	 * L1 testcases. The diff is for the first (and only) event and the
	 * previous "reaching" event.
	 */
	HashMap l1DiffHash = new HashMap();

	// Empty contstructor
	public void
	State()
	{
	}

	/**
	 * Read directory full of L1 testcase and compute the difference
	 * between the first event and the previous reaching event.
	 * Store it in 'l1DiffHash' hash with event ID as the key.
	 */
	public void
	readL1(String strDirname)
	throws FileNotFoundException
	{
		int totalFiles = 0, eventFound = 0, diffFound = 0;
      // Read L1 oracles directory
      File l1Path = new File(strDirname);
      if (!l1Path.exists()) {
         GUITARLog.log.error("Length-1 testcase oracle directory not found");
         throw new FileNotFoundException();
      }

		// Read directory for all oracle file names
		String filesL1[] = l1Path.list();

      // Parse each L1 oracle from file
      for (String file : filesL1) {
			totalFiles++;

			// Read file
			GUITARLog.log.info("Reading " + file);
			TestCase tcOracle
               = (TestCase) (IO.readObjFromFile(
                     l1Path + File.separator + file, TestCase.class));

			// Iterate through steps looking for first non-"reaching" event
         List<StepType> steps = tcOracle.getStep();
         boolean prevReaching = true, curReaching;
         StepType beforeStep = null, afterStep = null;
         for (StepType step : steps) {
				curReaching = step.isReachingStep();
            if (prevReaching == true && curReaching == false) {
               afterStep = step;
               break;
            }
            beforeStep = step;
				prevReaching = curReaching;
         }

         // Check if no non-"reaching" step found then skip this state file
         if (afterStep == null) {
            continue;
         }

			// Found an event
			eventFound++;

			// If no "reaching" step, then initial state if the "before" state
         GUIStructure beforeGUIStruct = (beforeStep == null) ?
                                          tcOracle.getGUIStructure() :
                                          beforeStep.getGUIStructure();
         GUIStructure afterGUIStruct = afterStep.getGUIStructure();

			// Compute diff
			List<Object> diffList
				= (new GUIStructureWrapper(beforeGUIStruct)).
					compare(afterGUIStruct);

			// Extract (boolean, GUIStructure) from returned List<Object>
			boolean matchStatus = (Boolean)diffList.get(0);
			GUIStructure diffGUIStruct = (GUIStructure)diffList.get(1);

			// Store in 'l1DiffHash' hash, even if null diff
			l1DiffHash.put(afterStep.getEventId(), diffGUIStruct);
			if (matchStatus) {
				GUITARLog.log.info("Found diff for event ID"
										+ afterStep.getEventId());
				diffFound++;

				// Debug
				IO.writeObjToFile(diffGUIStruct, "/tmp/" + afterStep.getEventId());
         }
      }

		GUITARLog.log.info("Total oracle files   : " + totalFiles);
		GUITARLog.log.info("Valid L1 event found : " + eventFound);
		GUITARLog.log.info("Non-null diff        : " + diffFound);

	} // Enf of function

	/**
	 * Look up the L1 diff hash map for a "diff" corresponding to an event
	 * specified by 'eventId'.
	 */
	public GUIStructure
	getL1Diff(String eventID)
	{
			return (GUIStructure)l1DiffHash.get(eventID);
	}

}
