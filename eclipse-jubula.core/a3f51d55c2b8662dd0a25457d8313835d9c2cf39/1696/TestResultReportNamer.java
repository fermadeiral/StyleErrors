/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.businessprocess;

/**
 * Generates filenames for a Test Result Report.
 */
public class TestResultReportNamer {
    /**
     * <code>ENCODING</code>
     */
    public static final String ENCODING = "UTF-8"; //$NON-NLS-1$
    
    /** file extension for XML */
    public static final String FILE_EXTENSION_XML = ".xml"; //$NON-NLS-1$

    /** file extension for HTML */
    public static final String FILE_EXTENSION_HTML = ".html"; //$NON-NLS-1$

    /** 
     * ID for the report to write (must be unique within the context of the 
     * write location) 
     */
    private String m_baseName;

    /**
     * Constructor
     * 
     * @param baseName The base name for all generated filenames.
     */
    public TestResultReportNamer(String baseName) {
        m_baseName = baseName;
    }

    /**
     * 
     * @return the generated filename for the HTML version of the 
     *         Test Result Report.
     */
    public String getHtmlEntryName() {
        return m_baseName + FILE_EXTENSION_HTML;
    }
    
    /**
     * 
     * @return the generated filename for the XML version of the 
     *         Test Result Report.
     */
    public String getXmlEntryName() {
        return m_baseName + FILE_EXTENSION_XML;
    }
}
