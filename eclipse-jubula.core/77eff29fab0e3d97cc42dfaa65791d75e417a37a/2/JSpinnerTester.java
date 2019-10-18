/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.swing.tester;

import java.awt.Component;

import javax.swing.JSpinner;
import javax.swing.plaf.basic.BasicArrowButton;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.tester.WidgetTester;

/**
 * Tester Class for the AUT-Agent. This class realizes the technical access to
 * provide testability for new component type: JSpinner. By implementing the
 * class "WidgetTester" you have nothing to implement to enable testability of
 * your new component on the "Graphics Component"-level. That means all actions
 * which are available for the "Graphics Component" should work for your new
 * component.
 * 
 * @author BREDEX GmbH
 * 
 */
@SuppressWarnings("nls")
public class JSpinnerTester extends WidgetTester {

    /**
     * @return the casted Spinner instance
     */
    protected JSpinner getSpinner() {
        return (JSpinner) getRealComponent();
    }

    /**
     * Clicks a variable number of times on the increment button of the Spinner.
     * 
     * @param count
     *            The number of times the increment button should be clicked on.
     * @param mouseButton
     *            The mouse button that should be used to click on the increment
     *             button.
     */
    public void rcClickIncrement(final Integer count, final int mouseButton) {
        final BasicArrowButton incrementButton = 
                findArrowButton("Spinner.nextButton");

        getRobot().click(incrementButton, null, 
                ClickOptions.create().setClickCount(count)
                .setMouseButton(mouseButton), 50, false, 50, false);
    }

    /**
     * Clicks a variable number of times on the decrement button of the Spinner.
     * 
     * @param count
     *            The number of times the decrement button should be clicked on.
     * @param mouseButton
     *            The mouse button that should be used to click on the increment
     *            button.
     */
    public void rcClickDecrement(final Integer count, final int mouseButton) {
        final BasicArrowButton decrementButton = 
                findArrowButton("Spinner.previousButton");

        getRobot().click(decrementButton, null, 
                ClickOptions.create().setClickCount(count)
                .setMouseButton(mouseButton), 50, false, 50, false);
    }
    
    /**
     * Iterates over the Spinner components to find the button with the
     * given name.
     * @param buttonName the button's name that should be found
     * @return the button or null if it could not be found
     */
    private BasicArrowButton findArrowButton(final String buttonName) {
        final Component[] components = getSpinner().getComponents();
        BasicArrowButton tempButton = null;
        for (Component component : components) {
            if (component instanceof BasicArrowButton) {
                BasicArrowButton button = (BasicArrowButton) component;
                if (button.getName().equals(buttonName)) {
                    tempButton = button;
                }
            }
        }
        final BasicArrowButton button = tempButton;
        return button;
    }
}