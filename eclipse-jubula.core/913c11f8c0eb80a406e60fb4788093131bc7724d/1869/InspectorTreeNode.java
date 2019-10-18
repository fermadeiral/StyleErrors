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
package org.eclipse.jubula.client.inspector.ui.model;

import org.eclipse.jface.viewers.TreeNode;

/**
 * @author BREDEX GmbH
 */
public class InspectorTreeNode extends TreeNode {

    /**
     * @param value
     *            the value
     */
    public InspectorTreeNode(Object value) {
        super(value);
    }
}
