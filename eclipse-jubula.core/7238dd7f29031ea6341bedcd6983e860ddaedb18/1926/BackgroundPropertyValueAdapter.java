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

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import org.eclipse.jubula.rc.common.adaptable.IPropertyValue;

/**
 * @author BREDEX GmbH
 */
public class BackgroundPropertyValueAdapter 
    implements IPropertyValue<Background> {
    
    /** {@inheritDoc} */
    public String getStringRepresentation(Background b) {
        
        StringBuilder sb = new StringBuilder("Fills="); //$NON-NLS-1$
        Iterator<BackgroundFill> fillIterator = b.getFills().iterator();
        while (fillIterator.hasNext()) {
            BackgroundFill backgroundFill = fillIterator.next();
            sb.append(backgroundFill.getFill().toString());
            if (fillIterator.hasNext()) {
                sb.append(","); //$NON-NLS-1$
            }
        }        
        return sb.toString();
    }
}