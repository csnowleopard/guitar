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
package edu.umd.cs.guitar.replayer2;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.exception.ReplayerConstructionException;
import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.WebApplication;
import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.model.WebConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class WebReplayerMonitor2 extends GReplayerMonitor2 {


  private Logger LOGGER = Logger.getLogger(WebReplayerMonitor2.class);

  boolean isSearchWithinWindow = false;


  /**
   * Get component using their GUI identification properties.
   *
   * Algorithm description:
   * <p>
   *
   * The pair <code><tag, id_property> </code> is used as GUI component identifier, where <code> id_property
   * </code> is a value declared in {@link edu.umd.cs.guitar.model.WebConstants.ID_ATTRIBUTE_LIST}.
   *
   * <p>
   * The monitor scans through all pairs <code><tag, id_property> </code> to find the one which
   * return the unique GUI element. If such element is not found then the monitor relies on the pair
   * <code><tag, text> </code> for element identifying
   *
   * TODO (banguyen): For most of the time, this algorithm can return the desired element. However,
   * the current heuristics used is not always correct. Ideally we need to have a configurable
   * identifier, in which the user can define the algorithm to identify the components in their web
   *
   * @return the component if found and null if not found
   */
  @SuppressWarnings("javadoc")
  @Override
  public GComponent getComponent(
      GApplication application, ComponentType window, ComponentType component) {
    if (!(application instanceof WebApplication)) throw new ReplayerConstructionException();

    WebApplication webApplication = (WebApplication) application;
    WebDriver driver = webApplication.getDriver();

    String currentHandler = driver.getWindowHandle();
    List<String> IDProperties = new ArrayList<String>();
    IDProperties.addAll(Arrays.asList(WebConstants.ID_ATTRIBUTE_LIST));

    // Scan all open window for the desired element
    for (String windowHandler : driver.getWindowHandles()) {
      try {
        driver.switchTo().window(windowHandler);

        if (isSearchWithinWindow) {
          ComponentType windowComponent = window;
          ComponentTypeWrapper windowComponentWrapper = new ComponentTypeWrapper(windowComponent);
          String windowTitle =
              windowComponentWrapper.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
          windowTitle = normalizeURL(windowTitle);

          String url = driver.getCurrentUrl();
          url = normalizeURL(url);
          if (!windowTitle.equals(url)) {
            continue;
          }
        }

        // Find the elements matching tag
        ComponentTypeWrapper componentWrapper = new ComponentTypeWrapper(component);
        String tag = componentWrapper.getFirstValueByName(WebConstants.TAG_NAME);

        // ----------------------
        // Search by id properties
        for (String attribute : IDProperties) {
          String value = componentWrapper.getFirstValueByName(attribute);

          if (value == null || "".equals(value.trim())) {
            continue;
          }

          String xpath = "//" + tag + "[" + "@" + attribute + " = '" + value + "']";

          LOGGER.debug("Xpath: " + xpath);

          List<WebElement> candidateElements = driver.findElements(By.xpath(xpath));

          // remove invisible and disabled elements
          List<WebElement> interacableElementList = new ArrayList<WebElement>();
          for (WebElement element : candidateElements) {
            if (element.isDisplayed() && element.isEnabled()) interacableElementList.add(element);
          }
          candidateElements = interacableElementList;

          if (candidateElements.size() == 1) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("ID property found");
              LOGGER.debug("\tName: " + attribute);
              LOGGER.debug("\tValue: " + value);
            }
            WebElement element = candidateElements.get(0);
            GComponent webComponent = new WebComponent(element, null, null, null);
            return webComponent;
          }
        }

        // ----------------------
        // By cssSelector
        String classValueList = componentWrapper.getFirstValueByName("class");
        if (classValueList != null) {
          List<WebElement> elementList;
          for (String classValue : classValueList.split("\\s+")) {
            String cssSelector = tag + "." + classValue;
            LOGGER.debug("\tWith cssSelector: " + cssSelector);
            elementList = driver.findElements(By.cssSelector(cssSelector));
            if (elementList.size() == 1) {
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Element found");
                LOGGER.debug("\tClass: " + classValue);
              }
              WebElement element = elementList.get(0);
              GComponent webComponent = new WebComponent(element, null, null, null);
              return webComponent;
            }
          }
        }

        
        // ----------------------
        // Search by Text
        String textValue = componentWrapper.getFirstValueByName(WebConstants.TEXT_TAG);
        if (!(textValue == null || "".equals(textValue.trim()))) {
          LOGGER.debug("Searching element by text: " + textValue);
          List<WebElement> elementList;

          // ----------------------
          // With cssSelector
          if (classValueList != null) {
            for (String classValue : classValueList.split("\\s+")) {
              String cssSelector = tag + "." + classValue;
              LOGGER.debug("\tWith cssSelector: " + cssSelector);
              elementList = driver.findElements(By.cssSelector(cssSelector));
              elementList = filterInteractableElementByText(elementList, textValue);
              if (elementList.size() == 1) {
                if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("Element found");
                  LOGGER.debug("\tText: " + textValue);
                }
                WebElement element = elementList.get(0);
                GComponent webComponent = new WebComponent(element, null, null, null);
                return webComponent;
              }
            }
          }

          // ----------------------
          // With Tag
          LOGGER.debug("\tWith tag: " + tag);
          elementList = driver.findElements(By.tagName(tag));
          elementList = filterInteractableElementByText(elementList, textValue);

          if (elementList.size() == 1) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("Element found");
              LOGGER.debug("\tText: " + textValue);
            }
            WebElement element = elementList.get(0);
            GComponent webComponent = new WebComponent(element, null, null, null);
            return webComponent;
          }

        }
      } catch (org.openqa.selenium.NoSuchElementException e) {
        LOGGER.debug("Elemement NOT found in: " + driver.getCurrentUrl());
      }
    }

    driver.switchTo().window(currentHandler);
    return null;
  }

  private List<WebElement> filterInteractableElementByText(
      List<WebElement> elementList, String text) {
    List<WebElement> candidateElement = new ArrayList<WebElement>();

    List<WebElement> interacableElementList = new ArrayList<WebElement>();
    for (WebElement element : elementList) {
      if (element.isDisplayed() && element.isEnabled()) interacableElementList.add(element);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Finding element with: ");
      LOGGER.debug("\tText:* " + text + "*");
      LOGGER.debug("\tTotal Element to examine: " + elementList.size());
    }
    if (text == null) {
      return candidateElement;
    }

    for (WebElement element : interacableElementList) {
      WebComponent examinedComponent = new WebComponent(element, null, null, null);
      String elementText = examinedComponent.getText();
      if ("".equals(elementText)) continue;
      if (text.equals(elementText)) {
        candidateElement.add(element);
      }
    }
    return candidateElement;
  }

  private String normalizeURL(String url) {
    String normalizeURL = url.replaceAll("/|http|https", "");
    return normalizeURL;
  }

  /**
   * @return the isSearchInWindow
   */
  public boolean isSearchInWindow() {
    return isSearchWithinWindow;
  }

  /**
   * @param isSearchInWindow the isSearchInWindow to set
   */
  public void setSearchInWindow(boolean isSearchInWindow) {
    this.isSearchWithinWindow = isSearchInWindow;
  }

  /**
   * Delay the browser.
   *
   * @param milliseconds time to delay.
   */
  @Override
  public void delay(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      LOGGER.error("Unable to delay");
    }
  }
}
