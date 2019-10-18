/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.rc.common.adaptable;

/**
 * This renderer should wrap components using a registered adapter.
 */
public interface ITextRendererAdapter {
    /**
     * @return Return the shown / rendered text of a renderer. May also return
     *         <code>null</code> which will be treated as an empty String.
     */
    public String getText();
}
