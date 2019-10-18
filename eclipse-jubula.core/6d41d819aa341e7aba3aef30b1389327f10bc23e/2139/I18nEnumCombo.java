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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.swt.widgets.Composite;

/**
 * @author BREDEX GmbH
 * @created 02.02.2006
 * @param <E> which enum is supported
 */
public class I18nEnumCombo <E extends Enum> extends AbstractI18nCombo<E> {

    /** fixed parameter for super class constructor */
    private static final IKeyMaker KEYMAKER = new IKeyMaker() {
        public String makeKey(Object o) {
            return ((Enum)o).name();
        }
    };
    
    /**
     * @param parent parent
     * @param style style
     * @param baseKey baseKey
     * @param enumClass enumClass
     * @param isNullSelectionAllowed isNullSelectionAllowed
     * @param sortEntries sortEntries
     */
    public I18nEnumCombo(Composite parent, int style, String baseKey,
        Class<E> enumClass, boolean isNullSelectionAllowed, 
        boolean sortEntries) {
        super(parent, style, baseKey, Arrays.asList(enumClass
            .getEnumConstants()), KEYMAKER, isNullSelectionAllowed, 
            sortEntries);
    }
    /**
     * This method does nothing for this subclass and must not be called. If
     * called it throws an IllegalStateException.
     * {@inheritDoc}
     */
    @Deprecated
    public void setItems(List keys) {
        throw new IllegalStateException(
            Messages.SetItemsNotValidForThisSubclass);        
    }
}
