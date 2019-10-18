/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.aut.dvdtool;

/**
 * @author al
 *
 * The class maintains an artificial development state which is used to
 * trigger certain behaviors which simulated development states.
 */
public final class DevelopmentState {
    /**Symbolic name for development state */
    public static final int V0 = 0;
    /**Symbolic name for development state */
    public static final int V1 = 1;
    /**Symbolic name for development state */
    public static final int V2 = 2;
    /**Symbolic name for development state */
    public static final int V3 = 3;
    
    /** Singleton instance variable */
    private static DevelopmentState instance;
    /** simulated development state */
    private int m_state;

    /**
     * private constructor required by the Singleton pattern.
     */
    private DevelopmentState() {
        m_state = V0;
    }
    
    /**
     * Singleton
     * @return The only instance of {@link DevelopmentState}
     */
    public static DevelopmentState instance() {
        if (instance == null) {
            instance = new DevelopmentState();         
        }
        return instance;
    }

    /**
     * @return the m_state
     */
    public int getState() {
        return m_state;
    }

    /**
     * @param state the m_state to set
     */
    public void setState(int state) {
        m_state = state;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV0() {
        return m_state == V0;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV1() {
        return m_state == V1;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV2() {
        return m_state == V2;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV3() {
        return m_state == V3;
    }
    
}
