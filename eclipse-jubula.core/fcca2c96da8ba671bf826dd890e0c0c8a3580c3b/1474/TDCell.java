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
package org.eclipse.jubula.client.core.model;

import java.util.List;

import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;


/**
 * This class represents a cell in the test data manager.
 * 
 * {@inheritDoc}
 *
 * @author BREDEX GmbH
 * @created 01.09.2005
 *
 */
public class TDCell {
    /**
     * The row.
     */
    private int m_row;
    /**
     * The column.
     */
    private int m_col;
    /**
     * The test data entry.
     */
    private String m_testData;
    /**
     * @param testData The test data entry
     * @param row The row
     * @param col The column
     */
    public TDCell(String testData, int row, int col) {
        setTestData(testData);
        m_row = row;
        m_col = col;
    }
    /**
     * @return Returns the column.
     */
    public int getCol() {
        return m_col;
    }
    /**
     * @return Returns the row.
     */
    public int getRow() {
        return m_row;
    }
    /**
     * @return Returns the test data entry.
     */
    public String getTestData() {
        return m_testData;
    }
    /**
     * @param node associated node
     * @return list of reference names contained in value of testdata object
     */
    public List <String> getReferences(IParamNodePO node) {
        String uniqueId = node.getDataManager().getUniqueIds().get(m_col);
        IParamDescriptionPO desc = node.getParameterForUniqueId(uniqueId);
        ParamValueConverter conv = 
            new ModelParamValueConverter(getTestData(), node, desc);
        return conv.getNamesForReferences();
    }
    /**
     * @param testData the testData to set
     */
    public void setTestData(String testData) {
        m_testData = testData;
    }
}