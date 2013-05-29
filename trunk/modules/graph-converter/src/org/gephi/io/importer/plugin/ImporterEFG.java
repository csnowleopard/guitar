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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.type.DynamicInteger;
import org.gephi.data.attributes.type.Interval;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.DynamicUtilities;
import org.gephi.dynamic.api.DynamicModel;
import org.gephi.dynamic.api.DynamicModel.TimeFormat;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

import com.itextpdf.text.pdf.ArabicLigaturizer;
import com.ojn.gexf4j.core.Edge;
import com.ojn.gexf4j.core.Graph;
import com.ojn.gexf4j.core.Node;

import edu.umd.cs.guitar.graph.converter.gexf.Const;
import edu.umd.cs.guitar.graph.converter.gexf.CoverageType;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIMapWrapper;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class ImporterEFG {

	/**
	 * @return the isSimpleMode
	 */
	public boolean isSimpleMode() {
		return isSimpleMode;
	}

	/**
	 * @param isSimpleMode
	 *            the isSimpleMode to set
	 */
	public void setSimpleMode(boolean isSimpleMode) {
		this.isSimpleMode = isSimpleMode;
	}

	/**
	 * 
	 */

	protected ContainerLoader container;
	protected Report report;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	Calendar origDay = Calendar.getInstance();
	GUIMapWrapper map;
	List<EFG> efgList;
	DynamicInteger DEFAULT_COMPLEX_DYNAMIC_VALUE;
	DynamicInteger DEFAULT_SIMPLE_DYNAMIC_VALUE;

	boolean isSimpleMode = false;
	boolean isNoEdge = false;

	/**
	 * @return the isNoEdge
	 */
	public boolean isNoEdge() {
		return isNoEdge;
	}

	/**
	 * @param isNoEdge
	 *            the isNoEdge to set
	 */
	public void setNoEdge(boolean isNoEdge) {
		this.isNoEdge = isNoEdge;
	}
	
	private boolean isNoTitle = true;

	/**
	 * @return the noTitle
	 */
	public boolean isNoTitle() {
		return isNoTitle;
	}

	/**
	 * @param noTitle the noTitle to set
	 */
	public void setNoTitle(boolean noTitle) {
		this.isNoTitle = noTitle;
	}

	/**
	 * Default end date
	 */
	int maxIteration = 6;

	/**
	 * @param maxIteration
	 *            the maxIteration to set
	 */
	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}

	/**
	 * @param minIteration
	 *            the minIteration to set
	 */
	public void setMinIteration(int minIteration) {
		this.minIteration = minIteration;
	}

	int minIteration = 0;
	

	/**
	 * 
	 */
	public ImporterEFG() {
		super();
		origDay.set(2012, 00, 01);
		DEFAULT_COMPLEX_DYNAMIC_VALUE = getComplexDynamicValue(minIteration,
				maxIteration, 1);
		DEFAULT_SIMPLE_DYNAMIC_VALUE = getSimpleDynamicValue(minIteration,
				maxIteration, 1);

	}

	/**
	 * @param container
	 */
	public void execute(ContainerLoader container) {

		if (efgList == null)
			return;

		// Setup

		this.container = container;
		this.container.setTimeFormat(TimeFormat.DATE);
		this.report = new Report();

		setupNodeAttributes();

		// Progress.start(progress);
		System.out.println("efgList size: " + efgList.size());
		int startIteration = 0;
		for (EFG efg : efgList) {
			System.out.println("Iteration: " + startIteration);
			addEFG(efg, startIteration);
			startIteration++;
		}

		// Progress.finish(progress);
	}

	/**
	 * 
	 */
	protected void setupNodeAttributes() {

		// Node
		if (!isSimpleMode) {
			container
					.getAttributeModel()
					.getNodeTable()
					.addColumn(Const.NODE_COVERED_TYPE_ID, "Covered Type",
							AttributeType.DYNAMIC_INT, AttributeOrigin.DATA,
							DEFAULT_SIMPLE_DYNAMIC_VALUE);
		}

		if (!isNoTitle) {
			container
					.getAttributeModel()
					.getNodeTable()
					.addColumn(Const.NODE_WINDOW_ID, "Window",
							AttributeType.STRING, AttributeOrigin.DATA, "");

			container
					.getAttributeModel()
					.getNodeTable()
					.addColumn(Const.NODE_TITLE_ID, "Title",
							AttributeType.STRING, AttributeOrigin.DATA, "");

			container
					.getAttributeModel()
					.getNodeTable()
					.addColumn(Const.NODE_CLASS_ID, "Class",
							AttributeType.STRING, AttributeOrigin.DATA, "");

		}
		if (!isSimpleMode) {

			container
					.getAttributeModel()
					.getNodeTable()
					.addColumn(Const.NODE_OPTIONAL_ID, "Optional",
							AttributeType.STRING, AttributeOrigin.DATA, "");
		}
//		container
//				.getAttributeModel()
//				.getEdgeTable()
//				.addColumn(Const.EDGE_COVERED_TYPE_ID, "Covered Type",
//						AttributeType.DYNAMIC_INT, AttributeOrigin.DATA,
//						DEFAULT_SIMPLE_DYNAMIC_VALUE);

		if (!isSimpleMode) {
			container
					.getAttributeModel()
					.getEdgeTable()
					.addColumn(Const.EDGE_OPTIONAL_ID, "Optional",
							AttributeType.STRING, AttributeOrigin.DATA, "");
		}

	}

	/**
	 * @param minIteration2
	 * @param maxIteration2
	 * @return
	 */
	private DynamicInteger getSimpleDynamicValue(int minValue, int maxValue,
			int value) {
		List<Interval<Integer>> intervals = new ArrayList<Interval<Integer>>();

		String default_start_date = getStrDateFromIteration(minValue);
		String default_end_date = getStrDateFromIteration(maxValue);
		Double dDefaultStartDate = DynamicUtilities
				.getDoubleFromXMLDateString(default_start_date);
		Double dDefaultEndDate = DynamicUtilities
				.getDoubleFromXMLDateString(default_end_date);
		intervals.add(new Interval<Integer>(dDefaultStartDate, dDefaultEndDate,
				value));

		DynamicInteger result = new DynamicInteger(intervals);
		return result;
	}

	/**
	 * @param minIteration2
	 * @param maxIteration2
	 * @return
	 */
	private DynamicInteger getComplexDynamicValue(int minValue, int maxValue,
			int value) {

		List<Interval<Integer>> intervals = new ArrayList<Interval<Integer>>();

		for (int i = minValue; i < maxValue; i++) {
			String default_start_date = getStrDateFromIteration(i);
			String default_end_date = getStrDateFromIteration(i + 1);
			Double dDefaultStartDate = DynamicUtilities
					.getDoubleFromXMLDateString(default_start_date);
			Double dDefaultEndDate = DynamicUtilities
					.getDoubleFromXMLDateString(default_end_date);
			intervals.add(new Interval<Integer>(dDefaultStartDate,
					dDefaultEndDate, value));
		}

		DynamicInteger result = new DynamicInteger(intervals);
		return result;
	}

	/**
	 * @param efg
	 * @param startIteration
	 */
	protected void addEFG(EFG efg, int iteration) {
		List<EventType> eventList = efg.getEvents().getEvent();
		EFGWrapper wEFG = new EFGWrapper(efg);

		System.out.println("Adding nodes....");
		// Add the node if it does not exist yet
		for (EventType event : eventList) {
			addNode(event, iteration);
		}
		System.out.println("Adding edges....");
		if (!isNoEdge) {
			// add edge
			for (EventType sourceEvent : eventList) {
				for (EventType targetEvent : eventList) {
					int edgeValue = wEFG.getEdge(sourceEvent, targetEvent);
					if (edgeValue != GUITARConstants.NO_EDGE) {
						addEdge(sourceEvent, targetEvent, edgeValue, iteration);
					}

				}
			}
		}
	}

	/**
	 * @param sourceEvent
	 * @param targetEvent
	 * @param iteration
	 */
	void addEdge(EventType sourceEvent, EventType targetEvent, int weight, int iteration) {
		String sourceId = sourceEvent.getEventId();
		String targetId = targetEvent.getEventId();

		NodeDraft source = container.getNode(sourceId);
		NodeDraft target = container.getNode(targetId);

		if (source == null || target == null)
			return;

		EdgeDraft edge = container.getEdge(source, target);

		String startDate;

		if (edge == null) {
			edge = container.factory().newEdgeDraft();
			edge.setSource(source);
			edge.setTarget(target);
			edge.setWeight(weight);
			container.addEdge(edge);
			startDate = getStrDateFromIteration(iteration);
			edge.addTimeInterval(startDate,
					getStrDateFromIteration(maxIteration));
		}
	}

	void addNode(EventType event, int iteration) {
		String startDate = getStrDateFromIteration(iteration);

		NodeDraft node = container.factory().newNodeDraft();

		String id = event.getEventId();

		if (!container.nodeExists(id)) {
			// Set ID
			node.setId(id);

			if (map != null) {
				// Set label
				String label;
				String widgetClass;

				WidgetMapElementType widgetMapElement = map.getWidgetMap(id);
				if (widgetMapElement == null) {
					label = id;
					widgetClass = "No Class";
				} else {
					ComponentType component = widgetMapElement.getComponent();

					ComponentTypeWrapper wComponent = new ComponentTypeWrapper(
							component);
					label = wComponent
							.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
					label = label.replace("Pos", "Item");
					widgetClass = wComponent
							.getFirstValueByName(GUITARConstants.CLASS_TAG_NAME);
				}

				node.addTimeInterval(startDate,
						getStrDateFromIteration(maxIteration), false, false);
				AttributeColumn column;
				// Add properties
				// Title
				column = container.getAttributeModel().getNodeTable()
						.getColumn(Const.NODE_TITLE_ID);
				node.addAttributeValue(column, label);
				// Class
				column = container.getAttributeModel().getNodeTable()
						.getColumn(Const.NODE_CLASS_ID);
				node.addAttributeValue(column, widgetClass);

				// Window
				column = container.getAttributeModel().getNodeTable()
						.getColumn(Const.NODE_WINDOW_ID);
				if (column == null)
					return;
				String windowTitle = "";

				if (widgetMapElement != null) {

					ComponentType window = widgetMapElement.getWindow();
					ComponentTypeWrapper wComponent = new ComponentTypeWrapper(
							window);
					windowTitle = wComponent
							.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
				}
				node.addAttributeValue(column, windowTitle);
			}

			container.addNode(node);
		}

	}

	/**
	 * @param iteration
	 * @return
	 */
	private String getStrDateFromIteration(int iteration) {
		Calendar date = (Calendar) origDay.clone();
		date.add(Calendar.DAY_OF_MONTH, iteration);
		return dateFormat.format(date.getTime());

	}

	public void setInputData(GUIMap map, List<EFG> efgList) {
		this.map = new GUIMapWrapper(map);
		this.efgList = efgList;
	}

	/**
	 * @param eid
	 * @param attrID
	 * @param attrValue
	 * @param startIteration
	 * @param endIteration
	 */
	public void addEventProperty(String eid, String attrID, Object attrValue,
			int startIteration, int endIteration) {

		String startDate = getStrDateFromIteration(startIteration);
		String endDate = getStrDateFromIteration(endIteration);

		AttributeColumn column = container.getAttributeModel().getNodeTable()
				.getColumn(attrID);
		if (column == null)
			return;

		NodeDraft node = container.getNode(eid);
		node.addAttributeValue(column, attrValue, startDate, endDate);

	}

	/**
	 * @return the efgList
	 */
	public List<EFG> getEfgList() {
		return efgList;
	}

	/**
	 * @param efgList
	 *            the efgList to set
	 */
	public void setEfgList(List<EFG> efgList) {
		this.efgList = efgList;
	}

	/**
	 * @param sourceID
	 * @param targetID
	 * @param attrID
	 * @param attrValue
	 * @param startIteration
	 * @param endIteration
	 */
	public void addEdgeProperty(String sourceID, String targetID,
			String attrID, int attrValue, int startIteration, int endIteration) {
		String startDate = getStrDateFromIteration(startIteration);
		String endDate = getStrDateFromIteration(endIteration);

		AttributeColumn column = container.getAttributeModel().getEdgeTable()
				.getColumn(attrID);
		if (column == null) {
			System.err.println("No column ID:" + attrID);
			return;
		}
		NodeDraft source = container.getNode(sourceID);
		NodeDraft target = container.getNode(targetID);

		EdgeDraft edge = container.getEdge(source, target);
		if (edge != null)
			edge.addAttributeValue(column, attrValue, startDate, endDate);

	}
}
