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
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * @author BREDEX GmbH
 * @created 08.10.2004
 */
public class TestCaseTreeCompositeContentProvider 
    extends AbstractTreeViewContentProvider {

    /** Whether to show only categories */
    private boolean m_onlyCategories = false;

    /** Whether to show reused projects */
    private boolean m_showReusedProjects = true;

    /**
     * Constructor
     * @param reuseds whether to show reused projects
     * @param categories whether to only show categories
     */
    public TestCaseTreeCompositeContentProvider(boolean reuseds,
            boolean categories) {
        m_showReusedProjects = reuseds;
        m_onlyCategories = categories;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IProjectPO) {
            IProjectPO project = (IProjectPO)parentElement;
            List<Object> elements = new ArrayList<Object>();
            if (m_onlyCategories) {
                elements.add(((IProjectPO) parentElement).getSpecObjCont());
            } else {
                for (INodePO child : project.getUnmodSpecList()) {
                    elements.add(child);
                }
            }
            if (m_showReusedProjects) {
                elements.addAll(project.getUsedProjects());
            }
            return elements.toArray();
        }
        
        if (parentElement instanceof ICategoryPO) {
            List<INodePO> allChildren = ((ICategoryPO)parentElement)
                    .getUnmodifiableNodeList(); 
            if (!m_onlyCategories) {
                return allChildren.toArray();
            }
            List<INodePO> onlyCategories = new ArrayList<>();
            for (INodePO child : allChildren) {
                if (child instanceof ICategoryPO) {
                    onlyCategories.add(child);
                }
            }
            return onlyCategories.toArray();
        }
        
        if (parentElement instanceof IReusedProjectPO) {
            try {
                IProjectPO reusedProject = 
                    ProjectPM.loadReusedProjectInMasterSession(
                            (IReusedProjectPO)parentElement);

                if (reusedProject == null) {
                    return ArrayUtils.EMPTY_OBJECT_ARRAY;
                }

                List<Object> res = new ArrayList<>();
                for (INodePO spec : reusedProject.getUnmodSpecList()) {
                    if (!m_onlyCategories || (spec instanceof ICategoryPO)) {
                        res.add(spec);
                    }
                }
                return res.toArray();                
            } catch (JBException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            }
        }
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
}
