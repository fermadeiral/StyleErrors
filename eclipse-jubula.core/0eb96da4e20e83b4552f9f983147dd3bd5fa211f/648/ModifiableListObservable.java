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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * class for observation of ModifiableList
 * @author BREDEX GmbH
 * @created 13.06.2006
 */
public class ModifiableListObservable implements IModifiableListObservable {
    
    /** standard logging */
    private static final Logger LOG = LoggerFactory.getLogger(
        ModifiableListObservable.class);
    
    /**
     * <code>m_contentAddedListener</code> listener for added content
     */
    private List<IContentAddedListener> m_contentAddedListener = 
        new ArrayList<IContentAddedListener>();
    
    /**
     * <code>m_contentChangedListener</code> listener for changed content
     */
    private List<IContentChangedListener> m_contentChangedListener = 
        new ArrayList<IContentChangedListener>();
    
    /**
     * <code>m_contentRemovedListener</code> listener for removed content
     */
    private List<IContentRemovedListener> m_contentRemovedListener = 
        new ArrayList<IContentRemovedListener>();

    /**
     * <code>m_selectionChangedListener</code> listener for change of selection
     */
    private List<ISelectionChangedListener> m_selectionChangedListener = 
        new ArrayList<ISelectionChangedListener>();

    /**
     * <code>m_optionalButtonSelectedListener</code> listener for selection of
     * optional button
     */
    private List<IOptionalButtonSelectedListener> 
        m_optionalButtonSelectedListener = 
            new ArrayList<IOptionalButtonSelectedListener>();
    
    /**
     * {@inheritDoc}
     */
    public void addContentAddedListener(IContentAddedListener listener) {
        m_contentAddedListener.add(listener);        
    }

    /**
     * {@inheritDoc}
     */
    public void addContentChangedListener(IContentChangedListener listener) {
        m_contentChangedListener.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void addContentRemovedListener(IContentRemovedListener listener) {
        m_contentRemovedListener.add(listener);        
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectionChangedListener(
        ISelectionChangedListener listener) {
        m_selectionChangedListener.add(listener);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void addOptionalButtonSelectedListener(
        IOptionalButtonSelectedListener listener) {
        m_optionalButtonSelectedListener.add(listener);        
    }


    /**
     * {@inheritDoc}
     */
    public void removeContentAddedListener(IContentAddedListener listener) {
        m_contentAddedListener.remove(listener);     
        
    }

    /**
     * {@inheritDoc}
     */
    public void removeContentChangedListener(IContentChangedListener listener) {
        m_contentChangedListener.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeContentRemovedListener(IContentRemovedListener listener) {
        m_contentRemovedListener.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectionChangedListener(
        ISelectionChangedListener listener) {
        m_selectionChangedListener.remove(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeOptionalButtonSelectedListener(
        IOptionalButtonSelectedListener listener) {
        m_optionalButtonSelectedListener.remove(listener);   
    }

    /**
     * {@inheritDoc}
     */
    public void fireContentAdded(String newValue) {
        final Set<IContentAddedListener> listener = 
            new HashSet<IContentAddedListener>(m_contentAddedListener);
        for (IContentAddedListener l : listener) {
            try {
                l.updateContentAdded(newValue);
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionWhileCallingListeners, t);
            }
        }
        
    }

    /**
     * {@inheritDoc}
     */
    public void fireContentChanged(String oldValue, String newValue) {
        final Set<IContentChangedListener> listener = 
            new HashSet<IContentChangedListener>(m_contentChangedListener);
        for (IContentChangedListener l : listener) {
            try {
                l.updateContentChanged(oldValue, newValue);
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionWhileCallingListeners, t);
            }
        }        
    }

    /**
     * {@inheritDoc}
     */
    public void fireContentRemoved(String oldValue) {
        final Set<IContentRemovedListener> listener = 
            new HashSet<IContentRemovedListener>(m_contentRemovedListener);
        for (IContentRemovedListener l : listener) {
            try {
                l.updateContentRemoved(oldValue);
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionWhileCallingListeners, t);
            }
        }        
    }

    /**
     * {@inheritDoc}
     */
    public void fireSelectionChanged(String value) {
        final Set<ISelectionChangedListener> listener = 
            new HashSet<ISelectionChangedListener>(m_selectionChangedListener);
        for (ISelectionChangedListener l : listener) {
            try {
                l.updateSelectionChanged(value);
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionWhileCallingListeners, t);
            }
        }       
    }
    
    /**
     * {@inheritDoc}
     */
    public void fireOptionalButtonSelected() {
        final Set<IOptionalButtonSelectedListener> listener = 
                new HashSet<IOptionalButtonSelectedListener>(
                m_optionalButtonSelectedListener);
        for (IOptionalButtonSelectedListener l : listener) {
            try {
                l.updateOptionalButtonSelected();
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionWhileCallingListeners, t);
            }
        }      
        
    }

    // required listener interfaces
    
    /**
     * @author BREDEX GmbH
     * @created 13.06.2006
     */
    public interface IContentAddedListener {
        /**
         * @param newValue newly added content
         */
        public void updateContentAdded(String newValue);
    }

    /**
     * @author BREDEX GmbH
     * @created 13.06.2006
     */
    public interface IContentChangedListener {
        /**
         * @param oldValue this value was just changed
         * @param newValue this is the new value
         */
        public void updateContentChanged(String oldValue, String newValue);
    }

    /**
     * @author BREDEX GmbH
     * @created 13.06.2006
     */
    public interface IContentRemovedListener {
        /**
         * @param oldValue the value which was just removed from the container
         */
        public void updateContentRemoved(String oldValue);
    }

    /**
     * @author BREDEX GmbH
     * @created 13.06.2006
     */
    public interface ISelectionChangedListener {
        /**
         * @param value which value is selected
         */
        public void updateSelectionChanged(String value);
    }
    
    /**
     * @author BREDEX GmbH
     * @created 19.06.2006
     */
    public interface IOptionalButtonSelectedListener {
        /**
         * update because optional button was clicked
         */
        public void updateOptionalButtonSelected();

    }

}
