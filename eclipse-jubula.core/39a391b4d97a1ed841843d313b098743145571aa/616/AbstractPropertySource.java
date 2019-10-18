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
package org.eclipse.jubula.client.ui.rcp.controllers.propertysources;

import java.util.Iterator;

import org.eclipse.jubula.client.core.businessprocess.compcheck.CompletenessGuard;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.propertysources.IPropertyController;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;


/**
 * This is the abstract superclass of all PropertySources of Jubula.
 * 
 * @author BREDEX GmbH
 * @created 31.01.2005
 * @param <NODE_TYPE> type of node
 */
public abstract class AbstractPropertySource <NODE_TYPE>
    extends org.eclipse.jubula.client.ui.controllers.
    propertysources.AbstractPropertySource {
    /** The default image */
    public static final Image DEFAULT_IMAGE = null;
    
    /** Image for deprecated action or component*/
    public static final Image DEPRECATED_IMAGE = 
            IconConstants.DEPRECATED_IMAGE; 
    
    /** Image for readonly */
    public static final Image READONLY_IMAGE = IconConstants.READ_ONLY_IMAGE; 
    
    /** Image for incomplete data */
    public static final Image INCOMPL_DATA_IMAGE = IconConstants.
        INCOMPLETE_DATA_IMAGE;
    
    /** Image for warning */
    public static final Image WARNING_IMAGE = IconConstants.WARNING_IMAGE;
    /** The INodePO for this PropertySource*/
    private NODE_TYPE m_node;
    
    /**
     * Constructor.
     * @param guiNode the INodePO for this PropertySource.
     */
    public AbstractPropertySource(NODE_TYPE guiNode) {
        m_node = guiNode;
    }

    /**
     * Gets a <code>IPropertyDescriptor</code> by the given ID.
     * @param id the ID of the searched Descriptor.
     * @return a IPropertyDescriptor or null if no descriptor found.
     */
    protected IPropertyDescriptor getPropertyDescriptorById(
        IPropertyController id) {
        Iterator<IPropertyDescriptor> iter = 
            getPropertyDescriptorList().iterator();
        while (iter.hasNext()) {
            IPropertyDescriptor descriptor = iter.next();
            if (id == descriptor.getId()) {
                return descriptor;
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getEditableValue() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public void resetPropertyValue(Object id) {
        // Reset not supported. Do nothing.
    }
    
    /**
     * Checks the entry sets
     * @param nodePo the node
     */
    protected void checkEntrySets(IParamNodePO nodePo) {
        boolean bool = nodePo.isTestDataComplete();
        CompletenessGuard.setCompletenessTestData(nodePo, bool);
    }
    
    /**
     * @return Returns the guiNode.
     */
    protected NODE_TYPE getNode() {
        return m_node;
    }
    
    /**
     * A general base class for all property controllers. It stores the
     * current SWT control the controller is associated with.
     */
    public abstract static class AbstractPropertyController implements
        IPropertyController {
        
        /**
         * parent property source
         */
        private AbstractNodePropertySource m_propertySource;
        
        /**
         * Constructor
         * @param s
         *      AbstractNodePropertySource
         */
        public AbstractPropertyController(AbstractNodePropertySource s) {
            setPropertySource(s);
        }
        /**
         * constructor
         */
        public AbstractPropertyController() {
            // do nothing
        }
        
        /**
         * @see AbstractPropertyController#getImage()
         * @param value the new value
         * @return an <code>Image</code> value. The Image.
         */
        public Image getImage(Object value) {
            if (value == null || StringConstants.EMPTY.equals(value)) {
                return INCOMPL_DATA_IMAGE;
            }
            return DEFAULT_IMAGE;
        }
        
        /**
         * {@inheritDoc}
         * calls getImage(getProperty())
         * if the depending value is available, call getImage(Object value)!
         */
        public Image getImage() {
            return getImage(getProperty());
        }
        
        /**
         * 
         * @return parent PropertySource
         */
        public AbstractNodePropertySource getPropertySource() {
            return m_propertySource;
        }
        
        /**
         * 
         * @param propertySource
         * parent PropertySource
         */
        public void setPropertySource(AbstractNodePropertySource 
            propertySource) {
            m_propertySource = propertySource;
        }
    }
    
    /**
     * A Dummy-Controller for an empty line in the Property View.
     * 
     * @author BREDEX GmbH
     * @created 11.02.2005
     */
    protected class DummyController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            // do nothing
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            return StringConstants.EMPTY;
        }
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }
    
    /** {@inheritDoc} */
    public INodePO getNodeOrNull() {
        if (getNode() instanceof INodePO) {
            return (INodePO) getNode();
        }
        return null;
    }
}