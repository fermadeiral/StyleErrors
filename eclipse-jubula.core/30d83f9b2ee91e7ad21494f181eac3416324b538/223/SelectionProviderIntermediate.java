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
package org.eclipse.jubula.client.ui.rcp.provider.selectionprovider;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Taken from Marc R. Hoffmann's article 
 * "Eclipse Workbench: Using the Selection Service"
 * (http://www.eclipse.org/articles/Article-WorkbenchSelections/article.html)
 * 
 * and modified to pass Checkstyle.
 * 
 * From the original javadoc:
 * IPostSelectionProvider implementation that delegates to another
 * ISelectionProvider or IPostSelectionProvider. The selection provider used
 * for delegation can be exchanged dynamically. Registered listeners are
 * adjusted accordingly. This utility class may be used in workbench parts with
 * multiple viewers.
 *
 * @author BREDEX GmbH
 * @created Apr 14, 2009
 */
public class SelectionProviderIntermediate implements IPostSelectionProvider {

    /** the selection listeners for this provider */
    private final ListenerList m_selectionListeners = new ListenerList();

    /** the post-selection listeners for this provider */
    private final ListenerList m_postSelectionListeners = new ListenerList();

    /** the selection provider currently being used as a delegate */
    private ISelectionProvider m_delegate;

    /**
     * Listens for changes in selection from the current delegate and forwards
     * those changes as events.
     */
    private ISelectionChangedListener m_selectionListener = 
        new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                fireSelectionChanged(event.getSelection());
            }
        };

    /**
     * Listens for changes in selection from the current delegate and forwards
     * those changes as events. This is used if the delegate is a 
     * <code>IPostSelectionListener</code>.
     */
    private ISelectionChangedListener m_postSelectionListener = 
        new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                firePostSelectionChanged(event.getSelection());
            }
        };

    /**
     * Sets a new selection provider to delegate to. Selection listeners
     * registered with the previous delegate are removed beforehand. 
     * 
     * @param newDelegate new selection provider
     */
    public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {
        if (m_delegate == newDelegate) {
            return;
        }
        if (m_delegate != null) {
            m_delegate.removeSelectionChangedListener(m_selectionListener);
            if (m_delegate instanceof IPostSelectionProvider) {
                ((IPostSelectionProvider)m_delegate)
                    .removePostSelectionChangedListener(
                            m_postSelectionListener);
            }
        }
        m_delegate = newDelegate;
        if (newDelegate != null) {
            newDelegate.addSelectionChangedListener(m_selectionListener);
            if (newDelegate instanceof IPostSelectionProvider) {
                ((IPostSelectionProvider)newDelegate)
                    .addPostSelectionChangedListener(m_postSelectionListener);
            }
            fireSelectionChanged(newDelegate.getSelection());
            firePostSelectionChanged(newDelegate.getSelection());
        }
    }

    /**
     * Fires a selection changed event.
     * 
     * @param selection The new selection with which to fire the event.
     */
    protected void fireSelectionChanged(ISelection selection) {
        fireSelectionChanged(m_selectionListeners, selection);
    }

    /**
     * Fires a post-selection changed event.
     * 
     * @param selection The new selection with which to fire the event.
     */
    protected void firePostSelectionChanged(ISelection selection) {
        fireSelectionChanged(m_postSelectionListeners, selection);
    }

    /**
     * Fires a selection changed event.
     * 
     * @param list The list of listeners that should be informed of the event.
     * @param selection The new selection with which to fire the event.
     */
    private void fireSelectionChanged(
            ListenerList list, ISelection selection) {
        SelectionChangedEvent event = 
            new SelectionChangedEvent(m_delegate, selection);
        Object[] listeners = list.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            ISelectionChangedListener listener = 
                (ISelectionChangedListener) listeners[i];
            listener.selectionChanged(event);
        }
    }

    // IPostSelectionProvider Implementation
    /**
     * 
     * {@inheritDoc}
     */
    public void addSelectionChangedListener(
            ISelectionChangedListener listener) {
        m_selectionListeners.add(listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        m_selectionListeners.remove(listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addPostSelectionChangedListener(
            ISelectionChangedListener listener) {
        m_postSelectionListeners.add(listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removePostSelectionChangedListener(
            ISelectionChangedListener listener) {
        m_postSelectionListeners.remove(listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public ISelection getSelection() {
        return m_delegate == null ? null : m_delegate.getSelection();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setSelection(ISelection selection) {
        if (m_delegate != null) {
            m_delegate.setSelection(selection);
        }
    }

}
