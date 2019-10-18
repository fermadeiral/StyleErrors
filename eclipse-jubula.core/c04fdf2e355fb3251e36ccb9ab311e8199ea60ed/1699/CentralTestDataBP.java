/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.businessprocess;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;

/**
 * @author BREDEX GmbH
 * @created Nov 03, 2011
 */
public class CentralTestDataBP {

    /**
     * Private constructor for utility class.
     */
    private CentralTestDataBP() {
        // nothing to initialize
    }
    
    /**
     * 
     * @param category The starting category. Must no be <code>null</code>.
     * @return all ancestors of the given category, in order, starting with 
     *         the parent of the given category.
     */
    public static List<ITestDataCategoryPO> getAncestors(
            ITestDataCategoryPO category) {

        Validate.notNull(category);
        List<ITestDataCategoryPO> ancestorList = 
                new LinkedList<ITestDataCategoryPO>();
        ITestDataCategoryPO currentCategory = category.getParent();

        while (currentCategory != null) {
            ancestorList.add(currentCategory);
            currentCategory = currentCategory.getParent();
        }
        
        return ancestorList;
    }
    
}
