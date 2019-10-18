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
package org.eclipse.jubula.client.ui.rcp.handlers.filter.components;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.ui.rcp.provider.contentprovider.ComponentNameBrowserContentProvider.ReusedCompnamesCategory;


/**
 * @author BREDEX GmbH
 * @created 16.02.2009
 */
public class FilterReusedComponentNames extends ViewerFilter {

    /**
     * {@inheritDoc}
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return !(element instanceof ReusedCompnamesCategory);
    }
}
