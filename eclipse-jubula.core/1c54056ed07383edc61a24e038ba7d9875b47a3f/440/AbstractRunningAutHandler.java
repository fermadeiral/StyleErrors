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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.businessprocess.RunningAutBP;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created May 10, 2010
 */
public abstract class AbstractRunningAutHandler extends AbstractHandler
        implements IElementUpdater {
    
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractRunningAutHandler.class);

    /** parameter key => last AUT identifier map */
    private static Map<String, AutIdentifier> lastAutID = new HashMap<>();
    
    /**
     * @param event
     *            the execution event this handler has been triggered from
     * @param parameterKey
     *            the key for the running aut command parameter
     * @return the AutIdentifier for the requested running AUT
     */
    protected AutIdentifier getRunningAut(ExecutionEvent event,
            String parameterKey) {
        if (Job.getJobManager().find(this).length > 0) {
            return null;
        }
        Object runningAutObj = null;
        try {
            runningAutObj = event.getObjectParameterForExecution(parameterKey);
            if (runningAutObj instanceof AutIdentifier) {
                return (AutIdentifier) runningAutObj;
            }
            LOG.error(Messages.RunningAUTParameter + StringConstants.SPACE
                    + StringConstants.APOSTROPHE + runningAutObj
                    + StringConstants.APOSTROPHE + StringConstants.SPACE
                    + Messages.NotOfCorrectType + StringConstants.DOT);
            return null;
        } catch (ExecutionException ee) {
            // ignore --> check for only one running aut
            LOG.info(Messages.MissingRunningAUTParameter);
        }
        Collection<AutIdentifier> availableAUTs = 
                RunningAutBP.getListOfDefinedRunningAuts();
        if (lastAutID.get(parameterKey) != null) {
            if (availableAUTs.contains(lastAutID.get(parameterKey))) {
                return lastAutID.get(parameterKey);
            }
            lastAutID.put(parameterKey, null);
        }
        if (!availableAUTs.isEmpty()) {
            return availableAUTs.iterator().next();
        }
        return null;
    }
    
    /**
     * Sets the last AUTId for the key
     * @param key the key
     * @param id the AutIdentifier
     */
    protected static void setLastAutID(String key, AutIdentifier id) {
        lastAutID.put(key, id);
    }
    
    /** 
     * Returns the parameter key
     * @return the parameter key
     */
    protected abstract String getKey();
    
    /** {@inheritDoc} */
    public void updateElement(UIElement element, Map parameters) {
        Object oAutID = parameters.get(getKey());
        AutIdentifier autID = null;

        if (!(oAutID instanceof String)) {
            return;
        }
        
        try {
            autID = AutIdentifier.decode((String) oAutID);
        } catch (IllegalArgumentException e) {
            // nothing important, we just won't check this element...
            return;
        }

        element.setChecked(autID.equals(lastAutID.get(getKey())));
    }
}