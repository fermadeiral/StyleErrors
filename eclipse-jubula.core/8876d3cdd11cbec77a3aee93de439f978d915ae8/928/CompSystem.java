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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.ConfigXmlException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.xml.businessprocess.ConfigVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a container for all components which can be tested by
 * Jubula.
 * @author BREDEX GmbH
 * @created 18.07.2005
 */
public class CompSystem {
    /** The logger */
    private static Logger log = LoggerFactory.getLogger(CompSystem.class); 
    
    /** The list of all components. */
    private List<Component> m_components;
    
    /** fast lookup */
    private Map<String, Component> m_componentsByType;
    
    /** fast lookup */
    private Map<String, Component> m_componentsByTypeLowerCase;
    
    /** The list of abstract components. */
    private List<AbstractComponent> m_abstractComponents;
    
    /** The list of concrete components. */
    private List<ConcreteComponent> m_concreteComponents;
    
    /** The list of concrete components. */
    private List<ConcreteComponent> m_simpleExtensionComponents;
    
    /** The List of Event Types. */
    private Map<String, Integer> m_eventTypes;
    
    /** The most abstract component in the hierarchy */
    private Component m_mostAbstractComponent;
    
    /**
     * Map of {@link ToolkitDescriptor}
     * Key: id of toolkit, value: ToolkitDescriptor
     */
    private Map<String, ToolkitDescriptor> m_toolkitDescriptors;
    
    /** Stores whether the component is initialized. */
    private boolean m_initialized = false;
    
    /** <code>m_configVersion</code> version for clientConfig.xml */
    private ConfigVersion m_configVersion = null;

    /** A List of all DataTypes */
    private Set<String> m_dataTypes = null;

    /** Default constructor */
    public CompSystem() {
        init();
    }
    
    /**  */
    private void init() {
        if (m_components == null) {
            m_components = new ArrayList<Component>();
        }
        if (m_componentsByType == null) {
            m_componentsByType = new HashMap<String, Component>(1001);
        }
        if (m_componentsByTypeLowerCase == null) {
            m_componentsByTypeLowerCase = new HashMap<String, Component>(1001);
        }
        if (m_abstractComponents == null) {
            m_abstractComponents = new ArrayList<AbstractComponent>();
        }
        if (m_concreteComponents == null) {
            m_concreteComponents = new ArrayList<ConcreteComponent>();
        }
        if (m_simpleExtensionComponents == null) {
            m_simpleExtensionComponents = new ArrayList<ConcreteComponent>();
        }
        if (m_eventTypes == null) {
            m_eventTypes = new HashMap<String, Integer>(4);
        }
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = 
                new HashMap<String, ToolkitDescriptor>();
        }
        // FIXME Achim only hard coded EventTypes so far
        m_eventTypes.put(TestErrorEvent.ID.ACTION_ERROR, 
            new Integer(7));
        m_eventTypes.put(TestErrorEvent.ID.COMPONENT_NOT_FOUND, 
            new Integer(7));
        m_eventTypes.put(TestErrorEvent.ID.CONFIGURATION_ERROR, new Integer(1));
        m_eventTypes.put(TestErrorEvent.ID.VERIFY_FAILED, new Integer(1));
        m_initialized = true;
    }

    /**
     * Gets the list with all components of all installed Toolkit-Plugins.
     * @return List A <code>List</code> object.
     */
    public List<Component> getComponents() {
        return m_components;
    }

    /**
     * @param toolkitId the unique toolkit id <br>
     * This information is available at the Project!
     * @param addReferencedToolkits <code>true</code> if components from toolkits 
     *                            that are "include"ed or "depend"ed on by
     *                            the given toolkitId should
     *                            be included in the list
     * @return a List with toolkit specific components of the given toolkit
     * (and its included Toolkit if <code>addIncludedToolkits</code> is 
     * <code>true</code>).
     */
    public List<Component> getComponents(String toolkitId, 
        boolean addReferencedToolkits) {
        final List<Component> toolkitComponents = new ArrayList<Component>();
        toolkitComponents.addAll(m_simpleExtensionComponents);
        final ToolkitDescriptor currDescriptor = 
            getToolkitDescriptor(toolkitId);
        String includesToolkit = currDescriptor.getIncludes();
        final ToolkitDescriptor includesDescriptor = 
            getToolkitDescriptor(includesToolkit);
        if (includesDescriptor != null) {
            final String includesLevel = includesDescriptor.getLevel();
            if (!ToolkitConstants.LEVEL_TOOLKIT.equals(includesLevel)) {
                includesToolkit = ToolkitConstants.NO_VALID_INCLUDE_TOOLKIT;
            }
        } else {
            includesToolkit = ToolkitConstants.NO_VALID_INCLUDE_TOOLKIT;
        }
        final List<String> dependsToolkits = getDependsToolkitIds(toolkitId);
        for (Component component : getComponents()) {
            final String compToolkitId = component.getToolkitDesriptor()
                .getToolkitID();
            if (toolkitId.equals(compToolkitId)
                || (includesToolkit.equals(compToolkitId)
                    && addReferencedToolkits)
                || (dependsToolkits.contains(compToolkitId) 
                    && addReferencedToolkits)) {
                
                toolkitComponents.add(component);
            }
        }
        return toolkitComponents;
    }

    /**
     * @param componentNames the names of the components to support
     * @param toolkitDescriptor the descriptor of the belonging toolkit
     */
    public void addSimpleExtensions(List<String> componentNames,
            ToolkitDescriptor toolkitDescriptor) {
        Component graphicsComponent = null;
        for (Component comp : getComponents()) {
            if (comp.getType().equalsIgnoreCase("guidancer.abstract.Widget")) { //$NON-NLS-1$
                graphicsComponent = comp;
            }
        }
        cleanPreviousSimpleExtension(graphicsComponent);
        for (String componentName : componentNames) {
            String cleanName = componentName.trim();
            if (m_componentsByType.containsKey(cleanName)) {
                // if the Component is already supported, we do not add it to the list
                continue;
            }
            ConcreteComponent myComponent = new ConcreteComponent();
            myComponent.setType(cleanName); 
            myComponent.setTesterClass(
                    "org.eclipse.jubula.rc.common.tester.WidgetTester"); //$NON-NLS-1$
            myComponent.setComponentClass(new ComponentClass(cleanName));
            myComponent.addRealizedType("guidancer.abstract.Widget"); //$NON-NLS-1$
            myComponent.setToolkitDesriptor(toolkitDescriptor);
            myComponent.addAllRealizer(myComponent);
            myComponent.getRealizers().add(myComponent);
            graphicsComponent.addAllRealizer(myComponent);
            myComponent.addRealized(graphicsComponent);
            m_componentsByType.put(myComponent.getType(), myComponent);
            m_componentsByTypeLowerCase.put(myComponent.getType().toLowerCase(),
                    myComponent);
            m_simpleExtensionComponents.add(myComponent);
        }
        getConcreteComponents().addAll(m_simpleExtensionComponents);
        getComponents().addAll(m_simpleExtensionComponents);
    }

    /**
     * @param graphicsComponent the "guidancer.abstract.Widget" component
     */
    private void cleanPreviousSimpleExtension(Component graphicsComponent) {
        getConcreteComponents().removeAll(m_simpleExtensionComponents);
        getComponents().removeAll(m_simpleExtensionComponents);
        for (Component c : m_simpleExtensionComponents) {
            m_componentsByType.remove(c.getType());
        }
        graphicsComponent.removeFromAllRealizer(m_simpleExtensionComponents);
        m_simpleExtensionComponents.clear();
    }
    
    
    /**
     * @return Returns the abstractComponents.
     */
    public List<AbstractComponent> getAbstractComponents() {
        return m_abstractComponents;
    }
    
    /**
     * @return Returns the concreteComponents.
     */
    public List<ConcreteComponent> getConcreteComponents() {
        return m_concreteComponents;
    }
    
    /**
     * Gets a List of all includes toolkits (the whole hierarchy) of the given
     * toolkit and the given toolkit itself.
     * 
     * @param toolkitId
     *            the id of a toolkit whose include hierarchy is wanted.
     * @param toolkits
     *            an empty List.
     * @return the given toolkits List
     */
    private List<String> getIncludesToolkits(String toolkitId,
        List<String> toolkits) {
        toolkits.add(toolkitId);
        final ToolkitDescriptor toolkitPluginDescriptor = 
            getToolkitDescriptor(toolkitId);
        if (toolkitPluginDescriptor != null) {
            final String includes = toolkitPluginDescriptor.getIncludes();
            if (!ToolkitConstants.EMPTY_EXTPOINT_ENTRY.equals(includes)) {
                getIncludesToolkits(includes, toolkits);
            }
        }
        return toolkits;
    }
    
    /**
     * @param toolkitId
     *            a Toolkit id
     * @return a List of toolkit names which depends on the given toolkit id.
     */
    private List<String> getDependsToolkitIds(String toolkitId) {
        final List<String> dependsToolkits = new ArrayList<String>();
        for (String tkId : m_toolkitDescriptors.keySet()) {
            final ToolkitDescriptor tkDescr = m_toolkitDescriptors
                .get(tkId);
            if (toolkitId.equals(tkDescr.getDepends())) {
                dependsToolkits.add(tkId);
            }
        }
        return dependsToolkits;
    }
    
    
    /**
     * Gets all Component-Types of the given toolkit, its "Includes-Toolkits"
     * and "Depends-Toolkits".
     * @param toolkitId a toolkit id
     * @return A List of Component-Types
     */
    public String[] getComponentTypes(String toolkitId) {
        final List<String> compTypes = new ArrayList<String>();
        List<String> toolkits = new ArrayList<String>();
        toolkits = getIncludesToolkits(toolkitId, toolkits);
        toolkits.addAll(getDependsToolkitIds(toolkitId));
        for (Component comp : getComponents()) {

            final String compToolkitId = comp.getToolkitDesriptor()
                .getToolkitID();
            if (!comp.isExtender() && toolkits.contains(compToolkitId)) {
                compTypes.add(comp.getType());
            }
        }
        return compTypes.toArray(new String[compTypes.size()]);
    }
    

    
   /**
    * 
    * @return a <code>String</code> Array of Event Types.
    */
    public Map<String, Integer> getEventTypes() {
        return m_eventTypes;
    }
    

    /**
     * Checks if there are multiple components with the same type.
     * 
     * @param component
     *            A
     *            <code>org.eclipse.jubula.tools.internal.xml.businessmodell.Component</code>
     *            object
     * 
     */
    private void check(Component component) {
        for (Component current : getComponents()) {
            if (current.getType().equals(component.getType())) {
                final String msg = "multiple definition of component type " //$NON-NLS-1$
                        + component.getType();
                log.error(msg);
                String descriptor = current.getToolkitDesriptor().getToolkitID()
                        + StringConstants.COMMA + StringConstants.SPACE 
                        + component.getToolkitDesriptor().getToolkitID();
                throw new ConfigXmlException(descriptor, msg, 
                    MessageIDs.E_MULTIPLE_COMPONENT);
            }
        }
    }
    
    /**
     * Adds all passed components to <code>m_components</code>. The method
     * <code>check()</code> is called for each component in the list.
     * 
     * @param components
     *            The components to add
     */
    private void addAll(List components) {
        for (Iterator it = components.iterator(); it.hasNext();) {
            Component component = (Component)it.next();
            check(component);
            getComponents().add(component);
            m_componentsByType.put(component.getType(), component);
            m_componentsByTypeLowerCase.put(component.getType().toLowerCase(),
                    component);
        }
    }
    /**
     * Adds an Event Type to the List.
     * @param eventType a <code>String</code> object which describes the Event Type.
     * @param reentryProp The re-entry property (<code>Integer</code> object).
     * 
     */
    public void addEventType(String eventType, Integer reentryProp) {
        m_eventTypes.put(eventType, reentryProp);
    }
    
    /**
     * 
     * @param toolkitId the id of the toolkit
     * @param descriptor the {@link ToolkitDescriptor}
     */
    public void addToolkitPluginDescriptor(String toolkitId, 
        ToolkitDescriptor descriptor) {
        
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = 
                new HashMap<String, ToolkitDescriptor>();
        }
        m_toolkitDescriptors.put(toolkitId, descriptor);
    }
    
    /**
     * 
     * @param toolkitId
     *            the id of the toolkit
     * @return the {@link ToolkitDescriptor} for the given toolkit ID, or
     *         <code>null</code> if the given toolkit ID does not have a
     *         corresponding active plugin.
     */
    public ToolkitDescriptor getToolkitDescriptor(
        String toolkitId) {
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = 
                new HashMap<String, ToolkitDescriptor>();
        }
        return m_toolkitDescriptors.get(toolkitId);
    }
    
    /**
     * @return the {@link ToolkitDescriptor}s of all toolkits
     */
    public List<ToolkitDescriptor> getAllToolkitDescriptors() {
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = 
                new HashMap<String, ToolkitDescriptor>();
        }
        return new ArrayList<ToolkitDescriptor>(
            m_toolkitDescriptors.values());
    }
   
    /**
     * @param level Only Toolkits with this level will be returned. May be
     *              <code>null</code>, in which case independent toolkits
     *              for all levels will be returned.
     * @return the {@link ToolkitDescriptor}s of all independent 
     *         toolkits with the given level.
     */
    public List<ToolkitDescriptor> getIndependentToolkitDescriptors(
        String level) {
        final String emptyStr = StringConstants.EMPTY;
        
        List<ToolkitDescriptor> toolkitDesriptors = 
            getAllToolkitDescriptors();
        
        Collections.sort(toolkitDesriptors);

        Iterator<ToolkitDescriptor> descIt = toolkitDesriptors.iterator();

        // Remove all non-independent and invalid toolkits
        while (descIt.hasNext()) {
            ToolkitDescriptor desc = descIt.next();

            final String includes = desc.getIncludes();
            String toolkitID = desc.getToolkitID();

            boolean removeDueToToolkitLevel = level != null
                && !level.equals(desc.getLevel());
            if (removeDueToToolkitLevel
                || (!ToolkitConstants.LEVEL_ABSTRACT.equals(level) && (emptyStr
                    .equals(includes)
                    || ToolkitConstants.EMPTY_EXTPOINT_ENTRY.equals(includes
                        .toLowerCase()) || emptyStr.equals(toolkitID) 
                            || ToolkitConstants.EMPTY_EXTPOINT_ENTRY
                                .equals(toolkitID.toLowerCase())))) {

                descIt.remove();
            }

        }
        
        return toolkitDesriptors;
    }
    
    /**
     * @return A List of all DataTypes of the Actions.
     */
    public Set<String> getAllDataTypes() {
        if (m_dataTypes != null && !m_dataTypes.isEmpty()) {
            return m_dataTypes;
        }
        m_dataTypes = new HashSet<String>();
        final List<Component> components = getComponents();
        for (Component component : components) {
            for (Action action : component.getActions()) {
                for (Param param : action.getParams()) {
                    m_dataTypes.add(param.getType());
                }
            }
        }
        return m_dataTypes;
    }
    
    /**
     * Returns the component with the specified typeName.
     * 
     * @param typeName
     *            Name of the specified component (the I18N key).
     * @return the specified Component.
     */
    public Component findComponent(String typeName) {
        Validate.notNull(typeName);
        
        if (StringConstants.EMPTY.equals(typeName)) {
            if (log.isDebugEnabled()) {
                log.debug("CompSystem.findComponent(...) called with empty String. Returning InvalidComponent."); //$NON-NLS-1$
            }
            return new InvalidComponent();
        }
        
        Component comp = m_componentsByType.get(typeName);
        if (comp != null) {
            return comp;
        }
        
        if (log.isDebugEnabled()) {
            String translatedName = CompSystemI18n.getString(typeName, true);
            String message = "Component " + translatedName + " does not exist"; //$NON-NLS-1$ //$NON-NLS-2$
            log.debug(message);
        }
        
        return new InvalidComponent();
    }
    
    /**
     * Returns the components with the specified typeName.
     * 
     * @param typeName
     *            Name of the specified component (the I18N key).
     * @return the specified Components.
     */
    public List<Component> findComponents(String typeName) {
        Validate.notNull(typeName);
        List<Component> comps = new LinkedList<Component>();
        if (StringConstants.EMPTY.equals(typeName)) {
            if (log.isDebugEnabled()) {
                log.debug("CompSystem.findComponent(...) called with empty String. Returning InvalidComponent."); //$NON-NLS-1$
            }
            comps.add(new InvalidComponent());
            return comps;
        }
        
        for (Component comp : getComponents()) {
            if (comp instanceof ConcreteComponent) {
                ConcreteComponent ccomp = (ConcreteComponent)comp;
                if (typeName.equals(ccomp.getComponentClass().getName())) {
                    comps.add(ccomp);
                }
            }            
        }
        if (!(comps.isEmpty())) {
            return comps;
        }
        
        if (log.isDebugEnabled()) {
            String translatedName = CompSystemI18n.getString(typeName, true);
            String message = "Component " + translatedName + " does not exist"; //$NON-NLS-1$ //$NON-NLS-2$
            log.debug(message);
        }
        comps.add(new InvalidComponent());
        return comps;
    }

    /**
     * Returns a string representation of the component system object.
     * 
     * @return String
     */
    public String toString() {
        return new ToStringBuilder(this).append(
                "Abstract comps", m_abstractComponents) //$NON-NLS-1$
                .append("Concrete comps", m_concreteComponents) //$NON-NLS-1$
                .toString();
    }
    
    
    /**
     * Adds all Components of the given CompSystem to this CompSystem
     * @param compSystem the CompSystem to merge
     */
    public void merge(CompSystem compSystem) {
        if (!m_initialized) {
            init();
        }
        if (compSystem.m_abstractComponents != null) {
            m_abstractComponents.addAll(compSystem.m_abstractComponents);
        }
        if (compSystem.m_concreteComponents != null) {
            m_concreteComponents.addAll(compSystem.m_concreteComponents);
        }
        if (compSystem.m_toolkitDescriptors != null) {
            m_toolkitDescriptors.putAll(compSystem.m_toolkitDescriptors);
        }
    }
    
    /**
     * make all Actions available at all realizing (derived) Components; build
     * the lists of components (realizing concrete Components)
     * 
     */
    public void postProcess() {
        if (!m_initialized) {
            init();
        }
        addAll(m_concreteComponents);
        addAll(m_abstractComponents);
        
        for (Component component : getComponents()) {
            component.completeActions(this);
            handleRealizer(component);
            handleExtender(component);
            handleDepender(component);
        }
        validateComponents();
        for (Component component : getAbstractComponents()) {
            if (component.getRealized().isEmpty()) {
                m_mostAbstractComponent = component;
                break;
            }
        }
    }
    
    /**
     * Handles independent Components (no realizer, no extender) which are added
     * via user plugin to an existing plugin.
     * Those Components are added to the depending toolkit.
     * @param component A Component.
     */
    private void handleDepender(Component component) {
        final ToolkitDescriptor toolkitDesriptor = component
            .getToolkitDesriptor();
        final String depends = toolkitDesriptor.getDepends();
        if (!component.isExtender()
            && !component.isRealizer()
            && !ToolkitConstants.EMPTY_EXTPOINT_ENTRY.equals(depends)
            && toolkitDesriptor.isUserToolkit()) {
            
            final ToolkitDescriptor dependsDescr = 
                m_toolkitDescriptors.get(depends);
            if (dependsDescr != null) {
                component.setToolkitDesriptor(dependsDescr);
            }
            
        }
    }
    /**
     * Validates the Components.
     */
    private void validateComponents() {
        for (AbstractComponent ac : m_abstractComponents) {
            if (ac.getRealizers().isEmpty()) {
                String message = "AbstractComponent " + ac.getType() //$NON-NLS-1$
                    + " has no realizing concreteComponents"; //$NON-NLS-1$
                if (ac.isVisible()) {
                    log.error("visible " + message); //$NON-NLS-1$
                    throw new ConfigXmlException(ac.getToolkitDesriptor()
                            .getToolkitID(), 
                            "visible " + message, //$NON-NLS-1$
                        MessageIDs.E_NO_ABSTRACT_COMPONENT);
                }
                log.warn(message);
            }
        }
        for (ConcreteComponent cc : m_concreteComponents) {
            if (cc.isExtender() && !StringUtils.isBlank(
                cc.getComponentClass().getName())) {
                // extender must not have a componentClass!
                final String msg = "Extending ConcreteComponent '" //$NON-NLS-1$
                    + cc.getType() + "' must not have a componentClass!"; //$NON-NLS-1$
                log.error(msg);
                throw new ConfigXmlException(cc.getToolkitDesriptor()
                        .getToolkitID(), msg,
                    MessageIDs.E_GENERAL_COMPONENT_ERROR);
            }
        }
    }
    
    
    /**
     * Resolves the "realized" relation.
     * @param component a Component.
     */
    private void handleRealizer(Component component) {
        final boolean isConcrete = component.isConcrete();
        Set<Component> realizedSet = component.getAllRealized();
        for (Component realized : realizedSet) {
            if (isConcrete) {
                realized.addRealizer((ConcreteComponent) component);
            }
            realized.addAllRealizer(component);
        }
        if (isConcrete) {
            component.addRealizer((ConcreteComponent) component);
        }
        component.addAllRealizer(component);
    }
    
    /**
     * Handles a Component which extends another Component and adds its Actions
     * and its tester class to the extended Component.
     * @param component the extending Component.
     */
    private void handleExtender(Component component) {
        if (component.isExtender()) {
            final List<Action> extenderActions = component.getActions();
            final List<String> extendedTypes = component.getExtendedTypes();
            final boolean isExtenderVisible = component.isVisible();
            for (String extendedType : extendedTypes) {
                final Component extendedComponent = findComponent(extendedType);
                if (!extendedComponent.isVisible()) {
                    extendedComponent.setVisible(isExtenderVisible);
                }
                for (Action extenderAction : extenderActions) {
                    extendedComponent.addAction(extenderAction);
                    if (component instanceof ConcreteComponent
                        && extendedComponent instanceof ConcreteComponent) {

                        final ConcreteComponent extender = 
                            (ConcreteComponent) component;
                        final ConcreteComponent extended = 
                            (ConcreteComponent) extendedComponent;
                        
                        extended.setTesterClass(extender.getTesterClass());
                    }
                }
            }
        }
    }
    
    /**
     * @return Returns the configVersion.
     */
    public ConfigVersion getConfigVersion() {
        return m_configVersion;
    }
    
    /**
     * @param type a Component Type.
     * @return The Component of the given Component Type or null if no 
     * Component was found.
     */
    public Component getComponentForType(String type) {
        return m_componentsByTypeLowerCase.get(type.toLowerCase());
    }
    
    /**
     * @return The most abstract component in the hierarchy.
     */
    public final Component getMostAbstractComponent() {
        return m_mostAbstractComponent;
    }
    
    /**
     * 
     * @param type1 a Component Type.
     * @param type2 a Component Type.
     * @return the more concrete Component Type or null if the given 
     * Component Types are incompatible.
     */
    public final String getMoreConcreteType(String type1, String type2) {
        final Component comp1 = getComponentForType(type1);
        final Component comp2 = getComponentForType(type2);
        final Component moreConcreteComp = getMoreConcreteComponent(comp1, 
                comp2);
        
        return moreConcreteComp != null ? moreConcreteComp.getType() : null;
    }
    
    /**
     * 
     * @param components The Components to check. May be empty but may not be 
     *                   <code>null</code>.
     * @return the most concrete of the given components, or <code>null</code>
     *         if any of the given components are incompatible or if 
     *         <code>components</code> is empty.
     */
    public final Component getMostConcrete(Component [] components) {
        Component mostConcrete = null;
        if (components.length > 0) {
            mostConcrete = components[0];
        }
        for (int i = 1; i < components.length && mostConcrete != null; i++) {
            mostConcrete = 
                getMoreConcreteComponent(mostConcrete, components[i]);
        }
        
        return mostConcrete;
    }
    
    /**
     * 
     * @param comp1 a Component
     * @param comp2 a Component
     * @return the more concrete Component or null if the given Components are 
     * incompatible.
     */
    private Component getMoreConcreteComponent(Component comp1, 
            Component comp2) {
        
        return getMoreConcreteComponentImpl(comp1, comp2, true);
    }
    
    
    /**
     * 
     * @param comp1 a Component
     * @param comp2 a Component
     * @param isFirstCall caller should set this to true always!
     * @return the more concrete Component or null if the given Components are 
     * incompatible.
     */
    private Component getMoreConcreteComponentImpl(Component comp1, 
            Component comp2, boolean isFirstCall) {
        
        if (comp1 == null || comp2 == null) {
            return null;
        }
        if (comp1.equals(comp2)) {
            return comp1;
        }
        final String comp2Type = comp2.getType();
        for (Component realizer : comp1.getAllRealizers()) {
            if (realizer.getType().equals(comp2Type)) {
                return realizer;
            }
        }
        // if comp2 is not more concrete than comp1, try inverted search:
        return isFirstCall ? getMoreConcreteComponentImpl(comp2, comp1, false) 
                : null;
    }
    
    /**
     * Checks if the given realizingType is realizing the give realizedType.
     * @param realizingType the realizingType to check.
     * @param realizedType the realizedType.
     * @return true if the realizingType is realizing the realizedType, false 
     * otherwise.
     */
    public final boolean isRealizing(String realizingType, 
            String realizedType) {
        if (realizingType.equals(realizedType)) {
            return true;
        }
        final Component realizer = findComponent(realizingType);
        return realizer.isRealizing(realizedType);
    }
    
    /**
     * @param toolkitLevel
     *            true if only toolkits with level "toolkit" are wanted, false
     *            if all toolkits are wanted.
     * @return a List of I18N names of the toolkits or a List of toolkit IDs.
     */
    public List<ToolkitDescriptor> getIndependentToolkitDescriptors(
        boolean toolkitLevel) {
        String level = toolkitLevel ? ToolkitConstants.LEVEL_TOOLKIT : null;
        return getIndependentToolkitDescriptors(level);
    }

    /**
     * 
     * @param componentClassName The FQN (fully qualified name) of the 
     *                           supported class for the Component. 
     * @param availableComponents Components through which to search.
     * @return The Component from the provided collection that supports the
     *         given class name, or <code>null</code> if no such Component
     *         is found.
     */
    public static String getComponentType(String componentClassName,
        Collection<Component> availableComponents) {

        Validate.notNull(componentClassName);
        Validate.allElementsOfType(availableComponents, Component.class);

        for (Component currentComp : availableComponents) {
            if (currentComp instanceof ConcreteComponent
                && componentClassName.equals(((ConcreteComponent) currentComp)
                    .getComponentClass().getName())) {

                return currentComp.getType();
            }
        }

        return null;
    }
}
