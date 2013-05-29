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
package edu.umd.cs.guitar.ripper;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.umd.cs.guitar.event.WebOpenNewInternalLink;
import edu.umd.cs.guitar.event.WebOpenLink;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class WebRipperMainTest {

	/**
	 * 
	 */
	private static final String URL = "http://www.google.com/analytics";
	// private static final String URL =
	// "https://www.google.com/analytics/web//";
	String RESOUCE_DIR = "resources-tests/data";
	String DOMAIN = "edu.umd.cs.guitar.ripper";
	String PLUGIN = "WebPluginInfo";

	String CONFIG_FILE = RESOUCE_DIR + "/" + "configuration.xml";
	String GUI_FILE = RESOUCE_DIR + "/" + "Google-Analytics.GUI";
	String PROFILE = "/home/baonn/.mozilla/firefox/profiles.ini";

	String[] args = { DOMAIN + "." + PLUGIN, "-u", URL, "-g", GUI_FILE,
	// "-cf", CONFIG_FILE
	};

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// @Test
	// @Ignore
	public void testWebRipperMain() throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException, Exception {
		System.out.println("testWebRipperMain");
		Launcher.main(args);
		System.out.println("DONE");
	}

	@Test
//	@Ignore
	public void testWebRipperMainSimpleDomain() throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException, Exception {
		System.out.println("testWebRipperMainNoExpand");
		WebOpenNewInternalLink
				.setDomainPattern(
						"(" +
						"http://www.google.com/analytics" +
						"|http://www.google.com/analytics/features/index.html" +
						")");
		Launcher.main(args);
		System.out.println("DONE");
	}

	@Test
	@Ignore
	public void testWebRipperFullDomain() throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException, Exception {
		System.out.println("testWebRipperMainNoExpand");
		WebOpenNewInternalLink
				.setDomainPattern(
						"http://www.google.com/analytics.*" 
								);
		Launcher.main(args);
		System.out.println("DONE");
	}

}
