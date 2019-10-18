/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamText;
import org.eclipse.swt.widgets.Combo;

/**
 * Data class for storing the set of execution Test Cases.
 *
 * @author BREDEX GmbH
 */
public class ReplaceExecTestCaseData extends ChooseTestCaseData {

    /** The parameter description map between new as key and old as value. */
    private Map<IParamDescriptionPO, IParamDescriptionPO> m_newOldParamMap;

    /** The (new unmatched parameter) => (its value) map */
    private Map<IParamDescriptionPO, String> m_unmatchedValuesMap =
            new HashMap<>();
    
    /**
     * @param execTestCases The set of execution Test Cases, for which the
     *                      usage of the specification Test Case has to changed.
     */
    public ReplaceExecTestCaseData(Set<IExecTestCasePO> execTestCases) {
        super(execTestCases);
    }

    /**
     * Set the new specification Test Case and initialize the map between
     * the new and old parameters with null for the old parameter name.
     * {@inheritDoc}
     */
    @Override
    public void setNewSpecTestCase(ISpecTestCasePO newSpecTestCase) {
        super.setNewSpecTestCase(newSpecTestCase);
        List<String> oldParamNames = new ArrayList<String>();
        if (newSpecTestCase != null) {
            int size = newSpecTestCase.getParameterListSize();
            for (int i = 0; i < size; i++) {
                oldParamNames.add(null);
            }
        }
        setOldParameterNames(oldParamNames);
    }

    /**
     * @param newParamDesc The new parameter description.
     * @return An array of strings containing all parameter names from the old
     *         specification Test Case with the same type as the given parameter description.
     */
    public List<String> getOldParameterNamesByType(
            IParamDescriptionPO newParamDesc) {
        List<String> matchingNames = new ArrayList<String>();
        for (IParamDescriptionPO oldParamDesc: getOldSpecTestCase()
                .getParameterList()) {
            String newType = newParamDesc.getType();
            if ("java.lang.String".equals(newType) //$NON-NLS-1$
                    || newParamDesc.getType().equals(oldParamDesc.getType())) {
                matchingNames.add(oldParamDesc.getName());
            }
        }
        return matchingNames;
    }

    /**
     * @param oldParamNameCombos The list of combo boxes with the selected
     *                           old parameter names.
     * @see #getNewOldParamMap()
     */
    public void setOldParameterNamesWithCombos(List<Combo> oldParamNameCombos) {
        List<String> oldParamNames =
                new ArrayList<String>(oldParamNameCombos.size());
        for (Combo combo: oldParamNameCombos) {
            String oldParamName = null;
            if (combo != null) {
                oldParamName = combo.getText();
            }
            oldParamNames.add(oldParamName);
        }
        setOldParameterNames(oldParamNames);
    }
    
    /**
     * @param textFields The ParamDesc => Text map, will overwrite the current
     */
    public void setUnmatchedValuesMap(
            Map<IParamDescriptionPO, CheckedParamText> textFields) {
        m_unmatchedValuesMap.clear();
        for (IParamDescriptionPO desc : textFields.keySet()) {
            CheckedParamText text = textFields.get(desc);
            if (text.isValid()) {
                m_unmatchedValuesMap.put(desc, text.getText());
            } else {
                m_unmatchedValuesMap.put(desc, StringUtils.EMPTY);
            }
        }
    }

    /**
     * Set the selected old parameters by a list of names. Use
     * {@link #getNewOldParamMap()} to get the result.
     * @param oldParamNames The list of selected parameter names in the order
     *                      of the new parameter list.
     */
    private void setOldParameterNames(List<String> oldParamNames) {
        ISpecTestCasePO newSpec = getNewSpecTestCase();
        Iterator<String> it = oldParamNames.iterator();
        Map<IParamDescriptionPO, IParamDescriptionPO> newOldParamMap =
                new HashMap<IParamDescriptionPO, IParamDescriptionPO>();
        for (IParamDescriptionPO newParamDesc: newSpec.getParameterList()) {
            IParamDescriptionPO oldParamDesc = null;
            String oldName = it.next();
            if (oldName != null) {
                oldParamDesc = getOldSpecTestCase()
                        .getParameterForName(oldName);
            }
            newOldParamMap.put(newParamDesc, oldParamDesc);
        }
        this.m_newOldParamMap = newOldParamMap;
    }

    /**
     * @return The map between new parameter descriptions as key and old as value.
     *         The old parameter description is null, if it is unmatched.
     */
    public Map<IParamDescriptionPO, IParamDescriptionPO> getNewOldParamMap() {
        return m_newOldParamMap;
    }

    /**
     * @return The map between unmatched new parameter descriptions as key and values.
     */
    public Map<IParamDescriptionPO, String> getUnmatchedValuesMap() {
        return m_unmatchedValuesMap;
    }
    
    /**
     * @return True, if both the new and the old Test Case do not have parameters,
     *         otherwise false.
     */
    public boolean haveNewAndOldTestCasesNoParameters() {
        return getNewSpecTestCase().getParameterListSize() == 0
                && getOldSpecTestCase().getParameterListSize() == 0;
    }

    /**
     * @return True, if one or more new parameters are not matched,
     *         otherwise false.
     */
    public boolean hasUnmatchedNewParameters() {
        return m_newOldParamMap.values().contains(null);
    }

    /**
     * @return True, if not all old parameters are matched to a new parameter,
     *         otherwise false.
     */
    public boolean hasUnmatchedOldParameters() {
        // create a temporary list from the old parameter list by copying
        List<IParamDescriptionPO> tmpParams =
                new ArrayList<IParamDescriptionPO>(
                        getOldSpecTestCase().getParameterList());
        // remove the selected old parameters from the temporary list
        tmpParams.removeAll(m_newOldParamMap.values());
        // there are unmatched parameters, if the temporary list is not empty
        return !tmpParams.isEmpty();
    }

    /**
     * @return True, if all new parameters have no matched old parameters, i.e.
     *         all values of the parameter map {@link #getNewOldParamMap()} are null,
     *         otherwise false.
     */
    public boolean hasNoMatching() {
        Collection<IParamDescriptionPO> values = m_newOldParamMap.values();
        return Collections.frequency(values, null) == values.size();
    }
    
}
