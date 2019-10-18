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

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author marcell
 * 
 */
public abstract class Analyzer {

    /** name of the analyzer */
    private String m_name;

    /**
     * Analyses an object and executes operations depending on what this
     * analyser should do.
     * 
     * @param obj
     *            The object where the analyser will be executed with.
     */
    public abstract void execute(Object obj);

    /**
     * @return The suffix. Empty if none is required.
     */
    public String getSuffix() {
        return StringUtils.EMPTY;
    }

    /**
     * @return The icon path of the decoration
     */
    public String getIconPath() {
        return StringUtils.EMPTY;
    }

    /**
     * @return The prefix of this decoration, empty if none is required.
     */
    public String getPrefix() {
        return StringUtils.EMPTY;
    }

    /**
     * @param name
     *            New name
     */
    public final void setName(String name) {
        this.m_name = name;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return m_name;
    }

    @Override
    public int hashCode() {
        return m_name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Analyzer) {
            Analyzer other = (Analyzer) obj;
            return other.getName().equals(this.getName());
        }
        return false;
    }

    /**
     * Creates an relation between the object and the analyzer, so that the
     * analyzer is marked as analyzed and will be decorated.
     * 
     * @param obj
     *            Object where the analyzer should be added to
     */
    public final void addToAnalyzed(Object obj) {
        AnalyzerContainer.addAnalyzed(obj, this);
    }

}
