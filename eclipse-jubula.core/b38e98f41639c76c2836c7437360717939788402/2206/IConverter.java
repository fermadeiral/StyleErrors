/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.client.archive.converter.utils;

/**
 * Interface for import converter
 * @author BREDEX GmbH
 * @param <PROJECT_TYPE> the project type
 */
public interface IConverter<PROJECT_TYPE> {
    
    /**
     * the main method which performs the conversion
     * 
     * @param project
     *            the project to operate on
     */
    public void convert(PROJECT_TYPE project);
}
