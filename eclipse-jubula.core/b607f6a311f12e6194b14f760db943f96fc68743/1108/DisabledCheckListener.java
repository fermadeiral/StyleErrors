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
package org.eclipse.jubula.rc.common.listener;

import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

/**
 * This check listener does nothing - may be used in toolkits that do not (yet)
 * support checking
 * 
 * @author BREDEX GmbH
 */
public class DisabledCheckListener implements AUTEventListener {
    /** {@inheritDoc} */
    public void cleanUp() {
    // empty
    }

    /** {@inheritDoc} */
    public boolean highlightComponent(IComponentIdentifier comp) {
        // empty
        return false;
    }

    /** {@inheritDoc} */
    public void update() {
    // empty
    }

    /** {@inheritDoc} */
    public long[] getEventMask() {
        return null;
    }
}
