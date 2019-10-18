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
package org.eclipse.jubula.client.teststyle.problems;


import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;


/**
 * A Problem represents a violation of a check in checkstyle.
 * 
 * @author marcell
 * 
 */
public class Problem {

    /** Determines the check which lead to the problem */
    private BaseCheck m_check;

    /** The object that violated the definition */
    private Object m_obj;

    /**
     * Creates a new object with the relationship between a check and an object.
     * 
     * @param check
     *            The check which was violated
     * @param po
     *            the persistent object
     */
    public Problem(BaseCheck check, Object po) {
        this.m_check = check;
        this.m_obj = po;
    }

    /**
     * ({@inheritDoc}
     */
    public boolean equals(Object o) {
        if (o instanceof Problem) {
            Problem other = (Problem)o;
            return m_check.equals(other.m_check) && m_obj.equals(other.m_obj);
        }
        return false;
    }

    /**
     * ({@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_check).append(m_obj).toHashCode();
    }

    /**
     * 
     * @return the check which was violated
     */
    public BaseCheck getCheck() {
        return m_check;
    }

    /**
     * 
     * @return the persistent object with the problem
     */
    public Object getPO() {
        return m_obj;
    }
}
