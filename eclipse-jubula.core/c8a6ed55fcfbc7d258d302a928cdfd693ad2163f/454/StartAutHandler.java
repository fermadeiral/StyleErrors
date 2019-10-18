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

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.businessprocess.StartAutBP;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionGUIController;
import org.eclipse.jubula.client.ui.rcp.dialogs.NagDialog;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;


/**
 * Handler for "Start AUT" command.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class StartAutHandler extends AbstractHandler 
        implements IElementUpdater {

    /** ID of command parameter for AUT to start */
    public static final String AUT_TO_START = 
        "org.eclipse.jubula.client.ui.rcp.commands.ChooseAutCommand.parameter.autToStart"; //$NON-NLS-1$
    
    /** ID of command parameter for AUT Configuration to start */
    public static final String AUT_CONF_TO_START =
        "org.eclipse.jubula.client.ui.rcp.commands.ChooseAutCommand.parameter.autConfigToStart"; //$NON-NLS-1$
    
    /** ID of command state for most recently started AUT */
    public static final String LAST_STARTED_AUT =
        "org.eclipse.jubula.client.ui.rcp.commands.ChooseAutCommand.state.lastStartedAut"; //$NON-NLS-1$

    /** ID of command state for most recently started AUT Configuration */
    public static final String LAST_STARTED_CONFIG =
        "org.eclipse.jubula.client.ui.rcp.commands.ChooseAutCommand.state.lastStartedAutConfig"; //$NON-NLS-1$
    
    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        Object autToStartObj = null;
        Object autConfigToStartObj = null;
        IAUTMainPO autToStart = null;
        IAUTConfigPO autConfigToStart = null;
        try {
            autToStartObj = event.getObjectParameterForExecution(AUT_TO_START);
            autConfigToStartObj = 
                event.getObjectParameterForExecution(AUT_CONF_TO_START);
        } catch (ExecutionException ee) {
            // Parameters could not be found or parsed.
            // Not a problem, we'll try later to use the current command
            // state to find out which AUT to start.
        }
        Command command = event.getCommand();
        if (autToStartObj instanceof IAUTMainPO
                && autConfigToStartObj instanceof IAUTConfigPO) {
            autToStart = (IAUTMainPO)autToStartObj;
            autConfigToStart = (IAUTConfigPO)autConfigToStartObj;
            
        } else {
            State lastStartedAutState = 
                command.getState(LAST_STARTED_AUT);
            State lastStartedAutConfigState = 
                command.getState(LAST_STARTED_CONFIG);
            if (lastStartedAutState != null
                    && lastStartedAutConfigState != null) {

                Object autStateValue = lastStartedAutState.getValue();
                Object autConfigStateValue = 
                    lastStartedAutConfigState.getValue();
                if (autStateValue instanceof IAUTMainPO
                        && autConfigStateValue instanceof IAUTConfigPO) {
                    IAUTMainPO lastAUT = 
                        (IAUTMainPO)autStateValue;
                    Long currentProjectId = GeneralStorage.getInstance()
                            .getProject().getId();
                    if (ObjectUtils.equals(lastAUT.getParentProjectId(),
                            currentProjectId)) {
                        autToStart = (IAUTMainPO)autStateValue;
                        autConfigToStart = (IAUTConfigPO)autConfigStateValue;
                    }
                }
            }
        }

        if (autToStart != null && autConfigToStart != null) {
            // Start the AUT
            startAut(autToStart, autConfigToStart);

            // Update command state
            State lastAutState = command.getState(LAST_STARTED_AUT);
            State lastConfigState = command.getState(LAST_STARTED_CONFIG);
            if (lastAutState != null && lastConfigState != null) {

                lastAutState.setValue(autToStart);
                lastConfigState.setValue(autConfigToStart);
            }
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void updateElement(UIElement element, Map parameters) {
        Object autObj = parameters.get(AUT_TO_START);
        Object autConfObj = parameters.get(AUT_CONF_TO_START);

        boolean setChecked = false;
        IAUTMainPO lastUsedAut = StartAutBP.getInstance().getLastUsedAut();
        if (lastUsedAut != null 
                && lastUsedAut.getId().toString().equals(autObj)) {
            IAUTConfigPO lastUsedConf = StartAutBP.getInstance()
                .getLastUsedConf();
            if (lastUsedConf != null 
                    && lastUsedConf.getId().toString().equals(autConfObj)) {
                setChecked = true;
            }
        }
        
        element.setChecked(setChecked);
    }

    /**
     * @param aut AUT to start
     * @param conf corresponding configuration
     */
    public static void startAut(IAUTMainPO aut, IAUTConfigPO conf) {
        if (aut.getToolkit().equals(CommandConstants.RCP_TOOLKIT)) {
            NagDialog.runNagDialog(null, "InfoNagger.RunRcpAut",  //$NON-NLS-1$
                    ContextHelpIds.AUT_CONFIG_SETTING_WIZARD_PAGE);
        }
        StartAutBP instance = StartAutBP.getInstance();
        instance.fireAutStarted();
        TestExecutionGUIController.startAUT(aut, conf);
        instance.setLastUsedAut(aut);
        instance.setLastUsedAutConf(conf);
    }
}