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
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * This interface holds all methods which are needed to test
 * ComboBox like components.
 * @author BREDEX GmbH
 *
 */
public interface IComboComponent extends ITextComponent {
    /**
     * @return <code>true</code> if the combobox is editable, <code>false</code>
     *         otherwise
     */
    boolean isEditable();
    
    /**
     * select the whole text of the component.
     */
    void selectAll();
    
    
    /**
     * @return The currently selected index for the combo box, or -1 if no
     *          index is currently selected.
     */
    public int getSelectedIndex();
        
    /**
     * Selects the combobox element with the passed index.
     * 
     * @param index
     *            the zero based index of the entry
     */
    public void select(int index);
    
    /**
     * Inputs <code>text</code> to <code>component</code>.<br>
     * 
     * @param text
     *            the text to type
     * @param replace
     *            whether to replace the text or not
     * @throws StepExecutionException
     *             if an error occurs during typing <code>text</code>
     * @throws IllegalArgumentException
     *             if <code>component</code> or <code>text</code> are null
     */
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException;
    
    /**
     * Gets all Values from the <code>component</code> as <code>String</code> array
     * @return the values from the component
     */
    public String[] getValues();
}
