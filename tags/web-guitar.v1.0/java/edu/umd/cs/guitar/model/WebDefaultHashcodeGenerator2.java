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

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openqa.selenium.WebElement;


/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 *
 */
public class WebDefaultHashcodeGenerator2 implements GHashCodeGenerator2 {

  boolean includeText = true;

  /**
   * @return the includeText
   */
  public boolean isIncludedText() {
    return includeText;
  }

  /**
   * @param includeText the includeText to set
   */
  public void setIncludeText(boolean includeText) {
    this.includeText = includeText;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * edu.umd.cs.guitar.model.GHashCodeGenerator2#generateHashCode(edu.umd.cs
   * .guitar.model.GWindow, edu.umd.cs.guitar.model.GComponent)
   */
  @Override
  public long generateHashCode(GWindow window, GComponent gComponent) {
    return generateHashCode(gComponent);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.umd.cs.guitar.model.GHashCodeGenerator2#generateLocalHashCode(edu.
   * umd.cs.guitar.model.GComponent)
   */
  @Override
  public long generateHashCode(GComponent gComponent) {
    if (!(gComponent instanceof WebComponent)) {
      return 0;
    }
    WebComponent webComponent = (WebComponent) gComponent;
    WebElement element = webComponent.getElement();
    
    if (element == null) {
      return 0;
    }

    HashCodeBuilder hashcodeBuilder = new HashCodeBuilder();

    // Tag
    hashcodeBuilder.append(element.getTagName());

    if (isIncludedText()) {
      hashcodeBuilder.append(element.getText());
    }

    // ID attributes
    for (String attributeName : WebConstants.ID_ATTRIBUTE_LIST) {
      String attributeValue = element.getAttribute(attributeName);
      if (attributeValue != null && !("".equals(attributeName.trim()))) {
        hashcodeBuilder.append(attributeName);
        hashcodeBuilder.append(attributeValue);
      }
    }
    return Math.abs(hashcodeBuilder.toHashCode());
  }


}
