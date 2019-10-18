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

import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.swt.widgets.Composite;

/**
 * @author BREDEX GmbH
 * @created 02.02.2006
 * @param <TheObject> Type of object to display
 * 
 */
public abstract class AbstractI18nCombo<TheObject> extends
    I18nDirectCombo<TheObject> {

    /**
     * Build a key to be used as a part of an I18N key
     */
    public static interface IKeyMaker {
        /**
         * Make a key from an Object
         * @param o use this to make the key
         * @return a string to be used as part of an I18N key
         */
        public String makeKey(Object o);
    }
    
    // setup information
    /** The base of the translation */
    private String m_baseKey;
    /** make a key string from the key object */
    private IKeyMaker m_keyMaker;
    
    /**
     * A Combo which supports I18N
     * 
     * {@inheritDoc}
     * @param parent see Combo
     * @param style see Combo
     * @param baseKey
     *            The base of the translation
     * @param keys
     *            Single keys for translation
     * @param keyMaker
     *            make a key string from the key object
     * @param isNullSelectionAllowed
     *            true if the Combo should start with an empty entry to support
     *            null selection
     * @param comparator Sorting criteria for display values. <code>null</code>
     * is allowed. In this case no sorting is done.
     */
    public AbstractI18nCombo(Composite parent, int style, String baseKey,
        List<TheObject> keys, IKeyMaker keyMaker,
        boolean isNullSelectionAllowed, Comparator<String> comparator) {
        super(parent, style, keys, buildI18nKeys(baseKey, keys, keyMaker),
            isNullSelectionAllowed, comparator);
        m_baseKey = baseKey;
        m_keyMaker = keyMaker;
    }
    /**
     * A Combo which supports I18N
     * @param parent see Combo
     * @param style see Combo
     * @param baseKey The base of the translation
     * @param keys Single keys for translation
     * @param keyMaker make a key string from the key object
     * @param isNullSelectionAllowed true if the Combo should start with an empty entry to support null selection
     * @param sortEntries Sort the display values by the standard String compareTo() method.
     */
    public AbstractI18nCombo(Composite parent, int style, String baseKey,
        List<TheObject> keys, IKeyMaker keyMaker,
        boolean isNullSelectionAllowed, boolean sortEntries) {
        this(parent, style, baseKey, keys, keyMaker, isNullSelectionAllowed,
            sortEntries ? new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            } : null);
    }

    /**
     * {@inheritDoc}
     * @param keys see constructor for details
     */
    public void setItems(List<TheObject> keys) {
        super.setItems(keys, buildI18nKeys(m_baseKey, keys,
            m_keyMaker));
    }

    
    /**
     * This method does nothing for this subclass and must not be called. If
     * called it throws an IllegalStateException.
     * {@inheritDoc}
     */
    @Deprecated
    public void setItems(List<TheObject> values, List<String> i18nKeys) {
        throw new IllegalStateException(
            Messages.SetItemsNotValidForThisSubclass);        
    }
    /**
     * {@inheritDoc}
     */
    protected void checkSubclass() {
        // do nothing, therefor allowing subclassing 
    }
    
    /**
     * Build the complete i18n keys, using parts from the values
     * @param baseKey for i18n
     * @param keys List of entries
     * @param keyMaker specifies how to make a key string from the key object
     * @return a List of keys 
     */
    private static List<String> buildI18nKeys(String baseKey,
        List keys, IKeyMaker keyMaker) {
        List<String> result = new ArrayList<String>(keys.size());
        String startOfKey = baseKey + '.';
        for (Object key : keys) {
            result.add(startOfKey + keyMaker.makeKey(key));
        }
        return result;
    }
}
