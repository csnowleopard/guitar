package overlayGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import overlayGraph.util.SiteInfo;

public class VisualizationDriver {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USAGE: ValidationVisualizer.jar <URL> <Workflow Prefix>");
            System.exit(0);
        }
        final String nodeClickedURL = args[0];
        final String ripName = args[1];
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        final SiteInfo siteInfo= new SiteInfo(ripName + ".xml");

        new Simulator(nodeClickedURL, nodeClickedURL, ripName, siteInfo);
    }
}
