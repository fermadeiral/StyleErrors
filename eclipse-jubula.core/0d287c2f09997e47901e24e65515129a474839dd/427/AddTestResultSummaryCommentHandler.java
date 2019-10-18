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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.TestresultSummaryBP;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.dialogs.EnterCommentAndDetailsDialog;
import org.eclipse.jubula.client.ui.handlers.AbstractTestResultViewHandler;
import org.eclipse.jubula.client.ui.rcp.validator.MaxStringLengthValidator;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Aug 23, 2010
 */
public class AddTestResultSummaryCommentHandler 
    extends AbstractTestResultViewHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        ITestResultSummaryPO selectedSummary = getSelectedSummary(event);

        if (selectedSummary != null) {
            final String origTitle = selectedSummary.getCommentTitle();
            final String origDetail = selectedSummary.getCommentDetail();

            EnterCommentAndDetailsDialog dialog = 
                    new EnterCommentAndDetailsDialog(
                    HandlerUtil.getActiveShell(event),
                    new MaxStringLengthValidator(), origTitle, origDetail);
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),
                    ContextHelpIds.ADD_COMMENT);
            int result = dialog.open();
            if (result != Window.OK) {
                return null;
            }
            String newTitle = dialog.getCommentTitle();
            String newDetails = dialog.getCommentDetail();
            if (!StringUtils.equals(origTitle, newTitle)
                    || !StringUtils.equals(origDetail, newDetails)) {
                TestresultSummaryBP.getInstance().setCommentTitleAndDetails(
                        selectedSummary, newTitle, newDetails);
            }
        }
        
        return null;
    }
}
