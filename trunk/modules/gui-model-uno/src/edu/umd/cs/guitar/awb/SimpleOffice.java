package edu.umd.cs.guitar.awb;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.XExtendedToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;
import com.sun.star.container.XIndexAccess;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawView;
import com.sun.star.drawing.XShape;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XTask;
import com.sun.star.frame.XTasksSupplier;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;


/** 
 *  This class was extracted from the awb test harness to 
 *  simplify some tasks like loading a document or getting various objects.
 *  
 *  <p> 
 *  
 * @author Modified by Bao Nguyen
 *
 */
public class SimpleOffice
{
    XDesktop mxDesktop = null;
    OfficeConnection aConnection;
    int mnPortNumber;

    public SimpleOffice (int nPortNumber)
    {
        mnPortNumber = nPortNumber;
        connect ();
        getDesktop ();
    }

    public void connect ()
    {
        aConnection = new OfficeConnection (mnPortNumber);
        mxDesktop = null;
        getDesktop ();
    }

    public XModel loadDocument (String URL)
    {
        XModel xModel = null;
        try
        {
            //  Load the document from the specified URL.
            XComponentLoader xLoader = 
                (XComponentLoader)UnoRuntime.queryInterface(
                    XComponentLoader.class, mxDesktop);

            XComponent xComponent = xLoader.loadComponentFromURL (
                URL,
                "_blank",
                0,
                new PropertyValue[0]
                );

            xModel = (XModel) UnoRuntime.queryInterface(
                XModel.class, xComponent);
        }
        catch (java.lang.NullPointerException e)
        {
            System.err.println ("caught exception while loading "
                + URL + " : " + e);
        }
        catch (Exception e)
        {
        	System.err.println ("caught exception while loading "
                + URL + " : " + e);
        }
        return xModel;
    }




    public XModel getModel (String name)
    {
        XModel xModel = null;
        try
        {
            XTasksSupplier xTasksSupplier = 
                (XTasksSupplier) UnoRuntime.queryInterface(
                    XTasksSupplier.class, mxDesktop);
            XEnumerationAccess xEA = xTasksSupplier.getTasks();
            XEnumeration xE = xEA.createEnumeration();
            while (xE.hasMoreElements())
            {
                XTask xTask = (XTask) UnoRuntime.queryInterface(
                    XTask.class, xE.nextElement());
                System.err.print (xTask.getName());
            }
        }
        catch (Exception e)
        {
        	System.err.println ("caught exception while getting Model " + name
                + ": " + e);
        }
        return xModel;
    }


    public XModel getModel (XDrawView xView)
    {
        XController xController = (XController) UnoRuntime.queryInterface(
            XController.class, xView);
        if (xController != null)
            return xController.getModel();
        else
        {
        	System.err.println ("can't cast view to controller");
            return null;
        }
    }

    public  XDesktop getDesktop ()
    {
        //System.out.println("Got getDesktop");
        if (mxDesktop != null)
            return mxDesktop;
        try
        {
            //  Get the factory of the connected office.
            XMultiServiceFactory xMSF = aConnection.getServiceManager ();
            if (xMSF == null)
            {
            	System.out.println ("can't connect to office");
                return null;
            }
            else
            	System.out.println ("Connected successfully.");
            
            //  Create a new desktop.
            mxDesktop = (XDesktop) UnoRuntime.queryInterface(
                XDesktop.class,
                xMSF.createInstance ("com.sun.star.frame.Desktop")
                );
        }
        catch (Exception e)
        {
        	System.err.println ("caught exception while creating desktop: "
                + e);
        }

        return mxDesktop;
    }


    /** Return a reference to the extended toolkit which is a broadcaster of
        top window, key, and focus events.
    */
    public XExtendedToolkit getExtendedToolkit ()
    {
        XExtendedToolkit xToolkit = null;
        try
        {
            //  Get the factory of the connected office.
            XMultiServiceFactory xMSF = aConnection.getServiceManager ();
            if (xMSF != null)
            	
            {
                xToolkit = (XExtendedToolkit) UnoRuntime.queryInterface(
                    XExtendedToolkit.class,
                    xMSF.createInstance ("stardiv.Toolkit.VCLXToolkit")
                    );
            }
        }
        catch (Exception e)
        {
        	System.err.println ("caught exception while creating extended toolkit: " + e);
        }

        return xToolkit;
    }



    public XAccessible getAccessibleObject (XInterface xObject)
    {
        XAccessible xAccessible = null;
        try
        {
            xAccessible = (XAccessible) UnoRuntime.queryInterface(
                XAccessible.class, xObject);
        }
        catch (Exception e)
        {
        	System.err.println (
                "caught exception while getting accessible object" + e);
            e.printStackTrace();
        }
        return xAccessible;
    }

    /** Return the root object of the accessibility hierarchy.
    */
    public XAccessible getAccessibleRoot (XAccessible xAccessible)
    {
        try
        {
            XAccessible xParent = null;
            do
            {
                XAccessibleContext xContext = xAccessible.getAccessibleContext();
                if (xContext != null)
                    xParent = xContext.getAccessibleParent();
                if (xParent != null)
                    xAccessible = xParent;
            }
            while (xParent != null);
        }
        catch (Exception e)
        {
        	System.err.println (
                "caught exception while getting accessible root" + e);
            e.printStackTrace();
        }
        return xAccessible;
    }




    /** @descr Return the current window associated with the given 
                model.
    */
    public XWindow getCurrentWindow ()
    {
        return getCurrentWindow ((XModel) UnoRuntime.queryInterface(
                XModel.class, getDesktop()));
    }





    public XWindow getCurrentWindow (XModel xModel)
    {
        XWindow xWindow = null;
        try 
        {        
            if (xModel == null)
            	System.err.println ("invalid model (==null)");
            XController xController = xModel.getCurrentController();
            if (xController == null)
            	System.err.println ("can't get controller from model");
            XFrame xFrame = xController.getFrame();
            if (xFrame == null)
            	System.err.println ("can't get frame from controller");
            xWindow = xFrame.getComponentWindow ();
            if (xWindow == null)
            	System.err.println ("can't get window from frame");
        }
        catch (Exception e)
        {
        	System.err.println ("caught exception while getting current window" + e);
        }
        
        return xWindow;
    }


    /** @descr Return the current draw page of the given desktop.
    */
    public XDrawPage getCurrentDrawPage ()
    {
        return getCurrentDrawPage ((XDrawView) UnoRuntime.queryInterface(
                XDrawView.class, getCurrentView()));
    }




    public XDrawPage getCurrentDrawPage (XDrawView xView)
    {
        XDrawPage xPage = null;
        try 
        {        
            if (xView == null)
            	System.err.println ("can't get current draw page from null view");
            else
                xPage = xView.getCurrentPage();
        }
        catch (Exception e)
        {
        	System.err.println ("caught exception while getting current draw page : " + e);
        }
        
        return xPage;
    }




    /** @descr Return the current view of the given desktop.
    */
    public XDrawView getCurrentView ()
    {
        return getCurrentView (getDesktop());
    }

    public XDrawView getCurrentView (XDesktop xDesktop)
    {
        if (xDesktop == null)
        	System.err.println ("can't get desktop to retrieve current view");

        XDrawView xView = null;
        try 
        {        
            XComponent xComponent = xDesktop.getCurrentComponent();
            if (xComponent == null)
            	System.err.println ("can't get component to retrieve current view");

            XFrame xFrame = xDesktop.getCurrentFrame();
            if (xFrame == null)
            	System.err.println ("can't get frame to retrieve current view");

            XController xController = xFrame.getController();
            if (xController == null)
            	System.err.println ("can't get controller to retrieve current view");
            
            xView = (XDrawView) UnoRuntime.queryInterface(
                XDrawView.class, xController);
            if (xView == null)
            	System.err.println ("could not cast controller into view");
        }
        catch (Exception e)
        {
        	System.err.println ("caught exception while getting current view : " + e);
        }
        
        return xView;
    }




    //  Return the accessible object of the document window.
    public static XAccessible getAccessibleDocumentWindow (XDrawPage xPage)
    {
        XIndexAccess xShapeList = (XIndexAccess) UnoRuntime.queryInterface(
            XIndexAccess.class, xPage);
        if (xShapeList.getCount() > 0)
        {
            // All shapes return as accessible object the document window's
            // accessible object.  This is, of course, a hack and will be
            // removed as soon as the missing infrastructure for obtaining
            // the object directly is implemented.
            XShape xShape = null;
            try{
                xShape = (XShape) UnoRuntime.queryInterface(
                    XShape.class, xShapeList.getByIndex (0));
            } catch (Exception e)
            {}
            XAccessible xAccessible = (XAccessible) UnoRuntime.queryInterface (
                XAccessible.class, xShape);
            return xAccessible;
        }
        else
            return null;
    }
}
