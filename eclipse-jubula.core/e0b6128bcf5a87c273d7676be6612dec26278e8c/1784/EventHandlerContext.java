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
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;


/**
 * @author marcell
 * @created Dec 3, 2010
 */
public class EventHandlerContext extends BaseContext {

    /**
     * 
     */
    public EventHandlerContext() {
        super(IEventExecTestCasePO.class);
    }

    @Override
    public List<Object> getAll() {
        List<Object> tmp = new ArrayList<Object>();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        for (INodePO p : project.getUnmodSpecList()) {
            tmp.addAll(getEventHandler(p));
        }
        return tmp;
    }

    /**
     * Recursive method to get all spec test cases from a node like a category.
     * Will be called recursively because some categories have categories on
     * their own that must be searched for test cases.
     * 
     * @param root
     *            The node that will be checked with its children.
     * @return the list of the TestCases of this node.
     */
    private List<Object> getEventHandler(Object root) {
        List<Object> tmp = new ArrayList<Object>();
        if (root instanceof ISpecTestCasePO) {
            tmp.addAll(handleSpecTestCase((ISpecTestCasePO)root));
        } else if (root instanceof ICategoryPO) {
            ICategoryPO cat = (ICategoryPO)root;
            for (Object o : cat.getUnmodifiableNodeList()) {
                tmp.addAll(getEventHandler(o));
            }
        }
        return tmp;
    }

    /**
     * @param spec
     *            The spec test case which will return the exec test cases.
     * @return An list with all the exec test cases of this spec test case
     */
    private List<Object> handleSpecTestCase(ISpecTestCasePO spec) {
        List<Object> tmp = new ArrayList<Object>();
        for (Object obj : spec.getAllEventEventExecTC()) {
            IEventExecTestCasePO event = (IEventExecTestCasePO)obj;
            if (!isFromReusedProject(event)) {
                tmp.add(obj);
            }
        }
        return tmp;
    }
    
    /**
     * @param exec The exec that will be checked
     * @return True, if the exec test case is from a reusde
     */
    private boolean isFromReusedProject(IEventExecTestCasePO exec) {
        if (null == exec.getSpecTestCase()) {
            return true;
        }
        long projId = GeneralStorage.getInstance().getProject().getId();
        return projId != exec.getSpecTestCase().getParentProjectId();
    }

    @Override
    public String getName() {
        return Messages.ContextEventHandlerName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextEventHandlerDescription;
    }

}
