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

import java.util.Set;

import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public class RenameTestDataManagerDialog extends EnterTestDataManagerDialog {
    /**
     * @param parentShell
     *            see EnterTestDataManagerDialog
     * @param initialName if set used to initialize the name field
     * @param usedNames
     *            a set of already used names
     */
    public RenameTestDataManagerDialog(String initialName, Shell parentShell,
            Set<String> usedNames) {
        super(parentShell, initialName, usedNames);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.RenameTestDataCubeDialogTitle);
        setTitleImage(IconConstants.NEW_TESTDATAMANAGER_DIALOG_IMAGE);
        setMessage(Messages.RenameTestDataCubeDialogMessage);
        getShell().setText(Messages.RenameTestDataCubeDialogTitle);
        return super.createDialogArea(parent);
    }
}
