package edu.umd.cs.guitar.tools.mavenupdater;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc2.SvnExport;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class MavenUpdaterMain {

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		// Create a checkout location

		
		// Create an SVN client
		SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		
		// get the pom file of guitar-parent
		String workspace_root = "/home/bryan/files/workspace";
		String pom = workspace_root + "/guitar-parent/pom.xml";
		
		// Clean up temp folder if necessary
		FileUtils.deleteDirectory(new File("tmp"));

		Reader reader = new FileReader(pom);
		try {
		    // Parse Maven pom file
			MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
		    Model model = xpp3Reader.read(reader);
		    
		    for (String module : model.getModules()){
		    	
		    	// Strip all but the name of the module
		    	module = module.replaceAll("\\.", "");
		    	module = module.replaceAll("\\/", "");
		    	
		    	// If this is a tool module, skip it
		    	// These don't exist in the GUITAR trunk
		    	if(module.substring(0, 4).equals("tool")){
		    		System.out.println("Skipping tool module: " + module);
		    		continue;
		    	}

		    	// Check out the module from sourceforge
		    	// Technically we are doing an export
				File target_loc = new File("tmp/guitar-ant/" + module);
				System.out.println("Checking out module " + module);
				
				SVNURL source_loc = SVNURL.create("http", "bryantrobbins", "guitar.svn.sourceforge.net", 80, "/svnroot/guitar/trunk/modules/" + module, false);
		    	SvnExport checkoutOp = svnOperationFactory.createExport();
				checkoutOp.setSingleTarget(SvnTarget.fromFile(target_loc));
				checkoutOp.setSource(SvnTarget.fromURL(source_loc));
				checkoutOp.run();
				
				// Now just copy the exported src folder to the corresponding Maven project's source location
				File mvn_java = new File(workspace_root + "/" + module + "/src/main/java");
				File ant_edu = new File(target_loc.getAbsolutePath() + "/src/edu");
				System.out.println("Copying " + ant_edu.getAbsolutePath() + " to " + mvn_java.getAbsolutePath());
				
				FileUtils.copyDirectoryToDirectory(ant_edu, mvn_java);
				
		    }
		} finally {
		    reader.close();
		    svnOperationFactory.dispose();
		}


	}

}
