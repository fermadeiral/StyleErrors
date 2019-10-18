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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.rcp.dialogs.ClassPathDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite with a file and directory browser list, where directory and file
 * pathes can be added, edited, removed.
 *
 * @author BREDEX GmbH
 * @created Jan 4, 2016
 */
@SuppressWarnings("synthetic-access")
public class FileDirectoryBrowser extends BaseMultiBrowserComposite {
    
    /**
     * Listener for the file or directory selection changes.
     *
     * @author BREDEX GmbH
     * @created Jan 4, 2016
     */
    public interface IFileDirectorySelectionListener {
        /**
         * Handle a change, when a directory or a file is added, edited, or removed
         * @param keyName name of the configuration
         * @param value value, which contains the selected directories.
         */
        public void selectedFileDirectoriesChanged(String keyName,
                String value);
        
        /**
         * Handle, whether the the browse is allowed or not.
         * @return true if the browse is allowed, else false
         */
        public boolean isBrowseable();
    }

    /** list listeners */
    private Set<IFileDirectorySelectionListener> m_listeners = 
        new HashSet<IFileDirectorySelectionListener>();
    
    /**
     * Only files with one of the provided extensions will be shown in the
     * dialog. May be <code>null</code>, in which case all files will be shown.
     */
    private String[] m_filterExtensions;
    
    /**
     * Title of the file or directory selection dialog.
     */
    private String m_selectDialogTitle;
    
    /** true, if only file selection is allowed, false if directory selection is allowed too*/
    private boolean m_fileSelectionAllowed;

    /**
     * Multi-directory browser with a list and add, edit, remove buttons.
     * 
     * @param parent
     *            The parent composite
     * @param selectDialogTitle
     *            Title of the file or directory selection dialog.
     * @param attrId
     *            monitoring constant id
     * @param configurationValue
     *            value, which contains the current configuration
     * @param extensionFilters
     *            Only files with one of the provided extensions will be shown
     *            in the dialog. May be <code>null</code>, in which case all
     *            files will be shown.
     * @param fileSelectionAllowed true, if only file selection is allowed, 
     * false if directory selection
     */
    public FileDirectoryBrowser(Composite parent, String selectDialogTitle,
            String attrId, String configurationValue,
            String[] extensionFilters, boolean fileSelectionAllowed) {
        super(parent, attrId, configurationValue);
        this.m_filterExtensions = extensionFilters;
        this.m_selectDialogTitle = selectDialogTitle;
        this.m_fileSelectionAllowed = fileSelectionAllowed;
    }

    /**
     * Handle selection of directory/file
     * 
     * @param isEditButtonPressed
     *            if true, this is modifying a current path, if false, it is
     *            addition for a new path
     */
    void handleSelection(boolean isEditButtonPressed) {
        int maxLength = IPersistentObject.MAX_STRING_LENGTH
                - getItemsLength();
        if (maxLength < 1) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_TOO_LONG_CLASSPATH,
                    new Object[] { IPersistentObject.MAX_STRING_LENGTH }, null);
            return;
        }

        String oldItemPath = StringConstants.EMPTY;
        if (isEditButtonPressed) {
            oldItemPath = getItemList().getSelection()[0];
        }
        
        ClassPathDialog dialog = new ClassPathDialog(getShell(),
                this.m_selectDialogTitle, oldItemPath,
                Messages.AUTConfigComponentMessage,
                Messages.AUTConfigComponentLabel,
                Messages.AUTConfigComponentWrongInputMessage,
                StringConstants.EMPTY,
                null,
                Messages.AUTConfigComponentShellText, false, maxLength,
                getIsBrowseable(), m_filterExtensions, m_fileSelectionAllowed);
        dialog.setStyle(SWT.TITLE);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        if (dialog.getReturnCode() == Window.OK) {
            String[] elements = dialog.getName().split(System.getProperty("path.separator")); //$NON-NLS-1$
            for (int i = 0; i < elements.length; i++) {
                String element = elements[i];
                if (element == null) {
                    continue;
                }
                
                if (isEditButtonPressed) {
                    getItemList().remove(oldItemPath);
                    getItemList().add(element);
                } else {
                    getItemList().add(element);
                }
            }
            updateStoredValues();
        } 
    }

    /**
     * invoke the listeners, which are responsible to persist changes.
     */
    void updateStoredValues() {
        fireListChanged(
                String.valueOf(getItemList()
                        .getData(MonitoringConstants.MONITORING_KEY)),
                getItemPathes());
    }

    /**
     * Informs all listeners that the directory or file list has been modified.
     * 
     * @param key
     *            name of the configuration
     * @param value
     *            value, which contains the selected directories.
     */
    private void fireListChanged(String key, String value) {
        for (IFileDirectorySelectionListener listener : m_listeners) {
            listener.selectedFileDirectoriesChanged(key, value);
        }
    }
    
    /**
     * Check whether the browse is allowed, or not.
     * @return true if browseable, else fale
     */
    private boolean getIsBrowseable() {
        for (IFileDirectorySelectionListener listener : m_listeners) {
            return listener.isBrowseable();
        }
        return false;
    }

    /**
     * 
     * @param listener
     *            The listener to add.
     */
    public void addListModifiedListener(
            IFileDirectorySelectionListener listener) {
        m_listeners.add(listener);
    }
}
