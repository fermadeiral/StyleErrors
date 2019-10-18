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
package org.eclipse.jubula.client.ui.rcp.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.client.core.model.IComponentNamePO;


/**
 * @author BREDEX GmbH
 * @created 17.02.2009
 */
public class ComponentNameNameViewerSorter extends ViewerComparator {
    /**
     * {@inheritDoc}
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof IComponentNamePO && e2 instanceof IComponentNamePO) {
            return super.compare(viewer, e1, e2);
        }
        return 0;
    }
}
