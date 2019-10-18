/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.project;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.handlers.project.AbstractProjectHandler;
import org.eclipse.jubula.client.ui.rcp.wizards.pages.UpdateReusedProjectsDialog;
import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * @author BREDEX GmbH
 * @created 01.07.2016
 */
public class UpdateReusedProjectHandler extends AbstractProjectHandler {

    /** Info about the reused projects and the newest versions */
    private Map<IReusedProjectPO, ProjectVersion> m_oldReusedProjects;
    
    /** Constructor */
    public UpdateReusedProjectHandler() {
        searchOldReusedProjects();
    }
    
    @Override
    protected Object executeImpl(ExecutionEvent event) {
        showUpdateReusedProjectDialog();
        return null;
    }

    /**
     * Brings up the UpdateReusedProjectDiaog
     * @return the return code of dialog
     */
    public int showUpdateReusedProjectDialog() {
        UpdateReusedProjectsDialog dialog = new UpdateReusedProjectsDialog(
                getActiveShell(), m_oldReusedProjects);
        dialog.setHelpAvailable(true);
        return dialog.open();
    }
    
    /** Fill up the m_oldReusedProjects */
    private void searchOldReusedProjects() {
        m_oldReusedProjects = new HashMap<IReusedProjectPO, ProjectVersion>();
        Iterator<IReusedProjectPO> reusedProjects = GeneralStorage.getInstance()
                .getProject().getUsedProjects().iterator();
        while (reusedProjects.hasNext()) {
            IReusedProjectPO reusedProject = reusedProjects.next();
            try {
                ProjectVersion newestVersion = ProjectPM
                        .findHighestVersionNumber(
                            reusedProject.getProjectGuid());
                if (newestVersion != null && newestVersion.compareTo(
                        reusedProject.getProjectVersion()) > 0) {
                    m_oldReusedProjects.put(reusedProject, newestVersion);
                }
            } catch (JBException e) {
                e.printStackTrace();
            }
        }
    }

    /** @return reused project info */
    public Map<IReusedProjectPO, ProjectVersion> getOldReusedProjects() {
        return m_oldReusedProjects;
    }
    
    /** 
     * @return <code>false</code> if m_oldReusedProjects is null or empty
     * <code>true</code> otherwise.
     */
    public boolean isThereOldReusedProject() {
        return m_oldReusedProjects == null
                || m_oldReusedProjects.isEmpty() ? false : true;
    }
}
