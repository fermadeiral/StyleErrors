/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.client.teststyle.checks.contexts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.db.TestJobBP;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;

/**
 * @author BREDEX GmbH
 *
 */
public class CommentContext extends BaseContext {

    /**
     * the class the context is for
     */
    public CommentContext() {
        super(ICommentPO.class);
    }
    @Override
    public String getName() {
        return Messages.ContextCommentName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextCommentDescription;
    }

    @Override
    public List<? extends Object> getAll() {
        List<Object> tmp = new ArrayList<Object>();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        for (INodePO p : project.getUnmodSpecList()) {
            tmp.addAll(getComments(p));
        }
        for (ITestSuitePO p : TestSuiteBP.getListOfTestSuites(project)) {
            tmp.addAll(getComments(p));
        }
        for (ITestJobPO p : TestJobBP.getListOfTestJobs(project)) {
            tmp.addAll(getComments(p));
        }
        return tmp;
    }
    
    /**
     * Recursive method to get all {@link ICommentPO} nodes from a node like a category.
     * Will be called recursively because some categories have categories on
     * their own that must be searched for test cases.
     * 
     * @param root
     *            The node that will be checked with its children.
     * @return the list of the comments of this node.
     */
    private List<Object> getComments(Object root) {
        List<Object> tmp = new ArrayList<Object>();
        if (root instanceof ICommentPO) {
            tmp.add(root);
        } else if (root instanceof INodePO) {
            INodePO node = (INodePO) root;
            for (Object o : node.getUnmodifiableNodeList()) {
                tmp.addAll(getComments(o));
            }
        }
        return tmp;
    }

}
