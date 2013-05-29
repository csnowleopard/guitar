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
package edu.umd.cs.guitar.graph.converter.gexf;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class EFG2GexfConverterGexf4jSimpleInputTest {

	private String testDataDir = "tests-data/simple";

	
	private String[] args = { "", "-e", testDataDir + File.separator + "CS-0.EFG",
			 "-e", testDataDir+ File.separator + "CS-1.EFG" ,
			"-m", testDataDir + File.separator + "CS.MAP",
			"-f",
			testDataDir + File.separator + "CS.GEXF", };
	

//	String[] args = { "", "-e", testDataDir + File.separator + "sample-0.EFG",
//			"-e", testDataDir+ File.separator + "sample-1.EFG" ,
//			"-m", testDataDir + File.separator + "sample.MAP", "-f",
//			testDataDir + File.separator + "sample.GEXF", };


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

	@Test
	public void testMain() {
		try {
			System.out.println("Test main");
			EFG2GexfConverter4jMain.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
