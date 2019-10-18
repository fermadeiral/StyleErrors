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
package org.eclipse.jubula.client.teststyle.analyze;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;


/**
 * Container.
 * 
 * @author marcell
 */
public class AnalyzerContainer {

    /** relationship between classes and analyzer */
    private static Map<BaseContext, Set<Analyzer>> relation =
            new HashMap<BaseContext, Set<Analyzer>>();

    /** analyzed objects */
    private static Map<Object, Set<Analyzer>> analyzed =
            new HashMap<Object, Set<Analyzer>>();

    /** Private constructor for utility class */
    private AnalyzerContainer() {
        // no-op
    }

    /**
     * FIXME mbs
     * 
     * @param contexts
     *            FIXME mbs
     * @return FIXME mbs
     */
    public static Set<Analyzer> getIntersections(Set<BaseContext> contexts) {
        Set<Analyzer> tmp = new HashSet<Analyzer>();
        Iterator<BaseContext> iter = contexts.iterator();
        if (iter.hasNext()) {
            BaseContext context = iter.next();
            Set<Analyzer> analyzers = relation.get(context);
            analyzers = analyzers != null ? analyzers : new HashSet<Analyzer>();
            tmp.addAll(analyzers);
            while (iter.hasNext()) {
                context = iter.next();
                analyzers = relation.get(context);
                analyzers =
                        analyzers != null ? analyzers : new HashSet<Analyzer>();
                tmp.retainAll(analyzers);
            }
        }
        return tmp;
    }

    /**
     * FIXME mbs
     * 
     * @param context
     *            FIXME mbs
     * @param analyzer
     *            FIXME mbs
     */
    public static void add(BaseContext context, Analyzer analyzer) {
        if (!relation.containsKey(context)) {
            relation.put(context, new HashSet<Analyzer>());
        }
        relation.get(context).add(analyzer);
    }

    /**
     * 
     * @return all defined analyzers
     */
    public static Set<Analyzer> getAll() {
        Set<Analyzer> tmp = new HashSet<Analyzer>();
        for (Set<Analyzer> analyzers : relation.values()) {
            tmp.addAll(analyzers);
        }
        return tmp;
    }

    /**
     * FIXME mbs
     * 
     * @param obj
     *            FIXME mbs
     * @return FIXME mbs
     */
    public static Set<Analyzer> getAnalyzerFor(Object obj) {
        return analyzed.get(obj);
    }
    
    /**
     * FIXME mbs
     * 
     * @param obj
     *            FIXME mbs
     * @param analyzer
     *            FIXME mbs
     */
    public static void addAnalyzed(Object obj, Analyzer analyzer) {
        if (!analyzed.containsKey(obj)) {
            analyzed.put(obj, new HashSet<Analyzer>());
        }
        analyzed.get(obj).add(analyzer);
    }
}
