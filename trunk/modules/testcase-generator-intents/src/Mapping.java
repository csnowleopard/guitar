import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Mapping {
	static HashMap<String, HashSet<Intent>> temp = new HashMap<String, HashSet<Intent>>();
	static HashSet<HashMap<Integer, String>> completePath = new HashSet<HashMap<Integer,String>>();

	public static void main(String args[]){
		Intent ia = new Intent(); ia.activity = "baba.b";
		Intent ib = new Intent(); ib.activity = "caca.c";
		Intent ic = new Intent(); ic.activity = "aba.a";
		Intent id = new Intent(); id.activity = "aba.a";

		HashSet<Intent> ha = new HashSet<Intent>(); ha.add(ia); 
		HashSet<Intent> hb = new HashSet<Intent>(); hb.add(ib);
		HashSet<Intent> hc = new HashSet<Intent>(); hc.add(ic);
		HashSet<Intent> hd = new HashSet<Intent>(); hd.add(id);

		temp.put("aba.a", ha); temp.put("baba.b",  hb); temp.put("caca.c", hc);
		temp.put("d", hd);
		for(String src: temp.keySet()){
			HashMap<Integer,String> path = new HashMap<Integer,String>();
			path.put(1,src);
			Boolean bla = iterate(path, src);
		}

		for(HashMap<Integer, String> h: completePath){
			for(int i = 1; i < h.size(); i++)
				System.out.print(h.get(i) + " -> ");
			System.out.println();
		}
	}

	public static boolean iterate(HashMap<Integer,String> path, String curAct){
		if(!temp.containsKey(curAct)){
			completePath.add(path);
			return true;
		}

		for(Intent targetIntent: temp.get(curAct)){
			String act = targetIntent.activity;
			if(path.values().contains(act)){
				completePath.add(path);
			} else {
				int i = path.size()+1;
				path.put(i, act);
				Boolean bla = iterate(path, act);
			}
		}
		return true;

	}
}
