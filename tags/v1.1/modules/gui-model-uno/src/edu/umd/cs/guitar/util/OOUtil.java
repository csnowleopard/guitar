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
package edu.umd.cs.guitar.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.model.GUITARConstants;

/**
 * Static utility functions which might be used globally
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * @author <a href="mailto:wikum@cs.umd.edu"> Wikum Dinalankara </a>
 */
public class OOUtil {

    /**
     * Hide the constructor
     */
    private OOUtil() {
    }

    /**
     * Get a list of string from a text file. Used for ignoring widgets,
     * windows...
     * 
     * @param sFileName
     * @param isOrdered
     * @return
     */

    public static List<String> getListFromFile(String sFileName,
            boolean isOrdered) {

        List<String> retList;

        if (isOrdered) {
            retList = new LinkedList<String>();
        } else {
            retList = new ArrayList<String>();
        }

        try {
            
            ClassLoader cl = Util.class.getClassLoader();
            //sFileName = System.getProperty("user.dir") + File.separator + sFileName;
            InputStream is = new FileInputStream(sFileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
//            BufferedReader br = new BufferedReader(new FileReader(sFileName));
            //if (is != null)
	    //System.out.println("Found " + sFileName);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith(GUITARConstants.IGNORE_COMMENT_PREFIX)
                        && !("".equals(line)))
                    retList.add(line);
            }
            
        } catch (Exception e) {
        	System.err.println( sFileName + " not found") ;
        }
 
        return retList;
    }
}
