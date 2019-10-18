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
package org.eclipse.jubula.client.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for bundle issues
 * 
 * @author BREDEX GmbH
 * @created Nov 29, 2010
 */
public class BundleUtils {
    /** standard logging */
    private static final Logger LOG = 
        LoggerFactory.getLogger(BundleUtils.class);

    /**
     * Constructor
     */
    private BundleUtils() {
    // hide
    }

    /**
     * @param bundle
     *            the bundle to resolve the file URL from
     * @param resouceName
     *            the relative resource name e.g. resources/plugin.properties
     * @return the URL for the resource
     */
    public static URL getFileURL(Bundle bundle, String resouceName) {
        try {
            URL url = bundle.getEntry(resouceName);
            if (url != null) {
                return FileLocator.toFileURL(url);
            }
            LOG.error(Messages.Resource + StringConstants.COLON 
                + StringConstants.SPACE + resouceName + StringConstants.SPACE
                + Messages.NotFound + StringConstants.DOT);
        } catch (MalformedURLException e) {
            LOG.error(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
    
    /**
     * @param bundle the bundle to load the properties from
     * @param propertyFileName the name of the properties file
     * @return the loaded properties
     * @throws JBFatalException in case of error during properties loading
     */
    public static Properties loadProperties(Bundle bundle,
            String propertyFileName) throws JBFatalException {
        Properties prop = new Properties();
        InputStream propStream = null;
        try {
            propStream = getFileURL(bundle, propertyFileName).openStream();
            prop.load(propStream);
        } catch (IOException e) {
            String msg = Messages.CantLoad + StringConstants.COLON
                + StringConstants.SPACE + propertyFileName;
            LOG.error(msg, e);
            throw new JBFatalException(msg,
                    MessageIDs.E_PROPERTIES_FILE_NOT_FOUND);
        } finally {
            try {
                if (propStream != null) {
                    propStream.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        return prop;
    }
}
