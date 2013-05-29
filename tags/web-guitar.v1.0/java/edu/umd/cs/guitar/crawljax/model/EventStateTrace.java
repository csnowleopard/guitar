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
package edu.umd.cs.guitar.crawljax.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Data structure to keep track of the trace of GUI events
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class EventStateTrace {
  public static final String INIT_EVENT_ID = "INIT";
  LinkedList<EventEffect> eventEffectList = new LinkedList<EventEffect>();


  /**
   * Add a new event effect to the trace 
   */
  public void addEffect(String triggerEventID, EventState eventState) {
    EventEffect effect = new EventEffect(triggerEventID, eventState);
    this.eventEffectList.addLast(effect);
  }

  public LinkedList<EventEffect> getEventEffectList() {
    return eventEffectList;
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer();

    for (EventEffect eventEffect : eventEffectList) {
      result.append(eventEffect.triggerEventID);
      result.append(": ");
      result.append(eventEffect.eventState.toString());
      result.append("\n");
    }
    return result.toString();
  }

  /**
   * Get the list of events that leads to a change in the current event set
   */
  public List<String> getExpandingEventList() {
    List<String> result = new ArrayList<String>();
    if (eventEffectList.size() <= 1) return result;

    EventEffect previousEventEffect = eventEffectList.getFirst();
    for (int i = 1; i < eventEffectList.size(); i++) {
      EventEffect currentEventEffect = eventEffectList.get(i);
      EventState previousState = previousEventEffect.getEventState();
      EventState currentState = currentEventEffect.getEventState();


      if (!previousState.equals(currentState)) 
        result.add(currentEventEffect.getTriggerEventID());
      
      previousEventEffect = currentEventEffect;
    }
    return result;
  }
}
