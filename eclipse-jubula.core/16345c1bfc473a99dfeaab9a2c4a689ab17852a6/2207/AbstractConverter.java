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
 * An abstract implementation of the {@link IConverter} which checks if the conversion
 * is necessary
 * @author BREDEX GmbH
 * @param <PROJECT_TYPE> the project type
 */
public abstract class AbstractConverter<PROJECT_TYPE> 
    implements IConverter<PROJECT_TYPE> {

    /**
     * {@inheritDoc}
     */
    public void convert(PROJECT_TYPE project) {
        if (conversionIsNecessary(project)) {
            convertImpl(project);
        }
    }

    /**
     * @param project
     *            the project to operate on
     * @return true if conversion is necessary
     */
    protected abstract boolean conversionIsNecessary(PROJECT_TYPE project);

    /**
     * the main method which performs the conversion
     * 
     * @param project
     *            the project to operate on
     */
    protected abstract void convertImpl(PROJECT_TYPE project);

}
