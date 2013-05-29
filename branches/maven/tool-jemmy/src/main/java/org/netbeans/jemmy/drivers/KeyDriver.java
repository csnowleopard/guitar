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

package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.Timeout;
import org.netbeans.jemmy.operators.ComponentOperator;

/**
 * Defines how to simulate keyboard operations.
 */
public interface KeyDriver {

    /**
     * Presses a key.
     * @param oper Component operator.
     * @param keyCode Key code (<code>KeyEvent.VK_*</code> value)
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    public void pressKey(ComponentOperator oper, int keyCode, int modifiers);

    /**
     * Typed a key.
     * @param oper Component operator.
     * @param keyChar Symbol to be typed.
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    public void typedKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers);

    /**
     * Releases a key.
     * @param oper Component operator.
     * @param keyCode Key code (<code>KeyEvent.VK_*</code> value)
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    public void releaseKey(ComponentOperator oper, int keyCode, int modifiers);

    /**
     * Pushes a key.
     * @param oper Component operator.
     * @param keyCode Key code (<code>KeyEvent.VK_*</code> value)
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     * @param pushTime Time between pressing and releasing.
     */
    public void pushKey(ComponentOperator oper, int keyCode, int modifiers, Timeout pushTime);

    /**
     * Types a symbol.
     * @param oper Component operator.
     * @param keyCode Key code (<code>KeyEvent.VK_*</code> value)
     * @param keyChar Symbol to be typed.
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     * @param pushTime Time between pressing and releasing.
     */
    public void typeKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers, Timeout pushTime);
}
