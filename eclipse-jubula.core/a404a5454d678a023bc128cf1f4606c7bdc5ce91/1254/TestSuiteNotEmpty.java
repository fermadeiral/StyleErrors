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
package org.eclipse.jubula.client.teststyle.impl.standard.checks;

import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.impl.standard.i18n.Messages;

/**
 * Testsuites should at least reference one test case
 */
public class TestSuiteNotEmpty extends BaseCheck {
    @Override
    public String getDescription() {
        return Messages.NoEmptyTestSuitesProblemDescription;
    }

    @Override
    public boolean hasError(Object obj) {
        if (obj instanceof ITestSuitePO) {
            ITestSuitePO ts = (ITestSuitePO) obj;
            if (ts.getNodeListSize() == 0) {
                return true;
            }
        }
        return false;
    }
}
