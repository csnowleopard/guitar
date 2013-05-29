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
package edu.umd.cs.guitar.ripper;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

//import org.kohsuke.args4j.CmdLineException;
//import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.model.data.GUIStructure;
// Commented
//import edu.umd.cs.guitar.model.process.IO;
//import edu.umd.cs.guitar.util.ConLog;
//import edu.umd.cs.guitar.util.Log;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * 
 * Main Entry class for UNORipper
 *
 *
 */
public class UNORipperMain {

    /**
     * @param args
     */
    public static void main(String[] args) {

        UNORipperConfiguration configuration = new UNORipperConfiguration();
        //CmdLineParser parser = new CmdLineParser(configuration);
        final UNORipper unoRipper = new UNORipper(configuration);
        
        try {
            // parser.setUsageWidth(Integer.MAX_VALUE);
        	System.out.println("Running from " + System.getProperty("user.dir") );
            //parser.parseArgument(args);
        	//System.out.println("Parsed arguments");
            unoRipper.execute();
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println();
            System.err.println("Usage: java [JVM options] "
                    + UNORipperMain.class.getName() + " [Ripper options] \n");
            //System.err.println("where [Ripper options] include:");
            //System.err.println();
            //parser.printUsage(System.err);
        }
        System.exit(0);
    }
}
