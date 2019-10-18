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
package org.eclipse.jubula.rc.common.classloader;

import java.io.IOException;
import java.net.URL;

/**
 * This interface defines the method for resolving a URL, which depends on
 * eclipse or not.
 */
public interface IUrlLocator {

    /**
     * @param url The URL, which will be converted
     * @return The converted URL, which can be used by {@link java.io.File}.
     * @throws IOException
     */
    public URL convertUrl(URL url) throws IOException;

}
