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
package org.eclipse.jubula.toolkit.common.utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.i18n.Messages;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 24.04.2007
 */
public class ToolkitUtils {
    /** The logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ToolkitUtils.class);
    
    /** Map with description of toolkit level abstractness*/
    private static Map<String, Integer> abstractness = 
        new HashMap<String, Integer>();
    static {
        abstractness.put(ToolkitConstants.LEVEL_ABSTRACT, 0);
        abstractness.put(ToolkitConstants.LEVEL_CONCRETE, 1);
        abstractness.put(ToolkitConstants.LEVEL_TOOLKIT, 2);
    }
    
    /**
     * utility constructor
     */
    private ToolkitUtils() {
        // nothing here
    }

    /**
     * Returns a (resolved) URL of the given bundle and the given
     * bundle-relative path.
     * 
     * @param bundle
     *            the bundle
     * @param bundleRelPath
     *            the relative path
     * @return a (resolved) URL or null if the path could not be resolved.
     */
    public static URL getURL(Bundle bundle, String bundleRelPath) {
        
        URL unresolvedUrl = bundle.getEntry(bundleRelPath);
        URL fileURL = null;
        try {
            fileURL = FileLocator.resolve(unresolvedUrl);
        } catch (IOException e) {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append(Messages.CouldNotResolvePath);
            logMsg.append(StringConstants.COLON);
            logMsg.append(StringConstants.SPACE);
            logMsg.append(bundleRelPath);
            logMsg.append(StringConstants.SPACE);
            logMsg.append(Messages.OfPlugin);
            logMsg.append(StringConstants.COLON);
            logMsg.append(StringConstants.SPACE);
            LOG.error(logMsg.toString(), e);
        }
        return fileURL;
    }
    
    /**
     * Checks if the given toolkit level1 is more concrete than the given
     * toolkit level2.
     * @param level1 a toolkit level.
     * @param level2 a toolkit level.
     * @return true if level1 is more concrete than level2, false otherwise.
     */
    public static boolean isToolkitMoreConcrete(String level1, String level2) {
        String lvl1 = level1;
        String lvl2 = level2;
        final String emptyLevel = StringConstants.EMPTY;
        if (emptyLevel.equals(lvl1)) {
            lvl1 = ToolkitConstants.LEVEL_ABSTRACT;
        }
        if (emptyLevel.equals(lvl2)) {
            lvl1 = ToolkitConstants.LEVEL_ABSTRACT;
        }
        final int abstractness1 = abstractness.get(lvl1);
        final int abstractness2 = abstractness.get(lvl2);
        return abstractness1 > abstractness2;   
    }
    
    /**
     * Gets the toolkit name of the given toolkitID.
     * @param toolkitId a Toolkit ID.
     * @return the toolkit name of the given toolkitID or the given ID if no
     * name was found.
     */
    public static String getToolkitName(String toolkitId) {
        final List<ToolkitDescriptor> toolkitPluginDescriptors = 
            ComponentBuilder.getInstance().getCompSystem()
                .getAllToolkitDescriptors();
        for (ToolkitDescriptor desc : toolkitPluginDescriptors) {
            if (desc.getToolkitID().equalsIgnoreCase(toolkitId)) {
                return desc.getName();
            }
        }
        return toolkitId;
    }
    
    /**
     * Checks if the given toolkit <code>toolkitId</code> somehow includes, or
     * inherits from, <code>includedToolkitId</code>.
     * @param toolkitId The toolkit to check.
     * @param includedToolkitId The toolkit that may be included.
     * @return <code>true</code> if the given <code>toolkitId</code> includes, 
     *         at some point in its hierarchy, <code>includedToolkitId</code>.
     */
    public static boolean doesToolkitInclude(String toolkitId, 
        String includedToolkitId) {
        
        CompSystem compSys = ComponentBuilder.getInstance().getCompSystem();
        
        ToolkitDescriptor desc = 
            compSys.getToolkitDescriptor(toolkitId);
        
        String includes = desc.getIncludes();
        
        if (includes != null && includes.equals(includedToolkitId)) {
            return true;
        }
        
        while (desc != null && includes != null 
            && !ToolkitConstants.EMPTY_EXTPOINT_ENTRY.equals(includes)) {
            
            desc = compSys.getToolkitDescriptor(includes);
            if (desc != null) {
                includes = desc.getIncludes();
                if (includes != null && includes.equals(includedToolkitId)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Loads and creates an instance of the given Class-Name with the given 
     * constructor parameter with the given ClassLoader.
     *  
     * @param className the class name of the AutConfigComponent.
     * @param classLoader the ClassLoader to load the class with.
     * @param parent the parent of the AutConfigComponent.
     * @param style the SWT-Style
     * @param autConfig the AUT configuration as a Map.
     * @param autName the name of the AUT that will be using this configuration.
     * @return an instance of the AutConfigComponent.
     */
    @SuppressWarnings("unchecked")
    public static Composite createAutConfigComponent(String className, 
        ClassLoader classLoader, Composite parent, int style, 
        Map<String, String> autConfig, String autName) 
        throws ToolkitPluginException {
        
        Composite autConfigDialog = null;
        String log = NLS.bind(Messages.FailedLoading, 
                String.valueOf(className));
        try {
            Class autConfigComponentClass = classLoader.loadClass(className);
            Constructor<Composite> constructor = autConfigComponentClass
                .getConstructor(new Class[]{
                    Composite.class, int.class, Map.class, String.class});
            autConfigDialog = constructor.newInstance(
                new Object[]{parent, style, autConfig, autName});
        } catch (SecurityException e) {
            handleException(log, e);
        } catch (IllegalArgumentException e) {
            handleException(log, e);
        } catch (ClassNotFoundException e) {
            handleException(log, e);
        } catch (NoSuchMethodException e) {
            handleException(log, e);
        } catch (InstantiationException e) {
            handleException(log, e);
        } catch (IllegalAccessException e) {
            handleException(log, e);
        } catch (InvocationTargetException e) {
            handleException(log, e);
        }
        return autConfigDialog;
    }

    /**
     * @param logMsg
     *            the log message
     * @param exception
     *            the exception
     * @throws ToolkitPluginException
     *             the wrapper exception
     */
    private static void handleException(String logMsg,
            Exception exception) throws ToolkitPluginException {
        LOG.error(exception.getLocalizedMessage(), exception);
        throw new ToolkitPluginException(logMsg.toString(), exception);
    }
}
