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
package org.eclipse.jubula.client.inspector.ui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * Global model object encapsulating the currently inspected component.
 *
 * @author BREDEX GmbH
 * @created Jun 12, 2009
 */
public class InspectedComponent {

    /** the single instance */
    private static InspectedComponent instance = null;

    /** the identifier for the currently inspected component */
    private IComponentIdentifier m_compId;
    
    /** property change support */
    private PropertyChangeSupport m_propChangeSupport;
    
    /**
     * Private constructor.
     */
    private InspectedComponent() {
        m_propChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        m_propChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        m_propChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * 
     * @return the single instance.
     */
    public static synchronized InspectedComponent getInstance() {
        if (instance == null) {
            instance = new InspectedComponent();
        }
        
        return instance;
    }

    /**
     * @param compId the compId to set
     */
    public void setCompId(IComponentIdentifier compId) {
        IComponentIdentifier oldCompId = m_compId;
        m_compId = compId;
        m_propChangeSupport.firePropertyChange(
                "compId", oldCompId, compId); //$NON-NLS-1$
    }

    /**
     * @return the compId
     */
    public IComponentIdentifier getCompId() {
        return m_compId;
    }

}
