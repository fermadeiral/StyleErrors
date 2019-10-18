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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;

/**
 * @author Markus Tiede
 * @created Jul 20, 2011
 */
public class ParameterInterfacePropertyTester extends
        AbstractBooleanPropertyTester {
    /**
     * <code>HAS_MULTIPLE_DATA_SETS_PROPERTY</code>
     */
    private static final String HAS_MULTIPLE_DATA_SETS_PROPERTY = "hasMultipleDataSets"; //$NON-NLS-1$

    /**
     * <code>USES_CTDS_PROPERTY</code>
     */
    private static final String USES_CTDS_PROPERTY = "usesCTDS"; //$NON-NLS-1$

    /**
     * <code>USES_EXCEL_PROPERTY</code>
     */
    private static final String USES_EXCEL_PROPERTY = "usesExcel"; //$NON-NLS-1$

    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] {
        USES_EXCEL_PROPERTY, USES_CTDS_PROPERTY,
        HAS_MULTIPLE_DATA_SETS_PROPERTY };

    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        IParameterInterfacePO paramInterface = (IParameterInterfacePO)receiver;
        if (property.equals(USES_CTDS_PROPERTY)) {
            return testUsesCTDS(paramInterface);
        }
        if (property.equals(USES_EXCEL_PROPERTY)) {
            return testUsesExcel(paramInterface);
        }
        if (property.equals(HAS_MULTIPLE_DATA_SETS_PROPERTY)) {
            return testHasMultipleDataSets(paramInterface);
        }
        return false;
    }

    /**
     * @param paramInterface
     *            the param interface object to test
     * @return whether param interface po has multiple data sets
     */
    private boolean testHasMultipleDataSets(
            IParameterInterfacePO paramInterface) {
        return paramInterface.getDataManager().getDataSetCount() > 1;
    }

    /**
     * @param paramInterface
     *            the param interface object to test
     * @return whether param interface po makes use of excel as a data source
     */
    private boolean testUsesExcel(IParameterInterfacePO paramInterface) {
        return !StringUtils.isEmpty(paramInterface.getDataFile());
    }

    /**
     * @param paramInterface
     *            the param interface object to test
     * @return whether param interface po uses a central test data set
     */
    private boolean testUsesCTDS(IParameterInterfacePO paramInterface) {
        return paramInterface.getReferencedDataCube() != null ? true : false;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return IParameterInterfacePO.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
