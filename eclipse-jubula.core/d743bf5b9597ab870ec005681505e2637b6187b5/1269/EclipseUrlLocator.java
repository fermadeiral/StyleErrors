/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.rcp.common.classloader;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jubula.rc.common.classloader.IUrlLocator;

/**
 * This class implements the URL locator for Eclipse environments.
 */
public class EclipseUrlLocator implements IUrlLocator {

    /**
     * @return The given URL converted by Eclipse runtime method {@link FileLocator#resolve(URL)}.
     * {@inheritDoc}
     * @throws IOException
     */
    public URL convertUrl(URL url) throws IOException {
        return FileLocator.resolve(url);
    }

}
