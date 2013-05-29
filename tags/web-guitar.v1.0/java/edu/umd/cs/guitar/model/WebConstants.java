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
package edu.umd.cs.guitar.model;

import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Constants specific to Web GUITAR
 *
 * <p>
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class WebConstants {

  // ------------------------------
  // Ignored components
  // ------------------------------

  public static String RESOURCE_DIR = "resources";
  public static String CONFIG_DIR = RESOURCE_DIR + File.separator + "config";

  public static String TERMINAL_WIDGET_FILE = "terminal_widget.ign";

  public static List<AttributesTypeWrapper> sTerminalWidgetSignature =
      new LinkedList<AttributesTypeWrapper>();
  public static List<String> sIgnoredWins = new ArrayList<String>();

  // ------------------------------
  // GUI Properties of interest, should be in lower case
  // ------------------------------

  /**
   * List of interested GUI properties
   */
  static List<String> GUI_PROPERTIES_LIST = Arrays.asList("opaque",
      "height",
      "width",
      "foreground",
      "background",
      "visible",
      "tooltip",
      "font",
      "accelerator",
      "enabled",
      "editable",
      "focusable",
      "selected",
      "text");

  static List<String> WINDOW_PROPERTIES_LIST = Arrays.asList("layout",
      "x",
      "y",
      "height",
      "width",
      "opaque",
      "visible",
      "alwaysOnTop",
      "defaultLookAndFeelDecorated",
      "font",
      "foreground",
      "insets",
      "resizable",
      "background",
      "colorModel",
      "iconImage",
      "locale");

  /**
   * Web specific tags
   *
   */
  public static final String TITLE_TAG = "Title";
  public static final String VALUE_TAG = "Value";
  public static final String INDEX_TAG = "Index";
  public static final String TAGID_TAG = "Tag ID";
  public static final String HREF_TAG = "Href";
  public static final String NAME_TAG = "Name";
  public static final String TEXT_TAG = "Text";
  public static final String TAG_NAME = "Tag";


  /**
   * The list of attributes captured for GUI element identification
   */
  public final static String[] ID_ATTRIBUTE_LIST =
      {"id", "name", "type", "href", "class", "title",};

  /**
   * List of properties used to identify a widget on the GUI (Deprecated)
   */
  @Deprecated
  public static List<String> ID_PROPERTIES =
      Arrays.asList(TITLE_TAG, TAGID_TAG, HREF_TAG, NAME_TAG);

  // ------------------------------
  // GUITAR LOG
  // ------------------------------

  public static final String LOG4J_PROPERTIES_FILE = // CONFIG_DIR
      // + File.separator +
      "log4j.properties"; // This name needs to be a command-line parameter

  // The URL for a site with one link to a new page.
  public static final String NEW_PAGE_URL = "http://www.cs.umd.edu/~baonn/gtac/index.html";
  // Keys to send to password
  public static final
      String KEYS_TO_SEND =
              "GUITAR DEFAULT TEXT:"
              + "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~" + "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþ" + "ÿĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǀǁǂǃ" + "ǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰ" + "ǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȠȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳȴȵȶȷȸȹȺȻȼȽȾȿɀɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏɐɑɒɓɔɕɖɗɘəɚɛɜɝɞɟɠɡɢɣɤɥɦɧɨɩɪɫɬɭɮɯɰɱɲɳɴɵɶɷɸɹɺɻɼɽɾɿʀʁʂʃʄʅʆʇʈʉʊʋʌʍʎʏʐʑʒʓʔʕʖʗʘʙʚʛʜʝʞʟʠʡʢʣʤʥʦʧʨʩʪʫʬʭʮʯʰʱʲʳʴʵʶʷʸʹ";

  // ------------------------------
  // Environment flags
  // ------------------------------

  public static final String GUITAR_WEB_URL_PROPERTY = "guitar.web.url";


  /**
	 * 
	 */
  public static final String GUITAR_GUIFILE_PROPERTY = "guitar.gui";
  public static final String GUITAR_EFGFILE_PROPERTY = "guitar.efg";
  public static final String GUITAR_TESTCASE_FILE_PROPERTY = "guitar.testcase";



}
