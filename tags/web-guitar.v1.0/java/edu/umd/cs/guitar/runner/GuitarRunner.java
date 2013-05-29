/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of
 * this group may be obtained by sending an e-mail to atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.runner;

import java.io.File;

/**
 * Abstract runner for Guitar, containing constants and utility methods for concrete runner.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class GuitarRunner {

  // Flag default values.
  protected static final String DEFAULT_TEST_NUMBER = "0";

  protected static final String DEFAULT_TEST_LENGTH = "2";

  private static final String DEFAULT_OUTPUT_DIRECTORY = "." + "-" + System.currentTimeMillis();

  protected static final String DEFAULT_EFG_FILE =
      DEFAULT_OUTPUT_DIRECTORY + File.separator + "Guitar.EFG";

  protected static final String DEFAULT_RIPPING_SCREENSHOTS_DIRECTORY =
      DEFAULT_OUTPUT_DIRECTORY + File.separator + "ripping-screenshots";

  protected static final String DEFAULT_TEST_CASE_DIRECTORY =
      DEFAULT_OUTPUT_DIRECTORY + File.separator + "testcases";

  // Flag names. 
  protected static final String GUITAR_TEST_SPECIFICATION_FLAG = "testSpec";
  protected static final String GUITAR_ENTRY_URL_FLAG = "url";

  protected static final String GUITAR_MAP_FILE_FLAG = "mapFile";
  protected static final String GUITAR_EFG_FILE_FLAG = "efgFile";

  protected static final String GUITAR_RIPPING_SCREENSHOT_OFF_FLAG = "rippingScreenshotOff";
  protected static final String GUITAR_RIPPING_SCREENSHOT_DIR_FLAG = "rippingScreenshotDir";

  protected static final String GUITAR_GEN_TEST_OFF_FLAG = "genTestOff";

  protected static final String GUITAR_TEST_DIRECTORY_FLAG = "testDir";
  protected static final String GUITAR_TEST_NUMBER_FLAG = "testNumber";
  protected static final String GUITAR_TEST_LENGTH_FLAG = "testLength";
  protected static final String GUITAR_TEST_RANDOMIZED_FLAG = "randomized";
  

  /**
   * Analyze JVM flag for parameters.
   */
  protected static String getValueFromJVMFlag(
      String flag, boolean isRequired, String defaultValue) {

    String value = System.getProperty(flag);
    if (value != null){
      return value;
    }

    if (isRequired) {
      throw new IllegalArgumentException("Need to specify value for JVM parameter -D" + flag);
    } else {
      return defaultValue;
    }
  }

  protected static String getValueFromJVMFlag(String flag) {
    return getValueFromJVMFlag(flag, true, null);
  }
}
