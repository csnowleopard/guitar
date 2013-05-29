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


import com.crawljax.core.CrawljaxException;

import edu.umd.cs.guitar.exception.RipperConstructionException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Runner to rip the web site from its URL and generate test cases. Parameters are passed through
 * JVM's flags to be consistent with the test runner {@link GuitarJUnitRunner}.
 * <p>
 * To generate tests from the command line, run: <code>
 * edu.umd.cs.guitar.runner.GuitarTestCaseGenerator -Dparameter1=value1 -Dparameter2=value2 ... </code> where
 * parameters are:
 * <p>
 *
 * Required:
 * <ul>
 * <li>-DtestSpec: Test specification file specifying (1) ripping behaviors and (2) initialization plugin.
 * <li>-Durl: Entry URL to start ripping (e.g., http://example.com).
 * <li>-DmapFile: Output GUI map file containing all elements captured.
 * </ul>
 * <p>
 * Optional:
 * <ul>
 * <li>-DefgFile: Output Event-flow graph file for test case generation.
 * <li>-DrippingScreenshotOff: Turn off screenshot capturing feature.
 * <li>-DrippingScreenshotDir: Output screenshot directory.
 * <li>-DgenTestOff: Turn off test case generation feature
 * <li>-DtestNumber: Maximum number of test case to generate. 0 means generate all possible test
 * cases
 * <li>-DtestLength: Length of event sequence coverage by the generated test suite. For example, 1 =
 * covering all single events, 2 = covering all pairs of event interactions. Refer to <a
 * href="http://www.cs.umd.edu/~atif/papers/MemonFSE2001.pdf"> this paper </a> for more detail on
 * coverage criteria for testing GUI applications.
 * </ul>
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class GuitarTestCaseGenerator extends GuitarRunner {

  private static Logger LOGGER = Logger.getLogger(GuitarTestCaseGenerator.class);

  /**
   * Setup log4j to printout the right message.
   * <p>
   * TODO(baonn): Figure out a way to make this configuration work via an external log.property
   * file
   */
  static {
    Logger root = Logger.getRootLogger();
    if (!root.getAllAppenders().hasMoreElements()) {
      Appender appender =
          new ConsoleAppender(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN));

      Logger guitarLogger = Logger.getLogger("edu.umd.cs.guitar");
      guitarLogger.setLevel(Level.INFO);
      guitarLogger.addAppender(appender);

      Logger crawljaxLogger = Logger.getLogger("com.crawljax");
      crawljaxLogger.setLevel(Level.OFF);
      crawljaxLogger.addAppender(appender);
    }
  }

  // Ripper configuration.
  private static String testSpec;

  private static String url;

  private static String mapFile;

  private static String efgFile;

  private static String rippingScreenshotDir;

  private static boolean isRippingScreenshotOff;

  // Test case generator configuration.
  private static boolean genTestOff;

  private static String testCaseDir;

  private static Integer testLength;

  /**
   * Number of test case to generate. 0 means generate all possible test cases.
   */
  private static Integer testNumber;

  public static void main(String[] args) throws ClassNotFoundException, ConfigurationException,
      RipperConstructionException, CrawljaxException {
    readArgs();
    runMain();
  }

  private static void runMain() throws ClassNotFoundException, ConfigurationException,
      RipperConstructionException, CrawljaxException {

    LOGGER.info("Test specification:" + testSpec);
    LOGGER.info("Entry URL:" + url);
    LOGGER.info("Output GUI map file:" + mapFile);
    LOGGER.info("Output EFG file:" + efgFile);

    // 1. Ripping
    GuitarController controller = new GuitarController(testSpec, url);

    controller.setGuiMapFile(mapFile);
    controller.setEfgFile(efgFile);
    controller.setTestCaseDir(testCaseDir);

    // Configure screenshot directory
    if (!isRippingScreenshotOff) {
      LOGGER.info("Output screenshot directory:" + rippingScreenshotDir);
      controller.setRippingScreenshotDir(rippingScreenshotDir);
    }

    controller.rip();
    // 2. Generating test cases
    if (!genTestOff) {
      LOGGER.info("Output test case directory:" + testCaseDir);
      controller.setTestCaseLength(testLength);
      controller.setTestCaseNumber(testNumber);
      controller.generateTestCases();
    }
  }

  /**
   * Reading arguments from JVM flags.
   */
  private static void readArgs() {

    // Ripper parameters.
    testSpec = getValueFromJVMFlag(GUITAR_TEST_SPECIFICATION_FLAG);
    url = getValueFromJVMFlag(GUITAR_ENTRY_URL_FLAG);
    mapFile = getValueFromJVMFlag(GUITAR_MAP_FILE_FLAG);

    efgFile = getValueFromJVMFlag(GUITAR_EFG_FILE_FLAG, false, DEFAULT_EFG_FILE);

    if (getValueFromJVMFlag(GUITAR_GEN_TEST_OFF_FLAG, false, null) != null) {
      genTestOff = true;
    } else {
      genTestOff = false;
    }

    if (getValueFromJVMFlag(GUITAR_RIPPING_SCREENSHOT_OFF_FLAG, false, null) != null) {
      isRippingScreenshotOff = true;
    } else {
      isRippingScreenshotOff = false;
    }

    rippingScreenshotDir = getValueFromJVMFlag(
        GUITAR_RIPPING_SCREENSHOT_DIR_FLAG, false, DEFAULT_RIPPING_SCREENSHOTS_DIRECTORY);

    // Test case generator parameters.
    testCaseDir =
        getValueFromJVMFlag(GUITAR_TEST_DIRECTORY_FLAG, false, DEFAULT_TEST_CASE_DIRECTORY);

    testLength =
        Integer.parseInt(getValueFromJVMFlag(GUITAR_TEST_LENGTH_FLAG, false, DEFAULT_TEST_LENGTH));

    testNumber =
        Integer.parseInt(getValueFromJVMFlag(GUITAR_TEST_NUMBER_FLAG, false, DEFAULT_TEST_NUMBER));
  }
}
