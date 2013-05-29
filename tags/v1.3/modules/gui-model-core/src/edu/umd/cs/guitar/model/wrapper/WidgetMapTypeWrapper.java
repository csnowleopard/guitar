package edu.umd.cs.guitar.model.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.data.WidgetMapType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WidgetMapType", propOrder = { "widgetMapElement" })
public class WidgetMapTypeWrapper extends WidgetMapType {

	static ObjectFactory factory = new ObjectFactory();

	/**
	 * @param widgetMapElement
	 */
	public WidgetMapTypeWrapper(List<WidgetMapElementType> widgetMapElement) {
		this.widgetMapElement = widgetMapElement;
	}

	public WidgetMapElementType getElementByEventID(String widgetID) {
		if (widgetMapElement == null)
			return null;
		for (WidgetMapElementType widgetElement : this.widgetMapElement) {

			ComponentType component = widgetElement.getComponent();
			ComponentTypeWrapper wComponent = new ComponentTypeWrapper(
					component);
			String oWidgetID = wComponent
					.getFirstValueByName(GUITARConstants.ID_TAG_NAME);

			if (widgetID.equals(oWidgetID))
				return widgetElement;

		}

		return null;
	}

	// private String getWidgetIDFromEventID(String eventID) {
	// }

}
