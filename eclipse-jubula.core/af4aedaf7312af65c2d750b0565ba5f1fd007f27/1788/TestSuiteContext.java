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

import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.teststyle.i18n.Messages;



/**
 * Specific Context for TestSuites.
 * @author marcell
 *
 */
public class TestSuiteContext extends BaseContext {

    /**
     * Explicit constructor which calls the super constructor for setting the
     * class.
     */
    public TestSuiteContext() {
        super(ITestSuitePO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Object> getAll() {
        return TestSuiteBP.getListOfTestSuites();
    }

    @Override
    public String getName() {
        return Messages.ContextTestSuiteName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextTestSuiteDescription;
    }    
}
