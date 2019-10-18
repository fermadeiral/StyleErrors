/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.swt.tester;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.WidgetTester;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.widgets.Group;

/**
 * Tester Class for the RcpAccessor. This class realizes the technical access to
 * provide testability for new component type: Group. By implementing the
 * abstract class "AbstractControlImplClass" you only have to implement a few
 * methods to enable testability of your new component on the
 * "Graphics Component"-level.
 * 
 * @author BREDEX GmbH
 * 
 */
public class GroupTester extends WidgetTester {
    /**
     * @return the casted Group instance
     */
    protected Group getGroup() {
        return (Group) getRealComponent();
    }
    
    /**
     * Verifies the group title text
     * 
     * @param text
     *            The text to verify.
     * @param operator
     *            The operation used to verify
     * @throws StepExecutionException
     *             if an error occurs
     */
    public void rcVerifyText(String text, String operator)
        throws StepExecutionException {
        final Group group = getGroup();
        String groupLabelText = getEventThreadQueuer()
                .invokeAndWait("getText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        return SwtUtils.removeMnemonics(group.getText());
                    }
                });
        Verifier.match(groupLabelText, text, operator);
    }
}