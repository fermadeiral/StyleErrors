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
package org.eclipse.jubula.client.teststyle.properties.provider;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jubula.client.teststyle.properties.nodes.INode;
import org.eclipse.swt.graphics.Image;


/**
 * @author marcell
 * @created Oct 21, 2010
 */
public class TeststyleLabelProvider implements ILabelProvider {

    /**
     * {@inheritDoc}
     */
    public Image getImage(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        INode node = (INode)element;
        return node.getText();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(ILabelProviderListener listener) {
    // nothing at the moment
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(ILabelProviderListener listener) {
    // nothing needed
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    // DO NOTHING
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

}
