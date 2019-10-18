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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.progress.ElementLoadedProgressListener;
import org.eclipse.jubula.client.core.businessprocess.progress.InsertProgressListener;
import org.eclipse.jubula.client.core.businessprocess.progress.RemoveProgressListener;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.client.core.utils.DependencyCheckerOp;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base class for all kinds of nodes in test tree
 * 
 * @author BREDEX GmbH
 * @created 17.08.2004
 */
@Entity
@Table(name = "NODE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.CHAR, 
                     name = "CLASS_ID")
@DiscriminatorValue(value = "N")
@EntityListeners(value = { 
        ElementLoadedProgressListener.class, 
        InsertProgressListener.class, RemoveProgressListener.class })
abstract class NodePO implements INodePO {
    
    /** unsupported operation warning */
    public static final String NOSUPPORT = "This type of node does not support alteration of its collection of children."; //$NON-NLS-1$
    
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(NodePO.class);
    
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** Globally Unique Identifier for recognizing nodes across databases */
    private transient String m_guid = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;
    
    /** Flag if the parent at the children is set.
     * @see getNodeList() 
     */
    private transient boolean m_isParentNodeSet = false;
    
    /**
     * generated tag
     */
    private boolean m_isGenerated;
    
    /**
     * whether this element has been marked as "active" or "inactive"
     */
    private boolean m_isActive = true;
    
    /**
     * The current toolkit level of this node.
     * Not to persist!
     */
    private transient String m_toolkitLevel = StringConstants.EMPTY;
    
    /**
     * name of the real node, e.g. CapPO name or Testcase name
     */
    private String m_name;

    /**
     * the task Id of the node
     */
    private String m_taskId;

    /**
     * The changed info is a map with a time stamp as key and a comment as value.
     */
    private Map<Long, String> m_trackedChangesMap = new HashMap<Long, String>();

    /**
     * describes, if the node is derived from another node
     */
    private INodePO m_parentNode = null;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /**
     * list of all child nodes, if existent
     */
    private List<INodePO> m_nodeList = new ArrayList<INodePO>();
    
    /**
     * contains the comment for a node
     */
    private String m_comment;  
    
    /** The timestamp */
    private long m_timestamp = 0;
    
    /** set of problems */
    private Set<IProblem> m_problems = new HashSet<IProblem>(5);
    
    /** string for description */
    private String m_description;

    /** */
    private boolean m_isJUnitSuite;
    
    /**
     * constructor for a node with a pre-existing GUID
     * @param name of the node
     * @param guid of the node
     * @param isGenerated indicates whether this node has been generated
     */
    protected NodePO(String name, String guid, boolean isGenerated) {
        setName(name);
        setGuid(guid);
        setGenerated(isGenerated);
    }
    
    /**
     * constructor
     * @param name of the node
     * @param isGenerated indicates whether this node has been generated
     */
    protected NodePO(String name, boolean isGenerated) {
        this(name, PersistenceUtil.generateUUID(), isGenerated);
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    NodePO() {
        // only for Persistence (JPA / EclipseLink)
    }

    
    /**
     * @param nodeList The nodeList to set.
     */
    void setHbmNodeList(List<INodePO> nodeList) {
        m_nodeList = nodeList;
        m_isParentNodeSet = false;
    }
    
    /**
     * 
     * {@inheritDoc}
     * @return The name of this node
     */
    @Transient
    public String getName() {
        return getHbmName();
    }
    
    /**
     * gets the value of the m_name property
     * 
     * @return the name of the node
     */
    @Basic
    @Column(name = "NAME", length = MAX_STRING_LENGTH)
    private String getHbmName() {
        return m_name;
    }
    
    /**
     * For Persistence (JPA / EclipseLink) only
     * Sets the value of the m_name property.
     * 
     * @param name
     *            the new value of the m_name property
     */
    private void setHbmName(String name) {
        m_name = name;
    }

    /**
     * Sets the value of the m_name property.
     * @param name the name of this node
     */
    public void setName(String name) {
        setHbmName(name);
    }
    
    /**
     * @return the current value of the m_parentNode property or null
     */
    @Transient
    public INodePO getParentNode() {
        return m_parentNode;
    } 
    
    /**
     * @param parent parent to set
     */
    public void setParentNode(INodePO parent) {
        if (LOG.isErrorEnabled() && parent == null) {
            try {
                throw new IllegalArgumentException(
                        "The parent of the INodePO (UUID " + getGuid()  //$NON-NLS-1$
                            + ") is not intended to be set to null."); //$NON-NLS-1$
            } catch (IllegalArgumentException e) {
                LOG.info(ExceptionUtils.getFullStackTrace(e), e);
            }
            
        }
        m_parentNode = parent;
    }

    /**
     * 
     * Access method for the m_nodeList property.
     * only to use for Persistence (JPA / EclipseLink)
     * 
     * The reason for the Many-To-Many is that Eclipselink cannot deal with moving around
     *     nodes of a tree which have grandchildren...
     * 
     * @return the current value of the m_nodeList property
     */
    @OneToMany(fetch = FetchType.LAZY, 
                cascade = CascadeType.ALL, 
                targetEntity = NodePO.class)
    @JoinColumn(name = "PARENT")
    @OrderColumn(name = "IDX")
    @BatchFetch(value = BatchFetchType.JOIN)
    List<INodePO> getHbmNodeList() {
        return m_nodeList;
    }
    
    /**
     * Frees the node children of the node, lets them handle their children and deletes them
     * @param sess the session
     */
    private void removeNodeChildren(EntityManager sess) {
        if (m_nodeList.isEmpty()) {
            return;
        }
        for (INodePO child : m_nodeList) {
            child.goingToBeDeleted(sess);
        }
        Query q = sess.createNativeQuery("delete from NODE where ID in " + NativeSQLUtils.getIdList(m_nodeList)); //$NON-NLS-1$
        q.executeUpdate();
    }
    
    /**
     * @return The List of children nodes
     */
    @Transient
    List<INodePO> getNodeList() {
        if (!m_isParentNodeSet) {
            List<INodePO> nodeList = getHbmNodeList();
            for (Object o : nodeList) {
                INodePO node = (INodePO)o;
                node.setParentNode(this);
            }
            m_isParentNodeSet = true;
        }
        return getHbmNodeList();
    }
    
    /**
     * @return the unmodifiable node list.
     */
    @Transient
    public List<INodePO> getUnmodifiableNodeList() {
        return Collections.unmodifiableList(getNodeList());
    }
    
    /**
     * 
     * @return Returns the m_comment.
     */
    @Basic
    @Column(name = "COMM_TXT", length = MAX_STRING_LENGTH)
    private String getHbmComment() {
        return m_comment;
    }
    
    
    /**
     * @return Returns the m_comment.
     */
    @Transient
    public String getComment() {
        return getHbmComment();
    }
    
    /**
     * For Persistence (JPA / EclipseLink) only
     * @param comment The m_comment to set.
     */
    private void setHbmComment(String comment) {
        m_comment = comment;
    }
    
    /**
     * @param comment The m_comment to set.
     */
    public void setComment(String comment) {
        setHbmComment(comment);
    }
    /**
     * adds a childnode to an existent node
     * creation of reference to the parent node
     * @param childNode
     *            reference to the childnode
     */
    public void addNode(INodePO childNode) {
        addNode(-1, childNode);
    }
    
    /**
     * adds a childnode to an existent node
     * creation of reference to the parent node
     * @param position the position to add the childnode.
     * @param childNode
     *            reference to the childnode
     */
    public void addNode(int position, INodePO childNode) {
        if (position < 0 || position > getNodeList().size()) {
            getNodeList().add(childNode);
        } else {
            getNodeList().add(position, childNode);            
        }
        childNode.setParentNode(this);
        setParentProjectIdForChildNode(childNode);
    }

    /**
     * Sets the child node's parentProjectId equal to this node's parentProjectId.
     * This is the default implementation. Subclasses may override.
     * 
     * @param childNode The node that will have its parentProjectId set.
     */
    protected void setParentProjectIdForChildNode(INodePO childNode) {
        childNode.setParentProjectId(getParentProjectId());
    }

    /**
     * deletes a node and resolves the
     * reference to the parent node
     * sign off as child node of the parent node
     * @param childNode reference to the childnode
     */
    public void removeNode(INodePO childNode) {
        ((NodePO)childNode).removeMe(this);   
    }
    
    /**
     * @param parent removes the node from childrenList or eventhandlerMap
     */
    protected void removeMe(INodePO parent) {
        ((NodePO)parent).getNodeList().remove(this);
    }
    
    /**
     * Removes all child nodes and sets the parent of the child nodes 
     * to <code>null</code>
     */
    public void removeAllNodes() {
        Iterator<INodePO> iter = getNodeList().iterator();
        while (iter.hasNext()) {
            INodePO childNode = iter.next();
            childNode.setParentNode(null);
            iter.remove();
        }
    }
    
    /**
     * Returns the index of the given node in the node list.
     * @param node the node whose index is want.
     * @return the index of the given node.
     */
    public int indexOf(INodePO node) {
        return getNodeList().indexOf(node);
    }
    
    /**
     * Returns the valid status of the node.<br>
     * Normally all Nodes are valid. only CapPOs with an InvalidComponent
     * should return false.
     * @return true if the Node is valid, false otherwise. 
     */
    @Transient
    public boolean isValid() {
        return true;
    }
    
    
    /** {@inheritDoc} */
    public int hashCode() { // NOPMD by al on 3/19/07 1:35 PM
        return getGuid().hashCode();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Iterator<INodePO> getNodeListIterator() {
        return Collections.unmodifiableList(getNodeList()).iterator();
    }
       
    /**
     * @return size of nodeList
     */
    @Transient
    public int getNodeListSize() {
        return getNodeList().size();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return super.toString() + StringConstants.SPACE 
            + StringConstants.LEFT_PARENTHESIS + getName() 
            + StringConstants.RIGHT_PARENTHESIS;
    }
    
    /**
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }
    /**
     * 
     * @return Long
     */
    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return m_version;
    }
    /**
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }
    
    /**
     *    
     * @return the GUID.
     */
    @Basic
    @Column(name = "GUID")
    @Index(name = "PI_NODE_GUID")
    public String getGuid() {
        return m_guid;
    }
    /**
     * @param guid The GUID to set.
     */
    private void setGuid(String guid) {
        m_guid = guid;
    }

    /**
     * Checks for circular dependences with a potential parent.
     * @param parent the parent to check
     * @return true if there is a circular dependence, false otherwise.
     */
    public boolean hasCircularDependencies(INodePO parent) {

        DependencyCheckerOp op = new DependencyCheckerOp(parent);
        TreeTraverser traverser = new TreeTraverser(this, op);
        traverser.traverse(true);
        return op.hasDependency();
    }
    
    /**
     * @param parent the parent to check
     * @return the path where the dependency occurs
     * 
     */
    public String collectPathtoConflictNode(INodePO parent) {
        
        DependencyCheckerOp op = 
                new DependencyCheckerOp(parent);
        TreeTraverser traverser = new TreeTraverser(this, op);
        traverser.traverse(true);
        String dependencyPath = op.getDependencyPath();
        return dependencyPath;
    }
    
    /**
     * Checks the equality of the given Object with this Object.
     * {@inheritDoc}
     * @param obj the object to check
     * @return if there is a database ID it returns true if the ID is equal.
     * If there is no ID it will be compared to identity.
     */
    public boolean equals(Object obj) { // NOPMD by al on 3/19/07 1:35 PM
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NodePO  || obj instanceof INodePO)) {
            return false;
        }
        INodePO o = (INodePO)obj;
        return getGuid().equals(o.getGuid());
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (INodePO node : getHbmNodeList()) {
            node.setParentProjectId(projectId);
        }
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    @Index(name = "PI_NODE_PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * 
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * @return the current toolkit level of this node.
     */
    @Transient
    public String getToolkitLevel() {
        return m_toolkitLevel;
    }

    /**
     * Sets the current toolkit level of this node.
     * @param toolkitLevel the toolkit level.
     */
    public void setToolkitLevel(String toolkitLevel) {
        m_toolkitLevel = toolkitLevel;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Basic
    public long getTimestamp() {
        return m_timestamp;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setTimestamp(long timestamp) {
        m_timestamp = timestamp;
    }

    /**
     *      
     * @return the isGenerated Attribute for all nodes
     */
    @Basic(optional = false)
    @Column(name = "IS_GENERATED")
    public boolean isGenerated() {
        return m_isGenerated;
    }

    /**
     * @param isGenerated the isGenerated to set
     */
    public void setGenerated(boolean isGenerated) {
        m_isGenerated = isGenerated;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setActive(boolean isActive) {
        m_isActive = isActive;
    }

    /**
     *      
     * @return the isActive Attribute for all nodes
     */
    @Basic(optional = false)
    @Column(name = "IS_ACTIVE")
    public boolean isActive() {
        return m_isActive;
    }    
    
    /** {@inheritDoc} */
    public boolean addProblem(IProblem problem) {
        if (isActive()) {
            return m_problems.add(problem);
        }
        return false;
    }
    
    /** {@inheritDoc} */
    public void clearProblems() {
        m_problems.clear();
    }
    
    /** {@inheritDoc} */
    public boolean removeProblem(IProblem problem) {
        return m_problems.remove(problem);
    }
    
    /** {@inheritDoc} */
    public Set<IProblem> getProblems() {
        return Collections.unmodifiableSet(m_problems);
    }
    
    /**
     * gets the value of the taskId property
     * 
     * @return the taskId of the node
     */
    @Basic
    @Column(name = "TASK_ID", length = MAX_STRING_LENGTH)
    public String getTaskId() {
        return m_taskId;
    }
    
    /**
     * For Persistence (JPA / EclipseLink) only
     * Sets the value of the taskId property. If the length of
     * the trimmed new taskId string is zero, the taskId property
     * is set to null.
     * 
     * @param taskId
     *            the new value of the taskId property
     */
    public void setTaskId(String taskId) {
        String newTaskId = taskId;
        if (newTaskId != null) {
            newTaskId = newTaskId.trim();
            if (newTaskId.length() == 0) {
                newTaskId = null;
            }
        }
        m_taskId = newTaskId;
    }

    /**
     * Only for Persistence (JPA / EclipseLink).
     * @param trackedChangesMap The tracked changes as a map of time stamp as key and comment as value.
     */
    public void setTrackedChangesMap(Map<Long, String> trackedChangesMap) {
        this.m_trackedChangesMap = trackedChangesMap;
    }

    /**
     * Only for Persistence (JPA / EclipseLink).
     * @return The map of change information.
     */
    @ElementCollection
    @CollectionTable(name = "NODE_TRACK",
            joinColumns = @JoinColumn(name = "NODE_ID", nullable = false))
    @MapKeyColumn(name = "TIMESTAMP", nullable = false)
    @Column(name = "TRACK_COMMENT")
    private Map<Long, String> getTrackedChangesMap() {
        return m_trackedChangesMap;
    }
    
    /**
     * Removes the track children of the node
     * @param sess the session
     */
    private void removeTrackChildren(EntityManager sess) {
        Query q = sess.createNativeQuery("delete from NODE_TRACK where NODE_ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId());
        q.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public void addTrackedChange(String optionalComment, boolean isCleaning) {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project == null || !project.getIsTrackingActivated()) {
            return; // no project is opened or tracking not activated
        }
        final long timestampInMS = new Date().getTime();
        if (isCleaning) { // remove tracked changes
            int span = project.getProjectProperties().getTrackChangesSpan();
            switch (project.getProjectProperties().getTrackChangesUnit()) {
                case CHANGES:
                    removeTrackedChangesByCount(span - 1); // -1 for the new one
                    break;
                case DAYS:
                    removeTrackedChangesByTimeInDays(timestampInMS, span);
                    break;
                default:
                    return;
            }
        }
        final String name = project.getProjectProperties()
                .getTrackChangesSignature();
        String value = StringConstants.EMPTY;
        if (!StringUtils.isEmpty(name)) {
            value = EnvironmentUtils.getProcessOrSystemProperty(name);
        }
        if (value != null) {
            StringBuffer comment = new StringBuffer(value);
            if (optionalComment.length() > 0) {
                if (comment.length() > 0) {
                    comment.append(StringConstants.COLON);
                    comment.append(StringConstants.SPACE);
                }
                comment.append(optionalComment);
            }
            m_trackedChangesMap.put(timestampInMS, comment.toString());
        }
    }

    /**
     * Remove tracked changes of this node beginning with the oldest, if the maximum number has reached.
     * @param maxTrackedChangesPerNode The maximum allowed number of tracked changes per node.
     */
    private void removeTrackedChangesByCount(
            final int maxTrackedChangesPerNode) {
        if (maxTrackedChangesPerNode >= 0) {
            SortedMap<Long, String> copy = getTrackedChanges();
            for (int removeCount = m_trackedChangesMap.size()
                    - maxTrackedChangesPerNode;
                    removeCount > 0; removeCount--) {
                Long key = copy.lastKey();
                copy.remove(key);
                m_trackedChangesMap.remove(key);
            }
        }
    }

    /**
     * Remove tracked changes of this node beginning with the oldest, if they are created before
     * the given time stamp.
     * @param currentTimesInMS The current time stamp in milliseconds
     * @param days The maximum allowed number in days of tracked changes per node.
     */
    private void removeTrackedChangesByTimeInDays(
            final long currentTimesInMS, final int days) {
        if (days >= 0) {
            final long durationOfDayInMS = 1000L * 60 * 60 * 24;
            final long beginningInMS =
                    currentTimesInMS - days * durationOfDayInMS;
            SortedMap<Long, String> copy = getTrackedChanges();
            while (copy.size() > 0 && copy.lastKey() < beginningInMS) {
                Long key = copy.lastKey();
                copy.remove(key);
                m_trackedChangesMap.remove(key);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<Long, String> getTrackedChanges() {
        SortedMap<Long, String> sortedMap = new TreeMap<Long, String>(
                Collections.reverseOrder());
        sortedMap.putAll(m_trackedChangesMap);
        return sortedMap;
    }
    
    /**
     * {@inheritDoc}
     */
    public void deleteTrackedChanges() {
        m_trackedChangesMap.clear();
    }
    
    /**
     * 
     * @return Returns the m_description.
     */
    @Column(name = "DESCRIPTION_TXT")
    @Lob
    public String getDescription() {
        return m_description;
    }
    
    /**
     * @param description The m_description to set.
     */
    public void setDescription(String description) {
        m_description = description;
    }
    
    /** {@inheritDoc} */
    @Transient
    public Iterator<INodePO> getAllNodeIter() {
        return getNodeListIterator();
    }
    
    /** {@inheritDoc} */
    @Transient
    public INodePO getSpecAncestor() {
        INodePO par = this;
        while (par != null && !(par instanceof ISpecTestCasePO 
                || par instanceof ITestSuitePO || par instanceof ITestJobPO)) {
            par = par.getParentNode();
        }
        return par;
    }
    
    /** {@inheritDoc} */
    public void goingToBeDeleted(EntityManager sess) {
        removeNodeChildren(sess);
        removeTrackChildren(sess);
    }

    /**
     * @return return whether the Node acts as a JUnit testsuite
     */
    @Basic
    @Column(name = "JUNITTESTSUITE")
    public boolean isJUnitTestSuite() {
        return m_isJUnitSuite;
    }
    
    /**
     * @param isJUnitSuite true = node is viewed as a testsuite
     */
    public void setJUnitTestSuite(boolean isJUnitSuite) {
        m_isJUnitSuite = isJUnitSuite;
    }
    
    /** {@inheritDoc} */
    public boolean isExecObjCont() {
        INodePO par = getParentNode();
        if (!(par instanceof IProjectPO)) {
            return false;
        }
        return this.equals(((IProjectPO) getParentNode()).getExecObjCont());
    }
    
    /** {@inheritDoc} */
    public boolean isSpecObjCont() {
        INodePO par = getParentNode();
        if (!(par instanceof IProjectPO)) {
            return false;
        }
        return this.equals(((IProjectPO) getParentNode()).getSpecObjCont());
    }

}
