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
package org.eclipse.jubula.client.teststyle.checks;


/**
 * @author marcell
 * @created Nov 5, 2010
 */
public abstract class DecoratingCheck extends BaseCheck {

    /**
     * {@inheritDoc}
     */
    public final String getDescription() {
        // Always return null, because they're not in the problems marker
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean hasError(Object obj) {
        return false; // Will never be called.
    }

    /**
     * @param obj
     *            The object that the decorator calls to check for it.
     * @return Should the checked object be decorated with a symbol / prefix /
     *         suffix?
     */
    public abstract boolean decorate(Object obj);

}
