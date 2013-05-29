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
package edu.umd.cs.guitar.util;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;


/**
 * Graph utility class
 */
public class GraphUtil {

   // Graph data to manupulate
   private EFG inputGraph;

   /**
    * Map of <event, all events used to reveal it> Note that we
    * don't store all predecessor events here
    */
   private Hashtable<String, Vector<EventType>> preds;

   /**
    * Map of <event, all successor events>
    */
   private Hashtable<String, Vector<EventType>> succs;

   /**
    * Events marked as "initial" in the input graph.
    */
   private List<String> initialEvents = null;

   /*
    * List of visited events for each event during search.
    */
   private Map <String, List<EventType>> visited;


   /**
    * Initialise state.
    */
   public GraphUtil(EFG inputGraph)
   {
      this.inputGraph = inputGraph;
      parseFollowRelations();
      parseInitialEvents();
   }


   /**
    * Determine the initial events in the input graph.
    */
   protected void
   parseInitialEvents()
   {
      initialEvents = new ArrayList<String>();

      if (inputGraph == null) {
         GUITARLog.log.error("Input graph is null");
         return;
      }

      List<EventType> eventList = inputGraph.getEvents().getEvent();
      for (EventType event : eventList) {
         if (event.isInitial()) {
            initialEvents.add(event.getEventId());
         }
      }

      GUITARLog.log.info("Graph total events: " +
                         eventList.size());
      GUITARLog.log.info("Graph initial events: " +
                         initialEvents.size());
   }


   /**
    * Get follow relations in the input graph
    */
   private void
   parseFollowRelations()
   {
      List<EventType> eventList = inputGraph.getEvents().getEvent();
      int eventGraphSize = eventList.size();
      EventGraphType eventGraph = inputGraph.getEventGraph();

      if (inputGraph == null) {
         GUITARLog.log.error("Empty input graph");
         return;
      }

      succs = new Hashtable<String, Vector<EventType>>();
      preds = new Hashtable<String, Vector<EventType>>();

      for (int row = 0; row < eventGraphSize; row++) {
         EventType currentEvent = eventList.get(row);
         Vector<EventType> s = new Vector<EventType>();

         for (int col = 0; col < eventGraphSize; col++) {
            int relation = eventGraph.getRow().get(row).getE().get(col);

            // Other is followed by current event: current -> other
            if (relation != GUITARConstants.NO_EDGE) {
               EventType otherEvent = eventList.get(col);

               s.add(otherEvent);

               if (relation == GUITARConstants.REACHING_EDGE
                     && !otherEvent.getEventId().equals(
                           currentEvent.getEventId())) {

                  // Create preds list
                  Vector<EventType> p = preds.get(otherEvent);
                  if (p == null) {
                     p = new Vector<EventType>();
                  }

                  p.add(currentEvent);
                  preds.put(otherEvent.getEventId(), p);
               } // if
            } // if

            succs.put(currentEvent.getEventId(), s);
         } // for
      } // for
   }


   public LinkedList<EventType>
   pathToRoot(EventType event)
   {
      visited = new HashMap<String, List<EventType>>();
      return pathToRootInt(event);
   }

   /**
    * Find path to root
    *
    * @param event
    * @return
    */
   public LinkedList<EventType>
   pathToRootInt(EventType event)
   {
      if (initialEvents.contains(event.getEventId())) {
         LinkedList<EventType> path = new LinkedList<EventType>();
         path.add(event);

         return path;
      } else {
         Vector<EventType> predEventList = preds.get(event.getEventId());
         if (predEventList == null) {
            return null;
         } else if (predEventList.size() == 0) {
            GUITARLog.log.error(event.getEventId()
                                + " has empty predEventList");

            return null;
         } else {
            for (EventType pred : predEventList) {
               if (isVisited(pred, event)) {
                  continue;
               }

               List<EventType> predVisitedEvent =
                  visited.get(event.getEventId());
               if (predVisitedEvent == null) {
                  predVisitedEvent = new ArrayList<EventType>();
               }
               predVisitedEvent.add(pred);
               visited.put(event.getEventId(), predVisitedEvent);
                                      
               LinkedList<EventType> predPathToRoot = pathToRootInt(pred);

               if (predPathToRoot == null) {
                  continue;
               } else if (!isContains(predPathToRoot, event)) {
                  predPathToRoot.add(event);
                  return predPathToRoot;
               } // if
            } // for

            return null;
         }
      }
   }

   /**
    * Lookup event structure in 'inputGraph' given the event ID.
    */
   public EventType
   lookupEvent(String eventID)
   {
      if (this.inputGraph == null) {
         return null;
      }

      List<EventType> lInputEvents = inputGraph.getEvents().getEvent();

      for (EventType event : lInputEvents) {
         if (event.getEventId().equals(eventID)) {
            return event;
         }
      }

      return null;
   }


   private boolean
   isVisited(EventType pred,
             EventType event)
   {
      List<EventType> predEventList = visited.get(event.getEventId());

      if (predEventList == null) {
         return false;
      }

      for (EventType aPredEvent : predEventList) {
         String eventID = aPredEvent.getEventId();
         String predID = pred.getEventId();

         if (eventID.equals(predID)) {
            return true;
         }
      }

      return false;
   }

   private boolean
   isContains(LinkedList<EventType> predPathToRoot,
              EventType event)
   {
      for (EventType anEvent : predPathToRoot) {
         if (anEvent.getEventId().equals(event.getEventId())) {
            return true;
         }
      }
      return false;
   }



} // Class
