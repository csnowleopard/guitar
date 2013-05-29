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
package edu.umd.cs.guitar.crawljax.ripper;

import com.crawljax.core.configuration.CrawlElement;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.InputSpecification;

import edu.umd.cs.guitar.crawljax.model.EventSpecification;
import edu.umd.cs.guitar.crawljax.model.GuitarEventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.exception.RipperConstructionException;

/**
 * Specifications of elements to crawl and record of {@link CrawljaxWebRipper}.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class RippingSpecification {

  private static final boolean DEFAULT_RANDOM_INPUT_INFORMS = false;
  private static final int DEFAULT_CRAWLING_TIME = 30000;
  private static final int DEFAULT_CRAWLING_DEPTH = 30;

  /**
   * Internal crawljax crawling specification.
   */
  private CrawlSpecification crawljaxSpecification;

  /**
   * Event manager to manage event recorded by GUITAR.
   */
  private GuitarEventManager guitarEventManager = new GuitarEventManager();

  public RippingSpecification() {
    super();
    this.crawljaxSpecification = new CrawlSpecification("");

    // Default crawljax specification.
    this.crawljaxSpecification.setDepth(DEFAULT_CRAWLING_DEPTH);
    this.crawljaxSpecification.setMaximumRuntime(DEFAULT_CRAWLING_TIME);
    this.crawljaxSpecification.setRandomInputInForms(DEFAULT_RANDOM_INPUT_INFORMS);
    this.crawljaxSpecification.setClickOnce(true);
  }



  /**
   * Specify Web element's tag to click on during ripping.
   */
  public CrawlElement clickOnTag(String tagName) {
    return crawljaxSpecification.click(tagName);
  }

  /**
   * Specify Web element's tag NOT to click on during ripping.
   */
  public CrawlElement dontClickOnTag(String tagName) {
    return crawljaxSpecification.dontClick(tagName);
  }

  /**
   * Specify frame NOT to examine during ripping.
   *
   * @param frameName
   * @see com.crawljax.core.configuration.CrawlSpecification#dontCrawlFrame(java.lang.String)
   */
  public void dontCrawlFrame(String frameName) {
    crawljaxSpecification.dontCrawlFrame(frameName);
  }

  /**
   * Register GUITAR event to record during ripping.
   *
   * @param guitarEvent
   * @param tagName
   * @throws RipperConstructionException
   */
  public CrawlElement recordEventForTag(Class<? extends GEvent> guitarEvent, String tagName)
      throws RipperConstructionException {
    checkEventValidity(guitarEvent);
    // Find the right class.
    EventSpecification eventSpecification = guitarEventManager.getEventSpecification(guitarEvent);
    CrawlElement result = eventSpecification.recordEventOnTag(tagName);
    guitarEventManager.registerEvent(guitarEvent, eventSpecification);
    return result;
  }


  /**
   * Register GUITAR event NOT to record during ripping.
   *
   * @param guitarEvent
   * @param tagName
   * @throws RipperConstructionException
   */
  public CrawlElement dontRecordEventForTag(Class<? extends GEvent> guitarEvent, String tagName)
      throws RipperConstructionException {
    checkEventValidity(guitarEvent);
    // Find the right class
    EventSpecification eventSpecification = guitarEventManager.getEventSpecification(guitarEvent);
    // update the specification
    CrawlElement result = eventSpecification.dontRecordEventOnTag(tagName);
    guitarEventManager.registerEvent(guitarEvent, eventSpecification);
    return result;
  }

  /**
   * Don't record event on a specified frame.
   *
   * @param guitarEvent
   * @param frameName
   * @throws RipperConstructionException
   */
  public void dontRecordEventOnFrame(Class<? extends GEvent> guitarEvent, String frameName)
      throws RipperConstructionException {
    checkEventValidity(guitarEvent);
    // Find the right class
    EventSpecification eventSpecification = guitarEventManager.getEventSpecification(guitarEvent);
    // update the specification
    eventSpecification.dontRecordFrame(frameName);
    guitarEventManager.registerEvent(guitarEvent, eventSpecification);
  }

  // Delegate methods for CrawlSpecification.
  /**
   * Delegation for method for
   * {@link com.crawljax.core.configuration.CrawlSpecification#setClickOnce}.
   *
   * @param clickOnce true if each candidate element should be examined only once.
   * @see com.crawljax.core.configuration.CrawlSpecification#setClickOnce(boolean)
   */
  public void setClickOnce(boolean clickOnce) {
    crawljaxSpecification.setClickOnce(clickOnce);
  }

  /**
   * Delegation for method for
   * {@link com.crawljax.core.configuration.CrawlSpecification#setInputSpecification}.
   *
   * @see com.crawljax.core.configuration.CrawlSpecification#setInputSpecification(InputSpecification)
   */
  public void setInputSpecification(InputSpecification inputSpecification) {
    crawljaxSpecification.setInputSpecification(inputSpecification);
  }


  /**
   * Delegation for method for
   * {@link com.crawljax.core.configuration.CrawlSpecification#setRandomInputInForms}.
   *
   * @see com.crawljax.core.configuration.CrawlSpecification#setRandomInputInForms(boolean)
   */
  public void setRandomInputInForms(boolean value) {
    crawljaxSpecification.setRandomInputInForms(value);
  }

  /**
   * Delegation for method for
   * {@link com.crawljax.core.configuration.CrawlSpecification#setWaitTimeAfterReloadUrl}.
   *
   * @param milliseconds time to wait
   * @see com.crawljax.core.configuration.CrawlSpecification#setWaitTimeAfterReloadUrl(int)
   */
  public void setWaitTimeAfterReloadUrl(int milliseconds) {
    crawljaxSpecification.setWaitTimeAfterReloadUrl(milliseconds);
  }

  /**
   * Set crawling depth.
   * 
   * @param crawlDepth depth to crawl
   * @see com.crawljax.core.configuration.CrawlSpecification#setDepth(int)
   */
  public void setDepth(int crawlDepth) {
    crawljaxSpecification.setDepth(crawlDepth);
  }

  /**
   * Set max running time. 
   * 
   * @param seconds time to run 
   * @see com.crawljax.core.configuration.CrawlSpecification#setMaximumRuntime(int)
   */
  public void setMaximumRuntime(int seconds) {
    crawljaxSpecification.setMaximumRuntime(seconds);
  }

  /**
   * Set time to waint after each event. 
   * 
   * @param milliseconds
   * @see com.crawljax.core.configuration.CrawlSpecification#setWaitTimeAfterEvent(int)
   */
  public void setWaitTimeAfterEvent(int milliseconds) {
    crawljaxSpecification.setWaitTimeAfterEvent(milliseconds);
  }

  protected CrawlSpecification getCrawljaxSpecification() {
    return crawljaxSpecification;
  }

  protected GuitarEventManager getGuitarEventManager() {
    return guitarEventManager;
  }
  
  private void checkEventValidity(Class<? extends GEvent> guitarEvent)
      throws RipperConstructionException {
    if (GuitarEventManager.EXPANDING_EVENT.equals(guitarEvent)) {
      throw new RipperConstructionException(GuitarEventManager.EXPANDING_EVENT
          + " is reserved for special use " + "and cannot be registered/unregistered");
    }
  }
}
