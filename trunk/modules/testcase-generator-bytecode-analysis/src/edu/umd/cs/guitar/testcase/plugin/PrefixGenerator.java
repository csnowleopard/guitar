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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.plugin.BytecodeAnalysis.Output;
import edu.umd.cs.guitar.testcase.plugin.edg.EventNode;
import edu.umd.cs.guitar.testcase.plugin.edg.Field;

/**
 * PrefixGenerator
 *
 */
public class PrefixGenerator implements TCGeneratorMethod {
	
	/**
	 * EDG
	 */
	private List<EventNode> edg;
	
	/**
	 * Output
	 */
	private Output out;
	
	/**
	 * Max test cases
	 */
	private int maxTC;
	
	/**
	 * Count of test cases
	 */
	private int countTC;
	
	@Override
	public void generate(List<EventNode> edg,
						 List<EventType> initialEvents,
						 Hashtable<EventType, Vector<EventType>> preds,
						 Hashtable<EventType, Vector<EventType>> succs,
						 Output out,
						 int maxTC,
						 int lengthTC) {
		
		if (lengthTC <= 0)
			return; // to short to generate a prefix
		
		this.edg = edg;
		this.countTC = 0;
		this.maxTC = maxTC;
		this.out = out;
		
		for (EventNode n : edg) {
			try {
				generatePrefix(n, lengthTC);
			} catch (MaxTCReachedException e) {
				break;
			}
		}
	}
	
	/**
	 * Generates a prefix
	 * @param n Node
	 * @param prefixLength Prefix length
	 * @throws MaxTCReachedException
	 */
	private void generatePrefix(EventNode n, int prefixLength) throws MaxTCReachedException {
		LinkedList<EventType> seq = new LinkedList<EventType>();
		seq.push(n.getEvent());
		prefixHelper(seq, n.getReads(), prefixLength);
	}
	
	/**
	 * Prefix helper
	 * @param seq Sequence
	 * @param reads Reads
	 * @param prefixLength Prefix length
	 * @throws MaxTCReachedException
	 */
	private void prefixHelper(LinkedList<EventType> seq, Set<Field> reads, int prefixLength) throws MaxTCReachedException {
		int newLength = prefixLength - 1;
		for (EventNode n : edg) {
			if (!TCUtil.isTerminalEvent(n.getEvent()) && n.writesTo(reads)) {
				seq.push(n.getEvent());
				if (newLength <= 0) {
					// write the testcase
					if (out.createSequenceTC(seq)) {
						// if testcase was created test if maxTC is reached
						countTC++;
						if (!(maxTC <= 0) && countTC >= maxTC)
							throw new MaxTCReachedException();
					}
				} else {
					// extend the sequence
					// maintain unfulfilled reads
					HashSet<Field> newReads = new HashSet<Field>(reads);
					newReads.removeAll(n.getWrites());
					newReads.addAll(n.getReads());
					prefixHelper(seq, newReads, newLength);
				}
				seq.pop();
			}
		}
	}
	
}
