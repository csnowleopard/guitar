/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland.
 * Names of owners of this group may be obtained by sending an e-mail to
 * atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

package edu.umd.cs.guitar.ripper;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.WebOpenNewInternalLink;
import edu.umd.cs.guitar.exception.RipperConstructionException;
import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.WebApplication;
import edu.umd.cs.guitar.model.WebDefaultIDGenerator;
import edu.umd.cs.guitar.ripper.adapter.GRipperAdapter;
import edu.umd.cs.guitar.ripper.monitor.GRipperStepMonitor;
import edu.umd.cs.guitar.ripper.monitor.RipperDelayMonintor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * Builder class for the WebRipper
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 *
 */
public class WebRipperBuilder extends GRipperBuilder {
  /**
   * The {@link GApplication object representing the web page to rip};
   */
  WebApplication application;

  /**
   * Regular expression pattern to specify the internal domain
   */
  String internalDomainPattern = null;

  /**
   * List of events used to expand the GUI
   */
  private static List<Class<? extends GEvent>> addtionalExpandingEventList =
      new ArrayList<Class<? extends GEvent>>();

  /**
   * List of events captured by the ripper and later on supported by the
   * replayer
   */
  private static Set<Class<? extends GEvent>> supportedEventList =
      new HashSet<Class<? extends GEvent>>();


  /**
   * List of ripper adapters
   */
  private static List<GRipperAdapter> ripperAdapterList = new ArrayList<GRipperAdapter>();

  /**
   * List of ripper step monitor
   */
  private static List<GRipperStepMonitor> ripperStepMonitorList =
      new ArrayList<GRipperStepMonitor>();

  /**
   * ID generator
   */
  GIDGenerator idGenerator;

  /**
   * delay time once an element is expanded it is the time for the DOM to get
   * refreshed
   */
  int expandingDelay;

  /**
   * the delay time between every element ripping step regardless of if the
   * element is expandable or not
   */
  int stepDelay = 0;


  /**
   * Set the delay time once an element is expanded
   * <p>
   *
   * @param expandingDelay the delay to set
   */
  public WebRipperBuilder withExpandingDelay(int expandingDelay) {
    this.expandingDelay = expandingDelay;
    return this;
  }


  /**
   * Set the delay time between ripping step
   *
   * @param stepDelay
   * @return
   */
  public WebRipperBuilder withStepDelay(int stepDelay) {
    this.stepDelay = stepDelay;
    return this;
  }


  /**
   * @param ripperAdapter
   * @return the builder
   * @see java.util.List#add(java.lang.Object)
   */
  public WebRipperBuilder addRipperAdapter(GRipperAdapter ripperAdapter) {
    ripperAdapterList.add(ripperAdapter);
    return this;
  }

  /**
   * @param index
   * @param ripperAdapter
   * @see java.util.List#add(int, java.lang.Object)
   */
  public WebRipperBuilder addRipperAdapter(int index, GRipperAdapter ripperAdapter) {
    ripperAdapterList.add(index, ripperAdapter);
    return this;
  }

  /**
   * set the internal domain pattern
   *
   * @param internalDomainPattern the internalDomainPattern to set
   */
  public WebRipperBuilder withInternalDomainPattern(String internalDomainPattern) {
    this.internalDomainPattern = internalDomainPattern;
    return this;
  }

  /**
   * set ID generator for the Ripper
   *
   * @param idGenerator
   * @return the builder
   */
  public WebRipperBuilder withIDGenerator(GIDGenerator idGenerator) {
    this.idGenerator = idGenerator;
    return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * edu.umd.cs.guitar.ripper.GRipperBuilder#withApplication(edu.umd.cs.guitar
   * .model.GApplication)
   */
  @Override
  public WebRipperBuilder withApplication(GApplication application) {
    if (!(application instanceof WebApplication)) throw new RipperConstructionException();
    this.application = (WebApplication) application;
    return this;
  }


  /**
   * @param event
   * @return the builder
   */
  public WebRipperBuilder addExpandingEvent(Class<? extends GEvent> event) {
    addtionalExpandingEventList.add(event);
    return this;
  }



  /**
   * @param eventList
   * @return the builder
   * @see java.util.List#addAll(java.util.Collection)
   */
  public WebRipperBuilder addSupportedEventList(
      Collection<? extends Class<? extends GEvent>> eventList) {
    supportedEventList.addAll(eventList);
    return this;
  }

  /**
   * @param event
   * @return the builder
   */
  public WebRipperBuilder addSupportedEvent(Class<? extends GEvent> event) {
    supportedEventList.add(event);
    return this;
  }

  /**
   * The builder method to create a ripper
   *
   * @return the ripper
   */
  @Override
  public RipperWithMonitor build() {
    // Ripper ripper = new Ripper();
    RipperWithMonitor ripper = new RipperWithMonitor();

    // Create monitor
    WebRipperMonitor webRipperMonitor = new WebRipperMonitor(application);
    ripper.setMonitor(webRipperMonitor);

    // add ID generator, use the default one if not
    // set
    if (idGenerator == null) {
      idGenerator = WebDefaultIDGenerator.getInstance();
    }
    ripper.setIDGenerator(idGenerator);

    // Configure ripping process
    if (internalDomainPattern != null) {
      WebOpenNewInternalLink.setDomainPattern(internalDomainPattern);
    }

    // Default ripping event is to follow the links
    webRipperMonitor.addExpandingEvent(0, WebOpenNewInternalLink.class);

    // Add additional expanding event to the list
    webRipperMonitor.addExpandingEventList(addtionalExpandingEventList);

    // set delay
    webRipperMonitor.setDelay(expandingDelay);

    // Add supported events to the event manager
    EventManager em = EventManager.getInstance();
    for (Class<? extends GEvent> event : supportedEventList) {
      try {
        GEvent gEvent = event.newInstance();
        em.registerEvent(gEvent);
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    // Add ripper adapters
    if (ripperAdapterList != null) {
      for (GRipperAdapter ripperAdapter : ripperAdapterList) {
        ripper.addComponentFilter(ripperAdapter);
      }
    }

    // Add ripping step delay
    if (stepDelay > 0) {
      ripper.addComponentRippingMonitor(new RipperDelayMonintor(stepDelay));
    }

    // Add ripper step monitor
    if (ripperStepMonitorList != null) {
      for (GRipperStepMonitor ripperStepMonitor : ripperStepMonitorList) {
        ripper.addComponentRippingMonitor(ripperStepMonitor);
      }
    }

    return ripper;
  }


  /**
   * @param stepMonitor
   * @return
   */
  public WebRipperBuilder addStepMonitor(GRipperStepMonitor stepMonitor) {
    ripperStepMonitorList.add(stepMonitor);
    return this;
  }


  /**
   * @param index
   * @param stepMonitor
   */
  public WebRipperBuilder addStepMonitor(int index, GRipperStepMonitor stepMonitor) {
    ripperStepMonitorList.add(index, stepMonitor);
    return this;
  }


  /**
   * @param stepMonitor
   * @return
   */
  public WebRipperBuilder removeStepMonitor(Object stepMonitor) {
    ripperStepMonitorList.remove(stepMonitor);
    return this;
  }

}
