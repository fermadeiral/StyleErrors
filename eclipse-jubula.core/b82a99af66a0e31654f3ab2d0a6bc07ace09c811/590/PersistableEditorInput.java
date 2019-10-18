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
package org.eclipse.jubula.client.ui.rcp.editors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 02.09.2005
 *
 */
public class PersistableEditorInput implements IEditorInput {

    /** IPersistentObject which shall be edited */
    private IPersistentObject m_node;
    /** edit support to use in editor */
    private EditSupport m_editSupport;
    /**
     * @param node IPersistentObject to be edited
     * @throws PMException if the node could not be loaded
     */
    public PersistableEditorInput(IPersistentObject node) throws PMException {
        setNode(node);
        m_editSupport = new EditSupport(m_node, 
                new ParamNameBPDecorator(ParamNameBP.getInstance(), m_node));
    }

    /**
     * {@inheritDoc}
     * @return always false for the time being
     */
    public boolean exists() {        
        return false;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public ImageDescriptor getImageDescriptor() {
        return PlatformUI.getWorkbench().getEditorRegistry()
            .getImageDescriptor(getName());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public String getName() {
        return StringUtils.defaultString(m_node.getName());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public IPersistableElement getPersistable() {        
        return null;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public String getToolTipText() {
        return getName();
    }

    /**
     * {@inheritDoc}
     * @param adapter
     * @return
     */
    public Object getAdapter(Class adapter) {        
        if (adapter == PersistableEditorInput.class) {
            return this;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PersistableEditorInput)) {
            return false;
        }
        PersistableEditorInput o = (PersistableEditorInput)obj;
        return m_node.equals(o.m_node);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int hashCode() {        
        return m_node.hashCode();
    }

    /**
     * @return Returns the node.
     */
    public IPersistentObject getNode() {
        return m_node;
    }

    /**
     * @return Returns the editSupport.
     */
    public EditSupport getEditSupport() {
        return m_editSupport;
    }

    /**
     * 
     */
    public void dispose() {
        m_editSupport = null;
        m_node = null;
    }
    
    /**
     * @param po node to set
     */
    protected void setNode(IPersistentObject po) {
        Validate.notNull(po, "null value not allowed."); //$NON-NLS-1$
        m_node = po;
    }
    
    /**
     * refreshes the node of master session
     */
    public void refreshNode() {
        setNode(getEditSupport().getOriginal());        
    }
}
