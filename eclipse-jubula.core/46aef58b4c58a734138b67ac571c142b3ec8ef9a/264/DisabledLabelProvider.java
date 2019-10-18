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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.graphics.Color;



/**
 * Label provider for "disabled" (non-editable) text elements.
 *
 * @author BREDEX GmbH
 * @created Apr 9, 2010
 */
public class DisabledLabelProvider extends LabelProvider implements
        IColorProvider {

    /**
     * {@inheritDoc}
     */
    public Color getBackground(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getForeground(Object element) {
        return LayoutUtil.GRAY_COLOR;
    }

}
