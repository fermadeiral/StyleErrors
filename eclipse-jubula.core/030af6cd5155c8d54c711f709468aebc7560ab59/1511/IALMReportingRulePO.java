/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.model;

import org.eclipse.jubula.client.core.utils.ReportRuleType;

/**
 * @author BREDEX GmbH
 */
public interface IALMReportingRulePO extends IPersistentObject, Comparable {
    /**
     * @return the name of the rule
     */
    public abstract String getName();

    /**
     * sets the name of the rule
     * @param name the name
     */
    public abstract void setName(String name);

    /**
     * @return the id of the attribute where the rule gets applied to
     */
    public abstract String getAttributeID();

    /** sets the id of the attribute where the rule gets applied to
     * @param attributeID the id
     */
    public abstract void setAttributeID(String attributeID);

    /**
     * @return the value which will be written into the field
     */
    public abstract String getValue();

    /** sets the value which will be written into the field
     * @param value the value 
     */
    public abstract void setValue(String value);

    /**
     * returns for which occasions the rule applies 
     * @return the type
     */
    public abstract ReportRuleType getType();

    /** sets the type of the rule
     * @param type the type 
     */
    public abstract void setType(ReportRuleType type);

    /** Returns a copy of the instance
     * @return the copy
     */
    public abstract IALMReportingRulePO copy();
}