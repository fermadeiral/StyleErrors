/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.common.utils;

/**
 * A register for a implementation of {@link IAUTStartHelper}
 * @author BREDEX GmbH
 */
public enum AutStartHelperRegister {
    /** the instance*/
    INSTANCE;
    
    /** the {@link IAUTStartHelper} user for starting auts */
    private IAUTStartHelper m_autStartHelper = null;
    
    /** Util class*/
    private AutStartHelperRegister() {
    }

    /**
     * @return the current {@link IAUTStartHelper} implementation
     */
    public IAUTStartHelper getAutStartHelper() {
        return m_autStartHelper;
    }

    /**
     * @param autStartHelper the new {@link IAUTStartHelper} implementation
     */
    public void setAutStartHelper(IAUTStartHelper autStartHelper) {
        m_autStartHelper = autStartHelper;
    }

}
