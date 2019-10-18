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

package org.eclipse.jubula.rc.javafx.adapter;

import java.util.Iterator;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;

import org.eclipse.jubula.rc.common.adaptable.IPropertyValue;

/**
 * @author BREDEX GmbH
 */
public class BorderPropertyValueAdapter 
    implements IPropertyValue<Border> {
    
    /** {@inheritDoc} */
    public String getStringRepresentation(Border b) {
        StringBuilder sb = new StringBuilder();
        Iterator<BorderStroke> strokeIterator = b.getStrokes().iterator();
        while (strokeIterator.hasNext()) {
            BorderStroke stroke = strokeIterator.next();
            sb.append("LeftStroke: " //$NON-NLS-1$
                    + "Color=" + stroke.getLeftStroke().toString() //$NON-NLS-1$
                    + "," //$NON-NLS-1$
                    + "Style=" + stroke.getLeftStyle().toString() //$NON-NLS-1$
            );
            sb.append("; "); //$NON-NLS-1$
            sb.append("RightStroke: " //$NON-NLS-1$
                    + "Color=" + stroke.getRightStroke().toString() //$NON-NLS-1$
                    + "," //$NON-NLS-1$
                    + "Style=" + stroke.getRightStyle().toString() //$NON-NLS-1$
            );
            sb.append("; "); //$NON-NLS-1$
            sb.append("TopStroke: " //$NON-NLS-1$
                    + "Color=" + stroke.getTopStroke().toString() //$NON-NLS-1$
                    + "," //$NON-NLS-1$
                    + "Style=" + stroke.getTopStyle().toString() //$NON-NLS-1$
            );
            sb.append("; "); //$NON-NLS-1$
            sb.append("BottomStroke: " //$NON-NLS-1$
                    + "Color=" + stroke.getBottomStroke().toString() //$NON-NLS-1$
                    + "," //$NON-NLS-1$
                    + "Style=" + stroke.getBottomStyle().toString() //$NON-NLS-1$
            );
            if (strokeIterator.hasNext()) {
                sb.append(" | "); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }
}