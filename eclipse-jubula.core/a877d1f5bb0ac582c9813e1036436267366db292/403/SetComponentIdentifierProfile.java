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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingProfilePO;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.command.parameters.ProfileTypeParameter;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;
import org.eclipse.jubula.tools.internal.xml.businessprocess.ProfileBuilder;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
/**
 * Set Profile command handler
 *
 * @author BREDEX GmbH
 * @created 21.12.2015
 */
public class SetComponentIdentifierProfile extends AbstractSelectionBasedHandler
        implements IElementUpdater {
    @Override
    public void updateElement(UIElement element, Map parameters) {
        for (Object o : parameters.keySet()) {
            if (o.equals("org.eclipse.jubula.client.ui.rcp.command.parameters.profilesParameter")) { //$NON-NLS-1$
                ISelection sSel = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getSelectionService()
                        .getSelection();

                if (sSel instanceof TreeSelection
                        && ((TreeSelection) sSel).size() == 1
                        && ((TreeSelection) sSel)
                                .getFirstElement() 
                                instanceof IObjectMappingAssoziationPO) {

                    IObjectMappingAssoziationPO assoCompId = 
                            ((IObjectMappingAssoziationPO) 
                                    ((TreeSelection) sSel)
                            .getFirstElement());
                    IObjectMappingProfilePO p = 
                            assoCompId.getTechnicalName().getProfilePO();
                    String profileName = null;
                    if (p != null) {
                        profileName = getProfileName(p);
                        if (profileName != null && parameters.get(o).
                            equals(profileName)) {
                            element.setChecked(true);
                        } else {
                            element.setChecked(false);
                        }
                    } else if (parameters.get(o)
                            .equals(ProfileTypeParameter.GLOBAL)) {
                        element.setChecked(true);
                    } else {
                        element.setChecked(false);
                    }
                }
            }
        }
    }

    /**
     * Get the name of a profile by comparing it to the standard profiles. If p is null then the name is Global.
     * @param p the profile 
     * @return the name of the profile
     */
    private String getProfileName(IObjectMappingProfilePO p) {
        if (p == null) {
            return ProfileTypeParameter.GLOBAL;
        }
        List<Profile> stdProfiles = ProfileBuilder.getProfiles();
        String profileName = null;
        for (Profile profile : stdProfiles) {
            if (p.matchesTemplate(profile)) {
                profileName = profile.getName();
            }
        }
        return profileName;
    }

    /**
     * Get the selected Component Identifier
     * @param event the event
     * @return return the selected identifier
     */
    private IObjectMappingAssoziationPO getSelectedCompIdentifier(
            ExecutionEvent event) {
        IObjectMappingAssoziationPO selectedIDs = null;
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelection(event);
        if (selection != null && !selection.isEmpty()) {
            Iterator selIterator = selection.iterator();
            if (selIterator.hasNext()) {
                Object selectedElement = selIterator.next();
                if (selectedElement instanceof IObjectMappingAssoziationPO) {
                    selectedIDs = (IObjectMappingAssoziationPO) selectedElement;
                }
            }
        }
        return selectedIDs;
    }

    @Override
    protected Object executeImpl(ExecutionEvent event)
            throws ExecutionException {
        IObjectMappingAssoziationPO selectedID = 
                getSelectedCompIdentifier(event);

        String parameter = event.getParameter(
                "org.eclipse.jubula.client.ui.rcp.command.parameters.profilesParameter"); //$NON-NLS-1$
        if (parameter != null && parameter.equals(
                getProfileName(selectedID.getTechnicalName().getProfilePO()))) {
            changeProfile(event, selectedID, null);
        } else if (parameter != null) {
            Profile p = ProfileBuilder.getProfile((parameter));
            changeProfile(event, selectedID, p);
        }
        return null;
    }

    /**
     * changes the profile of a component identifier
     * @param event the event
     * @param iCompIdent the component identifier
     * @param p the profile
     * @return true if changing was successful, false otherwise
     * @throws ExecutionException
     */
    private boolean changeProfile(ExecutionEvent event,
            IObjectMappingAssoziationPO iCompIdent, Profile p)
                    throws ExecutionException {
        ObjectMappingMultiPageEditor ome = 
                ((ObjectMappingMultiPageEditor) HandlerUtil
                .getActivePartChecked(event));
        if (ome.getEditorHelper().requestEditableState() == EditableState.OK) {
            iCompIdent.getTechnicalName().setProfile(p);
            DataEventDispatcher ded = DataEventDispatcher.getInstance();
            ded.fireDataChangedListener(iCompIdent, DataState.StructureModified,
                    UpdateState.onlyInEditor);
            ome.getEditorHelper().setDirty(true);
            return true;
        }
        return false;
    }

}
