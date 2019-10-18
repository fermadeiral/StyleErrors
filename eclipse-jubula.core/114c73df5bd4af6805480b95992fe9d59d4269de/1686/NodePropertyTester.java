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
package org.eclipse.jubula.client.core.propertytester;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;


/**
 * PropertyTester for INodePO objects.
 *
 * @author BREDEX GmbH
 * @created Jan 13, 2009
 */
public class NodePropertyTester extends AbstractBooleanPropertyTester {
    /** the id of the "isEditable" property */
    public static final String EDITABLE_PROP = "isEditable"; //$NON-NLS-1$
    /** the id of the "isRootNode" property */
    public static final String ROOT_CONT_PROP = "isRootContainer"; //$NON-NLS-1$
    /** the id of the "hasTaskId" property */
    public static final String HAS_TASK_ID_PROP = "hasTaskId"; //$NON-NLS-1$
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { EDITABLE_PROP,
        HAS_TASK_ID_PROP, ROOT_CONT_PROP};
    
    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        IPersistentObject po = (IPersistentObject)receiver;
        if (property.equals(EDITABLE_PROP)) {
            return testIsEditable(po);
        } else if (property.equals(HAS_TASK_ID_PROP)) {
            return hasTaskIdSet(po);
        } else if (property.equals(ROOT_CONT_PROP)) {
            return testIsRootCont(po);
        }
        return false;
    }

    /**
     * @param node The node for which to check the editabilty.
     * @return the results of <code>guiNode.isEditable()</code>.
     */
    private boolean testIsEditable(IPersistentObject node) {
        if (node instanceof IObjectMappingCategoryPO) {
            return true;
        }
        return NodeBP.isEditable(node);
    }

    /**
     * @param node the node to test
     * @return whether the node is a Project's Spec / Exec container
     */
    private boolean testIsRootCont(IPersistentObject node) {
        return node instanceof INodePO
                && (((INodePO) node).isSpecObjCont()
                        || ((INodePO) node).isExecObjCont());
    }
    
    /**
     * @param po
     *            The node for which to check the task id.
     * @return whether a task id is set or not
     */
    public static boolean hasTaskIdSet(IPersistentObject po) {
        if (po instanceof INodePO) {
            INodePO node = (INodePO) po;
            return StringUtils.isNotEmpty(getTaskIdforNode(node));
        }
        return false;
    }
    
    /**
     * @param node
     *            the node to retrieve the task id for; may be <code>null</code>
     * @return the taskId for the given node or <code>null</code> if not set /
     *         found
     */
    public static String getTaskIdforNode(INodePO node) {
        if (node == null) {
            return null;
        }
        String taskId = node.getTaskId();
        if (node instanceof IRefTestSuitePO) {
            IRefTestSuitePO refTS = (IRefTestSuitePO) node;
            taskId = refTS.getTestSuite().getTaskId();
        } else if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTC = (IExecTestCasePO) node;
            ISpecTestCasePO specTestCase = execTC.getSpecTestCase();
            if (specTestCase != null) {
                taskId = specTestCase.getTaskId();
            }
        }
        return taskId;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return IPersistentObject.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
