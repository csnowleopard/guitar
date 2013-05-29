/*
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included
 *   in all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *   OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *   IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *   TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 *   THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.testcase.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;

import edu.umd.cs.guitar.testcase.TestCaseGeneratorConfiguration;

import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.util.GraphUtil;
import edu.umd.cs.guitar.util.TimeCounter;

/**
 * 
 * Randomly generate EIG test case with a specific length
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class RandomSequenceLengthCoverage extends GTestCaseGeneratorPlugin {

   /**
    * 
    */
   private static final int ALL_TEST_CASES = 0;

   // Requested # testcases (0=all)
   int             nReqTestcases;

   // Generate all possible test cases
   boolean         isGenAll;

   // Index of all testcases considered in a linear manner
   int             index;

   // Current testcase being generated
   int             generatedTestcases;

   // Testcases skipped during generation
   int              nSkippedDup;
   int              nSkippedPath;

   // Max possible testcases
   int             nAllTestcases;

   List<Integer>   lTestCaseIndex;

   // Graph utility for computing path to root
   GraphUtil       graphUtil;

   /*
    * (non-Javadoc)
    * 
    * @see edu.umd.cs.guitar.testcase.plugin.TCPlugin#isValidArgs()
    */
   @Override
   public boolean
   isValidArgs()
   {
      // Check instance
      if (TestCaseGeneratorConfiguration.LENGTH == null) {
         GUITARLog.log.error("Missing testcase generator configuration");
         return false;
      }
      if (TestCaseGeneratorConfiguration.EFG_FILE == null) {
         GUITARLog.log.error("Missing input graph file");
         return false;
      }

      return true;
   }

   private boolean
   isSelectedEvent(EventType event) {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see edu.umd.cs.guitar.testcase.plugin.TCPlugin#generate()
    */
   @Override
   public void
   generate(EFG efg,
            String outputDir,
            int nReqTestcases,
            boolean noDuplicateEvent,
            boolean treatTerminalEventSpecially)
   {
      new File(outputDir).mkdir();

      this.efg = efg;
      this.nReqTestcases = nReqTestcases;

      if (efgx == null) {
         /*
          * Use EFG as path-to-root lookup if additional graph
          * is not specified.
          */
         graphUtil = new GraphUtil(efg);
      } else {
         /*
          * Use additional EFG as path-to-root lookup if given.
          */
         graphUtil = new GraphUtil(efgx);
      }

      /*
       * This flas is ignored currently.
       * At present, duplicate testcases are skipped based
       * on index of testcase (also filename)
       */
      this.noDuplicateEvent = noDuplicateEvent;

      this.treatTerminalEventSpecially = treatTerminalEventSpecially;

      initialize();

      List<EventType> eventList = efg.getEvents().getEvent();
      List<EventType> interactionEventList = new ArrayList<EventType>();

      for (EventType event : eventList) {
         if (isSelectedEvent(event)) {
            interactionEventList.add(event);
         }
      }

      nAllTestcases      = 0;
      nSkippedDup        = 0;
      nSkippedPath       = 0;
      index              = 0;
      generatedTestcases = 0;

      // Count total number of test cases
      for (EventType e : interactionEventList) {
         nAllTestcases += countTestCases(
               TestCaseGeneratorConfiguration.LENGTH, e);
      }

      GUITARLog.log.info("Total number of potential test cases: "
                         + nAllTestcases);

      lTestCaseIndex = new ArrayList<Integer>();
      int i = 0;

      if (nReqTestcases == 0) {
         isGenAll = true;
         nReqTestcases = nAllTestcases;
      } else {
         nReqTestcases =
            (nAllTestcases > TestCaseGeneratorConfiguration.MAX_NUMBER)
             ? TestCaseGeneratorConfiguration.MAX_NUMBER
             : nAllTestcases;
      }

      Random random = new Random();
      if (!isGenAll) {
         GUITARLog.log.debug("Generating random index");
         while (i < nReqTestcases) {
            int nRandomIndex = random.nextInt(nAllTestcases);

            if (!lTestCaseIndex.contains(nRandomIndex)) {
               lTestCaseIndex.add(nRandomIndex);
               GUITARLog.log.debug(nRandomIndex);
               i++;
            }
         }
      }

      // A loop to generate test case
      TimeCounter timeCounter = new TimeCounter("RandomSequenceLength");
      timeCounter.start();

      for (EventType aEvent : interactionEventList) {
         LinkedList<EventType> initialList = new LinkedList<EventType>();

         initialList.add(aEvent);
         generateRandomWithLength(TestCaseGeneratorConfiguration.LENGTH,
                                  initialList);
      }

      timeCounter.printInfo();

      GUITARLog.log.info("Generated " + generatedTestcases +
                         " SkippedDup " + nSkippedDup +
                         " SkippedPath " + nSkippedPath);
   }


   /**
    * Count the total number of potential test cases. Counting the
    * maximum possible testcases is based off the input graph.
    * (not the additional graph). This is because the main input
    * graph determines the set of testcases which can potentially
    * be generated from it.
    * 
    * <p>
    * 
    * @param length
    * @param root
    * @return
    */
   private int
   countTestCases(int length,
                  EventType root)
   {

      if (length <= 1) {
         return 1;
      } else {
         int count = 0;

         for (EventType succEvent : succs.get(root)) {

            // Ignore non-interaction events
            if (!isSelectedEvent(succEvent)) {
               continue;
            }

            count += countTestCases(length - 1, succEvent);
         }

         return count;
      }
   }

   private void
   generateRandomWithLength(int length,
                            LinkedList<EventType> postfix)
   {
      if (generatedTestcases > nReqTestcases &&
          nReqTestcases != ALL_TEST_CASES) {
         return;
      }

      if (length <= 1) {
         EventType root = postfix.getFirst();

         LinkedList<EventType> path =
            graphUtil.pathToRoot(postfix.getFirst());

         index++;

         if (path != null) {
            if (isGenAll || lTestCaseIndex.contains(index)) {
               // Remove the root event itself to avoid duplication
               if (path.size() > 0) {
                  path.removeLast();
               }

               LinkedList<EventType> tTestCase = new LinkedList<EventType>();

               String sTestName = TEST_NAME_PREFIX;

               if (TestCaseGeneratorConfiguration.LENGTH < 30) {
                  for (EventType event : postfix) {
                     sTestName += ("_" + event.getEventId());
                  }
               } else {
                  sTestName += ("_" + index);
               }
               sTestName += TEST_NAME_SUFIX;

               // Write to file
               TestCase tc = factory.createTestCase();
               List<StepType> lStep = new ArrayList<StepType>();

               for (EventType e : path) {
                  StepType step = factory.createStepType();
                  step.setEventId(e.getEventId());
                  step.setReachingStep(true);
                  lStep.add(step);
               }

               for (EventType e : postfix) {
                  StepType step = factory.createStepType();
                  step.setEventId(e.getEventId());
                  step.setReachingStep(false);
                  lStep.add(step);
               }

               tc.setStep(lStep);

               String sPath = TestCaseGeneratorConfiguration.OUTPUT_DIR
                     + File.separator + sTestName;

               File file = new File(sPath);
               if (!file.exists()) {
                  /*
                   * This should not occur since the 'index' is unique.
                   * May occur for long testcases, since we limit the
                   * filename size to 30 events.
                   */
                  GUITARLog.log.info("Writting "
                                     + generatedTestcases + "/" + nAllTestcases
                                     + " " +  sPath);
                  IO.writeObjToFile(tc, sPath);

                  generatedTestcases++;
               } else {
                  GUITARLog.log.info("Skipped, duplicate event ID string");
                  nSkippedDup++;
               }
            }
         } else {
            GUITARLog.log.info("Skipped, no path to root "
                               + root.getWidgetId());
            nSkippedPath++;
         } // (path != null)

      } else {
         EventType lastEvent = postfix.getLast();

         for (EventType succEvent : succs.get(lastEvent)) {
            LinkedList<EventType> extendedPostfix = new LinkedList<EventType>(
                  postfix);
            extendedPostfix.addLast(succEvent);
            generateRandomWithLength(length - 1, extendedPostfix);
         }
      }
   }

   /**
    * A Debugging method
    */
   private void
   printGraph()
   {
      List<EventType> eventList = efg.getEvents().getEvent();
      int eventGraphSize = eventList.size();
      EventGraphType eventGraph = efg.getEventGraph();

      for (int row = 0; row < eventGraphSize; row++) {

         EventType currentEvent = eventList.get(row);
         Vector<EventType> s = new Vector<EventType>();

         for (int col = 0; col < eventGraphSize; col++) {

            EventType event = eventList.get(col);

            int relation = eventGraph.getRow().get(row).getE().get(col);

            if (relation == GUITARConstants.FOLLOW_EDGE) {
               GUITARLog.log.info(currentEvent.getEventId() + ","
                     + currentEvent.getWidgetId() + "->"
                     + event.getEventId() + "," + event.getWidgetId());
            }

         }
      }
   }

   /**
    * Just for debugging
    */
   private void debug() {

      GUITARLog.log.info("Debugging.........");
      List<EventType> eventList = efg.getEvents().getEvent();
      for (EventType e : eventList) {
         GUITARLog.log.info("================================");
         GUITARLog.log.info("Analyzing " + e.getEventId() + "-"
               + e.getWidgetId());
         List<EventType> predEventList = preds.get(e);

         String sEventList = "Pred: [";
         for (EventType predEvent : predEventList) {
            sEventList += (predEvent.getEventId() + "-"
                  + predEvent.getWidgetId() + ", ");

         }
         sEventList += "]";
         GUITARLog.log.info(sEventList);
      }
   }

}
