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
package org.eclipse.jubula.client.core.businessprocess;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;


/**
 * Factory for creating Component Name mappers.
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2009
 */
public final class CompNameCacheFactory {

    /**
     * Private Constructor for utility class
     */
    private CompNameCacheFactory() {
        // private constructor for utility class
    }
    
    /**
     * 
     * @param node The PO for which to find an appropriate Component Name 
     *             mapper. May not be <code>null</code>.
     * @return a Component Name mapper capable of managing Component Names for
     *         the given PO.
     */
    public static IWritableComponentNameCache createCompNameCache(
            IPersistentObject node)
        throws IllegalArgumentException {
        
        Validate.notNull(node);
        
        if (node instanceof ISpecTestCasePO
            || node instanceof ITestSuitePO) {
            return new TestCaseCompNameCache(node);
        } else if (node instanceof IAUTMainPO) {
            return new ObjectMappingCompNameCache(node);
        } else if (node instanceof IProjectPO) {
            return new ProjectCompNameCache(node);
        }
        return new BasicCompNameCache(node);
    }

}
