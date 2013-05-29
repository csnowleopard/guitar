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

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

import edu.umd.cs.guitar.model.data.ComponentType;

import edu.umd.cs.guitar.model.GComponent;

import edu.umd.cs.guitar.model.wrapper.EventMapTypeWrapper;

import edu.umd.cs.guitar.model.wrapper.WidgetMapTypeWrapper;

import edu.umd.cs.guitar.model.data.EventType;

import edu.umd.cs.guitar.model.data.EventMapElementType;

import edu.umd.cs.guitar.model.data.WidgetMapElementType;

import edu.umd.cs.guitar.model.GUITARConstants;


import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GHashCodeGenerator2;
import edu.umd.cs.guitar.model.data.EventMapType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.WidgetMapType;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzer to analyze the current state and extract replayable web event
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
@Deprecated
public class WebEventAnalyzer {
  private static final Logger LOGGER = Logger.getLogger(WebEventAnalyzer.class);
  /**
   * Event manager to manage all events to capture
   */
  EventManager eventManager;
  GHashCodeGenerator2 hashcodeGenerator;

  String rootXpath;

  /**
   * GUITAR data factory
   */
  private ObjectFactory factory = new ObjectFactory();

  /**
   * @param eventManager
   * @param hashcodeGenerator
   * @param rootXpath
   */
  public WebEventAnalyzer(
      EventManager eventManager, GHashCodeGenerator2 hashcodeGenerator, String rootXpath) {
    super();
    this.eventManager = eventManager;
    this.hashcodeGenerator = hashcodeGenerator;
    this.rootXpath = rootXpath;
  }

  /**
   * @param eventManager
   * @param rootXpath
   */
  public WebEventAnalyzer(EventManager eventManager, String rootXpath) {
    this(eventManager, new WebDefaultHashcodeGenerator2(), rootXpath);
  }

  /**
   * @param eventManager
   */
  public WebEventAnalyzer(EventManager eventManager) {
    this(eventManager, new WebDefaultHashcodeGenerator2());
  }

  /**
   * @param eventManager
   * @param idGenerator
   */
  public WebEventAnalyzer(EventManager eventManager, GHashCodeGenerator2 idGenerator) {
    this(eventManager, idGenerator, "//html");
  }

  /**
   *@see WebEventAnalyzer#extractEvents(WebElement)
   * @param driver
   * @return a look up table for all replayable events and widgets
   */
  public GUIMap extractEvents(WebDriver driver) {
    WebElement root;
    try {
      root = driver.findElement(By.xpath(rootXpath));
    } catch (NoSuchElementException e) {
      return null;
    }
    return extractEvents(root);
  }

  /**
   * Analyze a web element and extract all replayable event
   *
   * <p>
   *
   * @param element
   * @return a look up table for all replayable events and widgets
   */
  public GUIMap extractEvents(WebElement element) {
    GUIMap map = factory.createGUIMap();

    WidgetMapTypeWrapper widgetMap = new WidgetMapTypeWrapper(factory.createWidgetMapType());
    EventMapTypeWrapper eventMap = new EventMapTypeWrapper(factory.createEventMapType());


    // List<WebElement> children = element.findElements(By.cssSelector("*"));
    List<WebElement> children = element.findElements(By.xpath(".//*"));
    List<WebElement> childrenEnabled = new ArrayList<WebElement>();

    for (WebElement aChild : children) {
      try {
        if (!aChild.isEnabled() || !(aChild.isDisplayed())) continue;
      } catch (StaleElementReferenceException exception) {
        LOGGER.error(exception);
      }
      childrenEnabled.add(aChild);
    }
    children = childrenEnabled;

    LOGGER.info("TOTAL visible element to explore: " + children.size());


    for (WebElement aChild : children) {
      if (!aChild.isEnabled() || !(aChild.isDisplayed())) continue;
      WebComponent gComponent = new WebComponent(aChild, null, null, null);

      if (hasEventSupported(gComponent)) {
        long hashcode = getHashCode(aChild);


        String widgetID = GUITARConstants.COMPONENT_ID_PREFIX + hashcode;

        if (!widgetMap.contains(widgetID)) {
          // create lookup widget
          WidgetMapElementType widgetElement = factory.createWidgetMapElementType();
          ComponentType componentData = gComponent.extractProperties();
          ComponentTypeWrapper componentDataWrapper = new ComponentTypeWrapper(componentData);
          componentDataWrapper.addProperty(GUITARConstants.ID_TAG_NAME, widgetID);
          componentData = componentDataWrapper.getDComponentType();
          widgetElement.setComponent(componentData);
          widgetElement.setWidgetId(widgetID);

          widgetMap.getWidgetMapElement().add(widgetElement);
        }

        for (GEvent event : eventManager.getEvents()) {
          if (event.isSupportedBy(gComponent)) {
            String eventID = GUITARConstants.EVENT_ID_PREFIX + hashcode;
            if (!eventMap.contains(eventID)) {
              EventType eventElement = factory.createEventType();
              
              // event id 
              eventElement.setEventId(eventID);
              // widget id
              eventElement.setWidgetId(widgetID);
              // action
              String action = event.getClass().getName();
              eventElement.setAction(action);
              
              eventMap.getEventMapElement().add(eventElement);
            }
          }
        }
      }
    }

    map.setWidgetMap(widgetMap);
    map.setEventMap(eventMap);
    return map;
  }

  /**
   * @param webElement
   * @return
   */
  public long getHashCode(WebElement webElement) {
    WebComponent gComponent = new WebComponent(webElement, null, null, null);
    return hashcodeGenerator.generateHashCode(gComponent);
  }

  /**
   * Create empty GUI map for debugging
   *
   * @param element
   * @return
   */
  public GUIMap createEmptyGUIMap() {
    GUIMap map = factory.createGUIMap();
    WidgetMapTypeWrapper widgetMap = new WidgetMapTypeWrapper(factory.createWidgetMapType());
    EventMapTypeWrapper eventMap = new EventMapTypeWrapper(factory.createEventMapType());
    map.setWidgetMap(widgetMap);
    map.setEventMap(eventMap);


    return map;
  }

  /**
   * Check if a component support a registered replayable event
   * <p>
   *
   * @param gComponent
   * @return true iff the component support at least one event
   */
  private boolean hasEventSupported(WebComponent gComponent) {
    for (GEvent event : eventManager.getEvents()) {
      if (event.isSupportedBy(gComponent)) return true;
    }
    return false;
  }

}
