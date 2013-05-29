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
package edu.umd.cs.guitar.crawljax.model;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.EventsType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.RowType;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIMapWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Data structure to keep the ripping result:
 * <ul>
 * <li> A {@link GUIMap} object containing all GUI elements recorded
 * <li> A {@link EventStateTrace} object containing sequence of pairs
 *  <triggered event, event state> 
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class RippingResult {

  EventStateTrace eventTrace = new EventStateTrace();
  ObjectFactory factory = new ObjectFactory();
  GUIMap guiMap = factory.createGUIMap();

  /**
   * Getter the event trace  
   * @return the eventTrace
   */
  public EventStateTrace getEventTrace() {
    return eventTrace;
  }


  /**
   * @param firedID
   * @param eventState
   * @see edu.umd.cs.guitar.crawljax.model.EventStateTrace#addEffect(java.lang.String,
   *      edu.umd.cs.guitar.crawljax.model.EventState)
   */
  public void appendEventStateTrace(String firedID, EventState eventState) {
    eventTrace.addEffect(firedID, eventState);
  }

  /**
   * Getter the GUI map  
   * @return the guiMap
   */
  public GUIMap getGuiMap() {
    return guiMap;
  }


  /**
   * Analyze the data collected during ripping and return the EFG model
   * ALGORITHM: 
   *    Traversing backward on the trace for analysis
   *    For each event effect
   *        1. connect all events except the one FROM expanding event
   *        2. connect the trigger event with other
   *            2.1 with an expanding edge if the event is new
   *            2.2 with a flowing edge if the event is old
   */
  public EFG getEFG() {
    ObjectFactory factory = new ObjectFactory();
    EFG efg = factory.createEFG();
    Set<String> allEventIDs = getEventIDsFromTrace();
    GUIMapWrapper guiMap = new GUIMapWrapper(this.guiMap);

    // Add nodes
    EventsType eventSet = factory.createEventsType();
    efg.setEvents(eventSet);

    List<EventType> eventTypeList = new ArrayList<EventType>();
    for (String id : allEventIDs) {
      EventType event = guiMap.getEventByID(id);
      eventTypeList.add(event);

    }
    eventSet.getEvent().clear();
    eventSet.getEvent().addAll(eventTypeList);

    // Add edges
    EventGraphType graph = factory.createEventGraphType();
    efg.setEventGraph(graph);
    List<RowType> lRowList = new ArrayList<RowType>();

    // Initialize the EFG with no edge
    for (EventType source : eventTypeList) {
      RowType row = factory.createRowType();
      for (EventType target : eventTypeList) {
        row.getE().add(GUITARConstants.NO_EDGE);
      }
      lRowList.add(row);
    }

    graph.getRow().clear();
    graph.getRow().addAll(lRowList);
    // -----------------------------
    // Populate EFG edge values
    // -----------------------------
    EFGWrapper efgWrapper = new EFGWrapper(efg);

    List<String> expandingEventList = eventTrace.getExpandingEventList();
    LinkedList<EventEffect> eventEffectList = eventTrace.getEventEffectList();

    int l = eventEffectList.size() - 1;

    for (int i = l; i >= 0; i--) {
      EventEffect curEffect = eventEffectList.get(i);
      String triggerId = curEffect.getTriggerEventID();
      EventState curState = curEffect.getEventState();
      Collection<String> curEvents = curState.getEventSet();

      // Connect all the events in size a state
      for (String sourceID : curEvents) {
        // We treat expanding event specially
        if (expandingEventList.contains(sourceID)) continue;
        for (String targetID : curEvents) {
          efgWrapper.addEdge(sourceID, targetID, GUITARConstants.FOLLOW_EDGE);
        }
      }

      // Non-initial state
      if (!EventStateTrace.INIT_EVENT_ID.equals(triggerId) && (i > 0)) {
        EventEffect prevEffect = eventEffectList.get(i - 1);
        EventState prevState = prevEffect.getEventState();
        Collection<String> prevEvents = prevState.getEventSet();

        // Set of newly appear events
        Collection<String> newEvents = new ArrayList<String>();
        newEvents.addAll(curEvents);
        newEvents.removeAll(prevEvents);

        for (String eventID : curEvents) {
          if (newEvents.contains(eventID)) {
            efgWrapper.addEdge(triggerId, eventID, GUITARConstants.REACHING_EDGE);
          } else {
            efgWrapper.addEdge(triggerId, eventID, GUITARConstants.FOLLOW_EDGE);
          }
        }

        // For initial state we add the initial flag for the events
      } else {
        for (String eventID : curEvents) {
          EventType intialEvent = efgWrapper.getEventByID(eventID);
          intialEvent.setInitial(true);
        }
      }
    }
    efg = efgWrapper.getEfg();
    return efg;
  }

  /**
   * get all event ID from the trace 
   */
  private Set<String> getEventIDsFromTrace() {
    Set<String> allEventIDs = new HashSet<String>();

    // Add nodes
    for (EventEffect ee : eventTrace.getEventEffectList()) {
      for (String id : ee.eventState.getEventSet()) {
        allEventIDs.add(id);
      }
    }
    return allEventIDs;
  }

  /**
   * Add a new widget to the looking table  
   * @param widget
   *    widget to add 
   */
  public boolean addWidget(WidgetMapElementType widget) {
    GUIMapWrapper guiMapWrapper = new GUIMapWrapper(guiMap);
    boolean result = guiMapWrapper.addWidget(widget);
    this.guiMap.setWidgetMap(guiMapWrapper.getWidgetMap());
    return result;
  }


  /**
   * Add a new event to the looking table 
   * @param event
   *    event to add 
   */
  public boolean addEvent(EventType event) {
    GUIMapWrapper guiMapWrapper = new GUIMapWrapper(guiMap);
    boolean result = guiMapWrapper.addEvent(event);
    this.guiMap.setEventMap(guiMapWrapper.getEventMap());
    return result;
  }


  /**
   * Get the last event state in the trace
   * @return
   *    the last event state in the trace
   *    <code> null </code> if  there is no such event state  
   */
  public EventState getLastEventState() {
    if (eventTrace == null || eventTrace.getEventEffectList() == null) return null;
    LinkedList<EventEffect> effectList = eventTrace.getEventEffectList();
    if (effectList.size() > 0) {
      EventEffect lastEffect = eventTrace.getEventEffectList().getLast();
      return lastEffect.getEventState();
    }
    return null;
  }
}
