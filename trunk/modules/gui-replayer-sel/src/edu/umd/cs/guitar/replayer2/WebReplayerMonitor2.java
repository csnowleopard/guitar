/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland.
 * Names of owners of this group may be obtained by sending an e-mail to
 * atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
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
package edu.umd.cs.guitar.replayer2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.replayer.WebReplayerMonitor;

import edu.umd.cs.guitar.replayer2.GReplayerMonitor2;

import edu.umd.cs.guitar.exception.ReplayerConstructionException;

import edu.umd.cs.guitar.model.GApplication;

import edu.umd.cs.guitar.model.WebComponent;

import edu.umd.cs.guitar.util.GUITARLog;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.WebApplication;
import edu.umd.cs.guitar.model.WebConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An improved version of {@link WebReplayerMonitor}
 * <p>
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class WebReplayerMonitor2 extends GReplayerMonitor2 {

  boolean isSearchWithinWindow = false;
  /**
   * Turn on this flag to shutdown the application after 
   * executing test case
   */
  boolean isShutDownAfterTest = true;
  

  /**
   * @return the isShutDownAfterTest
   */
  public boolean isShutDownAfterTest() {
    return isShutDownAfterTest;
  }

  /**
   * @param isShutDownAfterTest the isShutDownAfterTest to set
   */
  public void setShutDownAfterTest(boolean isShutDownAfterTest) {
    this.isShutDownAfterTest = isShutDownAfterTest;
  }


  /**
   * Get component using their GUI identification properties
   * <p>
   *
   * @return the component if found and null if not found
   */
  @Override
  public GComponent getComponent(GApplication application, ComponentType window, ComponentType component) {
    if (!(application instanceof WebApplication)) throw new ReplayerConstructionException();

    WebApplication webApplication = (WebApplication) application;
    WebDriver driver = webApplication.getDriver();

    String xpathExpression = getXpathFromComponent(component);

    GUITARLog.log.debug("xPath Expression: " + xpathExpression);


    String currentHandler = driver.getWindowHandle();
    driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    
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

        WebElement element = driver.findElement(By.xpath(xpathExpression));
        GComponent webComponent = new WebComponent(element, null, null, null);

        GUITARLog.log.debug("Elemement FOUND in: " + driver.getCurrentUrl());
        return webComponent;

      } catch (org.openqa.selenium.NoSuchElementException e) {
        GUITARLog.log.debug("Elemement NOT found in: " + driver.getCurrentUrl());
      }
    }
    
    driver.switchTo().window(currentHandler);
    return null;
  }


  /**
   * @param url
   * @return
   */
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
   * get xpath expression from component information
   *
   * @param component
   * @return
   */
  private String getXpathFromComponent(ComponentType component) {
    StringBuffer xPath = new StringBuffer();
    ComponentTypeWrapper componentWrapper = new ComponentTypeWrapper(component);
    String tag = componentWrapper.getFirstValueByName(GUITARConstants.CLASS_TAG_NAME);

    if (tag == null || "".equals(tag.trim())) tag = "*";
    xPath.append("//" + tag);

    StringBuffer attributeList = new StringBuffer();
    boolean isFirstAtrribute = true;
    for (String idProperty : WebConstants.ID_ATTRIBUTE_LIST) {
      String value = componentWrapper.getFirstValueByName(idProperty);

      if (value != null && !("".equals(value.trim()))) {
        if (!isFirstAtrribute) {
          attributeList.append(" and ");
        } else {
          isFirstAtrribute = false;
        }
        attributeList.append("@" + idProperty + " = '" + value + "'");
      }
    }

    // add text if needed
    String text = componentWrapper.getFirstValueByName(WebConstants.TEXT_TAG);
    

    if (text != null && !("".equals(text.trim()))) {
      if (!isFirstAtrribute) 
        attributeList.append(" and ");
      attributeList.append(" text()= '" + text + "'");
    }

    xPath.append("[");
    xPath.append(attributeList);
    xPath.append("]");
    return xPath.toString();
  }


  /*
   * (non-Javadoc)
   *
   * @see edu.umd.cs.guitar.replayer.GReplayerMonitor#delay(int)
   */
  @Override
  public void delay(int delay) {

  }


//  /*
//   * (non-Javadoc)
//   *
//   * @see edu.umd.cs.guitar.replayer.GReplayerMonitor#setUp()
//   */
//  @Override
//  public void setUp() {
//    // TODO(banguyen): Auto-generated method stub
//
//  }
//
//
//  /*
//   * (non-Javadoc)
//   *
//   * @see edu.umd.cs.guitar.replayer.GReplayerMonitor#cleanUp()
//   */
//  @Override
//  public void cleanUp() {
//    if((application instanceof WebApplication)){
//      ((WebApplication) application).disconnect();  
//    }
//    
//  }


}
