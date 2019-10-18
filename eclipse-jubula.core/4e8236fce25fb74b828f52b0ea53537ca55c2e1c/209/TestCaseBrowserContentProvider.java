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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 08.10.2004
 */
public class TestCaseBrowserContentProvider extends BrowserContentProvider {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(TestCaseBrowserContentProvider.class);
    
    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof INodePO[]) {
            return (Object[])parentElement;
        }

        if (parentElement instanceof ICategoryPO
                && ((ICategoryPO)parentElement).isSpecObjCont()) {
            ICategoryPO specObj = (ICategoryPO) parentElement;
            List<Object> elements = new ArrayList<Object>();
            elements.addAll(specObj.getUnmodifiableNodeList());
            IProjectPO activeProject = 
                    GeneralStorage.getInstance().getProject();
            if (activeProject != null) {
                elements.addAll(activeProject.getUsedProjects());
            } else {
                LOG.error(Messages.TestCaseBrowser_NoActiveProject);
            }
            return elements.toArray();
        }
        
        if (parentElement instanceof IExecTestCasePO) {
            ISpecTestCasePO referencedTestCase = 
                ((IExecTestCasePO)parentElement).getSpecTestCase();
            if (referencedTestCase != null) {
                List<INodePO> displayedChildren = getChildrenToDisplay(
                        referencedTestCase);
                return ArrayUtils.addAll(
                        Collections.unmodifiableCollection(
                                referencedTestCase.getAllEventEventExecTC())
                                .toArray(), displayedChildren.toArray());
            }
            
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }

        if (parentElement instanceof INodePO) {
            INodePO parentNode = ((INodePO) parentElement);
            List<INodePO> displayedChildren = getChildrenToDisplay(parentNode);
            Object[] children = displayedChildren.toArray();
            if (parentElement instanceof ISpecTestCasePO) {
                children = ArrayUtils.addAll(
                        Collections.unmodifiableCollection(
                                ((ISpecTestCasePO) parentElement)
                                        .getAllEventEventExecTC()).toArray(),
                        children);
            }
            return children;
        }
        
        if (parentElement instanceof IReusedProjectPO) {
            try {
                IProjectPO reusedProject = 
                    ProjectPM.loadReusedProjectInMasterSession(
                            (IReusedProjectPO)parentElement);

                if (reusedProject != null) {
                    return reusedProject.getUnmodSpecList().toArray();
                }

                return ArrayUtils.EMPTY_OBJECT_ARRAY;
            } catch (JBException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
                return ArrayUtils.EMPTY_OBJECT_ARRAY;
            }
        }
        
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    } 
}