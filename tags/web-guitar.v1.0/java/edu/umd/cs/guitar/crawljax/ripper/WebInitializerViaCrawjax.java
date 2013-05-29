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

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.browser.WebDriverBackedEmbeddedBrowser;
import com.crawljax.core.plugin.OnUrlLoadPlugin;
import com.crawljax.core.plugin.PreCrawlingPlugin;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import edu.umd.cs.guitar.crawljax.browser.WebDriverBackedEmbeddedBrowserWraper;
import edu.umd.cs.guitar.exception.ApplicationConnectException;
import edu.umd.cs.guitar.model.PageLoadPlugin;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class WebInitializerViaCrawjax implements PreCrawlingPlugin, OnUrlLoadPlugin {

  Logger LOGGER = Logger.getLogger(WebInitializerViaCrawjax.class);

  /**
   * @param plugin
   */
  public WebInitializerViaCrawjax(PageLoadPlugin plugin) {
    super();
    this.pageLoadMonitor = plugin;
  }

  PageLoadPlugin pageLoadMonitor;

  /**
   * Perform action before page is loaded
   */
  @Override
  public void onUrlLoad(EmbeddedBrowser browser) {
    if (browser instanceof WebDriverBackedEmbeddedBrowserWraper) {
      WebDriverBackedEmbeddedBrowserWraper wrapper = (WebDriverBackedEmbeddedBrowserWraper) browser;
      browser = wrapper.getEmbeddedBrowser();
    }

    if (!(browser instanceof WebDriverBackedEmbeddedBrowser)) {
      LOGGER.error("This plugin is only support for webdriver");
      throw new ApplicationConnectException();
    }

    WebDriverBackedEmbeddedBrowser webdirverBrowser = (WebDriverBackedEmbeddedBrowser) browser;
    WebDriver driver = webdirverBrowser.getBrowser();
    pageLoadMonitor.afterPageLoaded(driver);
  }

  @Override
  public void preCrawling(EmbeddedBrowser browser) {
    if (browser instanceof WebDriverBackedEmbeddedBrowserWraper) {
      WebDriverBackedEmbeddedBrowserWraper wrapper = (WebDriverBackedEmbeddedBrowserWraper) browser;
      browser = wrapper.getEmbeddedBrowser();
    }

    if (!(browser instanceof WebDriverBackedEmbeddedBrowser)) {
      LOGGER.error("This plugin is only support for webdriver");
      throw new ApplicationConnectException();
    }

    WebDriverBackedEmbeddedBrowser webdirverBrowser = (WebDriverBackedEmbeddedBrowser) browser;
    WebDriver driver = webdirverBrowser.getBrowser();
    pageLoadMonitor.beforePageLoaded(driver);

  }

}
