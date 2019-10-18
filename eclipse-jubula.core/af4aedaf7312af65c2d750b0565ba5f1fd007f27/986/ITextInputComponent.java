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
/**
 * Interface for all needed methods which are needed from TextComponents
 * @author BREDEX GmbH
 *
 */
public interface ITextInputComponent extends ITextComponent {

    /**
     * Sets the caret to a specific position.
     * 
     * @param start The zero based index at which the selection begins.
     */
    void setSelection(int start);
    
    /**
     * Selects text in the component.
     * 
     * @param start
     *            the zero based index at which the selection begins.
     * @param end
     *            the zero based index at which the selection ends.
     */
    void setSelection(int start, int end);
    
    /**
     * 
     * @return the selected text
     */
    String getSelectionText();

    /**
     * Selects all text in the component.
     */
    void selectAll();

    /**
     * @return the value if the Text Component is editable
     */
    boolean isEditable();
}
