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

import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;


/**
 * Contains all necessary information of a component for toolkit info generation
 * @author BREDEX GmbH
 */
public class CompInfoForToolkitGen {
    
    /** the most specific visible super type of a component */
    private ComponentClass m_componentClass;

    /** the most specific visible super type of a component */
    private String m_testerClass;
    
    /**
     * Contains all necessary information of a component for toolkit info generation
     * @param componentClass the component class
     * @param testerClass the tester class
     */
    public CompInfoForToolkitGen(ComponentClass componentClass,
            String testerClass) {
        m_componentClass = componentClass;
        m_testerClass = testerClass;
    }

    /** 
     * Returns the tester class
     * @return the tester class
     */
    public String getTesterClass() {
        return m_testerClass;
    }
    
    /** 
     * Returns the component class
     * @return the component class
     */
    public ComponentClass getComponentClass() {
        return m_componentClass;
    }
}
