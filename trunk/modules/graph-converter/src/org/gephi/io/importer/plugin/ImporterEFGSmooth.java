/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
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
package org.gephi.io.importer.plugin;

import java.util.List;

import org.gephi.dynamic.api.DynamicModel.TimeFormat;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.Report;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class ImporterEFGSmooth extends ImporterEFG {
	int index = 0;

	/**
	 * 
	 */
	public ImporterEFGSmooth() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param container
	 */
	public void execute(ContainerLoader container) {

		if (map == null || efgList == null)
			return;

		// Setup

		this.container = container;
		this.container.setTimeFormat(TimeFormat.DATE);
		this.report = new Report();

		setupNodeAttributes();

		// Progress.start(progress);

		for (EFG efg : efgList) {
			System.out.println("Start index: " + index);
			addEFG(efg, index);
			index++;
		}

		// Progress.finish(progress);
	}

	/**
	 * @param efg
	 * @param startIteration
	 */
	@Override
	protected void addEFG(EFG efg,  int iteration) {
		List<EventType> eventList = efg.getEvents().getEvent();
		EFGWrapper wEFG = new EFGWrapper(efg);

		// Add the node if it does not exist yet
		for (EventType event : eventList) {
			addNode(event, this.index++);
		}

		// add edge
		for (EventType sourceEvent : eventList) {
			this.index++;
			for (EventType targetEvent : eventList) {
				int edgeValue = wEFG.getEdge(sourceEvent, targetEvent);

				if (edgeValue != GUITARConstants.NO_EDGE) {
					addEdge(sourceEvent, targetEvent, edgeValue, this.index);
				}

			}
		}
	}

}
