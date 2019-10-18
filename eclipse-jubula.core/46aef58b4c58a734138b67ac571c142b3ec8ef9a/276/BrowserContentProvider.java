/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
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

import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.INodePO;

/**
 * @author BREDEX GmbH
 * @created 29.10.2015
 */
abstract class BrowserContentProvider 
    extends AbstractTreeViewContentProvider {

    /**
     * @param parent node
     * @return all children of a test case which should be displayed
     */
    List<INodePO> getChildrenToDisplay(INodePO parent) {
        List<INodePO> allChildren = parent
                .getUnmodifiableNodeList();
        List<INodePO> children = new ArrayList<INodePO>();
        for (INodePO child : allChildren) {
            if (!(child instanceof ICommentPO)) {
                children.add(child);
            }
        }
        return children;
    }  
}