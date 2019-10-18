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
package org.eclipse.jubula.client.core.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.persistence.EntityManager;

import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;

/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface INodePO extends ITimestampPO {

    /** The created string. */
    public static final String CREATED = "created"; //$NON-NLS-1$

    /**
     * @return The name of this node
     */
    public abstract String getName();

    /**
     * Sets the value of the m_name property.
     * @param name the name of this node
     */
    public abstract void setName(String name);
    
    /**
     * @return the model parent node of this node
     */
    public INodePO getParentNode();
    
    /**
     * @return the unmodifiable node list.
     */
    public abstract List<INodePO> getUnmodifiableNodeList();

    /**
     * @return Returns the m_comment.
     */
    public abstract String getComment();

    /**
     * @param comment The m_comment to set.
     */
    public abstract void setComment(String comment);
    
    /**
     * adds a childnode to an existent node
     * creation of reference to the parent node
     * @param childNode
     *            reference to the childnode
     */
    public abstract void addNode(INodePO childNode);

    /**
     * adds a childnode to an existent node
     * creation of reference to the parent node
     * @param position the position to add the childnode.
     * @param childNode
     *            reference to the childnode
     */
    public abstract void addNode(int position, INodePO childNode);

    /**
     * deletes a node and resolves the
     * reference to the parent node
     * sign off as child node of the parent node
     * @param childNode reference to the childnode
     */
    public abstract void removeNode(INodePO childNode);

    /**
     * Removes all child nodes and sets the parent of the child nodes 
     * to <code>null</code>
     */
    public abstract void removeAllNodes();

    /**
     * Returns the index of the given node in the node list.
     * @param node the node whose index is want.
     * @return the index of the given node.
     */
    public abstract int indexOf(INodePO node);

    /**
     * {@inheritDoc}
     */
    public abstract int hashCode();

    /**
     * The behaviour of this iterator is the following:
     *  - for SpecTestCasePOs it returns the normal nodes - without the Event Handlers
     *  - for ExecTestCasePOs it returns the corresponding SpecTestCasePO's getNodeListIterator,
     *      or an empty iterator if the SpectTCPO is null
     *  - for ConditionPOs it returns the condition nodes
     * @return iterator for unmodifiable NodeList
     */
    public abstract Iterator<INodePO> getNodeListIterator();
    
    /**
     * The behaviour of this iterator is the following:
     *  - for SpecTestCasePOs it returns all nodes, event handlers and all children of
     *        children of RestrictedNode children
     *  - for ExecTestCasePOs it returns the corresponding SpecTestCasePO's getAllNodeIter,
     *      or an empty iterator if the SpectTCPO is null
     * This assumes a very restricted structure of children for SpecTestCasePOs:
     *    Restricted Nodes only have Containers as direct children which in turn cannot contain
     *    Containers or Restricted Nodes
     * @return the iterator
     */
    public Iterator<INodePO> getAllNodeIter();

    
    /**
     * @return boolean that shows whether a testcase is used as a JUnitTestcase
     */
    public abstract boolean isJUnitTestSuite();

    /**
     * @param isJUnitTestsuite sets the value of the m_isJUnitTestSuite property 
     */
    public abstract void setJUnitTestSuite(boolean isJUnitTestsuite);
    
    /**
     * @return size of nodeList
     */
    public abstract int getNodeListSize();

    /**
     * {@inheritDoc}
     */
    public abstract String toString();

    /**
     * @return Returns the GUID.
     */
    public abstract String getGuid();
    
    /**
     * Checks for circular dependences with a potential parent.
     * @param parent the parent to check
     * @return true if there is a circular dependence, false otherwise.
     */
    public abstract boolean hasCircularDependencies(INodePO parent);

    /**
     * @param parent the parent to check
     * @return the path where the dependenvcy occurs
     * 
     */
    public abstract String collectPathtoConflictNode(INodePO parent);
    
    /**
     * Checks the equality of the given Object with this Object.
     * {@inheritDoc}
     * @param obj the object to check
     * @return if there is a database ID it returns true if the ID is equal.
     * If there is no ID it will be compared to identity.
     */
    public abstract boolean equals(Object obj);
    
    /**
     * @param parent
     *            the model parent to set
     */
    public abstract void setParentNode(INodePO parent);

    /**
     * @return the current toolkit level of this node.
     */
    public abstract String getToolkitLevel();

    /**
     * Sets the current toolkit level of this node.
     * @param toolkitLevel the toolkit level.
     */
    public abstract void setToolkitLevel(String toolkitLevel);
    
    /**
     * Returns the valid status of the node.<br>
     * Normally all Nodes are valid. only CapPOs with an InvalidComponent
     * should return false.
     * @return true if the Node is valid, false otherwise. 
     */
    public boolean isValid();

    /**
     * @return true, if node has been generated, false otherwise
     */
    public boolean isGenerated();
    
    /**
     * @param isGenerated flag for generation 
     */
    public void setGenerated(boolean isGenerated);
    
    /**
     * @param isActive the isActive to set
     */
    public void setActive(boolean isActive);

    /**
     * @return the isActive
     */
    public boolean isActive();
    
    /**
     * Adds a problem to this node. This problem will be used to display 
     * tooltips, decorations and problems in the UI
     * 
     * @param problem The problem that is added to this node.
     * @return <code>true</code> if the problem was added and wasn't already
     * in the list.
     */
    public boolean addProblem(IProblem problem);
    
    /** 
     * Removes problem from this node. 2 Problems are equal, if their fields are
     * equal. 
     * 
     * @param problem Problem that should be removed.
     * @return <code>true</code> if the problem did exist on this node and was
     * successfully removed.
     */
    public boolean removeProblem(IProblem problem);
    
    /**
     * Returns an immutable list of problems. To remove or add problems, please
     * use addProblem and removeProblem.
     * 
     * @return Unmodifiable set of problems.
     */
    public Set<IProblem> getProblems();
    
    /**
     * @return The taskId of this node
     */
    public String getTaskId();

    /**
     * Sets the value of the task Id property.
     * @param taskId the taskId of this node
     */
    public void setTaskId(String taskId);

    /**
     * Add a tracked change information consisting of the current time stamp as key and
     * a comment. The created comment begins with the value of the property defined in the
     * project configuration and suffixed by the optional comment, if it is given.
     *
     * Not relevant changes can be are automatically removed by the rules defined
     * in the project configuration, if it is wished.
     * @param optionalComment An additional information added to the value of the property
     *                        defined in the project configuration. It can be null,
     *                        to store only the value of the property defined in the
     *                        project configuration.
     * @param isCleaning True, if not relevant changes are automatically removed by the rules
     *                   defined in the project configuration, otherwise nothing is removed.
     * @see {@link #getTrackedChanges()}
     */
    public void addTrackedChange(String optionalComment, boolean isCleaning);

    /**
     * @return A sorted copied map of change information with time stamp as key and comment as value.
     *         The keys are in descending order, i.e. the first element is the newest tracked change.
     *         The comment can be null.
     * @see {@link #addTrackedChange(Long, String)}
     * @see {@link SortedMap#firstKey()}
     * @see {@link SortedMap#lastKey()}
     */
    public SortedMap<Long, String> getTrackedChanges();

    /**
     * Only for importing.
     * 
     * @param trackedChangesMap
     *            The tracked changes as a map of time stamp as key and comment
     *            as value.
     */
    public void setTrackedChangesMap(Map<Long, String> trackedChangesMap);

    /**
     * clears the list of tracked changes
     */
    public abstract void deleteTrackedChanges();

    /**
     * @return Returns the m_description.
     */
    public String getDescription();
    
    /**
     * @param description The m_description to set.
     */
    public void setDescription(String description);
    
    /**
     * Returns the SpecTC or TestSuite ancestor of a NodePO or null
     * @return the ancestor
     */
    public INodePO getSpecAncestor();
    
    /**
     * A node is going to be deleted, so it should remove all dependencies of its children
     * @param sess the session
     */
    public void goingToBeDeleted(EntityManager sess);
    
    /**
     * @return whether the node is an Exec Obj Cont (TSB root) of a Project
     */
    public boolean isExecObjCont();

    /**
     * @return whether the node is a Spec Obj Cont (TCB root) of a Project
     */
    public boolean isSpecObjCont();
    
}