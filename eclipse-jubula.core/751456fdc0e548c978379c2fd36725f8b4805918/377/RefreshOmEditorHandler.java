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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.rcp.businessprocess.OMEditorBP;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 31.05.2006
 */
public class RefreshOmEditorHandler extends AbstractHandler {
    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        ObjectMappingMultiPageEditor omEditor = (ObjectMappingMultiPageEditor)
            HandlerUtil.getActiveEditor(event);
        OMEditorBP omBP = new OMEditorBP(omEditor);
        omBP.collectNewLogicalComponentNames();
        return null;
    } 
}