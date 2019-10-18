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
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;


/**
 * @author BREDEX GmbH
 * @created Mar 17, 2010
 */
public class CentralTestDataContentProvider extends
        AbstractTreeViewContentProvider {

    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ITestDataCategoryPO) {
            ITestDataCategoryPO category = (ITestDataCategoryPO)parentElement;
            List<Object> childList = new ArrayList<Object>();
            childList.addAll(category.getCategoryChildren());
            childList.addAll(category.getTestDataChildren());
            return childList.toArray();
        }
        
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }
}
