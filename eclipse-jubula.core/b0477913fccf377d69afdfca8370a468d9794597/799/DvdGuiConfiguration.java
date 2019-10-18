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
package org.eclipse.jubula.examples.aut.dvdtool.model;

import java.io.Serializable;

/**
 * This is the model class for storage of gui configuration of dvd details.
 *
 * @author BREDEX GmbH
 * @created 28.02.2008
 */
public class DvdGuiConfiguration implements Serializable {
    
    /** tab placement defined by constant from JTabbedPane */
    private int m_tabPlacement = -1;
    
    /** radio button label placement defined by constant from AbstractButton */
    private int m_labelPlacement = -1;
    
    /**
     * public constructor 
     */
    public DvdGuiConfiguration() {
        // empty
    }
    
    /**
     * @return Returns the tab placement.
     */
    public int getTabPlacement() {
        return m_tabPlacement;
    }
    
    /**
     * @param tabPlacement The tab placement to set.
     */
    public void setTabPlacement(int tabPlacement) {
        m_tabPlacement = tabPlacement;
    }
    
    /**
     * @return Returns the radio button label placement.
     */
    public int getLabelPlacement() {
        return m_labelPlacement;
    }
    
    /**
     * @param labelPlacement The radio button label placement to set.
     */
    public void setLabelPlacement(int labelPlacement) {
        m_labelPlacement = labelPlacement;
    }
    
}
