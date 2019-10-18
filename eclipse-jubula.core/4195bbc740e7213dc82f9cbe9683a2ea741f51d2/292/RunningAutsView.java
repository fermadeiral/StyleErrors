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
package org.eclipse.jubula.client.ui.rcp.views;

import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.IAutRegistrationListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.RunningAutsViewLabelProvider;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;


/**
 * View for visualizing the list of running AUTs managed by the currently
 * connected AUT Agent.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public class RunningAutsView extends ViewPart 
    implements IProjectLoadedListener, IProjectStateListener {

    /** listens for changes in AUT registration */
    private IAutRegistrationListener m_autRegListener;

    /** component to show the currently running AUTs */
    private ListViewer m_runningAutComponent;
    
    /** collection of currently running AUTs */
    private WritableList m_runningAuts;

    /** 
     * The databinding realm. All updates to running AUTs must be executed 
     * from within this realm. 
     */
    private Realm m_viewRealm;
    
    /**
     * 
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        m_runningAuts = new WritableList();
        m_runningAutComponent = new ListViewer(parent);
        m_viewRealm = Realm.getDefault();
        
        ViewerSupport.bind(m_runningAutComponent, m_runningAuts, 
                PojoProperties.value(AutIdentifier.PROP_EXECUTABLE_NAME));
        
        m_autRegListener = new IAutRegistrationListener() {

            @SuppressWarnings("synthetic-access")
            public void handleAutRegistration(
                    final AutRegistrationEvent event) {
                m_viewRealm.exec(new Runnable() {
                    public void run() {
                        switch (event.getStatus()) {
                            case Register:
                                m_runningAuts.add(event.getAutId());
                                break;
                            case Deregister:
                                m_runningAuts.remove(event.getAutId());
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
            
        };

        AutAgentRegistration.getInstance().addListener(m_autRegListener);
        
        getSite().setSelectionProvider(m_runningAutComponent);
        m_runningAutComponent.setLabelProvider(
                new RunningAutsViewLabelProvider());
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectLoadedListener(this, true);
        ded.addProjectStateListener(this);
        
        Plugin.getHelpSystem().setHelp(m_runningAutComponent.getControl(),
                ContextHelpIds.RUNNING_AUTS_VIEW);

        // Create menu manager and menu
        MenuManager menuMgr = new MenuManager();
        Menu menu = menuMgr.createContextMenu(
                m_runningAutComponent.getControl());
        m_runningAutComponent.getControl().setMenu(menu);
        // Register menu for extension.
        getViewSite().registerContextMenu(menuMgr, m_runningAutComponent);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setFocus() {
        m_runningAutComponent.getControl().setFocus();
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        AutAgentRegistration.getInstance().removeListener(m_autRegListener);
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeProjectLoadedListener(this);
        ded.removeProjectStateListener(this);
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        refreshViewer();
    }

    /**
     * refreshes the viewer and the labels
     */
    private void refreshViewer() {
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                m_runningAutComponent.refresh(true);
            }
        });
    }

    /** {@inheritDoc} */
    public void handleProjectStateChanged(ProjectState state) {
        if (ProjectState.prop_modified.equals(state)) {
            refreshViewer();
        }
    }
}
