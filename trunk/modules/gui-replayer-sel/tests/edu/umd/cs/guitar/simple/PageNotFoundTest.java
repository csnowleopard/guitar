/*
*   Copyright (c) 2009-@year@. The GUITAR group at the University of
*   Maryland.  Names of owners of this group may be obtained by sending an
*   e-mail to atif@cs.umd.edu
*
*   Permission is hereby granted, free of charge, to any person obtaining a
*   copy of this software and associated  documentation files (the "Software"),
*   to deal in the Software without restriction,  including without  limitation
*   the rights to use, copy, modify, merge, publish, distribute, sublicense,
*   and or sell copies of the Software, and to permit persons to whom the
*   Software is furnished to do so, subject to the following  conditions:
*
*   The above copyright notice and this permission notice shall be included in
*   all copies or substantial  portions of the Software.
*
*   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*   IMPLIED, INCLUDING BUT NOT  LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO  EVENT SHALL
*   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*   LIABILITY, WHETHER  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
*   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR  THE USE OR OTHER
*   DEALINGS IN THE SOFTWARE.
*/
package edu.umd.cs.guitar.simple;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import edu.umd.cs.guitar.model.WebWindowManager;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class PageNotFoundTest {
	/**
	 * 
	 */
	private static final String PAGENOTFOUND_URL = "http://pagenotfound.edu/";
	private static final String XYZ_TEST = "XYZ-TEST";

	@Test
	public void testPageNotFound() {
		
		WebDriver driver;
		driver = new FirefoxDriver();
		WebWindowManager wwm = WebWindowManager.getInstance(driver);
		wwm.createNewWindow(PAGENOTFOUND_URL);
		System.out.println(driver.getTitle());
		System.out.println(driver.getWindowHandles().size());
	}

	private String injectAnchorTag(String id, String url) {
		return String
				.format("var anchorTag = document.createElement('a'); "
						+ "anchorTag.appendChild(document.createTextNode('Start page for Web GUITAR'));"
						+ "anchorTag.setAttribute('id', '%s');"
						+ "anchorTag.setAttribute('href', '%s');"
						+ "anchorTag.setAttribute('target', '_blank');"
						+ "anchorTag.setAttribute('style', 'display:block;');"
						+ "document.getElementsByTagName('body')[0].appendChild(anchorTag);",
						id, url);
	}
}
