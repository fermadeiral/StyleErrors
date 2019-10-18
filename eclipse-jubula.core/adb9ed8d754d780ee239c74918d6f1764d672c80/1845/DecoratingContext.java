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
package org.eclipse.jubula.client.teststyle.checks.contexts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marcell
 * @created Nov 5, 2010
 */
public abstract class DecoratingContext extends BaseContext {

    /**
     * @param cls
     * Class of this decorating context.
     */
    protected DecoratingContext(Class<?> cls) {
        super(cls);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> getAll() {
        // Empty list, because this is never called for the context.
        return new ArrayList<Object>();
    }

}
