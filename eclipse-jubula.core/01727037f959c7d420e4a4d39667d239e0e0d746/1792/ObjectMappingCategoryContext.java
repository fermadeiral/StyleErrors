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
package org.eclipse.jubula.client.teststyle.checks.contexts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;


/**
 * @author marcell
 * @created Dec 3, 2010
 */
public class ObjectMappingCategoryContext extends BaseContext {

    /**
     * @param cls
     */
    public ObjectMappingCategoryContext() {
        super(IObjectMappingCategoryPO.class);
    }

    @Override
    public List<Object> getAll() {
        List<Object> tmp = new ArrayList<Object>();
        Set<IAUTMainPO> auts =
                GeneralStorage.getInstance().getProject().getAutMainList();
        for (IAUTMainPO aut : auts) {
            tmp.addAll(handleObjectMapping(aut.getObjMap()));
        }
        return tmp;
    }

    /**
     * @param objMap
     *            The object mapping object which contains the objects I want.
     * @return A list of the categories.
     */
    private List<Object> handleObjectMapping(IObjectMappingPO objMap) {
        List<Object> tmp = new ArrayList<Object>();
        
        IObjectMappingCategoryPO[] roots = {
            objMap.getMappedCategory(),
            objMap.getUnmappedLogicalCategory(),
            objMap.getUnmappedTechnicalCategory(),
        };
        
        for (IObjectMappingCategoryPO root : roots) {
            tmp.addAll(getCategories(root));
        }
        
        return tmp;
    }

    /**
     * @param rt
     *            The category which children should be observed to get all
     *            categories.
     * @return The list of these children + the parent.
     */
    private List<Object> getCategories(IObjectMappingCategoryPO rt) {
        List<Object> tmp = new ArrayList<Object>();
        tmp.add(rt);
        for (IObjectMappingCategoryPO cat : rt.getUnmodifiableCategoryList()) {
            tmp.addAll(getCategories(cat));
        }
        return tmp;
    }

    @Override
    public String getName() {
        return Messages.ContextOMCategoryName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextOMCategoryDescription;
    }

}
