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
package org.eclipse.jubula.client.core.businessprocess.compcheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.ICondStructPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;

/**
 * @author BREDEX GmbH
 * @created 10.05.2005
 */
public final class CompletenessGuard {
    /**
     * Operation to set the CompleteSpecTc flag for a node
     */
    private static class CheckMissingTestCaseReferences extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO execTc = (IExecTestCasePO) node;
                boolean isMissingSpecTc = execTc.getSpecTestCase() == null;
                setCompletenessMissingTestCase(execTc, !isMissingSpecTc);
            }
            return !alreadyVisited;
        }
    }

    /**
     * Operation to remove problems from inactive nodes
     */
    private static class InactiveNodesOperation extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (!node.isActive()) {
                Set<IProblem> problemsToRemove = new HashSet<IProblem>(
                        node.getProblems());
                for (IProblem problem : problemsToRemove) {
                    node.removeProblem(problem);
                }
            }
            return !alreadyVisited;
        }
    }

    /**
     * Operation to check for empty conditions
     */
    private static class CheckEmptyConditions extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (alreadyVisited) {
                return false;
            }
            if (node instanceof ITestSuitePO
                    || node instanceof ISpecTestCasePO) {
                checkEmptyContainer(node);
                return false; // We stop...
            }
            return true;
        }
    }

    /**
     * Operation to set the CompleteTestData flag at TDManager.
     * 
     * @author BREDEX GmbH
     * @created 10.10.2005
     */
    private static class CheckTestDataCompleteness extends
            AbstractNonPostOperatingTreeNodeOperation<INodePO> {

        /**
         * Sets the CompleteTDFlag in all ParamNodePOs. {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (alreadyVisited) {
                return false;
            }
            checkLocalTestData(node);
            return true;
        }
    }

    /**
     * private constructor
     */
    private CompletenessGuard() {
        // nothing
    }

    /**
     * Runs all relevant checks for a single node (can be the Project itself)
     * 
     * @param root
     *            INodePO
     * @param monitor
     *            the progress monitor to use
     */
    public static void checkAll(INodePO root, IProgressMonitor monitor) {
        // Iterate Execution tree
        TreeTraverser traverser = new TreeTraverser(root);
        traverser.setTraverseSpecPart(true);
        traverser.setMonitor(monitor);
        traverser.setTraverseIntoExecs(false);
        traverser.setTraverseSpecPart(true);
        traverser.addOperation(new CheckTestDataCompleteness());
        traverser.addOperation(new CheckMissingTestCaseReferences());
        traverser.addOperation(new InactiveNodesOperation());
        traverser.addOperation(new CheckEmptyConditions());
        traverser.traverse(true);

        List<ITestSuitePO> tsList = null;
        if (root.equals(GeneralStorage.getInstance().getProject())) {
            tsList = TestSuiteBP.getListOfTestSuites();
        } else if (root instanceof ITestSuitePO) {
            tsList = new ArrayList<>();
            tsList.add((ITestSuitePO) root);
        }
        if (tsList != null && !tsList.isEmpty()) {
            CompCheck check = new CompCheck(tsList);
            check.traverse();
            check.addProblems();
        }
    }

    /**
     * checks TD Completeness of all TS
     * 
     * @param root
     *            INodePO
     */
    public static void checkTestData(INodePO root) {
        new TreeTraverser(root, new CheckTestDataCompleteness()).traverse(true);
    }

    /**
     * @param node
     *            the node
     * @param problem
     *            the problem
     * @param deleteOrAdd
     *            true to delete; false to add problem to node
     */
    private static void setNodeProblem(INodePO node, IProblem problem,
            boolean deleteOrAdd) {
        if (deleteOrAdd) {
            node.removeProblem(problem);
        } else {
            node.addProblem(problem);
        }
    }

    /**
     * @param node
     *            the node
     * @param completeTCFlag
     *            true to delete; false to add problem to node
     */
    private static void setCompletenessMissingTestCase(INodePO node,
            boolean completeTCFlag) {
        setNodeProblem(node, ProblemFactory.MISSING_NODE, completeTCFlag);
    }

    /**
     * method to set the sumTdFlag for a given Locale
     * 
     * @param node
     *            the node
     * @param flag
     *            the state of sumTdFlag to set
     */
    public static void setCompletenessTestData(INodePO node, boolean flag) {
        setNodeProblem(node,
                ProblemFactory.createIncompleteTestDataProblem(node), flag);
    }

    /**
     * @param node
     *            the node to check
     */
    public static void checkLocalTestData(INodePO node) {
        INodePO possibleDataSourceNode = node;
        if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTc = (IExecTestCasePO) node;
            if (StringUtils.isNotBlank(execTc.getDataFile())) {
                return;
            }
            if (execTc.getHasReferencedTD()) {
                possibleDataSourceNode = execTc.getSpecTestCase();
            }
        }
        if (possibleDataSourceNode instanceof IParamNodePO) {
            IParamNodePO dataSourceNode = (IParamNodePO) possibleDataSourceNode;
            INodePO nodeToModify = null;
            if (!(node instanceof ISpecTestCasePO)) {
                nodeToModify = node;
            }
            if (nodeToModify != null)  {
                setCompletenessTestData(nodeToModify,
                        dataSourceNode.isTestDataComplete());
            }
        }
    }
    
    /**
     * marking the node if it is an empty condition
     * @param node the node
     */
    public static void checkEmptyContainer(INodePO node) {
        // Marking incomplete conditions
        List<INodePO> list = node.getUnmodifiableNodeList();
        for (INodePO child : list) {
            if (child instanceof ICondStructPO || child instanceof IIteratePO) {
                boolean ok = false;
                INodePO branch = null;
                if (child instanceof ICondStructPO) {
                    branch = ((ICondStructPO) child).getCondition();
                } else {
                    branch = ((IIteratePO) child).getDoBranch();
                }
                for (Iterator<INodePO> it = branch.getNodeListIterator();
                        it.hasNext(); ) {
                    INodePO branchChild = it.next();
                    if (branchChild.isActive()
                            && !(branchChild instanceof ICommentPO)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    String message = Messages.ProblemIncompleteBranch;
                    child.addProblem(ProblemFactory.createProblemWithMarker(
                            new Status(IStatus.ERROR,
                                    Activator.PLUGIN_ID, message), message,
                            child, ProblemType.REASON_IF_WITHOUT_TEST));
                }
            }
        }
    }
}
