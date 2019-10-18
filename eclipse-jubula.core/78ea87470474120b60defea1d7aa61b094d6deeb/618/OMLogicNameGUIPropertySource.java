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

import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.PropertyControllerLabelProvider;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 21.04.2005
 */
@SuppressWarnings("synthetic-access")
public class OMLogicNameGUIPropertySource 
    extends AbstractPropertySource<IComponentNamePO> {

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPNAME =
        Messages.OMLogicNameGUIPropertySourceComponentName;
    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_COMPTYPE =
        Messages.OMLogicNameGUIPropertySourceCompType;
    /** Label for parent project property */
    public static final String P_ELEMENT_DISPLAY_PARENTPROJECT =
        Messages.OMLogicNameGUIPropertySourceParentProject;
    /** Constant for Category Component */
    public static final String P_COMPONENT_CAT =
        Messages.OMLogicNameGUIPropertySourceComponent;

    /**
     * Constructor
     * 
     * @param compName The Component Name from which properties are obtained.
     */
    public OMLogicNameGUIPropertySource(IComponentNamePO compName) {
        super(compName);
        initPropDescriptor();
    }

    /**
     * Inits the PropertyDescriptors
     */
    protected void initPropDescriptor() {
        clearPropertyDescriptors();
        PropertyDescriptor propDes = null;
        // Component Name
        propDes = new PropertyDescriptor(
            new ComponentNameController(), P_ELEMENT_DISPLAY_COMPNAME);
        propDes.setCategory(P_COMPONENT_CAT); 
        propDes.setLabelProvider(new PropertyControllerLabelProvider());
        addPropertyDescriptor(propDes);

        // Component Type
        propDes = new PropertyDescriptor(
            new ComponentTypeController(), P_ELEMENT_DISPLAY_COMPTYPE);
        propDes.setCategory(P_COMPONENT_CAT); 
        propDes.setLabelProvider(new PropertyControllerLabelProvider());
        addPropertyDescriptor(propDes);

        // Parent Project
        propDes = new PropertyDescriptor(
                new ParentProjectController(), P_ELEMENT_DISPLAY_PARENTPROJECT);
        propDes.setCategory(P_COMPONENT_CAT); 
        propDes.setLabelProvider(new PropertyControllerLabelProvider());
        addPropertyDescriptor(propDes);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPropertySet(Object id) {
        boolean isPropSet = false;
        return isPropSet;
    }

    /**
     * Class to control component name.
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    private class ComponentNameController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IComponentNamePO compName = getNode();
            IComponentNameCache compCache = Plugin.getActiveCompCache();
            compName = compCache.getResCompNamePOByGuid(compName.getGuid());
            if (compName != null && compName.getName() != null) {
                return compName.getName();
            }
            return StringConstants.EMPTY;
        }  
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return IconConstants.LOGICAL_NAME_IMAGE;
        }
    }
    
    /**
     * Class to control component name.
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    private class ComponentTypeController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IComponentNamePO compName = getNode();
            IComponentNameCache compCache = Plugin.getActiveCompCache();
            compName = compCache.getResCompNamePOByGuid(compName.getGuid());
            if (compName != null && compName.getComponentType() != null) {
                return CompSystemI18n.getString(
                        compName.getComponentType(), true);
            }
            return StringConstants.EMPTY;
        }  
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }

    /**
     * Class to control parent project.
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    private class ParentProjectController extends AbstractPropertyController {
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IComponentNamePO compName = getNode();
            Long parentProjectId = compName.getParentProjectId();
            IProjectPO currentProject = GeneralStorage.getInstance()
                    .getProject();
            if (currentProject != null) {
                Long currentProjectId = currentProject.getId();
                String parentProjectGuid = null;
                if (parentProjectId == null
                        || parentProjectId.equals(currentProjectId)) {
                    parentProjectGuid = currentProject.getGuid();
                } else {
                    try {
                        parentProjectGuid = ProjectPM
                                .getGuidOfProjectId(compName
                                        .getParentProjectId());
                    } catch (JBException e) {
                        // No problem. We just won't be able to show the
                        // parent project.
                    }
                }

                if (parentProjectGuid != null) {
                    return ProjectNameBP.getInstance().getName(
                            parentProjectGuid);
                }
            }

            return Messages.OMLogicNameGUIPropertySourceUnknownParentProject;
        }  
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }
}