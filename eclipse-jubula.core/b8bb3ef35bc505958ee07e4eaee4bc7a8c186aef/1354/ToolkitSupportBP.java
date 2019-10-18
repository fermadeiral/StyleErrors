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
package org.eclipse.jubula.toolkit.common.businessprocess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.toolkit.common.IToolkitProvider;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 25.06.2007
 */
public class ToolkitSupportBP {

    /** The logger */
    private static Logger log = LoggerFactory.getLogger(ToolkitSupportBP.class);
    
    /** Map of {@link IToolkitProvider} */
    private static Map<ToolkitDescriptor, IToolkitProvider> 
        toolkitProvider = 
        new HashMap<ToolkitDescriptor, IToolkitProvider>();

    /**
     * Utility Constructor.
     */
    private ToolkitSupportBP() {
        // nothing
    }
 
    /**
     * Gets the AutConfigComposite for the toolkit with the given name.
     * @param toolkit the name of the toolkit.
     * @param parent the parent Composite of the AutConfigComposite
     * @param style the style
     * @param autConfig the Map of Aut-Configuration.
     * @param autName the name of the AUT that will be using this configuration.
     * @return the dependent AutConfigComposite
     */
    public static Composite getAutConfigComposite(String toolkit, 
        Composite parent, int style, Map<String, String> autConfig,
        String autName) throws ToolkitPluginException {
        
        IToolkitProvider provider = getToolkitProvider(toolkit);
        Composite autConf = provider.getAutConfigDialog(parent, style, 
            autConfig, autName);
        String toolkitName = toolkit;
        while (autConf == null) {
            provider = getSuperToolkitProvider(toolkitName);
            toolkitName = getToolkitDescriptor(toolkitName).getName();
            if (provider == null) {
                break;
            }
            autConf = provider.getAutConfigDialog(
                parent, style, autConfig, autName);
        }
        if (autConf == null) {
            throwToolkitPluginException(
                Messages.NoAutConfigFound + StringConstants.COLON 
                + StringConstants.SPACE
                + String.valueOf(toolkit), null);
        }
        return autConf;
    }
    
    /**
     * Gets the level of the given toolkit name.
     * @param toolkitName the name of the toolkit.
     * @return the level.
     * @throws ToolkitPluginException if no plugin can be found for the given
     *         toolkit name.
     */
    public static String getToolkitLevel(String toolkitName) 
        throws ToolkitPluginException {
        
        ToolkitDescriptor descr = getToolkitDescriptor(toolkitName);
        return descr.getLevel();
    }
    
    /**
     * Gets the ToolkitProvider with the given name.
     * @param name the name of the Toolkit
     * @return the {@link IToolkitProvider}.
     */
    private static IToolkitProvider getToolkitProvider(String name) 
        throws ToolkitPluginException {
        
        if (name == null) {    
            final String msg = Messages.ToolkitNameIsNull 
                + StringConstants.EXCLAMATION_MARK;
            log.error(msg);
            throwToolkitPluginException(msg, null);
        }
        final ToolkitDescriptor descr = getToolkitDescriptor(name);
        return toolkitProvider.get(descr);
    }
    
    /**
     * Gets the {@link IToolkitProvider} of the included (extended) toolkit
     * of the toolkit with the given name.
     * @param name the name of the extending toolkit
     * @return the {@link IToolkitProvider} of the super toolkit.
     */
    private static IToolkitProvider getSuperToolkitProvider(String name) 
        throws ToolkitPluginException {
        
        if (name == null) {
            final String msg = Messages.ToolkitNameIsNull 
                + StringConstants.EXCLAMATION_MARK;
            log.error(msg);
            throwToolkitPluginException(msg, null);
        }
        final ToolkitDescriptor descr = getToolkitDescriptor(name);
        final String superId = descr.getIncludes();
        final IToolkitProvider superToolkitProv = getToolkitProvider(superId);
        return toolkitProvider.get(superToolkitProv);
    }
    
    /**
     * 
     * @param toolkitId the id of the toolkit
     * @return the {@link ToolkitDescriptor} of the toolkit with the 
     * given id.
     */
    public static ToolkitDescriptor getToolkitDescriptor(
        String toolkitId) throws ToolkitPluginException {
        
        if (toolkitId == null) {
            final String msg = Messages.ToolkitNameIsNull 
                + StringConstants.EXCLAMATION_MARK;
            log.error(msg);
            throwToolkitPluginException(msg, null);
        }
        for (ToolkitDescriptor descr : toolkitProvider.keySet()) {
            if (toolkitId.equals(descr.getToolkitID())) {
                return descr;
            }
        }
        final String msg = Messages.NoToolkitPluginDescriptorFound
            + StringConstants.COLON + StringConstants.SPACE
            + String.valueOf(toolkitId);
        log.error(msg);
        throwToolkitPluginException(msg, null);
        return null;
    }
    
    /**
     * Adds a new {@link IToolkitProvider} with the dependent 
     * {@link ToolkitDescriptor} as key.
     * @param descr a ToolkitDescriptor (key)
     * @param provider a IToolKitProvider (value)
     */
    public static void addToolkitProvider(ToolkitDescriptor descr, 
        IToolkitProvider provider) {
        
        toolkitProvider.put(descr, provider);
    }
    
    /**
     * Throws a ToolkitPluginException with the given parameter.
     * @param message a message
     * @param cause a cause. Can be null.
     * @throws ToolkitPluginException
     */
    private static void throwToolkitPluginException(String message, 
        Throwable cause) throws ToolkitPluginException {
        
        if (cause == null) {
            throw new ToolkitPluginException(message);
        } 
        throw new ToolkitPluginException(message, cause);
    }
    
    /**
     * Returns a pseudo component identifier representing the most abstract
     * realizing toolkit component for an abstract component with default mapping
     * or <code>null</code> if none can be found
     * 
     * @param toolkitID
     *            the toolkit id
     * @param cc
     *            the concrete component
     * @return the component identifier
     */
    public static IComponentIdentifier
        getIdentifierOfMostAbstractRealizingComponentInToolkit(
        String toolkitID, ConcreteComponent cc) {
        ConcreteComponent concreteComponent =
                getMostAbstractRealizingComponentInToolkit(toolkitID, cc);
        IComponentIdentifier technicalName = new ComponentIdentifier();
        technicalName.setComponentClassName(
            concreteComponent
                .getComponentClass().getName());
        return technicalName;
    }
    
    /**
     * Returns the most abstract realizing toolkit component 
     * for an abstract component with default mapping
     * or <code>null</code> if none can be found
     * 
     * @param toolkitID
     *            the toolkit id
     * @param cc
     *            the concrete component
     * @return the component
     */
    public static ConcreteComponent getMostAbstractRealizingComponentInToolkit(
        String toolkitID, ConcreteComponent cc) {
        String toolkitIdToSearchIn = toolkitID;        
        Set realizers = cc.getAllRealizers();
        ToolkitDescriptor tpd = null;
        while (!StringUtils.isEmpty(toolkitIdToSearchIn)) {

            for (Iterator iterator = realizers.iterator(); iterator
                    .hasNext();) {
                ConcreteComponent concreteComponent = 
                        (ConcreteComponent) iterator.next();
                if (toolkitIdToSearchIn.equals(concreteComponent
                        .getToolkitDesriptor().getToolkitID())
                        && concreteComponent
                            .getComponentClass() != null) {
                    return concreteComponent;
                }
            }
            try {
                tpd = getToolkitDescriptor(toolkitIdToSearchIn);
                toolkitIdToSearchIn = tpd.getIncludes();
            } catch (ToolkitPluginException e) {
                log.error("No possible technical name found", e); //$NON-NLS-1$
                return null;
            }
        }
        return null;
    }
}
