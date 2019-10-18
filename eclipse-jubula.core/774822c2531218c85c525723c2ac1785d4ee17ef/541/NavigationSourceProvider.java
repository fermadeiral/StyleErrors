/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.ui.rcp.businessprocess.JBNavigationHistory;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

/**
 * Source provider for the navigation history
 * @author BREDEX GmbH
 *
 */
public class NavigationSourceProvider extends AbstractSourceProvider {

    /** Can go back */
    public static final String GO_BACK = "org.eclipse.jubula.client.ui.rcp.variable.navCanGoBack"; //$NON-NLS-1$

    /** Can go forward */
    public static final String GO_FORWARD = "org.eclipse.jubula.client.ui.rcp.variable.navCanGoForward"; //$NON-NLS-1$

    /** Can go back edited */
    public static final String EDITED_GO_BACK = "org.eclipse.jubula.client.ui.rcp.variable.navCanEditedGoBack"; //$NON-NLS-1$

    /** Can go forward edited */
    public static final String EDITED_GO_FORWARD = "org.eclipse.jubula.client.ui.rcp.variable.navCanEditedGoForward"; //$NON-NLS-1$

    /** Provided variables */
    private static final String[] VARIABLES =
        new String[] {GO_BACK, GO_FORWARD, EDITED_GO_BACK, EDITED_GO_FORWARD};

    /** The current state */
    private Map<String, Boolean> m_currentState = new HashMap<>();

    /** Constructor */
    public NavigationSourceProvider() {
        JBNavigationHistory.getInstance().addProvider(this);
    }

    @Override
    public void dispose() {
        JBNavigationHistory.getInstance().removeProvider(this);
    }

    @Override
    public Map getCurrentState() {
        return m_currentState;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return VARIABLES;
    }

    /**
     * Fires source changed 
     * @param goBack can go back
     * @param goForward can go forward
     * @param editGoBack can go to prev edited Editor
     * @param editGoForw can go to next edited Editor
     */
    public void fireSourceChanged(boolean goBack, boolean goForward,
            boolean editGoBack, boolean editGoForw) {
        m_currentState.clear();
        m_currentState.put(GO_BACK, goBack);
        m_currentState.put(GO_FORWARD, goForward);
        m_currentState.put(EDITED_GO_BACK, editGoBack);
        m_currentState.put(EDITED_GO_FORWARD, editGoForw);
        fireSourceChanged(ISources.WORKBENCH, m_currentState);
    }
}
