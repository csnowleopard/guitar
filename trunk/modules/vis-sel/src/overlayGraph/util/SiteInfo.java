package overlayGraph.util;

import java.io.IOException;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.*;

/**
 * Contains information on all the components on all pages on the website. Should only be initialized once in pre-processing. 
 */
public class SiteInfo {
    // URL -> (widgetID->Component)
	private Map<String, Map<String, Component>> urls;
	
	public SiteInfo(String fileLoc) {
		urls = new HashMap<String, Map<String, Component>>();
		initialize(fileLoc);
	}

	/**
	 * @param fileLoc File location of unified XML.
	 */
	public void initialize(String fileLoc) {
	    Document dom= this.getDocumentFromFile(fileLoc);
	    
		this.parseDocument(dom);
	}
	
	private Document getDocumentFromFile(String fileLoc){
		DocumentBuilder documentBuilder= null;
		Document document= null;
		try {
			
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		

		try {
			//parse using builder to get DOM representation of the XML file
			document= documentBuilder.parse(fileLoc);
		}catch(SAXException se) {
			
			se.printStackTrace();
		}catch(IOException ioe) {
			
			ioe.printStackTrace();
		}
		
		return document;
	}

	private void parseDocument(Document dom){
		//get the root elememt
		Element rootElement = dom.getDocumentElement();		
		//get a nodelist of <URL> elements
		NodeList rootNodeList= rootElement.getElementsByTagName("URL");
		
		if(rootNodeList != null && rootNodeList.getLength() > 0) {
			
			for(int i= 0 ; i < rootNodeList.getLength(); i++) {
				Element currElement= (Element)rootNodeList.item(i);				
				String url= currElement.getAttribute("id");				
				//get a nodelist of <EventInfo> elements
				NodeList widgets= currElement.getElementsByTagName("EventInfo");
				
				for (int j= 0; j < widgets.getLength(); j++) {
					Element currWidget= (Element) widgets.item(j);
					Component currComponent= buildComponent(currWidget, url);
					
				    //adds the widget into the urls hashmap
                    Map<String, Component> comps= urls.get(url);
                    
                    if (comps == null) {
                        comps = new HashMap<String, Component>();
                        urls.put(url, comps);
                    }
                    comps.put(currComponent.getWidgetID(), currComponent);
				}
			}
		}
		
		//get edges
		NodeList edges = rootElement.getElementsByTagName("Edge");
		
		for (int i = 0; i < edges.getLength(); i++) {
			Element el = (Element)edges.item(i);
			String fromWidgetID = el.getAttribute("fromWidgetId");
			String toWidgetID = el.getAttribute("toWidgetId");
			
			Component fromComp = getComponentFromId(fromWidgetID);
			Component toComp = getComponentFromId(toWidgetID);
			
			fromComp.getNeighbors().add(toComp);
		}
	}
	
	/**
	 * Searches every site for the Component with the given <code>id</code> and returns it.
	 * @param id
	 * @return The component with id of <code>id</code>, or null if none exists.
	 */
	public Component getComponentFromId(String id) {
	    for (Map<String, Component> comps : urls.values()) {
	        Component result = comps.get(id);
	        if (result != null)
	            return result;
	    }
	    
	    return null;
	}
	
	/**
	 * Builds a component from an EventInfo XML element.
	 * @param el
	 * @param url
	 * @return The built component
	 */
	private Component buildComponent(Element el, String url){
		
		String widgetID = el.getAttribute("WidgetId");
		String htmlTag = el.getAttribute("htmlTag");
		String nameTag = el.getAttribute("name");
		String eventType = el.getAttribute("eventType");
		String tagId = el.getAttribute("tagId");
		String href = el.getAttribute("url");
		int x = Integer.parseInt(el.getAttribute("X"));
		int y = Integer.parseInt(el.getAttribute("Y"));
		
		return new Component(widgetID, url, x, y, htmlTag, nameTag, eventType, tagId, href);
	}
	
	/**
	 * @param url
	 * @return A map of form (Widget ID)->(Component)
	 */
	public Map<String, Component> getPageComponents(String url) {
	    Map<String, Component> result= urls.get(url);
	    
	    if (result == null)
	        throw new RuntimeException("Could not get components, web page not found in crawl: " + url);
	    
		return result;
	}
	
}
