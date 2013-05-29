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

import java.awt.Container;

import javax.swing.JCheckBox;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.TimeoutExpiredException;

/**
 *
 * <BR><BR>Timeouts used: <BR>
 * AbstractButtonOperator.PushButtonTimeout - time between button pressing and releasing<BR>
 * ComponentOperator.WaitComponentTimeout - time to wait button displayed <BR>
 * ComponentOperator.WaitComponentEnabledTimeout - time to wait button enabled <BR>.
 *
 * @see org.netbeans.jemmy.Timeouts
 *	
 * @author Alexandre Iline (alexandre.iline@sun.com)
 *	
 */

public class JCheckBoxOperator extends JToggleButtonOperator{

    /**
     * Constructor.
     * @param b a component
     */
    public JCheckBoxOperator(JCheckBox b) {
	super(b);
    }

    /**
     * Constructs a JCheckBoxOperator object.
     * @param cont a container
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     */
    public JCheckBoxOperator(ContainerOperator cont, ComponentChooser chooser, int index) {
	this((JCheckBox)cont.
             waitSubComponent(new JCheckBoxFinder(chooser),
                              index));
	copyEnvironment(cont);
    }

    /**
     * Constructs a JCheckBoxOperator object.
     * @param cont a container
     * @param chooser a component chooser specifying searching criteria.
     */
    public JCheckBoxOperator(ContainerOperator cont, ComponentChooser chooser) {
	this(cont, chooser, 0);
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont a container
     * @param text Button text. 
     * @param index Ordinal component index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JCheckBoxOperator(ContainerOperator cont, String text, int index) {
	this((JCheckBox)
	     waitComponent(cont, 
			   new JCheckBoxFinder(new AbstractButtonOperator.
					       AbstractButtonByLabelFinder(text, 
									   cont.getComparator())),
			   index));
	copyEnvironment(cont);
    }
    
    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont a container
     * @param text Button text. 
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JCheckBoxOperator(ContainerOperator cont, String text) {
	this(cont, text, 0);
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont a container
     * @param index Ordinal component index.
     * @throws TimeoutExpiredException
     */
    public JCheckBoxOperator(ContainerOperator cont, int index) {
	this((JCheckBox)
	     waitComponent(cont, 
			   new JCheckBoxFinder(),
			   index));
	copyEnvironment(cont);
    }
    
    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont a container
     * @throws TimeoutExpiredException
     */
    public JCheckBoxOperator(ContainerOperator cont) {
	this(cont, 0);
    }

    /**
     * Searches JCheckBox in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return JCheckBox instance or null if component was not found.
     */
    public static JCheckBox findJCheckBox(Container cont, ComponentChooser chooser, int index) {
	return((JCheckBox)findJToggleButton(cont, new JCheckBoxFinder(chooser), index));
    }

    /**
     * Searches 0'th JCheckBox in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return JCheckBox instance or null if component was not found.
     */
    public static JCheckBox findJCheckBox(Container cont, ComponentChooser chooser) {
	return(findJCheckBox(cont, chooser, 0));
    }

    /**
     * Searches JCheckBox by text.
     * @param cont Container to search component in.
     * @param text Button text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param index Ordinal component index.
     * @return JCheckBox instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JCheckBox findJCheckBox(Container cont, String text, boolean ce, boolean ccs, int index) {
	return(findJCheckBox(cont, 
			     new JCheckBoxFinder(new AbstractButtonOperator.
						 AbstractButtonByLabelFinder(text, 
									     new DefaultStringComparator(ce, ccs))), 
			     index));
    }

    /**
     * Searches JCheckBox by text.
     * @param cont Container to search component in.
     * @param text Button text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @return JCheckBox instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JCheckBox findJCheckBox(Container cont, String text, boolean ce, boolean ccs) {
	return(findJCheckBox(cont, text, ce, ccs, 0));
    }

    /**
     * Waits JCheckBox in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return JCheckBox instance.
     * @throws TimeoutExpiredException
     */
    public static JCheckBox waitJCheckBox(Container cont, ComponentChooser chooser, int index) {
	return((JCheckBox)waitJToggleButton(cont, new JCheckBoxFinder(chooser), index));
    }

    /**
     * Waits 0'th JCheckBox in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return JCheckBox instance.
     * @throws TimeoutExpiredException
     */
    public static JCheckBox waitJCheckBox(Container cont, ComponentChooser chooser) {
	return(waitJCheckBox(cont, chooser, 0));
    }

    /**
     * Waits JCheckBox by text.
     * @param cont Container to search component in.
     * @param text Button text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param index Ordinal component index.
     * @return JCheckBox instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public static JCheckBox waitJCheckBox(Container cont, String text, boolean ce, boolean ccs, int index) {
	return(waitJCheckBox(cont,  
			     new JCheckBoxFinder(new AbstractButtonOperator.
						 AbstractButtonByLabelFinder(text, 
									     new DefaultStringComparator(ce, ccs))), 
			     index));
    }

    /**
     * Waits JCheckBox by text.
     * @param cont Container to search component in.
     * @param text Button text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @return JCheckBox instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public static JCheckBox waitJCheckBox(Container cont, String text, boolean ce, boolean ccs) {
	return(waitJCheckBox(cont, text, ce, ccs, 0));
    }

    /**
     * Checks component type.
     */
    public static class JCheckBoxFinder extends Finder {
        /**
         * Constructs JCheckBoxFinder.
         * @param sf other searching criteria.
         */
	public JCheckBoxFinder(ComponentChooser sf) {
            super(JCheckBox.class, sf);
	}
        /**
         * Constructs JCheckBoxFinder.
         */
	public JCheckBoxFinder() {
            super(JCheckBox.class);
	}
    }
}
