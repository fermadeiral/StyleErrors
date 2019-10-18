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
package org.eclipse.jubula.client.teststyle.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.constants.Ext;
import org.eclipse.jubula.client.teststyle.problems.Problem;
import org.eclipse.jubula.client.teststyle.quickfix.Quickfix;


/**
 * Handles the communication between my framework and the problems view.
 * 
 * @author marcell
 * 
 */
public class MarkerHandler {

    /** singleton */
    private static MarkerHandler instance;
    /** the workspace root resource */
    private IWorkspaceRoot m_res = ResourcesPlugin.getWorkspace().getRoot();
    /** List of the problems in relation of their markers. */
    private Map<Problem, IMarker> m_problems = new HashMap<Problem, IMarker>();
    /** List of the markers in relation of their problems. */
    private Map<IMarker, Problem> m_markers = new HashMap<IMarker, Problem>();

    /**
     * Private constructor for the singleton
     */
    private MarkerHandler() {
        // Marcell was here
    }

    /**
     * @return The singleton instance.
     */
    public static MarkerHandler getInstance() {
        if (instance == null) {
            instance = new MarkerHandler();
        }
        return instance;
    }

    /**
     * 
     * @param problem
     *            the problem which causes the marker to appear.
     */
    public void add(Problem problem) {
        BaseCheck chk = problem.getCheck();
        Object obj = problem.getPO();
        try {
            IMarker marker = m_res.createMarker(Ext.TSM_MARKER);

            if (obj instanceof IPersistentObject) {
                // When the object is a IPersistentObject, the method .getName
                // is better for setting the location.
                marker.setAttribute(IMarker.LOCATION, ((IPersistentObject)obj)
                        .getName());
            } else {
                // When its something different, just take the toString()
                // of the object.
                marker.setAttribute(IMarker.LOCATION, obj);
            }

            marker.setAttribute(IMarker.SEVERITY, chk.getSeverity().ordinal());
            marker.setAttribute(IMarker.MESSAGE, chk.getDescription());
            marker.setAttribute(IMarker.SOURCE_ID, chk.getId());
            marker.setAttribute(IMarker.USER_EDITABLE, false);
            m_problems.put(problem, marker);
            m_markers.put(marker, problem);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a problem from the problems view.
     * 
     * @param problem
     *            the problem with the marker that should be deleted
     */
    public void remove(Problem problem) {
        try {
            IMarker problemMarker = m_problems.get(problem);
            if (problemMarker != null) {
                IMarker marker = m_res.findMarker(problemMarker.getId());
                marker.delete();
                m_markers.remove(marker);
            }
            m_problems.remove(problem);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param marker
     *            Marker which resolutions should be searched.
     * @return Resolutions of this marker.
     */
    public Quickfix[] getResolutions(IMarker marker) {
        Problem problem = m_markers.get(marker);
        Quickfix[] fixes = problem.getCheck().getQuickfix(problem.getPO());
        for (Quickfix quickfix : fixes) {
            quickfix.setSource(marker);
        }
        return problem.getCheck().getQuickfix(problem.getPO());
    }
    
    /**
     * 
     * @param markers Markers
     * @param source = src
     * @return markers
     */
    public IMarker[] findOtherMarker(IMarker[] markers, IMarker source) {
        Set<IMarker> mLst = new HashSet<IMarker>();
        Problem sourceProblem = m_markers.get(source);
        for (IMarker marker : markers) {
            Problem p = m_markers.get(marker);
            if (p != null && !marker.equals(source) 
                    && p.getCheck().equals(sourceProblem.getCheck())) {
                mLst.add(marker);
            }
        }
        return mLst.toArray(new IMarker[mLst.size()]);
    }
    
    /**
     * @param marker Marker where the problem should be searched for
     * @return The problem
     */
    public Problem getProblemFromMarker(IMarker marker) {
        return m_markers.get(marker);
    }

}
