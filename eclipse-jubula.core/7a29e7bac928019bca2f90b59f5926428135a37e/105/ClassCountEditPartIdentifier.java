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
package org.eclipse.jubula.rc.rcp.e3.gef.identifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.EditPart;

/**
 * Default implementation of {@link IEditPartIdentifier}. The identifier is
 * the name of the class of the {@link EditPart} plus a sequential number.
 *
 * @author BREDEX GmbH
 * @created May 13, 2009
 */
public class ClassCountEditPartIdentifier implements IEditPartIdentifier {

    /** the EditPart for which identifiers can be generated */
    private EditPart m_editPart;

    /**
     * Constructor
     *
     * @param editPart The EditPart for which identifiers can be generated.
     */
    public ClassCountEditPartIdentifier(EditPart editPart) {
        m_editPart = editPart;
    }

    /**
     * {@inheritDoc}
     */
    public String getIdentifier() {
        EditPart parent = m_editPart.getParent();
        String className = m_editPart.getClass().getName();
        String id = className;
        if (parent != null) {
            id += "_"; //$NON-NLS-1$
            Iterator siblingIterator = parent.getChildren().iterator();
            boolean isFound = false;
            int count = 0;
            while (siblingIterator.hasNext() && !isFound) {
                EditPart sibling = (EditPart)siblingIterator.next();
                if (sibling == m_editPart) {
                    isFound = true;
                }
                if (sibling.getClass().getName().equals(className)) {
                    count++;
                }
            }
            id += count;
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ConnectionAnchor> getConnectionAnchors() {
        return new HashMap<String, ConnectionAnchor>();
    }
}