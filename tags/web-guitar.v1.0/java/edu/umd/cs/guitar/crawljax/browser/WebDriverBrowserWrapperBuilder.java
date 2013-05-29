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
package edu.umd.cs.guitar.crawljax.browser;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.browser.EmbeddedBrowserBuilder;
import com.crawljax.browser.WebDriverBackedEmbeddedBrowser;
import com.crawljax.core.configuration.CrawljaxConfigurationReader;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import edu.umd.cs.guitar.crawljax.browser.monitor.CJBrowserMonitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for {@link WebDriverBackedEmbeddedBrowserWraper} 
 * to provide some improved monitoring features of the browser.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 **/
public class WebDriverBrowserWrapperBuilder implements EmbeddedBrowserBuilder {


  List<CJBrowserMonitor> browserMonitorList = new ArrayList<CJBrowserMonitor>();

  /**
   * Add a new {@link CJBrowserMonitor} to the browser
   *
   * @param monitor
   *    monitor to add
   * @return the current builder
   */
  public WebDriverBrowserWrapperBuilder withMonitor(CJBrowserMonitor monitor) {
    this.browserMonitorList.add(monitor);
    return this;
  }


  /**
   * Build a new WebDriver based EmbeddedBrowser.
   *
   * @see EmbeddedBrowserBuilder#buildEmbeddedBrowser(CrawljaxConfigurationReader)
   * @param configuration the configuration object to read the config values from
   * @return the new build WebDriver based embeddedBrowser
   */
  @Override
  public EmbeddedBrowser buildEmbeddedBrowser(CrawljaxConfigurationReader configuration) {
    // Retrieve the config values used
    List<String> filterAttributes = configuration.getFilterAttributeNames();
    int crawlWaitReload = configuration.getCrawlSpecificationReader().getWaitAfterReloadUrl();
    int crawlWaitEvent = configuration.getCrawlSpecificationReader().getWaitAfterEvent();


    EmbeddedBrowser internalBrowser;
    // Determine the requested browser type
    switch (configuration.getBrowser()) {
      case firefox:
        if (configuration.getProxyConfiguration() != null) {
          FirefoxProfile profile = new FirefoxProfile();

          profile.setPreference(
              "network.proxy.http", configuration.getProxyConfiguration().getHostname());
          profile.setPreference(
              "network.proxy.http_port", configuration.getProxyConfiguration().getPort());
          profile.setPreference(
              "network.proxy.type", configuration.getProxyConfiguration().getType().toInt());
          profile.setPreference("network.proxy.no_proxies_on", "");
          internalBrowser = WebDriverBackedEmbeddedBrowser.withDriver(
              new FirefoxDriver(profile), filterAttributes, crawlWaitReload, crawlWaitEvent);
          break;
        } else {
          internalBrowser = WebDriverBackedEmbeddedBrowser.withDriver(new FirefoxDriver(),
              configuration.getFilterAttributeNames(),
              configuration.getCrawlSpecificationReader().getWaitAfterEvent(),
              configuration.getCrawlSpecificationReader().getWaitAfterReloadUrl());
          break;
        }
      case ie:

        internalBrowser = WebDriverBackedEmbeddedBrowser.withDriver(new InternetExplorerDriver(),
            configuration.getFilterAttributeNames(),
            configuration.getCrawlSpecificationReader().getWaitAfterEvent(),
            configuration.getCrawlSpecificationReader().getWaitAfterReloadUrl());
        break;
      case chrome:
        internalBrowser = WebDriverBackedEmbeddedBrowser.withDriver(new ChromeDriver(),
            configuration.getFilterAttributeNames(),
            configuration.getCrawlSpecificationReader().getWaitAfterEvent(),
            configuration.getCrawlSpecificationReader().getWaitAfterReloadUrl());
        break;
      case remote:
        internalBrowser = WebDriverBackedEmbeddedBrowser.withRemoteDriver(
            configuration.getRemoteHubUrl(), configuration.getFilterAttributeNames(),
            configuration.getCrawlSpecificationReader().getWaitAfterEvent(),
            configuration.getCrawlSpecificationReader().getWaitAfterReloadUrl());
        break;
      case htmlunit:
        internalBrowser = WebDriverBackedEmbeddedBrowser.withDriver(new HtmlUnitDriver(true),
            configuration.getFilterAttributeNames(),
            configuration.getCrawlSpecificationReader().getWaitAfterEvent(),
            configuration.getCrawlSpecificationReader().getWaitAfterReloadUrl());
        break;
      default:
        internalBrowser = WebDriverBackedEmbeddedBrowser.withDriver(new FirefoxDriver(),
            configuration.getFilterAttributeNames(),
            configuration.getCrawlSpecificationReader().getWaitAfterEvent(),
            configuration.getCrawlSpecificationReader().getWaitAfterReloadUrl());
        break;
    }

    WebDriverBackedEmbeddedBrowserWraper browserWrapper =
        new WebDriverBackedEmbeddedBrowserWraper(internalBrowser);
    for (CJBrowserMonitor monitor : browserMonitorList) {
      browserWrapper.addMonitor(monitor);
    }
    return browserWrapper;
  }
}
