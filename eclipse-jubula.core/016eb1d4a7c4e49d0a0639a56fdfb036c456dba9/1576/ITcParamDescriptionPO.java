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

import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;

/**
 * @author BREDEX GmbH
 * @created 29.06.2007
 */
public interface ITcParamDescriptionPO extends IParamDescriptionPO {
    /**
     * @param mapper associates ParamNameMapper
     * important for resolving of parameter names 
     */
    public void setParamNameMapper(IParamNameMapper mapper);
    
    /**
     * @return {@link IParamValueSetPO} for Value Sets and default value
     */
    public IParamValueSetPO getValueSet();
    
    /**
     * @param valueSet {@link IParamValueSetPO} for Value Sets and default value
     */
    public void setValueSet(IParamValueSetPO valueSet);
}
