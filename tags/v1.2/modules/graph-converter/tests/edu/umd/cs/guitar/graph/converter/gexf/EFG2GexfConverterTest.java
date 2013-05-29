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
public class EFG2GexfConverterTest {

	String inputDir = "/media/Data/Ore_no_documents/Research/AUT/ArgoUML/visualization/data-skoll";
	String outputDir = "/media/Data/Ore_no_documents/Research/AUT/ArgoUML/visualization/data-skoll";

	String[] args = { "", "-e", inputDir + File.separator + "AU-0.EFG", "-e",
			inputDir + File.separator + "AU-1.EFG", "-e",
			inputDir + File.separator + "AU-2.EFG", "-e",
			inputDir + File.separator + "AU-3.EFG", "-e",
			inputDir + File.separator + "AU-4.EFG", "-m", 

			inputDir + File.separator + "AU.MAP", "-f",
			outputDir + File.separator + "AU-smooth.GEXF"};

	// String inputDir =
	// "/media/Data/Ore_no_documents/Research/AUT/ArgoUML/visualization/data/efg";
	// String outputDir =
	// "/media/Data/Ore_no_documents/Research/AUT/ArgoUML/visualization/data";
	//
	// String[] args = { "", "-e", inputDir + File.separator + "AU-0.EFG",
	// "-e", inputDir + File.separator + "AU-1.EFG", "-e",
	// inputDir + File.separator + "AU-2.EFG", "-e",
	// inputDir + File.separator + "AU-3.EFG", "-e",
	// inputDir + File.separator + "AU-4.EFG", "-m",
	// inputDir + File.separator + "AU.MAP", "-f",
	// outputDir + File.separator + "AU-smooth.GEXF", "-s"};

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
			System.out.println("Test main");
			EFG2GexfConverterMain.main(args);
	}
}
