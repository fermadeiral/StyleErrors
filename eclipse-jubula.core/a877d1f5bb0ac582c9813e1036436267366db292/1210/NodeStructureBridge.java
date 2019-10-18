/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.bridge.bridge;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class NodeStructureBridge extends AbstractContextStructureBridge {
    /**
     * the content type
     */
    public static final String CONTENT_TYPE = "org.eclipse.jubula.client.alm.mylyn.ui.bridge.content.type.node"; //$NON-NLS-1$
    
    /**
     * <code>logger</code>
     */
    private static Logger log = LoggerFactory.getLogger(
            NodeStructureBridge.class);

    /**
     * Constructor
     */
    public NodeStructureBridge() {
    // default constructor
    }

    /** {@inheritDoc} */
    public boolean acceptsObject(Object object) {
        Boolean accepted = false;
        if (object instanceof SearchResultElement<?>) {
            accepted = true;
        } else if (object instanceof INodePO) {
            INodePO thisNode = (INodePO) object;
            if (thisNode.isExecObjCont() || thisNode.isExecObjCont()) {
                return true;
            }
            accepted = true;
            INodePO node = getRoot(thisNode);
            String name = node.getName();
            if (name == null && log.isWarnEnabled()) {
                log.warn("found INodePO with no name"); //$NON-NLS-1$
                accepted = false;
            }
        } else if (object instanceof IReusedProjectPO) {
            accepted = true;
        }
        return accepted;
    }

    /**
     * 
     * @param node
     *            - node
     * @return root
     */
    private INodePO getRoot(INodePO node) {
        if (node.getParentNode() != null) {
            return getRoot(node.getParentNode());
        }
        return node;
    }

    /** {@inheritDoc} */
    public boolean canBeLandmark(String handle) {
        Object element = getObjectForHandle(handle);
        return canFilter(element);
    }

    /** {@inheritDoc} */
    public boolean canFilter(Object element) {
        if (element instanceof INodePO) {
            INodePO node = (INodePO) element;
            if (node.isExecObjCont() || node.isSpecObjCont()) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    public List<String> getChildHandles(String handle) {
        List<String> childHandles = new ArrayList<String>();
        Object objForHandle = getObjectForHandle(handle);
        if (objForHandle instanceof INodePO) {
            INodePO node = (INodePO)objForHandle;
            for (INodePO child : node.getUnmodifiableNodeList()) {
                childHandles.add(getHandleIdentifier(child));
            }
        } else if (objForHandle instanceof IReusedProjectPO) {
            try {
                IProjectPO reusedProject = ProjectPM
                        .loadReusedProjectInMasterSession(
                                (IReusedProjectPO) objForHandle);

                if (reusedProject != null) {
                    for (INodePO specs : reusedProject
                            .getUnmodSpecList()) {
                        childHandles.add(specs.getGuid());
                    }
                }
            } catch (JBException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            }
        }
        
        return childHandles;
    }

    /** {@inheritDoc} */
    public String getContentType() {
        return CONTENT_TYPE;
    }

    /** {@inheritDoc} */
    public String getContentType(String elementHandle) {
        return getContentType();
    }

    /** {@inheritDoc} */
    public String getHandleForOffsetInObject(Object resource, int offset) {
        return null;
    }

    /** {@inheritDoc} */
    public String getHandleIdentifier(Object object) {
        if (object instanceof INodePO) {
            return ((INodePO)object).getGuid();
        } else if (object instanceof IReusedProjectPO) {
            return ((IReusedProjectPO)object).getProjectGuid();
        }
        return null;
    }

    /** {@inheritDoc} */
    public String getLabel(Object object) {
        if (object instanceof INodePO) {
            return ((INodePO)object).getName();
        } else if (object instanceof IReusedProjectPO) {
            return ((IReusedProjectPO)object).getName();
        }
        return StringConstants.EMPTY;
    }

    /** {@inheritDoc} */
    public Object getObjectForHandle(final String handle) {
        Object obj = null;
        if (handle != null) {
            IProjectPO activeProject = GeneralStorage.getInstance()
                    .getProject();
            if (activeProject != null) {
                if (activeProject.getGuid().equals(handle)) {
                    return activeProject;
                }
                obj = NodePM.getNode(activeProject.getId(), handle);
                if (obj == null) {
                    for (IReusedProjectPO rProj : activeProject
                            .getUsedProjects()) {
                        if (rProj.getProjectGuid().equals(handle)) {
                            obj = rProj;
                            break;
                        }

                        try {
                            Long rProjId = ProjectPM.findProjectId(
                                    rProj.getProjectGuid(), 
                                    rProj.getMajorNumber(), 
                                    rProj.getMinorNumber(),
                                    rProj.getMicroNumber(),
                                    rProj.getVersionQualifier());
                            if (rProjId != null) {
                                obj = NodePM.getNode(rProjId, handle);
                                if (obj != null) {
                                    break;
                                }
                            }
                        } catch (JBException e) {
                            // do nothing. an error is logged where the exception 
                            // is thrown.
                        }
                    }
                }
            }
        }
        
        return obj;
    }

    /** {@inheritDoc} */
    public String getParentHandle(String handle) {
        try {
            Object objForHandle = getObjectForHandle(handle);
            if (objForHandle instanceof INodePO) {
                INodePO node = (INodePO)objForHandle;
                if (node.getParentNode() != null) {
                    INodePO parent = node.getParentNode();
                    if (parent.isSpecObjCont()) {
                        // This works around the fact that each top-level node
                        // from a Reused Project has the Spec Obj Cont of the project
                        // as its parent.
                        // Since we actually want such nodes to have the Reused 
                        // Project as parent in this case, the fix is to 
                        // return the GUID of the corresponding reused Project.
                        IProjectPO activeProject = 
                                GeneralStorage.getInstance().getProject();
                        Long parentProjectId = node.getParentProjectId();
                        if (activeProject != null 
                                && parentProjectId != activeProject.getId()) {
                            return ProjectPM.getGuidOfProjectId(
                                    parentProjectId);
                        }
                    }
                    return getHandleIdentifier(parent);
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while retrieving parent handle.", e); //$NON-NLS-1$
        }
        return null;
    }

    /** {@inheritDoc} */
    public boolean isDocument(String handle) {
        return true;
    }

    /**
     * @return parent content type
     */
    public String getParentContentType() {
        return CONTENT_TYPE;
    }
}
