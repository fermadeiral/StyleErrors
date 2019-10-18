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
package org.eclipse.jubula.client.ui.utils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.views.logview.LogView;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class to create a dialog for prompting to open/activate a view
 * 
 * @author BREDEX GmbH
 */
public class OpenViewUtils {
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(OpenViewUtils.class);
    
    /**
     * private constructor
     */
    private OpenViewUtils() {
        // empty
    }
    /**
     * 
     * @param viewId
     *            the id for view to show
     * @param preferenceKey
     *            the key for the preference to save the remembered value to
     */
    public static void showViewPrompt(String viewId,
            final String preferenceKey) {
        IWorkbench worbench = PlatformUI.getWorkbench();
        IWorkbenchWindow activeWindow = worbench.getActiveWorkbenchWindow();
        final IPreferenceStore preferenceStore =
                Plugin.getDefault().getPreferenceStore();
        IViewDescriptor descr = worbench.getViewRegistry().find(viewId);
        String viewName = descr != null ? descr.getLabel() : ""; //$NON-NLS-1$
        int value = preferenceStore.getInt(preferenceKey);
        IWorkbenchPage activePage = activeWindow.getActivePage();
        IViewPart part = activePage.findView(viewId);
        if (part == null || !activePage.isPartVisible(part)) {
            if (value != Constants.UTILS_NO && value != Constants.UTILS_YES) {
                int exitCode = OpenViewUtils.createQuestionDialog(preferenceKey,
                        activeWindow, preferenceStore, viewName);
                if (exitCode == IDialogConstants.YES_ID) {
                    showView(viewId, activePage);
                }
            } else if (value == Constants.UTILS_YES) {
                showView(viewId, activePage);
            }
        }
    }

    /**
     * 
     * @param preferenceKey
     *            the key for the preference to save the remembered value to
     * @param activeWindow
     *            the active {@link IWorkbenchWindow}
     * @param preferenceStore
     *            the instance of the {@link IPreferenceStore}
     * @param viewName
     *            the name of the view to activate
     * @return the return value of the dialog {@link IDialogConstants#NO_ID},
     *         {@link IDialogConstants#YES_ID} or <code>-1</code> if aborted
     */
    private static int createQuestionDialog(final String preferenceKey,
            IWorkbenchWindow activeWindow,
            final IPreferenceStore preferenceStore, String viewName) {
        MessageDialogWithToggle dialog = new MessageDialogWithToggle(
                activeWindow.getShell(), Messages.UtilsOpenViewTitle, null,
                NLS.bind(Messages.UtilsViewQuestion, viewName),
                MessageDialog.QUESTION,
                new String[] { IDialogConstants.YES_LABEL,
                    IDialogConstants.NO_LABEL },
                0, Messages.UtilsRemember, false) {
            /**
             * {@inheritDoc}
             */
            protected void buttonPressed(int buttonId) {
                super.buttonPressed(buttonId);
                int val = Constants.UTILS_PROMPT;
                if (getToggleState()
                        && getReturnCode() == IDialogConstants.NO_ID) {
                    val = Constants.UTILS_NO;
                } else if (getToggleState()
                        && getReturnCode() == IDialogConstants.YES_ID) {
                    val = Constants.UTILS_YES;
                }
                preferenceStore.setValue(preferenceKey, val);
            }
        };
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        int i = dialog.open();
        return i;
    }

    /**
     * 
     * @param viewId
     *            the id for view to show
     * @param activePage
     *            the {@link IWorkbenchPage} in which to show the view
     */
    private static void showView(String viewId, IWorkbenchPage activePage) {
        try {
            activePage.showView(viewId);
        } catch (PartInitException e) {
            LOG.debug("Part init exception during showView", e); //$NON-NLS-1$
        }
    }
    
    /**
     * {@link ISelectionChangedListener} to react on testresultnodes which have
     * a command log
     */
    public static class TestResultNodeSelectionListener 
        implements ISelectionChangedListener {
        /** saving the last selection */
        private ISelection m_lastSelection = null;
        /**
         * {@inheritDoc}
         */
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            if (!selection.equals(m_lastSelection)
                    && selection instanceof IStructuredSelection) {
                IStructuredSelection istruc =
                        (IStructuredSelection) selection;
                Object o = istruc.getFirstElement();
                if (o instanceof TestResultNode) {
                    TestResultNode node = (TestResultNode) o;
                    if (StringUtils.isNotBlank(node.getCommandLog())) {
                        OpenViewUtils.showViewPrompt(
                                LogView.VIEW_ID,
                                Constants.OPEN_LOGVIEW_KEY);
                    }
                }
            }
            m_lastSelection = selection;
        }
    }
}
