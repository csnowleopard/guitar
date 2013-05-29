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

package org.netbeans.jemmy.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.operators.JScrollPaneOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.WindowOperator;
import org.netbeans.jemmy.operators.Operator.ComponentVisualizer;

/**
 * 
 * Used as component visualizer by default.
 *
 * @see org.netbeans.jemmy.operators.Operator#setVisualizer(Operator.ComponentVisualizer)
 * @see org.netbeans.jemmy.operators.Operator.ComponentVisualizer
 * 
 * @author Alexandre Iline (alexandre.iline@sun.com)
 * 
 */
public class DefaultVisualizer implements ComponentVisualizer, Cloneable {
    private boolean window = true;
    private boolean internalFrame = true;
    private boolean scroll = false;
    private boolean switchTab = false;
    private boolean modal = false;

    public DefaultVisualizer() {
    }

    /**
     * Forces vizualizer to check that component is
     * on the top modal dialog or no modal dialog
     * displayed.
     * @param yesOrNo If true, JemmyInputException will be throught
     * if component is not on the top modal dialog and a modal dialog
     * is dislayed.
     */
    public void checkForModal(boolean yesOrNo) {
	modal = yesOrNo;
    }

    /**
     * Informs that a window contained component should be activated.
     * @param yesOrNo true if windows need to be activated.
     */
    public void activateWindow(boolean yesOrNo) {
	window = yesOrNo;
    }

    /**
     * Informs that an internal frame contained component
     * should be activated.
     * @param yesOrNo true if internal frames need to be activated.
     */
    public void activateInternalFrame(boolean yesOrNo) {
	internalFrame = yesOrNo;
    }

    /**
     * Informs that scrolling should be made.
     * @param yesOrNo true if scroll panes need to be scrolled.
     */
    public void scroll(boolean yesOrNo) {
	scroll = yesOrNo;
    }

    /**
     * Informs that tab switching should be made.
     * @param yesOrNo true if tabbed panes need to be switched.
     */
    public void switchTab(boolean yesOrNo) {
	switchTab = yesOrNo;
    }

    /**
     * Returns true if window is active.
     * @param winOper an operator representing the window.
     * @return true is window is active.
     */
    protected boolean isWindowActive(WindowOperator winOper) {
        return(winOper.isFocused() && winOper.isActive());
    }

    /**
     * Performs an atomic window-activization precedure.
     * A window is sopposed to be prepared for the activization
     * (i.e. put "to front").
     * @param winOper an operator representing the window.
     */
    protected void makeWindowActive(WindowOperator winOper) {
        winOper.activate();
    }

    /**
     * Activates a window. Uses makeWindowActive if necessary.
     * @param winOper an operator representing the window.
     * @see #makeWindowActive
     */
    protected void activate(WindowOperator winOper) {
        boolean active = isWindowActive(winOper);
	winOper.toFront();
        if(!active) {
            makeWindowActive(winOper);
        }
    }

    /**
     * Inits an internal frame.
     * @param intOper an operator representing the frame.
     */
    protected void initInternalFrame(JInternalFrameOperator intOper) {
	if(!intOper.isSelected()) {
	    intOper.activate();
	}
    }

    /**
     * Scrolls JScrollPane to make the component visible.
     * @param scrollOper an operator representing a scroll pane.
     * @param target a component - target to be made visible.
     */
    protected void scroll(JScrollPaneOperator scrollOper, Component target) {
	if(!scrollOper.checkInside(target)) {
	    scrollOper.scrollToComponent(target);
	}
    }

    /**
     * Switches tabs to make the component visible.
     * @param tabOper an operator representing a tabbed pane.
     * @param target a component - target to be made visible.
     */
    protected void switchTab(JTabbedPaneOperator tabOper, Component target) {
	int tabInd = 0;
	for(int j = 0; j < tabOper.getTabCount(); j++) {
	    if(target == tabOper.getComponentAt(j)) {
		tabInd = j;
		break;
	    }
	}
	if(tabOper.getSelectedIndex() != tabInd) {
	    tabOper.selectPage(tabInd);
	}
    }

    /**
     * Prepares the component for user input.
     * @param compOper an operator representing the component.
     * @throws JemmyInputException
     * @see #checkForModal(boolean)
     */
    public void makeVisible(ComponentOperator compOper) {
	try {
	    if(modal) {
		Dialog modalDialog = JDialogOperator.getTopModalDialog();
		if(modalDialog != null &&
		   compOper.getWindow() != modalDialog) {
		    throw(new JemmyInputException("Component is not on top modal dialog.", 
						  compOper.getSource()));
		}
	    }
            WindowOperator winOper = new WindowOperator(compOper.getWindow());
	    if(window) {
		winOper.copyEnvironment(compOper);
		winOper.setVisualizer(new EmptyVisualizer());
		activate(winOper);
	    }
            if(internalFrame && compOper instanceof JInternalFrameOperator) {
                initInternalFrame((JInternalFrameOperator)compOper);
            }
	    Container[] conts = compOper.getContainers();
	    for(int i = conts.length - 1; i >=0 ; i--) {
		if       (internalFrame && conts[i] instanceof JInternalFrame) {
		    JInternalFrameOperator intOper = new JInternalFrameOperator((JInternalFrame)conts[i]);
		    intOper.copyEnvironment(compOper);
		    intOper.setVisualizer(new EmptyVisualizer());
		    initInternalFrame(intOper);
		} else if(scroll        && conts[i] instanceof JScrollPane) {
		    JScrollPaneOperator scrollOper = new JScrollPaneOperator((JScrollPane)conts[i]);
		    scrollOper.copyEnvironment(compOper);
		    scrollOper.setVisualizer(new EmptyVisualizer());
		    scroll(scrollOper, compOper.getSource());
		} else if(switchTab     && conts[i] instanceof JTabbedPane) {
		    JTabbedPaneOperator tabOper = new JTabbedPaneOperator((JTabbedPane)conts[i]);
		    tabOper.copyEnvironment(compOper);
		    tabOper.setVisualizer(new EmptyVisualizer());
		    switchTab(tabOper, i == 0 ? compOper.getSource() : conts[i - 1]);
		}
	    }
	} catch(TimeoutExpiredException e) {
	    JemmyProperties.getProperties().getOutput().printStackTrace(e);
	}
    }

    /**
     * Creates an exact copy of this visualizer.
     * @return new instance.
     */
    public DefaultVisualizer cloneThis() {
        try {
            return((DefaultVisualizer)super.clone());
        } catch(CloneNotSupportedException e) {
            //that's impossible
            throw(new JemmyException("Even impossible happens :)", e));
        }
    }

}
