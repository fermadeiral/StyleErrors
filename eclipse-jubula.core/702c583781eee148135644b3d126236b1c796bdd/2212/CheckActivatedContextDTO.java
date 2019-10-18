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
package org.eclipse.jubula.client.archive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class CheckActivatedContextDTO {
    /** */
    private String m_clazz;
    /** */
    private boolean m_active;
    
    /**
     * @return class
     */
    @JsonProperty("clazz")
    public String getClazz() {
        return m_clazz;
    }
    
    /**
     * @param clazz 
     */
    public void setClazz(String clazz) {
        this.m_clazz = clazz;
    }
    
    /**
     * @return active
     */
    @JsonProperty("active")
    public boolean isActive() {
        return m_active;
    }
    
    /**
     * @param active 
     */
    public void setActive(boolean active) {
        this.m_active = active;
    }
}