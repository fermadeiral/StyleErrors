/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
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

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ICompletenessCheckListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProblemPropagationListener;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the completeness check.
 *
 * @author BREDEX GmbH
 */
public class CompletenessCheckSourceProvider extends AbstractJBSourceProvider
    implements IProblemPropagationListener, ICompletenessCheckListener {
    /** ID of variable that indicates whether the completeness check is currently running */
    public static final String IS_CC_RUNNING = 
        "org.eclipse.jubula.client.ui.rcp.variable.isCCRunning"; //$NON-NLS-1$

    /** value for variable indicating whether the completeness check is currently running */
    private boolean m_isCCRunning = false;
    
    /** Constructor */
    public CompletenessCheckSourceProvider() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProblemPropagationListener(this);
        ded.addCompletenessCheckListener(this);
    }

    /** {@inheritDoc} */
    public void dispose() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeProblemPropagationListener(this);
        ded.removeCompletenessCheckListener(this);
    }

    /** {@inheritDoc} */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = 
            new HashMap<String, Object>();

        currentState.put(IS_CC_RUNNING, m_isCCRunning);
        return currentState;
    }

    /** {@inheritDoc} */
    public String[] getProvidedSourceNames() {
        return new String [] {IS_CC_RUNNING};
    }

    /** {@inheritDoc} */
    public void problemPropagationFinished() {
        gdFireSourceChanged(ISources.WORKBENCH, 
                IS_CC_RUNNING, m_isCCRunning);
    }

    /** {@inheritDoc} */
    public void completenessCheckStarted() {
        m_isCCRunning = true;
        gdFireSourceChanged(ISources.WORKBENCH, 
                IS_CC_RUNNING, m_isCCRunning);
    }

    /** {@inheritDoc} */
    public void completenessCheckFinished() {
        m_isCCRunning = false;
    }
}