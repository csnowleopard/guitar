/*	
 *  Copyright (c) 2011. The GREYBOX group at the University of Freiburg, Chair of Software Engineering.
 *  Names of owners of this group may be obtained by sending an e-mail to arlt@informatik.uni-freiburg.de
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package edu.umd.cs.guitar.testcase.plugin;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.plugin.BytecodeAnalysis.Output;
import edu.umd.cs.guitar.testcase.plugin.edg.EventNode;

/**
 * RandomGenerator
 *
 */
public class RandomGenerator implements TCGeneratorMethod {
	
	/**
	 * Random
	 */
	private Random random = new Random();
	
	@Override
	public void generate(List<EventNode> edg,
						 List<EventType> initialEvents,
						 Hashtable<EventType, Vector<EventType>> preds,
						 Hashtable<EventType, Vector<EventType>> succs,
						 Output out,
						 int maxTC,
						 int lengthTC) {
		
		for ( int i = 0; i < maxTC; i++ ) {
			// create empty test case
			LinkedList<EventType> tc = new LinkedList<EventType>();
			
			int j = 0;
			while ( j < lengthTC ) {
				// select random event
				int k = random.nextInt(edg.size());
				EventNode e = edg.get(k);
				
				// empty event or terminal event?
				if ( e.isEmpty() || TCUtil.isTerminalEvent(e.getEvent()) )
					continue;
				
				// add event to test case
				tc.add(e.getEvent());
				j++;
			}
			
			// write test case
			out.createSequenceTC(tc);
		}
	}
	
}
