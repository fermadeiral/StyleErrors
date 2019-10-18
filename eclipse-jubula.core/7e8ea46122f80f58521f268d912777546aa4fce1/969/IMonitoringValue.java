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
package org.eclipse.jubula.tools.internal.objects;

/**
 * Minimum functionality of a monitoring value
 * 
 * @author BREDEX GmbH
 * @created 20.08.2010
 */
public interface IMonitoringValue {
    /**
     * 
     * @return The value
     */
    public String getValue();

    /**
     * 
     * @return The type
     */
    public String getType();

    /**
     * 
     * @param value
     *            The value to set
     */
    public void setValue(String value);

    /**
     * 
     * @param type
     *            The type to set
     */
    public void setType(String type);

    /**
     * @param category
     *            The name of the category
     */
    public void setCategory(String category);

    /**
     * @return The name of the category which this monitoring value is set to
     */
    public String getCategory();

    /**
     * @return If true, this value will be displayed is TestResultSummaryView
     */
    public Boolean isSignificant();

    /**
     * @param isSignificant
     *            if true, this value will be displayed is TestResultSummaryView
     */
    public void setSignificant(Boolean isSignificant);

}
