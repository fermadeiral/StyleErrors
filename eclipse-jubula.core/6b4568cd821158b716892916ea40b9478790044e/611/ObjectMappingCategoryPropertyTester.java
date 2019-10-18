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
package org.eclipse.jubula.client.ui.rcp.propertytester;

import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;


/**
 * Property tester for Object Mapping Category.
 * 
 * @author BREDEX GmbH
 * @created Mar 4, 2009
 */
public class ObjectMappingCategoryPropertyTester 
    extends AbstractBooleanPropertyTester {
    /** the id of the "isTopLevel" property */
    public static final String IS_TOP_LEVEL = "isTopLevel"; //$NON-NLS-1$

    /** the id of the "isEmpty" property */
    public static final String IS_EMPTY = "isEmpty"; //$NON-NLS-1$

    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { IS_EMPTY,
        IS_TOP_LEVEL };

    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        IObjectMappingCategoryPO category = (IObjectMappingCategoryPO)receiver;
        if (property.equals(IS_TOP_LEVEL)) {
            return testIsTopLevel(category);
        } else if (property.equals(IS_EMPTY)) {
            return testEmpty(category);
        }
        return false;
    }

    /**
     * 
     * @param category The Object Mapping Category to test.
     * @return <code>true</code> if the given category is a top-level category. 
     *         Otherwise <code>false</code>.
     */
    private boolean testIsTopLevel(
            IObjectMappingCategoryPO category) {

        return category.getParent() == null;
    }

    /**
     * 
     * @param category The Object Mapping Category to test.
     * @return <code>true</code> if the given category is empty. 
     *         Otherwise <code>false</code>.
     */
    private boolean testEmpty(IObjectMappingCategoryPO category) {

        return category.getUnmodifiableAssociationList().isEmpty() 
            && category.getUnmodifiableCategoryList().isEmpty();
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return IObjectMappingCategoryPO.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
