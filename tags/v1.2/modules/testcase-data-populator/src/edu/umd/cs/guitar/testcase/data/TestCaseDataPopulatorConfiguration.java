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
package edu.umd.cs.guitar.testcase.data;

import org.kohsuke.args4j.Option;

;

/**
 * Class contains the runtime configurations for TestCaseDataPopulator 
 * 
 * <p>
 * 
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class TestCaseDataPopulatorConfiguration {

    @Option(name = "-it",
            usage = "input test case",
            aliases = "--input-testcase",
            required= true)
    static public String INPUT_TESTCASE = null;
    
    @Option(name = "-ot",
            usage = "output test case prefix",
            aliases = "--output-testcase-prefix",
            required= true)
    static public String OUTPUT_TESTCASE_PREFIX = null;
    
    @Option(name = "-g",
            usage = "GUI file",
            aliases = "--gui-file",
            required= true)
    static public String GUI_FILE = null;
    
    
    @Option(name = "-e",
            usage = "EFG file",
            aliases = "--efg-file",
            required= true)
    static public String EFG_FILE = null;

    @Option(name = "-d",
            usage = "test data file",
            aliases = "--data-file",
            required= true)
    static public String DATA_FILE = null;

    @Option(name = "-?",
            usage = "print this help message",
            aliases = "--help")
    static protected boolean HELP;
    /**
     * Check arguments' configuration
     * 
     * @return true/false
     */

    public boolean isValid() {
        return true;
    }
}
