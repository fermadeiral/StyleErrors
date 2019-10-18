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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a Test Result Report in XML format.
 */
public class XmlResultReportWriter implements IXMLReportWriter {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(XmlResultReportWriter.class);

    /** the internal stream writer */
    private OutputStreamWriter m_writer;
    
    /**
     * Constructor
     * 
     * @param writer The internal stream writer.
     */
    public XmlResultReportWriter(OutputStreamWriter writer) {
        m_writer = writer;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void write(Document document) {
        OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
        xmlFormat.setEncoding(m_writer.getEncoding());

        XMLWriter xmlWriter = null;
        try {
            // write xml
            xmlWriter = new XMLWriter(m_writer, xmlFormat);
            xmlWriter.write(document);
        } catch (UnsupportedEncodingException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        } catch (IOException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        }

    }
    
}
