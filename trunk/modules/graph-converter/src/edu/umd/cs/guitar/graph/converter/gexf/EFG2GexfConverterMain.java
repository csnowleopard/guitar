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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIMap;

/**
 * The main class to convert EFG to Gexf format for graph visualization and
 * analysis
 * 
 * @see <a href="http://gexf.net/format/">http://gexf.net/format/</a> <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class EFG2GexfConverterMain {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EFG2GexfConverterConfig configuration = new EFG2GexfConverterConfig();
		CmdLineParser parser = new CmdLineParser(configuration);
		try {
			parser.parseArgument(args);
			System.out.println("Reading data....");

			GUIMap map = null;
			if (configuration.mapFile != null)
				map = (GUIMap) IO.readObjFromFile(configuration.mapFile,
						GUIMap.class);

			List<EFG> efgList = new ArrayList<EFG>();

			for (String efgFile : configuration.efgFileList) {
				EFG efg = (EFG) IO.readObjFromFile(efgFile, EFG.class);
				efgList.add(efg);
			}

			File outFile = new File(configuration.gexfFile);
			OutputStream fos = new FileOutputStream(outFile);

			EFG2GexfConverter converter;
			if (map != null)
				converter = new EFG2GexfConverter(map, efgList);
			else
				converter = new EFG2GexfConverter(efgList);
			
			converter.setSimpleMode(configuration.isSimpleMode);
			converter.setNoEdge(configuration.isNoEdge);
			converter.setNoTitle(configuration.isNoTitle );

			System.out.println("Converting....");
			converter.convert();
			System.out.println("Saving....");

			File outputFile = new File(configuration.gexfFile);
			try {
				converter.exportFile(outputFile);
				// converter.exportWriter(new StringWriter());
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Output file: " + configuration.gexfFile);

			System.out.println("DONE");

		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println("Usage: java [JVM options] "
					+ EFG2GexfConverterMain.class.getName() + " [Options] \n");

			System.err.println("where [Options] include:");
			System.err.println();

			parser.printUsage(System.err);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
