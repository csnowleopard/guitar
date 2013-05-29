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
package edu.umd.cs.guitar.ripper.plugin;

import edu.umd.cs.guitar.model.GObject;
import edu.umd.cs.guitar.model.GWindow;

import edu.umd.cs.guitar.util.GUITARLog;

import edu.umd.cs.guitar.ripper.Ripper;

/**
 * Interface to be implemented by classes that wish to be notified when 
 * each ripping step is run
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */

public abstract class GRipperStepMonitor {

  /**
	 * 
	 */
  public GRipperStepMonitor() {
    super();
  }

  Ripper ripper;

  /**
   * Notification right before a window is ripped
   * @param gWindow 
   *
   */
  public void beforeRippingWindow(GWindow gWindow) {
  }

  /**
   * Notification right after a window is ripped
   * @param gWindow 
   *
   * @param arg
   */
  public void afterRippingWindow(GWindow gWindow) {
  }


  /**
   * Notification right before a component is ripped
   * @param window 
   * @param component 
   *
   */
  public void beforeRippingComponent(GObject component, GWindow window) {
  }

  /**
   * Notification right after a component is ripped
   * @param window 
   * @param component 
   *
   * @param arg
   */
  public void afterRippingComponent(GObject component, GWindow window) {
  }

  /**
   * @return the ripper
   */
  public Ripper getRipper() {
    return ripper;
  }

  /**
   * @param ripper the ripper to set
   */
  public void setRipper(Ripper ripper) {
    this.ripper = ripper;
  }

}
