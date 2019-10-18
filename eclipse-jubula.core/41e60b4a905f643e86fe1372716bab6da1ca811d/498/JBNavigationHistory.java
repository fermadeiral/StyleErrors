/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.events.GuiEventDispatcher;
import org.eclipse.jubula.client.ui.rcp.events.GuiEventDispatcher.IEditorDirtyStateListener;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.sourceprovider.NavigationSourceProvider;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Own Navigation History implementation - listens to selection
 *   changes in the workbench, thus keeping track of editors
 * @author BREDEX GmbH
 */
public class JBNavigationHistory implements ISelectionListener,
        IEditorDirtyStateListener {

    /** The maximum number of displayed history entries */
    private static final int MAX_DISPLAYED = 10;

    /** The maximum number of stored history entries */
    private static final int MAX_STORED = 50;

    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            JBNavigationHistory.class);

    /** The instance */
    private static JBNavigationHistory instance = null;

    /** The navigation history */
    private List<NavHistEntry> m_navHistory = new ArrayList<>();

    /** The current navigation entry */
    private int m_current = -1;

    /** List of providers */
    private List<NavigationSourceProvider> m_providers = new ArrayList<>();

    /** A navigation history entry */
    private class NavHistEntry {
        /** The edited PO - identifies the editor */
        private IPersistentObject m_po;
        /** Whether the PO was edited */
        private boolean m_edited = false;
        /** @param po the po */
        private NavHistEntry(IPersistentObject po) {
            m_po = po;
        }
    }

    /** The private constructor */
    private JBNavigationHistory() {
        // empty
    }

    /**
     * @return the instance
     */
    public static synchronized JBNavigationHistory getInstance() {
        if (instance == null) {
            instance = new JBNavigationHistory();
            GuiEventDispatcher.getInstance().addEditorDirtyStateListener(
                    instance, false);
        }
        return instance;
    }

    /**
     * @param prov a source provider
     */
    public synchronized void addProvider(NavigationSourceProvider prov) {
        m_providers.add(prov);
    }

    /**
     * @param prov a source provider to remove
     */
    public synchronized void removeProvider(NavigationSourceProvider prov) {
        m_providers.remove(prov);
    }

    /** Fires the providers */
    private void fireProviders() {
        boolean canBack = m_current > 0;
        boolean canForward = m_current < m_navHistory.size() - 1;
        boolean canEditBack = false;
        for (int i = 0; i < m_current; i++) {
            if (m_navHistory.get(i).m_edited) {
                canEditBack = true;
                break;
            }
        }
        boolean canEditForw = false;
        for (int i = m_current + 1; i < m_navHistory.size(); i++) {
            if (m_navHistory.get(i).m_edited) {
                canEditForw = true;
                break;
            }
        }
        for (NavigationSourceProvider prov : m_providers) {
            prov.fireSourceChanged(canBack, canForward,
                    canEditBack, canEditForw);
        }
    }

    /** {@inheritDoc} */
    public synchronized void selectionChanged(
            IWorkbenchPart part, ISelection selection) {
        try {
            IPersistentObject ob = null;
            if (part instanceof AbstractJBEditor) {
                ob = ((AbstractJBEditor) part).getEditorHelper().
                        getEditSupport().getOriginal();
            } else if (part instanceof ObjectMappingMultiPageEditor) {
                ob = ((ObjectMappingMultiPageEditor) part).getEditorHelper().
                        getEditSupport().getOriginal();
            }
            if (ob == null || (!m_navHistory.isEmpty()
                    && ob.equals(m_navHistory.get(m_current).m_po))) {
                // irrelevant active part, or the same as the current
                return;
            }
            addHistoryEntry(ob);
        } catch (Exception e) {
            // we really don't want any exception in a SelectionListener...
            LOG.error(e.getLocalizedMessage(), e);
            m_navHistory.clear();
            m_current = -1;
        }
        fireProviders();
    }

    /**
     * Adds a new history entry
     * @param po the edited object
     */
    private void addHistoryEntry(IPersistentObject po) {
        int size = m_navHistory.size();
        if (size > 0 && ObjectUtils.equals(
                po, m_navHistory.get(size - 1).m_po)) {
            // No entry duplication
            return;
        }
        if (size == MAX_STORED) {
            m_navHistory.remove(0);
        }
        m_navHistory.add(new NavHistEntry(po));
        m_current = m_navHistory.size() - 1;
    }

    /**
     * Moves back- or forward, restoring an editor if necessary
     * @param pos the position
     * @param absolute whether pos is an actual position (as opposed to a direction)
     */
    public synchronized void move(int pos, boolean absolute) {
        int newPos;
        if (absolute) {
            newPos = pos;
        } else {
            newPos = m_current + pos;
        }
        if (!isValidPos(newPos)) {
            return;
        }
        setPosition(newPos);
    }

    /**
     * Sets the current navigation entry position
     * @param pos the position
     */
    private void setPosition(int pos) {
        int save = m_current;
        // Required - the openEditor fires a SelectionChanged
        // before commencing, and we catch that...
        m_current = pos;
        if (AbstractOpenHandler.openEditor(
                m_navHistory.get(pos).m_po) == null) {
            // failed opening the editor - probably the po was removed
            // probably the entries get a bit confused here, but that's
            // not a very big problem
            m_current = save;
            m_navHistory.remove(pos);
            if (m_current > m_navHistory.size()) {
                m_current = m_navHistory.size() - 1;
            } else if (m_current > pos) {
                m_current--;
            }
        } else {
            m_current = pos;
        }
        fireProviders();
    }

    /**
     * Moves back- or forward to an edited Editor
     * @param direction the direction (-1 or 1)
     */
    public synchronized void editedMove(int direction) {
        int start = m_current + direction;
        int end = direction == 1 ? m_navHistory.size() - 1 : 0;
        if (!(isValidPos(start) && isValidPos(end))) {
            return;
        }
        int safe = 0;
        for (int i = start; i != end + direction; i += direction) {
            safe++;
            if (safe == 1000) {
                LOG.error("Infinite loop!"); //$NON-NLS-1$
            }
            if (m_navHistory.get(i).m_edited) {
                setPosition(i);
                return;
            }
        }
    }

    /**
     * Checks whether the history position is valid
     * @param pos the position
     * @return whether valid
     */
    public synchronized boolean isValidPos(int pos) {
        return pos >= 0 && pos < m_navHistory.size();
    }

    /**
     * Returns the list of backward / forward, edited / all history entries
     * @param backwards whether going backwards
     * @param edit whether we are interested only in edited positions
     * @return the ordered map of (position => title) entries
     */
    public synchronized SortedMap<Integer, String> getContribItems(
            boolean backwards, boolean edit) {
        SortedMap<Integer, String> res = new TreeMap<>();
        int start;
        int end;
        if (backwards) {
            start = 0;
            end = m_current - 1;
        } else {
            start = m_current + 1;
            end = m_navHistory.size() - 1;
        }
        if (!(isValidPos(start) && isValidPos(end))) {
            // something went wrong with the indices, so we return the empty map
            return res;
        }
        int num = 0;
        for (int i = start; i <= end; i++) {
            if (!edit || m_navHistory.get(i).m_edited) {
                IPersistentObject po = m_navHistory.get(i).m_po;
                String name;
                if (po instanceof ITestDataCategoryPO) {
                    name = "Central Test Data"; //$NON-NLS-1$
                } else if (po instanceof IAUTMainPO) {
                    name = "OM / " + po.getName(); //$NON-NLS-1$
                } else {
                    name = po.getName();
                }
                res.put(i, name);
                num++;
                if (num > MAX_DISPLAYED) {
                    break;
                }
            }
        }
        return res;
    }

    @Override
    public synchronized void handleEditorDirtyStateChanged(IJBEditor editor,
            boolean isDirty) {
        if (m_current < 0 || m_current >= m_navHistory.size()) {
            // sometimes an Editor can become dirty before the history is filled
            // (e.g. when we open an OME which becomes dirty instantly due
            // to to-be-mapped CNs...)
            // in such a case an exception would be thrown, so we just quit instead
            return;
        }
        NavHistEntry curr = m_navHistory.get(m_current);
        IPersistentObject editedPo = editor.getEditorHelper().
                getEditSupport().getWorkVersion();
        if (isDirty && ObjectUtils.equals(editedPo, curr.m_po)) {
            curr.m_edited = true;
            fireProviders();
        }
    }

}