package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import org.openqa.selenium.remote.*;
import org.openqa.selenium.WebElement;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.WebComponent;

public class WebSubmit implements GEvent {

	@Override
	public boolean isSupportedBy(GComponent gComponent) {
		if(gComponent instanceof WebComponent) {
			WebComponent webComponent = (WebComponent) gComponent;
			if( webComponent.getElement() instanceof RemoteWebElement ) {
				RemoteWebElement renderedWebElement = (RemoteWebElement) webComponent.getElement();
				if(renderedWebElement.isDisplayed()) {
					String tagName = webComponent.getElement().getTagName().toLowerCase();
					if("input".equals(tagName) && 
						"submit".equals(webComponent.getElement().getAttribute("type").toLowerCase())) {
						return true;
					}
					if("button".equals(tagName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void perform(GComponent gComponent, List<String> parameters,
			Hashtable<String, List<String>> optionalData) {
		perform(gComponent, optionalData);
	}

	@Override
	public void perform(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {

		if(gComponent instanceof WebComponent) { 
			WebElement el = ((WebComponent) gComponent).getElement();
			
			if(!(el instanceof WebElement)) {
				return;
			} else {
				if(!((WebElement) el).isDisplayed())
					return;
			}
			
			el.click();
		}
	}

}
