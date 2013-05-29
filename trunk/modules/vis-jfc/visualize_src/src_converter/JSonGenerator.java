import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class JSonGenerator {
	
	//public JSONArray components;
	public JSonGenerator()
	{
	
		//components = new JSONArray();
	}
	/*
	public void add(ComponentNode node)
	{
		///*	Component Node Components
		// * String _wId, String _class,String _type, String _title,String _text,int eventIndex;
		 //*	boolean isRoot,String nodeType,ArrayList<ComponentNode> relations;
		 //* */ /*
		LinkedHashMap<String, Serializable> compMap = new LinkedHashMap<String, Serializable>();
		compMap.put("_wID",node._wId);
		compMap.put("_class",node._class);
		compMap.put("_type",node._type);
		compMap.put("_title",node._title);
		compMap.put("_text",node._text);
		compMap.put("_eventIndex",node.eventIndex);
		compMap.put("_isRoot",node.isRoot);
		compMap.put("_nodeType",node.nodeType);
		compMap.put("_relations",node.relationsString());
		compMap.put("_x",node._x);
		compMap.put("_y",node._y);
		components.add(compMap);
		
		
		
	}
	*/
	
	public String genJSONString(ArrayList<ComponentNode> nodes) {
		
		String ret = "{ \"bindings\": [";
		
		for(ComponentNode n : nodes) {
			ret = ret + "{";
			
			//put all the information into string from here
			ret = ret + "\"_wId\":\"" + n._wId + "\",";
			ret = ret + "\"_class\":\"" + n._class + "\",";
			ret = ret + "\"_type\":\"" + n._type + "\",";
			ret = ret + "\"_title\":\"" + n._title + "\",";
			ret = ret + "\"_text\":\"" + n._text + "\",";
			ret = ret + "\"_eventIndex\":" + n.eventIndex + ",";
			ret = ret + "\"_isRoot\":" + n.isRoot + ",";
			ret = ret + "\"_x\":" + n._x + ",";
			ret = ret + "\"_y\":" + n._y + ",";
			ret = ret + "\"relations\":" + n.relationsString();
			
			ret = ret + "},";
		}
		
		ret = ret.substring(0,ret.length()-1);
		
		ret = ret + "]};";
		//System.out.println(ret);
		return ret;
	}

}
