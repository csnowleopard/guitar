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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ListDriver;

/**
 * <BR><BR>Timeouts used: <BR>
 * ComponentOperator.WaitComponentTimeout - time to wait component displayed <BR>.
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 * @author Alexandre Iline (alexandre.iline@sun.com)
 *	
 */

public class JTabbedPaneOperator extends JComponentOperator
    implements Outputable {

    /**
     * Identifier for a "selected page" property.
     * @see #getDump
     */
    public static final String SELECTED_PAGE_DPROP = "Selected";

    /**
     * Identifier for a "page" properties.
     * @see #getDump
     */
    public static final String PAGE_PREFIX_DPROP = "Page";

    private TestOut output;
    private ListDriver driver;

    /**
     * Constructor.
     * @param b a component
     */
    public JTabbedPaneOperator(JTabbedPane b) {
	super(b);
	driver = DriverManager.getListDriver(getClass());
    }

    /**
     * Constructs a JTabbedPaneOperator object.
     * @param cont a container
     * @param chooser a component chooser specifying searching criteria.
     * @param index an index between appropriate ones.
     */
    public JTabbedPaneOperator(ContainerOperator cont, ComponentChooser chooser, int index) {
	this((JTabbedPane)cont.
             waitSubComponent(new JTabbedPaneFinder(chooser),
                              index));
	copyEnvironment(cont);
    }

    /**
     * Constructs a JTabbedPaneOperator object.
     * @param cont a container
     * @param chooser a component chooser specifying searching criteria.
     */
    public JTabbedPaneOperator(ContainerOperator cont, ComponentChooser chooser) {
	this(cont, chooser, 0);
    }

    /**
     * Constructor.
     * Waits component by tab title first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont a container
     * @param text Tab title. 
     * @param tabIndex a page index to check. if equal to -1, selected page is checked.
     * @param index Ordinal component index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JTabbedPaneOperator(ContainerOperator cont, String text, int tabIndex, int index) {
	this((JTabbedPane)waitComponent(cont, 
					new JTabbedPaneByItemFinder(text, tabIndex,
								    cont.getComparator()),
					index));
	copyEnvironment(cont);
    }

    /**
     * Constructor.
     * Waits component by activetab title first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont a container
     * @param text Title of tab which is currently selected. 
     * @param index Ordinal component index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JTabbedPaneOperator(ContainerOperator cont, String text, int index) {
	this(cont, text, -1, index);
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont a container
     * @param text Title of tab which is currently selected. 
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JTabbedPaneOperator(ContainerOperator cont, String text) {
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
    public JTabbedPaneOperator(ContainerOperator cont, int index) {
	this((JTabbedPane)
	     waitComponent(cont, 
			   new JTabbedPaneFinder(),
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
    public JTabbedPaneOperator(ContainerOperator cont) {
	this(cont, 0);
    }

    /**
     * Searches JTabbedPane in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return JTabbedPane instance or null if component was not found.
     */
    public static JTabbedPane findJTabbedPane(Container cont, ComponentChooser chooser, int index) {
	return((JTabbedPane)findComponent(cont, new JTabbedPaneFinder(chooser), index));
    }

    /**
     * Searches 0'th JTabbedPane in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return JTabbedPane instance or null if component was not found.
     */
    public static JTabbedPane findJTabbedPane(Container cont, ComponentChooser chooser) {
	return(findJTabbedPane(cont, chooser, 0));
    }

    /**
     * Searches JTabbedPane by tab title.
     * @param cont Container to search component in.
     * @param text Tooltip text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param itemIndex Tab index. if -1 selected one is checked.
     * @param index Ordinal component index.
     * @return JTabbedPane instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTabbedPane findJTabbedPane(Container cont, String text, boolean ce, boolean ccs, int itemIndex, int index) {
	return(findJTabbedPane(cont, new JTabbedPaneByItemFinder(text, itemIndex, new DefaultStringComparator(ce, ccs)), index));
    }

    /**
     * Searches JTabbedPane by tab title.
     * @param cont Container to search component in.
     * @param text Tooltip text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param itemIndex Tab index. if -1 selected one is checked.
     * @return JTabbedPane instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTabbedPane findJTabbedPane(Container cont, String text, boolean ce, boolean ccs, int itemIndex) {
	return(findJTabbedPane(cont, text, ce, ccs, itemIndex, 0));
    }

    /**
     * Searches JTabbedPane object which component lies on.
     * @param comp Component to find JTabbedPane under.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return JTabbedPane instance or null if component was not found.
     */
    public static JTabbedPane findJTabbedPaneUnder(Component comp, ComponentChooser chooser) {
	return((JTabbedPane)findContainerUnder(comp, new JTabbedPaneFinder(chooser)));
    }
    
    /**
     * Searches JTabbedPane object which component lies on.
     * @param comp Component to find JTabbedPane under.
     * @return JTabbedPane instance or null if component was not found.
     */
    public static JTabbedPane findJTabbedPaneUnder(Component comp) {
	return(findJTabbedPaneUnder(comp, new JTabbedPaneFinder()));
    }
    
    /**
     * Waits JTabbedPane in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return JTabbedPane instance.
     * @throws TimeoutExpiredException
     */
    public static JTabbedPane waitJTabbedPane(Container cont, ComponentChooser chooser, int index) {
	return((JTabbedPane)waitComponent(cont, new JTabbedPaneFinder(chooser), index));
    }

    /**
     * Waits 0'th JTabbedPane in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return JTabbedPane instance.
     * @throws TimeoutExpiredException
     */
    public static JTabbedPane waitJTabbedPane(Container cont, ComponentChooser chooser) {
	return(waitJTabbedPane(cont, chooser, 0));
    }

    /**
     * Waits JTabbedPane by tab title.
     * @param cont Container to search component in.
     * @param text Tooltip text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param itemIndex Tab index. if -1 selected one is checked.
     * @param index Ordinal component index.
     * @return JTabbedPane instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public static JTabbedPane waitJTabbedPane(Container cont, String text, boolean ce, boolean ccs, int itemIndex, int index) {
	return(waitJTabbedPane(cont, new JTabbedPaneByItemFinder(text, itemIndex, new DefaultStringComparator(ce, ccs)), index));
    }

    /**
     * Waits JTabbedPane by tab title.
     * @param cont Container to search component in.
     * @param text Tooltip text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param itemIndex Tab index. if -1 selected one is checked.
     * @return JTabbedPane instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public static JTabbedPane waitJTabbedPane(Container cont, String text, boolean ce, boolean ccs, int itemIndex) {
	return(waitJTabbedPane(cont, text, ce, ccs, itemIndex, 0));
    }

    public void setOutput(TestOut output) {
	super.setOutput(output.createErrorOutput());
	this.output = output;
    }

    public TestOut getOutput() {
	return(output);
    }

    public void copyEnvironment(Operator anotherOperator) {
	super.copyEnvironment(anotherOperator);
	driver = 
	    (ListDriver)DriverManager.
	    getDriver(DriverManager.LIST_DRIVER_ID,
		      getClass(), 
		      anotherOperator.getProperties());
    }

    /**
     * Searches tab index by tab title.
     * @param chooser page searching criteria
     * @return a page index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @deprecated Use findPage(String) or findPage(String, StringComparator)
     */
    public int findPage(TabPageChooser chooser) {
	for(int i = 0; i < getTabCount(); i++) {
	    if(chooser.checkPage(this, i)) {
		return(i);
	    }
	}
	return(-1);
    }

    /**
     * Searches tab index by tab title.
     * @param title a page title.
     * @param comparator a string comparision algorithm
     * @return a page index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @deprecated Use findPage(String) or findPage(String, StringComparator)
     */
    public int findPage(String title, StringComparator comparator) {
        return(findPage(new BySubStringTabPageChooser(title, comparator)));
    }

    /**
     * Searches tab index by tab title.
     * isCaptionEqual method is used to compare page title with
     * match.
     * @param title a page title.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @return a page index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @deprecated Use findPage(String) or findPage(String, StringComparator)
     */
    public int findPage(String title, boolean ce, boolean ccs) {
	return(findPage(title, new DefaultStringComparator(ce, ccs)));
    }

    /**
     * Searches tab index by tab title.
     * isCaptionEqual method is used to compare page title with
     * match.
     * Uses StringComparator assigned to this object.
     * @param title a page title.
     * @return a page index.
     */
    public int findPage(String title) {
	return(findPage(title, getComparator()));
    }

    /**
     * Selects tab.
     * @param index a page ordinal index.
     * @return a root corresponding to the page.
     */
    public Component selectPage(int index) {
	output.printLine("Selecting " + index + "'th page in tabbed pane\n    :" + toStringSource());
        makeComponentVisible();
	driver.selectItem(this, index);
	if(getVerification()) {
            waitSelected(index);
        }
	return(getComponentAt(index));
    }

    /**
     * Selects tab.
     * @param chooser page searching criteria
     * @return a root corresponding to the page.
     */
    public Component selectPage(TabPageChooser chooser) {
	output.printLine("Selecting \"" + chooser.getDescription() + 
                         "\" page in tabbed pane\n    :" + toStringSource());
 	return(selectPage(waitPage(chooser)));
    }

    /**
     * Selects tab.
     * @param title a page title.
     * @param comparator a string comparision algorithm
     * @return a root corresponding to the page.
     */
    public Component selectPage(String title, StringComparator comparator) {
        return(selectPage(new BySubStringTabPageChooser(title, comparator)));
    }

    /**
     * Selects tab by tab title.
     * @param title a page title.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @deprecated Use selectPage(String) or selectPage(String, StringComparator)
     * @return a root corresponding to the page.
     */
    public Component selectPage(String title, boolean ce, boolean ccs) {
	return(selectPage(title, new DefaultStringComparator(ce, ccs)));
    }

    /**
     * Selects tab by tab title.
     * Uses StringComparator assigned to this object.
     * @param title a page title.
     * @return a root corresponding to the page.
     */
    public Component selectPage(String title) {
        return(selectPage(title, getComparator()));
    }

    /**
     * Wait for a page to exist.
     * @param chooser page searching criteria
     * @return a page index.
     */
    public int waitPage(final TabPageChooser chooser) {
	waitState(new ComponentChooser() {
                public boolean checkComponent(Component comp) {
                    return(findPage(chooser) > -1);
                }
                public String getDescription() {
                    return("Tabbed with " + chooser.getDescription() + " page.");
                }
            });
        return(findPage(chooser));
    }

    /**
     * Wait for a page to exist.
     * @param title a page title.
     * @param comparator a string comparision algorithm
     * @return a page index.
     */
    public int waitPage(String title, StringComparator comparator) {
        return(waitPage(new BySubStringTabPageChooser(title, comparator)));
    }

    /**
     * Wait for a page to exist.
     * @param title a page title.
     * @return a page index.
     */
    public int waitPage(String title) {
        return(waitPage(title, getComparator()));
    }

    /**
     * Waits for a page to be selected.
     * @param pageIndex an index of a page to be selected.
     */
    public void waitSelected(final int pageIndex) {
	getOutput().printLine("Wait " + Integer.toString(pageIndex) + "'th page to be " +
			      " selected in component \n    : "+
			      toStringSource());
	getOutput().printGolden("Wait " + Integer.toString(pageIndex) + "'th page to be " +
				" selected");
	waitState(new ComponentChooser() {
		public boolean checkComponent(Component comp) {
		    return(getSelectedIndex() == pageIndex);
		}
		public String getDescription() {
		    return(Integer.toString(pageIndex) + "'th page has been selected");
		}
	    });
    }

    /**
     * Waits for a page to be selected.
     * @param pageTitle a title of a page to be selected.
     */
    public void waitSelected(final String pageTitle) {
	waitSelected(findPage(pageTitle));
    }

    public Hashtable getDump() {
	Hashtable result = super.getDump();
	if(((JTabbedPane)getSource()).getSelectedIndex() != -1) {
	    result.put(SELECTED_PAGE_DPROP, ((JTabbedPane)getSource()).
		       getTitleAt(((JTabbedPane)getSource()).getSelectedIndex()));
	}
	String[] pages = new String[((JTabbedPane)getSource()).getTabCount()];
	for(int i = 0; i < ((JTabbedPane)getSource()).getTabCount(); i++) {
	    pages[i] = ((JTabbedPane)getSource()).getTitleAt(i);
	}
	addToDump(result, PAGE_PREFIX_DPROP, pages);
	return(result);
    }

    ////////////////////////////////////////////////////////
    //Mapping                                             //

    /**Maps <code>JTabbedPane.addChangeListener(ChangeListener)</code> through queue*/
    public void addChangeListener(final ChangeListener changeListener) {
	runMapping(new MapVoidAction("addChangeListener") {
		public void map() {
		    ((JTabbedPane)getSource()).addChangeListener(changeListener);
		}});}

    /**Maps <code>JTabbedPane.addTab(String, Component)</code> through queue*/
    public void addTab(final String string, final Component component) {
	runMapping(new MapVoidAction("addTab") {
		public void map() {
		    ((JTabbedPane)getSource()).addTab(string, component);
		}});}

    /**Maps <code>JTabbedPane.addTab(String, Icon, Component)</code> through queue*/
    public void addTab(final String string, final Icon icon, final Component component) {
	runMapping(new MapVoidAction("addTab") {
		public void map() {
		    ((JTabbedPane)getSource()).addTab(string, icon, component);
		}});}

    /**Maps <code>JTabbedPane.addTab(String, Icon, Component, String)</code> through queue*/
    public void addTab(final String string, final Icon icon, final Component component, final String string1) {
	runMapping(new MapVoidAction("addTab") {
		public void map() {
		    ((JTabbedPane)getSource()).addTab(string, icon, component, string1);
		}});}

    /**Maps <code>JTabbedPane.getBackgroundAt(int)</code> through queue*/
    public Color getBackgroundAt(final int i) {
	return((Color)runMapping(new MapAction("getBackgroundAt") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getBackgroundAt(i));
		}}));}

    /**Maps <code>JTabbedPane.getBoundsAt(int)</code> through queue*/
    public Rectangle getBoundsAt(final int i) {
	return((Rectangle)runMapping(new MapAction("getBoundsAt") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getBoundsAt(i));
		}}));}

    /**Maps <code>JTabbedPane.getComponentAt(int)</code> through queue*/
    public Component getComponentAt(final int i) {
	return((Component)runMapping(new MapAction("getComponentAt") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getComponentAt(i));
		}}));}

    /**Maps <code>JTabbedPane.getDisabledIconAt(int)</code> through queue*/
    public Icon getDisabledIconAt(final int i) {
	return((Icon)runMapping(new MapAction("getDisabledIconAt") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getDisabledIconAt(i));
		}}));}

    /**Maps <code>JTabbedPane.getForegroundAt(int)</code> through queue*/
    public Color getForegroundAt(final int i) {
	return((Color)runMapping(new MapAction("getForegroundAt") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getForegroundAt(i));
		}}));}

    /**Maps <code>JTabbedPane.getIconAt(int)</code> through queue*/
    public Icon getIconAt(final int i) {
	return((Icon)runMapping(new MapAction("getIconAt") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getIconAt(i));
		}}));}

    /**Maps <code>JTabbedPane.getModel()</code> through queue*/
    public SingleSelectionModel getModel() {
	return((SingleSelectionModel)runMapping(new MapAction("getModel") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getModel());
		}}));}

    /**Maps <code>JTabbedPane.getSelectedComponent()</code> through queue*/
    public Component getSelectedComponent() {
	return((Component)runMapping(new MapAction("getSelectedComponent") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getSelectedComponent());
		}}));}

    /**Maps <code>JTabbedPane.getSelectedIndex()</code> through queue*/
    public int getSelectedIndex() {
	return(runMapping(new MapIntegerAction("getSelectedIndex") {
		public int map() {
		    return(((JTabbedPane)getSource()).getSelectedIndex());
		}}));}

    /**Maps <code>JTabbedPane.getTabCount()</code> through queue*/
    public int getTabCount() {
	return(runMapping(new MapIntegerAction("getTabCount") {
		public int map() {
		    return(((JTabbedPane)getSource()).getTabCount());
		}}));}

    /**Maps <code>JTabbedPane.getTabPlacement()</code> through queue*/
    public int getTabPlacement() {
	return(runMapping(new MapIntegerAction("getTabPlacement") {
		public int map() {
		    return(((JTabbedPane)getSource()).getTabPlacement());
		}}));}

    /**Maps <code>JTabbedPane.getTabRunCount()</code> through queue*/
    public int getTabRunCount() {
	return(runMapping(new MapIntegerAction("getTabRunCount") {
		public int map() {
		    return(((JTabbedPane)getSource()).getTabRunCount());
		}}));}

    /**Maps <code>JTabbedPane.getTitleAt(int)</code> through queue*/
    public String getTitleAt(final int i) {
	return((String)runMapping(new MapAction("getTitleAt") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getTitleAt(i));
		}}));}

    /**Maps <code>JTabbedPane.getUI()</code> through queue*/
    public TabbedPaneUI getUI() {
	return((TabbedPaneUI)runMapping(new MapAction("getUI") {
		public Object map() {
		    return(((JTabbedPane)getSource()).getUI());
		}}));}

    /**Maps <code>JTabbedPane.indexOfComponent(Component)</code> through queue*/
    public int indexOfComponent(final Component component) {
	return(runMapping(new MapIntegerAction("indexOfComponent") {
		public int map() {
		    return(((JTabbedPane)getSource()).indexOfComponent(component));
		}}));}

    /**Maps <code>JTabbedPane.indexOfTab(String)</code> through queue*/
    public int indexOfTab(final String string) {
	return(runMapping(new MapIntegerAction("indexOfTab") {
		public int map() {
		    return(((JTabbedPane)getSource()).indexOfTab(string));
		}}));}

    /**Maps <code>JTabbedPane.indexOfTab(Icon)</code> through queue*/
    public int indexOfTab(final Icon icon) {
	return(runMapping(new MapIntegerAction("indexOfTab") {
		public int map() {
		    return(((JTabbedPane)getSource()).indexOfTab(icon));
		}}));}

    /**Maps <code>JTabbedPane.insertTab(String, Icon, Component, String, int)</code> through queue*/
    public void insertTab(final String string, final Icon icon, final Component component, final String string1, final int i) {
	runMapping(new MapVoidAction("insertTab") {
		public void map() {
		    ((JTabbedPane)getSource()).insertTab(string, icon, component, string1, i);
		}});}

    /**Maps <code>JTabbedPane.isEnabledAt(int)</code> through queue*/
    public boolean isEnabledAt(final int i) {
	return(runMapping(new MapBooleanAction("isEnabledAt") {
		public boolean map() {
		    return(((JTabbedPane)getSource()).isEnabledAt(i));
		}}));}

    /**Maps <code>JTabbedPane.removeChangeListener(ChangeListener)</code> through queue*/
    public void removeChangeListener(final ChangeListener changeListener) {
	runMapping(new MapVoidAction("removeChangeListener") {
		public void map() {
		    ((JTabbedPane)getSource()).removeChangeListener(changeListener);
		}});}

    /**Maps <code>JTabbedPane.removeTabAt(int)</code> through queue*/
    public void removeTabAt(final int i) {
	runMapping(new MapVoidAction("removeTabAt") {
		public void map() {
		    ((JTabbedPane)getSource()).removeTabAt(i);
		}});}

    /**Maps <code>JTabbedPane.setBackgroundAt(int, Color)</code> through queue*/
    public void setBackgroundAt(final int i, final Color color) {
	runMapping(new MapVoidAction("setBackgroundAt") {
		public void map() {
		    ((JTabbedPane)getSource()).setBackgroundAt(i, color);
		}});}

    /**Maps <code>JTabbedPane.setComponentAt(int, Component)</code> through queue*/
    public void setComponentAt(final int i, final Component component) {
	runMapping(new MapVoidAction("setComponentAt") {
		public void map() {
		    ((JTabbedPane)getSource()).setComponentAt(i, component);
		}});}

    /**Maps <code>JTabbedPane.setDisabledIconAt(int, Icon)</code> through queue*/
    public void setDisabledIconAt(final int i, final Icon icon) {
	runMapping(new MapVoidAction("setDisabledIconAt") {
		public void map() {
		    ((JTabbedPane)getSource()).setDisabledIconAt(i, icon);
		}});}

    /**Maps <code>JTabbedPane.setEnabledAt(int, boolean)</code> through queue*/
    public void setEnabledAt(final int i, final boolean b) {
	runMapping(new MapVoidAction("setEnabledAt") {
		public void map() {
		    ((JTabbedPane)getSource()).setEnabledAt(i, b);
		}});}

    /**Maps <code>JTabbedPane.setForegroundAt(int, Color)</code> through queue*/
    public void setForegroundAt(final int i, final Color color) {
	runMapping(new MapVoidAction("setForegroundAt") {
		public void map() {
		    ((JTabbedPane)getSource()).setForegroundAt(i, color);
		}});}

    /**Maps <code>JTabbedPane.setIconAt(int, Icon)</code> through queue*/
    public void setIconAt(final int i, final Icon icon) {
	runMapping(new MapVoidAction("setIconAt") {
		public void map() {
		    ((JTabbedPane)getSource()).setIconAt(i, icon);
		}});}

    /**Maps <code>JTabbedPane.setModel(SingleSelectionModel)</code> through queue*/
    public void setModel(final SingleSelectionModel singleSelectionModel) {
	runMapping(new MapVoidAction("setModel") {
		public void map() {
		    ((JTabbedPane)getSource()).setModel(singleSelectionModel);
		}});}

    /**Maps <code>JTabbedPane.setSelectedComponent(Component)</code> through queue*/
    public void setSelectedComponent(final Component component) {
	runMapping(new MapVoidAction("setSelectedComponent") {
		public void map() {
		    ((JTabbedPane)getSource()).setSelectedComponent(component);
		}});}

    /**Maps <code>JTabbedPane.setSelectedIndex(int)</code> through queue*/
    public void setSelectedIndex(final int i) {
	runMapping(new MapVoidAction("setSelectedIndex") {
		public void map() {
		    ((JTabbedPane)getSource()).setSelectedIndex(i);
		}});}

    /**Maps <code>JTabbedPane.setTabPlacement(int)</code> through queue*/
    public void setTabPlacement(final int i) {
	runMapping(new MapVoidAction("setTabPlacement") {
		public void map() {
		    ((JTabbedPane)getSource()).setTabPlacement(i);
		}});}

    /**Maps <code>JTabbedPane.setTitleAt(int, String)</code> through queue*/
    public void setTitleAt(final int i, final String string) {
	runMapping(new MapVoidAction("setTitleAt") {
		public void map() {
		    ((JTabbedPane)getSource()).setTitleAt(i, string);
		}});}

    /**Maps <code>JTabbedPane.setUI(TabbedPaneUI)</code> through queue*/
    public void setUI(final TabbedPaneUI tabbedPaneUI) {
	runMapping(new MapVoidAction("setUI") {
		public void map() {
		    ((JTabbedPane)getSource()).setUI(tabbedPaneUI);
		}});}

    //End of mapping                                      //
    ////////////////////////////////////////////////////////

    /**
     * Specifies criteria for a tab page searching.
     */
    public interface TabPageChooser {
	/**
	 * Should be true if a page is good.
	 * @param oper Operator used to search item.
	 * @param index Index of a page be checked.
         * @return true if a page fits the criteria.
	 */
	public boolean checkPage(JTabbedPaneOperator oper, int index);

	/**
	 * Page description.
         * @return a description.
	 */
	public String getDescription();
    }

    /**
     * Exception is thrown if a nonexistent page is trying to be selected.
     */
    public class NoSuchPageException extends JemmyInputException {
	/**
	 * Constructor.
         * @param item nonexistent page title.
	 */
	public NoSuchPageException(String item) {
	    super("No such page as \"" + item + "\"", getSource());
	}
    }

    /**
     * Allows to find component by page title.
     */
    public static class JTabbedPaneByItemFinder implements ComponentChooser {
	String title;
	int itemIndex;
	StringComparator comparator;
        /**
         * Constructs JTabbedPaneByItemFinder.
         * @param lb a text pattern
         * @param ii page index to check. If equal to -1, selected page is checked.
         * @param comparator specifies string comparision algorithm.
         */
	public JTabbedPaneByItemFinder(String lb, int ii, StringComparator comparator) {
	    title = lb;
	    itemIndex = ii;
	    this.comparator = comparator;
	}
        /**
         * Constructs JTabbedPaneByItemFinder.
         * @param lb a text pattern
         * @param ii page index to check. If equal to -1, selected page is checked.
         */
	public JTabbedPaneByItemFinder(String lb, int ii) {
            this(lb, ii, Operator.getDefaultStringComparator());
	}
	public boolean checkComponent(Component comp) {
	    if(comp instanceof JTabbedPane) {
		if(title == null) {
		    return(true);
		}
		JTabbedPaneOperator tpo = new JTabbedPaneOperator((JTabbedPane)comp);
		if(tpo.getTabCount() > itemIndex) {
		    int ii = itemIndex;
		    if(ii == -1) {
			ii = tpo.getSelectedIndex();
			if(ii == -1) {
			    return(false);
			}
		    }
		    return(comparator.equals(tpo.getTitleAt(ii),
					     title));
		}
	    }
	    return(false);
	}
	public String getDescription() {
	    return("JTabbedPane with text \"" + title + "\" in " + 
		   (new Integer(itemIndex)).toString() + "'th item");
	}
    }

    /**
     * Checks component type.
     */
    public static class JTabbedPaneFinder extends Finder {
        /**
         * Constructs JTabbedPaneFinder.
         * @param sf other searching criteria.
         */
	public JTabbedPaneFinder(ComponentChooser sf) {
            super(JTabbedPane.class, sf);
	}
        /**
         * Constructs JTabbedPaneFinder.
         */
	public JTabbedPaneFinder() {
            super(JTabbedPane.class);
	}
    }

    private static class BySubStringTabPageChooser implements TabPageChooser {
        String title;
        StringComparator comparator;

        public BySubStringTabPageChooser(String title, StringComparator comparator) {
            this.title = title;
            this.comparator = comparator;
        }

	public boolean checkPage(JTabbedPaneOperator oper, int index) {
            return(comparator.equals(oper.getTitleAt(index),
                                     title));
        }

	public String getDescription() {
	    return("Page having \"" + title + "\" title.");
        }
    }

}
