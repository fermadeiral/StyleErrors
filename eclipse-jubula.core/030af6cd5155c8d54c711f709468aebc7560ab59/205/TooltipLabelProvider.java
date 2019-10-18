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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider;

import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * A label provider implementation which, by default, 
 * uses an element's toString value for its text and null for its image.
 * @author BREDEX GmbH
 * @created 06.07.2004
 */
public class TooltipLabelProvider extends GeneralLabelProvider {
    /** the multi line tooltip prefix */
    private static final String MULTI_LINE_TOOLTIP_PREFIX = 
            StringConstants.MINUS + StringConstants.SPACE;

    /** {@inheritDoc} */
    public String getToolTipText(Object element) {
        if (element instanceof INodePO) {
            INodePO node = (INodePO)element;
            if (ProblemFactory.hasProblem(node)) {
                Set<IProblem> worstProblems = ProblemFactory
                        .getWorstProblems(node.getProblems());
                StringBuilder sb = new StringBuilder();
                String prefix = StringConstants.EMPTY;
                int size = worstProblems.size();
                if (size > 1) {
                    prefix = MULTI_LINE_TOOLTIP_PREFIX;
                }
                int count = 0;
                for (IProblem problem : worstProblems) {
                    sb.append(prefix);
                    sb.append(problem.getTooltipMessage());
                    if (++count < size) {
                        sb.append(StringConstants.NEWLINE);
                    }
                }
                return sb.toString();
            }
        } else if (element instanceof IComponentNamePO) {
            IProblem prob = ((IComponentNamePO) element).getTypeProblem();
            if (prob != null) {
                return prob.getTooltipMessage();
            }
        }

        return super.getToolTipText(element);
    }
}