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
package org.eclipse.jubula.toolkit.rcp.provider;

import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.jubula.toolkit.common.utils.ToolkitUtils;
import org.eclipse.jubula.toolkit.swt.provider.SWTToolkitProvider;

/**
 * @author BREDEX GmbH
 * @created 24.04.2007
 */
public class RCPToolkitProvider extends SWTToolkitProvider {
    /** <code>I18N_PROPERTIES</code> */
    private static final String I18N_PROPERTIES = 
        "org.eclipse.jubula.toolkit.rcp.provider.I18nStrings"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public URL getComponentConfigurationFileURL() {
        return ToolkitUtils.getURL(Activator.getDefault().getBundle(),
                COMP_CONFIG_PATH);
    }
    
    /** {@inheritDoc} */
    public ResourceBundle[] getResourceBundles() {
        return new ResourceBundle[] { ResourceBundle
                .getBundle(I18N_PROPERTIES) };
    }
}
