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
package edu.umd.cs.guitar.crawljax.model;

import com.crawljax.condition.crawlcondition.CrawlConditionChecker;
import com.crawljax.condition.eventablecondition.EventableConditionChecker;
import com.crawljax.core.CandidateElement;
import com.crawljax.core.CandidateElementExtractor;
import com.crawljax.core.CandidateElementManager;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.ExtractorManager;
import com.crawljax.core.configuration.CrawlElement;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawlSpecificationReader;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfigurationReader;

import java.util.List;

/**
 * An internal object to represent the set of candidate elements 
 * (to rip or to record)
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 *
 */
public class EventSpecification {

  CrawlSpecification crawlSpecification;
  CrawljaxConfiguration config;

  public EventSpecification() {
    this(new CrawlSpecification(""));
  }

  /**
   * @param crawlSpecification
   */
  public EventSpecification(CrawlSpecification crawlSpecification) {
    super();
    this.crawlSpecification = crawlSpecification;
    config = new CrawljaxConfiguration();
    config.setCrawlSpecification(crawlSpecification);
  }

  /**
   * @see
   *      com.crawljax.core.configuration.CrawlSpecification#click(java.lang.String)
   */
  public CrawlElement recordEventOnTag(String tagName) {
    return crawlSpecification.click(tagName);
  }

  /**
   * @see
   *      com.crawljax.core.configuration.CrawlSpecification#dontClick(java.lang.String)
   */
  public CrawlElement dontRecordEventOnTag(String tagName) {
    return crawlSpecification.dontClick(tagName);
  }

  /**
   * @see
   *      com.crawljax.core.configuration.CrawlSpecification#dontCrawlFrame(java.lang.String)
   */
  public void dontRecordFrame(String string) {
    crawlSpecification.dontCrawlFrame(string);
  }



  /**
   * Get the candidate elements to click on of the current session 
   * @param session
   *    current session
   *    
   * @return
   *    list of candidate elements
   * @throws CrawljaxException
   */
  public List<CandidateElement> getCandidateList(CrawlSession session) throws CrawljaxException {
    CrawljaxConfigurationReader configurationReader = new CrawljaxConfigurationReader(config);

    CrawlSpecificationReader crawlerReader = configurationReader.getCrawlSpecificationReader();

    EventableConditionChecker eventableConditionChecker =
        new EventableConditionChecker(configurationReader.getEventableConditions());

    CrawlConditionChecker crawlConditionChecker =
        new CrawlConditionChecker(crawlerReader.getCrawlConditions());

    ExtractorManager elementChecker =
        new CandidateElementManager(eventableConditionChecker, crawlConditionChecker);


    CandidateElementExtractor candidateExtractor =
        new CandidateElementExtractor(elementChecker, session.getBrowser(), null, crawlerReader);

    List<CandidateElement> candidateList =
        candidateExtractor.extract(configurationReader.getTagElements(),
            configurationReader.getExcludeTagElements(), true, session.getCurrentState());
    return candidateList;
  }


}
