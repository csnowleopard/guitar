// Copyright 2012 Google Inc. All Rights Reserved.

package edu.umd.cs.guitar;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.util.Util;

import edu.umd.cs.guitar.model.WebWindowManager;

import edu.umd.cs.guitar.model.WebConstants;

import java.io.IOException;
import java.util.List;


import edu.umd.cs.guitar.model.GApplicationConnectionAdapter;

import edu.umd.cs.guitar.model.WebApplication;

/**
 * @author banguyen@google.com (Bao Nguyen)
 *
 */
public class WebApplicationTest {

  /**
   * 
   */
  // private static final String GA_WEB =
  // "https://www.google.com/analytics/web/";
  private static final String GA_WEB = "https://www.google.com/analytics/web/#report";
  WebDriver driver;
  WebApplication application;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    System.setProperty("log4j.configuration", "log/guitar-debug-only.glc");
    driver = new FirefoxDriver();
    String rootURL = GA_WEB;
    application = new WebApplication(rootURL, driver);
//    GApplicationConnectionAdapter loginAdpater = new GALoginConnectionAdapter();
//    application.setAdapter(loginAdpater);
    application.connect();
  }

  @Test
  public void testConnectAdapter() {
    WebDriver driver = new FirefoxDriver();
    String rootURL = GA_WEB;
    WebApplication application = new WebApplication(rootURL, driver);
//    GApplicationConnectionAdapter loginAdpater = new GALoginConnectionAdapter();
//    application.setAdapter(loginAdpater);

    application.connect();

    System.out.println("DONE");
  }

  @Test
  public void testExpandEvent() {
    List<WebElement> elementList = driver.findElements(By.xpath("//*[text()='Behavior']"));

    System.out.println("Total elements found: " + elementList.size());

    for (WebElement element : elementList) {
      System.out.println("--------------------");
      System.out.println("Element found");
      System.out.println("Text:" + element.getText());
      System.out.println("Tag:" + element.getTagName());

      WebElement parent = element.findElement(By.xpath("../.."));
      String contents = (String) ((JavascriptExecutor) driver).executeScript(
          "return arguments[0].innerHTML;", parent);
      contents = contents.replaceAll(">", ">\n");
      System.out.println("*" + contents + "*");


      // break;
    }
    System.out.println("DONE");
  }

  @Test
  public void testHTMLInspect() {

    String contents = driver.getPageSource();
    contents = contents.replaceAll(">", ">\n");
    System.out.println(contents);
    // List<WebElement> elementList = driver.findElements(By.xpath("//html"));
    //
    // System.out.println("Total elements found: " + elementList.size());
    //
    // for (WebElement element : elementList) {
    // System.out.println("--------------------");
    // System.out.println("Element found");
    // String contents = (String) ((JavascriptExecutor) driver).executeScript(
    // "return arguments[0].innerHTML;", element);
    // contents = contents.replaceAll(">", ">\n");
    // System.out.println("*" + contents + "*");
    //
    //
    // // break;
    // }
    System.out.println("DONE");
  }

  @Test
  public void testLinkInspect() {
    List<WebElement> elementList = driver.findElements(By.xpath("//a"));

    System.out.println("Total elements found: " + elementList.size());

    for (WebElement renderedWebElement : elementList) {
      // if (renderedWebElement.isDisplayed()) {
      if ("a".equals(renderedWebElement.getTagName().toLowerCase())) {
        String href = renderedWebElement.getAttribute("href");
        if (href == null) {
          continue;
        }

        System.out.println("Link detected:" + href);
        href = href.toLowerCase();

      }
    }
    // break;
    System.out.println("DONE");
  }


  @Test
  public void testMisc() {
    WebWindowManager wwm = WebWindowManager.getInstance(driver);
    wwm.createNewWindow(
        "https://www.google.com/analytics/web/#report//a31829066w58731222p59953644");
  }


  final static String[] ATTRIBUTE_LIST =
      {"id", "name", "type", "href", "class", "title", "size", "height", "width", "onclick"};

  /**
   * 
   */
  private void cleanup() {
    Runtime rt = Runtime.getRuntime();
    try {
      rt.exec("killall firefox");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

}
