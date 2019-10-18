/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

/**
 * A token capable of containing other tokens.
 */
public interface INestableParamValueToken extends IParamValueToken {

    /**
     * 
     * @return the contained tokens.
     */
    public IParamValueToken[] getNestedTokens();
    
}
