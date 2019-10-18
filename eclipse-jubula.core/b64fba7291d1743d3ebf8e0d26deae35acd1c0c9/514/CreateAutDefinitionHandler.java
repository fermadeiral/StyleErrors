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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.RunningAutBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.AUTPropertiesDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler for creating an AUT Definition.
 *
 * @author BREDEX GmbH
 * @created Apr 27, 2010
 */
public class CreateAutDefinitionHandler 
    extends AbstractSelectionBasedHandler {
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        Object selectedObject = getSelection().getFirstElement();
        if (selectedObject instanceof AutIdentifier) {
            AutIdentifier autId = (AutIdentifier)selectedObject;
            IProjectPO currentProject = 
                GeneralStorage.getInstance().getProject();
            if (currentProject != null 
                    && !RunningAutBP.isAutDefined(autId)) {
                Set<String> existingNames = new HashSet<String>();
                for (IAUTMainPO aut : currentProject.getAutMainList()) {
                    existingNames.add(aut.getName());
                }
                String autName = generateUniqueName(
                        autId.getExecutableName(), existingNames);
                
                EditSupport es = null;
                try {
                    es = new EditSupport(
                            currentProject.getProjectProperties(), null);
                    es.lockWorkVersion();
                    IAUTMainPO newAut = PoMaker.createAUTMainPO(autName);
                    newAut.getAutIds().add(autId.getExecutableName());
                    AUTPropertiesDialog newAutDialog = 
                        new AUTPropertiesDialog(
                            HandlerUtil.getActiveWorkbenchWindow(event)
                                .getShell(), true, 
                            newAut, es.getWorkProject());
                    newAutDialog.create();
                    DialogUtils.setWidgetNameForModalDialog(newAutDialog);
                    newAutDialog.getShell().setText(
                        Messages.AUTPropertyPageAUTConfig);
                    
                    if (newAutDialog.open() == Window.OK) {
                        es.getWorkProject().addAUTMain(newAut);
                        es.saveWorkVersion();
                        
                        GeneralStorage.getInstance().getMasterSession()
                            .refresh(GeneralStorage.getInstance()
                                .getProject().getAutCont());
                        DataEventDispatcher.getInstance()
                                .fireProjectStateChanged(
                                        ProjectState.prop_modified);
                    }
                } catch (PMException e) {
                    ErrorHandlingUtil.createMessageDialog(e, null, null);
                } catch (ProjectDeletedException e) {
                    ErrorHandlingUtil.createMessageDialog(e, null, null);
                } finally {
                    if (es != null) {
                        es.close();
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * Generates a unique name based on the information provided. The returned
     * name is guaranteed to not already be contained in the provided set.
     * 
     * @param baseName The base to use for the generated name.
     * @param existingNames Already existing names.
     *                      
     * @return a name, based on <code>baseName</code> that is not already 
     *         contained within <code>existingNames</code>.
     */
    private String generateUniqueName(String baseName,
            Set<String> existingNames) {

        String generatedName = baseName;
        int count = 1;
        
        while (existingNames.contains(generatedName)) {
            generatedName = baseName + count;
            count++;
        }
        
        return generatedName;
    }

}
