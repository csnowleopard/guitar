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
package edu.umd.cs.guitar.testcase.plugin;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.ParameterListType;
import edu.umd.cs.guitar.model.data.StepType;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class SequenceLengthCoverageExperimentTest {
	protected String RESOUCE_DIR = "resources-tests/data";

	protected String EFG_FILE = RESOUCE_DIR + File.separator + "Sample.EFG";
	// protected String EFG_FILE = RESOUCE_DIR + File.separator + "Simple.EFG";

	protected String TESTCASE_DIR = RESOUCE_DIR + File.separator + "testcases";
	protected String LENGTH = "2";
	protected String MAX_NO_OF_TESTCASES = "20";

	String TESTCASE_GENERATOR = "edu.umd.cs.guitar.testcase.TestCaseGenerator";

	Map<String, String> defaultArgMap = new HashMap<String, String>();

	/**
	 * Get arguments from a map
	 * 
	 * @param argsMap
	 * @return
	 */
	protected String[] getArgs(Map<String, String> argsMap) {
		List<String> args = new ArrayList<String>();
		
		int i = 0;
		for (String flag : argsMap.keySet()) {
			args.add(flag);
			String value = argsMap.get(flag);
			if (!(value==null) && !("".equals(value))) {
				args.add(value);
			}
		}
		return args.toArray(new String[args.size()]);
	}

	@Test
	// @Ignore
	public void testSequenceLengthCoverageExt() {
		defaultArgMap.put("-p", "SequenceLengthCoverageWebExt");
		defaultArgMap.put("-e", EFG_FILE);
		defaultArgMap.put("-d", TESTCASE_DIR);
		defaultArgMap.put("-l", LENGTH);
		defaultArgMap.put("-m", MAX_NO_OF_TESTCASES);
		defaultArgMap.put("-T", null);

		edu.umd.cs.guitar.testcase.TestCaseGenerator
				.main(getArgs(defaultArgMap));
		System.out.println("DONE");
	}

	ObjectFactory factory = new ObjectFactory();

	// @Test
	public void testpopulateEventParameter() {
		EventType e = factory.createEventType();
		e.setEventId("1");

		ParameterListType p1 = new ParameterListType();
		ParameterListType p2 = new ParameterListType();
		e.getParameterList().add(p1);
		e.getParameterList().add(p2);

		// Map<String, StepType> map =
	}
}
