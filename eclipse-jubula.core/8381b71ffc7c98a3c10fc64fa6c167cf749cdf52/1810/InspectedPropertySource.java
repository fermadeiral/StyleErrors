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
package org.eclipse.jubula.client.inspector.ui.controller;

import java.util.Map;

import org.eclipse.jubula.client.inspector.ui.model.InspectedComponent;
import org.eclipse.jubula.client.inspector.ui.model.InspectorTreeNode;
import org.eclipse.jubula.client.ui.controllers.propertysources.AbstractPropertySource;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.JBPropertyDescriptor;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.OMTechNameGUIPropertySource;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author BREDEX GmbH
 */
public class InspectedPropertySource extends AbstractPropertySource {
    /** Property category text */
    public static final String P_ELEMENT_DISPLAY_PROPERTY_INFORMATION =
            Messages.OMTechNameGUIPropertySourcePropertyInformation;
    /** the tree node */
    private InspectorTreeNode m_treeNode;
    
    /** Constructor 
     * @param treeNode */
    public InspectedPropertySource(InspectorTreeNode treeNode) {
        m_treeNode = treeNode;
    }

    /** {@inheritDoc} */
    protected void initPropDescriptor() {
        clearPropertyDescriptors();
        if (!m_treeNode.hasChildren()) {
            initComponentProperties();
        }
    }

    /**
     * initializes the ComponentProperties
     *
     */
    private void initComponentProperties() {
        IComponentIdentifier compId = InspectedComponent.getInstance()
                .getCompId();
        if (compId != null) {
            Map<String, String> componentProperties = compId
                    .getComponentPropertiesMap();
            if (componentProperties != null) {
                for (String key : componentProperties.keySet()) {
                    PropertyDescriptor propDes = new JBPropertyDescriptor(
                        new OMTechNameGUIPropertySource
                            .ComponentPropertiesController(key, compId),
                        key);
                    propDes.setCategory(
                            P_ELEMENT_DISPLAY_PROPERTY_INFORMATION);
                    addPropertyDescriptor(propDes);
                }
            }
        }
    }
    
    /** {@inheritDoc} */
    public Object getEditableValue() {
        return "noEditableValues"; //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    public boolean isPropertySet(Object arg0) {
        // Do nothing
        return false;
    }

    /** {@inheritDoc} */
    public void resetPropertyValue(Object arg0) {
        // Do nothing
    }
}