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
package edu.umd.cs.guitar.model;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
/**
 * Creates and Handles a New window  *
 */
public class WebWindowCreator{

    private WebDriver driver;
    private String handle;
    private String name;
    private String parentHandle;
    private static int instanceCount = 0;

    /**
     * Creates a new window for given web driver
     * @param parent WebDriver instance
     * @param url   Initial url to load
     * @return new WebWindow
     */
    public WebWindowCreator(WebDriver parent, String url) {

        this.driver = parent;
        parentHandle = parent.getWindowHandle();
        name = createUniqueName();
        handle = createWindow(url);
        switchToWindow().get(url);
    }

    public String getWindowHandle(){
        return handle;
    }

    public String getParentHandle(){
        return parentHandle;
    }

    public void close(){
        switchToWindow().close();
        handle = "";
        //Switch back to the parent window
        driver.switchTo().window(parentHandle);
    }

    private static String createUniqueName() {
        return "a_Web_Window_" + instanceCount++;
    }

    public WebDriver switchToWindow(){
        checkForClosed();
        return driver.switchTo().window(handle);
    }

    public WebDriver switchToParent(){
        checkForClosed();
        return driver.switchTo().window(parentHandle);
    }

    private String createWindow(String url){

        //Record old handles
        Set<String> oldHandles = driver.getWindowHandles();
        parentHandle = driver.getWindowHandle();

        //Inject an anchor element
        ((JavascriptExecutor) driver).
                executeScript(
                        injectAnchorTag(name,url)
                );

        //Click on the anchor element
        driver.findElement(By.id(name)).click();

        handle = getNewHandle(oldHandles);

        return  handle;
    }

    private String getNewHandle(Set<String> oldHandles){

        Set<String> newHandles = driver.getWindowHandles();
        newHandles.removeAll(oldHandles);

        //Find the new window
        for(String handle : newHandles )
            return handle;

        return null;
    }

    private void checkForClosed(){
        if ( handle  == null || handle.equals("") )
            throw new WebDriverException("Web Window closed or not initialized");
    }

    private String injectAnchorTag(String id, String url){
        return String.format(  "var anchorTag = document.createElement('a'); " +
                "anchorTag.appendChild(document.createTextNode('nwh'));" +
                "anchorTag.setAttribute('id', '%s');" +
                "anchorTag.setAttribute('href', '%s');" +
                "anchorTag.setAttribute('target', '_blank');" +
                "anchorTag.setAttribute('style', 'display:block;');" +
                "document.getElementsByTagName('body')[0].appendChild(anchorTag);",
                    id, url
            );
    }

}
