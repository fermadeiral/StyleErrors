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
package org.eclipse.jubula.client.ui.rcp.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.TestresultState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ProblemsBP;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.rcp.editors.PersistableEditorInput;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class.
 *
 * @author BREDEX GmbH
 * @created 15.02.2005
 */
@SuppressWarnings("synthetic-access")
public class Utils {

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(Utils.class);
    
    /**
     * Constructor
     */
    private Utils() {
        // do nothing
    }
    
    /**
     * @return True, if the server is localhost. False, otherwise.
     */
    public static boolean isLocalhost() {
        IPreferenceStore prefStore = Plugin.getDefault().getPreferenceStore();
        String serverPort = 
                prefStore.getString(Constants.AUT_AGENT_SETTINGS_KEY);
        String server = serverPort.split(StringConstants.COLON)[0];
        return server.equals(Messages.UtilsLocalhost1)
                || server.equals(Messages.UtilsLocalhost3)
                || server.startsWith(Messages.UtilsLocalhost2);
    }
    
    /**
     * Returns the active perspective descriptor or <code>null</code>.
     * 
     * @param activePage
     *            the currently active page - may also be null
     * @return an <code>IPerspectiveDescriptor</code> value. The active
     *         perspective for the currently active page.
     */
    private static IPerspectiveDescriptor getActivePerspective(
            IWorkbenchPage activePage) {
        if (activePage != null) {
            return activePage.getPerspective();
        }
        return null;
    }
    
    /**
     * Opens a perspective with the given ID.
     * @param perspectiveID The ID of the perspective to open.
     * @return True, if the user wants to change the perspective, false otherwise.
     */
    public static boolean openPerspective(String perspectiveID) {
        IWorkbench worbench = PlatformUI.getWorkbench();
        IWorkbenchWindow activeWindow = worbench.getActiveWorkbenchWindow();
        try {
            IPerspectiveDescriptor activePerspective = getActivePerspective(
                    activeWindow.getActivePage());
            if (activePerspective != null
                    && activePerspective.getId().equals(perspectiveID)) {
                return true;
            }
            final IPreferenceStore preferenceStore = Plugin.getDefault()
                    .getPreferenceStore();
            int value = preferenceStore.getInt(Constants.PERSP_CHANGE_KEY);
            if (value == Constants.PERSPECTIVE_CHANGE_YES) {
                worbench.showPerspective(perspectiveID, activeWindow);
                return true;
            } else if (value == Constants.PERSPECTIVE_CHANGE_NO) {
                return true;
            }
            // if --> value = Constants.PERSPECTIVE_CHANGE_PROMPT:
            String perspectiveName = StringConstants.EMPTY;
            if (perspectiveID.equals(Constants.SPEC_PERSPECTIVE)) {
                perspectiveName = Messages.UtilsSpecPerspective;
            } else {
                perspectiveName = Messages.UtilsExecPerspective;
            }
            final int returnCodeYES = 256; // since Eclipse3.2 (not 0)
            final int returnCodeNO = 257; // since Eclipse3.2 (not 1)
            final int returnCodeCANCEL = -1;
            MessageDialogWithToggle dialog = new MessageDialogWithToggle(
                    activeWindow.getShell(), Messages.UtilsTitle, null,
                    NLS.bind(Messages.UtilsQuestion, perspectiveName),
                    MessageDialog.QUESTION, new String[] { Messages.UtilsYes,
                        Messages.UtilsNo }, 0, Messages.UtilsRemember,
                    false) {
                /**
                 * {@inheritDoc}
                 */
                protected void buttonPressed(int buttonId) {
                    super.buttonPressed(buttonId);
                    preferenceStore.setValue(Constants.REMEMBER_KEY,
                            getToggleState());
                    int val = Constants.PERSPECTIVE_CHANGE_PROMPT;
                    if (getToggleState() && getReturnCode() == returnCodeNO) {
                        val = Constants.PERSPECTIVE_CHANGE_NO;
                    } else if (getToggleState()
                            && getReturnCode() == returnCodeYES) {
                        val = Constants.PERSPECTIVE_CHANGE_YES;
                    }
                    preferenceStore.setValue(Constants.PERSP_CHANGE_KEY, val);
                }
            };
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            dialog.open();
            if (dialog.getReturnCode() == returnCodeNO) {
                return true;
            } else if (dialog.getReturnCode() == returnCodeCANCEL) {
                return false;
            }
            worbench.showPerspective(perspectiveID, activeWindow);
        } catch (WorkbenchException e) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.CannotOpenThePerspective)
                    .append(StringConstants.COLON)
                    .append(StringConstants.SPACE).append(perspectiveID)
                    .append(StringConstants.LEFT_PARENTHESIS).append(e)
                    .append(StringConstants.RIGHT_PARENTHESIS)
                    .append(StringConstants.DOT);
            log.error(msg.toString());
            ErrorHandlingUtil.createMessageDialog(MessageIDs.E_NO_PERSPECTIVE);
            return false;
        }
        return true;
    }
    
    /**
     * @return the last browsed path.
     */
    public static String getLastDirPath() {
        return Plugin.getDefault().getPreferenceStore().getString(
            Constants.START_BROWSE_PATH_KEY);
    }
    
    /**
     * Stores the last browsed path.
     * @param path The path to store.
     */
    public static void storeLastDirPath(String path) {
        Plugin.getDefault().getPreferenceStore().setValue(
            Constants.START_BROWSE_PATH_KEY, path);
    }
    
    /**
     * @return A list of all available languages.
     */
    public static List<String> getAvailableLanguages() {
        Languages langUtil = Languages.getInstance();
        java.util.List<String> list = new ArrayList<String>();
        for (Locale locale : langUtil.getSuppLangList()) {
            list.add(langUtil.getDisplayString(locale));
        }    
        return list;
    }
    
    /**
     * clears the content of client
     */
    public static void clearClient() {
        clearClient(false);
    }
    
    /**
     * clears the content of client
     * 
     * @param alsoProjectIndependent
     *            whether also project independent editors should be closed such
     *            as the testresultviewer
     */
    public static void clearClient(final boolean alsoProjectIndependent) {
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        TestExecution.getInstance().stopExecution();
        GeneralStorage gs = GeneralStorage.getInstance();
        if (gs != null && Persistor.instance() != null) {
            IProjectPO currProj = gs.getProject();
            if (currProj != null) {
                gs.nullProject();
            }
            gs.reset();
        }
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                Plugin.clearAllEditorsClipboard();
                final ProblemsBP problemsBP = ProblemsBP.getInstance();
                problemsBP.clearOldProblems();
                problemsBP.cleanupProblems();
                TestExecutionContributor.getInstance().getClientTest()
                        .resetToTesting();
                ded.fireRecordModeStateChanged(RecordModeState.notRunning);
                ded.fireOMStateChanged(OMState.notRunning);
                ded.fireProjectStateChanged(ProjectState.closed);
                Plugin.closeAllOpenedJubulaEditors(alsoProjectIndependent);
                ded.fireTestresultChanged(TestresultState.Clear);
                setTreeViewerInputNull(Constants.TESTRE_ID);
                for (TestCaseBrowser tcb : MultipleTCBTracker.getInstance()
                        .getOpenTCBs()) {
                    tcb.getTreeViewer().setInput(null);
                }
                setTreeViewerInputNull(Constants.TS_BROWSER_ID);
                setTreeViewerInputNull(Constants.COMPNAMEBROWSER_ID);
                clearAnalyzeResultPage();
            }
        });
        ded.fireProjectLoadedListener(new NullProgressMonitor());
    }
    /**
     * Clears the ResultPage of the Analyze-Plugin
     */
    private static void clearAnalyzeResultPage() {

        ISearchQuery[] querry = NewSearchUI.getQueries();
        for (int i = 0; i < querry.length; i++) {
            NewSearchUI.removeQuery(querry[i]);
        }
    }
    /**
     * @param viewID
     *            the id of the view to set it's tree viewer input to null.
     */
    private static void setTreeViewerInputNull(String viewID) {
        IViewPart view = Plugin.getView(viewID);
        if (view instanceof ITreeViewerContainer) {
            ((ITreeViewerContainer)view).getTreeViewer().setInput(null);
        }
    }
    
    /**
     * Returns the IEditorPart for the given node or null if no editor is
     * opened for the given node
     * 
     * @param po
     *            the persistent object of the wanted editor
     * @return the IEditorPart or null if no editor found
     */
    public static IEditorPart getEditorByPO(IPersistentObject po) {
        IEditorReference editorRef = getEditorRefByPO(po);
        if (editorRef != null) {
            return editorRef.getEditor(false);
        }
        return null;
    }
    
    /**
     * Returns the IEditorReference for the given node or null if no editor is
     * opened for the given node
     * 
     * @param po
     *            the persistent object of the wanted editor
     * @return the IEditorReference or null if no editor found
     */
    public static IEditorReference getEditorRefByPO(IPersistentObject po) {
        for (IEditorReference editorRef : Plugin.getAllEditors()) {
            PersistableEditorInput pei = null;
            try {
                pei = editorRef.getEditorInput().getAdapter(
                        PersistableEditorInput.class);
            } catch (PartInitException e) {
                // do nothing here
            }
            if (pei != null && pei.getNode().equals(po)) {
                return editorRef;
            }
        }
        return null;
    }
    
    /**
     * Copies the params of autConfigOrig to autConfigCopy.
     * @param autConfigOrig the orignal autconfig
     * @param autConfigCopy the copy of the original autconfig
     */
    public static void makeAutConfigCopy(Map<String, String> autConfigOrig, 
        Map<String, String> autConfigCopy) {
        
        autConfigCopy.clear();
        final Set<String> autConfigKeys = autConfigOrig.keySet();
        for (String key : autConfigKeys) {
            String value = autConfigOrig.get(key);
            if (value != null && value.length() > 0) {
                autConfigCopy.put(key, value);
            }
        }
    }
    
    /**
     * Converts a given int color to a SWT RGB color object
     * @param intColor the int color
     * @return the RGB color object
     */
    public static RGB intToRgb(int intColor) {
        Color color = new Color(intColor);
        return new RGB(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Converts a given SWT RGB color object to an int color
     * @param color the RGB color object
     * @return the int color
     */
    public static int rgbToInt(RGB color) {
        return new Color(color.red, color.green, color.blue).getRGB();
    }
}