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
package edu.umd.cs.guitar.crawljax.ripper;

import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.plugin.Plugin;

import edu.umd.cs.guitar.exception.RipperConstructionException;
import edu.umd.cs.guitar.model.PageLoadPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link CrawljaxWebRipper}.
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class CrawljaxWebRipperBuilder {

  private String entryUrl = null;
  private PageLoadPlugin pageLoadPlugin = null;

  private RippingSpecification rippingSpecification;
  private CrawljaxConfiguration crawljaxConfiguration;
  private List<Plugin> crawljaxPluginList = new ArrayList<Plugin>();

  private boolean isCaptureScreenshot = false;
  private String screenshotDir = ".";

  public CrawljaxWebRipperBuilder(RippingSpecification rippingSpecification) {
    super();
    this.crawljaxConfiguration = new CrawljaxConfiguration();
    this.rippingSpecification = rippingSpecification;
  }



  /**
   * Setups the customized page loading plugin
   *
   * @param pageLoadPlugin the webInitializer to set
   */
  public CrawljaxWebRipperBuilder withPageLoadPlugin(PageLoadPlugin pageLoadPlugin) {
    this.pageLoadPlugin = pageLoadPlugin;
    return this;
  }

  /**
   * Set up entry URL to start ripping.
   *
   * @param entryUrl the entryUrl to set
   */
  public CrawljaxWebRipperBuilder withEntryUrl(String entryUrl) {
    this.entryUrl = entryUrl;
    return this;
  }

  public CrawljaxWebRipperBuilder withCrawljaxPlugin(Plugin plugin) {
    crawljaxPluginList.add(plugin);
    return this;
  }

  /**
   * Enables/disables screenshot capturing.
   *
   * @param isCaptureScreenshot the isCaptureScreenshot to set
   */
  public CrawljaxWebRipperBuilder captureScreenshot(boolean isCaptureScreenshot) {
    this.isCaptureScreenshot = isCaptureScreenshot;
    return this;
  }

  /**
   * Sets screenshot output directory.
   *
   * @param screenshotDir the screenshotDir to set
   */
  public CrawljaxWebRipperBuilder withScreenshotDir(String screenshotDir) {
    this.isCaptureScreenshot = true;
    this.screenshotDir = screenshotDir;
    return this;
  }

  /**
   * Builds the {@link CrawljaxWebRipper} object.
   */
  public CrawljaxWebRipper build() throws RipperConstructionException {

    if (this.entryUrl == null) {
      throw new RipperConstructionException("Need to specify URL");
    }

    // Add initialization plugin.
    if (pageLoadPlugin != null) {
      Plugin webInitializerViaCrawjax = new WebInitializerViaCrawjax(pageLoadPlugin);
      this.crawljaxConfiguration.addPlugin(webInitializerViaCrawjax);
    }

    for (Plugin plugin : crawljaxPluginList) {
      this.crawljaxConfiguration.addPlugin(plugin);
    }

    // set URL
    setUrl(rippingSpecification, entryUrl);

    CrawljaxWebRipper ripper = new CrawljaxWebRipper(rippingSpecification, crawljaxConfiguration);
    ripper.setCaptureScreenshot(isCaptureScreenshot);
    ripper.setScreenshotDir(screenshotDir);

    return ripper;
  }

  // -------------------------
  // Private utility methods.
  // -------------------------
  /**
   * This is a work around to separate the assignment of URL  and crawl element specification  
   * of the {@link CrawlSpecification} object. 
   *   
   * The current {@link CrawlSpecification}'s API does not allow to
   * modify the URL once it is set at the constructor. We only able to configure the
   * crawl element specification once the URL is fixed. This prevents us from sharing 
   * the configuration between different URL and sharing the URL between the ripper and replayer.
   * 
   * TODO(banguyen): Find a safer approach to overcome this limitation other than using 
   * reflection to change the private variable. 
   */
  private void setUrl(RippingSpecification rippingSpecification, String url)
      throws RipperConstructionException {
    try {
      CrawlSpecification spec = rippingSpecification.getCrawljaxSpecification();
      @SuppressWarnings("unchecked")
      Class<CrawlSpecification> aClass = (Class<CrawlSpecification>) spec.getClass();
      Field field;
      field = aClass.getDeclaredField("url");
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      int prevModifier = field.getModifiers();
      modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
      field.set(spec, url);
      modifiersField.set(field, prevModifier);
      field.setAccessible(false);
    } catch (SecurityException e) {
      throw new RipperConstructionException("Unable to assign entry URL to rip");
    } catch (NoSuchFieldException e) {
      throw new RipperConstructionException("Unable to assign entry URL to rip");
    } catch (IllegalAccessException e) {
      throw new RipperConstructionException("Unable to assign entry URL to rip");
    }
  }

}
