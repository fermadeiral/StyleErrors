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
package org.eclipse.jubula.toolkit.swing;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.toolkit.swing.internal.SwingToolkitInfo;

/**
 * Swing toolkit information
 * 
 * @noextend This class is not intended to be extended by clients.
 * @since 4.0
 */
public class SwingToolkit {
    /** Constructor */
    private SwingToolkit() {
        super();
    }

    /**
     * Returns a new instance of toolkit information. Use this method if you
     * want to modify the toolkit by adding tester classes.
     * 
     * @return a toolkit information
     * @since 4.0
     */
    @NonNull
    public static ToolkitInfo createToolkitInformation() {
        return new SwingToolkitInfo();
    }
}