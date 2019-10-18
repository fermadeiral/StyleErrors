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
package org.eclipse.jubula.client.core.utils;

import java.util.Iterator;

import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;


/**
 * @author BREDEX GmbH
 * @created 12.06.2006
 */
public class SpecTreeTraverser extends TreeTraverser {

    /**
     * Creates a traverser traversing over all Spec-Nodes (SpecTestCases and CAPs).
     * @param rootNode the root node (The Project)
     * @param operation the operation to execute.
     */
    public SpecTreeTraverser(INodePO rootNode, 
            ITreeNodeOperation<INodePO> operation) {
        super(rootNode, operation);
    }

    /**
     * {@inheritDoc}
     */
    protected void traverseImpl(ITreeTraverserContext<INodePO> context, 
            INodePO parent, INodePO node) {
        
        context.append(node);
        if (context.isContinue()) {
            for (ITreeNodeOperation<INodePO> operation : getOperations()) {
                operation.operate(context, parent, node, false);
            }
            if (node instanceof IProjectPO) {
                IProjectPO project = (IProjectPO)node;
                for (INodePO specPers : project.getUnmodSpecList()) {
                    
                    traverseImpl(context, project, specPers);
                }
            } else if (node instanceof ICategoryPO) {
                ICategoryPO catPO = (ICategoryPO)node;
                Iterator<INodePO> iter =  catPO.getNodeListIterator();
                while (iter.hasNext()) {
                    INodePO next = iter.next();
                    traverseImpl(context, catPO, next);
                }
            } else if (node instanceof ISpecTestCasePO) {
                ISpecTestCasePO specTC = (ISpecTestCasePO)node;
                Iterator<? extends INodePO> iter = specTC.getNodeListIterator();
                while (iter.hasNext()) {
                    traverseImpl(context, specTC, iter.next());
                }
                iter = specTC.getAllEventEventExecTC().iterator();
                while (iter.hasNext()) {
                    traverseImpl(context, specTC, iter.next());
                }
            }
            for (ITreeNodeOperation<INodePO> operation : getOperations()) {
                operation.postOperate(context, parent, node, false);
            }
        }
        context.removeLast();
    }  
}