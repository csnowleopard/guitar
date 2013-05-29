/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland.
 * Names of owners of this group may be obtained by sending an e-mail to
 * atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.umd.cs.guitar.event;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.model.WebWindowManager;
import edu.umd.cs.guitar.util.Util;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * @deprecated This event is replaced by {@link WebClick}
 *
<<<<<<< HEAD
 * @author banguyen@google.com (Bao Nguyen)
=======
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
>>>>>>> add_cj_browser_monitor
 *
 */
@Deprecated
public class WebOpenNewInternalLink implements GEvent, GEventConfigurable {

  /**
   * Store all examined links
   */
  Set<String> examinedLinks = new HashSet<String>();

  /**
   * @return the domainPattern
   */
  public static String getDomainPattern() {
    return domainPattern;
  }

  /**
   * @param domainPattern the domainPattern to set
   */
  public static void setDomainPattern(String domainPattern) {
    WebOpenNewInternalLink.domainPattern = domainPattern;
  }

  static String domainPattern = "(" + ".*" + ")";

  @Override
  public boolean isSupportedBy(GComponent gComponent) {
    if (gComponent instanceof WebComponent) {
      WebComponent webComponent = (WebComponent) gComponent;
      WebElement renderedWebElement = webComponent.getElement();

      if ("a".equals(renderedWebElement.getTagName().toLowerCase())) {
        String href = renderedWebElement.getAttribute("href");
        if (href == null) {
          return false;
        }

        href = href.toLowerCase();
        if (examinedLinks.contains(href)) {
          return false;
        }
        examinedLinks.add(href);

        if (Util.isRegExMatched(href, domainPattern)) {
          return true;
        }
        return false;
      }
    }
    return false;
  }

  @Override
  public void perform(GComponent gComponent, List<String> parameters,
      Hashtable<String, List<String>> optionalData) {
    perform(gComponent, optionalData);
  }

  @Override
  public void perform(GComponent gComponent, Hashtable<String, List<String>> optionalData) {
    // Should do this by using WebWindowHandler, not use
    // getElement().click(), as clicking the
    // link will lead the current window away from the current page, whereas
    // we want to keep both in memory
    // smcmaster says: This is wrong; for "javascript:" URLs, click() is
    // exactly what we should to.

    if (gComponent instanceof WebComponent) {
      WebElement el = ((WebComponent) gComponent).getElement();

      // do not expand the element which is not displayed
      if (!(el.isDisplayed())) return;

      WebDriver driver = ((WebComponent) gComponent).getWindow().getDriver();

      // Check to see if this is a link of the form: <a href="URL"></a>
      if (el.getAttribute("href") != null && el.getTagName().equals("a")) {

        // New Window is opening, presumably
        String href = el.getAttribute("href");
        String finalURL = "";

        int isJavascript = 0;
        if (href.startsWith("http://") || href.startsWith("https://")) {
          finalURL = href;
        } else {
          // Must not be a full url
          String currentURL = driver.getCurrentUrl();

          if (href.startsWith("/")) {
            // Link is a direct reference from root
            int third = 7 + currentURL.substring(7).indexOf('/');
            finalURL = currentURL.substring(0, third) + href;
          } else if (href.startsWith("javascript:")) {
            el.click();
            isJavascript = 1;
          } else {
            // Not from root
            String end = currentURL.substring(0, currentURL.lastIndexOf('/') + 1);
            finalURL = end + href;
          }
        }

        // Load a new page with finalURL
        if (isJavascript == 0) {
          WebDriver rDriver = driver;
          WebWindowManager wwm = WebWindowManager.findByDriver(driver);
          if (wwm == null) {
            wwm = WebWindowManager.getInstance(rDriver);
          }
          wwm.createNewWindow(finalURL);
        }
      }
    }
  }

  @Override
  public void configure(GEventConfiguration config) {
    // Place holder
  }
}
