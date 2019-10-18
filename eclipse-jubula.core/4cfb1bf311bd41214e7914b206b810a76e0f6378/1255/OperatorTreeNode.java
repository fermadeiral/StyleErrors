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
package org.eclipse.jubula.examples.aut.adder.swing.model;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents an operator tree node in the options tree.
 *
 * @author BREDEX GmbH
 * @created 22.03.2005
 */
public class OperatorTreeNode extends DefaultMutableTreeNode {
    /**
     * @param operator The operator.
     */
    public OperatorTreeNode(IOperator operator) {
        super(operator);
    }
}
