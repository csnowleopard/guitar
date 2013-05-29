import java.util.Set;
import java.util.TreeSet;

import javax.xml.transform.TransformerException;



import org.w3c.dom.Document;
import org.w3c.dom.NodeList;





public class EFG {

	private Set<String> id;
	public EFG(Document efgDoc)  {
		id=new TreeSet<String>();
		try {
			XmlUtility.print(efgDoc);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList nodes = efgDoc.getChildNodes();
		nodes=(NodeList) nodes.item(0);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeName().equals("Events")){
				nodes=nodes.item(i).getChildNodes();
				break;
			}
		}
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeName().equals("Event")){
				NodeList event =nodes.item(i).getChildNodes();
				for(int j = 0; j < event.getLength(); j++) {
					if(event.item(j).getNodeName().equals("EventId")){
						id.add(event.item(j).getTextContent().substring(1));
						break;
					}
				}
			}
		}
		System.out.println(id.toString());
	}
	public Set<String> getIDs(){
		return new TreeSet<String>(id);
	}

}
