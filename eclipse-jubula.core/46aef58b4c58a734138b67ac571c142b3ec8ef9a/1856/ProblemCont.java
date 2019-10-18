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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.gui.MarkerHandler;


/**
 * Contains all Problems that Checkstyle has to handle.
 * 
 * @author BREDEX GmbH
 * 
 */
public enum ProblemCont {
    /** singleton */
    instance;

    /** List containing the problems to the POs */
    private Map<Object, Set<Problem>> m_problems = Collections
            .synchronizedMap(new HashMap<Object, Set<Problem>>());

    /**
     * Adds an Object and the problem to the container. This objects will be in
     * relation to each other.
     * 
     * @param obj
     *            the persistent object which has the problem
     * @param check
     *            the check triggered the problem
     */
    public void add(Object obj, BaseCheck check) {
        Problem problem = new Problem(check, obj);
        if (!m_problems.containsKey(obj)) {
            m_problems.put(obj, new CopyOnWriteArraySet<Problem>());
        }
        if (m_problems.get(obj).add(problem)) {
            MarkerHandler.getInstance().add(problem);
        }
    }

    /**
     * Removes the problem from the container. When the object was a problem,
     * then removes the marker from the problemview. When there's no problem
     * left for one po, then the whole po will be dismissed from the container.
     * 
     * @param obj
     *            the persistent object which has the problem
     * @param check
     *            the check that triggered the problem
     */
    public void remove(Object obj, BaseCheck check) {
        Problem problem = new Problem(check, obj);
        if (m_problems.get(obj).remove(problem)) {
            MarkerHandler.getInstance().remove(problem);
        }
        if (m_problems.get(obj).size() == 0) {
            m_problems.remove(obj);
        }
    }

    /**
     * Checks if the container already has an Object.
     * 
     * @param obj
     *            The element to be checked
     * @return if the element is in the keys of the problems member
     */
    public boolean contains(Object obj) {
        return m_problems.containsKey(obj);
    }

    /**
     * Clears all problems from the container.
     */
    public void clear() {
        Set<Object> keys = new HashSet<Object>(m_problems.keySet());
        for (Object obj : keys) {
            remove(obj);
        }
    }

    /**
     * Removes all problems from one po.
     * 
     * @param po
     *            The po where all problems should be removed.
     */
    public void remove(Object po) {
        Object obj = po;
        if (obj instanceof ITestDataCubePO) {
            obj = ((ITestDataCubePO)obj).getId(); 
        }
        if (!m_problems.containsKey(obj)) {
            return;
        }
        for (Problem p : m_problems.get(obj)) {
            MarkerHandler.getInstance().remove(p);
        }
        m_problems.remove(obj);
    }

    /**
     * 
     * @param obj
     *            The object which should be checked for violations.
     * @return Returns all checks which violated the object and an empty array
     *         if nothing violated the object.
     */
    public Set<BaseCheck> getChecksFor(Object obj) {
        Object newObj = obj;
        if (obj instanceof ITestDataCubePO) {
            newObj = ((ITestDataCubePO)obj).getId();
        }
        Set<BaseCheck> checks = new HashSet<BaseCheck>();
        if (contains(newObj)) {
            for (Problem p : m_problems.get(newObj)) {
                checks.add(p.getCheck());
            }
        }
        return checks;
    }
}
