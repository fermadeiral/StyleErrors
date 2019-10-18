/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.api.gen.internal.genmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all necessary information for factory generation
 * @author BREDEX GmbH
 * @created 17.10.2014
 */
public class FactoryGenInfo {
    
    /** name of the component */
    private String m_toolkitInfoName;
    
    /** name of the component */
    private String m_toolkitInfoFqName;

    /** list with component infos */
    private List<CompInfoForFactoryGen> m_compInfoList =
            new ArrayList<CompInfoForFactoryGen>();
    
    /**
     * @return list with component infos
     */
    public List<CompInfoForFactoryGen> getCompInformation() {
        return m_compInfoList;
    }
    
    /**
     * adds a component information to the list
     * @param compInfo the comp info
     */
    public void addCompInformation (CompInfoForFactoryGen compInfo) {
        m_compInfoList.add(compInfo);
    }
    
    /**
     * @return the name of the toolkit information class
     */
    public String getToolkitInfoName() {
        return m_toolkitInfoName;
    }
    
    /**
     * @return the fully qualified name of the toolkit information class
     */
    public String getToolkitInfoFqName() {
        return m_toolkitInfoFqName;
    }
    
    /**
     * @param tkInfoName name of the toolkit information class
     * @param tkInfoFqName fully qualified name of the toolkit information class
     */
    public void setToolkitInfoName(String tkInfoName,
            String tkInfoFqName) {
        m_toolkitInfoName = tkInfoName;
        m_toolkitInfoFqName = tkInfoFqName;
    }
}
