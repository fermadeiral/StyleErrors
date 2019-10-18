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
package org.eclipse.jubula.client.ui.rcp.events;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Jun 7, 2010
 */
public class GuiEventDispatcher {
    
    /** Multiplicator milliseconds to minutes. */
    private static final int MS_TO_MIN_MULTIPLICATOR = 60000;

    /** to notify clients about change of the dirty state of an editor */
    public interface IEditorDirtyStateListener {
        /**
         * callback method
         * @param editor The editor whose dirty state changed
         * @param isDirty The new dirty state
         */
        public void handleEditorDirtyStateChanged(IJBEditor editor, 
                boolean isDirty);
    }
    
    /** logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(GuiEventDispatcher.class);
    

    /**
     * <code>m_instance</code> singleton
     */
    private static GuiEventDispatcher instance = null;

    /**
     * <code>m_editorDirtyStateListeners</code> listener for notification 
     * about change of the dirty state of an editor
     */
    private Set<IEditorDirtyStateListener> m_editorDirtyStateListeners =
        new HashSet<IEditorDirtyStateListener>();
    /**
     * <code>m_editorDirtyStateListenersPost</code> listener for notification 
     * about change of the dirty state of an editor
     *  POST-Event for gui updates 
     */
    private Set<IEditorDirtyStateListener> m_editorDirtyStateListenersPost =
        new HashSet<IEditorDirtyStateListener>();
    
    /**
     * 
     */
    private ScheduledExecutorService m_dirtyScheduler =
            Executors.newSingleThreadScheduledExecutor();

    /**
     * The timestamp when the last save occurred.
     */
    private volatile long m_dirtyTimestamp = System.currentTimeMillis();

    /**
     * If the timer for the save reminder is activated.
     */
    private volatile boolean m_dirtyTimer = false;

    /**
     * private constructor
     */
    private GuiEventDispatcher() {
        m_dirtyScheduler = Executors.newSingleThreadScheduledExecutor();
        m_dirtyScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long timer = m_dirtyTimestamp
                        + (Plugin.getDefault().getPreferenceStore()
                                .getInt(Constants.SAVE_REMINDER_INTERVAL_KEY)
                                * MS_TO_MIN_MULTIPLICATOR);
                boolean enabled = Plugin.getDefault().getPreferenceStore()
                        .getBoolean(Constants.SAVE_REMINDER_ENABLE_KEY);
                if (System.currentTimeMillis() > timer && enabled
                        && m_dirtyTimer) {
                    showSaveReminder();
                    updateDirtyTimer(false);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Shows the reminder pop-up.
     */
    private void showSaveReminder() {
        final String message = NLS.bind(Messages.EditorSaveReminder,
                Plugin.getDefault().getPreferenceStore()
                        .getInt(Constants.SAVE_REMINDER_INTERVAL_KEY));
        Plugin.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog
                        .openWarning(
                                PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                "Warning", message); //$NON-NLS-1$
            }
        });
    }
    
    /**
     * @return the single instance
     */
    public static synchronized GuiEventDispatcher getInstance() {
        if (instance == null) {
            instance = new GuiEventDispatcher();
        }
        return instance;
    }

    /**
     * @param l The listener to add as new listener for notification 
     * about change of the dirty state of an editor
     * @param guiMode
     *      should this listener be called after the model listener 
     */
    public void addEditorDirtyStateListener(
        IEditorDirtyStateListener l, boolean guiMode) {
        if (guiMode) {
            m_editorDirtyStateListenersPost.add(l);
        } else {
            m_editorDirtyStateListeners.add(l);
        }
    }
    
    /**
     * @param l The listener to be deleted as listener for notification 
     * about change of the dirty state of an editor
     */
    public void removeEditorDirtyStateListener(IEditorDirtyStateListener l) {
        m_editorDirtyStateListeners.remove(l);
        m_editorDirtyStateListenersPost.remove(l);
        // scheduler update
        updateDirtyTimer(false);
    }
    
    /**
     * notify listener about change of the dirty state of an editor
     * @param editor The editor whose dirty state changed
     * @param isDirty The new dirty state
     */
    public void fireEditorDirtyStateListener(IJBEditor editor, 
            boolean isDirty) {
        // model updates
        final Set<IEditorDirtyStateListener> stableListeners = 
            new HashSet<IEditorDirtyStateListener>(m_editorDirtyStateListeners);
        for (IEditorDirtyStateListener l : stableListeners) {
            try {
                l.handleEditorDirtyStateChanged(editor, isDirty);
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionCallingListeners, t);
            }
        }

        // gui updates
        final Set<IEditorDirtyStateListener> stableListenersPost = 
                new HashSet<IEditorDirtyStateListener>(
                m_editorDirtyStateListenersPost);
        for (IEditorDirtyStateListener l : stableListenersPost) {
            try {
                l.handleEditorDirtyStateChanged(editor, isDirty);
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionCallingListeners, t);
            }
        }

        // scheduler update
        updateDirtyTimer(isDirty);
    }

    /**
     * 
     * @param dirty
     *            determines if new dirty editors exist
     */
    private synchronized void updateDirtyTimer(boolean dirty) {
        if (dirty && !m_dirtyTimer) {
            this.m_dirtyTimestamp = System.currentTimeMillis();
        }
        m_dirtyTimer = dirty;
    }

}
