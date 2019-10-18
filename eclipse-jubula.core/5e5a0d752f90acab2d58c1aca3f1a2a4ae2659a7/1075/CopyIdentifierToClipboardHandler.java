/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.ui.handlers;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.api.ui.utils.OMExport;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.utils.ObjectMappingUtil;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 07.10.2014
 */
public class CopyIdentifierToClipboardHandler extends AbstractHandler {
    
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(CopyIdentifierToClipboardHandler.class);
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) 
                HandlerUtil.getCurrentSelection(event);
        if (selection != null && !selection.isEmpty()) {
            Object selectedElement = selection.getFirstElement();
            if (selectedElement instanceof IObjectMappingAssoziationPO) {
                IObjectMappingAssoziationPO assoziation = 
                        (IObjectMappingAssoziationPO) selectedElement;
                IComponentIdentifier compIdentifier = ObjectMappingUtil
                        .createCompIdentifierFromAssoziation(assoziation);
                try {
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    String serialization = OMExport.getSerialization(
                            (ComponentIdentifier) compIdentifier);
                    Shell activeShell = HandlerUtil.getActiveShell(event);
                    Display display = activeShell.getDisplay();
                    if (display != null) {
                        new Clipboard(display).setContents(
                                new String[]{serialization},
                                new Transfer[]{textTransfer});
                    }
                } catch (IOException e) {
                    log.error(
                        "Error while copying component identifier to clipboard", //$NON-NLS-1$
                        e);
                }
            }
        }
        return null;
    }
}