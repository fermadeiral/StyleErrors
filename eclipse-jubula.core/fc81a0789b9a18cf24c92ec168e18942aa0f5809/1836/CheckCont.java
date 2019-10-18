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
package org.eclipse.jubula.client.teststyle.checks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;


/**
 * A container for checks. It got special methods for getting checks in special
 * circumstances, e.g. every check from a context or every check from a
 * category.
 * 
 * Some of the data are redundant, for easier access and speed purposes.
 * 
 * (I know its some kind of global state, but I don't know another way that is
 * good)
 * 
 * @author marcell
 * 
 */
public final class CheckCont {

    /** plain ol' list of checks */
    private static Set<BaseCheck> checks = new HashSet<BaseCheck>();

    /** the contexts associated with the checks */
    private static Map<BaseContext, List<BaseCheck>> contextsWithChecks = 
        new HashMap<BaseContext, List<BaseCheck>>();
    
    /** the contexts associated with the decorating checks */
    private static Map<BaseContext, List<DecoratingCheck>> contextsWithDChecks =
        new HashMap<BaseContext, List<DecoratingCheck>>();

    /** The set of categories */
    private static Set<Category> categories = new HashSet<Category>();

    /** Private constructor for utility class */
    private CheckCont() {
    // Nothing >_<
    }
   
    /**
     * @return All checks that were ever added in this container with no
     *         specific association.
     */
    public static Set<BaseCheck> getAll() {
        return checks;
    }

    /**
     * The method is used for finding every check that is associated with the
     * context that is parameterized in this method.
     * 
     * @param context
     *            The context which checks should be returned.
     * @return The checks to the context.
     */
    public static List<BaseCheck> getChecksFor(BaseContext context) {
        if (contextsWithChecks.get((context)) != null) {
            return contextsWithChecks.get(context);
        }
        return new ArrayList<BaseCheck>();

    }
    
    /**
     * The method is used for finding every check that is associated with the
     * context that is parameterized in this method.
     * 
     * @param context
     *            The context which checks should be returned.
     * @return The checks to the context.
     */
    public static List<DecoratingCheck> getDecChecksFor(BaseContext context) {
        if (contextsWithDChecks.get((context)) != null) {
            return contextsWithDChecks.get(context);
        }
        return new ArrayList<DecoratingCheck>();

    }

    /**
     * @return Every Context ever used in this container.
     */
    public static Set<BaseContext> getContexts() {
        return contextsWithChecks.keySet();
    }

    /**
     * 
     * @return All categories
     */
    public static Set<Category> getCategories() {
        return categories;
    }

    /**
     * This method will be used to add a check with its association in this
     * container.
     * 
     * @param chk
     *            The checks that should be added.
     * @param cat
     *            The category that the check belongs to.
     * @param contexts
     *            The Contexts of the check.
     */
    public static void add(BaseCheck chk, Category cat,
            BaseContext[] contexts) {

        // First in the list of checks...
        checks.add(chk);

        // ...then in the categories Set...
        categories.add(cat);

        // ...and last but not least the context map.
        for (BaseContext context : contexts) {
            if (!contextsWithChecks.containsKey(context)) {
                contextsWithChecks.put(context, new ArrayList<BaseCheck>());
            }
            contextsWithChecks.get(context).add(chk);
        }
    }
    
    /**
     * This method will be used to add a check with its association in this
     * container.
     * 
     * @param chk
     *            The checks that should be added.
     * @param cat
     *            The category that the check belongs to.
     * @param contexts
     *            The Contexts of the check.
     */
    public static void add(DecoratingCheck chk, Category cat,
            BaseContext[] contexts) {

        // First in the list of checks...
        checks.add(chk);

        // ...then in the categories Set...
        categories.add(cat);

        // ...and last but not least the context map.
        for (BaseContext context : contexts) {
            if (!contextsWithDChecks.containsKey(context)) {
                contextsWithDChecks.put(context, 
                        new ArrayList<DecoratingCheck>());
            }
            contextsWithDChecks.get(context).add(chk);
        }
    }

}
