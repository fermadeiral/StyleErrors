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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping.OMEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.IEditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for starting the Object Mapping Mode
 *
 * @author BREDEX GmbH
 * @created Mar 2, 2010
 */
public class StartObjectMappingModeHandler extends AbstractRunningAutHandler {

    /** ID of command parameter for Running AUT to connect to for mapping */
    public static final String RUNNING_AUT = 
        "org.eclipse.jubula.client.ui.rcp.commands.OMStartMappingModeCommand.parameter.runningAut"; //$NON-NLS-1$
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(StartObjectMappingModeHandler.class);
    
    /**
     * Job that performs the actual starting of the Object Mapping Mode.
     *
     * @author BREDEX GmbH
     * @created Mar 18, 2010
     */
    private static class StartObjectMappingModeJob extends Job {

        /** the editor associated with the Object Mapping Mode activation */
        private ObjectMappingMultiPageEditor m_editor;
        
        /** 
         * the ID of the Running AUT associated with the  ObjectMapping Mode 
         * activation 
         */
        private AutIdentifier m_autId;
        
        /** the code for the modifier key(s) used to collect UI Elements */
        private int m_modifier;
        
        /** the code for the key used to collect UI Elements */
        private int m_key;

        /** the code for the modifier key(s) used to collect UI Elements and their parents */
        private int m_modifierWithParents;
        
        /** the code for the key used to collect UI Elements and their parents */
        private int m_keyWithParents;
        
        /** the category into which collected UI Elements will be collected */
        private IObjectMappingCategoryPO m_category;
        
        /** 
         * the type of input action that will collect a UI Element
         * 
         * @see {@link org.eclipse.jubula.tools.internal.constants.InputConstants}
         */
        private int m_type;
        
        /** 
         * the type of input action that will collect a UI Element and its parents
         * 
         * @see {@link org.eclipse.jubula.tools.internal.constants.InputConstants}
         */
        private int m_typeWithParents;

        /**
         * Constructor
         * 
         * @param editor    The editor associated with the Object Mapping Mode 
         *                  activation.
         * @param autId The ID of the Running AUT associated with the 
         *              Object Mapping Mode activation. 
         * @param modifier  The code for the modifier key(s) used to collect 
         *                  UI Elements.
         * @param key   The code for the key used to collect UI Elements.
         * @param category  The category into which collected UI Elements will 
         *                  be collected.
         * @param type  The type of input action that will collect a UI Element.
         * @param modifierWP  The code for the modifier key(s) used to collect 
         *                  UI Elements and their parents.
         * @param keyWP   The code for the key used to collect UI Elements
         *                  and their parents.
         * @param typeWP  The type of input action that will collect a UI Element
         *                  and its parents.
         * @see {@link org.eclipse.jubula.tools.internal.constants.InputConstants}
         */
        public StartObjectMappingModeJob(ObjectMappingMultiPageEditor editor, 
                AutIdentifier autId, int modifier, int key, 
                IObjectMappingCategoryPO category, int type,
                int modifierWP, int keyWP, int typeWP) {
            super("Start Object Mapping Mode"); //$NON-NLS-1$
            m_editor = editor;
            m_autId = autId;
            m_modifier = modifier;
            m_key = key;
            m_category = category;
            m_type = type;
            m_modifierWithParents = modifierWP;
            m_keyWithParents = keyWP;
            m_typeWithParents = typeWP;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public IStatus run(IProgressMonitor monitor) {
            try {
                TestExecutionContributor.getInstance()
                    .getClientTest().startObjectMapping(
                        m_autId, m_modifier, m_key, m_type,
                        m_modifierWithParents, m_keyWithParents,
                        m_typeWithParents);
                DataEventDispatcher.getInstance()
                    .fireOMStateChanged(OMState.running);
                DataEventDispatcher.getInstance()
                    .fireOMAutChanged(m_autId);
                ObjectMappingEventDispatcher.setCategoryToCreateIn(
                        m_category);
                m_editor.getOmEditorBP().setCategoryToCreateIn(m_category);
            } catch (CommunicationException ce) {
                LOG.error(Messages.ErrorStartingObjectMappingMode, ce);
            }

            return Status.OK_STATUS;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean belongsTo(Object family) {
            if (family instanceof StartObjectMappingModeHandler) {
                return true;
            }
            
            return super.belongsTo(family);
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        AutIdentifier runningAut = getRunningAut(event, RUNNING_AUT);
        
        IAUTMainPO mappingAut = AutAgentRegistration.getAutForId(
                runningAut, GeneralStorage.getInstance().getProject());
            
        ObjectMappingMultiPageEditor omEditor = null;
        if (!(Plugin.getActiveEditor() instanceof ObjectMappingMultiPageEditor)
                && Utils.getEditorByPO(mappingAut) != null) {

            IEditorPart toActivate = Utils.getEditorByPO(mappingAut);
            toActivate.getSite().getPage().activate(toActivate);
            if (toActivate instanceof ObjectMappingMultiPageEditor) {
                omEditor = (ObjectMappingMultiPageEditor)toActivate;
            }
        }
        if (omEditor == null) {
            AbstractOpenHandler.openEditor(mappingAut);
            IEditorPart activeEditor = Plugin.getActiveEditor();
            if (activeEditor instanceof ObjectMappingMultiPageEditor) {
                omEditor = (ObjectMappingMultiPageEditor)activeEditor;
            }
        }

        if (omEditor == null) {
            if (runningAut == null) {
                LOG.error(Messages.CouldNotOpenOMEditorNoAUT);
            } else {
                LOG.error(Messages.CouldNotOpenOMEditorForAUT 
                        + StringConstants.COLON + StringConstants.SPACE
                        + runningAut.getExecutableName());
            }
            AbstractRunningAutHandler.setLastAutID(RUNNING_AUT, null);
            return null;
        }

        AbstractRunningAutHandler.setLastAutID(RUNNING_AUT, runningAut);
        IObjectMappingCategoryPO unMappedTech = 
            omEditor.getAut().getObjMap().getUnmappedTechnicalCategory();

        // FIXME zeb: necessary to have this here until our 
        //            command/event/enablement story is more stable
        //            (it prevents clicking multiple times on the 
        // "Start Object Mapping Mode" button)
        DataEventDispatcher.getInstance().fireAutServerConnectionChanged(
                ServerState.Connecting);
        
        startMappingMode(runningAut, omEditor, unMappedTech);
        
        return null;
    }

    /**
     * @param autId The ID of the AUT for which to start the 
     *              Object Mapping Mode.
     * @param editor the actual om-editor
     * @param unmappedTechNames the top-level category for unmapped 
     *                          Technical Names
     */
    private void startMappingMode(AutIdentifier autId, 
            ObjectMappingMultiPageEditor editor, 
            IObjectMappingCategoryPO unmappedTechNames) {
        
        IObjectMappingCategoryPO category = null;
        TreeViewer treeViewer = editor.getTreeViewer();
        if (treeViewer != null && ((treeViewer.getSelection() 
                instanceof IStructuredSelection)
                || (treeViewer.getContentProvider() 
                        instanceof ITreeContentProvider))) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer
                    .getSelection();
            Object node;
            if (selection.size() == 1) {
                node = selection.getFirstElement();
                if (node instanceof IObjectMappingCategoryPO
                        && OMEditorDndSupport.getSection(
                                (IObjectMappingCategoryPO) node).equals(
                                unmappedTechNames)) {

                    category = (IObjectMappingCategoryPO) node;
                } else if (node instanceof IObjectMappingAssoziationPO
                        && OMEditorDndSupport.getSection(
                                (IObjectMappingAssoziationPO) node).equals(
                                unmappedTechNames)) {

                    category = ((IObjectMappingAssoziationPO) node)
                            .getCategory();
                }

                if (category != null) {
                    editor.getOmEditorBP().setCategoryToCreateIn(category);
                } else {
                    ObjectMappingEventDispatcher.setCategoryToCreateIn(null);
                }
            }
        }
        if (!AutAgentRegistration.getInstance().getRegisteredAuts()
                .contains(autId)) {
            String message = Messages.OMStartMappingModeActionError1;
            ErrorHandlingUtil.createMessageDialog(new JBException(message, 
                    MessageIDs.E_UNEXPECTED_EXCEPTION), null, new String[]{
                        message});
        } else {
            IPreferenceStore preferenceStore = Plugin.getDefault()
                    .getPreferenceStore();
            int mappingMod = preferenceStore.getInt(
                    Constants.MAPPING_MOD_KEY);
            int mappingKey = preferenceStore.getInt(
                    Constants.MAPPING_TRIGGER_KEY);
            int mappingType = preferenceStore
                    .getInt(Constants.MAPPING_TRIGGER_TYPE_KEY);
            int mappingWPMod = preferenceStore.getInt(
                    Constants.MAPPING_WITH_PARENTS_MOD_KEY);
            int mappingWPKey = preferenceStore.getInt(
                    Constants.MAPPING_WITH_PARENTS_TRIGGER_KEY);
            int mappingWPType = preferenceStore
                    .getInt(Constants.MAPPING_WITH_PARENTS_TRIGGER_TYPE_KEY);
            final String toolkit = editor.getAut().getToolkit();

            Job startObjectMappingModeJob = new StartObjectMappingModeJob(
                    editor, autId, mappingMod, mappingKey, category,
                    mappingType, mappingWPMod, mappingWPKey, mappingWPType);
            startObjectMappingModeJob.setSystem(true);
            JobUtils.executeJob(startObjectMappingModeJob, null);
        }
    }

    /** {@inheritDoc} */
    protected String getKey() {
        return RUNNING_AUT;
    }

}