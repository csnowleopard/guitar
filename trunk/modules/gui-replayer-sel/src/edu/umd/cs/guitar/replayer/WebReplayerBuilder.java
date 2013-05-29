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

package edu.umd.cs.guitar.replayer;


import org.xml.sax.SAXException;

import edu.umd.cs.guitar.replayer2.WebReplayerMonitor2;

import edu.umd.cs.guitar.replayer2.Replayer2;

import edu.umd.cs.guitar.replayer2.GReplayerMonitor2;

import edu.umd.cs.guitar.replayer.monitor.GTestMonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import edu.umd.cs.guitar.exception.ReplayerConstructionException;

import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.WebApplication;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class WebReplayerBuilder {
  
  private String testcaseFile; 
  private String guiFile;
  private String efgFile;
  
  private boolean isShutDownAfterTest = true;
  
  /**
   * The {@link GApplication} object representing the web page to rip;
   */
  WebApplication application;
  
  List<GTestMonitor> testMonitorList = new ArrayList<GTestMonitor>(); 

  /**
   * @param testMonitor
   * @return
   */
  public WebReplayerBuilder withTestMonitor(GTestMonitor testMonitor) {
    testMonitorList.add(testMonitor);
    return this;
  }

  /**
   * @param position
   * @param testMonitor
   * @see java.util.List#add(int, java.lang.Object)
   */
  public WebReplayerBuilder withTestMonitor(int position, GTestMonitor testMonitor) {
    testMonitorList.add(position, testMonitor);
    return this;
  }

  /**
   * @param position
   * @return
   * 
   * @see java.util.List#remove(int)
   */
  public WebReplayerBuilder remove(int position) {
    testMonitorList.remove(position);
    return this;
  }

  /**
   * @param o
   * @return
   * @see java.util.List#remove(java.lang.Object)
   */
  public WebReplayerBuilder remove(Object o) {
   testMonitorList.remove(o);
   return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * edu.umd.cs.guitar.ripper.GRipperBuilder#withApplication(edu.umd.cs.guitar
   * .model.GApplication)
   */
  public WebReplayerBuilder withApplication(GApplication application) throws ReplayerConstructionException {
    if (!(application instanceof WebApplication)) throw new ReplayerConstructionException();
    this.application = (WebApplication) application;
    return this;
  }

  /**
   * @return
   */
  public Replayer build() throws ReplayerConstructionException{
    
    // build input
    if( application ==null
        ||testcaseFile==null
        || guiFile==null 
        || efgFile ==null){
      throw new ReplayerConstructionException();
    }
    
    
    Replayer2 replayer;
    
    try {
      replayer = new Replayer2(testcaseFile, guiFile, efgFile);
    } catch (ParserConfigurationException e) {
      throw new ReplayerConstructionException();
    } catch (SAXException e) {
      throw new ReplayerConstructionException();
    } catch (IOException e) {
      throw new ReplayerConstructionException();
    }
    
    // setup platform-specific replayer monitor
    GReplayerMonitor2 monitor = new  WebReplayerMonitor2(application);
    replayer.setMonitor(monitor);

    // build test monitors 
    for(GTestMonitor testMonitor: this.testMonitorList){
       replayer.addTestMonitor(testMonitor);
    }

    return replayer;
  }

  /**
   * @param guiFile
   * @return
   */
  public  WebReplayerBuilder withGUIFile(String guiFile) {
    this.guiFile = guiFile;
    return this;
  }

  /**
   * @param efgFile
   * @return
   */
  public  WebReplayerBuilder withEFGFile(String efgFile) {
    this.efgFile = efgFile;
    return this;
  }
  
  /**
   * @param efgFile
   * @return
   */
  public  WebReplayerBuilder withTestCaseFile(String testcaseFile) {
    this.testcaseFile= testcaseFile;
    return this;
  } 
  
}
