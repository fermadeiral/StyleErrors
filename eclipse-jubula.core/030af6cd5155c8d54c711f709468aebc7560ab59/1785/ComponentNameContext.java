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
package org.eclipse.jubula.client.teststyle.checks.contexts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.teststyle.i18n.Messages;

/**
 * @author marcell
 * @created Nov 10, 2010
 */
public class ComponentNameContext extends BaseContext {

    /**
     * @param cls
     */
    public ComponentNameContext() {
        super(IComponentNamePO.class);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> getAll() {
        List<Object> cn = new ArrayList<Object>(CompNameManager.getInstance()
                .getAllCompNamePOs());
        return cn;
    }

    @Override
    public String getName() {
        return Messages.ContextComponentName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextComponentDescription;
    }

}
