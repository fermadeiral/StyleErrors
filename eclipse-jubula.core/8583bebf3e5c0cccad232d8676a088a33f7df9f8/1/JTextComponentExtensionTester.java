/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.swing.tester;

import javax.swing.text.JTextComponent;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractTextComponentTester;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;

/**
 * Tester class: this class is extending an existing component with an
 * additional action. It is extending 'AbstractTextComponentTester' since this
 * is the tester class which is used by the component we are extending in the
 * ComponentConfiguration.xml. This is very important since this
 * 'JTextComponentExtensionTester' is the class which will be called for all
 * actions which are used on JTextComponent.
 * 
 * @author BREDEX GmbH
 */
public class JTextComponentExtensionTester extends AbstractTextComponentTester {

    /**
     * 
     * @return the JTextComponent
     */
    private JTextComponent getTextField() {
        return (JTextComponent) getComponent().getRealComponent();
    }

    /**
     * Verifies the tooltip of a component
     * 
     * @param text the text to check against
     * @param operator the operator
     */
    @SuppressWarnings("nls")
    public void rcVerifyToolTip(String text, String operator) {
        if (text.equals("Exception")) {
            throw new StepExecutionException("This is a message",
                    EventFactory.createActionError("own Action error"));
        }
        final JTextComponent textField = getTextField();
        final String textToVerify = getEventThreadQueuer().invokeAndWait(
                "getToolTipText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() throws StepExecutionException {
                        return textField.getToolTipText();
                    }
                });
        Verifier.equals(text, textToVerify);
        Verifier.match(textToVerify, text, operator);
    }
}
