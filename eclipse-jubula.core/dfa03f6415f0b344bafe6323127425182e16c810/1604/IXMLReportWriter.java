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

import java.io.IOException;

import org.dom4j.Document;

/**
 * "Writes" an XML document. Implementations can write the document to 
 * a file, for example, or to an e-mail address, and so on.
 *
 * @author BREDEX GmbH
 * @created Jan 23, 2007
 */
public interface IXMLReportWriter {
    
    /**
     * Writes the given document.
     * @param document
     *      The <code>Document</code> to write.
     */
    public abstract void write(Document document) throws IOException;

}
