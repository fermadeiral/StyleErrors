/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.gui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.teststyle.Activator;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.Severity;
import org.eclipse.jubula.client.teststyle.problems.ProblemCont;


/**
 * @author Marcell Salvage
 */
public class TeststyleProblemAdder 
    extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
    /** {@inheritDoc} */
    public boolean operate(ITreeTraverserContext<INodePO> ctx, INodePO parent,
            INodePO node, boolean alreadyVisited) {
        clearNodeFromTeststyleProblem(node);

        for (BaseCheck chk : ProblemCont.instance.getChecksFor(node)) {
            int severity = getIntForSeverity(chk.getSeverity());
            String message = chk.getDescription();
            if (severity > IStatus.INFO) {
                node.addProblem(ProblemFactory.createProblem(
                        new Status(severity, Activator.PLUGIN_ID, message)));
            }
        }
        if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTestCase = (IExecTestCasePO) node;
            handleExecTestCase(execTestCase);
        }
        
        return true;
    }

    /**
     * @param node
     *            The node from which the teststyle problems should be deleted.
     */
    private void clearNodeFromTeststyleProblem(INodePO node) {
        Set<IProblem> copy = new HashSet<IProblem>(node.getProblems());
        for (IProblem problem : copy) {
            if (problem.getStatus().getPlugin().equals(Activator.PLUGIN_ID)) {
                node.removeProblem(problem);
            }
        }
    }

    /**
     * @param execTestCase
     *            The exec test case which should be handled from the traverser
     */
    private void handleExecTestCase(IExecTestCasePO execTestCase) {
        ISpecTestCasePO specTestCase = execTestCase.getSpecTestCase();
        int worstSeverity = getWorstSeverity(
                ProblemCont.instance.getChecksFor(specTestCase));
        IProblem problem;
        Status status = null;
        switch (worstSeverity) {
           
            case IStatus.ERROR:
                status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                        Messages.TooltipErrorInChildren);
                break;
            case IStatus.WARNING:
                status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                        Messages.TooltipErrorInChildren);
                break;
            case IStatus.INFO:
            default:
                break;
        }
        
        if (status != null) {
            problem = ProblemFactory.createProblem(status);
            execTestCase.addProblem(problem);
        }
    }
    
    /**
     * @param checks
     *            Checks with severities
     * @return The worst severity in int. NOTHING = -1, INFO = 0, WARNING = 1,
     *         ERROR = 2.
     */
    private int getWorstSeverity(Set<BaseCheck> checks) {
        int status = -1;
        for (BaseCheck chk : checks) {
            int chkStatus = getIntForSeverity(chk.getSeverity());
            if (chkStatus > status) {
                status = chkStatus;
            }
        }
        return status;
    }

    /**
     * 
     * @param severity
     *            Severity which will be checked.
     * @return 0 for INFO, 1 for WARNING, 2 for ERROR
     */
    private int getIntForSeverity(Severity severity) {
        switch (severity) {
            case INFO:
                return IStatus.INFO;
            case WARNING:
                return IStatus.WARNING;
            case ERROR:
                return IStatus.ERROR;
            default:
                return -1;
        }
    }
}