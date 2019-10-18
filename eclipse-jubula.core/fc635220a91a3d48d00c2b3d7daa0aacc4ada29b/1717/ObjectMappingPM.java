/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.client.core.persistence;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.PoMaker;

/**
 * @author BREDEX GmbH
 */
public class ObjectMappingPM {

    /**
     * Utility
     */
    private ObjectMappingPM() {
        // Utility
    }
    
    /**
     * deletes the {@link IObjectMappingCategoryPO} and its assoc
     * @param categories the categories to delete
     */
    public static void deleteOMCategories(
            Collection<IObjectMappingCategoryPO> categories) {
        if (categories.size() < 1) {
            return;
        }
        EntityManager session = Persistor.instance().openSession();
        EntityTransaction transaction = session.getTransaction();
        transaction.begin();
        final Query q = session.createQuery("DELETE FROM " //$NON-NLS-1$
                + PoMaker.getObjectMappingAssoziationClass().getSimpleName()
                + " assoc WHERE assoc.hbmCategory IN :ids"); //$NON-NLS-1$
        q.setParameter("ids", //$NON-NLS-1$
                categories.stream().map(IObjectMappingCategoryPO::getId)
                        .collect(Collectors.toList()));
        q.executeUpdate();
        transaction.commit();
    }
}
