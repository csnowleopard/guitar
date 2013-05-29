package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.WebComponent;
import edu.umd.cs.guitar.model.WebWindowManager;

public class WebExpand implements GEvent {


  @Override
  public boolean isSupportedBy(GComponent gComponent) {
    return true;
  }

  @Override
  public void perform(GComponent gComponent, List<String> parameters,
      Hashtable<String, List<String>> optionalData) {
    perform(gComponent, optionalData);
  }

  @Override
  public void perform(GComponent gComponent, Hashtable<String, List<String>> optionalData) {
    if (gComponent instanceof WebComponent) {
      WebElement el = ((WebComponent) gComponent).getElement();
      el.click();
    }
  }
}
