/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.client.teststyle.impl.standard.checks;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.impl.standard.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 *
 */
// CHECKSTYLE:OFF: mustdo
public class TodoCheck extends BaseCheck {
    // CHECKSTYLE:ON: mustdo
    /** inactive attribute name - should also inactive nodes be checked */
    private static final String INACTIVE = "inactive"; //$NON-NLS-1$
    /** annotation attribute name - the String to search for in the names */
    private static final String ANNOTATIONSTRING = "annotation"; //$NON-NLS-1$
    /**
     * case sensitive attribute name - should the string be search cases
     * sensitive or not
     */
    private static final String CASESENSITIVE = "caseSensitive"; //$NON-NLS-1$
    /** the Seperator used for multiple searches */
    private static final String SEPERATORCHAR = ";"; //$NON-NLS-1$
    /** name of the node for the description */
    private String m_nodeName;

    @Override
    public String getDescription() {
        // CHECKSTYLE:OFF: mustdo
        return NLS.bind(Messages.ToDoCheckDescription, m_nodeName);
        // CHECKSTYLE:ON: mustdo
    }

    @Override
    public boolean hasError(Object obj) {
        if (obj instanceof IPersistentObject) {
            IPersistentObject node = (IPersistentObject) obj;
            boolean isActive = true;
            if (node instanceof INodePO) {
                isActive = ((INodePO) node).isActive();
            }
            if (isActive || getInactive()) {
                String name = node.getName();
                String annotation = getAnnotationString();
                String[] split = StringUtils.split(annotation, SEPERATORCHAR);
                m_nodeName = name;
                for (String searchString : split) {
                    if (StringUtils.isBlank(searchString)) {
                        continue;
                    }
                    if (getCaseSensitive()
                            && StringUtils.contains(name, searchString)) {
                        return true;
                    } else if (!getCaseSensitive() && StringUtils
                            .containsIgnoreCase(name, searchString)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return value if inactive nodes should be checked
     */
    private boolean getInactive() {
        return Boolean.parseBoolean(getAttributeValue(INACTIVE));
    }

    /**
     * @return value if the name should be checked case sensitive
     */
    private boolean getCaseSensitive() {
        return Boolean.parseBoolean(getAttributeValue(CASESENSITIVE));
    }

    /**
     * @return the String to search for
     */
    private String getAnnotationString() {
        return StringUtils.defaultIfBlank(getAttributeValue(ANNOTATIONSTRING),
                StringConstants.EMPTY);
    }

}
