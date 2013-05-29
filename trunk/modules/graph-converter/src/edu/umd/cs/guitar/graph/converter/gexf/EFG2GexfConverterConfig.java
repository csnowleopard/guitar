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

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class EFG2GexfConverterConfig {

	@Option(name = "-e", usage = "input efg file list", aliases = "--efg-files", multiValued = true, required = true)
	static public List<String> efgFileList;

	@Option(name = "-m", usage = "input map file list", aliases = "--map-file", required = false)
	static public String mapFile;

	@Option(name = "-f", usage = "output gexf file list", aliases = "--gexf-file", required = true)
	static public String gexfFile;

	@Option(name = "-s", usage = "generate with a smooth increment", aliases = "--is-smooth", required = false)
	static public boolean isSmooth = false;

	@Option(name = "-si", usage = "generate with a simple mode to minimize  storage", aliases = "--is-simple", required = false)
	static public boolean isSimpleMode = false;

	@Option(name = "-ne", usage = "generate without edge", aliases = "--no-edge", required = false)
	static public boolean isNoEdge = false;

	@Option(name = "-nt", usage = "generate without title", aliases = "--no-title", required = false)
	static public boolean isNoTitle = false;

	// receives other command line parameters than options
	@Argument
	static public List<String> arguments = new ArrayList<String>();

}
