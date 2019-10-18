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
package org.eclipse.jubula.client.ui.rcp.provider.contentprovider;

import org.apache.commons.lang.Validate;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Markus Tiede
 * @created May 24, 2011
 */
public abstract class AbstractNodeTreeContentProvider implements
    ITreeContentProvider {
    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        Validate.isTrue(inputElement instanceof Object[]);
        return (Object[])inputElement;
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // no-op
    }
}
