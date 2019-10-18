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
package org.eclipse.jubula.client.archive.errorhandling;

import java.util.List;

/**
 * Makes no attempt whatsoever to resolve conflicts.
 *
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public class NullProjectNameConflictResolver implements
        IProjectNameConflictResolver {

    /**
     * {@inheritDoc}
     */
    public String resolveNameConflict(List<String> availableNames) {
        return null;
    }

}
