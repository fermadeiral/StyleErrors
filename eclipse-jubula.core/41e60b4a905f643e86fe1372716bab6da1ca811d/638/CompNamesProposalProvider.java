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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jubula.client.core.businessprocess.CompNameTypeManager;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created Apr 7, 2010
 */
public class CompNamesProposalProvider implements IContentProposalProvider {
    /**
     * @author BREDEX GmbH
     * @created 16.08.2005
     */
    private class JBComparator implements Comparator<IComponentNamePO> {

        /**
         * @param element0 element 0.
         * @param element1 element 1. 
         * @return a negative integer, zero, or a positive integer as the
         *         first argument is less than, equal to, or greater than the
         *         second. 
         */
        @SuppressWarnings("synthetic-access")
        public int compare(IComponentNamePO element0, 
                IComponentNamePO element1) {
            
            String name0 = element0.getName() == null ? StringConstants.EMPTY
                    : element0.getName();
            String name1 = element1.getName() == null ? StringConstants.EMPTY
                    : element1.getName();
            String type0 = element0.getComponentType() == null 
                ? StringConstants.EMPTY : element0.getComponentType();
            String type1 = element1.getComponentType() == null 
                ? StringConstants.EMPTY : element1.getComponentType();

            // Sorting:
            // 1st: types       (alphabetical componentTypes)
            // 2nd: names       (alphabetical componentNames)
            
            if (!type0.equals(type1)) {
                StringHelper helper = StringHelper.getInstance();
                return helper.get(type0, true).compareTo(
                    helper.get(type1, true));
            }            
            return name0.toLowerCase().compareTo(name1.toLowerCase());
        }
    }

    /** used for looking up Component Names */
    private IComponentNameCache m_compNameCache;

    /** 
     * Component Type for which to provide proposals. Only Component Names 
     * with a compatible type will be proposed. This value is *not* set in the
     * constructor because it never has a meaningful value until after 
     * initialization is complete.
     */
    private String m_typeFilter = StringConstants.EMPTY;

    /**
     * Constructor
     * 
     * @param compNameCache Used for looking up Component Names.
     */
    public CompNamesProposalProvider(IComponentNameCache compNameCache) {
        m_compNameCache = compNameCache;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("synthetic-access")
    public IContentProposal[] getProposals(final String contents,
            int position) {

        List<IComponentNamePO> compNamesList =
                new ArrayList<>();
        final Long currentProjectId = 
            GeneralStorage.getInstance().getProject() != null 
                ? GeneralStorage.getInstance().getProject().getId() : null;
        final String subString;
        if (position == 0) {
            subString = contents;
        } else {
            subString = contents.substring(0, position);
        }
        
        for (IComponentNamePO cN : m_compNameCache.getAllCompNamePOs()) {
            if (StringUtils.isEmpty(cN.getName()) 
                    || !cN.getName().startsWith(subString)
                    || cN.getParentProjectId() == null
                    || !cN.getParentProjectId().equals(
                                   currentProjectId)) {
                continue;
            }
            if (CompNameTypeManager.mayBeCompatible(cN, m_typeFilter)) {
                compNamesList.add(cN);
            }
        }

        Collections.sort(compNamesList, new JBComparator());

        List<IContentProposal> proposals = 
            new ArrayList<IContentProposal>(compNamesList.size());
        

        for (IComponentNamePO data : compNamesList) {
            proposals.add(new CompNamesProposal(data));
        }
        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

    /**
     * 
     * @param typeFilter The new filter to use. Only Component Names 
     *                   with a type that is compatible with the given type 
     *                   filter will be proposed. 
     */
    public void setTypeFilter(String typeFilter) {
        m_typeFilter = typeFilter;
    }

    /**
     * 
     * @param compNameCache The new cache to use.
     */
    public void setComponentNameCache(IComponentNameCache compNameCache) {
        m_compNameCache = compNameCache;
    }
}
