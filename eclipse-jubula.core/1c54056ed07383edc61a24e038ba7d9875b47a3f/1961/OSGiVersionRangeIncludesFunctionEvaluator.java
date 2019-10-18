/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Version;

/**
 * Function that performs a version range check
 */
public class OSGiVersionRangeIncludesFunctionEvaluator 
    extends AbstractFunctionEvaluator {
    /** {@inheritDoc} */
    public String evaluate(String[] arguments) throws InvalidDataException {
        boolean includes = false;
        validateParamCount(arguments, 2);
        final String versionRange = arguments[0];
        final String version = arguments[1];
        
        try {
            includes = new VersionRange(versionRange)
                    .includes(new Version(version));
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(e.getLocalizedMessage(),
                    MessageIDs.E_FUNCTION_EVAL_ERROR);
        }
        return String.valueOf(includes);
    }
}
