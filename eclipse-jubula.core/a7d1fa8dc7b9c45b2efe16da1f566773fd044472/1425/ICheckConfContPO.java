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
public interface ICheckConfContPO extends IPersistentObject {
    
    /**
     * 
     * @param chkId
     *            The check which has the configuration.
     * @param cfg
     *            The configuration to the check.
     */
    void addCheckConf(String chkId, ICheckConfPO cfg);
    
    /**
     * 
     * @param chkId
     *            The id where the configuration should be searched.
     * @return The configuration of the given id or if the id doesn't exist it
     *         returns <code>null</code>.
     */
    CheckConfPO getCheckConf(String chkId);

    /**
     * @return a new configuration for a check
     */
    ICheckConfPO createCheckConf();
    
    /**
     * @return the confMap
     */
    Map<String, CheckConfPO> getConfMap();
    
    /**
     * @return true, if teststyle is enabled for this project
     */
    boolean getEnabled();
    
    /**    
     * @param enabled the new enablement for this project
     */
    void setEnabled(boolean enabled);
}
