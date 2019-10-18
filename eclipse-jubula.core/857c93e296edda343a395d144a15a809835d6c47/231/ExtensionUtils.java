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
package org.eclipse.jubula.client.ui.rcp.extensions;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * This class contains some utils which are important when dealing with
 * extension points.
 * 
 * @author BREDEX GmbH
 * @created Oct 27, 2010
 */
public class ExtensionUtils {

    /** Private constructor for this utility class. */
    private ExtensionUtils() {
        // Will never be called, so nothing happens here
    }

    /**
     * @param id
     *            The id of the extension point.
     * @return All extensions of the specified extension point id.
     */
    /* package */ static IExtension[] getExtensions(String id) {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = 
            extensionRegistry.getExtensionPoint(id);
        return extensionPoint.getExtensions();
    }
}
