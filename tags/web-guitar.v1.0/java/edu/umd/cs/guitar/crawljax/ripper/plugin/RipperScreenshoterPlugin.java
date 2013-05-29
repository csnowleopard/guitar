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
package edu.umd.cs.guitar.crawljax.ripper.plugin;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.browser.WebDriverBackedEmbeddedBrowser;
import com.crawljax.core.state.Eventable;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.crawljax.browser.monitor.CJBrowserAfterFiringEvent;
import edu.umd.cs.guitar.crawljax.browser.monitor.CJBrowserBeforeFiringEvent;
import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.util.Screenshotter;

import java.io.File;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>j
 *
 */
public class RipperScreenshoterPlugin
    implements CJBrowserBeforeFiringEvent, CJBrowserAfterFiringEvent {

  public static final String DEFAULT_SCREENSHOT_DIR = "ripper-screenshots";
  private int counter = 0;

  private String lastEventTitle;
  private String outputDir;
  private Screenshotter screenshotter;

  /**
   * Hash code generator used to generate widget ID
   */
  private static final Logger LOGGER = Logger.getLogger(RipperScreenshoterPlugin.class);

  /**
   * @param manager
   */
  public RipperScreenshoterPlugin() {
    this.outputDir = DEFAULT_SCREENSHOT_DIR;
    this.screenshotter = new Screenshotter();
  }

  /**
   * @param outputDir the screenshotDir to set
   */
  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  /**
   * Record name of the element to click. This task need to be done before 
   * the click because the element might be disappear after the click
   */
  @Override
  public void beforeFiringEvent(Eventable event, EmbeddedBrowser embeddedBrowser) {
    try {
      WebElement webElement = embeddedBrowser.getWebElement(event.getIdentification());
      WebComponent gComponent = new WebComponent(webElement, null, null, null);
      lastEventTitle = gComponent.getTitle().replace(File.separator, "");

    } catch (NoSuchElementException exception) {
      LOGGER.debug("WebElement not found");
      lastEventTitle =null;
    }
  }

  /**
   * Capture the screenshot after the web element is clicked
   *
   */
  @Override
  public void afterFiringEvent(Eventable event, EmbeddedBrowser embeddedBrowser) {
    
    // We don't capture screenshot if the webelement is not 
    // successfully clicked
    if (lastEventTitle==null)
      return;

    WebDriver driver = ((WebDriverBackedEmbeddedBrowser) embeddedBrowser).getBrowser();
    File outputDir = new File(this.outputDir);

    if (!outputDir.exists()) outputDir.mkdirs();

    String fileName = (counter++) + "." + lastEventTitle + ".png";
    String filePath = outputDir + File.separator + fileName;
    screenshotter.caputureScreen(driver, filePath);
    LOGGER.info("Screenshot is written to: " + filePath);
  }
}
