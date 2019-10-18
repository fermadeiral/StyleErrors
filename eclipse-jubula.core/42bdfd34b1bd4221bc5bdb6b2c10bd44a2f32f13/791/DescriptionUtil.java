/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.wiki.ui.utils;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
/**
 * 
 * @author BREDEX GmbH
  */
public class DescriptionUtil {
    
    /** this is a util class*/
    private DescriptionUtil() {
        // private util class
    }
    
    /**
     * gets the description of the referenced {@link ISpecTestCasePO} or {@link ITestSuitePO} 
     * @param node the {@link IExecTestCasePO} or {@link IRefTestSuitePO}
     * @return the description of the corresponding {@link ISpecTestCasePO} or {@link ITestSuitePO}, 
     *         may be null
     */
    public static String getReferenceDescription(INodePO node) {
        String description = null;
        if (node instanceof IExecTestCasePO) {
            IExecTestCasePO exec = (IExecTestCasePO) node;
            description = exec.getSpecTestCase().getDescription();
        }
        if (node instanceof IRefTestSuitePO) {
            IRefTestSuitePO refTestSuite = (IRefTestSuitePO) node;
            description = refTestSuite.getTestSuite().getDescription();
        }
        return description;
    }
}
