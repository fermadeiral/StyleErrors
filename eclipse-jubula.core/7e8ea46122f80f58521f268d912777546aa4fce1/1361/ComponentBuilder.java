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
package org.eclipse.jubula.toolkit.common.xml.businessprocess;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.jubula.toolkit.common.IToolkitProvider;
import org.eclipse.jubula.toolkit.common.Activator;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.ConfigXmlException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.utils.generator.AbstractComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.jubula.tools.internal.xml.businessprocess.ConfigVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods for reading the configuration file and for 
 * mapping the configuration file to java objects.
 * 
 * @author BREDEX GmbH
 * @created 25.07.2005
 * 
 */
public class ComponentBuilder extends AbstractComponentBuilder {
    
    /** Singleton instance */
    private static ComponentBuilder instance;
    
    /** The logger. */
    private static Logger log = LoggerFactory.getLogger(ComponentBuilder.class);
    
    /** exception during initialization with {@link #initCompSystem()} */
    private Map<String, Exception> m_initExceptions = 
            new HashMap<String, Exception>();
    
    /**
     * Default constructor.
     */
    private ComponentBuilder() {
        super();
    }
    
    /**
     * 
     * @return the Exceptions which occurred during initialization
     */
    public Map<String, Exception> getInitExceptions() {
        return m_initExceptions;
    }

    /**
     * Initializes the Component System.<br>
     * Reads in all ComponentConfiguration Files of all installed 
     * Toolkit-Plugins.
     */
    private void initCompSystem() throws ToolkitPluginException {
        IExtension[] plugins = Activator.getDefault().findToolkitPlugins();
        for (IExtension extension : plugins) {
            IConfigurationElement[] elements = extension
                .getConfigurationElements();
            for (IConfigurationElement element : elements) {
                IToolkitProvider provider;
                try {
                    provider = (IToolkitProvider) element
                         .createExecutableExtension(
                             ToolkitConstants.ATTR_ITOOLKITPROVIDER);
                    URL componentConfigurationURL = provider
                        .getComponentConfigurationFileURL();
                    InputStream inputStream = getInputStream(
                        componentConfigurationURL);
                    CompSystem compSystem = createCompSystem(inputStream);
                    ToolkitDescriptor descr = 
                        createToolkitDescriptor(element, compSystem);
                    final ResourceBundle resourceBundle = provider
                        .getResourceBundle();
                    if (resourceBundle == null) {
                        log.error(Messages.NoI18n + StringConstants.MINUS
                                + Messages.ResourceBundleAvailable
                                + StringConstants.COLON
                                + StringConstants.SPACE
                                + String.valueOf(descr.getName()));
                    }
                    CompSystemI18n.addResourceBundle(resourceBundle);
                    setToolkitDescriptorToComponents(compSystem, descr);
                    // merge the CompSystem in the Main-CompSystem
                    addToolkitToCompSystem(compSystem);
                    ToolkitSupportBP.addToolkitProvider(descr, provider);
                } catch (IOException fileNotFoundEx) {
                    final String msg = Messages.ComponenConfigurationNotFound
                        + StringConstants.EXCLAMATION_MARK;
                    log.error(msg, fileNotFoundEx);
                    m_initExceptions.put(extension.getContributor().getName(),
                            fileNotFoundEx);
                } catch (CoreException coreEx) {
                    final String msg = Messages.CouldNotCreateToolkitProvider
                        + StringConstants.EXCLAMATION_MARK;
                    log.error(msg, coreEx);
                    m_initExceptions.put(extension.getContributor().getName(),
                            coreEx);
                } catch (RuntimeException ce) {
                    final String msg = Messages.CouldNotCreateToolkitProvider
                            + StringConstants.EXCLAMATION_MARK;
                    log.error(msg, ce);
                    m_initExceptions.put(extension.getContributor().getName(),
                            ce);
                }
            }
        }
        try {
            postProcess();
        } catch (ConfigXmlException cxe) {
            m_initExceptions.put(cxe.getToolkitDescriptor(),
                    cxe);
        }
    }

    /**
     * Creates a {@link ToolkitDescriptor} which hold the attributes
     * of the Toolkit and adds the Descriptor to the given CompSystem.
     * @param element an IConfigurationElement
     * @param compSystem the CompSystem
     * @return {@link ToolkitDescriptor}
     * Constants.ATTR_<attribute_name>
     * @throws ToolkitPluginException if an error occurs
     */
    private ToolkitDescriptor createToolkitDescriptor(
        IConfigurationElement  element, CompSystem compSystem) 
        throws ToolkitPluginException {
        
        final String toolkitId = element.getAttribute(
            ToolkitConstants.ATTR_TOOLKITID);
        try {
            if (compSystem.getToolkitDescriptor(toolkitId) == null) {
                
                final String name = element.getAttribute(
                    ToolkitConstants.ATTR_NAME);
                final String level = element.getAttribute(
                    ToolkitConstants.ATTR_LEVEL);
                final boolean isUserToolkit = Boolean.parseBoolean(element
                    .getAttribute(ToolkitConstants.ATTR_ISUSERTOOLKIT));
                final String includes = String.valueOf(element.getAttribute(
                    ToolkitConstants.ATTR_INCLUDES));
                final String depends = String.valueOf(element.getAttribute(
                    ToolkitConstants.ATTR_DEPENDS));
                final int order = Integer.parseInt(element.getAttribute(
                    ToolkitConstants.ATTR_ORDER));
                final ConfigVersion configVersion = compSystem
                    .getConfigVersion();
                final int majorVersion = configVersion.getMajorVersion();
                final int minorVersion = configVersion.getMinorVersion();
                final ToolkitDescriptor descr = 
                    new ToolkitDescriptor(toolkitId, name, 
                        includes, depends, level, order, isUserToolkit, 
                        majorVersion, minorVersion);
                compSystem.addToolkitPluginDescriptor(toolkitId, descr);
                return descr;
            }
        } catch (NumberFormatException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new ToolkitPluginException(
                Messages.ErrorWhileReadingAttributes + StringConstants.COLON
                + StringConstants.SPACE
                + String.valueOf(toolkitId), e);
        } catch (InvalidRegistryObjectException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new ToolkitPluginException(
                Messages.ErrorWhileReadingAttributes + StringConstants.COLON
                + StringConstants.SPACE
                + String.valueOf(toolkitId), e);
        } 
        return null;
    }
    
    /**
     * @return the instance
     */
    public static ComponentBuilder getInstance() {
        if (instance == null) {
            instance = new ComponentBuilder();
        } 
        return instance;
    }

    /**
     * Returns a <code>CompSystem</code> with all components which can be
     * tested by Jubula. The configuration files (ComponentConfiguration.xml)
     * will be read from the installed Toolkit-Plugins.
     * @return the CompSystem a<code>CompSystem</code> object.
     */
    public CompSystem getCompSystem() {
        if (super.getCompSystem() == null) {
            try {
                initCompSystem();
            } catch (RuntimeException e) {
                log.error(e.getMessage());
                throw new ConfigXmlException(e.getMessage(), 
                    MessageIDs.E_GENERAL_COMPONENT_ERROR);
            } catch (ToolkitPluginException tke) {
                throw new ConfigXmlException(tke.getMessage(), 
                    MessageIDs.E_GENERAL_COMPONENT_ERROR);
            }
        }
        return super.getCompSystem();
    }
    
    /**
     * @return The IDs of the installed independent CompSystems without abstract
     *         an concrete.
     */
    public List<String> getLevelToolkitIds() {
        List<ToolkitDescriptor> toolkitDescriptors = super
            .getCompSystem().getIndependentToolkitDescriptors(true);

        List<String> toolkitIds = new ArrayList<String>();

        for (ToolkitDescriptor desc : toolkitDescriptors) {
            toolkitIds.add(desc.getToolkitID());
        }

        return toolkitIds;
    }
}