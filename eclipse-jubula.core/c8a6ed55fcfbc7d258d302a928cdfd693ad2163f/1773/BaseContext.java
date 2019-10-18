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
package org.eclipse.jubula.client.teststyle.checks.contexts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Context which represents the place where the check should be performed.
 * Also, it contains the possibility of getting all elements of this context of
 * the open project.
 * 
 * @author marcell
 */
public abstract class BaseContext implements Comparable<BaseContext> {
    
    /** Set of contexts for the getFor() function */
    private static Set<BaseContext> contexts = new HashSet<BaseContext>(); 

    /** the context class */
    private Class<?> m_context;

    /**
     * The constructor needs a class which represents the scope where the checks
     * can be performed.
     * 
     * @param cls
     *            the context type
     */
    public BaseContext(Class<?> cls) {
        this.m_context = cls;
        contexts.add(this);
    }
    
    /** 
     * @return The name of the context that will be displayed.
     */
    public abstract String getName();
    
    /**
     * @return The description of this context.
     */
    public abstract String getDescription();

    /**
     * @return the context class
     */
    public Class<?> getContext() {
        return m_context;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof BaseContext) {
            BaseContext other = (BaseContext)obj;
            return other.m_context.equals(this.m_context);
        }
        return false;
        
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return m_context.hashCode();
    }

    /**
     * The implementation of this class greatly differs depending on the context
     * class.
     * 
     * @return Every object of the context class in the currents project.
     */
    public abstract List<? extends Object> getAll();

    /**
     * 
     * @param cls
     *            the class which the basecontext should posess
     * @return the BaseContext for equality measures.
     */
    public static BaseContext getFor(Class<?> cls) {
        for (BaseContext context : contexts) {
            if (context.getContext().isAssignableFrom(cls)) {
                return context;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param str
     *            the class which the basecontext should posess, null if no
     *            context was found.
     * @return the BaseContext for equality measures.
     */
    public static BaseContext getFor(String str) {
        for (BaseContext context : contexts) {
            if (context.getClass().getSimpleName().equals(str)) {
                return context;
            }
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public int compareTo(BaseContext o) {
        String left = this.getClass().getName();
        String right = o.getClass().getName();
        return left.compareTo(right);
    }
}
