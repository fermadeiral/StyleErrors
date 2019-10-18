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

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;



/**
 * @author Markus Tiede
 * @created Mar 09, 2011
 */
public class CapContext extends BaseContext {

    /**
     * @param cls
     */
    public CapContext() {
        super(ICapPO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getAll() {
        List<Object> tmp = new ArrayList<Object>();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        for (INodePO p : project.getUnmodSpecList()) {
            tmp.addAll(getCaps(p));
        }
        return tmp;
    }

    /**
     * Recursive method to get all cap from a node like a category. Will
     * be called recursively because some categories have categories on their
     * own that must be searched for test cases.
     * 
     * @param obj
     *            The node that will be checked.
     * @return the list of the CAPs of this node.
     */
    private List<Object> getCaps(Object obj) {
        List<Object> tmp = new ArrayList<Object>();
        if (obj instanceof ICapPO) {
            tmp.add(obj);
        } else if (obj instanceof INodePO) {
            INodePO node = (INodePO) obj;
            for (Object o : node.getUnmodifiableNodeList()) {
                tmp.addAll(getCaps(o));
            }
        }
        return tmp;
    }

    @Override
    public String getName() {
        return Messages.ContextCapName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextCapDescription;
    }

}
