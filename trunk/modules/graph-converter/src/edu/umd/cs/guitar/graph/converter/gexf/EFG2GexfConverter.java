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
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.graph.api.Attributable;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.plugin.ExporterGEXF;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.ImporterEFG;
import org.gephi.io.importer.plugin.ImporterEFGSmooth;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.RowType;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIMapWrapper;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class EFG2GexfConverter {

	/**
	 * @param isNoTitle
	 * @see org.gephi.io.importer.plugin.ImporterEFG#setNoTitle(boolean)
	 */
	public void setNoTitle(boolean isNoTitle) {
		this.isNoTitle = isNoTitle;
	}

	ProjectController pc;
	Workspace workspace;
	Container container;
	// Get controllers and models
	ImportController importController = Lookup.getDefault().lookup(
			ImportController.class);

	/**
	 * @param hasTitle
	 * @see org.gephi.io.importer.plugin.ImporterEFG#setHasTitle(boolean)
	 */

	ImporterEFG importer;
	GUIMap map;
	List<EFG> efgList;
	EFG sampleEFG;

	/**
	 * Generate with a smooth input space increment
	 */
	boolean isSmooth = false;
	boolean isSimpleMode = false;
	boolean isNoEdge;
	boolean isNoTitle = false;

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

	/**
	 * @param map
	 * @param efgList
	 * @param isSmooth
	 */
	public EFG2GexfConverter(GUIMap map, List<EFG> efgList, boolean isSmooth) {
		super();
		this.map = map;
		this.efgList = efgList;
		this.isSmooth = isSmooth;
	}

	/**
	 * @param map
	 * @param efgList
	 */
	public EFG2GexfConverter(GUIMap map, List<EFG> efgList) {
		super();
		this.map = map;
		this.efgList = efgList;
	}

	/**
	 * @param map
	 * @param efgList
	 * @return
	 */
	public void convert() {

		// Gephi variables
		// Init a project - and therefore a workspace
		pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		workspace = pc.getCurrentWorkspace();

		container = Lookup.getDefault().lookup(ContainerFactory.class)
				.newContainer();
		if (!isSmooth) {
			importer = new ImporterEFG();
			importer.setSimpleMode(isSimpleMode);
			importer.setNoEdge(isNoEdge);
			importer.setNoTitle(isNoTitle);
		} else
			importer = new ImporterEFGSmooth();

		if (map != null)
			importer.setInputData(map, efgList);
		else
			importer.setEfgList(efgList);

		importer.execute(container.getLoader());

	}

	/**
	 * @param efgList
	 */
	public EFG2GexfConverter(List<EFG> efgList) {
		super();
		this.efgList = efgList;

	}

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
	 * @param stringWriter
	 * @return
	 */
	public void exportWriter(StringWriter stringWriter) {
		System.out.println("Results:");
		// Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		// Export full graph
		ExportController ec = Lookup.getDefault()
				.lookup(ExportController.class);

		// Export to Writer
		Exporter exporter = ec.getExporter("gexf"); // Get GraphML
		((ExporterGEXF) exporter).setExportSize(false);
		((ExporterGEXF) exporter).setExportColors(false);
		((ExporterGEXF) exporter).setExportPosition(false);

		exporter.setWorkspace(workspace);
		ec.exportWriter(stringWriter, (CharacterExporter) exporter);
		System.out.println(stringWriter.toString());
	}

	/**
	 * @param stringWriter
	 * @return
	 */
	public void exportFile(File file) {
		// Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		// Export full graph
		ExportController ec = Lookup.getDefault()
				.lookup(ExportController.class);

		// Export to Writer
		Exporter exporter = ec.getExporter("gexf"); // Get GraphML
		((ExporterGEXF) exporter).setExportSize(false);
		((ExporterGEXF) exporter).setExportColors(false);
		((ExporterGEXF) exporter).setExportPosition(false);

		exporter.setWorkspace(workspace);

		try {
			ec.exportFile(file, exporter);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}

}
