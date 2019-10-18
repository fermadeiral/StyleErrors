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
package org.eclipse.jubula.tools.internal.xml.businessmodell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.tools.internal.exception.ConfigXmlException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents Components which can be tested by Jubula.
 *
 * @author BREDEX GmbH
 * @created 08.07.2005
 */
public abstract class Component {
    /**
     * the separator to use when listing action/method arguments
     */
    private static final String ARG_SEPARATOR = ", "; //$NON-NLS-1$

    /**
     * The logger
     */
    private static Logger log = LoggerFactory.getLogger(Component.class);

    /**
     * List of directly realized Components
     * ("direct superclasses").
     */
    private List<Component> m_realized = new ArrayList<Component>();
    
    /** List of extended Components. */
    private List<String> m_extendedTypes = new ArrayList<String>();
    
    /**
     * List of type names of directly realized AbstractComponents
     * ("direct superclasses").
     */
    private List<String> m_realizedTypes = new ArrayList<String>();
    
    /**
     * Set of all ConcreteComponent classes realizing this
     * Component. For Concrete Components, this set contains exactly 
     * this ConcreteComponent itself.
     */
    private Set<ConcreteComponent> m_realizers = 
        new HashSet<ConcreteComponent>();
    
    /**
     * Set of all Component classes realizing this
     * Component.
     */
    private transient Set<Component> m_allRealizers = new HashSet<Component>();
    
    /** Action list of the component. */
    private List<Action> m_actions = new ArrayList<Action>();
    
    /** The name of the component. */
    private String m_type;
    
    /** a description key for this component */
    private String m_descriptionKey;
    
    /** The descriptor of the ToolkitPlugin of this Component */
    private ToolkitDescriptor m_toolkitDesriptor;
    
    /**
     * <code>m_completionStarted</code> flag indicating
     * completeActions has started. See also m_completionDone.
     */
    private boolean m_completionStarted = false;
    
    /** Whether this Component is deprecated or not */
    private boolean m_deprecated = false;
    
    /**
     * <code>m_visible</code> property indicating whether this
     * Component shall be usable within the client UI
     */
    private boolean m_visible = true;
    
    /**
     * <code>m_completionDone</code> flag indicating 
     * completeActions has been called once before and 
     * is ready. If m_completionStarted is set but not
     * m_completionDone during a completeActions call, we 
     * found a reference loop.
     */
    private boolean m_completionDone = false;
    
    /**
     * <code>m_observable</code> property indicating whether this
     * Component is observable
     */
    private boolean m_observable = true;
    
    /**
     * Set this true if the API representation has to be the most concrete class.
     * This means that the Factory responsible for creating the component will
     * return the corresponding most concrete component class instead of the most
     * concrete visible one.
     */
    private boolean m_apiMostConcrete = false;
    
    /**
     * The version of the bundle (important for semantic versions) that's
     * initially been present for that new component
     */
    private String m_since;

    /** Default constructor. Do nothing. */
    public Component() {
        super();
    }
    /**
     * @return Returns the list actions.
     */
    public List<Action> getActions() {
        return m_actions;
    }
    
    /**
     * @return the directly realized "super"-components
     */
    public List<Component> getRealized() {
        return m_realized;
    }
    /**
     * @return the directly realized "super"-component's type names
     */
    public List<String> getRealizedTypes() {
        return m_realizedTypes;
    }
    /**
     * @return all directly or indirectly realized "super"-components
     * (excluding this Component itself)
     */
    public Set<Component> getAllRealized() {
        Set<Component> result = new HashSet<Component>();        
        result.addAll(m_realized);
        Iterator<Component> realizedIt = m_realized.iterator();
        while (realizedIt.hasNext()) {
            Component comp = realizedIt.next();
            result.addAll(comp.getAllRealized());
        }
        return result;            
    }
    /**
     * Checks if the passed type is realized by this component. This method gets
     * the same result as <code>getRealizedTypes().contains(type)</code>.
     * 
     * @param type
     *            The type to check
     * @return <code>true</code> if this component realizes <code>type</code>.
     */
    public boolean isRealizing(String type) {
        Validate.notNull(type, "The component type name must not be null"); //$NON-NLS-1$
        Iterator<Component> realizedIt = getAllRealized().iterator();
        while (realizedIt.hasNext()) {
            Component comp = realizedIt.next();
            if (type.equals(comp.getType())) {
                return true;
            }
        }
        return false;
    }
    
    /** 
     * @return true if this Component is realizing another component, false 
     * otherwise.
     */
    public boolean isRealizer() {
        return !m_realized.isEmpty();
    }
    
    /**
     * Checks if the component can be used for the given type.
     * 
     * @param type
     *            The type to check (i18n key).
     * @return <code>true</code> if this component is of the same type as 
     *         <code>type</code> or realizes <code>type</code>.
     */
    public boolean isCompatibleWith(String type) {
        return getType().equals(type) || isRealizing(type);
    }
    /**
     * @return Returns the type.
     */
    public String getType() {
        return m_type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        m_type = type;
    }
    /**
     * Returns the size of the actions list.
     * @return an <code>int</code> value.
     */
    public int getActionsSize() {
        return m_actions != null ? m_actions.size() : 0;
    }
    
    /**
     * Adds an action to the action list.
     * @param action an <code>Action</code> object.
     */
    public void addAction(Action action) {
        Iterator<Action> actionIt = m_actions.iterator();
        while (actionIt.hasNext()) {
            Action present = actionIt.next();
            if (present.equals(action)) {
                return; // just ignore the second
                        // attempt to add the same 
                        // action, may be perfectly legal.
            } 
            if (present.getName().equals(action.getName())) {
                final String msg = "duplicate definition of Action " //$NON-NLS-1$
                    + action.getName() 
                    + " at component " + getType() //$NON-NLS-1$
                    + ": method " + present.getMethod()  //$NON-NLS-1$
                    + " vs. method " + action.getMethod(); //$NON-NLS-1$
                log.error(msg);
                throw new ConfigXmlException(msg, 
                    MessageIDs.E_DUPLICATE_ACTION); 
            }
        }
        m_actions.add(action);
    }
    
    /**
     * Returns the action with the specified name.
     * @param name The name of the specified action
     * @return the specified action.
     */
    public Action findAction(String name) {
        Validate.notNull(name);      
        List<Action> actionList = getActions();
        Iterator<Action> actionIt = actionList.iterator();
        while (actionIt.hasNext()) {
            Action action = actionIt.next();
            if (name.equals(action.getName())) {
                return action;
            }
        }
        String message = "Action " //$NON-NLS-1$
            + name + " not found within component " //$NON-NLS-1$
            + m_type;
        log.debug(message);
        return new InvalidAction();
    }
    /**
     * @param methodName
     *            The method name.
     * @param argTypes
     *            The method's argument types.
     * @return The action for the passed method signature.
     */
    public Action findActionByMethodSignature(String methodName, 
            String[] argTypes) {
        
        Validate.notNull(methodName);      
        List<Action> actionList = getActions();
        Iterator<Action> actionIt = actionList.iterator();
        while (actionIt.hasNext()) {
            Action action = actionIt.next();
            if (isSignatureMatch(action, methodName, argTypes)) {
                return action;
            }
        }
        StringBuffer sb = new StringBuffer("Action "); //$NON-NLS-1$
        sb.append(methodName).append("("); //$NON-NLS-1$
        for (int i = 0; i < argTypes.length; i++) {
            sb.append(argTypes[i]);
            if (i < argTypes.length - 1) {
                sb.append(ARG_SEPARATOR);
            }
        }
        sb.append(") does not exist."); //$NON-NLS-1$
        String message = sb.toString();
        log.error(message);
        throw new ConfigXmlException(message, MessageIDs.E_NO_ACTION);
    }
    

    /**
     * Checks the given method signatures for equality.
     * 
     * @param action     One of the method signatures to check.
     * @param methodName The name of the second method to check.
     * @param argTypes   The argument types of the second method to check.
     * @return <code>true</code> if the given method signatures match. 
     *         Otherwise, <code>false</code>.
     */
    private boolean isSignatureMatch(Action action, String methodName,
            String[] argTypes) {

        
        if (methodName.equals(action.getMethod())) {
            // name matches, do arguments match?
            List actionParams = action.getParams();
            if (actionParams.size() != argTypes.length) {
                return false;
            }
            for (int i = 0; i < argTypes.length; i++) {
                Param param = (Param)actionParams.get(i);
                if (!param.getType().equals(argTypes[i])) {
                    return false;
                }
            }
            // name and argument types match
            return true;
        }

        return false;
    }
    /**
     * @return true if this Component is of type ConcreteComponent
     */
    public boolean isConcrete() {
        return false; /* this method is overridden in ConcreteComponent */
    }
        
    /**
     * @param realized the type name of a realized AbstractComponent
     * to add
     */
    public void addRealizedType(String realized) {
        m_realizedTypes.add(realized);
    }
    /**
     * @param realized a realized Component
     * to add
     */
    public void addRealized(Component realized) {
        m_realized.add(realized);
    }
    /**
     * @return Returns the realizers (all ConcreteComponents
     * that directly or indirectly realize this Component.
     * For ConcreteComponents themselves, this is a set with exactly
     * one element, namely itself.)
     */
    public Set<ConcreteComponent> getRealizers() {
        return m_realizers;
    }
    
    
    /**
     * @return Returns the realizers (all Components
     * that directly or indirectly realize this Component.
     */
    public Set<Component> getAllRealizers() {
        return m_allRealizers;
    }
    
    /**
     * @param realizer the realizing ConcreteComponent to add
     * @return true if the realizer wasn't present before
     */
    boolean addRealizer(ConcreteComponent realizer) {
        return m_realizers.add(realizer);
    }
    
    /**
     * @param realizer the realizing Component to add
     * @return true if the realizer wasn't present before
     */
    boolean addAllRealizer(Component realizer) {
        return m_allRealizers.add(realizer);
    }
    
    /**
     * removes the given list of realizers from the "allRealizers" list
     * @param realizers the realizing Components to remove
     */
    void removeFromAllRealizer(List<? extends Component> realizers) {
        m_allRealizers.removeAll(realizers);
    }
    
    /**
     * "pull" all Actions from realized (= "derived from") super-components
     * 
     * @param compSystem
     *            the component system this component resides in
     */
    void completeActions(CompSystem compSystem) {
        if (m_completionDone) {
            return;
        }
        if (m_completionStarted) {
            log.info("realization loop in component configuration for " //$NON-NLS-1$
                + getType() + " (may be regarded as an error?)"); //$NON-NLS-1$
        }
        resolveRealized(compSystem);
        m_completionStarted = true;
        Iterator<Component> realIt = m_realized.iterator();
        while (realIt.hasNext()) {
            Component realized = realIt.next();
            realized.completeActions(compSystem);
            Iterator<Action> actionIt = realized.getActions().iterator();
            while (actionIt.hasNext()) {
                addAction(actionIt.next());
            }
        }
        m_completionDone = true;
    }
    /**
     * @param compSystem
     *            the component system this component resides in
     */
    private void resolveRealized(CompSystem compSystem) {
        if (m_realized.isEmpty() && !m_realizedTypes.isEmpty()) {
            Iterator<String> typeIt = m_realizedTypes.iterator();
            while (typeIt.hasNext()) {
                String type = typeIt.next();
                Component comp = compSystem.findComponent(type);
                if (!(comp instanceof InvalidComponent)) {
                    addRealized(comp);
                }       
            }
            completeActions(compSystem);
        }
    }
    
    /**
     * Returns a string representation of the component object.
     * 
     * @return The string
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringBuilder(this).append("Type", getType()).append(//$NON-NLS-1$
            "Actions", getActions()).toString(); //$NON-NLS-1$
    }
    
    /**
     * @param obj
     *            The object to compare.
     * @return <code>true</code> if the passed object is an instance of this
     *         <code>Component</code> and the <code>type</code> s are equal.
     * 
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Component)) {
            return false;
        }
        Component rhs = (Component)obj;
        if (getType() != null) {
            return getType().equals(rhs.getType());
        }
        return false;
    }
    /**
     * @return The hash code build from <code>type</code>.
     */
    public int hashCode() {
        if (getType() != null) {
            return getType().hashCode();
        }
        return super.hashCode();
    }
    /**
     * @return the deprecated
     */
    public boolean isDeprecated() {
        return m_deprecated;
    }
    /**
     * @param deprecated the deprecated to set
     */
    public void setDeprecated(boolean deprecated) {
        m_deprecated = deprecated;
    }
    
    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        m_visible = visible;
    }
    
    /**
     * @return If <code>true</code>, this <code>Component</code>
     *         shall be visible and usable within the client gui.
     */
    public boolean isVisible() {
        return m_visible;
    }
    
    /**
     * @param observable the observable to set
     */
    public void setObservable(boolean observable) {
        m_observable = observable;
    }
    
    /**
     * @return If <code>true</code>, this <code>Component</code>
     *         is observable
     */
    public boolean isObservable() {
        return m_observable;
    }
    
    /**
     * Gets the valid staus of the Component.<br>
     * Normally, all Components should return true. Only the InvalidComponent
     * must return false.
     * @return true if this component is valid, dalse otherwise.
     */
    public boolean isValid() {
        return true;
    }
    
    /**
     * @return the description of the Toolkit of this Component
     */
    public ToolkitDescriptor getToolkitDesriptor() {
        return m_toolkitDesriptor;
    }
    
    /**
     * @param toolkitDesriptor the toolkitDesriptor to set
     */
    public void setToolkitDesriptor(ToolkitDescriptor toolkitDesriptor) {
        m_toolkitDesriptor = toolkitDesriptor;
    }
    
    /**
     * @return the directly extended "super"-component's type names
     */
    public List<String> getExtendedTypes() {
        return m_extendedTypes;
    }
    
    /**
     * @param extended the type name of an extended Component
     * to add
     */
    public void addExtendedType(String extended) {
        m_extendedTypes.add(extended);
    }
    
    /**
     * 
     * @return true if this Component extends another Component.
     */
    public boolean isExtender() {
        return !m_extendedTypes.isEmpty();
    }
    /**
     * @return the descriptionKey
     */
    public String getDescriptionKey() {
        return m_descriptionKey;
    }
    /**
     * @param descriptionKey the descriptionKey to set
     */
    public void setDescriptionKey(String descriptionKey) {
        m_descriptionKey = descriptionKey;
    }
    /**
     * @return the APIMostConcrete flag
     */
    public boolean isAPIMostConcrete() {
        return m_apiMostConcrete;
    }
    /**
     * @return the since
     */
    public String getSince() {
        return m_since;
    }
    /**
     * @param since the since to set
     */
    public void setSince(String since) {
        m_since = since;
    }
}