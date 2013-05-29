/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s): Alexandre Iline.
 *
 * The Original Software is the Jemmy library.
 * The Initial Developer of the Original Software is Alexandre Iline.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 *
 *
 * $Id$ $Revision$ $Date$
 *
 */

package org.netbeans.jemmy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * Load string resources from file.
 * Resources should be stored in <code>name=value</code> format.
 *
 * @see org.netbeans.jemmy.BundleManager
 *	
 * @author Alexandre Iline (alexandre.iline@sun.com)
 */

public class Bundle extends Object {

    private Properties resources;

    /**
     * Bunble constructor.
     */
    public Bundle() {
	resources = new Properties(); 
    }

    /**
     * Loads resources from an input stream.
     * 
     * @param	stream Stream to load resources from.
     * @exception	IOException
     */
    public void load(InputStream stream) 
	throws IOException {
	resources.load(stream);
    }

    /**
     * Loads resources from a simple file.
     * 
     * @param	fileName Name of the file to load resources from.
     * @exception	IOException
     * @exception	FileNotFoundException
     */
    public void loadFromFile(String fileName) 
	throws IOException, FileNotFoundException {
	load(new FileInputStream(fileName));
    }

    /**
     * Loads resources from a file in a jar archive.
     * 
     * @param	fileName Name of the jar archive.
     * @param	entryName ?enryName? Name of the file to load resources from.
     * @exception	IOException
     * @exception	FileNotFoundException
     */
    public void loadFromJar(String fileName, String entryName) 
	throws IOException, FileNotFoundException {
	JarFile jFile = new JarFile(fileName);
	load(jFile.getInputStream(jFile.getEntry(entryName)));
    }

    /**
     * Loads resources from a file in a zip archive.
     * 
     * @param	fileName Name of the zip archive.
     * @param	entryName ?enryName? Name of the file to load resources from.
     * @exception	ZipException
     * @exception	IOException
     * @exception	FileNotFoundException
     */
    public void loadFromZip(String fileName, String entryName)
	throws IOException, FileNotFoundException, ZipException {
	ZipFile zFile = new ZipFile(fileName);
	load(zFile.getInputStream(zFile.getEntry(entryName)));
    }

    /**
     * Prints bundle contents.
     * @param writer Writer to print data in.
     */
    public void print(PrintWriter writer) {
	Enumeration keys = resources.keys();
	while(keys.hasMoreElements()) {
	    String key = (String)keys.nextElement();
	    writer.println(key + "=" + getResource(key));
	}
    }

    /**
     * Prints bundle contents.
     * @param stream Stream to print data in.
     */
    public void print(PrintStream stream) {
	print(new PrintWriter(stream));
    }

    /**
     * Gets resource by key.
     * @param key Resource key
     * @return Resource value or null if resource was not found.
     */
    public String getResource(String key) {
	return(resources.getProperty(key));
    }

}
