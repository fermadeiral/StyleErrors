/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.checks.contexts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;



/**
 * @author marcell
 * @created Nov 9, 2010
 */
public class CentralTestDataContext extends BaseContext {

    /**
     * @param cls
     */
    public CentralTestDataContext() {
        super(ITestDataCubePO.class);
    }

    /**
     * {@inheritDoc}
     */
    public List<ITestDataCubePO> getAll() {
        IProjectPO project = GeneralStorage.getInstance().getProject();

        // Try to load the Test Suites of the project
        try {
            return Arrays.asList(
                    TestDataCubeBP.getAllTestDataCubesFor(project));
        } catch (NullPointerException exc) {
            // That means that there are none so return an empty list
            return Collections.emptyList();
        }
    }

    @Override
    public String getName() {
        return Messages.ContextCentralTestDataName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextCentralTestDataDescription;
    }

}
