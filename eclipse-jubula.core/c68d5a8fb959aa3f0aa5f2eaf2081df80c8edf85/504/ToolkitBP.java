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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP.ToolkitPluginError;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP.ToolkitPluginError.ERROR;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.IUsedToolkitPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;


/**
 * Class to handle / control toolkit settings.
 *
 * @author BREDEX GmbH
 * @created 09.07.2007
 */
public class ToolkitBP implements IProjectLoadedListener, IDataChangedListener {

    /**
     * The singleton instance
     */
    private static ToolkitBP instance = null;
    
    
    /**
     * Constructor
     */
    private ToolkitBP() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectLoadedListener(this, true);
        ded.addDataChangedListener(this, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            UsedToolkitBP.getInstance().getToolkitLevel(project);            
        }
    }
    
    /**
     * 
     * @return the singleton instance.s
     */
    public static ToolkitBP getInstance() {
        if (instance == null) {
            instance = new ToolkitBP();
        }
        return instance;
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        for (DataChangedEvent e : events) {
            
            if (e.getPo() instanceof INodePO 
                && (DataState.StructureModified == e.getDataState())) {
                
                final INodePO node = (INodePO)e.getPo();
                if (e.getPo() instanceof ISpecTestCasePO) {
                    UsedToolkitBP.getInstance().updateToolkitLevel(
                        node, node.getToolkitLevel());
                }
            }
        }
    }
    
    /**
     * Showing Info Message if loading old project
     * @param usedToolkits toolkits used in given project
     * @return true if project can be loaded, false otherwise.
     */
    public boolean checkXMLVersion(Set<IUsedToolkitPO> usedToolkits) {
    
        final List<ToolkitPluginError> errors = UsedToolkitBP.getInstance()
            .checkUsedToolkitPluginVersions(usedToolkits);
        if (errors.isEmpty()) {
            return true;
        }
        boolean loadProject = true;
        Integer messageID = null;
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(Messages.OpenProjectActionToolkitVersionConflict1);
        for (ToolkitPluginError error : errors) {
            String toolkitId = error.getToolkitId();
            ToolkitDescriptor desc = 
                ComponentBuilder.getInstance().getCompSystem()
                .getToolkitDescriptor(toolkitId);
            String toolkitName = desc != null ? desc.getName() : toolkitId;
            strBuilder.append(Messages.OpenProjectActionToolkitVersionConflict2)
                .append(toolkitName)
                .append(Messages.OpenProjectActionToolkitVersionConflict3);
            
            final ERROR errorType = error.getError();
            final String descr = 
                Messages.OpenProjectActionToolkitVersionConflict5;
            switch (errorType) {
                case MAJOR_VERSION_ERROR:
                    messageID = MessageIDs
                        .E_LOAD_PROJECT_TOOLKIT_MAJOR_VERSION_ERROR;
                    strBuilder.append(Messages
                            .OpenProjectActionToolkitVersionConflict4a);
                    strBuilder.append(descr);
                    loadProject = false;
                    break;
    
                case MINOR_VERSION_HIGHER:
                    messageID = MessageIDs
                        .E_LOAD_PROJECT_TOOLKIT_MAJOR_VERSION_ERROR;
                    strBuilder.append(Messages
                            .OpenProjectActionToolkitVersionConflict4b);
                    strBuilder.append(descr);
                    loadProject = false;
                    break;
                    
                case MINOR_VERSION_LOWER:
                    if (loadProject) { // do not overwrite if already false!
                        messageID = MessageIDs
                            .Q_LOAD_PROJECT_TOOLKIT_MINOR_VERSION_LOWER;
                    }
                    strBuilder.append(Messages
                            .OpenProjectActionToolkitVersionConflict4c);
                    break;
                    
                default:
                    Assert.notReached(Messages.UnknownErrorType 
                        + StringConstants.COLON + StringConstants.SPACE
                        + String.valueOf(errorType));
            }
        }
        String[] details = null;
        if (!messageID.equals(MessageIDs
            .Q_LOAD_PROJECT_TOOLKIT_MINOR_VERSION_LOWER)) {
            
            details = new String[]{strBuilder.toString()};                
        }
        final Dialog dialog = ErrorHandlingUtil.createMessageDialog(
                messageID, null, details);
        if (dialog.getReturnCode() == Window.CANCEL) {
            loadProject = false;
        }
        return loadProject;
    }

}
