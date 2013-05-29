import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;



public class GUI {
	private TreeMap<ComparablePoint2D,String> map;
	
	public GUI(Document guiDoc,Set<String> idSet)  {
		try {
			XmlUtility.print(guiDoc);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		map=new TreeMap<ComparablePoint2D,String>();
		NodeList nodes = guiDoc.getChildNodes();
		nodes=(NodeList) nodes.item(0);
		

		for(int i = 0; i < nodes.getLength(); i++) {
			
			if(nodes.item(i).getNodeName().equals("GUI")){
				nodes=nodes.item(i).getChildNodes();
				break;
			}
		}
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeName().equals("Container")){
				nodes=nodes.item(i).getChildNodes();
				break;
			}
		}
		
		Queue<NodeList> queue=new LinkedList<NodeList>();
		queue.add(nodes);
		
		while(!queue.isEmpty()&&!idSet.isEmpty()){
			nodes=queue.poll();

			for(int i = 0; i < nodes.getLength(); i++) {
				//System.out.println(nodes.item(i).getNodeName());
				
				if(nodes.item(i).getNodeName().equals("Contents")){
					queue.add(nodes.item(i).getChildNodes());
				}
				if(nodes.item(i).getNodeName().equals("Container")){
					queue.add(nodes.item(i).getChildNodes());
				}
				if(nodes.item(i).getNodeName().equals("Attributes")){
					NodeList attributes =nodes.item(i).getChildNodes();
					String id="";
					int x=0,y=0;
					int curItem=0;
					for(int j = 0; j < attributes.getLength()&&!id.equals("BAD"); j++) {
						if(attributes.item(j).getNodeName().equals("Property")){
							NodeList property =attributes.item(j).getChildNodes();
							for(int k = 0; k < property.getLength(); k++) {
								//System.out.println(property.item(k).getNodeName());
								if(curItem==0&&property.item(k).getNodeName().equals("Name")){
									String content=property.item(k).getTextContent();
									if(content.equals("ID")){
										curItem=1;
									}else if(content.equals("X")){
										curItem=2;
									}else if(content.equals("Y")){
										curItem=3;
									}
								}else if(curItem!=0&&property.item(k).getNodeName().equals("Value")){
									String content=property.item(k).getTextContent();
									if(curItem==1){
										id=content.substring(1);
										if(!idSet.remove(id)){
											id="BAD";
											break;
										}
									}else if(curItem==2){
										x=Integer.parseInt(content);
									}else if(curItem==3){
										y=Integer.parseInt(content);
									}
									curItem=0;
								}
							}
						}
					}
					if(!id.equals("BAD")){
						System.out.println(id+":"+x+" "+y);
						map.put(new ComparablePoint2D(x,y), id);
					}
					
				}
			}
			System.out.println("---------------");
		}
	}
	public Map<String,String> monkeyIDToEventID(){
		Map<String,String> ret=new TreeMap<String,String>();
		int count=1;
		for(String id:map.values()){
			ret.put("#"+count, "e"+id);
			count++;
		}
		return ret;
	}
}
