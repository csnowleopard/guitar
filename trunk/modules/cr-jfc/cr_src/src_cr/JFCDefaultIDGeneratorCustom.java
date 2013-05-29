/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
//package edu.umd.cs.guitar.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;
//import edu.umd.cs.guitar.util.GUITARLog;

import edu.umd.cs.guitar.model.* ;
import edu.umd.cs.guitar.model.data.* ;

/**
 * Default ID generator for JFC application
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class JFCDefaultIDGeneratorCustom implements GIDGenerator
{
   static JFCDefaultIDGeneratorCustom instance = null;
   static ObjectFactory factory = new ObjectFactory();
   static final int prime = 31;

   public static JFCDefaultIDGeneratorCustom
   getInstance()
   {
      if (instance == null) {
         instance = new JFCDefaultIDGeneratorCustom();
      }

      return instance;
   }

   private JFCDefaultIDGeneratorCustom() {  }

   /*
    * (non-Javadoc)
    * 
    * @see
    * edu.umd.cs.guitar.model.GIDGenerator#genID(edu.umd.cs.guitar.model.data
    * .GUIStructure)
    */
   @Override
   public void generateID(GUIStructure gs) {
      for (GUIType gui : gs.getGUI()) {
         generateGUIID(gui);
      }
   }

   /**
    * @param gui
    */
   private void
   generateGUIID(GUIType gui)
   {
      ContainerType container = gui.getContainer();

      if (container == null) {
         return;
      }

      long windowHashCode = getWindowHashCode(gui);

      List<ComponentType> subComponentList = container.getContents()
            .getWidgetOrContainer();

      if (subComponentList == null) {
         return;
      }

      for (ComponentType subComponent : subComponentList) {
         // Generate first container hash code specially to ignore window
         // susceptible to title change

         AttributesType attributes = subComponent.getAttributes();
         long hashcode = windowHashCode;
         if (attributes != null) {

            //fixed to maintain consistency without necessarily having windoHashCode
            String sID = GUITARConstants.COMPONENT_ID_PREFIX + getEntireHashcode(subComponent);

            List<PropertyType> lProperty = new ArrayList<PropertyType>();

            for (PropertyType p : attributes.getProperty()) {
               if (!GUITARConstants.ID_TAG_NAME.equals(p.getName())) {
                  lProperty.add(p);
               }
               if (GUITARConstants.TITLE_TAG_NAME.equals(p.getName())) {
                  /*GUITARLog.log.debug("generateGUIID: " + 
                                      "[" + sID + "] " + p.getValue() +
                                      "windowHashCode " + windowHashCode);*/
               }
            }

            PropertyType property = factory.createPropertyType();
            property.setName(GUITARConstants.ID_TAG_NAME);
            property.getValue().add(sID);
            lProperty.add(0, property);
            attributes.setProperty(lProperty);

         }

         if (subComponent instanceof ContainerType) {
            ContainerType subContainer = (ContainerType) subComponent;
            for (ComponentType component : subContainer.getContents()
                  .getWidgetOrContainer()) {
               generateComponentID(component, hashcode);
            }
         }
      }
   }

   /**
    * @param gui
    * @return
    */
   private long
   getWindowHashCode(GUIType gui)
   {
      GUITypeWrapper wGUI = new GUITypeWrapper(gui);
      String title = wGUI.getTitle();
      String fuzzyTitle = getFuzzyTitle(title);

      long hashcode = fuzzyTitle.hashCode();

      hashcode = (hashcode * 2) & 0xffffffffL;

      return hashcode;
   }

   /**
    * @param title
    * @return
    */
   private String
   getFuzzyTitle(String title)
   {
      for (String sPattern : WINDOW_PATTERNS) {
         if (isFuzzyMatched(title, sPattern))
            return sPattern;
      }

      return title;
   }

   /**
    * @param lIgnoredWindow2
    * @param sTitle
    * @return
    */
   private static boolean
   isFuzzyMatched(String sTitle, String sPattern)
   {
      Pattern pattern;
      Matcher matcher;

      pattern = Pattern.compile(sPattern);
      matcher = pattern.matcher(sTitle);
      if (matcher.matches()) {
         return true;
      }

      return false;
   }

   /**
    * TODO: Move this list to a configuration file 
    */
   private static List<String> WINDOW_PATTERNS = Arrays.asList("Rachota .*",
         "OmegaT-.*", "Buddi.*", "Open:.*",
         "JabRef.*", "GanttProject.*", ".*Pauker.*",
         ".*FreeMind.*", ".* - ArgoUML.*");

   private void
   generateComponentID(ComponentType component,
                       long windoHashCode)
   {
      AttributesType attributes = component.getAttributes();

      long hashcode = 1;

      if (attributes != null) {

         long localHashCode = getLocalHashcode(component);
         hashcode = windoHashCode * prime + localHashCode;
         hashcode = (hashcode * 2) & 0xffffffffL;

        //fixed to maintain consistency without necessarily having windoHashCode
        String sID = GUITARConstants.COMPONENT_ID_PREFIX + getEntireHashcode(component);

         List<PropertyType> lProperty = new ArrayList<PropertyType>();

         for (PropertyType p : attributes.getProperty()) {
            if (!GUITARConstants.ID_TAG_NAME.equals(p.getName())) {
               lProperty.add(p);
            }
            if (GUITARConstants.TITLE_TAG_NAME.equals(p.getName())) {
               /*GUITARLog.log.debug("generateComponentID " +
                                   "[" + sID + "] " + p.getValue() +
                                   "windoHashCode " + windoHashCode +
                                   "localHashCode " + localHashCode);*/
            }
         }

         PropertyType property = factory.createPropertyType();
         property.setName(GUITARConstants.ID_TAG_NAME);
         property.getValue().add(sID);
         lProperty.add(0, property);
         attributes.setProperty(lProperty);

      } else {
         hashcode = windoHashCode;
      }

      if (component instanceof ContainerType) {
         ContainerType container = (ContainerType) component;
         List<ComponentType> children = container.getContents()
               .getWidgetOrContainer();

         boolean isAddIndex;

         for (ComponentType child : children) {

            // Debug
            ComponentTypeWrapper wChild = new ComponentTypeWrapper(child);

            String sClass = wChild
                  .getFirstValueByName(GUITARConstants.CLASS_TAG_NAME);

            Integer index = children.indexOf(child);
            long propagatedHashCode = prime * windoHashCode
                    + index.hashCode();

            generateComponentID(child, propagatedHashCode);
         }
      }
   }

   /**
    * @param component
    * @return
    */
   private boolean
   hasUniqueChildren(ComponentType component)
   {
      if (!(component instanceof ContainerType)) {
         return true;
      }

      List<Long> examinedHashCode = new ArrayList<Long>();

      ContainerType container = (ContainerType) component;
      for (ComponentType child : container.getContents()
            .getWidgetOrContainer()) {
         long hashcode = getLocalHashcode(child);
         if (examinedHashCode.contains(hashcode)) {
            return false;
         } else {
            examinedHashCode.add(hashcode);
         }
      }

      return true;
   }

   static List<String> ID_PROPERTIES = new ArrayList<String>(
         JFCConstants.ID_PROPERTIES);

   /**
    * Those classes are invisible widgets but cause false-positive when
    * calculating ID
    */
   static List<String> IGNORED_CLASSES = Arrays.asList("javax.swing.JPanel",
         "javax.swing.JTabbedPane", "javax.swing.JScrollPane",
         "javax.swing.JSplitPane", "javax.swing.Box",
         "javax.swing.JViewport", "javax.swing.JScrollBar",
         "javax.swing.JLayeredPane",
         "javax.swing.JList$AccessibleJList$AccessibleJListChild",
         "javax.swing.JList$AccessibleJList", "javax.swing.JList",
         "javax.swing.JScrollPane$ScrollBar",
         "javax.swing.plaf.metal.MetalScrollButton");

   /**
    * Those classes are invisible widgets but cause false-positive when
    * calculating ID
    */
   static List<String> IS_ADD_INDEX_CLASSES = Arrays
         .asList("javax.swing.JTabbedPane$Page");

   /**
    * @param component
    * @return
    */
   public long
   getLocalHashcode(ComponentType component)
   {
      final int prime = 31;

      long hashcode = 1;

      AttributesType attributes = component.getAttributes();
      if (attributes == null) {
         return hashcode;
      }

      // Specially handle titles
      AttributesTypeWrapper wAttribute =
         new AttributesTypeWrapper(attributes);
      String sClass = wAttribute
            .getFirstValByName(GUITARConstants.CLASS_TAG_NAME);

      if (IGNORED_CLASSES.contains(sClass)) {
         hashcode = (prime * hashcode + (sClass == null ? 0 : (sClass
               .hashCode())));
         return hashcode;
      }

      // Normal cases
      // Using ID_Properties for hash code

      List<PropertyType> lProperty = attributes.getProperty();

      if (lProperty == null) {
         return hashcode;
      }

      for (PropertyType property : lProperty) {

         String name = property.getName();
         if (ID_PROPERTIES.contains(name)) {

            hashcode = (prime * hashcode + (name == null ? 0 : name
                  .hashCode()));

            List<String> valueList = property.getValue();
            if (valueList != null) {
               for (String value : valueList) {
                  hashcode = (prime * hashcode + (value == null ? 0
                        : (value.hashCode())));

               }
            }
         }
      }

      hashcode = (hashcode * 2) & 0xffffffffL;

      return hashcode;

   }

   //added to compute custom hash (without window hash)
   public String getEntireHashcode(ComponentType component){
      long hash = getLocalHashcode(component);
      hash *= prime;
      hash = hash & 0xffffffffL;
      return (new Long(hash)).toString();
   }

} // End of class
