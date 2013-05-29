package edu.umd.cs.guitar.replayer;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.kohsuke.args4j.CmdLineException;
import org.xml.sax.SAXException;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.replayer.monitor.GTestMonitor;
import edu.umd.cs.guitar.util.ConLog;
import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.util.Log;

import edu.umd.cs.guitar.exception.ComponentDisabled;
import edu.umd.cs.guitar.exception.ComponentNotFound;

public class OOReplayer {
	OOReplayerConfiguration CONFIG;
	
	public OOReplayer(OOReplayerConfiguration configuration) {
		super();
		this.CONFIG = configuration;
	}
	
	public void execute() throws CmdLineException {
		int nPort = 5678;
		
		// Read test case
		String sTC = OOReplayerConfiguration.TESTCASE;
		
                System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY, OOReplayerConfiguration.LOG_FILE);
		TestCase tc = (TestCase) IO.readObjFromFile(sTC, TestCase.class);
		//Log log = new ConLog();
	
		UNOReplayer replayer;
		OOReplayerMonitor monitor = null;
		try {
			replayer = new UNOReplayer(tc, OOReplayerConfiguration.GUI_FILE,
						OOReplayerConfiguration.EFG_FILE);
			// Configure monitor
			monitor = new OOReplayerMonitor(nPort);
			//monitor.setTimeout(REPLAYER_TIMEOUT);
				
			// Test case monitor
			GTestMonitor oTestMonitor = new OOMonitorTest();
			replayer.addTestMonitor(oTestMonitor); 
				
			replayer.setMonitor(monitor);
			replayer.setTimeOut(OOReplayerConfiguration.REPLAYER_TIMEOUT);
			replayer.execute();
				
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ComponentNotFound ex2){
			System.out.println("Could not find component");
		} catch (ComponentDisabled ex3){
			System.out.println("Component is disabled");
		} catch (Exception ex4){
			System.out.println("Could not execute testcase");
			ex4.printStackTrace();
		}

		if( CONFIG.CLOSE_APP_ON_FINISH ){
			try{
				monitor.closeApplication();
			}catch(Exception exClose){
				exClose.printStackTrace();
			}
		}

		System.exit(0);
	}
}
