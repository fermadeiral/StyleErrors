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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;

/**
 * @author BREDEX GmbH
 * @created 18.02.2009
 */
public class NodeNameViewerSorter extends ViewerComparator {
    /** {@inheritDoc} */
    public int compare(Viewer viewer, Object e1, Object e2) {
        List<Class<?>> preferredTypes = new ArrayList<>(3);
        // Show reused project before all other elements
        preferredTypes.add(IReusedProjectPO.class);
        // After that categories 
        preferredTypes.add(ICategoryPO.class);
        // Show Event Handler before all other nested exec test cases
        preferredTypes.add(IEventExecTestCasePO.class);
        
        for (Class c: preferredTypes) {
            if (isOnlyFirstObjectInstanceOfClass(e1, e2, c)) {
                return -1;
            }
            
            if (isOnlyFirstObjectInstanceOfClass(e2, e1, c)) {
                return 1;
            }
        }
        
        // do not sort the sequence of exec test cases or caps in spec test cases
        if (unsortable(e1) || unsortable(e2)) {
            return 0;
        }

        return super.compare(viewer, e1, e2);
    }
    
    /**
     * @param o to be checked
     * @return <code>true</code> if the sorting is not necessary.
     *          Otherwise return <code>false</code>
     */
    private boolean unsortable(Object o) {
        return o instanceof IExecTestCasePO
                || o instanceof ICapPO
                || o instanceof IRefTestSuitePO
                || o instanceof IControllerPO
                || o instanceof IAbstractContainerPO
                || o instanceof ICommentPO;
    }

    /**
     * @param o1
     *            the first object
     * @param o2
     *            the second object
     * @param c
     *            the object class to check
     * @return if only the first object is an instance of the given class
     */
    private boolean isOnlyFirstObjectInstanceOfClass(Object o1, Object o2,
            Class c) {
        return c.isInstance(o1) && !c.isInstance(o2);
    }
}