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
package org.eclipse.jubula.client.ui.rcp.search.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.client.ui.rcp.search.data.SelectionState;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;


/**
 * A helper class for listening on the selection state of buttons
 * used in the search dialog.
 * @author BREDEX GmbH
 * @created April 25, 2013
 * @see org.eclipse.jubula.client.ui.rcp.search.data.SearchOptions
 */
public class ButtonSelections implements SelectionListener {

    /** The counter of calling next */
    private int m_nextCounter;

    /** The list of stored selection data. */
    private List<SelectionState> m_storedSelectionDatas;

    /** The list of buttons. */
    private Map<Button, Integer> m_buttons;

    /** The list of stored selection data. */
    private List<SelectionState> m_newSelectionDatas;

    /**
     * Create an empty list of selection data.
     */
    public ButtonSelections() {
        m_storedSelectionDatas = new ArrayList<SelectionState>();
        m_buttons = new HashMap<Button, Integer>();
        m_newSelectionDatas = new ArrayList<SelectionState>();
    }

    /**
     * @param button The button for storing the checked state.
     * @param isSelected The default selected state.
     */
    public void next(Button button, boolean isSelected) {
        if (m_nextCounter == m_storedSelectionDatas.size()) {
            m_storedSelectionDatas.add(new SelectionState(isSelected));
        }
        boolean isSelectedNew = m_storedSelectionDatas.get(m_nextCounter)
                .isSelected();
        m_newSelectionDatas.add(new SelectionState(isSelectedNew));
        m_buttons.put(button, m_nextCounter);
        button.setSelection(isSelectedNew);
        button.addSelectionListener(this);
        m_nextCounter++;
    }

    /**
     * Reset the current selected states to the last stored state.
     */
    public void reset() {
        m_nextCounter = 0;
        m_buttons.clear();
        m_newSelectionDatas.clear();
    }

    /**
     * Store the checked state of all buttons and then call {@link #reset()}.
     */
    public void store() {
        // switch list of stored and new selection to store the new list
        List<SelectionState> tmp = m_storedSelectionDatas;
        m_storedSelectionDatas = m_newSelectionDatas;
        m_newSelectionDatas = tmp;
        reset();
    }

    /**
     * {@inheritDoc}
     */
    public void widgetSelected(SelectionEvent e) {
        Button button = (Button) e.getSource();
        Integer i = m_buttons.get(button);
        if (i != null) {
            m_newSelectionDatas.get(i).setSelected(button.getSelection());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        // do nothing
    }

}
