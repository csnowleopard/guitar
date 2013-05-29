/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of
 * this group may be obtained by sending an e-mail to atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and or sell copies of the Software, and to permit persons to whom the Software is
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
package edu.umd.cs.guitar.crawljax.ripper.plugin;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.browser.WebDriverBackedEmbeddedBrowser;
import com.crawljax.core.CandidateElement;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.plugin.OnNewStatePlugin;
import com.crawljax.core.state.Eventable;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.crawljax.browser.monitor.CJBrowserBeforeFiringEvent;
import edu.umd.cs.guitar.crawljax.model.EventSpecification;
import edu.umd.cs.guitar.crawljax.model.EventState;
import edu.umd.cs.guitar.crawljax.model.EventStateTrace;
import edu.umd.cs.guitar.crawljax.model.GuitarEventManager;
import edu.umd.cs.guitar.crawljax.model.RippingResult;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GHashCodeGenerator2;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.model.WebDefaultHashcodeGenerator2;
import edu.umd.cs.guitar.model.WebWindow;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GUITAR plugin to monitor the events available before and after each crawling activity
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class GUITARWebEventMonitorPlugin implements OnNewStatePlugin, CJBrowserBeforeFiringEvent {

  GuitarEventManager manager;
  /**
   * Hash code generator used to generate widget ID
   */
  GHashCodeGenerator2 hashcodeGenerator;
  private static final Logger LOGGER = Logger.getLogger(GUITARWebEventMonitorPlugin.class);

  /**
   * Ripping results
   */
  RippingResult rippingResult;

  /**
   * A temporary variable to store the latest event fired before it might be destroyed because of a
   * GUI change
   */
  private String lastFiredEventID;

  /**
   * Get the current ripping result
   *
   * @return the rippingResult
   */
  public RippingResult getRippingResult() {
    return rippingResult;
  }

  public void setHashcodeGenerator(GHashCodeGenerator2 hashcodeGenerator) {
    this.hashcodeGenerator = hashcodeGenerator;
  }

  public GUITARWebEventMonitorPlugin(GuitarEventManager manager) {
    super();
    this.manager = manager;
    this.hashcodeGenerator = new WebDefaultHashcodeGenerator2();
    this.rippingResult = new RippingResult();
    this.lastFiredEventID = EventStateTrace.INIT_EVENT_ID;
  }

  /**
   * Record event ID before performing it
   */
  @Override
  public void beforeFiringEvent(Eventable event, EmbeddedBrowser embeddedBrowser) {
    LOGGER.info("Logging event before firing");
    try {
      WebElement webElement = embeddedBrowser.getWebElement(event.getIdentification());
      WebComponent gComponent = new WebComponent(webElement, null, null, null);
      long hashcode = hashcodeGenerator.generateHashCode(gComponent);


      lastFiredEventID = getExpandingEventID(hashcode);

      LOGGER.info("Event to click: " + webElement.getText());
      LOGGER.info("EventID: " + lastFiredEventID);
    } catch (NoSuchElementException exception) {
      LOGGER.debug("WebElement not found");
    }

  }

  /**
   * Monitor GUITAR event changes  if there is a DOM change
   */
  @Override
  public void onNewState(CrawlSession session) {

    // Find candidate elements
    LOGGER.info("Searching candidate event....");
    Map<WebElement, Set<Class<? extends GEvent>>> eventActionMap =
        collectEventableWebElmenent(session);

    EmbeddedBrowser browser = session.getBrowser();
    WebDriver webDriver = null;
    if (browser instanceof WebDriverBackedEmbeddedBrowser) {
      webDriver = ((WebDriverBackedEmbeddedBrowser) browser).getBrowser();
    }

    // Save Web element info
    LOGGER.info("Recording event information....");

    Set<String> allEventIDs = new HashSet<String>();
    for (WebElement webElement : eventActionMap.keySet()) {

      WebComponent gComponent = new WebComponent(webElement, null, null, null);
      long hashcode = hashcodeGenerator.generateHashCode(gComponent);

      // extract and add widget information
      String widgetID = GUITARConstants.COMPONENT_ID_PREFIX + hashcode;

      WebWindow gWindow = new WebWindow(webDriver, null);
      WidgetMapElementType widget =
          WebComponentDataExtractor.getComponentProperty(gWindow, gComponent);
      widget.setWidgetId(widgetID);

      rippingResult.addWidget(widget);

      // extract and add event information
      Set<Class<? extends GEvent>> capturedEvents = eventActionMap.get(webElement);
      int index = 0;
      for (Class<? extends GEvent> gEvent : capturedEvents) {
        EventType event = WebComponentDataExtractor.getEventProperty(gEvent);
        event.setWidgetId(widgetID);
        String eventID;
        // generate widgetID
        if (GuitarEventManager.EXPANDING_EVENT.equals(gEvent)) {
          eventID = getExpandingEventID(hashcode);
        } else {
          eventID = GUITARConstants.EVENT_ID_PREFIX + hashcode;
          if (index > 0) {
            eventID += "." + index;
          }
          index++;
        }
        event.setEventId(eventID);
        event.setType(GUITARConstants.SYSTEM_INTERACTION);

        allEventIDs.add(eventID);
        rippingResult.addEvent(event);
      }
    }
    // append event trace
    EventState curentEventState = new EventState(allEventIDs);
    EventState lastEventState = rippingResult.getLastEventState();
    if (!curentEventState.equals(lastEventState)) {

      LOGGER.info("Event State is Change! Appending to the model");
      LOGGER.info("TOTAL event(s) recorded: " + allEventIDs.size());
      rippingResult.appendEventStateTrace(lastFiredEventID, curentEventState);

    } else {
      System.err.println("Event State is unchanged");
      LOGGER.info("Event State is Unchanged");
    }

  }

  /**
   * @param spec
   * @param session
   * @return
   * @throws CrawljaxException
   */
  private List<CandidateElement> getCandidateElementList(
      EventSpecification spec, CrawlSession session) throws CrawljaxException {
    List<CandidateElement> candidateList = spec.getCandidateList(session);
    return candidateList;
  }
  
  /**
   * Get expanding event ID given its hashcode
   *
   * @param hashcode
   */
  private String getExpandingEventID(long hashcode) {
    return GUITARConstants.EVENT_ID_PREFIX + GuitarEventManager.EXPANDING_EVENT_ID_INDEX + hashcode;
  }

  /**
   * Collect all events available in the current session
   * @param session
   */
  private Map<WebElement, Set<Class<? extends GEvent>>> collectEventableWebElmenent(
      CrawlSession session) {
    Map<WebElement, Set<Class<? extends GEvent>>> eventActionMap =
        new HashMap<WebElement, Set<Class<? extends GEvent>>>();

    for (Class<? extends GEvent> gEvent : manager.getSupportedEvents()) {
      // Setup event specification
      EventSpecification spec = manager.getEventSpecification(gEvent);
      try {
        List<CandidateElement> candidateList = getCandidateElementList(spec, session);
        for (CandidateElement element : candidateList) {
          EmbeddedBrowser browser = session.getBrowser();
          try {
            WebElement webElement = browser.getWebElement(element.getIdentification());

            // We ignore the invisible and disabled element
            if (!webElement.isDisplayed() || !webElement.isEnabled()) continue;

            Set<Class<? extends GEvent>> supportedEventSet = eventActionMap.get(webElement);

            if (supportedEventSet == null) {
              supportedEventSet = new HashSet<Class<? extends GEvent>>();
            }

            supportedEventSet.add(gEvent);
            eventActionMap.put(webElement, supportedEventSet);

          } catch (org.openqa.selenium.NoSuchElementException noElementException) {

            LOGGER.debug("WebElement is not accessable");
            LOGGER.debug("Identification: " + element.getIdentification());

          }
        }// end capture all GEvent

      } catch (CrawljaxException e) {
        LOGGER.debug("CrawljaxException" + e.getMessage());
      }
    }
    return eventActionMap;
  }
}
