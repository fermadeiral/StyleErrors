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
package org.eclipse.jubula.client.internal;

import java.util.concurrent.Exchanger;

/** @author BREDEX GmbH */
public class Synchronizer extends Exchanger<Object> {
    /** the singleton instance */
    private static Synchronizer instance = null;

    /** Constructor */
    private Synchronizer() {
        // currently empty
    }

    /** @return singleton instance */
    public static synchronized Synchronizer instance() {
        if (instance == null) {
            instance = new Synchronizer();
        }

        return instance;
    }
}
