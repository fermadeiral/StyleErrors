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

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.jubula.client.core.model.ITestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;


/**
 * Generates XML test result reports that include all test cases and test steps.
 *
 * @author BREDEX GmbH
 * @created Jan 23, 2007
 */
public class CompleteXMLReportGenerator extends AbstractXMLReportGenerator {

    /**
     * Constructor
     * 
     * @param testResult The Test Result for which to write a report.
     */
    public CompleteXMLReportGenerator(ITestResult testResult) {
        super(testResult);
    }

    /**
     * {@inheritDoc}
     */
    public Document generateXmlReport() {
        Element general = generateHeader();
        
        buildRootElement(general);

        return getDocument();
    }

    /**
     * Recursively builds the test result hierarchy in XML format.
     *
     * @param element The root XML element for the converted test result 
     *                hierarchy.
     * @param resultNode The root node of the test result hierarchy.
     * @return The most recently built element.
     */
    protected Element buildElement(Element element, 
            TestResultNode resultNode) {
        Element insertInto = super.buildElement(element, resultNode);
        
        for (TestResultNode node : resultNode.getResultNodeList()) {
            buildElement(insertInto, node);
        }
        
        return insertInto;
    }

    /**
     * {@inheritDoc}
     */
    protected String getStyleName() {
        return "Complete"; //$NON-NLS-1$
    }
    
}
