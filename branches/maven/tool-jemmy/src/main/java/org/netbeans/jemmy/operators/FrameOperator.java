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
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuBar;
import java.util.Hashtable;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.FrameWaiter;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FrameDriver;

/**
 * <BR><BR>Timeouts used: <BR>
 * FrameWaiter.WaitFrameTimeout - time to wait frame displayed <BR>
 * FrameWaiter.AfterFrameTimeout - time to sleep after frame has been dispayed <BR>
 * ComponentOperator.WaitStateTimeout - time to wait for text <BR>.
 *
 * @see org.netbeans.jemmy.Timeouts
 * 
 * @author Alexandre Iline (alexandre.iline@sun.com)
 *	
 */

public class FrameOperator extends WindowOperator implements Outputable {

    /**
     * Identifier for a title property.
     * @see #getDump
     */
    public static final String TITLE_DPROP = "Title";

    /**
     * Identifier for a state property.
     * @see #getDump
     */
    public static final String STATE_DPROP = "State";

    /**
     * Identifier for a "normal state" state property value.
     * @see #getDump
     */
    public static final String STATE_NORMAL_DPROP_VALUE = "NORMAL";

    /**
     * Identifier for a "iconified state" state property value.
     * @see #getDump
     */
    public static final String STATE_ICONIFIED_DPROP_VALUE = "ICONIFIED";

    /**
     * Identifier for a resizable property.
     * @see #getDump
     */
    public static final String IS_RESIZABLE_DPROP = "Resizable";

    TestOut output;
    FrameDriver driver;

    /**
     * Constructs a FrameOperator object.
     * @param w window
     */
    public FrameOperator(Frame w) {
	super(w);
	driver = DriverManager.getFrameDriver(getClass());
    }

    /**
     * Constructs a FrameOperator object.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     * @param env an operator to copy environment from.
     */
    public FrameOperator(ComponentChooser chooser, int index, Operator env) {
	this(waitFrame(new FrameFinder(chooser),
                       index, 
                       env.getTimeouts(),
                       env.getOutput()));
	copyEnvironment(env);
    }

    /**
     * Constructs a FrameOperator object.
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     */
    public FrameOperator(ComponentChooser chooser, int index) {
	this(chooser, index, Operator.getEnvironmentOperator());
    }

    /**
     * Constructs a FrameOperator object.
     * @param chooser a component chooser specifying searching criteria.
     */
    public FrameOperator(ComponentChooser chooser) {
	this(chooser, 0);
    }

    /**
     * Constructor.
     * Waits for the frame with "title" subtitle.
     * Constructor can be used in complicated cases when
     * output or timeouts should differ from default.
     * @param title a window title
     * @param index Ordinal component index.
     * @param env an operator to copy environment from.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public FrameOperator(String title, int index, Operator env) {
	this(waitFrame(new FrameByTitleFinder(title, 
						 env.getComparator()),
			index,
			env.getTimeouts(),
			env.getOutput()));
	copyEnvironment(env);
    }

    /**
     * Constructor.
     * Waits for the frame with "title" subtitle.
     * Uses current timeouts and output values.
     * @param title a window title
     * @param index Ordinal component index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     * @throws TimeoutExpiredException
     */
    public FrameOperator(String title, int index) {
	this(title, index,
	     ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Constructor.
     * Waits for the frame with "title" subtitle.
     * Uses current timeouts and output values.
     * @param title a window title
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     * @throws TimeoutExpiredException
     */
    public FrameOperator(String title) {
	this(title, 0);
    }

    /**
     * Constructor.
     * Waits for the index'th frame.
     * Uses current timeout and output for waiting and to init operator.
     * @param index Ordinal component index.
     * @throws TimeoutExpiredException
     */
    public FrameOperator(int index) {
	this((Frame)
	     waitFrame(new FrameFinder(),
		       index,
		       ComponentOperator.getEnvironmentOperator().getTimeouts(),
		       ComponentOperator.getEnvironmentOperator().getOutput()));
	copyEnvironment(ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Constructor.
     * Waits for the first frame.
     * Uses current timeout and output for waiting and to init operator.
     * @throws TimeoutExpiredException
     */
    public FrameOperator() {
	this(0);
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
	    (FrameDriver)DriverManager.
	    getDriver(DriverManager.FRAME_DRIVER_ID,
		      getClass(), 
		      anotherOperator.getProperties());
    }

    /**
     * Waits for title. Uses getComparator() comparator.
     * @param title Title to wait for.
     */
    public void waitTitle(final String title) {
	getOutput().printLine("Wait \"" + title + "\" title of frame \n    : "+
			      toStringSource());
	getOutput().printGolden("Wait \"" + title + "\" title");
	waitState(new FrameByTitleFinder(title, getComparator()));
    }

    /**
     * Iconifies the frame.
     */
    public void iconify() {
 	output.printLine("Iconifying frame\n    " + toStringSource());
 	output.printGolden("Iconifying frame");
	driver.iconify(this);
	if(getVerification()) {
            waitState(Frame.ICONIFIED);
        }
    }
    /**
     * Deiconifies the frame.
     */
    public void deiconify() {
 	output.printLine("Deiconifying frame\n    " + toStringSource());
 	output.printGolden("Deiconifying frame");
	driver.deiconify(this);
	if(getVerification()) {
            waitState(Frame.NORMAL);
        }
    }
    /**
     * Maximizes the frame.
     */
    public void maximize() {
 	output.printLine("Maximizing frame\n    " + toStringSource());
 	output.printGolden("Maximizing frame");
	driver.maximize(this);
	if(getVerification()) {
            waitState(Frame.NORMAL);
        }
    }
    /**
     * Demaximizes the frame.
     */
    public void demaximize() {
 	output.printLine("Demaximizing frame\n    " + toStringSource());
 	output.printGolden("Demaximizing frame");
	driver.demaximize(this);
	if(getVerification()) {
            waitState(Frame.NORMAL);
        }
    }

    /**
     * Waits for the frame to have a specified state.
     * @param state a state for the frame to have.
     */
    public void waitState(final int state) {
	getOutput().printLine("Wait frame to have " +
                              Integer.toString(state) +
			      " state \n    : "+
			      toStringSource());
	getOutput().printGolden("Wait frame to have " +
                                Integer.toString(state) +
                                " state");
	waitState(new ComponentChooser() {
		public boolean checkComponent(Component comp) {
                    return(((Frame)comp).getState() == state);
		}
		public String getDescription() {
		    return(Integer.toString(state) + " state");
		}
	    });
    }

    /**
     * Returns information about component.
     */
    public Hashtable getDump() {
	Hashtable result = super.getDump();
        if(((Frame)getSource()).getTitle() != null) {
            result.put(TITLE_DPROP, ((Frame)getSource()).getTitle());
        }
	result.put(STATE_DPROP, 
		   (((Frame)getSource()).getState() == Frame.ICONIFIED) ?
		   STATE_ICONIFIED_DPROP_VALUE : STATE_NORMAL_DPROP_VALUE);
	result.put(IS_RESIZABLE_DPROP, ((Frame)getSource()).isResizable() ? "true" : "false");
	return(result);
    }

    ////////////////////////////////////////////////////////
    //Mapping                                             //

    /**Maps <code>Frame.getIconImage()</code> through queue*/
    public Image getIconImage() {
	return((Image)runMapping(new MapAction("getIconImage") {
		public Object map() {
		    return(((Frame)getSource()).getIconImage());
		}}));}

    /**Maps <code>Frame.getMenuBar()</code> through queue*/
    public MenuBar getMenuBar() {
	return((MenuBar)runMapping(new MapAction("getMenuBar") {
		public Object map() {
		    return(((Frame)getSource()).getMenuBar());
		}}));}

    /**Maps <code>Frame.getState()</code> through queue*/
    public int getState() {
	return(runMapping(new MapIntegerAction("getState") {
		public int map() {
		    return(((Frame)getSource()).getState());
		}}));}

    /**Maps <code>Frame.getTitle()</code> through queue*/
    public String getTitle() {
	return((String)runMapping(new MapAction("getTitle") {
		public Object map() {
		    return(((Frame)getSource()).getTitle());
		}}));}

    /**Maps <code>Frame.isResizable()</code> through queue*/
    public boolean isResizable() {
	return(runMapping(new MapBooleanAction("isResizable") {
		public boolean map() {
		    return(((Frame)getSource()).isResizable());
		}}));}

    /**Maps <code>Frame.setIconImage(Image)</code> through queue*/
    public void setIconImage(final Image image) {
	runMapping(new MapVoidAction("setIconImage") {
		public void map() {
		    ((Frame)getSource()).setIconImage(image);
		}});}

    /**Maps <code>Frame.setMenuBar(MenuBar)</code> through queue*/
    public void setMenuBar(final MenuBar menuBar) {
	runMapping(new MapVoidAction("setMenuBar") {
		public void map() {
		    ((Frame)getSource()).setMenuBar(menuBar);
		}});}

    /**Maps <code>Frame.setResizable(boolean)</code> through queue*/
    public void setResizable(final boolean b) {
	runMapping(new MapVoidAction("setResizable") {
		public void map() {
		    ((Frame)getSource()).setResizable(b);
		}});}

    /**Maps <code>Frame.setState(int)</code> through queue*/
    public void setState(final int i) {
	runMapping(new MapVoidAction("setState") {
		public void map() {
		    ((Frame)getSource()).setState(i);
		}});}

    /**Maps <code>Frame.setTitle(String)</code> through queue*/
    public void setTitle(final String string) {
	runMapping(new MapVoidAction("setTitle") {
		public void map() {
		    ((Frame)getSource()).setTitle(string);
		}});}

    //End of mapping                                      //
    ////////////////////////////////////////////////////////

    /**
     * A method to be used from subclasses.
     * Uses timeouts and output passed as parameters during the waiting.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @param timeouts timeouts to be used during the waiting.
     * @param output an output to be used during the waiting.
     * @return Component instance or null if component was not found.
     * @throws TimeoutExpiredException
     */
    protected static Frame waitFrame(ComponentChooser chooser, int index,
				       Timeouts timeouts, TestOut output) {
	try {
	    FrameWaiter waiter = new FrameWaiter();
	    waiter.setTimeouts(timeouts);
	    waiter.setOutput(output);
	    return((Frame)waiter.waitFrame(new FrameFinder(chooser), index));
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	    return(null);
	}
    }

    /**
     * Checks component type.
     */
    public static class FrameFinder extends Finder {
        /**
         * Constructs FrameFinder.
         * @param sf other searching criteria.
         */
	public FrameFinder(ComponentChooser sf) {
            super(Frame.class, sf);
	}
        /**
         * Constructs FrameFinder.
         */
	public FrameFinder() {
            super(Frame.class);
	}
    }

    /**
     * Allows to find component by title.
     */
    public static class FrameByTitleFinder implements ComponentChooser {
	String title;
	StringComparator comparator;
        /**
         * Constructs FrameByTitleFinder.
         * @param t a text pattern
         * @param comparator specifies string comparision algorithm.
         */
	public FrameByTitleFinder(String t, StringComparator comparator) {
	    title = t;
	    this.comparator = comparator;
	}
        /**
         * Constructs FrameByTitleFinder.
         * @param t a text pattern
         */
	public FrameByTitleFinder(String t) {
            this(t, Operator.getDefaultStringComparator());
	}
	public boolean checkComponent(Component comp) {
	    if(comp instanceof Frame) {
		if(((Frame)comp).isShowing() && ((Frame)comp).getTitle() != null) {
		    return(comparator.equals(((Frame)comp).getTitle(), title));
		}
	    }
	    return(false);
	}
	public String getDescription() {
	    return("Frame with title \"" + title + "\"");
	}
    }
}

