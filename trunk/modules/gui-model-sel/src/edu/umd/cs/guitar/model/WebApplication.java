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
package edu.umd.cs.guitar.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import edu.umd.cs.guitar.util.GUITARLog;

import edu.umd.cs.guitar.exception.ApplicationConnectException;

/**
 * Implementation for {@link GApplication} for Web
 *
 * @see GApplication
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class WebApplication extends GApplication {
  String rootURL;
  WebDriver driver;
  WebWindowManager windowManager;
  
  List<GApplicationPlugin> pluginList;
  
  
  
  /**
   * @param rootURL
   * @param driver
   */
  public WebApplication(String rootURL, WebDriver driver) {
    super();
    this.rootURL = rootURL;
    this.driver = driver;
    this.windowManager = WebWindowManager.getInstance( driver);
    this.pluginList = new ArrayList<GApplicationPlugin>(); 
  }

  /**
   * @param rootURL
   */
  public WebApplication(String rootURL) {
    this(rootURL, new FirefoxDriver());
  }
  
  @Override
  public void connect() throws ApplicationConnectException {
    for(GApplicationPlugin plugin: pluginList){
      if (plugin instanceof GApplicationBeforeConnectPlugin) {
        GUITARLog.log.debug("Performing before connection action(s)...");
        GApplicationBeforeConnectPlugin beforeConnectPlugin 
        = (GApplicationBeforeConnectPlugin)plugin;
       beforeConnectPlugin.beforeConnect(this);
      }
    }
    
    windowManager.createNewWindow(rootURL);
    
    for(GApplicationPlugin plugin: pluginList){
      if (plugin instanceof GApplicationBeforeConnectPlugin) {
        GUITARLog.log.debug("Performing after connection action(s)...");
        GApplicationAfterConnectPlugin afterConnectPlugin 
        = (GApplicationAfterConnectPlugin )plugin;
        afterConnectPlugin.afterConnect(this);
      }
    }
  }

  /**
   * @return the rootURL
   */
  public String getRootURL() {
    return rootURL;
  }

  /**
   * @param rootURL the rootURL to set
   */
  public void setRootURL(String rootURL) {
    this.rootURL = rootURL;
  }

  @Override
  public void connect(String[] args) throws ApplicationConnectException {
    connect();
  }

  @Override
  public Set<GWindow> getAllWindow() {
    Set<GWindow> s = new HashSet<GWindow>();
    for (String handler : driver.getWindowHandles()) {
      // ignore the staring page
      if (!(handler.equals(windowManager.startPageHanlder))) {
        GWindow webWindow = new WebWindow((WebDriver) driver, handler);
        s.add(webWindow);
      }
    }
    return s;
  }

  /**
   * @return the driver
   */
  public WebDriver getDriver() {
    return driver;
  }

  /**
   * @param driver the driver to set
   */
  public void setDriver(WebDriver driver) {
    this.driver = driver;
  }

  /**
   * TODO: Move this method to the APIs
   */
  public void disconnect() {
    driver.quit();
  }

  public GWindow getWindowByURL(String sWindowTitle) {
    for (GWindow w : getAllWindow()) {
      if (w.getTitle().equals(sWindowTitle)) return w;
    }

    return null;
  }
}
