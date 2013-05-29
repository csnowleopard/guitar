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
 * Main class of oracle State comparator.
 *
 * Compares two directory full of oracle states
 *
 * OR
 *
 * Compares two specific oracle states
 */
public class StateCmp
{
   public static void
   main(String[] args)
   {
      int retVal = 0;
      CmdLineParser parser = 
         new CmdLineParser(new StateCmpConfiguration());

      try {
         parser.parseArgument(args);
         checkMandatoryArgs();

			StateCmp stateCmp = new StateCmp();
			stateCmp.execute();

      } catch (CmdLineException e) {
			GUITARLog.log.error(e.getMessage());
         parser.printUsage(System.err);
         retVal = -1;
      } catch (FileNotFoundException e) {
			GUITARLog.log.error(e.getMessage());
			retVal = -1;
		}

      System.exit(retVal);
   } // End of main


	private void
	execute()
	throws CmdLineException, FileNotFoundException
	{
		File fSrc = new File(StateCmpConfiguration.SRC);
		File fDst = new File(StateCmpConfiguration.DST);

		// Compare directories
		if (!fSrc.exists() || !fDst.exists()) {
			throw new FileNotFoundException("source ot destination not found");
		}

		if (fSrc.isDirectory() && fDst.isDirectory()) {
			compareDir(StateCmpConfiguration.SRC, StateCmpConfiguration.DST);
			return;
		}

		// Compare files
		if (fSrc.isFile() && fDst.isFile()) {
			compareFile(StateCmpConfiguration.SRC, StateCmpConfiguration.DST);
			return;
		}

		throw new CmdLineException("compare two files or two directories");
	} // End of function


	private void
	compareFile(String srcFile,
					String dstFile)
	{
		Boolean matchStatus = compare(srcFile, dstFile);
		GUITARLog.log.info("");
		if (matchStatus) {
			// true indicates mismatch
			GUITARLog.log.info("Mismatch");
		} else {
			GUITARLog.log.info("Match");
		}
	}


	private void
	compareDir(String srcDir,
			     String dstDir)
	throws FileNotFoundException
	{
		int i, j, jCur = 0;

		// Read source and destination testsuites
		File fSrc = new File(srcDir);
		if (!fSrc.isDirectory()) {
			throw new FileNotFoundException("Source testsuite not a directory");
		}
		File fDst = new File(dstDir);
		if (!fDst.isDirectory()) {
			throw new FileNotFoundException("Destination testsuite not a directory");
		}

		// Read all oracle state file names
		String filesSrc[] = fSrc.list();
		String filesDst[] = fDst.list();

		int commonFiles = 0, diffFound = 0, srcNewStates = 0, dstNewStates = 0;

		/*
		 * Both file lists are sorted. Iterate over the "src" looking
		 * for matching "dst" names.
		 */
		for (i = 0; i < filesSrc.length ; i++) {
			boolean found = false;

			// Iterate from the next "dst" location till end of "dst" list
			for (j = jCur; j < filesDst.length; ) {
				// If name mismatch, check next "dst"
				if (!filesSrc[i].equals(filesDst[j])) {
					j++;
					continue;
				} else {
					j++;
					// Located corresponding file in "dst"
					found = true;
					break;
				}
			}

			if (found) {
				// A file in src found a matching file in dst
				commonFiles++;

				// Compare the two states
				GUITARLog.log.info(filesSrc[i] + " " + filesDst[j-1]);
				Boolean matchStatus =
					compare(StateCmpConfiguration.SRC + File.separator + filesSrc[i],
					  StateCmpConfiguration.DST + File.separator + filesDst[j-1]);

				// true indicates mismatch
				diffFound += (matchStatus) ? 1 : 0;

				// Mark the next starting point
				dstNewStates += (j -1 - jCur);
				jCur = j;
			} else {
				// File in src could not be matched with a file in dst
				srcNewStates++;
			}
		}

		// Count remainign unmatches 'dst" files (if any)
		dstNewStates += (filesDst.length - jCur);

		GUITARLog.log.info("");
		GUITARLog.log.info("Source files         : " + filesSrc.length);
		GUITARLog.log.info("Destination files    : " + filesDst.length);
		GUITARLog.log.info("Common files         : " + commonFiles);
		GUITARLog.log.info("Unmatched states from source     : " + srcNewStates);
		GUITARLog.log.info("Unmatched states from destination: " + dstNewStates);
		GUITARLog.log.info("State mismatch                   : " + diffFound);
	
	} // End of function

	/**
	 * Compare the oracle states at the two specified file paths.
	 *
	 * @return true on mismatch, false on match
	 */
	private boolean
	compare(String strSrcFilepath,
			  String strDstFilepath)
	{
		int i;
		boolean matchStatus = false;

		// Read src state state file
		TestCase srcState
			= (TestCase) (IO.readObjFromFile(
					strSrcFilepath, TestCase.class));
		// Read dst state state file
		TestCase dstState
			= (TestCase) (IO.readObjFromFile(
					strDstFilepath, TestCase.class));

		// Oracle may be incomplete or testcase may not have completed
		if (srcState.getStep().size() == 0 ||
			 dstState.getStep().size() == 0) {
			GUITARLog.log.info("Skipping: [" + strSrcFilepath + ", " +
									strDstFilepath + "]" );
		}

		List<StepType> srcSteps = srcState.getStep();
		List<StepType> dstSteps = dstState.getStep();
		if (srcSteps.size() != dstSteps.size()) {
			GUITARLog.log.info("Source and destination state size mismatch");
			return true;
		}

		for (i = 0; i < srcSteps.size() ; i++) {
			List<Object> diffList
				= (new GUIStructureWrapper(srcSteps.get(i).getGUIStructure())).
				compare(dstSteps.get(i).getGUIStructure());

			// Extract (boolean, GUIStructure) from returned List<Object>
			matchStatus = (Boolean)diffList.get(0);

			// true indicates mismatch
			if (matchStatus) {
				break;
			}
		} // for

		return matchStatus;
	} // End of function


   private static void
   checkMandatoryArgs()
	throws CmdLineException
   {
      if (StateCmpConfiguration.SRC == null) {
         throw new CmdLineException("missing -s argument");
      }
      if (StateCmpConfiguration.DST == null) {
         throw new CmdLineException("missing -d argument");
      }
   }

} // End of class
