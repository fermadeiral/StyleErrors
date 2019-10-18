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
package org.eclipse.jubula.client.core.model;

import java.util.Map;

/**
 * @author BREDEX GmbH
 * @created Nov 17, 2010
 */
public interface ICheckConfPO extends IPersistentObject {
    
    /**
     * @return The severity in the ordinal value. To get the correct one used 
     * for my class please use 
     */
    public String getSeverity();
    
    /**
     * @param sev
     *            Sets the severity ordinal (there should be no dependency on my
     *            plugin in the end, so the enum that represents the severity
     *            can't be used)
     */
    public void setSeverity(String sev);
    
    /**
     * @return the active
     */
    public Boolean isActive();
    
    /**
     * @param active the active to set
     */
    public void setActive(Boolean active);
    
    /**
     * @return the attributes
     */
    public Map<String, String> getAttr();
    
    /**
     * @param attributes the attributes to set
     */
    public void setAttr(Map<String, String> attributes);
    
    /**
     * @return the contexts
     */
    public Map<String, Boolean> getContexts();
    
    /**
     * @param contexts the contexts to set
     */
    public void setContexts(Map<String, Boolean> contexts);
}
