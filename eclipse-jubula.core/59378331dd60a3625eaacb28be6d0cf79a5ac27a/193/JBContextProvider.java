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
package org.eclipse.jubula.client.ui.rcp.provider.contextprovider;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;

/**
 * ContextProvider for the context sensitive help in Jubula
 *
 * @author BREDEX GmbH
 * @created 13.06.2006
 */
public class JBContextProvider implements IContextProvider {
    
   /**
     * {@inheritDoc}
     * @return
     */
    public int getContextChangeMask() {
        return NONE;
    }

    /**
     * {@inheritDoc}
     * @param target here: the contextID
     * @return the context of the helpID
     */
    public IContext getContext(Object target) {
        if (target != null) {
            return HelpSystem.getContext(target.toString());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param target
     * @return
     */
    public String getSearchExpression(Object target) {
        return null;
    }
}