/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer.experiment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.kohsuke.args4j.CmdLineException;

import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Main class to run the SMUT replayer
 * 
 * <p>
 * 
 * @author baonn
 * 
 */
@Deprecated
public class JFCReplayerExp {

	JFCReplayerConfigurationExp configuration;

	public JFCReplayerExp(JFCReplayerConfigurationExp configuration) {
		this.configuration = configuration;
	}

	public void execute() throws CmdLineException {
		long nStartTime = System.currentTimeMillis();
		checkArgs();

		// Setup ...

		// ------------------
		// Elapsed time:
		long nEndTime = System.currentTimeMillis();
		long nDuration = nEndTime - nStartTime;
		DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		System.out.println("Time Elapsed: " + df.format(nDuration));

		printInfo();

	}

	/**
     * 
     */
	private void printInfo() {
		System.out
				.println("Testcase: " + JFCReplayerConfigurationExp.TESTCASE);
	}

	/**
	 * 
	 * Check for command-line arguments
	 * 
	 * @throws CmdLineException
	 * 
	 */
	private void checkArgs() throws CmdLineException {
		// Check argument
		if (configuration.HELP) {
			throw new CmdLineException("");
		}

		boolean isPrintUsage = false;

		if (isPrintUsage)
			throw new CmdLineException("");

	}
}
