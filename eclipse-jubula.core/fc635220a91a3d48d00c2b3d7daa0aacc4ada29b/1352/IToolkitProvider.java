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
package org.eclipse.jubula.toolkit.common;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.swt.widgets.Composite;

/**
 * @author BREDEX GmbH
 * @created 19.04.2007
 */
public interface IToolkitProvider {
    /** The default path to the ComponentConfiguration.xml */
    public static final String COMP_CONFIG_PATH = "resources/xml/ComponentConfiguration.xml"; //$NON-NLS-1$
    
    /**
     * @return the URL to the ComponentConfiguration.xml
     */
    public URL getComponentConfigurationFileURL();

    /**
     * @param parent
     *            the parent of the dialog
     * @param style
     *            the style of the dialog
     * @param autConfig
     *            the AUT-Configuration
     * @param autName
     *            the name of the AUT that will be using this configuration.
     * @return the dialog of the AUT-Configuration or null if no dialog is
     *         supported (all non-toolkit plug-ins)
     */
    public Composite getAutConfigDialog(Composite parent, int style,
            Map<String, String> autConfig, String autName)
        throws ToolkitPluginException;

    /**
     * @return the resource bundle for internationalization.
     */
    public ResourceBundle getResourceBundle();
}
