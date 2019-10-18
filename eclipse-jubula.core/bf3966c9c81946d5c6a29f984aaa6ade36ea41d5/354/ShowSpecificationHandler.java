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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.businessprocess.UINodeBP;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;


/**
 * @author BREDEX GmbH
 * @created 25.10.2005
 *
 */
public class ShowSpecificationHandler extends AbstractShowSpecificationHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        ISpecTestCasePO specTc = UINodeBP.getSpecTC(getSelection());
        if (specTc != null) {
            showSpecUINode(specTc, MultipleTCBTracker.getInstance()
                    .getMainTCB());
            return null;
        }
        ITestSuitePO testSuite = UINodeBP.getSpecTS(getSelection());
        if (testSuite != null) {
            showSpecUINode(testSuite, Constants.TS_BROWSER_ID);
            
        }
        return null;
    }
}