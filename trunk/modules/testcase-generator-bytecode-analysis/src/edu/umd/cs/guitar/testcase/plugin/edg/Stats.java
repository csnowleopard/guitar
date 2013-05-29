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

package edu.umd.cs.guitar.testcase.plugin.edg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Stats {

	/**
	 * EDG
	 */
	private List<EventNode> edg;

	/**
	 * C-tor
	 * @param edg EDG
	 */
	public Stats(List<EventNode> edg) {
		this.edg = edg;
	}

	/**
	 * Counts the number of empty events
	 */
	public void countEmptyEvents() {
		int count = 0;
		for ( EventNode e : edg ) {
			if ( e.isEmpty() ) {
				count++;
			}
		}
		
		print("# empty events: " + count);
		print("# non-empty events: " + (edg.size() - count));
	}

	/**
	 * Counts the number of invokes
	 */
	public void countInvokes() {
		for ( EventNode event : edg ) {
			if ( null == event.getMethods() ) {
				continue;
			}
			
			int count = 0;
			for ( MethodDescriptor method : event.getMethods() ) {
				Set<MethodDescriptor> visited = new HashSet<MethodDescriptor>();
				count += countInvokes(method, visited);
			}
			
			print(count);
		}
	}

	/**
	 * Counts the number of invokes of a given method
	 * @param method Method
	 * @param visited Visited methods
	 * @return Number of invokes of a given method
	 */
	protected int countInvokes(MethodDescriptor method, Set<MethodDescriptor> visited) {
		if ( null == method.getInvokes() )
			return 0;
		
		int count = 0;
		for ( MethodDescriptor i : method.getInvokes() ) {
			if ( !visited.contains(i) ) {
				visited.add(i);
				count += 1 + countInvokes(i, visited);
			}
		}
		return count;
	}

	/**
	 * Counts the number of condition reads
	 */
	public void countConditionReads() {
		int reads = 0;
		int conditionReads = 0;
		
		for ( EventNode event : edg ) {
			if ( null == event.getMethods() ) {
				continue;
			}
			
			Set<Field> cReads = new HashSet<Field>();
			for ( MethodDescriptor method : event.getMethods() ) {
				Set<MethodDescriptor> visited = new HashSet<MethodDescriptor>();
				countConditionReads(method, visited, cReads);
			}
			
			conditionReads += cReads.size();
			reads += event.getReads().size() - cReads.size();
		}
		
		print(reads);
		print(conditionReads);
	}

	/**
	 * Counts the number of condition reads of a given method
	 * @param method Method
	 * @param visited Visited
	 * @param cReads Number of condition reads
	 */
	protected void countConditionReads(MethodDescriptor method, Set<MethodDescriptor> visited, Set<Field> cReads) {
		cReads.addAll(method.getConditionReads());
		
		if ( null == method.getInvokes() )
			return;

		for ( MethodDescriptor i : method.getInvokes() ) {
			if ( !visited.contains(i) ) {
				visited.add(i);
				countConditionReads(i, visited, cReads);
			}
		}
	}

	/**
	 * Counts the number of condition writes
	 */
	public void countConditionWrites() {
		int writes = 0;
		int conditionWrites = 0;
		
		for ( EventNode event : edg ) {
			if ( null == event.getMethods() ) {
				continue;
			}
			
			Set<Field> cWrites = new HashSet<Field>();
			for ( MethodDescriptor method : event.getMethods() ) {
				Set<MethodDescriptor> visited = new HashSet<MethodDescriptor>();
				countConditionWrites(method, visited, cWrites);
			}
			
			conditionWrites += cWrites.size();
			writes += event.getWrites().size() - cWrites.size();
		}
		
		print(writes);
		print(conditionWrites);
	}

	/**
	 * Counts the number of condition writes of a given method
	 * @param method Method
	 * @param visited Visited
	 * @param cWrites Number of condition writes
	 */
	protected void countConditionWrites(MethodDescriptor method, Set<MethodDescriptor> visited, Set<Field> cWrites) {
		cWrites.addAll(method.getConditionWrites());
		
		if ( null == method.getInvokes() )
			return;

		for ( MethodDescriptor i : method.getInvokes() ) {
			if ( !visited.contains(i) ) {
				visited.add(i);
				countConditionWrites(i, visited, cWrites);
			}
		}
	}

	/**
	 * Explores the write/read depths
	 */
	public void exploreWriteReadDepths() {
		List<EventNode> edg = new ArrayList<EventNode>();
		edg.add(this.getEventNode("e3724345464")); // close database
		
		//edg.add(this.getEventNode("e1795980068")); // ok (in database properties)
		edg.add(this.getEventNode("e955740480")); // ok (in manage content selectors)
		
		for ( EventNode event : edg ) {
			if ( null == event.getMethods() ) {
				continue;
			}
			
			Map<Field,Integer> writes = new HashMap<Field,Integer>();
			Map<Field,Integer> reads = new HashMap<Field,Integer>();
			
			for ( MethodDescriptor method : event.getMethods() ) {
				Set<MethodDescriptor> visited = new HashSet<MethodDescriptor>();
				exploreWriteReadDepths(method, visited, writes, reads, 0);
			}
			
			/*for ( Entry<Field,Integer> write : writes.entrySet() ) {
				if ( !reads.containsKey(write.getKey()) )
					continue;
				
				int writeDepth = write.getValue();
				int readDepth = reads.get(write.getKey());
				print(writeDepth + "\t" + readDepth);
			}*/
			
			print("writes");
			for ( Entry<Field,Integer> write : writes.entrySet() ) {
				print(write.getKey().toString() + " = " + write.getValue());
			}
			print("reads");
			for ( Entry<Field,Integer> read : writes.entrySet() ) {
				print(read.getKey().toString() + " = " + read.getValue());
			}			
		}
	}

	/**
	 * Explores the write/read depths
	 * @param method Method
	 * @param visited Visited methods
	 * @param writes Writes
	 * @param reads Reads
	 */
	public void exploreWriteReadDepths(MethodDescriptor method, Set<MethodDescriptor> visited,
			Map<Field,Integer> writes, Map<Field,Integer> reads, int depth) {
		
		markDepth(method.getWrite(), writes, depth);
		markDepth(method.getRead(), reads, depth);
		
		if ( null == method.getInvokes() )
			return;

		for ( MethodDescriptor i : method.getInvokes() ) {
			if ( !visited.contains(i) ) {
				visited.add(i);
				exploreWriteReadDepths(i, visited, writes, reads, depth + 1);
			}
		}
	}
	
	/**
	 * Marks the depth of fields
	 * @param fields Fields
	 * @param fieldDepths Depths of fields
	 * @param depth Depth
	 */
	public void markDepth(Set<Field> fields, Map<Field,Integer> fieldDepths, int depth) {
		for ( Field f : fields ) {
			if ( !fieldDepths.containsKey(f) || fieldDepths.get(f) < depth ) {
				fieldDepths.put(f, depth);
			}
		}
	}

	/**
	 * Returns the event node of the given event ID
	 * @param eventId Event ID
	 * @return Event Node or null
	 */
	public EventNode getEventNode(String eventId) {
		for ( EventNode e : edg ) {
			if ( e.getEvent().getEventId().equals(eventId) )
				return e;
		}
		return null;
	}

	/**
	 * Prints an object
	 * @param o Object
	 */
	public void print(Object o) {
		System.err.println(o);
	}

}
