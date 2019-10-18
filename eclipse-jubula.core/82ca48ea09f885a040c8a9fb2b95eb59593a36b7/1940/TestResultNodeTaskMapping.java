/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.mapping;

import java.util.Date;

import org.eclipse.jubula.client.alm.mylyn.ui.i18n.Messages;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.controllers.propertysources.TestResultNodePropertySource;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 *  Mapping from Test Result node to task
 */
public class TestResultNodeTaskMapping extends TaskMapping {
    /**
     * the test result node
     */
    private final TestResultNode m_node;

    /**
     * Constructor
     * 
     * @param node
     *            the node
     */
    public TestResultNodeTaskMapping(TestResultNode node) {
        m_node = node;
    }

    /** {@inheritDoc} */
    public Date getCreationDate() {
        return new Date();
    }

    /** {@inheritDoc} */
    public String getDescription() {
        TestResultNodePropertySource propSource = 
                new TestResultNodePropertySource(m_node);
        StringBuilder sb = new StringBuilder();
        int count = 1;
        String oldCat = null;
        for (IPropertyDescriptor pd : propSource.getPropertyDescriptors()) {
            String newCat = pd.getCategory().toUpperCase();
            if (!newCat.equals(oldCat)) {
                sb.append(newCat);
                sb.append(StringConstants.NEWLINE);
                oldCat = newCat;
            }
            sb.append(StringConstants.SPACE);
            sb.append(StringConstants.MINUS);
            sb.append(StringConstants.SPACE);
            sb.append(pd.getDisplayName());
            sb.append(StringConstants.COLON);
            sb.append(StringConstants.SPACE);
            sb.append(propSource.getPropertyValue(pd.getId()));
            if (count != propSource.getPropertyDescriptors().length) {
                sb.append(StringConstants.NEWLINE);
            }
            count++;
        }
        Object[] params = { m_node.getTypeOfNode(), sb.toString() };
        return NLS.bind(Messages.TaskDescription, params);
    }

    /** {@inheritDoc} */
    public String getSummary() {
        Object[] params = { m_node.getTypeOfNode(), m_node.getName(),
                m_node.getStatusString() };
        return NLS.bind(Messages.TaskTitle, params);
    }
}