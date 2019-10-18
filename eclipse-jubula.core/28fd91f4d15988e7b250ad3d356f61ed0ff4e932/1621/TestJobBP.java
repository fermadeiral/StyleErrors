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
package org.eclipse.jubula.client.core.businessprocess.db;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP.ExecNodeFinder;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.TreeTraverser;

/**
 * @author BREDEX GmbH
 * @created Oct 14, 2011
 */
public class TestJobBP extends NodeBP {
    /**
     * Utility class
     */
    private TestJobBP() {
    // do nothing
    }
    /**
     * @return an unmodifiable list of test suite for the current project 
     */
    public static List<ITestJobPO> getListOfTestJobs() {
        return getListOfTestJobs(GeneralStorage.getInstance().getProject());
    }
    
    /**
     * @param project the project to use for test suite retrieval
     * @return an unmodifiable list of test suite for the current project 
     */
    public static List<ITestJobPO> getListOfTestJobs(IProjectPO project) {
        if (project != null) {
            final ExecNodeFinder<ITestJobPO> op = 
                    new ExecNodeFinder<ITestJobPO>(ITestJobPO.class);
            new TreeTraverser(project, op, false, true).traverse(false);
            return op.getListOfExecNodes();
        }
        
        return ListUtils.EMPTY_LIST;
    }
}
