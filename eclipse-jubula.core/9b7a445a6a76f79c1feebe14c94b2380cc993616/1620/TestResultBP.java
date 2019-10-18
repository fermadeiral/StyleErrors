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
package org.eclipse.jubula.client.core.businessprocess;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.core.utils.BundleUtils;


/**
 * @author BREDEX GmbH
 * @created Nov 8, 2005
 */
public class TestResultBP {
    /**
     * instance
     */
    private static TestResultBP instance = null;
    
    /** The ResultTestModel */
    private TestResult m_result = null;

    /**
     * @return ResultTestSuiteModel
     */
    public TestResult getResultTestModel() {
        return m_result;
    }
    /**
     * 
     * @param result The Test Result.
     */
    public void setResultTestModel(TestResult result) {
        m_result = result;
    }

    /**
     * @return instance of BP
     */
    public static TestResultBP getInstance() {
        if (instance == null) {
            instance = new TestResultBP();
        }
        return instance;
    }
    
    /**
     * @return The name of the XSL file to use for transforming XML Test Result
     *         Reports to HTML Test Result Reports.
     */
    public URL getXslFileURL() {
        return BundleUtils.getFileURL(Platform.getBundle(Activator.PLUGIN_ID),
                Activator.RESOURCES_DIR + "format.xsl"); //$NON-NLS-1$
    }
}
