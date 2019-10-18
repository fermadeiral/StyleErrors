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
package org.eclipse.jubula.client.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.model.INodePO;

/**
 * object for stack
 * 
 * @author BREDEX GmbH
 * @created 12.04.2005
 *
 */
public class ExecObject {
    
    /**
     * <code>m_execNode</code> execTestCase
     */
    private INodePO m_execNode = null;
    
    /**
     * <code>m_numberDs</code> actual executed dataset
     */
    private int m_numberDs = -1;
    
    /**
     * <code>m_index</code> index of actual executed child
     */
    private int m_index = Traverser.NO_INDEX;
    
    /** 
     * the number of times the node for this object has been retried.
     * this is used for handling the RETRY event handler. 
     */
    private int m_retryCount = 0;
    
    /** The loop count */
    private int m_loopCount = 0;
    
    /** mapping from parameter identifiers to parameter values */
    private Map<String, String> m_parameters = new HashMap<String, String>();
    
    /**
     * constructor
     * 
     * @param node node
     * @param number
     *            number of actual executed dataset
     */
    public ExecObject(INodePO node, int number) {
        m_execNode = node;
        m_numberDs = number;
    }
    
    /**
     * @return Returns the node.
     */
    public INodePO getExecNode() {
        return m_execNode;
    }
    
    /**
     * @return Returns the lastDataSet.
     */
    public int getNumberDs() {
        return m_numberDs;
    }
    
    /**
     * increment index
     */
    public void incrementIndex() {
        ++m_index;
    }
    
    /**
     * decrement index
     */
    public void decrementIndex() {
        --m_index;
    }
    
    /**
     * increment dataset number
     */
    public void incrementDataSetNumber() {
        ++m_numberDs;
    }
    
    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return m_index;
    }
    /**
     * @param index The index to set.
     */
    public void setIndex(int index) {
        m_index = index;
    }
    
    /** 
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        boolean result = false;
        if (obj != null && obj instanceof ExecObject) {
            ExecObject execObj = (ExecObject) obj;
            result = m_index == execObj.getIndex()
                && m_numberDs == execObj.getNumberDs()
                && m_execNode.equals(execObj.getExecNode())
                && m_retryCount == execObj.getRetryCount();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_execNode.hashCode())
            .append(m_index).append(m_numberDs).append(m_retryCount)
            .toHashCode();
    }
    
    /**
     * 
     * @return the number of times the node for this object has been retried.
     */
    public int getRetryCount() {
        return m_retryCount;
    }
    
    /**
     * Sets the value for the parameter with the given identifier to 
     * <code>value</code> within the context of the receiver. If the receiver 
     * already has a value for the given identifier, it is overwritten by the 
     * new value.
     * 
     * @param identifier The identifier for the parameter. 
     *                   See {@link IParamDescriptionPO#getUniqueId()}.
     * @param value The new value to assign.
     */
    public void addParameter(String identifier, String value) {
        m_parameters.put(identifier, value);
    }

    /**
     * 
     * @param parameterIdentifier The identifier for the parameter. See 
     *                            {@link IParamDescriptionPO#getUniqueId()}.
     * @return the current value for the parameter with the given identifier
     *         within the context of the receiver.
     */
    public String getParameterValue(String parameterIdentifier) {
        return m_parameters.get(parameterIdentifier);
    }
    
    /**
     * Returns the incremented loop count
     * @return the count
     */
    public int getIncLoopCount() {
        return ++m_loopCount;
    }
    
}
