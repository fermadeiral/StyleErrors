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

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.JBPropertyDialog;
import org.eclipse.jubula.client.ui.rcp.extensions.ProjectPropertyExtensionHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.properties.AUTPropertyPage;
import org.eclipse.jubula.client.ui.rcp.properties.AbstractProjectPropertyPage;
import org.eclipse.jubula.client.ui.rcp.properties.ProjectALMPropertyPage;
import org.eclipse.jubula.client.ui.rcp.properties.ProjectGeneralPropertyPage;
import org.eclipse.jubula.client.ui.rcp.properties.ProjectUsedPropertyPage;
import org.eclipse.jubula.client.ui.rcp.properties.ProjectGeneralPropertyPage.IOkListener;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.DialogUtils.SizeType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author BREDEX GmbH
 * @created 01.07.2016
 */
public class ProjectPropertyDialog {

    /** Default constructor is not needed */
    private ProjectPropertyDialog() {
        // Default constructor is not needed
    }
    
    /**
     * @param shell 
     * @param sectionToOpen 
     * @param innerSectionToOpen 
     */
    public static final void showPropertyDialog(Shell shell,
            String sectionToOpen, String innerSectionToOpen) {
        PreferenceManager mgr = new PreferenceManager();
        ISelection sel = new StructuredSelection(GeneralStorage.getInstance()
            .getProject());
        // add the 1st property page
        try {
            final EditSupport es = AbstractProjectPropertyPage
                .createEditSupport();
            ProjectGeneralPropertyPage generalPage = createPages(es, mgr);

            // Adds project property pages from extensions
            for (AbstractProjectPropertyPage pg 
                    : ProjectPropertyExtensionHandler.createPages(es, mgr)) {
                if (pg instanceof IOkListener) {
                    generalPage.addOkListener((IOkListener)pg);
                }
            }
            
            JBPropertyDialog dialog = new JBPropertyDialog(shell, mgr, sel);
            dialog.create();
            if (sectionToOpen != null) {
                dialog.setCurrentPageId(sectionToOpen);
                IPreferencePage page = dialog.getCurrentPage();
                if (page instanceof PreferencePage) {
                    ((PreferencePage)page).applyData(innerSectionToOpen);
                }
            }
            DialogUtils.setWidgetNameForModalDialog(dialog);
            //sets the title
            Shell s = dialog.getShell();
            s.setText(Messages.ProjectPropertyPageShellTitle 
                    + generalPage.getProject().getName());
            DialogUtils.adjustShellSizeRelativeToClientSize(
                    s, .8f, .9f, SizeType.SIZE);
            dialog.open();
            es.close();
        } catch (PMObjectDeletedException e) { 
            // this implies that the project was deleted since the properties
            // are always available
            PMExceptionHandler.handleProjectDeletedException();            
        } catch (PMException e) {
            ErrorHandlingUtil.createMessageDialog(e, null, null);
        }
    }

    /**
     * Creates the project property pages.
     * 
     * @param es The edit support.
     * @param mgr The preference manager.
     * @return the created general property page.
     */
    private static final ProjectGeneralPropertyPage createPages(
            EditSupport es, PreferenceManager mgr) {

        ProjectGeneralPropertyPage generalPage = 
            new ProjectGeneralPropertyPage(es);
        generalPage.setTitle(Messages.PropertiesActionPage1);
        IPreferenceNode generalNode = new PreferenceNode(
            Constants.PROJECT_PROPERTY_ID, generalPage);
        mgr.addToRoot(generalNode);

        PropertyPage autPage = new AUTPropertyPage(es);
        autPage.setTitle(Messages.PropertiesActionPage3);
        IPreferenceNode autNode = new PreferenceNode(
            Constants.AUT_PROPERTY_ID, autPage);
        mgr.addToRoot(autNode);

        ProjectUsedPropertyPage usedPage = new ProjectUsedPropertyPage(es);
        usedPage.setTitle(Messages.PropertiesActionPage4);
        IPreferenceNode usedNode = new PreferenceNode(
            Constants.REUSED_PROJECT_PROPERTY_ID, usedPage);
        mgr.addToRoot(usedNode);

        ProjectALMPropertyPage almPage = new ProjectALMPropertyPage(es);
        almPage.setTitle(Messages.PropertiesActionPage5);
        IPreferenceNode almNode = new PreferenceNode(
                Constants.ALM_PROJECT_PROPERTY_ID, almPage);
        mgr.addToRoot(almNode);
        
        generalPage.addOkListener(usedPage);
        generalPage.addOkListener(almPage);
        
        return generalPage;
    }
}
