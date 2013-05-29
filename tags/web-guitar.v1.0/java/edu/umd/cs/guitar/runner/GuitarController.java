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

import edu.umd.cs.guitar.crawljax.model.RippingResult;
import edu.umd.cs.guitar.crawljax.ripper.CrawljaxWebRipper;
import edu.umd.cs.guitar.crawljax.ripper.CrawljaxWebRipperBuilder;
import edu.umd.cs.guitar.crawljax.ripper.RippingSpecification;
import edu.umd.cs.guitar.exception.RipperConstructionException;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.PageLoadPlugin;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.plugin.GPlugin;
import edu.umd.cs.guitar.replayer2.Replayer2;
import edu.umd.cs.guitar.replayer2.WebReplayer2Builder;
import edu.umd.cs.guitar.testcase.TestCaseGenerator;

import org.apache.commons.configuration.ConfigurationException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * General controller for Guitar.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class GuitarController {

  private static final int DEFAULT_TEST_CASE_LENGTH = 2;

  private static final int DEFAULT_NUMBER_OF_TEST_CASE_GENERATED = 0;

  private static final String DEFAULT_RIPPING_SCREENSHOTS_DIRECTORY =
      "." + File.separator + "ripping-screenshots";

  private String entryUrl;
  private RippingSpecification rippingSpecification;
  private PageLoadPlugin pageLoadPlugin;
  private List<GPlugin> replayerPluginList;

  private boolean isCaptureScreenshotDuringRipping = false;
  private String rippingScreenshotDir = null;

  private GUIMap guiMap;
  private EFG efg;

  private String guiMapFile;
  private String efgFile;

  private Integer testCaseLength = DEFAULT_TEST_CASE_LENGTH;

  /**
   * Specify the maximum number of test case generated. 0 means generate all possible test cases
   */
  private int testCaseNumber = DEFAULT_NUMBER_OF_TEST_CASE_GENERATED;

  private String testCaseDir;

  public GuitarController(final String guitarSpecification, final String entryUrl)
      throws ClassNotFoundException {
    this(Class.forName(guitarSpecification), entryUrl);
  }

  public GuitarController(final Class<?> guitarSpecification, final String entryUrl) {
    this.entryUrl = entryUrl;

    GuitarSpecificationParser parser = new GuitarSpecificationParser(guitarSpecification);
    this.rippingSpecification = parser.getRippingSpecification();
    this.pageLoadPlugin = parser.getPageLoadPlugin();
    this.replayerPluginList = parser.getReplayerPluginList();
  }

  public void runFullProcess()
      throws RipperConstructionException, ConfigurationException, CrawljaxException {
    // 1. Ripping the Web
    rip();
    // 2. Systematically generate test cases
    generateTestCases();
    // 3. Automatically replay generated test cases
    replayAll();
  }

  public void rip() throws RipperConstructionException, ConfigurationException, CrawljaxException {
    if (entryUrl == null) {
      throw new IllegalArgumentException("Need to provide an entry URL");
    }

    if (guiMapFile == null) {
      throw new IllegalArgumentException("Need to provide a GUI Map file name");
    }

    if (efgFile == null) {
      throw new IllegalArgumentException("Need to provide a EFG file name");
    }

    if (rippingSpecification == null) {
      throw new IllegalArgumentException("Need to provide a Ripping specification");
    }

    CrawljaxWebRipperBuilder builder = new CrawljaxWebRipperBuilder(rippingSpecification);
    if (pageLoadPlugin != null) {
      builder = builder.withPageLoadPlugin(pageLoadPlugin);
    }

    // Capture screenshot if (1) the rippingScreenshotDir is set
    // or (2) the isCaptureScreenshotDuringRipping is enable.
    if (rippingScreenshotDir != null) {
      builder = builder.captureScreenshot(true)
                       .withScreenshotDir(rippingScreenshotDir);
      
    } else if (isCaptureScreenshotDuringRipping) {
      builder = builder.captureScreenshot(true)
                       .withScreenshotDir(DEFAULT_RIPPING_SCREENSHOTS_DIRECTORY);
    }

    CrawljaxWebRipper ripper = builder.withEntryUrl(entryUrl).build();

    // ==============================
    // Start ripping
    // ==============================
    RippingResult result = ripper.rip();

    // ==============================
    // Get and write results to file
    // ==============================
    guiMap = result.getGuiMap();
    IO.writeObjToFile(guiMap, guiMapFile);

    efg = result.getEFG();
    IO.writeObjToFile(efg, efgFile);
  }

  public void generateTestCases() {
    if (testCaseDir == null) {
      throw new IllegalArgumentException("Need to provide an output test case directory");
    }

    if (guiMap == null || efg == null) {
      throw new IllegalArgumentException("Need to rip application before generating test cases");
    }

    System.out.println("===========================");
    System.out.println("START TEST CASE GENERATION....");
    System.out.println("===========================");

    File testdir = new File(testCaseDir);
    // Launch
    TestCaseGenerator.main(new String[] 
        {"-p", "SequenceLengthCoverageWebExt",
        "-e", efgFile,
        "-d", testCaseDir,
        "-l", Integer.toString(testCaseLength),
        "-m", Integer.toString(testCaseNumber)});
    System.out.println("===========================");
    System.out.println("Test case dir: " + testCaseDir);
    System.out.println("DONE TEST CASE GENERATION!!!");
    System.out.println("===========================");
  }

  /**
   * Replays all generated test cases.
   */
  public void replayAll() {
    File testcaseDir = new File(testCaseDir);
    File[] testcases = testcaseDir.listFiles();
    replayTopTests(testcases.length, true);
  }

  /**
   * Replays top k generated test cases.
   *
   * @param k number of test case to replay.
   * @param randomized <code>true</code> if replay in a random order
   */
  public void replayTopTests(int k, boolean randomized) {
    System.out.println("===========================");
    System.out.println("START REPLAYING....");
    System.out.println("===========================");

    File testcaseDir = new File(testCaseDir);
    File[] testcases = testcaseDir.listFiles();

    if (randomized) {
      Collections.shuffle(Arrays.asList(testcases));
    }
    for (int i = 0; i < k && i < testcases.length; i++) {

      File testcase = testcases[i];
      String testcasePath = testcase.getAbsolutePath();
      replaySingleTestCase(testcasePath);
    }

    System.out.println("===========================");
    System.out.println("DONE REPLAYING!!!");
    System.out.println("===========================");
  }

  /**
   * Replay a single test case.
   *
   * @param testcaseFileName test case file to run.
   */
  public void replaySingleTestCase(String testcaseFileName) {
    if (this.entryUrl == null) {
      throw new IllegalArgumentException("Need to supply entry URL");
    }
    if (this.guiMapFile == null) {
      throw new IllegalArgumentException("Need to supply GUI Map file");
    }

    Replayer2 replayer = null;
    WebReplayer2Builder builder = new WebReplayer2Builder(entryUrl).withGUIMap(guiMapFile);
    if (pageLoadPlugin != null) {
      builder = builder.withWebInitializer(pageLoadPlugin);
    }

    for (GPlugin plugin : replayerPluginList) {
      builder = builder.addPlugin(plugin);
    }

    replayer = builder.build();
    replayer.execute(testcaseFileName);
  }

  public void setEntryUrl(String entryUrl) {
    this.entryUrl = entryUrl;
  }

  public void setGuiMapFile(String guiMapFile) {
    this.guiMapFile = guiMapFile;
  }

  public void setEfgFile(String efgFile) {
    this.efgFile = efgFile;
  }

  public void setTestCaseLength(Integer testCaseLength) {
    this.testCaseLength = testCaseLength;
  }

  public void setTestCaseNumber(int testCaseNumber) {
    this.testCaseNumber = testCaseNumber;
  }

  public void setTestCaseDir(String testCaseDir) {
    this.testCaseDir = testCaseDir;
  }

  public void setCaptureScreenshotDuringRipping(boolean isCaptureScreenshotDuringRipping) {
    this.isCaptureScreenshotDuringRipping = isCaptureScreenshotDuringRipping;
  }

  public void setRippingScreenshotDir(String rippingScreenshotDir) {
    this.rippingScreenshotDir = rippingScreenshotDir;
  }

}
