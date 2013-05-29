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
package edu.umd.cs.guitar.replayer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Frame;
import java.awt.Window;
import java.awt.AWTException;
import java.awt.Robot;

import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.QueueTool;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.JFCEventHandler;
import edu.umd.cs.guitar.exception.ApplicationConnectException;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.JFCApplication;
import edu.umd.cs.guitar.model.JFCConstants;
import edu.umd.cs.guitar.model.JFCXWindow;
import edu.umd.cs.guitar.model.JFCXComponent;

import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.util.GUITARLog;

import static org.sikuli.cv.Utils.find;

/**
 * Replayer monitor for Java Swing (JFC) application
 * 
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class JFCReplayerMonitor extends GReplayerMonitor {

   /**
     * 
     */
   private static final int INITIAL_DELAY = 1000;
   String MAIN_CLASS;

   /**
    * Delay for widget searching loop
    */
   private static final int DELAY_STEP = 50;

   /**
    * @param main_class
    */
   public JFCReplayerMonitor(String main_class) {
      super();
      MAIN_CLASS = main_class;
   }

   /**
    * Class used to disable System.exit()
    * 
    * @author Bao Nguyen
    * 
    */
   private static class ExitTrappedException extends SecurityException {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;
   } // End of class

   SecurityManager oldSecurityManager;

   /*
    * (non-Javadoc)
    * 
    * @see edu.umd.cs.guitar.replayer.AbsReplayerMonitor#setUp()
    */
   @Override
   public void
   setUp()
   {
      GUITARLog.log.info("Setting up JFCReplayer monitor");

      // -------------------------------------
      // Add handler for all uncaught exceptions
      Thread.setDefaultUncaughtExceptionHandler(
         new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
               GUITARLog.log.error("Uncaught exception", e);
            }
         }
      );

      // -------------------------------------
      // Disable System.exit() call by changing SecurityManager

      oldSecurityManager = System.getSecurityManager();
      final SecurityManager securityManager = new SecurityManager() {
         @Override
         public void checkPermission(Permission permission, Object context) {
            if ("exitVM".equals(permission.getName())) {
               throw new ExitTrappedException();
            }
         }

         @Override
         public void checkPermission(Permission permission) {
            if ("exitVM".equals(permission.getName())) {
               throw new ExitTrappedException();
            }
         }
      };
      System.setSecurityManager(securityManager);

      // Registering default supported events
      EventManager em = EventManager.getInstance();

      for (Class<? extends JFCEventHandler> event : JFCConstants.DEFAULT_SUPPORTED_EVENTS) {
         try {
            em.registerEvent(event.newInstance());
         } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see edu.umd.cs.guitar.replayer.AbsReplayerMonitor#cleanUp()
    */
   @Override
   public void cleanUp() {
      System.setSecurityManager(oldSecurityManager);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getAction(java.lang.String)
    */
   @Override
   public GEvent
   getAction(String actionName)
   {
      GEvent retAction = null;
      try {
         Class<?> c = Class.forName(actionName);
         Object action = c.newInstance();

         retAction = (GEvent) action;

      } catch (Exception e) {
         GUITARLog.log.error("Error in getting action", e);
      }

      return retAction;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getArguments(java.lang.
    * String)
    */
   @Override
   public Object getArguments(String action) {
      return null;
   }

   /*
    * Wait for specified window to appear.
    *
    * This is a blocking function which waits until a window
    * with title 'sWindowTitle' appears.
    * 
    * @see
    * edu.umd.cs.guitar.replayer.AbsReplayerMonitor#getWindow(java.lang.String)
    */
   @Override
   public GWindow
   getWindow(String sWindowTitle)
   {
      GWindow retGXWindow = null;

      while (retGXWindow == null) {
         Frame[] windows = Frame.getFrames();

         if (windows == null) {
            continue;
         }

         for (Frame aWindow : windows) {
            Window window = getOwnedWindowByID(aWindow, sWindowTitle);
            if (window != null) {
               retGXWindow = new JFCXWindow(window);
               break;
            }
         }

			if (retGXWindow != null) {
				break;
			}

         new EventTool().waitNoEvent(DELAY_STEP);
      }

      return retGXWindow;
   }

   /**
    * Wait for specified window to appear. Use images based
	 * comparism.
    *
    * This is a blocking function which waits until a window
    * with the image given in sImageFilepath appears.
	 *
	 * @param  sWindowTitle     Title of window to expect (debugging only)
	 * @param  sImagefilepath   File path of image of window to expect
	 * @return Returns the window GWindow on successful identification.
    */
   @Override
   public GWindow
   waitForWindow(String sWindowTitle,
	              String sImageFilepath)
	throws IOException
   {
      GWindow retGXWindow = null;

      while (retGXWindow == null) {
         Frame[] windows = Frame.getFrames();

         if (windows == null) {
            continue;
         }

         for (Frame aWindow : windows) {
				GWindow jfcxWindow = new JFCXWindow(aWindow);
				BufferedImage bufferedImage = null;
				boolean found = false;

				try {
					bufferedImage = captureImage(jfcxWindow.getContainer());
					found = sikuliCompare(sImageFilepath, bufferedImage);
				} catch (AWTException e) {
				} catch (IOException e) {
					GUITARLog.log.error("Unable to read image file " +
					                    sImageFilepath);
					throw e;
				}

            if (found == true) {
					retGXWindow = jfcxWindow;
               break;
            }
         }

			if (retGXWindow != null) {
				break;
			}

         new EventTool().waitNoEvent(DELAY_STEP);
      }

      return retGXWindow;
   }

	

   /*
    * (non-Javadoc)
    * 
    * @see
    * edu.umd.cs.guitar.replayer.GReplayerMonitor#selectIDProperties(edu.umd

   /*
    * (non-Javadoc)
    * 
    * @see
    * edu.umd.cs.guitar.replayer.GReplayerMonitor#selectIDProperties(edu.umd
    * .cs.guitar.model.data.ComponentType)
    */
   @Override
   public List<PropertyType>
   selectIDProperties(ComponentType comp)
   {
      if (comp == null) {
         return new ArrayList<PropertyType>();
      }

      List<PropertyType> retIDProperties =
         new ArrayList<PropertyType>();

      AttributesType attributes = comp.getAttributes();
      List<PropertyType> lProperties = attributes.getProperty();

      for (PropertyType p : lProperties) {
         if (JFCConstants.ID_PROPERTIES.contains(p.getName()))
            retIDProperties.add(p);
      }

      return retIDProperties;
   }

   /**
    * 
    * Recursively search a window
    * 
    * @param  parent
    * @param  sWindowID
    * @return Window
    */
   private Window
   getOwnedWindowByID(Window parent, String sWindowID)
   {
      if (parent == null) {
         return null;
      }

      GWindow gWindow = new JFCXWindow(parent);

      String title = gWindow.getTitle();
      if (title == null) {
         return null;
      }


      if (isUseReg) {
         if (isRegMatched(title, sWindowID)) {
            GUITARLog.log.info("getOwnedWindowByID: " +
                            "Looking for " + sWindowID + " got " + title);
            return parent;
         }
      } else {
         if (sWindowID.equals(title)) {
            GUITARLog.log.info("getOwnedWindowByID: " +
                            "Looking for " + sWindowID + " got " + title);
            return parent;
         }
      }

      Window retWin = null;
      Window[] wOwnedWins = parent.getOwnedWindows();

      for (Window aOwnedWin : wOwnedWins) {
         retWin = getOwnedWindowByID(aOwnedWin, sWindowID);
         if (retWin != null)
            return retWin;
      }

      return retWin;

   }

   /*
    * (non-Javadoc)
    * 
    * @see edu.umd.cs.guitar.replayer.AbsReplayerMonitor#connectToApplication()
    */
   @Override
   public void
   connectToApplication()
   {
      try {
         GUITARLog.log.info("Loading URL");

         String[] URLs;

         if (JFCReplayerConfiguration.URL_LIST != null) {
            URLs = JFCReplayerConfiguration.URL_LIST
                  .split(GUITARConstants.CMD_ARGUMENT_SEPARATOR);
         } else {
            URLs = new String[0];
         }

         application = new JFCApplication(
            JFCReplayerConfiguration.MAIN_CLASS,
            JFCReplayerConfiguration.USE_JAR, URLs);

         String[] args;

         if (JFCReplayerConfiguration.ARGUMENT_LIST != null) {
            args = JFCReplayerConfiguration.ARGUMENT_LIST
                  .split(GUITARConstants.CMD_ARGUMENT_SEPARATOR);
         } else {
            args = new String[0];
         }

         GUITARLog.log.info("Loading URL - DONE");

         application.connect(args);

         GUITARLog.log.info("Waiting for applicationt to start "
               + JFCReplayerConfiguration.INITIAL_WAITING_TIME + "ms");

         try {
            Thread.sleep(JFCReplayerConfiguration.INITIAL_WAITING_TIME);
         } catch (InterruptedException e) {
            GUITARLog.log.error(e);
            throw new ApplicationConnectException();
         }

      } catch (MalformedURLException e) {
         GUITARLog.log.error(e);
         throw new ApplicationConnectException();
      } catch (ClassNotFoundException e) {
         GUITARLog.log.error(e);
         throw new ApplicationConnectException();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   /**
    * Check if a string is match by a regular expression temporarily
    * used for a matching window titles. Should move to some more
    * general modules for future use.
    * 
    * <p>
    * 
    * @param input
    * @param regExp
    * @return
    */
   private boolean
   isRegMatched(String input, String regExp) {

      Pattern pattern;
      Matcher matcher;

      pattern = Pattern.compile(regExp);
      matcher = pattern.matcher(input);
      if (matcher.matches()) {
         return true;
      }

      return false;
   }

    @Override
    public void delay(int delay)
    {
        new QueueTool().waitEmpty(delay);
        
    }

   /**
    * Captures the image of a GUITAR GUI component
    * and saves it to a the specified image file.
    *
    * @param  component     GUITAR component to capture
    * @param  strFilePath   File path name to store the image
    *                          (w/o extension)
    * @return void
    */
   private BufferedImage
   captureImage(GComponent component)
   throws AWTException
   {
      Robot robot;
      BufferedImage bufferedImage = null;

      try {
         robot = new Robot();

         JFCXComponent gComp = (JFCXComponent ) component;
         Component comp = gComp.getComponent();

         Point pos = comp.getLocationOnScreen();
         Dimension dim = comp.getSize();
         Rectangle bounder = new Rectangle(pos, dim);

         bufferedImage = robot.createScreenCapture(bounder);
      } catch (AWTException e) {
         throw e;
      }

		return bufferedImage;
   }

	/**
    * Determine if one image exists in another.
	 *
	 * @param  sBigImage     File path of big image to look in
	 * @param  sSmallImage   File path of small image to search
	 * @return Returns true on successful location, false otherwise
	 */
	private boolean
	sikuliCompare(String sBigImage,
	              BufferedImage smallImage)
	throws IOException
	{
      BufferedImage bigImage = null;

      try {
         bigImage = ImageIO.read(new File(sBigImage));
      } catch (IOException e) {
			throw e;
      }

      Point p = find(bigImage, smallImage);

		GUITARLog.log.debug(sBigImage);
		if (p != null) 
			GUITARLog.log.debug(p.getX() + " " + p.getY());

      return (p == null) ? false : true;
   }

} // End of class
