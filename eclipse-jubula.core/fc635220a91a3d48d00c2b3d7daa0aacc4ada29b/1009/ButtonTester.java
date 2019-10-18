/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IButtonComponent;
import org.eclipse.jubula.rc.common.util.Verifier;


/**
 * Implementation for all Button like classes, it holds the
 * general methods for the testing of buttons.
 * 
 * @author BREDEX GmbH
 *
 */
public class ButtonTester extends AbstractTextVerifiableTester {
    /**
     * @return the IButtonAdapter for the component. 
     */
    private IButtonComponent getButtonAdapter() {
        return (IButtonComponent)getComponent();
    }

    /**
     * Verifies the selected property.
     * 
     * @param selected The selected property value to verify.
     * @param timeout the maximum amount of time to wait for the component
     *                  to have the Selected property to be the same as 
     *                  the parameter
     */
    public void rcVerifySelected(final boolean selected, int timeout) {
        CheckWithTimeoutQueuer.invokeAndWait("rcVerifySelected", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        Verifier.equals(selected,
                                getButtonAdapter().isSelected());
                    }
                });
    }
    
    /**
     * Gets the selection property
     * @return the selection property
     */
    public Boolean rcIsSelected() {
        return getButtonAdapter().isSelected();
    }

    /** {@inheritDoc} */
    public String[] getTextArrayFromComponent() {
        String[] textArray = null;
        try {
            if (getComponent() instanceof IButtonComponent) {
                // This is needed because the observation modus try to use a MenuItem as Button.
                // Because in Swing the MenuItem is a child of AbstractButton.
                textArray = new String[] { getButtonAdapter().getText() };
            }
        } catch (StepExecutionException e) {
            // ok here - getText() might be an unsupported action
        }
        return textArray;
    }
}
