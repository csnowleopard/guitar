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
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.configuration.CrawljaxConfigurationReader;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.Identification;
import com.crawljax.forms.FormInput;

import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.crawljax.browser.monitor.CJBrowserAfterFiringEvent;
import edu.umd.cs.guitar.crawljax.browser.monitor.CJBrowserBeforeFiringEvent;
import edu.umd.cs.guitar.crawljax.browser.monitor.CJBrowserMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for {@link EmbeddedBrowser}
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class WebDriverBackedEmbeddedBrowserWraper implements EmbeddedBrowser {


  List<CJBrowserMonitor> browserMonitorList = new ArrayList<CJBrowserMonitor>();

  /**
   * Add a new monitor to the browser
   *
   * @param monitor
   *    the monitor to add 
   * 
   * @return
   *    true if successfully added
   */
  public boolean addMonitor(CJBrowserMonitor monitor) {
    return browserMonitorList.add(monitor);
  }


  /**
   * @param monitor
   * the monitor to remove
   * @return
   *    true if successfully removed
   */
  public boolean removeMontior(Object monitor) {
    return browserMonitorList.remove(monitor);
  }



  /**
   * @return the embeddedBrowser
   */
  public EmbeddedBrowser getEmbeddedBrowser() {
    return embeddedBrowser;
  }



  EmbeddedBrowser embeddedBrowser;

  /**
   * @param embeddedBrowser
   *    internal browser
   */
  public WebDriverBackedEmbeddedBrowserWraper(EmbeddedBrowser embeddedBrowser) {
    super();
    this.embeddedBrowser = embeddedBrowser;
  }


  /**
   * @see com.crawljax.browser.EmbeddedBrowser#close()
   */
  @Override
  public void close() {
    embeddedBrowser.close();
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#closeOtherWindows()
   */
  @Override
  public void closeOtherWindows() {
    embeddedBrowser.closeOtherWindows();
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#elementExists(com.crawljax.core.state
   * .Identification)
   */
  @Override
  public boolean elementExists(Identification identification) {
    return embeddedBrowser.elementExists(identification);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#executeJavaScript(java.lang.String)
   */
  @Override
  public Object executeJavaScript(String script) throws CrawljaxException {
    return embeddedBrowser.executeJavaScript(script);
  }

  /**
   * Run the monitors before and after any event is fired
   */
  @Override
  public boolean fireEvent(Eventable event) {
    for (CJBrowserMonitor monitor : browserMonitorList) {
      if (monitor instanceof CJBrowserBeforeFiringEvent) {
        CJBrowserBeforeFiringEvent beforeFiringEventMonitor = (CJBrowserBeforeFiringEvent) monitor;
        beforeFiringEventMonitor.beforeFiringEvent(event, embeddedBrowser);
      }
    }

    boolean firingResult = embeddedBrowser.fireEvent(event);

    for (CJBrowserMonitor monitor : browserMonitorList) {
      if (monitor instanceof CJBrowserAfterFiringEvent) {
        CJBrowserAfterFiringEvent afterFiringEventMonitor = (CJBrowserAfterFiringEvent) monitor;
        afterFiringEventMonitor.afterFiringEvent(event, embeddedBrowser);
      }
    }
    return firingResult;
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#getCurrentUrl()
   */
  @Override
  public String getCurrentUrl() {
    return embeddedBrowser.getCurrentUrl();
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#getDom()
   */
  @Override
  public String getDom() {
    return embeddedBrowser.getDom();
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#getDomWithoutIframeContent()
   */
  @Override
  public String getDomWithoutIframeContent() {
    return embeddedBrowser.getDomWithoutIframeContent();
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#getFrameDom(java.lang.String)
   */
  @Override
  public String getFrameDom(String iframeIdentification) {
    return embeddedBrowser.getFrameDom(iframeIdentification);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#getInputWithRandomValue(com.crawljax
   * .forms.FormInput)
   */
  @Override
  public FormInput getInputWithRandomValue(FormInput inputForm) {
    return embeddedBrowser.getInputWithRandomValue(inputForm);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#getWebElement(com.crawljax.core.state
   * .Identification)
   */
  @Override
  public WebElement getWebElement(Identification identification) {
    return embeddedBrowser.getWebElement(identification);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#goBack()
   */
  @Override
  public void goBack() {
    embeddedBrowser.goBack();
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#goToUrl(java.lang.String)
   */
  @Override
  public void goToUrl(String url) {
    embeddedBrowser.goToUrl(url);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#input(com.crawljax.core.state.Identification,
   * java.lang.String)
   */
  @Override
  public boolean input(Identification identification, String text) throws CrawljaxException {
    return embeddedBrowser.input(identification, text);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#isVisible(com.crawljax.core.state .Identification)
   */
  @Override
  public boolean isVisible(Identification identification) {
    return embeddedBrowser.isVisible(identification);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#saveScreenShot(java.io.File)
   */
  @Override
  public void saveScreenShot(File file) throws CrawljaxException {
    embeddedBrowser.saveScreenShot(file);
  }

  /**
   * @see com.crawljax.browser.EmbeddedBrowser#updateConfiguration(com.crawljax.
   * core.configuration.CrawljaxConfigurationReader)
   */
  @Override
  public void updateConfiguration(CrawljaxConfigurationReader configuration) {
    embeddedBrowser.updateConfiguration(configuration);
  }

}
