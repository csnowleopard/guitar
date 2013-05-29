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
package edu.umd.cs.guitar.util;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;

import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.util.GUIStructureInfoUtil;
import edu.umd.cs.guitar.util.GUITARLog;

import org.kohsuke.args4j.CmdLineException;


/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class GUIStructureInfo {

   /**
    * @param inputGraph
    */
   public
   GUIStructureInfo(GUIStructure guistructure) {
   }

   public static void
   main(String [] args)
   {
      String sGUIFilename = args[0];

      try {
         if (args.length < 2) {
            throw new CmdLineException("");
         }

         boolean leaf = (Integer.parseInt(args[1]) >= 1) ? true : false;

         File file = new File(sGUIFilename);
         if (!file.exists()) {
            throw new FileNotFoundException("");
         }

         GUIStructure guistructure =
            (GUIStructure) IO.readObjFromFile(sGUIFilename,
                                              GUIStructure.class);

         GUIStructureInfoUtil guistructureinfoutil =
            new GUIStructureInfoUtil();

         guistructureinfoutil.generate(guistructure, leaf);
      } catch (CmdLineException e) {
         System.out.println("Usage: " + 
                            GUIStructureInfo.class.getName() +
                            " <guifile> <show leaf>");
         System.exit(1);

      } catch (FileNotFoundException e) {
         GUITARLog.log.error("File not found " + sGUIFilename);
         System.exit(1);
		}

   }

} // End of class
