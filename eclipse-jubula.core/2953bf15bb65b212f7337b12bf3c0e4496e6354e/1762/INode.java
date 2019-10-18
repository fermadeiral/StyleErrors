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
package org.eclipse.jubula.client.teststyle.properties.nodes;

import javax.persistence.EntityManager;

/**
 * @author marcell
 * @created Oct 21, 2010
 */
public interface INode extends Comparable<INode> {
    
    /** Represent the state in the tree */
    public enum TreeState {
        /** White checkbox */
        EMPTY,
        /** checkbox with a horizontal line */
        GRAYED,
        /** Fully checked checkbox */
        CHECKED
    }
    
    /** @return All children of this node. */
    public abstract INode[] getChildren();
    
    /** @return The parent of this node. */
    public abstract INode getParent();
    
    /** @return The state of this node. */
    public abstract TreeState getState();
    
    /** @param state The behaviour of the node when the node is set active. */
    public abstract void setState(TreeState state);
    
    /**
     * How the node saves it changes in the model
     * @param s The enetitymanager which persists it.
     */
    public abstract void save(EntityManager s);
    
    /** @return Text for the label provider */
    public abstract String getText();
   
    /** @return true, if the node is an editable resource */
    public abstract boolean isEditable();
    
    /** @return true, if the node has an severity */
    public abstract boolean hasSeverity();
    
    /** @return tooltip for this node */
    public abstract String getTooltip();
    
}
