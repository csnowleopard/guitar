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


import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * JUnit-based Runner to run GUITAR's test cases.
 *
 * TODO(banguyen): Add java doc.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
@RunWith(Parameterized.class)
public class GuitarJUnitRunner extends GuitarRunner {

  private static Logger LOGGER = Logger.getLogger(GuitarJUnitRunner.class);
  private static GuitarController runner;

  private final String testCaseFileName;

  /**
   * Setup log4j to printout the right message.
   *
   *  TODO(banguyen): Figure out a way to make this configuration work via an external log.property
   * file
   */
  static {
    Logger root = Logger.getRootLogger();
    if (!root.getAllAppenders().hasMoreElements()) {
      Appender appender =
          new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));

      Logger guitarLogger = Logger.getLogger("edu.umd.cs.guitar");
      guitarLogger.setLevel(Level.INFO);
      guitarLogger.addAppender(appender);

      Logger crawljaxLogger = Logger.getLogger("com.crawljax");
      crawljaxLogger.setLevel(Level.OFF);
      crawljaxLogger.addAppender(appender);
    }
  }

  /**
   * {@link org.junit.rules.TestRule} performing logging and export information.
   */
  @Rule
  public final LogCollectionRule logging = new LogCollectionRule();

  /**
   * Generate parameterized data.
   *
   * @throws ClassNotFoundException
   */
  @Parameters
  public static Collection<Object[]> generateData() throws ClassNotFoundException {
    String testDirPath = getValueFromJVMFlag(GUITAR_TEST_DIRECTORY_FLAG);

    File testdir = new File(testDirPath);
    if (!testdir.exists()) {
      throw new IllegalArgumentException(
          "Need to specify test case directory using jvm paramter -D" + GUITAR_TEST_DIRECTORY_FLAG);
    }

    String testSpec = getValueFromJVMFlag(GUITAR_TEST_SPECIFICATION_FLAG);
    LOGGER.info("Test specification: " + testSpec);

    String entryUrl = getValueFromJVMFlag(GUITAR_ENTRY_URL_FLAG);
    LOGGER.info("Entry URL: " + entryUrl);

    runner = new GuitarController(testSpec, entryUrl);

    String guiMapFile = getValueFromJVMFlag(GUITAR_MAP_FILE_FLAG);
    runner.setGuiMapFile(guiMapFile);

    LOGGER.info("Reading test case files from " + testDirPath + "...");

    List<Object[]> parameterList = new ArrayList<Object[]>();
    File[] testCaseFileList = testdir.listFiles();

    if (getValueFromJVMFlag(GUITAR_TEST_RANDOMIZED_FLAG, false, null) != null) {
      Collections.shuffle(Arrays.asList(testCaseFileList));
    }

    int testNumber = Integer.parseInt(getValueFromJVMFlag(GUITAR_TEST_NUMBER_FLAG, false, "0"));
    if (testNumber == 0) {
      testNumber = testCaseFileList.length;
    }

    for (int i = 0; i < testCaseFileList.length && i < testNumber; i++) {
      File testCaseFile = testCaseFileList[i];
      String testCaseFilePath = testCaseFile.getAbsolutePath();
      parameterList.add(new Object[] {testCaseFilePath});
    }
    return parameterList;
  }

  /**
   * @param testCaseFileName
   */
  public GuitarJUnitRunner(String testCaseFileName) {
    this.testCaseFileName = testCaseFileName;
  }

  @Test
  public void testSingleRun() {
    LOGGER.info("Test case: " + testCaseFileName);
    runner.replaySingleTestCase(this.testCaseFileName);
  }

}
