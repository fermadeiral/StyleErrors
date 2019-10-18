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
package org.eclipse.jubula.client.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.controllers.propertysources.TestResultNodePropertySource;
import org.eclipse.jubula.client.ui.views.imageview.ImageProvider;
import org.eclipse.jubula.client.ui.views.imageview.TestResultNodeImageProvider;
import org.eclipse.jubula.client.ui.views.logview.LogProvider;
import org.eclipse.jubula.client.ui.views.logview.TestResultNodeLogProvider;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Adapter factory for test result nodes.
 * 
 * @author BREDEX GmbH
 * @created Jul 31, 2008
 */
public class TestResultNodeAdapterFactory implements IAdapterFactory {
    /** types for which adapters are available */
    private final Class[] m_types = { ImageProvider.class,
                                      LogProvider.class,
                                      TestResultNode.class, 
                                      IPropertySource.class};

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == LogProvider.class) {
            return new TestResultNodeLogProvider(
                    (TestResultNode) adaptableObject);
        } else if (adapterType == ImageProvider.class) {
            return new TestResultNodeImageProvider(
                    (TestResultNode) adaptableObject);
        } else if (adapterType == TestResultNode.class) {
            return new TestResultNodePropertySource(
                    (TestResultNode)adaptableObject);
        } else if (adapterType == IPropertySource.class
                && adaptableObject instanceof TestResultNode) {
            return new TestResultNodePropertySource(
                    (TestResultNode)adaptableObject);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getAdapterList() {
        return m_types;
    }
}
