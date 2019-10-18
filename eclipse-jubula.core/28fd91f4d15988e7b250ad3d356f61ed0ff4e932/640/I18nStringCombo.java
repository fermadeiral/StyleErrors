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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.List;

import org.eclipse.jubula.client.ui.widgets.AbstractI18nCombo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author BREDEX GmbH
 * @created 02.02.2006
 */
public class I18nStringCombo extends AbstractI18nCombo<String> {
    
    /** fixed parameter for super class constructor */
    private static final IKeyMaker KEYMAKER = new IKeyMaker() {
        public String makeKey(Object o) {
            return o.toString();
        }
    };

    /**
     * @param parent parent
     * @param style style
     * @param baseKey baseKey
     * @param keys keys
     * @param isNullSelectionAllowed isNullSelectionAllowed
     * @param sortEntries sortEntries
     */
    public I18nStringCombo(Composite parent, int style, String baseKey,
        List<String> keys, boolean isNullSelectionAllowed, 
        boolean sortEntries) {
        super(parent, style, baseKey, keys, KEYMAKER, isNullSelectionAllowed, 
            sortEntries);
    }
}
