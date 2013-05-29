package edu.umd.cs.guitar.helper;

import java.util.List;

import edu.umd.cs.guitar.gen.PropertyType;


/**
 * This class provides utility methods throughout the module
 * @author Muhammad Ashraf Ishak
 *
 */
public class iGUITARHelper {

	/**
	 * Get property value based on its name.
	 * @param list List of PropertyType. 
	 * @param name Name of the property
	 * @return Value based on its name.
	 */
	public static String getProperty(List<PropertyType> list, String name){
		for (PropertyType p: list){
			if (p.getName().equals(name)){
				return p.getValue().get(0);
			}
		}
		return "N/A";
	}
}
