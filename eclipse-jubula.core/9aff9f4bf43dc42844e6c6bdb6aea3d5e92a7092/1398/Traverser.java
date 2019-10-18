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
package org.eclipse.jubula.client.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.ExternalTestDataBP;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.TestExecution.PauseMode;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.ICondStructPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IEventStackModificationListener;
import org.eclipse.jubula.client.core.model.IExecStackModificationListener;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.IncompleteDataException;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class Traverser {  
    
    /**
     * Represents the exact location of a test step within the execution
     * hierarchy. 
     *
     * @author BREDEX GmbH
     * @created 06.03.2008
     */
    private static final class ExecStackMarker {

        /** the execution stack at the time the marker was created */
        private Vector<ExecObject> m_execStack;
    
        /** the test step represented by the marker */
        private ICapPO m_step;
        
        /**
         * Constructor
         * 
         * @param execStack The current execution stack.
         * @param step The current test step.
         */
        ExecStackMarker(Stack<ExecObject> execStack, ICapPO step) {
            m_execStack = new Vector<ExecObject>(execStack);
            m_step = step;
        }

        /**
         * 
         * @return the execution stack at the time this marker was created.
         */
        @SuppressWarnings("unchecked")
        List<ExecObject> getExecStack() {
            return UnmodifiableList.decorate(m_execStack);
        }

        /**
         * 
         * @return the test step represented by this marker.
         */
        ICapPO getStep() {
            return m_step;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public boolean equals(Object obj) {
            if (obj instanceof ExecStackMarker) {
                ExecStackMarker marker = (ExecStackMarker)obj;
                return new EqualsBuilder()
                    .append(m_execStack, marker.getExecStack())
                    .append(m_step, marker.getStep()).isEquals();
            }
            
            return super.equals(obj);
        }

        /**
         * 
         * {@inheritDoc}
         */
        public int hashCode() {
            return new HashCodeBuilder().append(m_execStack).append(m_step)
                .toHashCode();
        }
    }

    /**
     * <code>NO_DATASET</code> to use for node operations without
     * consideration of datasets
     */
    public static final int NO_DATASET = -1;

    /**
     * <code>NO_INDEX</code> placeholder for index to use, if childrenlist of
     * a node isn't yet proceeded
     */
    public static final int NO_INDEX = -1;

    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger(Traverser.class);
    
    /**
     * <code>m_root</code> root node
     */
    private INodePO m_root;
    
    /**
     * <code>m_execStack</code> stack for executed objects
     */
    private Stack<ExecObject> m_execStack = new Stack<ExecObject>();
    
    /**
     * list of execution stack listeners
     */
    private List<IExecStackModificationListener> m_execListenerList = 
        new ArrayList<IExecStackModificationListener>();
    
    /**
     * list of event stack listeners
     */
    private List<IEventStackModificationListener> m_eventListenerList = 
        new ArrayList<IEventStackModificationListener>();

    /**
     * <code>m_eventStack</code> stack for actual executed eventHandler
     * stackobjects are from type EventObject
     */
    private Stack<EventObject> m_eventStack = new Stack<EventObject>();

    /** 
     * mapping from positions in the execution hierarchy to the number of times
     * the test step at that position has been retried
     */
    private Map<ExecStackMarker, Integer> m_markerToNumRetriesMap =
        new HashMap<ExecStackMarker, Integer>();
    
    /** business process for retrieving test data */
    private ExternalTestDataBP m_externalTestDataBP;
    
    /** Indicates that we are just building the test result tree - this is for the loops */
    private boolean m_building = false;
    
    /** Maximum number of iterations */
    private int m_iterMax = 100;
    
    /**
     * @param root root node from tree
     */
    public Traverser(INodePO root) {
        m_root = root;
        m_externalTestDataBP = new ExternalTestDataBP();
        m_execStack.push(new ExecObject(root, 0));
        executeLogging();
    }

    /**
     * @return the next Cap, regarding to the actual position in tree
     * @throws JBException in case of missing testdata
     */
    @SuppressWarnings("unchecked")
    public ICapPO next() throws JBException {
        if (!m_execStack.isEmpty()) {
            ExecObject stackObj = m_execStack.peek();
            INodePO node = stackObj.getExecNode();
            // next index
            ITDManager tdManager = null;
            if (node instanceof IParamNodePO) {
                tdManager = 
                    m_externalTestDataBP.getExternalCheckedTDManager(
                            (IParamNodePO)node);
            }
            if (stackObj.getIndex() < node.getNodeListSize() - 1) {
                stackObj.incrementIndex();
                List<INodePO> nodeList = 
                    IteratorUtils.toList(node.getNodeListIterator());
                INodePO childNode = nodeList.get(stackObj.getIndex());
                if (!childNode.isActive()) {
                    return next();
                }
                if (childNode instanceof ICapPO) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(Messages.ActualExecutedCap 
                            + StringConstants.COLON + StringConstants.SPACE 
                            + childNode.getName());
                    }
                    fireNextCap((ICapPO)childNode);
                    return (ICapPO)childNode;
                }
                if (childNode instanceof IExecTestCasePO) {
                    if (((IExecTestCasePO)childNode).getSpecTestCase() 
                            == null) {
                        throw new IncompleteDataException(
                            Messages.ExecTestCasePOMissingReference,
                            MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA);
                    }
                    processExecTestCase(stackObj, (IExecTestCasePO)childNode);
                    return next();
                }
                if (childNode instanceof ICommentPO
                        || childNode instanceof IControllerPO
                        || childNode instanceof IAbstractContainerPO) {
                    m_execStack.push(new ExecObject(childNode, NO_DATASET));
                    fireExecStackIncremented(childNode);
                    if (childNode instanceof IIteratePO) {
                        // not very nice, but we handle the 0-times executed iteration this way
                        decrementIterate(((IIteratePO) childNode).
                                getDoBranch());
                    }
                    return next();
                }
                Assert.notReached(Messages.ErrorInTestExecutionTree);
                return null;
                // next dataset
            } else if (!(stackObj.getExecNode() instanceof ITestSuitePO)
                    && tdManager != null) {
                int maxDsNumber = tdManager.getDataSetCount();
                if (stackObj.getNumberDs() == Traverser.NO_DATASET) {
                    stackObj.incrementDataSetNumber();
                }
                if (stackObj.getNumberDs() + 1 < maxDsNumber) {
                    stackObj.incrementDataSetNumber();
                    stackObj.setIndex(NO_INDEX);
                    fireNextDataSetIteration();
                    return next();
                }
                ReentryProperty prop = decrementStack(node);
                return (prop == null) ? next() : next(prop);
            } else {
                ReentryProperty prop = decrementStack(node);
                return (prop == null) ? next() : next(prop);
            }
        } 
        // end
        return null;
    }
    
    /**
     * processes a childnode which is an ExecTestCasePO
     * @param stackObj the stack object
     * @param exec the child node
     * @throws JBException an exception.
     */
    private void processExecTestCase(ExecObject stackObj,
        final IExecTestCasePO exec) 
        throws JBException {
        
        final ITDManager tdManager = m_externalTestDataBP
                .getExternalCheckedTDManager(exec);
        
        IDataSetPO dataSet = null;
        
        int dataSetSize = tdManager.getDataSetCount();
        if (dataSetSize > 0) {
            dataSet = tdManager.getDataSet(0);
        }

        String modelValue = null;
        if (dataSet != null && dataSet.getColumnCount() > 0) {
            modelValue = dataSet.getValueAt(0);
        }
        // own data sets - > always begin at 0
        if (dataSetSize > 1) {
            m_execStack.push(new ExecObject(exec, 
                getFirstDataSetNumber(exec)));
        } else if (dataSetSize == 1) {
            // exact one dataset with a reference -> 
            // begin at data set index of parent
            String uniqueId = tdManager.getUniqueIds().get(0);
            IParamDescriptionPO desc = exec.getParameterForUniqueId(
                    uniqueId);
            ParamValueConverter conv = new ModelParamValueConverter(
                modelValue, exec, desc);
            if (modelValue != null && conv.containsReferences()) {
                m_execStack.push(new ExecObject(exec, 
                        stackObj.getNumberDs()));
            // exact one dataset with a value or no value in runIncomplete-mode
            // always use dataset number 0
            } else if (modelValue != null) {
                m_execStack.push(new ExecObject(exec, 0));
            } else {
                throw new IncompleteDataException(
                    NLS.bind(Messages.ErrorWhenBuildingTestExecutionTree, 
                            exec.getSpecAncestor().getName()),
                    MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA);
            }
        // no data sets or fixed value
        } else {
            m_execStack.push(new ExecObject(exec, NO_DATASET));
        }
        fireExecStackIncremented(exec);
    }

    /**
     * @param node node to pop from stack
     * @return the associated reentryProperty in case of eventExecTestCaseNode 
     * or null in case of execTestCaseNode
     */
    private ReentryProperty decrementStack(INodePO node)
            throws IncompleteDataException {
        ReentryProperty prop = null;
        if (isEventHandler(node)) {
            IEventExecTestCasePO eventExec = (IEventExecTestCasePO)node; 
            prop = eventExec.getReentryProp();
        }
        if (node instanceof IAbstractContainerPO) {
            // a container is finished without a Check Fail being triggered
            // what happens next depends on what the container's parent is and what is the container
            
            // first we remove the container
            m_execStack.pop();
            fireExecStackDecremented();
            
            if (m_execStack.isEmpty()) {
                // this can only happen when adding a new branch to the Test Result Tree
                // then the root is a ContainerPO
                return prop;
            }
            // so here the top of the exec stack is the controller, that is, node.getParent
            
            if (node.getParentNode() instanceof IConditionalStatementPO) {
                decrementCondStatementCont(node);
            } else if (node.getParentNode() instanceof IDoWhilePO
                    || node.getParentNode() instanceof IWhileDoPO) {
                decrementDoWhileDoCont(node);
            } else if (node.getParentNode() instanceof IIteratePO) {
                decrementIterate(node);
            }
            return prop;
        }
        if (!m_execStack.isEmpty()) {
            m_execStack.pop();
            fireExecStackDecremented();
        }
        return prop;
    }
    
    /**
     * A sub-branch of a Conditional Statement is finished
     * @param node the sub-branch
     */
    private void decrementCondStatementCont(INodePO node)
            throws IncompleteDataException {
        IConditionalStatementPO par =
                (IConditionalStatementPO) node.getParentNode();
        if (node.equals(par.getCondition())) {
            // the condition is finished, so we continue with the Then or Else branches
            INodePO next = par.isNegate() ? par.getElseBranch()
                    : par.getThenBranch();
            m_execStack.push(new ExecObject(next, NO_DATASET));
            fireExecStackIncremented(next);
            return;
        }
        // the Then or Else branches are finished, so the conditional statement is also
        m_execStack.pop();
        fireExecStackDecremented();
    }
    
    /**
     * A sub-branch of a Do-While or While-Do is finished
     * @param node the sub-branch
     */
    private void decrementDoWhileDoCont(INodePO node)
            throws IncompleteDataException {
        ICondStructPO par =
                (ICondStructPO) node.getParentNode();
        if (node.equals(par.getCondition())) {
            // the condition is TRUE
            if (par.isNegate() || m_building) {
                // negated Do-While-Do or only Test Result Tree building, so we have to stop now
                m_execStack.pop();
                fireExecStackDecremented();
            } else {
                // we continue with the do branch
                // if the Do-While-Do is negated then we will simply stop 
                INodePO next = par.getDoBranch();
                m_execStack.push(new ExecObject(next, NO_DATASET));
                fireExecStackIncremented(next);
            }
        } else {
            // the do branch is executed, here comes the condition
            if (m_execStack.peek().getIncLoopCount() >= m_iterMax) {
                // or rather not, this looks like an infinite loop...
                fireInifiniteLoop();
                m_execStack.pop();
                fireExecStackDecremented();
                LOG.error(Messages.ErrorInfiniteLoop);
            } else {
                INodePO next = par.getCondition();
                m_execStack.push(new ExecObject(next, NO_DATASET));
                fireExecStackIncremented(next);
            }
        }
    }
    
    /**
     * The Do block of an Iterate is finished
     * @param node the Do block
     */
    public void decrementIterate(INodePO node) throws IncompleteDataException {
        IIteratePO iter = (IIteratePO) node.getParentNode();
        ExecObject top = m_execStack.peek();
        int limit = 0;
        if (!m_building) {
            try {
                limit = Math.min(Integer.parseInt(top.getParameterValue(
                    iter.getParameterList().get(0).getUniqueId())), m_iterMax);
                
            } catch (NumberFormatException e) {
                limit = 0;
            }
        }
        if (limit < top.getIncLoopCount()) {
            // end
            if (limit == m_iterMax) {
                fireInifiniteLoop();
            }
            m_execStack.pop();
            fireExecStackDecremented();
            LOG.error(Messages.ErrorInfiniteLoop);
        } else {
            // continue
            INodePO next = iter.getDoBranch();
            m_execStack.push(new ExecObject(next, NO_DATASET));
            fireExecStackIncremented(next);
        }
    }

    /**
     * write information to log
     */
    private void executeLogging() {
        if (LOG.isDebugEnabled() && !m_execStack.isEmpty()) {
            LOG.debug(Messages.ActualPeekObjectOnStack + StringConstants.COLON
                + StringConstants.SPACE 
                + m_execStack.peek().getExecNode().getName());
        }
    }

    /**
     * @param node for which the number of dataset to find
     * @return number of datasets for given node
     * @throws JBException in case of missing testdata
     * hint: each execTestCase with a dataManager has parameter(s) and must
     * have at least one dataset
     */
    private int getFirstDataSetNumber(INodePO node) 
        throws JBException {
        int firstDs = NO_DATASET;

        if (node instanceof IParamNodePO
                && ((IParamNodePO) node).getDataManager() != null) {
            IParamNodePO paramNode = (IParamNodePO)node;
            ITDManager tdManager = 
                m_externalTestDataBP.getExternalCheckedTDManager(paramNode);
            int ds = tdManager.getDataSetCount();
            if (ds > 0) {
                firstDs = 0;
            }         
        }
        return firstDs;
    }

    /**
     * @return Returns the execStack as unmodifiable List
     */
    public List <ExecObject> getExecStackAsList() {
        return Collections.unmodifiableList(new ArrayList<ExecObject>(
                m_execStack));
    }
    /**
     * @return The nodes of the current execution stack in a list
     */
    public List<INodePO> getExecStackAsNodeList() {
        List<INodePO> nodes = new ArrayList<INodePO>(m_execStack.size());
        
        for (Iterator<ExecObject> it = m_execStack.iterator(); it.hasNext();) {
            ExecObject execObject = it.next();
            nodes.add(execObject.getExecNode());
        }
        return nodes;
    }
    /**
     * @return Returns the root.
     */
    public INodePO getRoot() {
        return m_root;
    }
    
    /**
     * register listener for stackModificationListener
     * @param listener listener to register
     */
    public void addExecStackModificationListener(
        IExecStackModificationListener listener) {
        
        if (!m_execListenerList.contains(listener)) {
            m_execListenerList.add(listener);
        }        
    }
    
    /**
     * remove listener from stackModificationListenerList
     * @param listener listener to remove
     */
    public void removeExecStackModificationListener(
        IExecStackModificationListener listener) {
        m_execListenerList.remove(listener);    
    }
    
    /**
     * register listener for stackModificationListener
     * @param listener listener to register
     */
    public void addEventStackModificationListener(
        IEventStackModificationListener listener) {
        
        if (!m_eventListenerList.contains(listener)) {
            m_eventListenerList.add(listener);
        }        
    }
    
    /**
     * remove listener from stackModificationListenerList
     * @param listener listener to remove
     */
    public void removeEventStackModificationListener(
            IEventStackModificationListener listener) {
        m_eventListenerList.remove(listener);    
    }

    /**
     *  event for push-operation on execStack
     *  @param node node, which was added to stack
     */
    private void fireExecStackIncremented(INodePO node)
            throws IncompleteDataException {
        addParameters(m_execStack.peek());
        executeLogging();
        Iterator<IExecStackModificationListener> it = 
            m_execListenerList.iterator();
        while (it.hasNext()) {
            IExecStackModificationListener l = it.next();
            try {
                l.stackIncremented(node);
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }
    
    /**
     *  event for pop-operation on execStack
     */
    private void fireExecStackDecremented() {
        executeLogging();
        Iterator<IExecStackModificationListener> it = 
            m_execListenerList.iterator();
        while (it.hasNext()) {
            IExecStackModificationListener l = it.next();
            try {
                l.stackDecremented();
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }

    /**
     *  event for pop-operation on execStack
     */
    private void fireInifiniteLoop() {
        Iterator<IExecStackModificationListener> it = 
            m_execListenerList.iterator();
        while (it.hasNext()) {
            IExecStackModificationListener l = it.next();
            try {
                l.infiniteLoop();
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }
    
    /**
     *  event for push-operation on execStack
     */
    private void fireEventStackIncremented() {
        executeLogging();
        Iterator<IEventStackModificationListener> it = 
            m_eventListenerList.iterator();
        while (it.hasNext()) {
            IEventStackModificationListener l = it.next();
            try {
                l.eventStackIncremented();
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }
    
    /**
     *  event for pop-operation on execStack
     */
    private void fireEventStackDecremented() {
        Iterator<IEventStackModificationListener> it = 
            m_eventListenerList.iterator();
        while (it.hasNext()) {
            IEventStackModificationListener l = it.next();
            try {
                l.eventStackDecremented();
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }

    /**
     *  event for next dataset iteration
     */
    private void fireNextDataSetIteration() throws IncompleteDataException {
        addParameters(m_execStack.peek());
        
        Iterator<IExecStackModificationListener> it = 
            m_execListenerList.iterator();
        while (it.hasNext()) {
            IExecStackModificationListener l = it.next();
            try {
                l.nextDataSetIteration();
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }
    
    /**
     * event for proceeding of next cap
     * 
     * @param cap
     *            actual proceeded cap
     */
    private void fireNextCap(ICapPO cap) {
        Iterator<IExecStackModificationListener> it = 
            m_execListenerList.iterator();
        while (it.hasNext()) {
            IExecStackModificationListener l = it.next();
            try {
                l.nextCap(cap);
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }
    
    /**
     * @return Returns the dataSetNumber.
     */
    public int getDataSetNumber() {
        int ds = 0;
        if (!m_execStack.isEmpty()) {
            ExecObject obj = m_execStack.peek();
            ds = obj.getNumberDs();
        }
        return ds;
    }
    
    /**
     * @param reentryProp reentryProperty
     * @return next Cap to execute
     * @throws JBException in case of incomplete testdata
     */
    private ICapPO next(ReentryProperty reentryProp) 
        throws JBException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ReentryProperty: " + String.valueOf(reentryProp)); //$NON-NLS-1$
        }
        if (!m_execStack.isEmpty()) {
            if (reentryProp.equals(ReentryProperty.CONTINUE)) {
                popEventStack();
            } else if (reentryProp.equals(ReentryProperty.REPEAT)) {
                ExecObject obj = m_execStack.peek();
                obj.decrementIndex();
                popEventStack();
            } else if (reentryProp.equals(ReentryProperty.BREAK)) {
                for (int i = m_eventStack.size(); i > 0 
                    && !m_execStack.isEmpty(); i--) {
                    m_execStack.pop();
                    fireExecStackDecremented();
                }
                int pos = m_eventStack.peek().getStackPos();
                popEventStack();
                popEventStackNested(pos);

            } else if (reentryProp.equals(ReentryProperty.RETURN)) {
                int stackPos = m_eventStack.peek().getStackPos();
                popEventStack();
                stackPos = popEventStackNested(stackPos);
                
                while (m_execStack.size() > stackPos) {
                    m_execStack.pop();
                    fireExecStackDecremented();
                }
                
            } else if (reentryProp.equals(ReentryProperty.EXIT)) {
                for (int i = m_execStack.size() - 1; i >= 0; i--) {
                    m_execStack.pop();
                    fireExecStackDecremented();
                }
                for (int i = m_eventStack.size() - 1; i >= 0; i--) {
                    popEventStack();
                }
            } else if (reentryProp.equals(ReentryProperty.STOP)) {
                TestExecution.getInstance().pauseExecution(PauseMode.PAUSE);
                popEventStack();
            } else if (reentryProp.equals(ReentryProperty.RETRY)) {
                popEventStack();
                return retryStep();
                
            // default is EXIT
            } else {
                for (int i = m_execStack.size() - 1; i >= 0; i--) {
                    m_execStack.pop();
                    fireExecStackDecremented();
                }
                for (int i = m_eventStack.size() - 1; i >= 0; i--) {
                    popEventStack();
                }
            }
            return next();
        }
        // end of testexecution
        return null;           
    }

    /**
     * Repeatedly pops the event stack until either the event stack is empty
     * or the top event on the stack has a lower execution stack position than
     * the top event on the stack at the time this method was called.
     * @param stackPos the stack position of the previous top element of the event stack
     * @return the execution stack position of the event object at the top of 
     *         the event stack at the time this method is called.
     */
    private int popEventStackNested(int stackPos) {
        // Remove all events that occurred higher on the execution stack
        // than the currently processed event
        while (!m_eventStack.isEmpty() 
                && m_eventStack.peek().getStackPos() >= stackPos) {
            popEventStack();
        }

        return stackPos;
    }
    
    /**
     * Calls pop() on the event stack and fires a corresponding event.
     */
    private void popEventStack() {
        m_eventStack.pop();
        fireEventStackDecremented();
    }

    /**
     * @return the cap to be retried, or <code>null</code> if the cap cannot be
     *         determined. A return value of <code>null</code> indicates an
     *         error.
     */
    @SuppressWarnings("unchecked")
    private ICapPO retryStep() {
        ExecObject execObj = m_execStack.peek();

        // inform listeners that we are retrying the step
        List<INodePO> nodeList = 
            IteratorUtils.toList(execObj.getExecNode().getNodeListIterator());
        INodePO childNode = nodeList.get(execObj.getIndex());

        if (childNode instanceof ICapPO) {
            ICapPO cap = (ICapPO)childNode;
            ExecStackMarker marker = new ExecStackMarker(m_execStack, cap);
            if (m_markerToNumRetriesMap.containsKey(marker)) {
                // increment number of retries
                m_markerToNumRetriesMap.put(
                        marker, m_markerToNumRetriesMap.get(marker) + 1);
            } else {
                m_markerToNumRetriesMap.put(marker, 1);
            }
            fireRetryStep(cap);
            return cap;
        }
        
        return null;
    }

    /**
     * event for retrying a test step
     * 
     * @param toRetry The step to retry.
     */
    private void fireRetryStep(ICapPO toRetry) {
        Iterator<IExecStackModificationListener> it = 
            m_execListenerList.iterator();
        while (it.hasNext()) {
            IExecStackModificationListener l = it.next();
            try {
                l.retryCap(toRetry);
            } catch (Throwable t) {
                LOG.error(Messages.ErrorWhileNotifyingListeners, t);
            }
        }
    }

    /**
     * determines, if node of type EventExecTestCase
     * @param node node to validate
     * @return result of validation
     */
    private boolean isEventHandler(INodePO node) {
        return node instanceof IEventExecTestCasePO;
    }

    /**
     * 
     * @param eventType The event type to be handled.
     * @return the reentry property of the event handler for the given
     *         event type.
     */
    public ReentryProperty getEventHandlerReentry(String eventType) {
        return getEventObject(eventType, false)
            .getEventExecTc().getReentryProp();
    }
    
    /**
     * @param eventType eventType of eventhandler, which is to execute in next
     * step
     * @return next cap to execute
     * @throws JBException in case of incomplete testdata
     */
    public ICapPO next(String eventType) 
        throws JBException {
        ExecObject execObj = m_execStack.peek();
        EventObject eventObj = getEventObject(eventType, true);
        IEventExecTestCasePO eventExecTC = eventObj.getEventExecTc();

        if (eventExecTC.getReentryProp().equals(ReentryProperty.CONDITION)) {
            // failed Condition - we need something similar to a RETURN reentry
            // the event stack does not contain the event handler!
            // the exec stack ends with the Condition container and its CondStruct parent  
            int stackPos = popEventStackNested(eventObj.getStackPos());
            while (m_execStack.size() > stackPos + 1) {
                m_execStack.pop();
                fireExecStackDecremented();
            }
            ExecObject top = m_execStack.peek();
            if (top.getExecNode() instanceof IConditionalStatementPO) {
                // The CondStruct is an If / Then / Else
                IConditionalStatementPO cond = (IConditionalStatementPO)
                        top.getExecNode();
                // This is the time to insert the Else or Then branch
                INodePO node = cond.isNegate() ? cond.getThenBranch()
                        : cond.getElseBranch();
                
                m_execStack.push(new ExecObject(node, NO_DATASET));
                fireExecStackIncremented(node);
            } else if (top.getExecNode() instanceof ICondStructPO) {
                // not very nice, but a CondStruct which is not an If
                // is a Do-While or While-Do
                ICondStructPO cond = (ICondStructPO) top.getExecNode();
                if (cond.isNegate()) {
                    // failed, so we continue the iteration
                    INodePO node = cond.getDoBranch();
                    m_execStack.push(new ExecObject(node, NO_DATASET));
                    fireExecStackIncremented(node);
                } else {
                    // failed, normal, so we stop the iteration
                    m_execStack.pop();
                    fireExecStackDecremented();
                }
            }
            return next();
        }
        
        int dataSetIndex = 0;
        final ITDManager mgr = eventExecTC.getDataManager();
        if (mgr.getDataSetCount() > 0) {
            IDataSetPO row = mgr.getDataSet(0);
            for (int col = 0; col < row.getColumnCount(); col++) {
                String td = row.getValueAt(col);
                String uniqueId = mgr.getUniqueIds().get(col);
                IParamDescriptionPO desc = 
                    eventExecTC.getParameterForUniqueId(uniqueId);
                // if EH uses params of parent, start at the iteration which failed!
                ParamValueConverter conv = new ModelParamValueConverter(
                    td, eventExecTC, desc);
                if (conv.containsReferences()) {
                    dataSetIndex = execObj.getNumberDs();
                    break;
                }
            }
        }
     
        m_execStack.push(new ExecObject(eventExecTC, dataSetIndex));
        m_eventStack.push(eventObj);
        fireEventStackIncremented();
        fireExecStackIncremented(eventExecTC);
        return next();
    }
    
    /**
     * find the next eventHandler for given eventType
     * 
     * @param eventType
     *            eventType for eventHandler to find
     * @param resetRetryCount
     *            if this is set to <code>true</code> the retry count of the
     *            EventHandler will be reset, if the EventHandler was
     *            unsuccessful and has reached his max retry count.
     *            This is necessary for http://eclip.se/347275
     * @return the next eventHandler for given eventType
     */
    @SuppressWarnings("unchecked")
    private EventObject getEventObject(String eventType,
            boolean resetRetryCount) {
        
        List<INodePO> nodeList = IteratorUtils.toList(
            m_execStack.peek().getExecNode().getNodeListIterator());
        ICapPO cap = (ICapPO)nodeList.get(m_execStack.peek().getIndex());
        ExecStackMarker marker = new ExecStackMarker(m_execStack, cap);

        EventObject eventObj = null;
        int startIndex = m_execStack.size() - 1;
        for (int i = startIndex; i > 0 && i < m_execStack.size(); --i) {
            ExecObject obj = m_execStack.get(i);
            
            if (eventType.equals(TestErrorEvent.ID.VERIFY_FAILED)
                    && obj.getExecNode() instanceof IAbstractContainerPO) {
                eventObj = handleContainer((IAbstractContainerPO) obj.
                        getExecNode(), i);
                if (eventObj != null) {
                    return eventObj;
                }
            }
            
            if (!(obj.getExecNode() instanceof IExecTestCasePO)) {
                continue;
            }
            IExecTestCasePO execTc = (IExecTestCasePO)obj.getExecNode();
            
            IEventExecTestCasePO eventExecTc = execTc.getEventExecTC(eventType);
            if (!isHandlingError(i) && eventExecTc !=  null) {
                if (!(eventExecTc.getReentryProp().equals(ReentryProperty.RETRY)
                        && m_markerToNumRetriesMap.containsKey(marker) 
                        && m_markerToNumRetriesMap.get(marker)
                            >= eventExecTc.getMaxRetries())) {
                    
                    eventObj = new EventObject(eventExecTc, i);
                    break;
                }
                eventExecTc = null;
            }           
        }
        // nothing found --> call defaultEventHandler
        if (eventObj == null) {
            // reset the marker of the EventHandler
            if (m_markerToNumRetriesMap.containsKey(marker)
                    && resetRetryCount) {
                m_markerToNumRetriesMap.put(marker, 0);
            }
            IEventExecTestCasePO eventExecTc = 
                DefaultEventHandler.getDefaultEventHandler(eventType, m_root);
            Validate.notNull(eventExecTc, 
                Messages.MissingDefaultEventHandlerForEventType + eventType 
                + StringConstants.DOT);
            eventObj = new EventObject(eventExecTc, 0);
        }
        return eventObj;      
    }
    
    /**
     * Handles container nodes for events
     * @param cont the container
     * @param i the stack position of the container
     * @return the EventObject or null if the container doesn't care about the event
     */
    private EventObject handleContainer(IAbstractContainerPO cont, int i) {
        INodePO par = cont.getParentNode();
        if (par instanceof ICondStructPO && cont.equals(
                ((ICondStructPO) par).getCondition())) {
            // this Condition fails, consuming the error event 
            // the 'real' handler node is the parent, that's why i - 1
            return new EventObject(NodeMaker.COND_EVENT_EXECTC, i - 1);
        }
        return null;
    }

    /**
     * Tells whether the test case at the given index in the execution stack
     * is currently handling an error. A test case is defined as "currently
     * handling an error" if any event handler for the test case is currently
     * on the execution stack.
     * 
     * @param execStackIndex The index at which the test case to check can be
     *                       found in the execution stack.
     * @return <code>true</code> if the test case at the specified index in the
     *         execution stack is currently handling an error. Otherwise 
     *         <code>false</code>.
     */
    private boolean isHandlingError(int execStackIndex) {
        for (EventObject event : m_eventStack) {
            if (event.getStackPos() == execStackIndex) {
                return true;
            }
        }
     
        return false;
    }

    /**
     * 
     * class to manage information about executed eventHandler
     * @author BREDEX GmbH
     * @created 29.04.2005
     */
    private static class EventObject {        
        /** <code>m_eventExec</code> the EventExecTestCasePO */
        private IEventExecTestCasePO m_eventExec;
        /** <code>m_stackPos</code> place of discovery of eventExecTestCase in execStack */
        private int m_stackPos;

        /**
         * @param eventExec the event exec PO
         * @param stackPos place of discovery of eventExecTestCase in execStack
         */
        private EventObject(IEventExecTestCasePO eventExec, int stackPos) {
            m_eventExec = eventExec;
            m_stackPos = stackPos;
        }
        /**
         * Returns the node as an EventExecTC
         * @return Returns the eventExecTc.
         */
        public IEventExecTestCasePO getEventExecTc() {
            return m_eventExec;
        }
        
        /**
         * @return Returns the stackPos.
         */
        public int getStackPos() {
            return m_stackPos;
        }
    }
    
    /**
     * 
     * @return the result value to use when the most recently executed cap was
     *         successful. This may depend on previous events within the test,
     *         such as whether the step has been retried.
     */
    public int getSuccessResult() {
        INodePO currentNode = getCurrentNode();
        if (currentNode instanceof ICapPO) {
            ExecStackMarker marker = 
                new ExecStackMarker(m_execStack, (ICapPO)currentNode);
            if (m_markerToNumRetriesMap.containsKey(marker)) {
                m_markerToNumRetriesMap.put(marker, 0);
                return TestResultNode.SUCCESS_RETRY;
            }
        }
        return TestResultNode.SUCCESS;
    }


    
    /**
     * 
     * @return the currently executing node.
     */
    @SuppressWarnings("unchecked")
    private INodePO getCurrentNode() {
        List<INodePO> nodeList = IteratorUtils.toList(
                m_execStack.peek().getExecNode().getNodeListIterator());
        return nodeList.get(m_execStack.peek().getIndex());

    }

    /**
     * Adds parameter values to the given execution object. If 
     * <code>execObject</code> already has parameters assigned, this method
     * may overwrite them. 
     * 
     * @param execObject The execution object to which the parameters will
     *                   be added.
     */
    private void addParameters(ExecObject execObject)
            throws IncompleteDataException {
        INodePO execNode = execObject.getExecNode();
        if (execNode instanceof IParamNodePO) {
            IParamNodePO paramNode = (IParamNodePO)execNode;
            List<IParamDescriptionPO> parameterList = 
                    paramNode.getParameterList();
            String value = null;
            for (IParamDescriptionPO desc : parameterList) {
                int column = 0;
                String descriptionId = desc.getUniqueId();
                ITDManager tdManager = null;
                try {
                    tdManager = 
                        m_externalTestDataBP.getExternalCheckedTDManager(
                                paramNode);
                } catch (JBException e) {
                    LOG.error(
                        Messages.TestDataNotAvailable + StringConstants.DOT, e);
                }
                TestExecution te = TestExecution.getInstance();

                List <ExecObject> stackList = 
                    new ArrayList<ExecObject>(getExecStackAsList());
                int dataSetIndex = getDataSetNumber();

                // Special handling for Test Case References that are repeated 
                // via Data Set. The test data manager for such References only has 
                // information about a single Data Set, so we need to ignore the 
                // actual current Data Set number.
                if (tdManager.getDataSetCount() <= 1) {
                    dataSetIndex = 0;
                }
                
                // Special handling for Test Steps. Their test data manager has 
                // information about multiple Data Sets, but we are only interested 
                // in the first one.
                if (paramNode instanceof ICapPO) {
                    dataSetIndex = 0;
                }

                if (tdManager.findColumnForParam(desc.getUniqueId()) == -1) {
                    IParameterInterfacePO referencedDataCube = paramNode
                            .getReferencedDataCube();
                    if (referencedDataCube != null) {
                        desc = referencedDataCube.getParameterForName(desc
                                .getName());
                    }
                }
                String date =
                        getDataForExec(execNode, desc, tdManager, dataSetIndex);
                if (StringUtils.isBlank(date)) {
                    throw new IncompleteDataException(
                        NLS.bind(Messages.MissingTestData,
                                execNode.getName()),
                        MessageIDs.E_MISSING_DATA);
                }
                ParamValueConverter conv = new ModelParamValueConverter(
                        date, paramNode,  desc);
                try {
                    value = conv.getExecutionString(stackList);
                } catch (InvalidDataException e) {
                    LOG.info(e.getMessage());
                    value = MessageIDs.getMessageObject(e.getErrorId()).
                        getMessage(new String[] {e.getLocalizedMessage()});
                }

                // It's important to use 'descriptionId' here instead of 
                // 'desc.getUniqueId()', as 'desc' may have changed between
                // its definition and here.
                execObject.addParameter(descriptionId, 
                        StringUtils.defaultString(value));
                column++;
            }
        }
    }

    /**
     * This method is also searching for default values if in the Exec there is only one CAP
     * @param node if this is a {@link IExecTestCasePO} we are also searching if
     *            there are default values
     * @param desc the {@link IParamDescriptionPO} to get the correct value for
     *            the parameter
     * @param tdManager the data manger
     * @param dataSetIndex the index for the data set
     * @return the value (also default value) for the node
     */
    private String getDataForExec(INodePO node, IParamDescriptionPO desc,
            ITDManager tdManager, int dataSetIndex) {
        String data = StringConstants.EMPTY;
        boolean cellNotFound = false;
        try {
            data = tdManager.getCell(dataSetIndex, desc);
        } catch (IndexOutOfBoundsException e) {
            cellNotFound = true;
            // ignored
        }
        if (cellNotFound && StringUtils.isBlank(data)
                && node instanceof IExecTestCasePO) {
            INodePO specNode = ((IExecTestCasePO) node).getSpecTestCase();
            if (specNode instanceof ISpecTestCasePO) {
                data = AbstractParamInterfaceBP
                        .getValueForSpecNodeWithParamDesc(desc,
                                (ISpecTestCasePO) specNode);
            }
        }
        return data;
    }
    
    /**
     * Sets the building flag
     * @param build the flag
     */
    public void setBuilding(boolean build) {
        m_building = build;
    }
    
    /**
     * @param iterMax the maximum iterate count
     */
    public void setIterMax(int iterMax) {
        m_iterMax = iterMax;
    }
}