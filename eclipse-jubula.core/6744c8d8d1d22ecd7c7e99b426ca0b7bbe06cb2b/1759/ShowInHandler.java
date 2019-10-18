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
package org.eclipse.jubula.client.teststyle.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.handlers.project.ProjectPropertiesHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * @author Markus Tiede
 * @created Jul 12, 2011
 */
public class ShowInHandler extends AbstractHandler {
    /**
     * <code>TESTSTYLE_PROJECT_PROPERTY_PAGE_ID</code>
     */
    private static final String TESTSTYLE_PROJECT_PROPERTY_PAGE_ID = "org.eclipse.jubula.client.teststyle.propPage"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel instanceof StructuredSelection) {
            StructuredSelection ssel = (StructuredSelection)sel;
            Object o = ssel.getFirstElement();
            if (o instanceof MarkerItem) {
                MarkerItem mn = (MarkerItem)o;
                try {
                    IMarker marker = mn.getMarker();
                    if (marker != null) {
                        showTeststyleRule(marker
                                .getAttribute(IMarker.SOURCE_ID));
                    }
                } catch (CoreException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    /**
     * @param attribute
     *            the attribute
     */
    
    private void showTeststyleRule(Object attribute) {
        if (attribute instanceof String) {
            Command projectPropertiesCommand = CommandHelper
                    .getCommandService().getCommand(
                            RCPCommandIDs.PROJECT_PROPERTIES);
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(ProjectPropertiesHandler.SECTION_TO_OPEN,
                    TESTSTYLE_PROJECT_PROPERTY_PAGE_ID);
            parameters.put(ProjectPropertiesHandler.INNER_SECTION_TO_OPEN,
                    (String)attribute);
            CommandHelper.executeParameterizedCommand(ParameterizedCommand
                    .generateCommand(projectPropertiesCommand, parameters));
        }
    }
}
