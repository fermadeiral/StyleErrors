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

import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;



/**
 * @author marcell
 * @created Nov 10, 2010
 */
public class CategoryContext extends BaseContext {

    /**
     * @param cls
     */
    public CategoryContext() {
        super(ICategoryPO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getAll() {
        List<Object> tmp = new ArrayList<Object>();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        for (INodePO p : project.getUnmodSpecList()) {
            tmp.addAll(getCategories(p));
        }
        return tmp;
    }

    /**
     * Recursive method to get all categories from a node like a category. Will
     * be called recursively because some categories have categories on their
     * own that must be searched for test cases.
     * 
     * @param obj
     *            The node that will be checked.
     * @return the list of the TestCases of this node.
     */
    private List<Object> getCategories(Object obj) {
        List<Object> tmp = new ArrayList<Object>();
        if (obj instanceof ICategoryPO) {
            tmp.add(obj);
        } 
        if (obj instanceof ICategoryPO) {
            for (Object o : ((ICategoryPO) obj).getUnmodifiableNodeList()) {
                tmp.addAll(getCategories(o));
            }
        }
        return tmp;
    }

    @Override
    public String getName() {
        return Messages.ContextCategoryName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextCategoryDescription;
    }

}
