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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.dom4j.Document;

/**
 * Writes an XML document to a file. Also writes an HTML document based on the
 * XML document to another file.
 * 
 * @author BREDEX GmbH
 * @created Jan 23, 2007
 */
public class FileXMLReportWriter {
    
    /** report namer */
    private TestResultReportNamer m_namer;
    
    /**
     * @param baseFileName
     *            given file name
     */
    public FileXMLReportWriter(String baseFileName) {
        m_namer = new TestResultReportNamer(baseFileName);
    }

    /**
     * Writes the given document to files. 
     * 
     * @param document The document to write.
     * @throws IOException if an I/O error occurs while writing.
     */
    public void write(Document document) throws IOException {
        OutputStreamWriter xmlStreamWriter = new OutputStreamWriter(
                new FileOutputStream(m_namer.getXmlEntryName()), 
                TestResultReportNamer.ENCODING);
        
        try {
            new XmlResultReportWriter(xmlStreamWriter).write(document);
        } finally {
            xmlStreamWriter.close();
        }

        OutputStreamWriter htmlStreamWriter = new OutputStreamWriter(
                new FileOutputStream(m_namer.getHtmlEntryName()),
                TestResultReportNamer.ENCODING);
        try {
            new HtmlResultReportWriter(htmlStreamWriter).write(document);
        } finally {
            htmlStreamWriter.close();
        }
        
    }
    
    /**
     * 
     * @return the generated filename for the HTML version of the 
     *         Test Result Report.
     */
    public String getHtmlFileName() {
        return m_namer.getHtmlEntryName();
    }

    /**
     * 
     * @return the generated filename for the XML version of the 
     *         Test Result Report.
     */
    public String getXmlFileName() {
        return m_namer.getXmlEntryName();
    }
}
