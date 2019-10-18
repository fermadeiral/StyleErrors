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
package org.eclipse.jubula.client.ui.rcp.handlers.project;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.handlers.project.AbstractProjectHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler for opening the Project Properties dialog.
 *
 * @author BREDEX GmbH
 * @created Apr 30, 2009
 */
public class ProjectPropertiesHandler extends AbstractProjectHandler {
    /** 
     * ID of command parameter for the section of the Project Properties
     * dialog to activate.
     */
    public static final String SECTION_TO_OPEN = 
        "org.eclipse.jubula.client.ui.rcp.commands.ProjectProperties.parameter.sectionToOpen"; //$NON-NLS-1$
    
    /** 
     * ID of command parameter for the Project Properties dialog after opening the specified section
     */
    public static final String INNER_SECTION_TO_OPEN = 
        "org.eclipse.jubula.client.ui.rcp.commands.ProjectProperties.parameter.innerSectionToOpen"; //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchWindow activeWindow = 
            HandlerUtil.getActiveWorkbenchWindow(event);
        Shell shell = activeWindow != null ? activeWindow.getShell() : null;
        String sectionToOpen = event.getParameter(SECTION_TO_OPEN);
        String innerSectionToOpen = event.getParameter(INNER_SECTION_TO_OPEN);
        ProjectPropertyDialog.showPropertyDialog(shell, sectionToOpen,
                innerSectionToOpen);
        return null;
    }
}
