/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s): Alexandre Iline.
 *
 * The Original Software is the Jemmy library.
 * The Initial Developer of the Original Software is Alexandre Iline.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 *
 *
 * $Id$ $Revision$ $Date$
 *
 */

package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.WindowWaiter;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.WindowDriver;

/**
 * <BR><BR>Timeouts used: <BR>
 * WindowWaiter.WaitWindowTimeout - time to wait window displayed <BR>
 * WindowWaiter.AfterWindowTimeout - time to sleep after window has been dispayed <BR>.
 *
 * @see org.netbeans.jemmy.Timeouts
 * 
 * @author Alexandre Iline (alexandre.iline@sun.com)
 *	
 */

public class WindowOperator extends ContainerOperator 
implements Outputable{

    TestOut output;
    WindowDriver driver;

    /**
     * Constructor.
     * @param w a component
     */
    public WindowOperator(Window w) {
	super(w);
	driver = DriverManager.getWindowDriver(getClass());
    }

    /**
     * Constructs a DialogOperator object.
     * @param owner window - owner
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     */
    public WindowOperator(WindowOperator owner, ComponentChooser chooser, int index) {
	this((Window)owner.
             waitSubWindow(chooser,
                           index));
	copyEnvironment(owner);
    }

    /**
     * Constructs a DialogOperator object.
     * @param owner window - owner
     * @param chooser a component chooser specifying searching criteria.
     */
    public WindowOperator(WindowOperator owner, ComponentChooser chooser) {
	this(owner, chooser, 0);
    }

    /**
     * Constructor.
     * Waits for the index'th displayed owner's child.
     * Uses owner's timeout and output for waiting and to init operator.
     * @param owner Operator pointing on a window owner.
     * @param index an index between appropriate ones.
     * @throws TimeoutExpiredException
     */
    public WindowOperator(WindowOperator owner, int index) {
	this(waitWindow(owner, 
			ComponentSearcher.getTrueChooser("Any Window"),
			index));
	copyEnvironment(owner);
    }

    /**
     * Constructor.
     * Waits for the first displayed owner's child.
     * Uses owner's timeout and output for waiting and to init operator.
     * @param owner Operator pointing on a window owner.
     * @throws TimeoutExpiredException
     */
    public WindowOperator(WindowOperator owner) {
	this(owner, 0);
    }

    /**
     * Constructor.
     * Waits for the index'th displayed window.
     * Constructor can be used in complicated cases when
     * output or timeouts should differ from default.
     * @param index an index between appropriate ones.
     * @param env an operator to copy environment from.
     * @throws TimeoutExpiredException
     */
    public WindowOperator(int index, Operator env) {
	this(waitWindow(ComponentSearcher.getTrueChooser("Any Window"),
			index, env.getTimeouts(), env.getOutput()));
	copyEnvironment(env);
    }

    /**
     * Constructor.
     * Waits for the index'th displayed window.
     * Uses current timeouts and output values.
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     * @param index an index between appropriate ones.
     * @throws TimeoutExpiredException
     */
    public WindowOperator(int index) {
	this(index,
	     getEnvironmentOperator());
    }

    /**
     * Constructor.
     * Waits for the first displayed window.
     * Uses current timeouts and output values.
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     * @throws TimeoutExpiredException
     */
    public WindowOperator() {
	this(0);
    }

    /**
     * Searches an index'th window.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     * @return a window
     */
    public static Window findWindow(ComponentChooser chooser, int index) {
	return(WindowWaiter.getWindow(chooser, index));
    }

    /**
     * Searches a window.
     * @param chooser a component chooser specifying searching criteria.
     * @return a window
     */
    public static Window findWindow(ComponentChooser chooser) {
	return(findWindow(chooser, 0));
    }

    /**
     * Searches an index'th window.
     * @param owner Window - owner.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     * @return a window
     */
    public static Window findWindow(Window owner, ComponentChooser chooser, int index) {
	return(WindowWaiter.getWindow(owner, chooser, index));
    }

    /**
     * Searches a window.
     * @param owner Window - owner.
     * @param chooser a component chooser specifying searching criteria.
     * @return a window
     */
    public static Window findWindow(Window owner, ComponentChooser chooser) {
	return(findWindow(owner, chooser, 0));
    }

    /**
     * Waits an index'th window.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     * @throws TimeoutExpiredException
     * @return a window
     */
    public static Window waitWindow(ComponentChooser chooser, int index) {
	return(waitWindow(chooser, index,
			  JemmyProperties.getCurrentTimeouts(),
			  JemmyProperties.getCurrentOutput()));
    }

    /**
     * Waits a window.
     * @param chooser a component chooser specifying searching criteria.
     * @throws TimeoutExpiredException
     * @return a window
     */
    public static Window waitWindow(ComponentChooser chooser) {
	return(waitWindow(chooser, 0));
    }

    /**
     * Waits an index'th window.
     * @param owner Window - owner.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     * @throws TimeoutExpiredException
     * @return a window
     */
    public static Window waitWindow(Window owner, ComponentChooser chooser, int index) {
	return(waitWindow(owner, chooser, index,
			  JemmyProperties.getCurrentTimeouts(),
			  JemmyProperties.getCurrentOutput()));
    }

    /**
     * Waits a window.
     * @param owner Window - owner.
     * @param chooser a component chooser specifying searching criteria.
     * @throws TimeoutExpiredException
     * @return a window
     */
    public static Window waitWindow(Window owner, ComponentChooser chooser) {
	return(waitWindow(owner, chooser, 0));
    }

    public void setOutput(TestOut out) {
	super.setOutput(out);
	output = out;
    }
    
    public TestOut getOutput() {
	return(output);
    }

    public void copyEnvironment(Operator anotherOperator) {
	super.copyEnvironment(anotherOperator);
	driver = 
	    (WindowDriver)DriverManager.
	    getDriver(DriverManager.WINDOW_DRIVER_ID,
		      getClass(), 
		      anotherOperator.getProperties());
    }

    /**
     * Activates the window.
     * Uses WindowDriver registered for the operator type.
     */
    public void activate() {
 	output.printLine("Activate window\n    " + getSource().toString());
 	output.printGolden("Activate window");
	driver.activate(this);
    }

    /**
     * Requests the window to close.
     * Uses WindowDriver registered for the operator type.
     */
    public void requestClose() {
 	output.printLine("Requesting close of window\n    " + getSource().toString());
 	output.printGolden("Requesting close of window");
	driver.requestClose(this);
    }

    /**
     * Closes a window by requesting it to close and then hiding it.
     * Not implemented for internal frames at the moment.
     * Uses WindowDriver registered for the operator type.
     *
     * @see #requestClose()
     */
    public void requestCloseAndThenHide() {
	output.printLine("Closing window\n    " + getSource().toString());
	output.printGolden("Closing window");
	driver.requestCloseAndThenHide(this);
	if(getVerification()) {
	    waitClosed();
	}
    }

    /**
     * Closes a window by requesting it to close and then, if it's a top-level
     * frame, hiding it. Uses WindowDriver registered for the operator type.
     * 
     * @deprecated Use requestClose(). It is the target window's responsibility
     *             to hide itself if needed. Or, if you really have to, use
     *             requestCloseAndThenHide().
     * @see #requestClose()
     * @see #requestCloseAndThenHide()
     */
    public void close() {
 	output.printLine("Closing window\n    " + getSource().toString());
 	output.printGolden("Closing window");
	driver.close(this);
	if(getVerification()) {
            waitClosed();
        }
    }

    /**
     * Moves the window to another location.
     * Uses WindowDriver registered for the operator type.
     * @param x coordinate in screen coordinate system
     * @param y coordinate in screen coordinate system
     */
    public void move(int x, int y) {
 	output.printLine("Moving frame\n    " + getSource().toString());
 	output.printGolden("Moving frame");
	driver.move(this, x, y);
    }

    /**
     * Resizes the window.
     * Uses WindowDriver registered for the operator type.
     * @param width new width
     * @param height new height
     */
    public void resize(int width, int height) {
 	output.printLine("Resizing frame\n    " + getSource().toString());
 	output.printGolden("Resizing frame");
	driver.resize(this, width, height);
    }

    /**
     * Searches an index'th window between windows owned by this window.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     * @return a subwindow
     */
    public Window findSubWindow(ComponentChooser chooser, int index) {
        getOutput().printLine("Looking for \"" + chooser.getDescription() +
                              "\" subwindow");
	return(findWindow((Window)getSource(), chooser, index));
    }

    /**
     * Searches a window between windows owned by this window.
     * @param chooser a component chooser specifying searching criteria.
     * @return a subwindow
     */
    public Window findSubWindow(ComponentChooser chooser) {
	return(findSubWindow(chooser, 0));
    }

    /**
     * Waits an index'th window between windows owned by this window.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     * @return a subwindow
     */
    public Window waitSubWindow(ComponentChooser chooser, int index) {
        getOutput().printLine("Waiting for \"" + chooser.getDescription() +
                              "\" subwindow");
        WindowWaiter ww = new WindowWaiter();
        ww.setOutput(getOutput());
        ww.setTimeouts(getTimeouts());
	try {
	    return(ww.waitWindow((Window)getSource(), chooser, index));
	} catch (InterruptedException e) {
	    throw(new JemmyException("Waiting for \"" + chooser.getDescription() +
				     "\" window has been interrupted", e));
	}
    }

    /**
     * Waits a window between windows owned by this window.
     * @param chooser a component chooser specifying searching criteria.
     * @return a subwindow
     */
    public Window waitSubWindow(ComponentChooser chooser) {
	return(waitSubWindow(chooser, 0));
    }

    /**
     * Waits the window to be closed.
     */
    public void waitClosed() {
	getOutput().printLine("Wait window to be closed \n    : "+
			      getSource().toString());
	getOutput().printGolden("Wait window to be closed");
	waitState(new ComponentChooser() {
		public boolean checkComponent(Component comp) {
		    return(!comp.isVisible());
		}
		public String getDescription() {
		    return("Closed window");
		}
	    });
    }

    ////////////////////////////////////////////////////////
    //Mapping                                             //

    /**Maps <code>Window.addWindowListener(WindowListener)</code> through queue*/
    public void addWindowListener(final WindowListener windowListener) {
	runMapping(new MapVoidAction("addWindowListener") {
		public void map() {
		    ((Window)getSource()).addWindowListener(windowListener);
		}});}

    /**Maps <code>Window.applyResourceBundle(String)</code> through queue*/
    public void applyResourceBundle(final String string) {
	runMapping(new MapVoidAction("applyResourceBundle") {
		public void map() {
		    ((Window)getSource()).applyResourceBundle(string);
		}});}

    /**Maps <code>Window.applyResourceBundle(ResourceBundle)</code> through queue*/
    public void applyResourceBundle(final ResourceBundle resourceBundle) {
	runMapping(new MapVoidAction("applyResourceBundle") {
		public void map() {
		    ((Window)getSource()).applyResourceBundle(resourceBundle);
		}});}

    /**Maps <code>Window.dispose()</code> through queue*/
    public void dispose() {
	runMapping(new MapVoidAction("dispose") {
		public void map() {
		    ((Window)getSource()).dispose();
		}});}

    /**Maps <code>Window.getFocusOwner()</code> through queue*/
    public Component getFocusOwner() {
	return((Component)runMapping(new MapAction("getFocusOwner") {
		public Object map() {
		    return(((Window)getSource()).getFocusOwner());
		}}));}

    /**Maps <code>Window.getOwnedWindows()</code> through queue*/
    public Window[] getOwnedWindows() {
	return((Window[])runMapping(new MapAction("getOwnedWindows") {
		public Object map() {
		    return(((Window)getSource()).getOwnedWindows());
		}}));}

    /**Maps <code>Window.getOwner()</code> through queue*/
    public Window getOwner() {
	return((Window)runMapping(new MapAction("getOwner") {
		public Object map() {
		    return(((Window)getSource()).getOwner());
		}}));}

    /**Maps <code>Window.getWarningString()</code> through queue*/
    public String getWarningString() {
	return((String)runMapping(new MapAction("getWarningString") {
		public Object map() {
		    return(((Window)getSource()).getWarningString());
		}}));}

    /**Maps <code>Window.pack()</code> through queue*/
    public void pack() {
	runMapping(new MapVoidAction("pack") {
		public void map() {
		    ((Window)getSource()).pack();
		}});}

    /**Maps <code>Window.removeWindowListener(WindowListener)</code> through queue*/
    public void removeWindowListener(final WindowListener windowListener) {
	runMapping(new MapVoidAction("removeWindowListener") {
		public void map() {
		    ((Window)getSource()).removeWindowListener(windowListener);
		}});}

    /**Maps <code>Window.toBack()</code> through queue*/
    public void toBack() {
	runMapping(new MapVoidAction("toBack") {
		public void map() {
		    ((Window)getSource()).toBack();
		}});}

    /**Maps <code>Window.toFront()</code> through queue*/
    public void toFront() {
	runMapping(new MapVoidAction("toFront") {
		public void map() {
		    ((Window)getSource()).toFront();
		}});}

    //End of mapping                                      //
    ////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////
    //Mapping 1.4                                         //

    /**Maps <code>Window.isFocused()</code> through queue. 
       @return result of the mapped method */
    public boolean isFocused() {
        if(System.getProperty("java.specification.version").compareTo("1.3") > 0) {
            return(runMapping(new MapBooleanAction("isFocused") {
                    public boolean map() {
                        try {
                            return(((Boolean)new ClassReference(getSource()).
                                    invokeMethod("isFocused", null, null)).booleanValue());
                        } catch(InvocationTargetException e) {
                            return(false);
                        } catch(NoSuchMethodException e) {
                            return(false);
                        } catch(IllegalAccessException e) {
                            return(false);
                        }
                    }}));
        } else {
            return(getFocusOwner() != null);
        }
    }

    /**Maps <code>Window.isActive()</code> through queue. 
       @return result of the mapped method */
    public boolean isActive() {
        if(System.getProperty("java.specification.version").compareTo("1.3") > 0) {
            return(runMapping(new MapBooleanAction("isActive") {
                    public boolean map() {
                        try {
                            return(((Boolean)new ClassReference(getSource()).
                                    invokeMethod("isActive", null, null)).booleanValue());
                        } catch(InvocationTargetException e) {
                            return(false);
                        } catch(NoSuchMethodException e) {
                            return(false);
                        } catch(IllegalAccessException e) {
                            return(false);
                        }
                    }}));
        } else {
            return(isShowing());
        }
    }

    //End of mapping 1.4                                  //
    ////////////////////////////////////////////////////////

    /**
     * A method to be used from subclasses.
     * Uses timeouts and output passed as parameters during the waiting.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @param timeouts timeouts to be used during the waiting.
     * @param output an output to be used during the waiting.
     * @return Component instance or null if component was not found.
     */
    protected static Window waitWindow(ComponentChooser chooser, int index,
				       Timeouts timeouts, TestOut output) {
	try {
	    WindowWaiter waiter = new WindowWaiter();
	    waiter.setTimeouts(timeouts);
	    waiter.setOutput(output);
	    return(waiter.waitWindow(chooser, index));
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	    return(null);
	}
    }

    /**
     * A method to be used from subclasses.
     * Uses <code>owner</code>'s timeouts and output during the waiting.
     * @param owner a window - dialog owner.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return Component instance or null if component was not found.
     */
    protected static Window waitWindow(WindowOperator owner, ComponentChooser chooser, int index) {
	return(waitWindow((Window)owner.getSource(), 
			  chooser, index, 
			  owner.getTimeouts(), owner.getOutput()));
    }

    /**
     * A method to be used from subclasses.
     * Uses timeouts and output passed as parameters during the waiting.
     * @param owner a window - dialog owner.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @param timeouts timeouts to be used during the waiting.
     * @param output an output to be used during the waiting.
     * @return Component instance or null if component was not found.
     */
    protected static Window waitWindow(Window owner, ComponentChooser chooser, int index,
				       Timeouts timeouts, TestOut output) {
	try {
	    WindowWaiter waiter = new WindowWaiter();
	    waiter.setTimeouts(timeouts);
	    waiter.setOutput(output);
	    return(waiter.waitWindow(owner, chooser, index));
	} catch(InterruptedException e) {
	    JemmyProperties.getCurrentOutput().printStackTrace(e);
	    return(null);
	}
    }

}
