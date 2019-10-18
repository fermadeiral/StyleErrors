/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;

/**
 * Class helping handling native SQL queries
 * Native SQL queries ignore our internal locks, so before executing these,
 *    the caller is responsible for acquiring proper locks for any possibly affected nodes
 * The versions of the objects are properly increased in the DB
 * @author BREDEX GmbH
 *
 */
public class NativeSQLUtils {
    
    /** Maximum number of elements in a DB Query list - 1000 for Oracle... */
    private static final int MAXLGT = 990;
    
    /** Exception message */
    private static final String FAIL = "Operation failed due to database error."; //$NON-NLS-1$ 
    
    /** private constructor */
    private NativeSQLUtils() {
        // private constructor
    }
    
    /**
     * Returns a list of Ids in the collection
     * @param objects the objects - should not be empty
     * @return the list
     */
    public static String getIdList(
            Collection<? extends IPersistentObject> objects) {
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("Collection should not be empty."); //$NON-NLS-1$
        }
        StringBuilder str = new StringBuilder("("); //$NON-NLS-1$
        for (IPersistentObject per : objects) {
            str.append(per.getId());
            str.append(","); //$NON-NLS-1$
        }
        str.deleteCharAt(str.length() - 1);
        str.append(")"); //$NON-NLS-1$
        return str.toString();
    }
    
    /**
     * Returns a list of lists of ids in the collection, each list is limited in size
     * @param objects the objects
     * @return the list of lists
     */
    public static List<String> getSlicedIdLists(
            Collection<? extends IPersistentObject> objects) {
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("Collection should not be empty."); //$NON-NLS-1$
        }
        StringBuilder str = null;
        int num = 0;
        List<String> result = new ArrayList<>();
        
        for (IPersistentObject per : objects) {
            if (num == 0) {
                str = new StringBuilder("("); //$NON-NLS-1$
            }
            str.append(per.getId());
            str.append(","); //$NON-NLS-1$
            num++;
            if (num == MAXLGT) {
                str.deleteCharAt(str.length() - 1);
                str.append(")"); //$NON-NLS-1$
                result.add(str.toString());
                num = 0;
            }
        }
        if (num != 0) {
            str.deleteCharAt(str.length() - 1);
            str.append(")"); //$NON-NLS-1$
            result.add(str.toString());
        }
        return result;
    }

    /**
     * Deletes a collection of independent top nodes from either the TC or TS Browsers
     *    The nodes have to be managed by the master session, and will be chaged
     * @param sess the session
     * @param nodes the nodes
     * @param monitor the progress monitor or null
     */
    public static void deleteFromTCTSTreeAFFECTS(EntityManager sess,
            Collection<INodePO> nodes, IProgressMonitor monitor) {
        
        for (INodePO node : nodes) {
            node.goingToBeDeleted(sess);
            removeNodeAFFECTS(sess, node);
            if (monitor != null) {
                monitor.worked(1);
            }
        }
        Query q;
        for (String smallList : getSlicedIdLists(nodes)) {
            q = sess.createNativeQuery("delete from NODE where ID in " + smallList); //$NON-NLS-1$
            q.executeUpdate();
        }
    }
    
    /**
     * Removes a node from its parent
     * This operation changes both the DB and the memory data.
     * @param sess the session
     * @param node the node
     */
    public static void removeNodeAFFECTS(EntityManager sess, INodePO node) {
        Query q1, q2;
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        int pos = -1;
        INodePO par = node.getParentNode();
        
        pos = par.indexOf(node);
        q1 = sess.createNativeQuery("update NODE set PARENT = null, IDX = null where ID = ?1"); //$NON-NLS-1$
        q1.setParameter(1, node.getId()).executeUpdate();
        q2 = sess.createNativeQuery("update NODE set IDX = IDX - 1 where PARENT = ?1 and IDX > ?2"); //$NON-NLS-1$
        q2.setParameter(1, par.getId());
        q2.setParameter(2, pos).executeUpdate();
        node.getParentNode().removeNode(node);
    }
    
    /**
     * Adds a node to another to the end of the child list
     * The parent node can be the TCB or TSB root node - in this case the master
     *      session's Spec(Exec)ObjCont is affected
     * This operation changes both the DB and the memory data.
     * @param sess the session
     * @param toAdd the node to add
     * @param par the target
     */
    public static void addNodeAFFECTS(EntityManager sess, INodePO toAdd,
            IPersistentObject par) {
        Query q1;
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        int pos = -1;
        pos = ((INodePO) par).getNodeListSize();
        q1 = sess.createNativeQuery("update NODE set PARENT = ?1, IDX = ?3 where ID = ?2"); //$NON-NLS-1$
        q1.setParameter(1, par.getId()).setParameter(2, toAdd.getId());
        int res = q1.setParameter(3, pos).executeUpdate();
        if (res != 1) {
            throw new PersistenceException(FAIL);
        }
        ((INodePO) par).addNode(toAdd);
    }
    
    /**
     * Moves a node from somewhere to somewhere else
     * This operation changes both the DB and the memory data.
     * @param sess the session
     * @param toMove node to move
     * @param target the target
     */
    public static void moveNode(EntityManager sess, INodePO toMove,
            IPersistentObject target) {
        removeNodeAFFECTS(sess, toMove);
        addNodeAFFECTS(sess, toMove, target);
    }

    /**
     * Finds the IDs of SpecTCs which contain any children from the supplied
     *    Nodes.
     * @param nodes the nodes
     * @param sess An EntityManager
     * @return the set of ids
     */
    @SuppressWarnings("unchecked")
    public static Set<Long> getSpecTCParentIds(
            Collection<? extends INodePO> nodes,
            EntityManager sess) {
        Set<Long> result = new HashSet<>();
        // IDs of non-SpecTC NodePOs - we have to collect the
        //   SpecTC ancestors of these.
        List<Long> extendingListOfIDs = new ArrayList<>();
        for (INodePO node : nodes) {
            extendingListOfIDs.add(node.getId());
        }
        int nextToHandle = 0;
        // We have to collect recursively more and more ancient ancestors,
        //   stopping at SpecTCPOs, as long as there are any more
        //   'unhandled' nodes.
        while (nextToHandle < extendingListOfIDs.size()) {
            int notYetHandled = Math.min(extendingListOfIDs.size(),
                    nextToHandle + MAXLGT);
            String listIDs = StringUtils.join(extendingListOfIDs.subList(
                    nextToHandle, notYetHandled), ","); //$NON-NLS-1$
            Query q = sess.createNativeQuery("select CLASS_ID, ID, PARENT from NODE where ID in (" + listIDs + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            List<Object[]> res = q.getResultList();
            for (Object[] next : res) {
                if (StringUtils.equals((String) next[0],
                        ISpecTestCasePO.DISCRIMINATOR)) {
                    result.add((Long) next[1]);
                } else {
                    extendingListOfIDs.add((Long) next[2]);
                }
            }
            nextToHandle = notYetHandled;
        }
        return result;
    }
}
