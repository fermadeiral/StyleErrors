/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.propertytester.NodePropertyTester;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 */ 
public class NodeAttributeEvaluator extends AbstractFunctionEvaluator {
    /** the comment attribute name */
    private static final String COMMENT_ATTRIBUTE = "comment"; //$NON-NLS-1$
    
    /** the name attribute name */
    private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
    
    /** the description attribute name */
    private static final String DESCRIPTION_ATTRIBUTE = "description"; //$NON-NLS-1$
    
    /** the taskId attribute name */
    private static final String TASK_ID_ATTRIBUTE = "task.id"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 1);
        final String attributeName = arguments[0].toLowerCase();
        String attributeValue = null;
        FunctionContext context = getContext();
        if (context != null) {
            INodePO node = context.getNode();
            switch (attributeName) {
                case NAME_ATTRIBUTE:
                    attributeValue = node.getName();
                    break;
                case COMMENT_ATTRIBUTE:
                    attributeValue = node.getComment();
                    break;
                case DESCRIPTION_ATTRIBUTE:
                    attributeValue = node.getDescription();
                    break;
                case TASK_ID_ATTRIBUTE:
                    attributeValue = NodePropertyTester.getTaskIdforNode(node);
                    break;
                default:
                    throw new InvalidDataException("Unkown attribute: " //$NON-NLS-1$
                        + attributeName, MessageIDs.E_FUNCTION_EVAL_ERROR);
            }
        } else {
            throw new InvalidDataException(
                    "Function is being called without a node context!", //$NON-NLS-1$
                    MessageIDs.E_FUNCTION_EVAL_ERROR);
        }
        return attributeValue;
    }
}