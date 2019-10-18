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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.handlers.AbstractEditParametersHandler;
import org.eclipse.jubula.client.ui.rcp.handlers.project.RefreshProjectHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.osgi.util.NLS;

/**
 * Operation for replacing the parameter names.
 * @author BREDEX GmbH
 */
public class ChangeCtdsColumnUsageOperation
        extends AbstractEditParametersHandler
        implements IRunnableWithProgress {

    /** The map of specification Test Cases to its editor support for locking the Test Cases. */
    private Map<ISpecTestCasePO, EditSupport> m_editSupports =
            new HashMap<ISpecTestCasePO, EditSupport>();

    /** The last selected parameter description. */
    private IParamDescriptionPO m_lastSelectedExistingParamDescription;

    /** The selected parameter names. */
    private final ExistingAndNewParameterData m_paramData;

    /**
     * @param paramDescriptions The selected parameter descriptions.
     */
    public ChangeCtdsColumnUsageOperation(
            ExistingAndNewParameterData paramDescriptions) {
        m_paramData = paramDescriptions;
    }

    /**
     * Locks the selected Test Cases of the old parameter description.
     * Before locking, previously locked Test Cases are unlocked.
     * @return null, if the selected Test Cases of the old parameter description
     *         can be locked, otherwise an error description.
     */
    public String canLock() {
        if (m_lastSelectedExistingParamDescription
                == m_paramData.getOldParamDescription()) {
            return null;
        }
        closeEditSupports();
        for (ISpecTestCasePO spec: m_paramData.getSelectedTestCases()) {
            ParamNameBPDecorator decorator = new ParamNameBPDecorator(
                    ParamNameBP.getInstance(), spec);
            try {
                EditSupport es = new EditSupport(spec, decorator);
                es.lockWorkVersion();
                m_editSupports.put(spec, es);
                m_lastSelectedExistingParamDescription =
                        m_paramData.getOldParamDescription();
            } catch (PMException e) {
                closeEditSupports();
                return NLS.bind(
                        Messages.ChangeCtdsColumnUsageTestCaseCouldNotBeLocked,
                        spec.getName());
            }
        }
        return null;
    }

    /**
     * Close all {@link EditSupport}s to unlock all previously locked Test Cases.
     */
    public void closeEditSupports() {
        if (!m_editSupports.isEmpty()) {
            for (EditSupport es: m_editSupports.values()) {
                LockManager.instance().unlockPOs(es.getSession());
            }
            m_editSupports.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run(IProgressMonitor monitor) {
        monitor.beginTask(
                NLS.bind(Messages.ChangeParameterUsageOperation,
                        m_paramData.getOldParamDescription().getName(),
                        m_paramData.getNewParamDescription().getName()),
                m_paramData.getSelectedTestCases().size());
        TestCaseParamBP testCaseParamBP = new TestCaseParamBP();
        EditSupport es = null;
        boolean isOk = true;
        for (ISpecTestCasePO spec: m_editSupports.keySet()) {
            try {
                isOk = false;
                boolean isModified = false;
                es = m_editSupports.get(spec);
                ProjectNameBP.getInstance().clearCache();
                isModified = editParameters(
                        spec,
                        m_paramData.getNewParametersFromSpecTestCase(spec),
                        false,
                        es.getParamMapper(),
                        testCaseParamBP);
                if (isModified) {
                    es.saveWorkVersion();
                }
                isOk = true;
            } catch (ProjectDeletedException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            } catch (PMException e) {
                ErrorHandlingUtil.createMessageDialog(e, null, null);
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                if (es != null) {
                    LockManager.instance().unlockPOs(es.getSession());
                    es.close();
                }
            }
            monitor.worked(1);
            if (!isOk) {
                break; // stop loop in case of errors
            }
        }
        monitor.done();
        if (refreshProject()) {
            DataEventDispatcher.getInstance().fireParamChangedListener();
            DataEventDispatcher.getInstance().firePropertyChanged(false);
        }
    }

    /**
     * Refresh the project by executing the same refresh project handler,
     * which is called, if the user pressed F5.
     * @return True, if the project has been refreshed, otherwise false on errors.
     */
    private static boolean refreshProject() {
        final AtomicReference<IStatus> statusOfRefresh =
                new AtomicReference<IStatus>();
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                RefreshProjectHandler rph =
                    new RefreshProjectHandler();
                statusOfRefresh.set((IStatus)rph.executeImpl(null));
            }
        });
        return statusOfRefresh.get() != null && statusOfRefresh.get().isOK();
    }

    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event) {
        return null;
    }

}
