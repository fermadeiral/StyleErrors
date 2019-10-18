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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.controllers.OpenOMETracker;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.filter.ObjectMappingEditorProfileFilter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.menus.UIElement;

/**
 * Filter Profile command handler
 *
 * @author BREDEX GmbH
 * @created 21.12.2015
 */
public class FilterProfileHandler extends AbstractHandler
        implements IElementUpdater, IChangeListener {

    /** parameter to set the filter active/inactive */
    private static final String FILTER_ACTIVE_PARAMETER = 
            "org.eclipse.jubula.client.ui.rcp.command.parameters.filterActiveParameter"; //$NON-NLS-1$
    
    /** parameter to set which profile should be filtered */
    private static final String FILTER_PROFILE_PARAMETER = 
            "org.eclipse.jubula.client.ui.rcp.command.parameters.filterProfilesParameter"; //$NON-NLS-1$
    
    /** command state; which profile will be filtered **/
    private static final String FILTER_PROFILE_STATE = 
            "org.eclipse.jubula.client.ui.rcp.commands.FilterProfile.state.filterProfile"; //$NON-NLS-1$
    
    /**
     * the filter
     */
    private static final ObjectMappingEditorProfileFilter FILTER = 
            new ObjectMappingEditorProfileFilter();

    /**
     * Constructor used to add listener
     */
    public FilterProfileHandler() {
        super();
        OpenOMETracker.INSTANCE.addListener(this);
    }

    @Override
    public void dispose() {
        OpenOMETracker.INSTANCE.removeListener(this);
        super.dispose();
    }



    @Override
    public void updateElement(UIElement element, Map parameters) {
        ICommandService service = PlatformUI.getWorkbench()
                .getService(ICommandService.class);
        Command command = service.getCommand(RCPCommandIDs.FILTER_PROFILE);
        State state = command.getState(FILTER_PROFILE_STATE); // $NON-NLS-1$
        State filterActive = command.getState(RegistryToggleState.STATE_ID);
        Object filterActiveParameter = parameters.get(FILTER_ACTIVE_PARAMETER);
        // Check if the UI Element is the toolbar button
        // This is the case if filterActiveParameter is in the parameters map
        if (state != null && filterActive != null
                && filterActiveParameter != null
                && filterActive.getValue().equals(true)) {
            element.setIcon(IconConstants.PROFILE_FILTER_ON_DESCRIPTOR);
        } else if (filterActive != null && filterActiveParameter != null
                && filterActive.getValue().equals(false)) {
            element.setIcon(IconConstants.PROFILE_FILTER_OFF_DESCRIPTOR);
            // Else the element is a menu item, therefore it is checked if it
            // should be checked
        } else {
            for (Object o : parameters.keySet()) {
                if (o.equals(FILTER_PROFILE_PARAMETER)) {
                    if (state != null && state.getValue() != null
                            && state.getValue().equals(parameters.get(o))) {
                        element.setChecked(true);
                    }
                }
            }
        }
    }

    @Override
    protected Object executeImpl(ExecutionEvent event)
            throws ExecutionException {
        String parameter = event.getParameter(FILTER_PROFILE_PARAMETER);
        Command command = event.getCommand();
        State profileState = command.getState(FILTER_PROFILE_STATE);
        State toggleState = command.getState(RegistryToggleState.STATE_ID);
        Iterator i = OpenOMETracker.INSTANCE.getIterator();
        List<TreeViewer> views = new ArrayList<TreeViewer>();
        //collect open views
        while (i.hasNext()) {
            ObjectMappingMultiPageEditor ome = (ObjectMappingMultiPageEditor) i
                    .next();
            views.addAll(getViews(ome));
        }
        // Same menu item selected, deactivate filter
        if (profileState != null && parameter != null
                && profileState.getValue() != null
                && profileState.getValue().equals(parameter)) {
            toggleState.setValue(false);
            profileState.setValue("null"); //$NON-NLS-1$
            checkFilter(views);
            // Toolbar Button pressed, deactivate filter
        } else if (parameter == null) {
            profileState.setValue(parameter);
            toggleState.setValue(false);
            checkFilter(views);
            // Menu item selected, activate filter
        } else {
            profileState.setValue(parameter);
            toggleState.setValue(true);
            for (TreeViewer treeViewer : views) {
                treeViewer.removeFilter(FILTER);
            }
            FILTER.setPattern(parameter);
            checkFilter(views);
        }
        // Refresh elements to change the force the call of updateElement
        // This is necessary to change the toolbar icon
        ICommandService commandService = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getService(ICommandService.class);
        if (commandService != null) {
            commandService.refreshElements(command.getId(), null);
        }
        return null;
    }

    /**
     * Get the relevant views from the ObjectMappingMultiPageEditor
     * 
     * @param ome
     *            the editor
     * @return list containing the views
     */
    private List<TreeViewer> getViews(ObjectMappingMultiPageEditor ome) {
        List<TreeViewer> views = new ArrayList<TreeViewer>();
        views.add(ome.getUIElementTreeViewer());
        views.add(ome.getMappedTreeViewer());
        return views;
    }

    /**
     * checks for the given views if the filter has to be added or removed and does that
     * @param views the views to check
     */
    private void checkFilter(List<TreeViewer> views) {
        ICommandService service = PlatformUI.getWorkbench()
                .getService(ICommandService.class);
        Command command = service.getCommand(RCPCommandIDs.FILTER_PROFILE);
        State filterActive = command.getState(RegistryToggleState.STATE_ID);
        for (TreeViewer treeViewer : views) {
            boolean alreadyHasFilter = false;
            for (ViewerFilter f : treeViewer.getFilters()) {
                // If there is a filter, but the command state is false (no
                // filter active) we remove the filter
                if (f.equals(FILTER)) {
                    if (filterActive.getValue().equals(false)) {
                        treeViewer.removeFilter(FILTER);
                    }
                    alreadyHasFilter = true;
                }
            }
            // The filter state is true therefore the views should be filtered
            // if our filter is not added we do this now
            if (filterActive.getValue().equals(true) && !alreadyHasFilter) {
                treeViewer.addFilter(FILTER);
            }
        }
        service.refreshElements(command.getId(), null);
    }

    /**
     * resets the states of the command to the default value, which is no filtering
     */
    private void resetCommand() {
        ICommandService service = PlatformUI.getWorkbench()
                .getService(ICommandService.class);
        Command command = service.getCommand(RCPCommandIDs.FILTER_PROFILE);
        State filterActive = command.getState(RegistryToggleState.STATE_ID);
        State profileState = command.getState(FILTER_PROFILE_STATE);
        filterActive.setValue(false);
        profileState.setValue(null);
    }

    @Override
    public void handleChange(ChangeEvent event) {
        Iterator i = OpenOMETracker.INSTANCE.getIterator();
        if (!i.hasNext()) {
            // NO Editor open reset state
            resetCommand();
        } else {
            while (i.hasNext()) {
                checkFilter(getViews((ObjectMappingMultiPageEditor) i.next()));
            }
        }
    }
}
