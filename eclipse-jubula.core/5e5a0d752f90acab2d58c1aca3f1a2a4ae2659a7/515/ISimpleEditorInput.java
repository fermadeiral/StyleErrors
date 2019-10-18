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
package org.eclipse.jubula.client.ui.rcp.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;

/**
 * Editor input with a <code>String</code> as content. Use for read-only text.
 *
 * @author BREDEX GmbH
 * @created Feb 9, 2007
 */
public interface ISimpleEditorInput extends IEditorInput {
    
    /**
     * 
     * @return the content for this input
     */
    public String getContent() throws CoreException;
}
