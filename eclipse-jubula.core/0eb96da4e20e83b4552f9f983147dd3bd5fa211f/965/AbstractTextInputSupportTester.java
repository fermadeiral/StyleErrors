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

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * This class represents the general implementation for components,
 *  which have text input support.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractTextInputSupportTester 
    extends AbstractTextVerifiableTester {

    /**
     * Verifies the editable property of the current component.<br>
     * If it is a complex component, it is always the selected object.
     * @param editable The editable property to verify.
     * @param timeout the maximum amount of time to wait for the component
     *                  to have the editable status to be the same as the parameter
     */
    public abstract void rcVerifyEditable(boolean editable, int timeout);
    /**
     * Types <code>text</code> into the component. This replaces the shown
     * content.<br>
     * If it is a complex component, it is always the selected object.
     * @param text the text to type in
     * @throws StepExecutionException
     *  If there is no selected cell, or if the cell is not editable,
     *  or if the table cell editor permits the text to be written.
     */
    public abstract void rcReplaceText(String text) 
        throws StepExecutionException;
    /**
     * Writes the passed text into the currently component.<br>
     * If it is a complex component, it is always the selected object.
     * @param text The text.
     * @throws StepExecutionException
     *             If there is no selected cell, or if the cell is not editable,
     *             or if the table cell editor permits the text to be written.
     */
    public abstract void rcInputText(final String text) 
        throws StepExecutionException;
}
