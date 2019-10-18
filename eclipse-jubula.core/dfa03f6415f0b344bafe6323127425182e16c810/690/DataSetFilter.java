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
package org.eclipse.jubula.client.ui.rcp.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.ui.internal.misc.StringMatcher;

/** This class has implemented the searching on data set view */
public class DataSetFilter extends ViewerFilter {

    /** .* */
    private static final String DOTSTAR = StringConstants.DOT
            + StringConstants.STAR;

    /** The search text */
    private String m_search = null;

    /** The matcher */
    private StringMatcher m_matcher = null;

    /** The regular matcher */
    private Matcher m_regMatcher;

    /**
     * @param origText is the new pattern
     */
    public void setSearchText(String origText) {
        if (origText != null) {
            m_search = DOTSTAR + origText + DOTSTAR;
            try {
                Pattern p = Pattern.compile(m_search);
                m_regMatcher = p.matcher(StringConstants.EMPTY);
            } catch (PatternSyntaxException e) {
                m_regMatcher = null;
            }
        }
        String text = origText == null ? StringConstants.STAR
                : StringConstants.STAR + origText + StringConstants.STAR;
        m_matcher = new StringMatcher(text, true, false);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (StringUtils.isEmpty(m_search) || !(element instanceof IDataSetPO)) {
            return true;
        }

        for (String value : ((IDataSetPO)element).getColumnStringValues()) {
            if (value == null) {
                continue;
            }
            if (m_regMatcher != null) {
                m_regMatcher.reset(value);
                if (m_regMatcher.matches()) {
                    return true;
                }
            } else if (m_matcher != null && m_matcher.match(value)) {
                return true;
            }
        }
        return false;
    }
}
