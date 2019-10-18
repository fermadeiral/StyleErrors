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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;


/**
 * @author BREDEX GmbH
 * @created Mar 26, 2010
 */
public class RunningAutsViewLabelProvider extends LabelProvider {
    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        if (element instanceof AutIdentifier) {
            AutIdentifier autId = ((AutIdentifier)element);
            IProjectPO currentProject = GeneralStorage.getInstance()
                    .getProject();
            String displayText = autId.getExecutableName();
            if (currentProject != null
                    && AutAgentRegistration
                        .getAutForId(autId, currentProject) != null) {
                return displayText;
            }
            return displayText + " (unknown AUT ID)"; //$NON-NLS-1$

        }
        return super.getText(element);
    }
}
