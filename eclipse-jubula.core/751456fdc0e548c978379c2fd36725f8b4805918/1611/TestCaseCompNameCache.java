/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.businessprocess;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * The cache for Test Case and Test Suite editors
 * @author BREDEX GmbH
 */
public class TestCaseCompNameCache extends BasicCompNameCache {

    /**
     * Constructor
     * @param context the context
     */
    public TestCaseCompNameCache(IPersistentObject context) {
        super(context);
    }
    
    /** {@inheritDoc} */
    public void handleExistingNames(
            Map<String, String> guidToCompNameMap) {
        Iterator iter = ((INodePO) getContext()).getAllNodeIter();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof IExecTestCasePO) {
                IExecTestCasePO execTestCase = (IExecTestCasePO)o;
                for (ICompNamesPairPO pair : execTestCase.getCompNamesPairs()) {
                    if (guidToCompNameMap.containsKey(pair.getSecondName())) {
                        pair.setSecondName(
                                guidToCompNameMap.get(
                                        pair.getSecondName()));
                    }
                }
            } else if (o instanceof ICapPO) {
                ICapPO capPO = (ICapPO)o;
                if (guidToCompNameMap.containsKey(capPO.getComponentName())) {
                    capPO.setComponentName(
                            guidToCompNameMap.get(
                                    capPO.getComponentName()));
                }
            }
        }
    }
}
