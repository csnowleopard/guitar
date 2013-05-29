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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.plugin.ExporterGEXF;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.openide.util.Lookup;

import com.ojn.gexf4j.core.Gexf;
import com.ojn.gexf4j.core.impl.StaxGraphWriter;

import edu.umd.cs.guitar.graph.converter.gexf.Const;
import edu.umd.cs.guitar.graph.converter.gexf.EFG2GexfConverterConfig;
import edu.umd.cs.guitar.graph.converter.gexf.EFG2GexfConverterGexf4j;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.ObjectFactory;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class ImporterEFGTest {
	ProjectController pc;
	Workspace workspace;
	Container container;
	// Get controllers and models
	ImportController importController = Lookup.getDefault().lookup(
			ImportController.class);

	ImporterEFG importer;
	GUIMap map;
	List<EFG> efgList;
	EFG sampleEFG;
	String outputFile;

	private String testDataDir = "tests-data/simple";
	private String[] args = { "", "-e",
			testDataDir + File.separator + "CS-0.EFG", "-e",
			testDataDir + File.separator + "CS-1.EFG", "-m",
			testDataDir + File.separator + "CS.MAP", "-f",
			testDataDir + File.separator + "CS.GEXF", };

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		// Gephi variables

		// Init a project - and therefore a workspace
		pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		workspace = pc.getCurrentWorkspace();

		container = Lookup.getDefault().lookup(ContainerFactory.class)
				.newContainer();

		loadData(args);

		importer = new ImporterEFG();
		importer.setInputData(map, efgList);
		sampleEFG = efgList.get(0);

		importer.execute(container.getLoader());
	}

	ObjectFactory factory;

	// @Test
	public void testAddEvent() {
		importer.execute(container.getLoader());
		// printResults();
	}

	@Test
	public void testEventType() {

		EventType sampleEvent = sampleEFG.getEvents().getEvent().get(0);
		String eid = sampleEvent.getEventId();

		String attrID = "0";
		int attrValue = 1;
		int startIteration = 1;
		int endIteration = 2;

		importer.addEventProperty(eid, attrID, attrValue, startIteration,
				endIteration);

		System.out.println("testEventType: DONE");
	}

//	@Test
	public void testEdgeType() {

		int length = sampleEFG.getEvents().getEvent().size();
		for (int i = 0; i < length / 2; i++) {
			EventType sampleSource = sampleEFG.getEvents().getEvent().get(i);
			String sourceID = sampleSource.getEventId();
			for (EventType sampleTarget : sampleEFG.getEvents().getEvent()) {
				String targetID = sampleTarget.getEventId();
				String attrID = Const.EDGE_COVERED_TYPE_ID;
				int attrValue = 1;
				int startIteration = 1;
				int endIteration = 2;

				importer.addEdgeProperty(sourceID, targetID, attrID, attrValue,
						startIteration, endIteration);
			}
		}

		System.out.println("testEdgeType: DONE");
	}

	/**
	 * 
	 */
	private void printResults() {
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
		StringWriter stringWriter = new StringWriter();
		ec.exportWriter(stringWriter, (CharacterExporter) exporter);
		System.out.println(stringWriter.toString());

		try {
			ec.exportFile(new File(outputFile), exporter);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}

	/**
	 * @param args
	 */
	private void loadData(String[] args) {
		EFG2GexfConverterConfig configuration = new EFG2GexfConverterConfig();
		CmdLineParser parser = new CmdLineParser(configuration);
		try {
			parser.parseArgument(args);

			System.out.println("Reading data....");
			map = (GUIMap) IO.readObjFromFile(configuration.mapFile,
					GUIMap.class);

			efgList = new ArrayList<EFG>();

			for (String efgFile : configuration.efgFileList) {
				EFG efg = (EFG) IO.readObjFromFile(efgFile, EFG.class);
				efgList.add(efg);
			}
			outputFile = configuration.gexfFile;

		} catch (CmdLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		printResults();
	}

}
