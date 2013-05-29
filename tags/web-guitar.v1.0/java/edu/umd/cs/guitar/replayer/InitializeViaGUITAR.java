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

package edu.umd.cs.guitar.replayer;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import edu.umd.cs.guitar.exception.ReplayerConstructionException;
import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.WebApplication;
import edu.umd.cs.guitar.model.PageLoadPlugin;
import edu.umd.cs.guitar.model.plugin.GAfterApplicationStart;
import edu.umd.cs.guitar.model.plugin.GBeforeApplicationStart;

/**
 * Initialize the web site via GUITAR's tool
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class InitializeViaGUITAR implements GBeforeApplicationStart, GAfterApplicationStart {

  Logger LOGGER = Logger.getLogger(InitializeViaGUITAR.class);
  PageLoadPlugin initializer;


  /**
   * @param initializer
   */
  public InitializeViaGUITAR(PageLoadPlugin initializer) {
    super();
    this.initializer = initializer;
  }


  /**
   * Action perform before web site is loaded
   */
  @Override
  public void beforeConnection(GApplication application) {
    if (!(application instanceof WebApplication)) {
      throw new ReplayerConstructionException("Cannot login with a non web application");
    }
    WebApplication webApplication = (WebApplication) application;
    WebDriver driver = webApplication.getDriver();
    initializer.beforePageLoaded(driver);
  }


  /**
   * Action perform after web site is loaded
   */
  @Override
  public void afterConnection(GApplication application) {
    if (!(application instanceof WebApplication)) {
      throw new ReplayerConstructionException("Cannot login with a non web application");
    }
    WebApplication webApplication = (WebApplication) application;
    WebDriver driver = webApplication.getDriver();
    initializer.afterPageLoaded(driver);
  }
}
