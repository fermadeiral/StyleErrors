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
package org.eclipse.jubula.client.core.functions;

import java.util.UUID;

/**
 * Function that generates a random Java UUID
 */
public class UUIDFunctionEvaluator implements IFunctionEvaluator {
    /** {@inheritDoc} */
    public String evaluate(String[] arguments) {
        return UUID.randomUUID().toString();
    }
}
