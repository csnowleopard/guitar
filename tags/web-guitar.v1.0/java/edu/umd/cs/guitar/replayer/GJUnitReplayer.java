/*
 *   Copyright (c) 2009-@year@. The GUITAR group at the University of
 *   Maryland.  Names of owners of this group may be obtained by sending an
 *   e-mail to atif@cs.umd.edu
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a
 *   copy of this software and associated  documentation files (the "Software"),
 *   to deal in the Software without restriction,  including without  limitation
 *   the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *   and or sell copies of the Software, and to permit persons to whom the
 *   Software is furnished to do so, subject to the following  conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial  portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT  LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO  EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR  THE USE OR OTHER
 *   DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.replayer;

import edu.umd.cs.guitar.exception.MissingArgumentExeception;
import edu.umd.cs.guitar.model.WebConstants;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * @deprecated no longer use this class for executing test cases 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
@Deprecated
public class GJUnitReplayer {
	/**
	 * Test case file
	 */
	String testcase;
	/**
	 * GUI file
	 */
	String gui;
	/**
	 * EFG file
	 */
	String efg;
	Replayer replayer;

	/**
	 * 
	 */
	public GJUnitReplayer() {
		super();
	}

	/**
	 * @throws MissingArgumentExeception
	 * 
	 */
	protected void setupEnvironmentVariables() {
		GUITARLog.log.debug("Setting up environment...");

		this.gui = System.getProperty(WebConstants.GUITAR_GUIFILE_PROPERTY);
		this.efg = System.getProperty(WebConstants.GUITAR_EFGFILE_PROPERTY);
		this.testcase = System
				.getProperty(WebConstants.GUITAR_TESTCASE_FILE_PROPERTY);

		if (GUITARLog.log.isDebugEnabled()) {
			GUITARLog.log.debug("GUI file:" + this.gui);
			GUITARLog.log.debug("EFG file:" + this.efg);
			GUITARLog.log.debug("Test case file:" + this.testcase);
		}

		GUITARLog.log.debug("DONE...");
	}

}