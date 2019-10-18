/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.utils;

import org.eclipse.jface.viewers.IElementComparer;

/**
 * @author Markus Tiede
 * @created May 11, 2011
 */
public class UIIdentitiyElementComparer implements IElementComparer {
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object a, Object b) {
        return a == b;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode(Object element) {
        return element.hashCode();
    }
}
