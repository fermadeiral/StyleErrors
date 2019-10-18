/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BREDEX GmbH
 */
public class DataRowDTO {
    
    /** */
    private List<String> m_columns = new ArrayList<String>();

    /**
     * @return columns
     */
    @JsonProperty("columns")
    public List<String> getColumns() {
        return m_columns;
    }

    /**
     * @param columns 
     */
    public void setColumns(List<String> columns) {
        this.m_columns = columns;
    }
}