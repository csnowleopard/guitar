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
package edu.umd.cs.guitar.model;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
<<<<<<< HEAD
 * @deprecated We are no longer manage window creation for Web
=======
 * @deprecated We no longer manage window creation for Web
>>>>>>> add_cj_browser_monitor
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
@Deprecated
public class WebWindowHandler {
	private RemoteWebDriver driver;
	private RemoteWebElement newPage;
	private String newPageHandle;
	private LinkedList<WebWindow> allWindows;
	private LinkedList<GWindow> newWindows;
	private String domainName = null;
	private static HashMap<RemoteWebDriver, WebWindowHandler> map;
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public WebWindowHandler(RemoteWebDriver driver) {
		this.driver = driver;
		this.domainName = domainName;
	}
	
	public void setUp() {
		map = new HashMap<RemoteWebDriver, WebWindowHandler>();
		driver.get(WebConstants.NEW_PAGE_URL);
		newPage = (RemoteWebElement)driver.findElement(By.id("NewPageLink"));
		newPageHandle = driver.getWindowHandle();
		map.put(driver, this);
		newWindows = new LinkedList<GWindow>();
		allWindows = new LinkedList<WebWindow>();
	}

	public WebWindow createNewWindow() {
		return createNewWindow("about:blank");
	}

	public WebWindow createNewWindow(String URL) {
		//Store previous handle so we can go back to it
		String prev = driver.getWindowHandle();
		Set<String> oldSet = driver.getWindowHandles();
		driver.switchTo().window(newPageHandle);
		newPage.click();
		
		Set<String> newSet = driver.getWindowHandles();
		newSet.removeAll(oldSet);
		String handle = newSet.iterator().next();
		driver.switchTo().window(handle);
		driver.get(URL);
		WebWindow w = new WebWindow(driver, handle);
		
		if(domainName != null && !domainName.equals(w.getDomainName())) {
			driver.close();
			driver.switchTo().window(prev);
			return null;
		}

		allWindows.add(w);
		newWindows.add(w);
		driver.switchTo().window(prev);
		return w;
	}
	
	public static WebWindowHandler findByDriver(RemoteWebDriver d) {
		return map.get(d);
	}
	
	public static WebWindowHandler getWebWindowHandler() {
		return map.values().iterator().next();
	}

	public void resetWindowCache() {
		newWindows = new LinkedList<GWindow>();
	}

	public LinkedList<GWindow> getOpenedWindowCache() {
		return newWindows;
	}

	public LinkedList<WebWindow> getAllWindows() {
		return allWindows;
	}
	
	public int getNumOpenWindows() {
		return driver.getWindowHandles().size() - 1;
	}

	public String getLegalWindow() {
		//Returns any legal window for driver
		if(driver.getWindowHandles().isEmpty())
			return null;
		
		return driver.getWindowHandles().iterator().next();
	}

	public WebWindow getWindowByURL(String sWindowTitle) {
		for(WebWindow w : allWindows) {
			if(w.getTitle().equals(sWindowTitle))
				return w;
		}
		
		return null;
	}

	public void close(String handle) {
		for(int i = allWindows.size() - 1; i >= 0; i--) {
			if(allWindows.get(i).getHandle().equals(handle))
				allWindows.remove(i);
		}
	}

	public void close(GWindow window) {
		allWindows.remove(window);		
	}
}
