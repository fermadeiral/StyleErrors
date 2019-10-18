/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.progress;

/**
 * Registry for ONE <code>IProgressConsole</code> implementation
 *
 * @author BREDEX GmbH
 * @created 21.07.2015
 */
public enum ProgressConsoleRegistry {
    /** Singleton */
    INSTANCE;
    /** the progress console */
    private IProgressConsole m_pConsole;
    
    /** register an IProgressConsole implementation and if necessary deregister the existing 
     * 
     *  @param pc the progress console which should be registered
     */
    public void register(IProgressConsole pc) {
        deregister();
        m_pConsole = pc;
    }
    
    /**
     * Deregister the currently registered progress console
     */
    public void deregister() {
        if (m_pConsole != null) {
            m_pConsole.closeConsole();
            m_pConsole = null;
        } 
    }
    
    /**
     * 
     * @return the currently registered progress console
     */
    public IProgressConsole getConsole() {
        return m_pConsole;
    }
}
