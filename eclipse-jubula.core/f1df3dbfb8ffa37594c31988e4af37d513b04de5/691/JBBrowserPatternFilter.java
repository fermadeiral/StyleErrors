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
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.ui.filter.JBPatternFilter;


/**
 * Patter Filter for Test Case Browser; skips CAPs and Exec's due to performance
 * issues
 * 
 * @author BREDEX GmbH
 * @created 04.03.2009
 */
public class JBBrowserPatternFilter extends JBPatternFilter {

    /**
     * {@inheritDoc}
     */
    public boolean isElementVisible(Viewer viewer, Object element) {
        if (element instanceof ICapPO || element instanceof IExecTestCasePO) {
            return false;
        }
        return super.isElementVisible(viewer, element);
    }
}
