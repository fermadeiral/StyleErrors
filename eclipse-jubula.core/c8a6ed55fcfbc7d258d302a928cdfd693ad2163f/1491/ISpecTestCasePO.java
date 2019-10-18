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

/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface ISpecTestCasePO extends ITestCasePO,
    IEventHandlerContainer, IModifiableParameterInterfacePO {

    /** The discriminator value of SpecTestCasePOs */
    public static final String DISCRIMINATOR = "S"; //$NON-NLS-1$
    /** The simple class name of SpecTestCasePOs */
    public static final String SPEC_TC_CLASSNAME =
            SpecTestCasePO.class.getSimpleName();
    /**
     * @return the isInterfaceLocked
     */
    public Boolean isInterfaceLocked();
    
    /**
     * @param isInterfaceLocked the isInterfaceLocked to set
     */
    public void setInterfaceLocked(Boolean isInterfaceLocked);
}