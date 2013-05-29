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

import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.WebComponent;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * @deprecated This event is no longer used because we don't need to open link
 * in a new window
 * <p>
 * 
<<<<<<< HEAD
 * @author banguyen@google.com (Bao Nguyen)
=======
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
>>>>>>> add_cj_browser_monitor
 *
 */
@Deprecated
public class WebOpenLink implements GEvent, GEventConfigurable {

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
    WebOpenLink.domainPattern = domainPattern;
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
        } else {
          return true;
        }
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
    if (gComponent instanceof WebComponent) {
      WebElement el = ((WebComponent) gComponent).getElement();
      // do not expand the element which is not displayed
      if (!(el.isDisplayed()))
        return;
      el.click();
      
    }

  }

  @Override
  public void configure(GEventConfiguration config) {
    // Place holder
  }
}
