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
package org.eclipse.jubula.client.ui.widgets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created 07.02.2006
 * 
 * @param <TheObject> the class which entities are the base for the set of choices
 */
public class I18nDirectCombo<TheObject> extends DirectCombo<TheObject> {
    
    /**
     * A Combo which supports I18N
     * 
     * {@inheritDoc}
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param values
     *            Object represented by combobox
     * @param i18nKeys           
     *            keys for translation
     * @param isNullSelectionAllowed
     *            true if the Combo should start with an empty entry to support
     *            null selection
     * @param comparator Sorting criteria for display values. <code>null</code>
     * is allowed. In this case no sorting is done.
     */
    public I18nDirectCombo(Composite parent, int style, List<TheObject> values,
        List<String> i18nKeys, boolean isNullSelectionAllowed,
        Comparator<String> comparator) {
        super(parent, style, values, translate(i18nKeys),
            isNullSelectionAllowed, comparator);        
    }

    /**
     * A Combo which supports I18N
     * 
     * {@inheritDoc}
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param values
     *            Object represented by combobox
     * @param i18nKeys           
     *            keys for translation
     * @param isNullSelectionAllowed
     *            true if the Combo should start with an empty entry to support
     *            null selection
     * @param sortEntries
     *            Sort the display values by the standard String compareTo()
     *            method.
     */
    public I18nDirectCombo(Composite parent, int style, List<TheObject> values,
        List<String> i18nKeys, boolean isNullSelectionAllowed,
        boolean sortEntries) {
        this(parent, style, values, i18nKeys, isNullSelectionAllowed,
            sortEntries ? new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            } : null);
    }
    
    /**
     * {@inheritDoc}
     * See the constructor for details
     */
    public void setItems(List<TheObject> values, List<String> i18nKeys) {
        super.setItems(values, translate(i18nKeys));
    }

    /**
     * Translate a List of i18n keys to their corresponding values
     * @param keys List of Keys
     * @return a List of values for the supplied keys
     */
    private static List<String> translate(List<String> keys) {
        List<String> t = new ArrayList<String>(keys.size());
        for (String key : keys) {
            t.add(I18n.getString(key));
        }
        return t;
    }


}
