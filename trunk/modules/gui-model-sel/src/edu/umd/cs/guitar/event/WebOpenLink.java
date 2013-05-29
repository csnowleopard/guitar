package edu.umd.cs.guitar.event;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.util.GUITARLog;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.model.WebWindowManager;
import edu.umd.cs.guitar.util.Util;

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

      // if (renderedWebElement.isDisplayed()) {
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
