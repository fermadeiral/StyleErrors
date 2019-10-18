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
package org.eclipse.jubula.client.ui.rcp.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.ui.filter.JBPatternFilter;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * @author BREDEX GmbH
 * @created 25.06.2009
 */
public class ObjectMappingEditorPatternFilter extends JBPatternFilter {

    /**
     * {@inheritDoc}
     */
    public boolean isElementVisible(Viewer viewer, Object element) {
        if (element instanceof IObjectMappingAssoziationPO) {
            IComponentIdentifier compId = 
                ((IObjectMappingAssoziationPO)element).getTechnicalName();
            if (compId != null && wordMatches(compId.getComponentClassName())) {
                return true;
            }
            
        }
        return super.isElementVisible(viewer, element);
    }
}
