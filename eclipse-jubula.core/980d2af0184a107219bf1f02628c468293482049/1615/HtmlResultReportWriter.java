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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a Test Result Report in HTML format.
 */
public class HtmlResultReportWriter implements IXMLReportWriter {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(HtmlResultReportWriter.class);
    
    /** the internal stream writer */
    private OutputStreamWriter m_writer;
    
    /**
     * Constructor
     * 
     * @param writer The internal stream writer.
     */
    public HtmlResultReportWriter(OutputStreamWriter writer) {
        m_writer = writer;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void write(Document document) {
        // write html, transformed by XSLT
        OutputFormat htmlFormat = OutputFormat.createCompactFormat();
        htmlFormat.setEncoding(m_writer.getEncoding());
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            TestResultBP trbp = TestResultBP.getInstance();
            final Transformer transformer = factory
                    .newTransformer(new StreamSource(trbp.getXslFileURL()
                            .openStream()));
            DocumentSource source = new DocumentSource(document);
            DocumentResult result = new DocumentResult();
            transformer.transform(source, result);
            Document transformedDoc = result.getDocument();
            XMLWriter htmlWriter = new XMLWriter(m_writer, htmlFormat);
            htmlWriter.write(transformedDoc);
        } catch (TransformerConfigurationException e1) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e1);
        } catch (TransformerException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        } catch (IOException e) {
            LOG.error(Messages.ErrorFileWriting + StringConstants.DOT, e);
        }

    }

}
