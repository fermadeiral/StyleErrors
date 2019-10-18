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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jubula.client.core.businessprocess.CalcTypes;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;

/**
 * A dialog warning a user that some local changes will lead
 *      to Component Name Type problems. The user can decide to
 *      revert the problematic changes or continue with them.
 * @author BREDEX GmbH
 *
 */
public class CNTypeProblemDialog {

    /** The type calculator */
    private CalcTypes m_calc;
    
    /** The used local Component Name Cache */
    private IComponentNameCache m_cache;
    
    /**
     * Constructor
     * @param calc the type calculator
     * @param cache the local cache
     */
    public CNTypeProblemDialog(CalcTypes calc,
            IComponentNameCache cache) {
        m_calc = calc;
        m_cache = cache;
    }
    
    /**
     * Presents the user with the Dialog and returns their decision
     * @param problems the CN Type problems
     * @return whether continue with the problem-causing action
     */
    public boolean canCommence(Map<String, ProblemType> problems) {
        List<String> info;
        StringBuilder msg = new StringBuilder();
        int num = 0;
        for (String guid : problems.keySet()) {
            info = m_calc.getProblemInfo(guid);
            IComponentNamePO cN = m_cache.getResCompNamePOByGuid(guid);
            if (cN == null) {
                continue;
            }
            msg.append(cN.getName());
            msg.append(StringConstants.COLON);
            msg.append(StringConstants.SPACE);
            if (problems.get(guid).equals(
                    ProblemType.REASON_INCOMPATIBLE_MAP_TYPE)) {
                msg.append(StringConstants.NEWLINE);
                msg.append("Mapped to: "); //$NON-NLS-1$
                msg.append(StringConstants.QUOTE);
                msg.append(info.get(0));
                msg.append(StringConstants.QUOTE);
                msg.append(StringConstants.COMMA);
                msg.append(StringConstants.SPACE);
                msg.append("used as: "); //$NON-NLS-1$
                msg.append(StringConstants.QUOTE);
                msg.append(info.get(1));
                msg.append(StringConstants.QUOTE);
                msg.append(StringConstants.NEWLINE);
            } else {
                msg.append(StringConstants.NEWLINE);
                for (int i = 0; i < 6; i++) {
                    msg.append(StringConstants.SPACE);
                }
                msg.append("Used as "); //$NON-NLS-1$
                msg.append(StringConstants.QUOTE);
                msg.append(info.get(0));
                msg.append(StringConstants.QUOTE);
                msg.append(" and "); //$NON-NLS-1$
                msg.append(StringConstants.QUOTE);
                msg.append(info.get(1));
                msg.append(StringConstants.QUOTE);
                msg.append(StringConstants.NEWLINE);
            }
            num++;
            if (num > 10) {
                msg.append(StringConstants.DOT);
                msg.append(StringConstants.DOT);
                msg.append(StringConstants.DOT);
                msg.append(StringConstants.NEWLINE);
                break;
            }
        }
        
        String message = NLS.bind(Messages.IncompatiblePairChangeDialogText,
                msg.toString());
        
        MessageDialog dialog = new MessageDialog(null, 
                Messages.IncompatiblePairChangeDialogTitle,
            null, 
            message, MessageDialog.QUESTION, new String[] {
                Messages.DialogMessageButton_YES,
                Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog.getReturnCode() == 0;
    }
    
    /**
     * Checks whether certain local changes introduce new CN type problems
     * If yes, then asks the user whether the operation can commence
     * @param cache the local cache
     * @param workTC the working version
     * @return whether the operation can commence
     */
    public static boolean noProblemOrIgnore(
            IWritableComponentNameCache cache, INodePO workTC) {
        CalcTypes calc = new CalcTypes(cache, workTC);
        calc.calculateTypes();
        Map<String, ProblemType> problems = cache.getNewProblems(calc);
        if (!problems.isEmpty() && !new CNTypeProblemDialog(
                calc, cache).canCommence(problems)) {
            return false;
        }
        cache.storeLocalProblems(calc);
        return true;
    }
}
