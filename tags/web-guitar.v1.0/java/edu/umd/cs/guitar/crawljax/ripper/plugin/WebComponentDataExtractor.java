/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.crawljax.ripper.plugin;


import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.data.EventType;

import edu.umd.cs.guitar.model.data.WidgetMapElementType;

import edu.umd.cs.guitar.model.WebWindow;

import edu.umd.cs.guitar.model.data.AttributesType;

import edu.umd.cs.guitar.model.data.PropertyType;

import java.util.List;

import edu.umd.cs.guitar.model.data.ObjectFactory;

import edu.umd.cs.guitar.model.GComponent;

import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.model.data.ComponentType;

/**
 * An utilty class to extract properties of a web component 
 *  
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
class WebComponentDataExtractor {
  private static ObjectFactory factory = new ObjectFactory();

  /**
   * @param gWindow
   * @param gComponent
   * @return
   */
  public static WidgetMapElementType getComponentProperty(
      WebWindow gWindow, WebComponent gComponent) {

    WidgetMapElementType retWidget = factory.createWidgetMapElementType();
    // capture Window properties  

    // capture Component properties  
    ComponentType component = factory.createComponentType();
    retWidget.setComponent(component);
    
    AttributesType attribute = factory.createAttributesType();
    component.setAttributes(attribute);

    // get all GUI properties
    List<PropertyType> lGUIProperties = gComponent.getGUIProperties();
    attribute.getProperty().addAll(lGUIProperties);
    
    return retWidget;
  }

  /**
   * @param gEvent
   * @return
   */
  public static EventType getEventProperty(Class<? extends GEvent> gEvent) {
    EventType event = factory.createEventType();
    event.setAction(gEvent.getName());
    return event;
  }

}
