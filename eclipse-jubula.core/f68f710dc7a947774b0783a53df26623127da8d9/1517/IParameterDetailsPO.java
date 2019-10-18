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

/**
 * @author BREDEX GmbH
 * @created Mar 8, 2010
 */
public interface IParameterDetailsPO {

    /**
     * only for Persistence (JPA / EclipseLink)
     * @return Returns the id.
     */
    public abstract Long getId();

    /**
     * {@inheritDoc}
     */
    public abstract String toString();
    
    /** 
     * {@inheritDoc}
     */
    public abstract Integer getVersion();
    
    /**
     * @return the parameter name.
     */
    public abstract String getParameterName();

    /**
     * @param parameterName the parameter name to set.
     */
    public abstract void setParameterName(String parameterName);
    
    /**
     * @return the internal parameter type.
     */
    @Deprecated
    public abstract String getInternalParameterType();

    /**
     * @param parameterType the internal parameter type to set.
     */
    @Deprecated
    public abstract void setInternalParameterType(String parameterType);
    
    /**
     * @return the parameter type.
     */
    public abstract String getParameterType();

    /**
     * @param parameterType the parameter Type to set.
     */
    public abstract void setParameterType(String parameterType);
    
    /**
     * @return the parameter value.
     */
    public abstract String getParameterValue();

    /**
     * @param parameterValue the parameter value to set.
     */
    public abstract void setParameterValue(String parameterValue);
    
    /**
     * @return the test result Summary ID
     */
    public Long getInternalTestResultSummaryID();
    
    /**
     * @param testResultSummaryId the test result Summary ID
     */
    public void setInternalTestResultSummaryID(Long testResultSummaryId);
    
    /**
     * Checks the equality of the given Object with this Object.
     * @param obj the object to check
     * @return if there is a database ID it returns true if the ID is equal.
     * If there is no ID it will be compared to identity.
     */
    public abstract boolean equals(Object obj);

    /**
     * 
     * {@inheritDoc}
     * @return
     */
    public abstract int hashCode();
}
