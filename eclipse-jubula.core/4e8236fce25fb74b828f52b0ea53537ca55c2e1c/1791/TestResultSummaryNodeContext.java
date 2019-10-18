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

import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.teststyle.i18n.Messages;


/**
 * @author marcell
 * @created Nov 4, 2010
 */
public class TestResultSummaryNodeContext extends DecoratingContext {

    /**
     * @param cls
     */
    public TestResultSummaryNodeContext() {
        super(TestResultNode.class);
    }

    @Override
    public String getName() {
        return Messages.ContextTestResultSummaryNodeName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextTestResultSummaryNodeDescription;
    }

}
