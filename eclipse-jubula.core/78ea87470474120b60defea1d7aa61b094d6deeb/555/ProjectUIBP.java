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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.ProjectDialog.ProjectData;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author BREDEX GmbH
 * @created 12.06.2012
 *
 */
public class ProjectUIBP {
    /** the singleton instance */
    private static ProjectUIBP instance;
    
    /** the logger */
    private static final Logger LOG = LoggerFactory.
            getLogger(ProjectUIBP.class);
    
    /** the preference store */
    private final IPreferenceStore m_prefStore;
    
    /**
     * hidden constructor
     */
    private ProjectUIBP() {
        m_prefStore = Plugin.getDefault().getPreferenceStore();
    }
    
    /**
     * creates the instance of this class or ...
     * @return returns the instance
     */
    public static ProjectUIBP getInstance() {
        if (instance == null) {
            instance = new ProjectUIBP();
        }
        return instance; 
    }
    
    /**
     * checks whether a project is saved in preference store
     * @return true when project data exists, 
     *         false otherwise
     */
    public boolean shouldPerformAutoProjectLoad() {
        return m_prefStore.getBoolean(Constants.PERFORM_AUTO_PROJECT_LOAD_KEY);
    }

    /**
     * gets the project data from secure preference store
     * @return the project data
     */
    public static ProjectData getMostRecentProjectData() {
        ISecurePreferences node = getSecurePreferenceNode();
        ProjectData projectData = new ProjectData(StringConstants.EMPTY, 
                StringConstants.EMPTY);
        if (node != null) {
            try {
                projectData.setGUID(node.get(
                        Constants.MOST_RECENT_PROJECTDATA_GUID_KEY, 
                        StringConstants.EMPTY));
                projectData.setVersionString(node.get(
                        Constants.MOST_RECENT_PROJECTDATA_VERSION_KEY,
                        StringConstants.EMPTY));
                
            } catch (StorageException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        return projectData;
    }
    
    /**
     * saves the project data in secure preference store
     * @param projectData the project data
     */
    public void saveMostRecentProjectData(
            ProjectData projectData) {
        m_prefStore.setValue(Constants.PERFORM_AUTO_PROJECT_LOAD_KEY, true);
        ISecurePreferences node = getSecurePreferenceNode();
        if (node != null)  {
            try {
                node.put(Constants.MOST_RECENT_PROJECTDATA_GUID_KEY, 
                        projectData.getGUID(), true);
                node.put(Constants.MOST_RECENT_PROJECTDATA_VERSION_KEY, 
                        projectData.getVersionString(), true);
            } catch (StorageException e1) {
                LOG.error(e1.getLocalizedMessage(), e1);
            }
        } else {
            LOG.error("Node not found"); //$NON-NLS-1$
        }
    }
    
    /**
     * removes the project data from secure preference store
     */
    public void removeMostRecentProjectData() {
        ISecurePreferences node = getSecurePreferenceNode();
        m_prefStore.setValue(Constants.PERFORM_AUTO_PROJECT_LOAD_KEY, false);
        if (node != null) {
            node.removeNode();
        }
    }
    
    /**
     * creates the secure preference node and returns it
     * @return the secure preference node
     */
    private static ISecurePreferences getSecurePreferenceNode() {
        ISecurePreferences root = SecurePreferencesFactory.getDefault();
        ISecurePreferences node = root;
        if (root != null)  {
            node = root.node(
                    Constants.ORG_ECLIPSE_JUBULA_MOST_RECENT_PROJECTDATA_KEY);
        }
        return node;
    }

}
