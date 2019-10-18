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
package org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors;

import org.eclipse.jubula.client.core.utils.IParamValueValidator;

/**
 * interface for desciptors with data validator for parameter values
 *
 * @author BREDEX GmbH
 * @created 27.11.2007
 */
public interface IVerifiable {

    /**
     * @return Returns the validator.
     */
    public abstract IParamValueValidator getDataValidator();

}