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

import java.net.URL;

/**
 * The default default implementation for the URL locator,
 * which do not change the given URL.
 */
public class DefaultUrlLocator implements IUrlLocator {
    /**
     * @return The same as the given URL.
     * {@inheritDoc}
     */
    public URL convertUrl(URL url) {
        return url;
    }
}