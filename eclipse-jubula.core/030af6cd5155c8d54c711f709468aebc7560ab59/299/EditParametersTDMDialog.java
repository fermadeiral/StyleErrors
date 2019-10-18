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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Jul 14, 2010
 */
public class EditParametersTDMDialog extends AbstractEditParametersDialog {
    /**
     * @param parentShell
     *            the parent shell to use
     * @param paramIntObj
     *            the IParameterInterfacePO
     */
    public EditParametersTDMDialog(Shell parentShell,
            IParameterInterfacePO paramIntObj) {
        super(parentShell, paramIntObj);
    }
    
    /** {@inheritDoc} */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.EditParametersTDMDialogEditParameters);
        setTitleImage(IconConstants.NEW_TESTDATAMANAGER_DIALOG_IMAGE);
        setMessage(Messages.EditParametersTDMDialogEditParamsOfTestDataSet);
        getShell().setText(
                Messages.EditParametersTDMDialogEditParametersShellTitle);
        return super.createDialogArea(parent);
    }
    
    /** {@inheritDoc} */
    protected String getEditedObjectNameString() {
        return Messages.EditParametersTDMDialogTdmName;
    }
    
    /**
     * @author BREDEX GmbH
     * @created Jul 14, 2010
     */
    private final class ParamTableCellModifier extends
            AbstractTableCellModifier {
        /** {@inheritDoc} */
        public boolean canModify(Object element, String property) {
            return true;
        }
    }

    /** {@inheritDoc} */
    protected ICellModifier getParamTableCellModifier() {
        return new ParamTableCellModifier();
    }
    
    /** {@inheritDoc} */
    protected boolean confirmChangeParamName() {
        if (TestDataCubeBP.isCubeReused(getParamInterfaceObj())) {
            final Dialog dialog = ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.Q_CHANGE_INTERFACE_CHANGE_PARAM_NAME, null,
                    null, getShell());
            return dialog.getReturnCode() == Window.OK;
        }
        return true;
    }
}
