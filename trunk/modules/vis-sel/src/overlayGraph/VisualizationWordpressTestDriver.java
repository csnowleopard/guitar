package overlayGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import overlayGraph.util.SiteInfo;

public class VisualizationWordpressTestDriver {
	
	public static void main(String[] args) {
        final String nodeClickedURL= "http://128.8.126.15/wordpress/";
        final String ripName = "GUITAR-Default";
        File javaCallConfirm= new File("javaCallConfirmation.txt");
        FileWriter javaCallConfirmFileWriter;
        PrintWriter javaCallConfirmPrintWriter;
        
		try {
			
			javaCallConfirmFileWriter = new FileWriter(javaCallConfirm);
			javaCallConfirmPrintWriter= new PrintWriter(javaCallConfirmFileWriter);
			
			javaCallConfirmPrintWriter.println("Java VisualizationTestDriver called");
			javaCallConfirmPrintWriter.println("\tby Rip named: \"" + ripName +"\"");
			javaCallConfirmPrintWriter.println("\t\ton URL: \"" + nodeClickedURL + "\"");
			
			javaCallConfirmPrintWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        final SiteInfo siteInfo= new SiteInfo(ripName + ".xml");

        new Simulator(nodeClickedURL, nodeClickedURL, ripName, siteInfo);
    }
}
