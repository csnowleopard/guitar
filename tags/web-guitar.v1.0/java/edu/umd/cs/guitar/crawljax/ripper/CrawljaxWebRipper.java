/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of
 * this group may be obtained by sending an e-mail to atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
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
package edu.umd.cs.guitar.crawljax.ripper;

import com.crawljax.core.CrawljaxController;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;

import edu.umd.cs.guitar.crawljax.browser.WebDriverBrowserWrapperBuilder;
import edu.umd.cs.guitar.crawljax.model.EventSpecification;
import edu.umd.cs.guitar.crawljax.model.GuitarEventManager;
import edu.umd.cs.guitar.crawljax.model.RippingResult;
import edu.umd.cs.guitar.crawljax.ripper.plugin.GUITARWebEventMonitorPlugin;
import edu.umd.cs.guitar.crawljax.ripper.plugin.RipperScreenshoterPlugin;

import org.apache.commons.configuration.ConfigurationException;

/**
 * Ripper object using Crawljax as internal the automation engine.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a> 
 */
public class CrawljaxWebRipper {

  private RippingSpecification rippingSpecification;
  private CrawljaxConfiguration crawljaxConfiguration;

  private boolean isCaptureScreenshot = false;
  private String screenshotDir = ".";

  /**
   * Enables/disables screenshot capturing.
   *
   * @param isCaptureScreenshot the isCaptureScreenshot to set
   */
  public void setCaptureScreenshot(boolean isCaptureScreenshot) {
    this.isCaptureScreenshot = isCaptureScreenshot;
  }

  public CrawljaxWebRipper(
      RippingSpecification rippingSpecification, CrawljaxConfiguration crawljaxConfiguration) {
    super();
    this.rippingSpecification = rippingSpecification;
    this.crawljaxConfiguration = crawljaxConfiguration;
  }

  /**
   * Starts ripping.
   */
  public RippingResult rip() throws ConfigurationException, CrawljaxException {

    // Crawl specification.
    CrawlSpecification crawlSpecification = rippingSpecification.getCrawljaxSpecification();
    crawljaxConfiguration.setCrawlSpecification(crawlSpecification);

    // Record events specification.
    GuitarEventManager guitarEventManager = rippingSpecification.getGuitarEventManager();
    WebDriverBrowserWrapperBuilder browserBuilder = new WebDriverBrowserWrapperBuilder();

    GUITARWebEventMonitorPlugin guitarEventMonitor =
        new GUITARWebEventMonitorPlugin(guitarEventManager);
    crawljaxConfiguration.addPlugin(guitarEventMonitor);
    browserBuilder.withMonitor(guitarEventMonitor);

    // Add expanding event specification to the guitarEventMonitor.
    EventSpecification expandingEventSpec = new EventSpecification(crawlSpecification);
    guitarEventManager.registerEvent(GuitarEventManager.EXPANDING_EVENT, expandingEventSpec);

    if (isCaptureScreenshot && screenshotDir != null) {
      RipperScreenshoterPlugin screenCapturePlugin = new RipperScreenshoterPlugin();
      screenCapturePlugin.setOutputDir(screenshotDir);
      browserBuilder.withMonitor(screenCapturePlugin);
    }
    crawljaxConfiguration.setBrowserBuilder(browserBuilder);

    // Create a crawjax controller and run.
    this.crawljaxConfiguration.setCrawlSpecification(crawlSpecification);

    CrawljaxController crawljax = new CrawljaxController(crawljaxConfiguration);
    crawljax.run();
    return guitarEventMonitor.getRippingResult();
  }

  protected void setScreenshotDir(String screenshotDir) {
    this.screenshotDir = screenshotDir;
  }
}
