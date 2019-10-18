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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;

/**
 * @author BREDEX GmbH
 * @created Feb 10, 2011
 */
public class ShowFromClipboard extends AbstractClipboardHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        if (GeneralStorage.getInstance().getProject() != null) {
            String clipboardContent = getClipboardContents();
            String[] content = clipboardContent.split(SPLIT_TOKEN);

            if (content.length == 2) {
                String id = content[1];
                INodePO node = NodePM.getNode(GeneralStorage.getInstance()
                        .getProject().getId(), id);
                openEditor(node);
            }
        }
        return null;
    }
}
