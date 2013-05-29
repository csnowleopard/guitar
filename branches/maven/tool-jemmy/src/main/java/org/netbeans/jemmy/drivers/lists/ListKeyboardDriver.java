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

package org.netbeans.jemmy.drivers.lists;

import java.awt.event.KeyEvent;

import org.netbeans.jemmy.Timeout;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ListOperator;

/**
 * List driver for java.awt.List component type.
 * Uses keyboard and mouse.
 *
 * @author Alexandre Iline(alexandre.iline@sun.com)
 */
public class ListKeyboardDriver extends ListAPIDriver implements MultiSelListDriver {
    /**
     * Constructs a ListKeyboardDriver.
     */
    public ListKeyboardDriver() {
	super();
    }

    public void selectItem(ComponentOperator oper, int index) {
	ListOperator loper = (ListOperator)oper;
	if(loper.isMultipleMode()) {
	    super.selectItem(loper, index);
	}
	DriverManager.getFocusDriver(oper).giveFocus(oper);
	KeyDriver   kDriver = DriverManager.getKeyDriver(oper);
	int current = loper.getSelectedIndex();
	int diff = 0;
	int key = 0;
	if(index > current) {
	    diff = index - current;
	    key = KeyEvent.VK_DOWN;
	} else {
	    diff = current - index;
	    key = KeyEvent.VK_UP;
	}
	Timeout pushTime = oper.getTimeouts().create("ComponentOperator.PushKeyTimeout");
	for(int i = 0; i < diff; i++) {
	    kDriver.pushKey(oper, key, 0, pushTime);
	}
	kDriver.pushKey(oper, KeyEvent.VK_ENTER, 0, pushTime);
    }
}
