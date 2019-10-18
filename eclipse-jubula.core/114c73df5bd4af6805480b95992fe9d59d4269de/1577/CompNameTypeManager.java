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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.Set;

import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;

/**
 * Class containing all type-related methods for Component Names
 * @author BREDEX GmbH
 */
public class CompNameTypeManager {
    
    /** Private constructor */
    private CompNameTypeManager() {
        // empty constructor
    }
    
    /**
     * Calculates the type of two types
     * @param type1 the first type
     * @param type2 the second type
     * @return the determined type or null if the types are not compatible
     */
    public static String calcUsageType(String type1, String type2) {
        if (type1 == null || type2 == null) {
            return ComponentNamesBP.UNKNOWN_COMPONENT_TYPE;
        }
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        Component comp1 = getMostConcreteVisibleAncestor(
                compSystem.findComponent(type1), compSystem);
        Component comp2 = getMostConcreteVisibleAncestor(
                compSystem.findComponent(type2), compSystem);
        
        if (comp1 == null || comp2 == null) {
            return ComponentNamesBP.UNKNOWN_COMPONENT_TYPE;
        }
        
        Component moreConcrete = getMoreConcreteComponent(comp1, comp2, true);
        if (moreConcrete == null) {
            return ComponentNamesBP.UNKNOWN_COMPONENT_TYPE;
        }
        
        String compType = moreConcrete.getType();
        
        moreConcrete = getMostConcreteVisibleAncestor(
                moreConcrete, compSystem);

        // Use the most concrete visible type, if one is available
        if (moreConcrete != null) {
            compType = moreConcrete.getType();
        }
        if (compType != null) {
            return compType;
        }
        return ComponentNamesBP.UNKNOWN_COMPONENT_TYPE;
    }
    
    /**
     * Returns the most abstract Component Type
     * @return the most abstract Component Type
     */
    public static String getMostAbstractType() {
        return ComponentBuilder.getInstance().getCompSystem()
            .getMostAbstractComponent().getType();
    }
    
    /**
     * Determines if the first type realize the second
     * @param first the first type
     * @param second the second type
     * @return whether the first type realizes the second
     */
    public static boolean doesFirstTypeRealizeSecond(
            String first, String second) {
        if (first == null || second == null) {
            return false;
        }
        if (first.equals(second)) {
            return true;
        }
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        Component comp1 = compSystem.getComponentForType(first);
        if (comp1 == null) {
            return false;
        }
        return comp1.isRealizing(second);
    }
    
    /**
     * @param comp1 a Component
     * @param comp2 a Component
     * @param isFirstCall caller should set this to true always!
     * @return the more concrete Component or null if the given Components are 
     * incompatible.
     */
    private static Component getMoreConcreteComponent(Component comp1, 
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
        return isFirstCall ? getMoreConcreteComponent(comp2, comp1, false) 
                : null;
    }
    
    /**
     * Find the most concrete visible ancestor of the given component.
     * 
     * @param component The component for which to find the most concrete
     *                  visible ancestor.
     * @param compSystem The component system to use to perform the search.
     * @return the most concrete, visible ancestor of <code>component</code>, 
     *         which may be <code>component</code> itself. 
     *         Returns <code>null</code> if <code>component</code> and
     *         its ancestors are all invisible.
     */
    private static Component getMostConcreteVisibleAncestor(Component component,
            CompSystem compSystem) {

        Component comp = component;
        while (comp != null 
                && !comp.isVisible()) {
            Set<Component> realized = comp.getAllRealized();
            comp = compSystem.getMostConcrete(
                    realized.toArray(new Component [realized.size()]));
        }
        return comp;
    }
    
    /**
     * Returns the most concrete visible ancestor type
     * @param type the type
     * @return the ancestor type
     */
    public static String getMostConcreteVisibleAncestorType(String type) {
        if (type == null) {
            return ComponentNamesBP.UNKNOWN_COMPONENT_TYPE;
        }
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem(); 
        Component comp = compSystem.findComponent(type);
        if (comp == null) {
            return ComponentNamesBP.UNKNOWN_COMPONENT_TYPE;
        }
        Component ancestor = getMostConcreteVisibleAncestor(comp, compSystem);
        return ancestor == null ? ComponentNamesBP.UNKNOWN_COMPONENT_TYPE
                : ancestor.getType();
    }
    
    /**
     * Decides whether a component name may have a new usage
     * If it says NO, then it definitely can't, but otherwise it is not sure it can
     * @param cN the Component Name
     * @param type the type
     * @return NO, if type is independent of the usage type of the component name
     *      or if the usage type and the real type of the component name differ, and
     *      the real type does not realize the given type
     */
    public static boolean mayBeCompatible(IComponentNamePO cN,
            String type) {
        if (cN.getComponentType() == null) {
            return false;
        }
        return (doesFirstTypeRealizeSecond(type, cN.getUsageType())
            || doesFirstTypeRealizeSecond(cN.getUsageType(), type))
            && (cN.getComponentType().equals(cN.getUsageType())
                || doesFirstTypeRealizeSecond(cN.getComponentType(), type));
    }
}