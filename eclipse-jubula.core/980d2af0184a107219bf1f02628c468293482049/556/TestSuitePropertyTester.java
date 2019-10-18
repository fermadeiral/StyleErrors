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
package org.eclipse.jubula.client.ui.rcp.propertytester;

import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;


/**
 * Property tester for Test Suite GUI Nodes.
 *
 * @author BREDEX GmbH
 * @created 28.04.2009
 */
public class TestSuitePropertyTester extends AbstractBooleanPropertyTester {
    /** the id of the "isInCurrentProject" property */
    public static final String HAS_AUT = "hasAUT"; //$NON-NLS-1$
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { HAS_AUT };

    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        ITestSuitePO testSuite = (ITestSuitePO)receiver;
        if (property.equals(HAS_AUT)) {
            return testSuite.getAut() != null ? true : false;
        }
        return false;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return ITestSuitePO.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
