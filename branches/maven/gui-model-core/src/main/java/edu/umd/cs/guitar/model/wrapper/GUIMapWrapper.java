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
package edu.umd.cs.guitar.model.wrapper;

import edu.umd.cs.guitar.model.data.WidgetMapType;

import edu.umd.cs.guitar.model.data.EventMapType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;

/**
 * The wrapper for <code>GUIMap</code>
 * <p>
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"eventMap", "widgetMap"})
@XmlRootElement(name = "GUIMap")
public class GUIMapWrapper extends GUIMap {

  static ObjectFactory factory = new ObjectFactory();

  public GUIMapWrapper(GUIMap map) {

    if (map.getEventMap() == null) {
      this.eventMap = factory.createEventMapType();
    } else {
      this.eventMap = map.getEventMap();
    }

    if (map.getWidgetMap() == null) {
      this.widgetMap = factory.createWidgetMapType();
    } else {
      this.widgetMap = map.getWidgetMap();
    }
  }

  /**
   * get widget map from event id
   *
   * @param eID event id
   * @return
   */
  public WidgetMapElementType getWidgetMap(String eID) {
    EventMapTypeWrapper wEM = new EventMapTypeWrapper(this.getEventMap());
    WidgetMapWrapper wWM = new WidgetMapWrapper(this.getWidgetMap());

    // Look up event from ID
    EventType event = wEM.getEvent(eID);
    if (event == null) return null;

    // Look up widgetID type from event
    String wID = event.getWidgetId();
    if (wID == null) return null;

    // Look up widget map element from widget ID
    WidgetMapElementType wMap = wWM.getWidgetMap(wID);

    return wMap;

  }

  /**
   * get a widget property from event id
   *
   * @param eID event id
   * @return
   */
  public String getWidgetProperty(String eID, String property) {
    String value = null;
    if (eID == null) return value;

    WidgetMapElementType widget = getWidgetMap(eID);
    if (widget != null) {
      ComponentType component = widget.getComponent();
      ComponentTypeWrapper wComponent = new ComponentTypeWrapper(component);
      value = wComponent.getFirstValueByName(property);
    }

    return value;

  }


  public EventType getEventByID(String ID) {
    for (EventType event : eventMap.getEventMapElement()) {
      if (ID.equals(event.getEventId())) {
        return event;
      }
    }
    return null;
  }

  public boolean containsEvent(String ID) {
    return (getEventByID(ID) != null);
  }

  public boolean containsWidget(String ID) {
    return (getWidgetByID(ID) != null);
  }

  public WidgetMapElementType getWidgetByID(String ID) {
    for (WidgetMapElementType widget : widgetMap.getWidgetMapElement()) {
      if (ID.equals(widget.getWidgetId())) {
        return widget;
      }
    }
    return null;
  }


  /**
   * Get the event type by analyzing the GUI Map file
   *
   * <p>
   *
   * @param event
   * @param map
   * @return
   */
  public String getEventType(EventType event) {
    String type = event.getType();
    String wid = event.getWidgetId();
    if (!EventTypeText.INTERACTION.equals(type)) {
      return type;
    }

    WidgetMapElementType widgetElement = getWidgetByID(wid);

    if (widgetElement == null) return type;
    return type;
    // ComponentType component = widgetElement.getComponent();
    // ComponentTypeWrapper wComponent = new
    // ComponentTypeWrapper(component);
    // String invokelist = wComponent
    // .getFirstValueByName(GUITARConstants.INVOKELIST_TAG_NAME);
    // return type;
    // if (invokelist == null)
    // return type;
    // else
    // return EventTypeText.EXPAND;

  }

  /**
   * Get the set of event id in GUI Map
   *
   * @return
   */
  public Set<String> getEventSet() {
    Set<String> eventSet = new HashSet<String>();

    for (EventType event : eventMap.getEventMapElement()) {
      eventSet.add(event.getEventId());

    }
    return eventSet;
  }

  /**
   * Get the event type by analyzing the GUI Map file
   *
   * <p>
   *
   * @param event
   * @param map
   * @return
   */
  public String getEventType(String eventID) {
    EventType event = getEventByID(eventID);
    return this.getEventType(event);
  }


  /**
   * Add a new widget to the GUI Map
   *
   * @param widget
   * @return
   */
  public boolean addWidget(WidgetMapElementType widget) {
    if (widgetMap == null) {
      widget = factory.createWidgetMapElementType();
    }
    String widgetID = widget.getWidgetId();
    
    if (!this.containsWidget(widgetID)) {
      return widgetMap.getWidgetMapElement().add(widget);
    } else {
      return false;
    }
  }

  
  /**
   * Add a new event to the GUI Map
   *
   */
  public boolean addEvent(EventType event) {
    if (eventMap == null) {
      eventMap = factory.createEventMapType(); 
    }
    String eventID = event.getEventId();
    if (!this.containsEvent(eventID)){
      return eventMap.getEventMapElement().add(event);
    }else{
      return false;
    }
  }
  
  public boolean addGUIMap(GUIMap map) {
    EventMapType eventMap = map.getEventMap();
    WidgetMapType widgetMap = map.getWidgetMap();

    if (eventMap == null || widgetMap == null) return false;

    List<EventType> eventList = eventMap.getEventMapElement();
    List<EventType> eventListResult = new ArrayList<EventType>();

    for (EventType event : eventList) {
      if (!containsEvent(event.getEventId())) {
        eventListResult.add(event);
      }
    }
    this.eventMap.getEventMapElement().addAll(eventListResult);

    List<WidgetMapElementType> widgetList = widgetMap.getWidgetMapElement();
    List<WidgetMapElementType> widgetListResult = new ArrayList<WidgetMapElementType>();
    for (WidgetMapElementType widget : widgetList) {
      if (!this.containsWidget(widget.getWidgetId())) {
        widgetListResult.add(widget);
      }
    }

    this.widgetMap.getWidgetMapElement().addAll(widgetListResult);

    return true;
  }

  interface EventTypeText {
    static final String INTERACTION = GUITARConstants.SYSTEM_INTERACTION;
    static final String EXPAND = GUITARConstants.EXPAND;
    static final String RESTRICED = GUITARConstants.RESTRICED_FOCUS;
    static final String TERMINAL = GUITARConstants.TERMINAL;
  }
}
