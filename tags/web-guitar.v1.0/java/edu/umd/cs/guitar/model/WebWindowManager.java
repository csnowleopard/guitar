package edu.umd.cs.guitar.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.util.Debugger;

/*
 * Managing all windows
 */
public class WebWindowManager {
	/**
	 * 
	 */
	static final String GUITAR_NEW_PAGE_TAG = "GUITAR_NEW_PAGE_TAG";

	WebDriver driver;

	LinkedList<WebWindow> allOpenedWindows = new LinkedList<WebWindow>();
	LinkedList<GWindow> newOpenedWindows = new LinkedList<GWindow>();

	LinkedList<String> newWindowHanldes = new LinkedList<String>();

	String startPageHanlder;

	/**
	 * static map to manage all drivers and their associated window manager
	 */
	static Map<WebDriver, WebWindowManager> mapDriverWindowManager = new HashMap<WebDriver, WebWindowManager>();

	/**
	 * Create a new window manager for <code>driver</code> if not exist
	 * <p>
	 * 
	 * @param driver
	 * @return
	 */
	public static WebWindowManager getInstance(WebDriver driver) {
		WebWindowManager instance = mapDriverWindowManager.get(driver);
		if (instance == null) {
			instance = new WebWindowManager(driver);
			mapDriverWindowManager.put(driver, instance);
		}
		return instance;
	}

	/**
	 * @param driver
	 */
	private WebWindowManager(WebDriver driver) {
		this.driver = driver;
		// create start page
		this.driver.get("about:blank");

		this.startPageHanlder = driver.getWindowHandle();
		// Insert link to open a new page
		((JavascriptExecutor) driver).executeScript(injectAnchorTag(
				GUITAR_NEW_PAGE_TAG, "about:blank"));
	}

	/**
	 * @return the driver
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public WebWindow createNewWindow() {
		return createNewWindow("about:blank");
	}

	/**
	 * Open an URL in a new window by clicking on the link in the start page
	 * <p>
	 * 
	 * @param URL
	 * @return
	 */
	/**
	 * @param URL
	 * @return
	 */
	public WebWindow createNewWindow(String URL) {
		Set<String> oldSet = driver.getWindowHandles();

		driver.switchTo().window(startPageHanlder);

		// Click on the anchor element
		WebElement anchor;
		anchor = driver.findElement(By.id(GUITAR_NEW_PAGE_TAG));
		anchor.click();

		// Find the new window detected
		Set<String> newSet = driver.getWindowHandles();
		newSet.removeAll(oldSet);
		String newHandle = newSet.iterator().next();

		driver.switchTo().window(newHandle);
		driver.get(URL);

		WebWindow newWindow = new WebWindow(driver, newHandle);

		this.allOpenedWindows.add(newWindow);
		this.newWindowHanldes.add(newHandle);
		return newWindow;
	}

	public void resetWindowCache() {
		newWindowHanldes = new LinkedList<String>();
	}

	public LinkedList<GWindow> getOpenedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();
		for (String window : newWindowHanldes) {
			GWindow gWindow = new WebWindow(driver, window);
			retWindows.addLast(gWindow);
		}
		return retWindows;
	}

	public LinkedList<WebWindow> getAllWindows() {
		return allOpenedWindows;
	}

	public int getNumOpenWindows() {
		return driver.getWindowHandles().size() - 1;
	}

	public String getLegalWindow() {
		if (driver.getWindowHandles().isEmpty())
			return null;

		return driver.getWindowHandles().iterator().next();
	}

	public WebWindow getWindowByURL(String sWindowTitle) {
		for (WebWindow w : allOpenedWindows) {
			if (w.getTitle().equals(sWindowTitle))
				return w;
		}

		return null;
	}

	public void close(String handle) {
		for (int i = allOpenedWindows.size() - 1; i >= 0; i--) {
			if (allOpenedWindows.get(i).getHandle().equals(handle))
				allOpenedWindows.remove(i);
		}
	}

	public void close(GWindow window) {
		allOpenedWindows.remove(window);
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

	public static WebWindowManager findByDriver(WebDriver driver) {
		return mapDriverWindowManager.get(driver);
	}
}
