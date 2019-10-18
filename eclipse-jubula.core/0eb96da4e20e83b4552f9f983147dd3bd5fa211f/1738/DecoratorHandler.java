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
package org.eclipse.jubula.client.teststyle.gui.decoration;

import java.util.Set;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.teststyle.TeststyleHandler;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.CheckCont;
import org.eclipse.jubula.client.teststyle.checks.DecoratingCheck;
import org.eclipse.jubula.client.teststyle.checks.Severity;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;
import org.eclipse.jubula.client.teststyle.problems.ProblemCont;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


/**
 * Handles the decoration of INodePO Elements which contains an element that
 * violated a checkstyle rule.
 * 
 * @author marcell
 * 
 */
public class DecoratorHandler extends GeneralLabelProvider implements
        ILightweightLabelDecorator {
    /** ID of this decorator */
    private static final String ID = 
        "org.eclipse.jubula.client.teststyle.tsTestresultDecorator"; //$NON-NLS-1$

    /**
     * Checks in the ProblemCont if the INodePO contains Elements which must be
     * decorated for violating a Checkstyle rule.
     * 
     * {@inheritDoc}
     */
    public void decorate(Object element, IDecoration decoration) {
        if (element == null || !TeststyleHandler.getInstance().isEnabled()) {
            return; // Cancel the method, an null element can't be decorated.
        }
        
        Severity severity = getWorstSeverity(getViolatingBaseChecks(element));
        
        if (element instanceof INodePO && severity != null) {
            INodePO node = (INodePO) element;
            IProblem worstProblem = ProblemFactory.getWorstProblem(node
                    .getProblems());
            if (worstProblem != null
                    && severity.ordinal() * 2 <= worstProblem.getStatus()
                            .getSeverity()) {
                severity = null;
            }
        }
        
        if (severity != null) {
            switch (severity) {
                case WARNING:
                    decoration.addOverlay(
                            IconConstants.WARNING_IMAGE_DESCRIPTOR,
                            IDecoration.BOTTOM_RIGHT);
                    break;
                case ERROR:
                    decoration.addOverlay(
                            IconConstants.ERROR_IMAGE_DESCRIPTOR,
                            IDecoration.BOTTOM_RIGHT);
                    break;
                default:
                    break;
            }
        }
        for (BaseCheck chk : getViolatingBaseChecks(element)) {
            decoration.addPrefix(chk.getPrefix(element));
            decoration.addSuffix(chk.getSuffix(element));
        }
        BaseContext cont = BaseContext.getFor(element.getClass());
        for (DecoratingCheck chk : CheckCont.getDecChecksFor(cont)) {
            if (chk.isActive(cont) && chk.decorate(element)) {
                // FIXME mbs decorating icons
                decoration.addPrefix(chk.getPrefix(element));
                decoration.addSuffix(chk.getSuffix(element));
            }
        }
    }

    /**
     * 
     * @param obj
     *            The object which could contain some checks that are violating
     *            the context.
     * @return The list of the checks which are violating obj.
     * 
     */
    private Set<BaseCheck> getViolatingBaseChecks(Object obj) {
        ProblemCont pCont = ProblemCont.instance;
        return pCont.getChecksFor(obj);
    }
    
    /**
     * 
     * @param checks List of checks
     * @return Worst severity of all checks or null if empty.
     */
    private Severity getWorstSeverity(Set<BaseCheck> checks) {
        Severity severity = null;
        for (BaseCheck check : checks) {
            if (severity == null) {
                severity = check.getSeverity();
            } else {
                if (severity.ordinal() < check.getSeverity().ordinal()) {
                    severity  = check.getSeverity();
                }
            }
        }
        return severity;
    }
    
    /**
     * 
     */
    public static void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                PlatformUI.getWorkbench().getDecoratorManager().update(ID);
            }
        });
    }
}
