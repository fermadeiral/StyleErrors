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
package org.eclipse.jubula.client;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Registry for ONE, already running and connected <code>AUT</code> instance
 *
 * @author BREDEX GmbH
 * @since 3.2
 */
public enum AUTRegistry {
    /** Singleton */
    INSTANCE;
    /** the AUT */
    private AUT m_aut;

    /**
     * register the AUT
     * 
     * @param aut
     *            the AUT which should be registered
     * @throws IllegalArgumentException
     *             - if AUT is not already Connected
     */
    public void register(AUT aut) {
        Validate.isTrue(aut.isConnected());
        unregister();
        m_aut = aut;
    }

    /**
     * Unregister the currently registered AUT
     */
    public void unregister() {
        if (m_aut != null) {
            m_aut = null;
        }
    }

    /**
     * @return the currently registered AUT
     */
    @Nullable
    public AUT get() {
        return m_aut;
    }
}