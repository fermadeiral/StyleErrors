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
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;


/**
 * @author BREDEX GmbH
 * @created Mar 17, 2010
 */
public class TestJobEditorContentProvider extends
    AbstractNodeTreeContentProvider {
    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ITestJobPO) {
            return ((ITestJobPO)parentElement)
                .getUnmodifiableNodeList().toArray();
        }

        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    /**
     * {@inheritDoc}
     */
    public Object getParent(Object element) {
        if (element instanceof IRefTestSuitePO) {
            return ((IRefTestSuitePO)element).getParentNode();
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        if (element instanceof ITestJobPO) {
            return ((ITestJobPO)element).getNodeListSize() > 0;
        }
        return false;
    }
}
