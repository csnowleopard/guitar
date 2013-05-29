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
package edu.umd.cs.guitar.replayer2;

import org.apache.log4j.Logger;

import edu.umd.cs.guitar.replayer2.plugin.GAfterTestStep;

import edu.umd.cs.guitar.replayer2.plugin.GBeforeTestStep;

import edu.umd.cs.guitar.replayer2.plugin.GAfterTestCase;

import edu.umd.cs.guitar.replayer2.plugin.GBeforeTestCase;

import edu.umd.cs.guitar.model.plugin.GPlugin;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.exception.ApplicationTerminatedException;
import edu.umd.cs.guitar.exception.ComponentDisabled;
import edu.umd.cs.guitar.exception.ComponentNotFound;
import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.exception.ReplayerStateException;
import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.wrapper.GUIMapWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 *
 *
 * <p>
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class Replayer2 {

  /**
   * 
   */
  private static final int DEFAULT_TIME_TO_WAIT = 10000;

  /**
   * Platform specific replayer monitor
   */
  GReplayerMonitor2 monitor;

  /**
   * Application under test
   */
  GApplication application;

  /**
   * {@link GUIMap} file to look for GUI components
   */
  GUIMap guiMap;

  /**
   * List of plugins to execute
   */
  private List<GPlugin> pluginList = new ArrayList<GPlugin>();

  /**
   * Add a plugin to the replayer
   * <p>
   *
   * @param plugin
   * @return
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean addPlugin(GPlugin plugin) {
    return pluginList.add(plugin);
  }

  /**
   * Add a plugin to the replayer at particular index
   * <p>
   *
   * @param index
   * @param plugin
   * @see java.util.List#add(int, java.lang.Object)
   */
  public void addPlugin(int index, GPlugin plugin) {
    pluginList.add(index, plugin);
  }

  /**
   * Time out for searching GUI element
   */
  private long timeToWait = DEFAULT_TIME_TO_WAIT;


  /**
   * @param timeToWait the timeToWait to set
   */
  public void setTimeToWait(long timeToWait) {
    this.timeToWait = timeToWait;
  }

  private Logger LOGGER = Logger.getLogger(Replayer2.class);

  /**
   * @param monitor Platform specific replayer monitor
   * @param application Application under test
   * @param guiMap Component lookup map
   */
  public Replayer2(GReplayerMonitor2 monitor, GApplication application, GUIMap guiMap) {
    super();
    this.monitor = monitor;
    this.application = application;
    this.guiMap = guiMap;
  }

  /**
   * @see #Replayer2(GReplayerMonitor2, GApplication, GUIMap)
   * @param monitor
   * @param application
   * @param guiMapFile
   */
  public Replayer2(GReplayerMonitor2 monitor, GApplication application, String guiMapFile) {
    this(monitor, application, (GUIMap) IO.readObjFromFile(guiMapFile, GUIMap.class));
  }


  /**
   * Execute a single step in the test case
   *
   * <p>
   *
   * @param step
   * @throws ComponentNotFound
   * @throws ReplayerStateException
   */
  void executeStep(StepType step) throws ComponentNotFound, ReplayerStateException {

    for (GPlugin plugin : pluginList) {
      if (plugin instanceof GBeforeTestStep) {
        LOGGER.info("Applying plugin: " + plugin.getClass().getName());
        GBeforeTestStep beforeStepPlugin = (GBeforeTestStep) plugin;
        beforeStepPlugin.beforeStep(step, guiMap, application);
      }
    }

    // Events
    String eventID = step.getEventId();
    LOGGER.info("Executing Step EventID = " + eventID);
    if (this.guiMap == null) {
      GUITARLog.log.error("Lookup table not found.");
      throw new ReplayerStateException();
    }

    GUIMapWrapper mapWrapper = new GUIMapWrapper(this.guiMap);

    // Get widget ID and actions

    EventType event = mapWrapper.getEventByID(eventID);
    String widgetId = event.getWidgetId();

    LOGGER.info("Searching for widget:");
    LOGGER.info(" + Widget ID:  " + widgetId);

    WidgetMapElementType widgetElement = mapWrapper.getWidgetByID(widgetId);
    if (widgetElement == null) {
      LOGGER.error("Component not found in the lookup table.");
      throw new ComponentNotFound();
    }

    ComponentType component = widgetElement.getComponent();
    ComponentType window = widgetElement.getWindow();

    GComponent gComponent = null;

    // Continuous searching until a timeout is reached
    long startTime = System.currentTimeMillis();
    long elapsedTime = 0;
    while (gComponent == null && elapsedTime < timeToWait) {
      gComponent = monitor.getComponent(application, window, component);
      elapsedTime = System.currentTimeMillis() - startTime;
    }

    // // Matching widget was not found
    if (gComponent == null) {
      // Bail out with exception
      LOGGER.error("Component NOT found on the GUI.");
      LOGGER.error("Time to wait:" + this.timeToWait + "ms");
      throw new ComponentNotFound();
    }

    // Matching widget was found
    LOGGER.info("FOUND widget");
    LOGGER.info(" + Widget Title = " + gComponent.getTitle());
    if (!gComponent.isEnable()) {
      LOGGER.error(gComponent.getTitle() + " is disabled.");
      throw new ComponentDisabled();
    }

    // Execute action on matchd widget
    String action = event.getAction();
    if (action == null) {
      LOGGER.info("Error in getting action named ");
      throw new ComponentNotFound();
    }
    GEvent gEvent = getAction(action);
    List<String> parameters = step.getParameter();
    LOGGER.info(" + Action: " + action);


    // Optional data
    AttributesType optional = component.getOptional();
    Hashtable<String, List<String>> optionalValues = new Hashtable<String, List<String>>();;

    if (optional != null) {
      for (PropertyType property : optional.getProperty()) {
        optionalValues.put(property.getName(), property.getValue());
      }
    }

    if (parameters == null || parameters.size() == 0) {
      gEvent.perform(gComponent, optionalValues);
    } else {
      gEvent.perform(gComponent, parameters, optionalValues);
    }

    for (GPlugin plugin : pluginList) {
      if (plugin instanceof GAfterTestStep) {
        LOGGER.info("Applying plugin: " + plugin.getClass().getName());
        GAfterTestStep beforeStepPlugin = (GAfterTestStep) plugin;
        beforeStepPlugin.afterStep(step, guiMap, application);
      }
    }
  }


  /**
   * Create an action (event) using its class name
   *
   * @param actionName
   * @return
   */
  private GEvent getAction(String actionName) {
    GEvent retAction = null;

    try {
      Class<?> c = Class.forName(actionName);
      Object action = c.newInstance();

      retAction = (GEvent) action;

    } catch (Exception e) {
      GUITARLog.log.error("Error in getting action named " + actionName, e);
    }

    return retAction;
  }

  /**
   * SECTION: LOGIC
   *
   * This section contains the core logic for replaying a GUITAR testcase. Parse and run test case.
   *
   * @param testcase
   * @throws GException
   */
  public void execute(TestCase testcase) throws GException {
    LOGGER.info("------ BEGIN TESTCASE -----");
    boolean threw = true;
    try {
      LOGGER.info("Connecting to application");
      application.connect();
      LOGGER.info("Application is connected.");

      // Monitor before the test case
      for (GPlugin plugin : pluginList) {
        if (plugin instanceof GBeforeTestCase) {
          LOGGER.info("Applying plugin: " + plugin.getClass().getName());
          GBeforeTestCase beforeTestCasePlugin = (GBeforeTestCase) plugin;
          beforeTestCasePlugin.beforeTestCase(testcase, guiMap, application);
        }
      }

      LOGGER.info("Testcase size " + testcase.getStep().size() + " steps");

      List<StepType> lSteps = testcase.getStep();
      int nStep = lSteps.size();

      for (int i = 0; i < nStep; i++) {
        LOGGER.info("------- BEGIN STEP --------");
        StepType step = lSteps.get(i);
        executeStep(step);
        LOGGER.info("-------- END STEP ---------");
      }

      for (GPlugin plugin : pluginList) {
        if (plugin instanceof GAfterTestCase) {
          LOGGER.info("Applying plugin: " + plugin.getClass().getName());
          GAfterTestCase beforeTestCasePlugin = (GAfterTestCase) plugin;
          beforeTestCasePlugin.afterTestCase(testcase, guiMap, application);
        }
      }
      threw = false;
    } finally {
      try {
        LOGGER.info("Disconnecting....");
        application.disconnect();
        LOGGER.info("Disconnected.");
      } catch (Exception exception) {
        if (!threw) {
          LOGGER.info("Application is disconnected.");
          if (exception instanceof GException) {
            throw (GException) exception;
          } else {
            throw new ApplicationTerminatedException(
                "Error disconnecting from the application", exception);
          }
        } else {
          LOGGER.error("Error disconnecting from the application because "
              + "there is unexpected exception during replaying.");
        }
      }
      LOGGER.info("------- END TESTCASE ------");
    }
  }

  /**
   * @see #execute(TestCase, GUIMap)
   * @param testcaseFile
   * @param guiMapFile
   * @throws GException
   */
  public void execute(String testcaseFile) throws GException {
    TestCase testcase = (TestCase) IO.readObjFromFile(testcaseFile, TestCase.class);
    execute(testcase);
  }

} // End of class
