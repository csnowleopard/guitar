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

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import edu.umd.cs.guitar.replayer2.WebReplayerMonitor2;

import edu.umd.cs.guitar.exception.ComponentNotFound;
import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.exception.MissingArgumentExeception;
import edu.umd.cs.guitar.model.WebConstants;
import edu.umd.cs.guitar.replayer.monitor.PauseMonitor;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Base class for running GUITAR's test cases using JUnit
 * 
 * <p>
 * 
 * Need to supply following enviroment variable as parameters:
 * 
 * <ul>
 * <li>-Dguitar.gui=<GUI file></li>
 * <li>-Dguitar.efg=<EFG file></li>
 * <li>-Dguitar.testcase=<Test case file></li>
 * </ul>
 * 
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
@Deprecated
public class WebJUnitReplayer extends GJUnitReplayer {

	/**
	 * root URL
	 */
	String url;

	WebDriver driver;

	/**
	 * Setup the executor
	 */
	public void setUp() {

	}

	/**
	 * Clean up the executor
	 */
	public void cleanUp() {

	}

	/**
	 * The main method to run the replayer
	 * 
	 * @throws Exception
	 * @throws GException
	 */
	@Test
	final public void execute() throws GException, Exception {
		setupEnvironmentVariables();
		if (this.gui == null || this.efg == null || this.testcase == null
				|| this.url == null)
			throw new MissingArgumentExeception();

		replayer = new Replayer(this.testcase, this.gui, this.efg);

		this.driver = new FirefoxDriver();
		GReplayerMonitor monitor = new WebReplayerMonitor();
		replayer.setMonitor(monitor);
		
		// setup the replayer
		setUp();

		// Execute test case
		try {
			replayer.execute();
		} catch (org.openqa.selenium.StaleElementReferenceException e) {
		  GUITARLog.log.error(ComponentNotFound.class.getName());
//			throw new ComponentNotFound();
		}

		// cleanup the replayer
		cleanUp();

	}

	/**
	 * 
	 */
	protected void setupEnvironmentVariables() {
		super.setupEnvironmentVariables();
		this.url = System.getProperty(WebConstants.GUITAR_WEB_URL_PROPERTY);

	}
}
