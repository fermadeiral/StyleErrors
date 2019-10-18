/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.client.core.testresult.export;

import org.eclipse.jubula.client.core.model.ITestResult;

/**
 * @author Bredex Gmbh
 */
public interface ITestResultExporter {

    /**
     * Initiliazing the Exporter (used to replace Constructor with parameters)
     * 
     * @param result the result of the test which is to be exported
     */
    public void initiliaze(ITestResult result);
    
    
    /**
     * writes the content into a file
     * 
     * @param path the targetpath for the file
     * @param filename the filename
     */
    public void writeTestResult(String path, String filename);
}
