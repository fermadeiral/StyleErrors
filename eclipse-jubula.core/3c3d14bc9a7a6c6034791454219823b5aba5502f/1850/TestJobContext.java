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

import org.eclipse.jubula.client.core.businessprocess.db.TestJobBP;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.teststyle.i18n.Messages;



/**
 * @author marcell
 * @created Nov 9, 2010
 */
public class TestJobContext extends BaseContext {

    /**
     * @param cls
     */
    public TestJobContext() {
        super(ITestJobPO.class);
    }

    @Override
    public List<? extends Object> getAll() {
        return TestJobBP.getListOfTestJobs();
    }

    @Override
    public String getName() {
        return Messages.ContextTestJobName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextTestJobDescription;
    }

}
