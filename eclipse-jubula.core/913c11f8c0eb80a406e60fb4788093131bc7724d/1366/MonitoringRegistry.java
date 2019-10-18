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
package org.eclipse.jubula.toolkit.common.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;



/**
 * This class reads the informations which are provided by the extension point. 
 * 
 * @author BREDEX GmbH
 * @created 20.07.2010
 */
public abstract class MonitoringRegistry {
    /** gets the eclipse extension registry */    
    private static IExtensionRegistry reg = Platform.getExtensionRegistry();
    
    /** gets all extensions for monitoring */
    private static IConfigurationElement[] extensions = 
        reg.getConfigurationElementsFor(
                MonitoringConstants.MONITORING_EXT_REG);
    /**
     * utility constructor
     */
    private MonitoringRegistry() {
        //utility constructor
    }
        
    /**
     * @return returns a list of all registered Monitoring id's      * 
     */        
    public static List<String> getAllRegisteredMonitoringIds() {
                
        ArrayList<String> list = new ArrayList<String>();
        
        for (int i = 0; i < extensions.length; i++) {
            
            IConfigurationElement element = extensions[i];             
            list.add(element.getAttribute(MonitoringConstants.M_ID)); 
                          
        }
        
        return list;
    }
    
    /**
     * @param monId the monId
     * @return returns a String URL for the given monitoring agent ID or
     *         <code>null</code> if not defined / found
     */        
    public static String getExtUrlForMonitoringId(String monId) {
        String url = null;
        
        if (monId != null) {
            for (int i = 0; i < extensions.length; i++) {
                IConfigurationElement element = extensions[i];
                if (monId.equals(element.getAttribute(
                        MonitoringConstants.M_ID))) {
                    url = element.getAttribute(MonitoringConstants.M_LINK_TEXT);
                    break;
                }
            }
        }
        
        return url;
    }
    
    /**
     * @return returns a list of all registered Monitoring names  
     */
    public static List<String> getAllRegisteredMonitoringNames() {
        
        ArrayList<String> list = new ArrayList<String>();
        
        for (int i = 0; i < extensions.length; i++) {
            
            IConfigurationElement element = extensions[i];             
            list.add(element.getAttribute(MonitoringConstants.M_NAME)); 
                  
        }        
        return list;             
    }       
    /**
     * 
     * @param id The unique name of the extension
     * @return the Element (the Monitoring Agent), or <code>null</code>
     *         if no Monitoring Agent is defined for the given ID (i.e. 
     *         corresponding Monitoring plug-in is missing).
     */    
    public static IConfigurationElement getElement(String id) {
        
        for (int i = 0; i < extensions.length; i++) {
            
            IConfigurationElement element = extensions[i];             
            if (id.equals(element.getAttribute(
                    MonitoringConstants.M_ATTR_ID))) { 
                
                return element;
            }
                                      
        }        
        return null;
        
    }  
    /**
     * 
     * @param element the element form which the children attributes 
     * should be returned
     * @return returns a list of attributes with the parameter type, description, id 
     * and the name of the parent monitoring agent
     * 
     */
    public static List<MonitoringAttribute> getAttributes(
            IConfigurationElement element) {
        
        IConfigurationElement[] elementChildren = element.getChildren();
        
        ArrayList<MonitoringAttribute> list = 
            new ArrayList<MonitoringAttribute>();
         
        for (int j = 0; j < elementChildren.length; j++) {           
            
            IConfigurationElement[] validator = elementChildren[j].getChildren(
                    MonitoringConstants.M_ATTR_VALIDATOR);
            
            IValidator vali = null;
            if (validator.length > 0) {
                try {
                    vali = (IValidator)validator[0].createExecutableExtension(
                            MonitoringConstants.M_ATTR_VALIDATOR);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }         
            
            String[] extensionFilters = null;
            String filterAttribute = elementChildren[j].getAttribute(
                    MonitoringConstants.M_ATTR_FILTER);
            if (filterAttribute != null) {
                extensionFilters = filterAttribute
                        .split(StringConstants.SEMICOLON);
            }
          
            list.add(new MonitoringAttribute(
                    elementChildren[j]
                            .getAttribute(MonitoringConstants.M_ATTR_TYPE),
                    elementChildren[j].getAttribute(
                            MonitoringConstants.M_ATTR_DESCRIPTION),
                    elementChildren[j]
                            .getAttribute(MonitoringConstants.M_ATTR_ID),
                    elementChildren[j].getAttribute(
                            MonitoringConstants.M_ATTR_DEFAULT_VALUE),
                    Boolean.valueOf(elementChildren[j]
                            .getAttribute(MonitoringConstants.M_ATTR_RENDER)),
                    element.getAttribute(MonitoringConstants.M_ATTR_NAME),
                    elementChildren[j]
                            .getAttribute(MonitoringConstants.M_INFO_TEXT),
                    vali, extensionFilters));
        
        }
        return list;
    }
    
}
