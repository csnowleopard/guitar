/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SitarWindow;

/**
 * Wraps a {@link TreeItem}.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SitarTableItem extends SitarItem {

	private final TableItem item;

	/**
	 * Wrap the given widget that lives in the given window.
	 * @param item the widget to wrap
	 * @param window the window the widget lives in
	 */
	protected SitarTableItem(TableItem item, SitarWindow window) {
		super(item, window);
		this.item = item;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see TreeItem#getItems()
	 */
	@Override
	public List<GComponent> getChildren() {
		return Collections.emptyList();
	}


	/**
	 * Returns whether this widget is enabled or not. By default, {@link Item
	 * Items} have no notion of being enabled. Thus, this method simply returns
	 * <code>false</code>. Subclasses, such as {@link SitarMenuItem}, may support
	 * being enabled, and are encouraged to override this method.
	 * 
	 * @return <code>false</code>
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}	
}
