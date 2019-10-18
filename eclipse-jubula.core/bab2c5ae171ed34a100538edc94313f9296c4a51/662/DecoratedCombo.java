/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.List;

import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Data class for storing the old and new specification Test Cases.
 *
 * @author BREDEX GmbH
 */
public class DecoratedCombo implements SelectionListener {

    /** The combo box for choosing the parameter name */
    private Combo m_combo;

    /** The warning decoration for this combo box. */
    private ControlDecorator m_warningDecoration;

    /**
     * A new combo box with the given items added to the given parent.
     * An empty item is added at the beginning of the list and is selected.
     * @param parent The parent composite.
     * @param items A list of items.
     * @param selectedIndex The index for default selection.
     * @param message The message to show in the bobble.
     */
    private DecoratedCombo(Composite parent, List<String> items,
            int selectedIndex, String message) {
        m_combo = new Combo(parent, SWT.READ_ONLY);
        m_warningDecoration = ControlDecorator.addWarningDecorator(
            m_combo, message);
        m_combo.addSelectionListener(this);
        items.add(0, ""); //$NON-NLS-1$
        m_combo.setItems(items.toArray(new String[] {}));
        m_combo.select(selectedIndex);
        updateVisibilityOfDecorator();
    }

    /**
     * Show the decorator, if the empty element is selected, otherwise
     * hide the decorator.
     */
    private void updateVisibilityOfDecorator() {
        m_warningDecoration.setVisible(m_combo.getSelectionIndex() == 0);
    }

    /**
     * A combo box with an attached warning decoration shown, if no element
     * is selected.
     * @param parent The parent composite.
     * @param items A list of items.
     * @param selectedIndex The index for default selection.
     * @param message The message to show in the warning bobble.
     * @return A new combo box with the given items added to the given parent.
     *         If the given items only contains one element, this element is selected
     *         automatically. In each case an empty item is added at the
     *         beginning of the list.
     */
    public static Combo create(Composite parent, List<String> items,
            int selectedIndex, String message) {
        DecoratedCombo decoratedCombo = new DecoratedCombo(
                parent, items, selectedIndex, message);
        return decoratedCombo.m_combo;
    }

    /**
     * Calls only {@link #updateVisibilityOfDecorator()}.
     * {@inheritDoc}
     */
    public void widgetSelected(SelectionEvent e) {
        updateVisibilityOfDecorator();
    }

    /**
     * Do nothing.
     * {@inheritDoc}
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        updateVisibilityOfDecorator();
    }

}
