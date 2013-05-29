/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of
 * this group may be obtained by sending an e-mail to atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and or sell copies of the Software, and to permit persons to whom the Software is
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

import edu.umd.cs.guitar.exception.ReplayerConstructionException;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.PageLoadPlugin;
import edu.umd.cs.guitar.model.WebApplication;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.plugin.GPlugin;
import edu.umd.cs.guitar.replayer.InitializeViaGUITAR;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link Replayer2} supported for web platform
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class WebReplayer2Builder {


  /**
   * @param entryURL  URL to replay.
   */
  public WebReplayer2Builder(String entryURL) {
    super();
    this.entryURL = entryURL;
  }

  String entryURL;

  PageLoadPlugin initializer = null;

  GUIMap guiMap;

  private List<GPlugin> pluginList = new ArrayList<GPlugin>();

  public static final int DEFAULT_TIME_TO_WAIT_FOR_COMPONENT_TO_APPEAR = 5000;
  private int timeToWait = DEFAULT_TIME_TO_WAIT_FOR_COMPONENT_TO_APPEAR;

  public WebReplayer2Builder withEntryURL(String entryURL) {
    this.entryURL = entryURL;
    return this;
  }

  public WebReplayer2Builder withWebInitializer(PageLoadPlugin initializer) {
    this.initializer = initializer;
    return this;
  }


  /**
   * Set replayer time to wait until finding the desire element.
   *
   * @param milliseconds the timeout to set
   */
  public WebReplayer2Builder withTimeToWait(int milliseconds) {
    this.timeToWait = milliseconds;
    return this;
  }

  public WebReplayer2Builder withPlugin(GPlugin plugin) {
    this.pluginList.add(plugin);
    return this;
  }


  /**
   * Assign {@link GUIMap} for component lookup.
   */
  public WebReplayer2Builder withGUIMap(GUIMap guiMap) {
    this.guiMap = guiMap;
    return this;
  }

  public WebReplayer2Builder withGUIMap(String guiMapFile) {
    GUIMap localGUIMap = (GUIMap) IO.readObjFromFile(guiMapFile, GUIMap.class);
    return this.withGUIMap(localGUIMap);
  }


  /**
   * Add a plugin to the replayer2.
   */
  public WebReplayer2Builder addPlugin(GPlugin plugin) {
    pluginList.add(plugin);
    return this;
  }

  /**
   * Add a plugin to the replayer2 at particular index.
   */
  public WebReplayer2Builder addPlugin(int index, GPlugin plugin) {
    pluginList.add(index, plugin);
    return this;
  }

  /**
   * Build the repalyer2. 
   */
  public Replayer2 build() throws ReplayerConstructionException {

    if (this.guiMap == null) throw new ReplayerConstructionException("GUI Map not found");

    WebApplication webApplication = new WebApplication(this.entryURL);
    if (initializer != null) {
      GPlugin guitarLogin = new InitializeViaGUITAR(initializer);
      webApplication.addPlugin(guitarLogin);
    }

    GReplayerMonitor2 webReplayerMonitor = new WebReplayerMonitor2();

    Replayer2 replayer = new Replayer2(webReplayerMonitor, webApplication, this.guiMap);
    replayer.setTimeToWait(timeToWait);

    // Add plugin.
    for (GPlugin plugin : pluginList) {
      replayer.addPlugin(plugin);
    }
    return replayer;
  }

}
