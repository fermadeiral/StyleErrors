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
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created Apr 7, 2010
 */
public class CompNamesProposal implements IContentProposal {

    /** display value */
    private String m_label;
    /** model value */
    private String m_content;
    
    /**
     * construct a content proposal from the ComponentNamePO
     * @param data ComponentNamePO
     */
    public CompNamesProposal(IComponentNamePO data) {
        
        StringBuilder label = new StringBuilder("G"); //$NON-NLS-1$
        label.append(data.getName());
        if (!(data.getComponentType() == null || StringConstants.EMPTY
                .equals(data.getComponentType()))) {
            label.append(StringConstants.SPACE);
            label.append(StringConstants.LEFT_PARENTHESIS);
            label.append(StringHelper.getInstance().get(
                    data.getComponentType(), true));
            label.append(StringConstants.RIGHT_PARENTHESIS);
        }
        m_label = label.toString();
        m_content = data.getName();
    }
    /**
     * {@inheritDoc}
     */
    public String getContent() {            
        return m_content;
    }

    /**
     * {@inheritDoc}
     */
    public int getCursorPosition() {            
        return m_content.length();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return m_label;
    }
    
}
