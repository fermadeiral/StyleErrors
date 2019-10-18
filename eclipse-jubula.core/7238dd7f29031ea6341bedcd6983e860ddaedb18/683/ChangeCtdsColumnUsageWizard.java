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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * This wizard is used for replacing parameter names in Test Cases.
 * @author BREDEX GmbH
 */
public class ChangeCtdsColumnUsageWizard extends Wizard {

    /** The running operation for persisting the column usage. */
    private ChangeCtdsColumnUsageOperation m_operation;

    /**
     * @param paramDescriptions The parameter descriptions of the selected Test Cases.
     */
    public ChangeCtdsColumnUsageWizard(
            ExistingAndNewParameterData paramDescriptions) {
        m_operation = new ChangeCtdsColumnUsageOperation(paramDescriptions);
        setWindowTitle(Messages.ChangeCtdsColumnUsageActionDialog);
        addPage(new ChangeCtdsColumnUsagePage(paramDescriptions));
    }

    /**
     * @return True, if all pages are complete and the running operation can lock,
     *         otherwise false.
     * {@inheritDoc}
     */
    @Override
    public boolean canFinish() {
        String canLock = m_operation.canLock();
        WizardPage page = (WizardPage) getContainer().getCurrentPage();
        page.setErrorMessage(canLock);
        if (canLock != null) {
            return false; // locking not possible
        }
        return super.canFinish();
    }

    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        try {
            IProgressService ps = PlatformUI.getWorkbench()
                    .getProgressService();
            ps.run(true, false, m_operation);
        } catch (InvocationTargetException e) {
            //Already handled;
        } catch (InterruptedException e) {
            //Already handled
        }
        return true;
    }

    /**
     * Unlock all previously locked persistent objects.
     */
    @Override
    public void dispose() {
        if (m_operation != null) {
            m_operation.closeEditSupports();
            m_operation = null;
        }
        super.dispose();
    }

}
