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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jubula.client.ui.rcp.wizards.ExportTestResultDetailsWizard;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.ui.IWorkbenchCommandConstants;

/**
 * Handler for exporting details for one or more Test Result Summaries.
 * 
 * @created Oct 5, 2012
 */
public class ExportTestResultsHandler extends AbstractHandler {

    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        
        Command exportTestResultDetailsCommand = 
                CommandHelper.getCommandService().getCommand(
                        IWorkbenchCommandConstants.FILE_EXPORT);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(IWorkbenchCommandConstants.FILE_EXPORT_PARM_WIZARDID,
                ExportTestResultDetailsWizard.ID);
        
        return CommandHelper.executeParameterizedCommand(
                ParameterizedCommand.generateCommand(
                        exportTestResultDetailsCommand, parameters));
        
    }

}
