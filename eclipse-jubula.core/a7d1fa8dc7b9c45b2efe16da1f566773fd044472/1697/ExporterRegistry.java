/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.testresult.export;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.client.core.model.ITestResult;

/**
 * @author Bredex Gmbh
 */
public final class ExporterRegistry {
    
    /** the ID of the Functions extension point */
    private static final String EXTENSION_POINT_ID = 
            "org.eclipse.jubula.client.core.exporter.testresult"; //$NON-NLS-1$
    
    /**
     *  ID for "class" attribute used for 
     */
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$


    /**
     * Empty cause utility class
     */
    private ExporterRegistry() {
        
    }

    
    /**
     * 
     * Goes through provided extensions and looks for Exporter extensions, when
     * finding an exporter extension, it will be initiliazed and then executes
     * the writing process.
     * 
     * @param testRes the testResult
     * @param path the target path for the file
     * @param filename the name of the file
     * 
     */
    public static void exportTestResult(ITestResult testRes,
            String path, String filename) {
        IExtensionPoint exporterExtensionPoint = 
                Platform.getExtensionRegistry().getExtensionPoint(
                        EXTENSION_POINT_ID);

        for (IExtension extension : exporterExtensionPoint.getExtensions()) {
            for (IConfigurationElement element
                    : extension.getConfigurationElements()) {
                
                try {
                    Object object;
                    object = element.createExecutableExtension(
                            ATTR_CLASS);
                    if (object instanceof ITestResultExporter) {
                        ITestResultExporter exporter =
                                (ITestResultExporter) object;
                        exporter.initiliaze(testRes);
                        exporter.writeTestResult(path, filename);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
