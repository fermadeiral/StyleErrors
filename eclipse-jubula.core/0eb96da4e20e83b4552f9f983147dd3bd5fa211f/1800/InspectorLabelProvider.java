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
package org.eclipse.jubula.client.inspector.ui.provider.labelprovider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;

/**
 * @author BREDEX GmbH
 * @created Jun 12, 2009
 */
public class InspectorLabelProvider extends LabelProvider {
    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        if (element instanceof TreeNode) {
            return getText(((TreeNode)element).getValue());
        } else if (element instanceof String []) {
            String [] stringArray = (String [])element;
            if (stringArray.length > 0) {
                return stringArray[0];
            }
        }
        return super.getText(element);
    }
}
