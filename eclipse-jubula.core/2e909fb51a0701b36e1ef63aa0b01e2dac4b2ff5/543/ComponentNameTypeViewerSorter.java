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
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;


/**
 * @author BREDEX GmbH
 * @created 17.02.2009
 */
public class ComponentNameTypeViewerSorter extends ViewerComparator {
    /**
     * {@inheritDoc}
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        String e1Prop = null;
        String e2Prop = null;

        if (e1 instanceof IComponentNamePO && e2 instanceof IComponentNamePO) {
            e1Prop = ((IComponentNamePO)e1).getComponentType();
            e2Prop = ((IComponentNamePO)e2).getComponentType();
        }

        if (e1Prop != null && e2Prop != null) {
            // sort by the displayed comp type strings
            return getComparator().compare(CompSystemI18n.getString(e1Prop),
                    CompSystemI18n.getString(e2Prop));
        }

        return 0;
    }
}
