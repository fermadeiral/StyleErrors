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

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;

/**
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public class TestCaseEditorContentProvider 
    extends AbstractNodeTreeContentProvider {
    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof INodePO[]) {
            return new Object[] { ((INodePO[])parentElement)[0] };
        }
        if (!(parentElement instanceof IExecTestCasePO)
                && parentElement instanceof INodePO
                && ((INodePO) parentElement).getNodeListSize() > 0) {
            return ((INodePO) parentElement).getUnmodifiableNodeList().
                    toArray();
        }
        
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    /**
     * {@inheritDoc}
     */
    public Object getParent(Object element) {
        if (element instanceof INodePO
                && !(element instanceof ISpecTestCasePO)) {
            return ((INodePO)element).getParentNode();
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        if (element instanceof IExecTestCasePO) {
            // ExecTCs cheat the getNodeListSize()...
            return false;
        }
        if (element instanceof INodePO) {
            return ((INodePO)element).getNodeListSize() > 0;
        }
        return false;
    }   
}