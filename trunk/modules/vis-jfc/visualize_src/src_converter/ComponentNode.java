import java.io.Serializable;
import java.util.ArrayList;


public class ComponentNode implements Serializable {
	public String _wId;
	public String _class;
	public String _type;
	public String _title;
	public String _text;

	public int _x;
	public int _y;

	public int eventIndex;
	public boolean isRoot = false;

	public String nodeType;

	public ArrayList<ComponentNode> relations;

	public ComponentNode() {
		relations = new ArrayList<ComponentNode>();
	}	

	public String relationsString() {
		String ret = "";
		ret = ret + "[";
		for(ComponentNode c : relations) {
			ret = ret + "\"" + c._wId + "\",";
		}
		if(relations.size() > 0)
			ret = ret.substring(0,ret.length()-1);
		ret = ret + "]";
		return ret;
	}

	public String toString() {
		return _wId;
	}
}
