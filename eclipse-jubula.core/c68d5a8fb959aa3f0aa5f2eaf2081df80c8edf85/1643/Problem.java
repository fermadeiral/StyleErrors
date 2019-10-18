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
package org.eclipse.jubula.client.core.businessprocess.problems;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.core.runtime.IStatus;

/**
 * Implements IProblem with its members.
 * 
 * @author BREDEX GmbH
 * @created 24.01.2011
 */
final class Problem implements IProblem {

    /** message that the marker shows */
    private String m_markerMessage;
    
    /** the backing status */
    private IStatus m_status;
    
    /** data */
    private Object m_data;
    
    /** problem type */
    private ProblemType m_problemType;
    
    /**
     * Constructor
     * 
     * @param markerMessage The message to use for the marker representing this
     *                      problem, or <code>null</code> if the Problem should
     *                      not be represented by a marker. Note that the empty
     *                      string (<code>""</code>) will result in using a 
     *                      marker with an empty message, so there is a 
     *                      difference between using <code>null</code> and 
     *                      <code>""</code>.
     * @param status Backing status for the created object. Must not be 
     *               <code>null</code>. Must contain an internationalized 
     *               message suitable for display as a tooltip.
     * @param data Data associated with the created object, or 
     *             <code>null</code> if no additional information is needed.
     * @param type The type of Problem that the created object represents.
     */
    Problem(String markerMessage, IStatus status, 
            Object data, ProblemType type) {
        
        Validate.notNull(status);
        Validate.notNull(status.getMessage());
        
        m_markerMessage = markerMessage;
        m_status = status;
        m_data = data;
        m_problemType = type;
    }

    /** {@inheritDoc} */
    public String getUserMessage() {
        return m_markerMessage;
    }

    /** {@inheritDoc} */
    public String getTooltipMessage() {
        return m_status.getMessage();
    }
    
    /** {@inheritDoc} */
    public IStatus getStatus() {
        return m_status;
    }

    /** {@inheritDoc} */
    public Object getData() {
        return m_data;
    }

    /** {@inheritDoc} */
    public ProblemType getProblemType() {
        return m_problemType;
    }
    
    /** {@inheritDoc} */
    public boolean hasUserMessage() {
        return getUserMessage() != null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof Problem)) {
            return false;
        }

        Problem otherProblem = (Problem)obj;
        return new EqualsBuilder().append(getData(), otherProblem.getData())
            .append(getUserMessage(), otherProblem.getUserMessage())
            .append(getProblemType(), otherProblem.getProblemType())
            .append(getTooltipMessage(), otherProblem.getTooltipMessage())
            .append(getStatus().getSeverity(), 
                    otherProblem.getStatus().getSeverity())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getData())
            .append(getUserMessage())
            .append(getProblemType())
            .append(getTooltipMessage())
            .append(getStatus().getSeverity())
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
