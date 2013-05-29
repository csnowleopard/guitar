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

package org.netbeans.jemmy.drivers.scrolling;

import java.awt.Point;
import java.awt.Scrollbar;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ScrollbarOperator;

/**
 * ScrollDriver for java.awt.Scrollbar component type.
 *
 * @author Alexandre Iline(alexandre.iline@sun.com)
 */
public class ScrollbarDriver extends AWTScrollDriver {
    private static final int CLICK_OFFSET = 5;

    /**
     * Constructs a ScrollbarDriver.
     */
    public ScrollbarDriver() {
	super(new String[] {"org.netbeans.jemmy.operators.ScrollbarOperator"});
    }

    @Override
    protected int position(ComponentOperator oper, int orientation) {
        return ((ScrollbarOperator)oper).getValue();
    }

    public void scrollToMinimum(final ComponentOperator oper, final int orientation) {
	scroll(oper, 
	       new ScrollAdjuster() {
		public int getScrollDirection() {
		    return((((ScrollbarOperator)oper).getMinimum() < 
			    ((ScrollbarOperator)oper).getValue()) ? 
			   DECREASE_SCROLL_DIRECTION :
			   DO_NOT_TOUCH_SCROLL_DIRECTION);
		}
		public int getScrollOrientation() {
		    return(((ScrollbarOperator)oper).getOrientation());
		}
		public String getDescription() {
		    return("Scroll to minimum");
		}
	    });
    }

    public void scrollToMaximum(final ComponentOperator oper, final int orientation) {
	scroll(oper, 
	       new ScrollAdjuster() {
		public int getScrollDirection() {
		    return(((((ScrollbarOperator)oper).getMaximum() - 
			     ((ScrollbarOperator)oper).getVisibleAmount()) > 
			    ((ScrollbarOperator)oper).getValue()) ? 
			   INCREASE_SCROLL_DIRECTION :
			   DO_NOT_TOUCH_SCROLL_DIRECTION);
		}
		public int getScrollOrientation() {
		    return(((ScrollbarOperator)oper).getOrientation());
		}
		public String getDescription() {
		    return("Scroll to maximum");
		}
	    });
    }

    protected Point getClickPoint(ComponentOperator oper, int direction, int orientation) {
	int x, y;
	if       (orientation == Scrollbar.HORIZONTAL) {
	    if       (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
		x = oper.getWidth() - 1 - CLICK_OFFSET;
	    } else if(direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
		x = CLICK_OFFSET;
	    } else {
		return(null);
	    }
	    y = oper.getHeight() / 2;
	} else if(orientation == Scrollbar.VERTICAL) {
	    if       (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
		y = oper.getHeight() - 1 - CLICK_OFFSET;
	    } else if(direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
		y = CLICK_OFFSET;
	    } else {
		return(null);
	    }
	    x = oper.getWidth() / 2;
	} else {
	    return(null);
	}
	return(new Point(x, y));
    }
}
