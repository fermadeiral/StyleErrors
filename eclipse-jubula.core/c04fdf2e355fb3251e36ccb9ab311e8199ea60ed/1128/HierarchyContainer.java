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
package org.eclipse.jubula.rc.common.components;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;


/**
* This class manages a container from the AUT. <br>
 * 
 * It holds a reference to the instance of the display in the AUT. The names 
 * for the widgets are also stored in instances of this class.<br>
 * @author BREDEX GmbH
 * @created 02.05.2006
 * 
 * @param <COMPONENT_TYPE>
 *            the type of the component
 */
public abstract class HierarchyContainer<COMPONENT_TYPE> {

    /** boolean that indicates whether component name is generated */
    private boolean m_nameGenerated;
    
    /** parent of this component */
    private HierarchyContainer<COMPONENT_TYPE> m_parent;
    
    /** a reference to the component/container in the AUT this instance represents */
    private AUTComponent<COMPONENT_TYPE> m_component;
    
    /** list of HierarchyContainers */
    private List<HierarchyContainer<COMPONENT_TYPE>> m_containerList = 
            new ArrayList<HierarchyContainer<COMPONENT_TYPE>>();
    
    /** list of HierarchyContainers */
    private List<EventListener> m_listenerList = new ArrayList<EventListener>();
    
    /** the name of the component */
    private String m_name;
    
    /**
     * constructor
     * @param component the reference to the container in the AUT
     * @param parent parent of the container
     */
    public HierarchyContainer(AUTComponent<COMPONENT_TYPE> component, 
            HierarchyContainer<COMPONENT_TYPE> parent) {
        
        m_component = component;
        m_parent = parent;
    }
    
    /**
     * constructor
     * @param component the reference to the container in the AUT
     */
    public HierarchyContainer(AUTComponent<COMPONENT_TYPE> component) {
        
        this(component, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof HierarchyContainer)) {
            return false;
        }
        if (obj == this) {
            return true; // a case of identity
        }
        HierarchyContainer o = (HierarchyContainer)obj;
        return m_component.equals(o.m_component);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return m_component.hashCode();
    }
    
    /**
     * @return Returns the component.
     */
    public AUTComponent<COMPONENT_TYPE> getCompID() {
        return m_component;
    }
    
    /**
     * Adds a component to the container.
     * @param component The component to add.
     */
    public void add(HierarchyContainer<COMPONENT_TYPE> component) {
        getContainerList().add(component);
    }
    
    /**
     * Removes a component from the container.
     * @param component The container to add.
     */
    public void remove(HierarchyContainer<COMPONENT_TYPE> component) {
        getContainerList().remove(component);
    }

    /**
     * @return Returns the components of the container.
     */
    public HierarchyContainer<COMPONENT_TYPE>[] getComps() {
        if (getContainerList().isEmpty()) {
            return new HierarchyContainer[0];
        }
        Object[] objectArray = getContainerList().toArray();
        HierarchyContainer<COMPONENT_TYPE>[] containerArray = 
            new HierarchyContainer[objectArray.length];
        for (int i = 0; i < objectArray.length; i++) {
            containerArray[i] = (HierarchyContainer)objectArray[i]; 
        }
        return containerArray;
    }
    
    /**
     * Adds container listener to listener list.
     * 
     * @param listener
     *            the container listener
     */
    protected void addContainerListnr(EventListener listener) {
        m_listenerList.add(listener);
    }

    /**
     * Removes container listener from listener list.
     * 
     * @param listener
     *            the container listener
     */
    protected void removeContainerListener(EventListener listener) {
        m_listenerList.remove(listener);
    }

    /**
     * @return Returns the listenerList.
     */
    protected EventListener[] getListnrs() {
        if (m_listenerList.isEmpty()) {
            return new EventListener[0];
        }
        return m_listenerList
            .toArray(new EventListener[m_listenerList.size()]);
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name The name to set.
     * @param nameGenerated A boolean indicating whether name is generated.
     */
    public final void setName(String name, boolean nameGenerated) {
        m_name = name;
        m_nameGenerated = nameGenerated;
    }
    
    /**
     * @return Returns a boolean indicating whether name is generated.
     */
    public final boolean isNameGenerated() {
        return m_nameGenerated;
    }

    /**
     * @return Returns the parent.
     */
    public HierarchyContainer<COMPONENT_TYPE> getPrnt() {
        return m_parent;
    }
    
    /**
     * @param parent the parent
     */
    public void setPrnt(HierarchyContainer<COMPONENT_TYPE> parent) {
        m_parent = parent; 
    }

    /**
     * @return the containerList
     */
    protected List<HierarchyContainer<COMPONENT_TYPE>> getContainerList() {
        return m_containerList;
    }
}