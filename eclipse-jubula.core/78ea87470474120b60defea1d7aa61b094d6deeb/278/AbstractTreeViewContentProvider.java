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
package org.eclipse.jubula.client.ui.rcp.provider.contentprovider;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 13.09.2005
 */
public abstract class AbstractTreeViewContentProvider extends
    AbstractNodeTreeContentProvider {
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AbstractTreeViewContentProvider.class);
    
    /** {@inheritDoc} */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }
    
    /** {@inheritDoc} */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }
    
    /** {@inheritDoc} */
    public Object getParent(Object element) {
        if (element instanceof INodePO) {
            // This bit of code is wrong for not correct in every case.
            // it is used e.g. for show specification
            INodePO node = ((INodePO)element);
            INodePO parent = node.getParentNode();
            Long nodeProjId = node.getParentProjectId();
            
            if (node.isExecObjCont() || node.isSpecObjCont()) {
                return GeneralStorage.getInstance().getProject();
            }
            
            IProjectPO activeProject = 
                GeneralStorage.getInstance().getProject();
            if (activeProject != null && nodeProjId != null 
                    && parent != null) {
                if (!nodeProjId.equals(activeProject.getId())
                        && parent.isSpecObjCont()) {
                    // Parent is a TCB_ROOT_NODE, but node is not from the
                    // current project. So it must be a reused project.
                    try {
                        String nodeProjGUID = ProjectPM
                                .getGuidOfProjectId(nodeProjId);
                        for (IReusedProjectPO reusedProject : activeProject
                                .getUsedProjects()) {
                            if (nodeProjGUID.equals(reusedProject
                                    .getProjectGuid())) {
                                return reusedProject;
                            }
                        }
                    } catch (JBException e) {
                        LOG.warn(
                                "Could not load referenced project information", e); //$NON-NLS-1$
                    }
                }
            }
            return parent;
        }
        if (element instanceof IReusedProjectPO) {
            return GeneralStorage.getInstance().getProject();
        }
        if (element instanceof ITestDataCategoryPO) {
            return ((ITestDataCategoryPO)element).getParent();
        }
        if (element instanceof ITestDataCubePO) {
            return ((ITestDataCubePO)element).getParent();
        }
        if (element instanceof SearchResultElement) {
            return null;
        }
        Assert.notReached(Messages.WrongTypeOfElement 
                + StringConstants.EXCLAMATION_MARK);
        return null;
    }

}