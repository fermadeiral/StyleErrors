/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.controllers.propertysources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author BREDEX GmbH
 * @created 22.11.2011
 */
public abstract class AbstractPropertySource implements IPropertySource {
    /** List of <code>IPropertyDescriptors</code>  */
    private List<IPropertyDescriptor> m_propDescriptors = 
        new ArrayList<IPropertyDescriptor>();
    
    /**
     * Adds a <code>IPropertyDescriptor</code> to the List of IPropertyDescriptors.
     * @param propDescr the IPropertyDescriptor to add.
     */
    protected void addPropertyDescriptor(IPropertyDescriptor propDescr) {
        m_propDescriptors.add(propDescr);
    }
    
    /**
     * Adds all IPropertyDescriptors of the given Collection.
     * @param propDescriptors the Collection to add.
     */
    protected void addPropertyDescriptor(Collection<IPropertyDescriptor> 
        propDescriptors) {
        m_propDescriptors.addAll(propDescriptors);
    }
    

    /**
     * Clears the List of <code>IPropertyDescriptor</code>s.
     */
    protected void clearPropertyDescriptors() {
        m_propDescriptors.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        initPropDescriptor();
        IPropertyDescriptor[] propdescr = 
            m_propDescriptors.toArray(
                new IPropertyDescriptor[m_propDescriptors.size()]);
        return propdescr;
    }
    
    /**
     * @return the <code>List</code> of <code>IPropertyDescriptor</code>s.
     */
    protected List<IPropertyDescriptor> getPropertyDescriptorList() {
        return m_propDescriptors;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getPropertyValue(Object id) {
        Object obj = null;
        if (id instanceof IPropertyController) {
            obj = ((IPropertyController)id).getProperty();
            return obj != null ? obj : StringConstants.EMPTY;
        }
        Assert.notReached(Messages.PropertyIDInexistent + StringConstants.COLON
                + StringConstants.SPACE + id);
        return obj;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPropertyValue(Object id, Object value) {
        if (id instanceof IPropertyController) {
            IPropertyController pc = (IPropertyController)id;
            pc.setProperty(value);
        } else {
            Assert.notReached(Messages.PropertyIDInexistent 
                + StringConstants.COLON + StringConstants.SPACE + id);
        }
        initPropDescriptor();
        DataEventDispatcher.getInstance().firePropertyChanged(false);
        DataEventDispatcher.getInstance().fireParamChangedListener();
    }
    
    /**
     * Inits the PropertyDescriptors
     */
    protected abstract void initPropDescriptor();
    
    /**
     * @return node data or null...
     */
    public INodePO getNodeOrNull() {
        return null;
    }
}
