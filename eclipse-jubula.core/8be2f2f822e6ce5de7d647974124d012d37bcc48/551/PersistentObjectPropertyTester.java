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

import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;


/**
 * Property tester for persistent model objects.
 *
 * @author BREDEX GmbH
 * @created Mar 5, 2009
 */
public class PersistentObjectPropertyTester 
    extends AbstractBooleanPropertyTester {
    /** the id of the "isInCurrentProject" property */
    public static final String IS_IN_CUR_PROJECT = "isInCurrentProject"; //$NON-NLS-1$
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { 
        IS_IN_CUR_PROJECT };

    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        IPersistentObject po = 
            (IPersistentObject)receiver;
        if (property.equals(IS_IN_CUR_PROJECT)) {
            return  testIsInCurrentProject(po);
        }
        return false;
    }

    /**
     * @param po The persistent model object to test.
     * @return <code>true</code> if the given object is in the currently 
     *         open project. Otherwise <code>false</code>.
     */
    private boolean testIsInCurrentProject(IPersistentObject po) {
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        if (currentProject != null && po.getParentProjectId() != null) {
            return po.getParentProjectId().equals(currentProject.getId());
        }
        
        return false;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return IPersistentObject.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
