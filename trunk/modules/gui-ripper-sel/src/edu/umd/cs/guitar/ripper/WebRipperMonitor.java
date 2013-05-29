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
package edu.umd.cs.guitar.ripper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.WebOpenNewInternalLink;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.WebApplication;
import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.model.WebWindow;
import edu.umd.cs.guitar.model.WebWindowManager;
import edu.umd.cs.guitar.util.GUITARLog;

import java.awt.AWTException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * Monitor for the ripper to handle Web specific features
 *
 * @see GRipperMonitor
 *
 * @author Philip Anderson
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class WebRipperMonitor extends GRipperMonitor {

  /**
   * 
   */
  private int delayAfterExpanding = 1000;

  /**
   * @return the delay
   */
  public int getDelay() {
    return delayAfterExpanding;
  }



  /**
   * @param delay the delay to set
   */
  public void setDelay(int delay) {
    this.delayAfterExpanding = delay;
  }

  /**
   * Website under test
   */
  WebApplication application;

  // /**
  // * set to true if want to expand the GUI
  // */
  // boolean isExpand = true;

  private static List<Class<? extends GEvent>> expandingEventList =
      new ArrayList<Class<? extends GEvent>>();

  /**
   * @param event
   * @return
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean addExpandingEvent(Class<? extends GEvent> event) {
    return expandingEventList.add(event);
  }



  /**
   * @param index
   * @param event
   * @see java.util.List#add(int, java.lang.Object)
   */
  public void addExpandingEvent(int index, Class<? extends GEvent> event) {
    expandingEventList.add(index, event);
  }



  /**
   * @param eventList
   * @return
   * @see java.util.List#addAll(java.util.Collection)
   */
  public boolean addExpandingEventList(Collection<? extends Class<? extends GEvent>> eventList) {
    return expandingEventList.addAll(eventList);
  }

  /**
   * @param index
   * @param eventList
   * @return
   * @see java.util.List#addAll(int, java.util.Collection)
   */
  public boolean addExpandingEventList(
      int index, Collection<? extends Class<? extends GEvent>> eventList) {
    return expandingEventList.addAll(index, eventList);
  }

  /**
   * WebDriver
   */
  WebWindowManager windowManager;


  /**
   * @param application
   */
  public WebRipperMonitor(WebApplication application) {
    super();
    this.application = application;
  }

  @Override
  public void cleanUp() {
    application.disconnect();
  }

  @Override
  public void closeWindow(GWindow window) {
    if (window instanceof WebWindow) {
      WebDriver driver = application.getDriver();
      driver.switchTo().window(((WebWindow) window).getHandle());
      driver.close();
      driver.switchTo().window(windowManager.getLegalWindow());
      windowManager.close(window);
    }
  }

  WebOpenNewInternalLink event = new WebOpenNewInternalLink();

  @Override
  public synchronized void expandGUI(GComponent component) {

    if (!(component instanceof WebComponent)) return;

    GUITARLog.log.debug("Trying to explore the element");

    for (Class<? extends GEvent> event : expandingEventList) {
      try {
        GEvent action;
        action = event.newInstance();
        if (action.isSupportedBy(component)) {
          action.perform(component, new Hashtable<String, List<String>>());

          GUITARLog.log.info("Delay for " + delayAfterExpanding + " ms after expanding");
          Thread.sleep(delayAfterExpanding);
          return;
        }
      } catch (InstantiationException exception) {
        GUITARLog.log.error(exception);
      } catch (IllegalAccessException e) {
        GUITARLog.log.error(e);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * Store all examined links
   */
  Set<String> examinedLinks = new HashSet<String>();

  /* Check if the URLS are not identical, but still point to the same page */
  @Override
  public boolean isRippedWindow(GWindow window) {

    String sWindowName = window.getTitle();

    if (lRippedWindow.contains(sWindowName)) return true;

    for (String otherWindowName : lRippedWindow)
      if (areTwoUrlsTheSame(sWindowName, otherWindowName)) return true;

    return false;
  }

  /* Compares two URLs and their variables */
  private static boolean areTwoUrlsTheSame(String url1, String url2) {
    ArrayList<String> urlList1 = variablesInUrl(url1);
    ArrayList<String> urlList2 = variablesInUrl(url2);
    Collections.sort(urlList1);
    Collections.sort(urlList2);

    if (urlList1.equals(urlList2)) {
      return true;
    } else {
      return false;
    }
  }

  /* Returns a list of the variables and their values in a URL */
  private static ArrayList<String> variablesInUrl(String url) {
    String[] tempList = null;
    ArrayList<String> variableList = new ArrayList<String>();

    url = url.substring(url.indexOf('?') + 1, url.length());
    tempList = url.split("&");

    for (String str : tempList)
      variableList.add(str);

    return variableList;
  }

  @Override
  public LinkedList<GWindow> getClosedWindowCache() {
    LinkedList<GWindow> l = new LinkedList<GWindow>();
    return l;
  }

  @Override
  public LinkedList<GWindow> getOpenedWindowCache() {
    return windowManager.getOpenedWindowCache();
  }

  @Override
  public List<GWindow> getRootWindows() {
    ArrayList<GWindow> arr = new ArrayList<GWindow>();
    arr.addAll(application.getAllWindow());
    return arr;
  }

  @Override
  public boolean isExpandable(GComponent component, GWindow window) {
    if (!(component instanceof WebComponent)) return false;

    WebComponent webCompnent = (WebComponent) component;
    WebElement webElement = webCompnent.getElement();
    if (webElement == null) return false;

    if ((!webElement.isDisplayed()) || (!webElement.isEnabled())) {
      GUITARLog.log.debug(
          "Ignore expanding because the element is either not displayed nor enabled ");
      return false;
    }

    return true;
  }

  @Override
  public boolean isIgnoredWindow(GWindow window) {
    return false;
  }

  @Override
  public boolean isNewWindowOpened() {
    return !getOpenedWindowCache().isEmpty();
  }

  @Override
  public boolean isWindowClosed() {
    return false;
  }

  @Override
  public void resetWindowCache() {
    windowManager.resetWindowCache();
  }

  @Override
  public void setUp() {

    // Register window manager
    windowManager = WebWindowManager.getInstance(application.getDriver());
    application.connect();
  }

  /**
   * This is a placeholder for the selenium platform.
   */
  @Override
  public void captureImage(GComponent component, String strFilePath)
      throws AWTException, IOException {
  }

  @Override
  public void addRippedList(GWindow window) {
    String windowTitle = window.getTitle();
    this.lRippedWindow.add(windowTitle);

    // added capture image call
    try {
      if (windowTitle.endsWith("/"))
        windowTitle = windowTitle.substring(0, windowTitle.length() - 1);
      captureImage(null, windowTitle);
    } catch (AWTException e) {
      // Ignore AWT exceptions
    } catch (IOException e) {

    }
  }


  private String parseUrl(String href) {
    String finalURL = "";
    //
    if (href.startsWith("http://") || href.startsWith("https://")) {
      finalURL = href;
    } else {
      // Must not be a full url
      WebDriver driver = application.getDriver();
      String current = driver.getCurrentUrl();

      if (href.startsWith("/")) {
        // Link is a direct reference from root
        // 7 = "http://".length
        int third = 7 + current.substring(7).indexOf('/');
        finalURL = current.substring(0, third) + href;
      } else {
        // Not from root
        String end = current.substring(0, current.lastIndexOf('/') + 1);
        finalURL = end + href;
      }
    }
    return finalURL;
  }
}
