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
package edu.umd.cs.guitar.graph.converter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.RowType;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class EFG2Graphviz {

	private static String OUT_DOT_FILE;
	private static String IN_EFG_FILE;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage:" + EFG2Graphviz.class.getName()
					+ "<efg file>  <dot file> ");
			System.exit(1);
		}

		IN_EFG_FILE = args[0];
		OUT_DOT_FILE = args[1];

		EFG efg = (EFG) IO.readObjFromFile(IN_EFG_FILE, EFG.class);

		StringBuffer result = toGraphviz(efg);
		
		try {
			// Create file
			FileWriter fstream = new FileWriter(OUT_DOT_FILE);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(result.toString());
			// Close the output stream
			out.close();
			System.out.println("DONE");
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public static StringBuffer toGraphviz(EFG efg) {
		StringBuffer result = new StringBuffer();

		result.append("digraph " + "EFG" + " {" + "\n");

		// Set up node
		result.append("\t/* Nodes */\n");
		for (EventType event : efg.getEvents().getEvent()) {
			result.append("\t");

			// Set up label
			result.append(event.getEventId());
			result.append("[");
			result.append(" label=\"");
			result.append(event.getEventId());
			result.append("\"");

			// set up initial state
			if (event.isInitial()) {
				result.append(" style=filled ");
			}
			result.append(" ]");
			result.append(";\n");
		}
		result.append("\n");
		result.append("\t/* Edges */\n");

		List<EventType> lEvents = efg.getEvents().getEvent();
		List<RowType> lRows = efg.getEventGraph().getRow();

		for (int row = 0; row < lRows.size(); row++) {
			List<Integer> lE = lRows.get(row).getE();
			for (int col = 0; col < lE.size(); col++) {
				if (lE.get(col) > 0) {

					String source = lEvents.get(row).getEventId();
					String target = lEvents.get(col).getEventId();
					result.append("\t");
					result.append(source + "->" + target);
					result.append(";\n");
				}
			}
		}
		//		
		// for (TransitionType trans : em.getTransitionSet().getTransition()) {
		// result.append("\t");
		//
		// String source = trans.getSource();
		// String target = trans.getTarget();
		// String label = "";
		//
		// result.append(source + "->" + target);
		// result.append(" [");
		// result.append(" label=\"<");
		//
		// List<String> lEvents = trans.getEventSequence().getEventId();
		// Iterator<String> iterator = lEvents.iterator();
		//
		// String event;
		// if (iterator.hasNext()) {
		// event = iterator.next();
		// label += event;
		// }
		// while (iterator.hasNext()) {
		// label += (", " + iterator.next());
		// }
		//
		// result.append(label);
		// result.append(">\"");
		// result.append(" ]");
		// result.append(";\n");
		// }

		result.append("}");
		return result;
	}
}
