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
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.commands.CAPRecordedCommand;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.rcp.editors.TestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Mar 19, 2010
 */
public class StartObservationModeHandler extends AbstractRunningAutHandler {

    /** ID of command parameter for Running AUT to connect to for mapping */
    public static final String RUNNING_AUT = "org.eclipse.jubula.client.ui.rcp.commands.StartObservationModeCommand.parameter.runningAut"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(StartObservationModeHandler.class);
    
    /**
     * @author BREDEX GmbH
     * @created Mar 22, 2010
     */
    private static class StartObservationModeJob extends Job {
        /** test case */
        private ISpecTestCasePO m_workCopy;
        
        /** The ComponentNamesDecorator associated with the 
         *  edit session of the spec test case.
         */
        private IWritableComponentNameCache m_compNamesCache;
        
        /** key modifier to activate/deactivate check mode */
        private int m_checkModeMods;
        
        /** key to activate/deactivate check mode */
        private int m_checkModeKey;
        
        /** key modifier for checking component */
        private int m_checkCompMods;
        
        /** key for checking component */
        private int m_checkCompKey;
        
        /** key modifier for selcting elements */
        private int m_recordCompMods;
        
        /** key for selcting elements */
        private int m_recordCompKey;
        
        /** key modifier for selcting elements */
        private int m_recordApplMods;
        
        /** key for selcting elements */
        private int m_recordApplKey;
        
        /** true if dialog should be open */
        private boolean m_dialogOpen;
        
        /** single line trigger */
        private SortedSet<String> m_singleLineTrigger;
        
        /** multi line trigger */
        private SortedSet<String> m_multiLineTrigger;
        
        /** editor for current observation */
        private TestCaseEditor m_editor;
        
        /** aut id of connected aut */
        private AutIdentifier m_autId;

        /**
         * @param workCopy  SpecTestCasePO
         * @param compNamesCache The Component Names Cache associated with the 
         *                        edit session of the spec test case.
         * @param checkModeMods key modifier to activate/deactivate check mode
         * @param checkModeKey key to activate/deactivate check mode
         * @param checkCompMods key modifier for checking component
         * @param checkCompKey key for checking component
         * @param recordCompMods key modifier for selcting elements
         * @param recordCompKey key for selcting elements
         * @param recordApplMods key modifier for selcting elements
         * @param recordApplKey key for selcting elements
         * @param dialogOpen boolean
         * @param singleLineTrigger SortedSet of single line trigger
         * @param multiLineTrigger SortedSet of multi line trigger
         * @param editor TestCaseEditor
         * @param autId AutIdentifier of connected aut
         */
        public StartObservationModeJob(ISpecTestCasePO workCopy,
                IWritableComponentNameCache compNamesCache,
                int recordCompMods, int recordCompKey, int recordApplMods,
                int recordApplKey, int checkModeMods, int checkModeKey,
                int checkCompMods, int checkCompKey, boolean dialogOpen,
                SortedSet<String> singleLineTrigger,
                SortedSet<String> multiLineTrigger,
                TestCaseEditor editor, AutIdentifier autId) {
            super("Start Observation Mode"); //$NON-NLS-1$
            m_workCopy = workCopy;
            m_compNamesCache = compNamesCache;
            m_recordCompMods = recordCompMods;
            m_recordCompKey = recordCompKey;
            m_recordApplMods = recordApplMods;
            m_recordApplKey = recordApplKey;
            m_checkModeMods = checkModeMods;
            m_checkModeKey = checkModeKey;
            m_checkCompMods = checkCompMods;
            m_checkCompKey = checkCompKey;
            m_dialogOpen = dialogOpen;
            m_singleLineTrigger = singleLineTrigger;
            m_multiLineTrigger = multiLineTrigger;
            m_editor = editor;
            m_autId = autId;
        }

        /**
         * {@inheritDoc}
         */
        protected IStatus run(IProgressMonitor monitor) {
            
            try {
                IStatus connected = AUTConnection.getInstance().connectToAut(
                        m_autId, new NullProgressMonitor());
                if (connected.getCode() == IStatus.OK) {
                    IAUTMainPO aut = TestExecution.getInstance()
                            .getConnectedAut();
                    if (aut == null) {
                        LOG.error(Messages.DisconnectedFromAUT);
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                                Messages.DisconnectedFromAUT);
                    }
                    final String toolkit = aut.getToolkit();
                    TestExecutionContributor.getInstance().getClientTest()
                        .startRecordTestCase(m_workCopy, m_compNamesCache,
                            m_recordCompMods, m_recordCompKey, m_recordApplMods,
                            m_recordApplKey, m_checkModeMods, m_checkModeKey,
                            m_checkCompMods, m_checkCompKey, m_dialogOpen,
                            m_singleLineTrigger, m_multiLineTrigger);
                }
            } catch (CommunicationException ce) {
                LOG.error(ce.getMessage());
                // HERE: notify the listeners about unsuccessfull mode change
            }
            CAPRecordedCommand.setRecordListener(m_editor);
            DataEventDispatcher.getInstance().fireRecordModeStateChanged(
                    RecordModeState.running);
            return Status.OK_STATUS;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean belongsTo(Object family) {
            if (family instanceof StartObservationModeHandler) {
                return true;
            }
            
            return super.belongsTo(family);
        }

    }

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        AutIdentifier runningAut = getRunningAut(event, RUNNING_AUT);
        AbstractRunningAutHandler.setLastAutID(RUNNING_AUT, null);
        if (!Utils.openPerspective(Constants.SPEC_PERSPECTIVE)) {
            return null;
        }
        TestCaseEditor editor;
        if (Plugin.getActiveEditor() instanceof TestCaseEditor) {
            editor = (TestCaseEditor)Plugin.getActiveEditor();
        } else {
            editor = askForNewTC();
        }
        if (editor != null
                && editor.getEditorHelper().requestEditableState() 
                == JBEditorHelper.EditableState.OK) {
            setEditor(editor, runningAut);
            AbstractRunningAutHandler.setLastAutID(RUNNING_AUT, runningAut);
        }
        return null;
    }

    /**
     * ask for a TC Name and returns a new TC Editor
     * 
     * @return SpecTestCaseEditor
     */
    private TestCaseEditor askForNewTC() {
        TestCaseEditor editor = null;
        String standardName = 
            InitialValueConstants.DEFAULT_TEST_CASE_NAME_OBSERVED;
        int index = 1;
        String newName = standardName + index;
        final Set<String> usedNames = new HashSet<String>();
        // generate a unique name
        for (Object node : GeneralStorage.getInstance().getProject()
                .getUnmodSpecList()) {
            if (node instanceof ITestCasePO
                    && ((INodePO) node).getName().startsWith(standardName)) {
                usedNames.add(((INodePO) node).getName());
            }
        }
        while (usedNames.contains(newName)) {
            index++;
            newName = standardName + index;
        }
        InputDialog dialog = createDialog(newName, usedNames);
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.DIALOG_OBS_TC_SAVE);
        dialog.open();
        if (Window.OK == dialog.getReturnCode()) {
            String tcName = dialog.getName();
            final INodePO parentPO = GeneralStorage.getInstance().
                    getProject().getSpecObjCont();
            ISpecTestCasePO recSpecTestCase = NodeMaker
                    .createSpecTestCasePO(tcName);
            try {
                NodePM.addAndPersistChildNode(parentPO, recSpecTestCase, null);
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        recSpecTestCase, DataState.Added, UpdateState.all);
                editor = (TestCaseEditor)AbstractOpenHandler
                        .openEditor(recSpecTestCase);
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleProjectDeletedException();
            }
        }
        dialog.close();
        return editor;
    }

    /**
     * set this editor for recording test case
     * 
     * @param editor SpecTestCaseEditor
     * @param autId AutIdentifier
     */
    private void setEditor(TestCaseEditor editor, AutIdentifier autId) {
        if (editor.getEditorHelper()
                .requestEditableState() != EditableState.OK) {
            editor.getEditorHelper().setDirty(true);
        }
        int recordCompMods = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.RECORDMOD_COMP_MODS_KEY);
        int recordCompKey = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.RECORDMOD_COMP_KEY_KEY);
        int recordApplMods = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.RECORDMOD_APPL_MODS_KEY);
        int recordApplKey = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.RECORDMOD_APPL_KEY_KEY);

        int checkModeMods = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.CHECKMODE_MODS_KEY);
        int checkModeKey = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.CHECKMODE_KEY_KEY);
        int checkCompMods = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.CHECKCOMP_MODS_KEY);
        int checkCompKey = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.CHECKCOMP_KEY_KEY);
        boolean dialogOpen = Plugin.getDefault().getPreferenceStore()
                .getBoolean(Constants.SHOWRECORDDIALOG_KEY);

        SortedSet<String> singleLineTrigger = new TreeSet<String>();
        SortedSet<String> multiLineTrigger = new TreeSet<String>();
        try {
            singleLineTrigger = 
                org.eclipse.jubula.client.ui.preferences.utils.Utils
                    .decodeStringToSet(Plugin.getDefault().getPreferenceStore()
                            .getString(Constants.SINGLELINETRIGGER_KEY),
                            StringConstants.SEMICOLON);
            multiLineTrigger = 
                org.eclipse.jubula.client.ui.preferences.utils.Utils
                    .decodeStringToSet(Plugin.getDefault().getPreferenceStore()
                            .getString(Constants.MULTILINETRIGGER_KEY),
                            StringConstants.SEMICOLON);
        } catch (JBException e) {
            e.printStackTrace();
        }

        final ISpecTestCasePO workCopy = 
            (ISpecTestCasePO)editor.getEditorHelper().getEditSupport()
                .getWorkVersion();
        final IWritableComponentNameCache compNamesCache =
                editor.getCompNameCache();
        
        Job startObservationModeJob = new StartObservationModeJob(
                workCopy, compNamesCache,
                recordCompMods, recordCompKey, recordApplMods,
                recordApplKey, checkModeMods, checkModeKey,
                checkCompMods, checkCompKey, dialogOpen,
                singleLineTrigger, multiLineTrigger,
                editor, autId);
        startObservationModeJob.setSystem(true);
        JobUtils.executeJob(startObservationModeJob, null);
    }

    /**
     * @param newName
     *            new name
     * @param usedNames
     *            used name
     * @return input dialog
     */
    private InputDialog createDialog(String newName,
            final Set<String> usedNames) {
        InputDialog dialog = new InputDialog(getActiveShell(), 
                Messages.RecordTestCaseActionTCTitle,
                newName, Messages.RecordTestCaseActionTCMessage,
                Messages.RecordTestCaseActionTCLabel,
                Messages.RenameActionTCError,
                Messages.RecordTestCaseActionDoubleTCName,
                IconConstants.OBSERVE_TC_DIALOG_STRING, 
                Messages.RecordTestCaseActionTCShell,
                false) {
            protected boolean isInputAllowed() {
                if (usedNames.contains((getInputFieldText()))) {
                    return false;
                }
                return super.isInputAllowed();
            }
        };
        return dialog;
    }
    
    /** {@inheritDoc} */
    protected String getKey() {
        return RUNNING_AUT;
    }


}
