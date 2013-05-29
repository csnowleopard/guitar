package edu.umd.cs.guitar.ripper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.umd.cs.guitar.ripper.adapter.GRipperAdapter;

import edu.umd.cs.guitar.model.GApplication;


import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.LogWidget;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * A builder class to create an instance of the ripper
 */
public abstract class GRipperBuilder {

  
  
  abstract GRipperBuilder withApplication(GApplication application);

  /**
   * builder method 
   * @return
   */
  abstract public Ripper build();

}
