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
package org.eclipse.jubula.tools.internal.utils.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.tools.internal.exception.ConfigXmlException;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.InvalidAction;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;


/**
 * This class processes the contents of the Component system. It
 * provides methods used by the Tex generators.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 */
/**
 * @author BREDEX GmbH
 * @created Aug 2, 2007
 */
public class CompSystemProcessor implements IProcessor {
    /**
     * The component system.
     */
    private CompSystem m_compSystem;

    /**
     * @param config
     *            file location information for the toolkits
     * @throws ConfigXmlException
     *             If the configuration cannot be loaded
     */
    public CompSystemProcessor(ToolkitConfig config)
        throws ConfigXmlException {
        AbstractComponentBuilder builder = new AbstractComponentBuilder(config);
        //
        m_compSystem = builder.getCompSystem();
    }

    /**
     * For use when we already have a built compSystem (i.e. within RCP)
     * 
     * @param compSystem
     *            the compsystem
     */
    public CompSystemProcessor(CompSystem compSystem) {
        m_compSystem = compSystem;
    }

    /**
     * @return a list of Infos for all toolkits
     */
    public List<ToolkitInfo> getToolkitInfos() {
        List<ToolkitInfo> infos = new ArrayList<ToolkitInfo>();
        List descriptors = m_compSystem.getAllToolkitDescriptors();
        for (Iterator i = descriptors.iterator(); i.hasNext();) {
            ToolkitDescriptor descr = (ToolkitDescriptor)i.next();
            ToolkitInfo info = getToolkitInfo(descr);
            infos.add(info);
        }
        return infos;
    }

    /**
     * @param descr
     *            the toolkit plugin descriptor
     * @return the toolkitinfo corresponding to the plugin descriptor
     */
    public static ToolkitInfo getToolkitInfo(ToolkitDescriptor descr) {
        Info info = new ToolkitInfo(descr.getName(),
                descr.getToolkitID());
        return (ToolkitInfo)info;
    }

    /**
     * @param showOnlyVisible if true, then only visible components will be returned;
     *                        if false, then all components will be returned
     * @return A list of all components encapsulated in infos
     */
    public List<ComponentInfo> getCompInfos(boolean showOnlyVisible) {
        List<ComponentInfo> infos = new ArrayList<ComponentInfo>();

        for (Iterator it = m_compSystem.getComponents().iterator(); it
                .hasNext();) {
            Component component = (Component)it.next();
            if (showOnlyVisible && !component.isVisible()) {
                continue;
            }
            ToolkitInfo tkInfo = getToolkitInfo(component
                    .getToolkitDesriptor());
            infos.add(new ComponentInfo(component, tkInfo));
        }
        return infos;
    }

    /**
     * @param toolkitId
     *            the id of the toolkit for which components should be listed
     * @param toolkitName
     *            the display name of the toolkit for which
     *            components should be listed.
     * @return the list of components
     */
    public List<ComponentInfo> getCompInfos(String toolkitId, 
        String toolkitName) {
        List<ComponentInfo> infos = new ArrayList<ComponentInfo>();
        ToolkitInfo tkInfo = new ToolkitInfo(toolkitName, toolkitId);
        for (Iterator<Component> it = 
                m_compSystem.getComponents(toolkitId, false).iterator();
            it.hasNext();) {
            Component component = it.next();
            if ((component instanceof ConcreteComponent)
                    && !(((ConcreteComponent) component)).isSupported()) {
                continue;
            }
            infos.add(new ComponentInfo(component, tkInfo));
        }
        return infos;
    }

    /**
     * @param types
     *            The hierarchy of components (infos)
     * @param componentInfo
     *            The current component(info)
     * @param level
     *            The current inheritance hierarchy level
     */
    private void getHierarchyCompInfosImpl(List<ComponentInfo> types,
            ComponentInfo componentInfo, int level) {

        Component component = componentInfo.getComponent();
        int newLevel = level;
        if (component.isVisible()) {
            types.add(componentInfo);
            newLevel++;
        }

        for (Iterator it = component.getRealized().iterator(); it.hasNext();) {
            Component realized = (Component)it.next();
            ToolkitInfo tkRealized = getToolkitInfo(realized
                    .getToolkitDesriptor());
            ComponentInfo realizedInfo = new ComponentInfo(realized,
                    newLevel, tkRealized);
            getHierarchyCompInfosImpl(types, realizedInfo, newLevel);
        }
    }

    /**
     * @param componentInfo
     *            A component info
     * @return A list of all super components in the hierarchy of the passed
     *         component in <code>componentInfo</code>
     */
    public List<ComponentInfo> getHierarchyCompInfos(
        ComponentInfo componentInfo) {
        List<ComponentInfo> types = new ArrayList<ComponentInfo>();
        getHierarchyCompInfosImpl(types, componentInfo, 0);
        return types;
    }

    /**
     * @param componentInfo
     *            A component info
     * @param action
     *            An action
     * @return The component in the inheritance hierarchy that defines the
     *         passed action
     */
    public ComponentInfo getDefiningComp(ComponentInfo componentInfo,
            Action action) {

        ComponentInfo result = null;
        List<ComponentInfo> types = getHierarchyCompInfos(componentInfo);
        for (int i = 0; i < types.size(); i++) {
            try {
                ComponentInfo info = types.get(i);

                if (!(info.getComponent().findAction(action.getName())
                        instanceof InvalidAction)) {
                    result = info;
                }
            } catch (ConfigXmlException e) { // NOPMD by al on 3/19/07 2:09
                // PM
                // OK because we're searching multiple
                // comps for a single action
            }
        }
        return result;
        // AbstractComponent comp = new AbstractComponent();
        // comp.setType("");
        // return new ComponentInfo(comp);
    }

    /**
     * @param compInfo
     *            A ComponentInfo instance
     * @return a list of ComponentInfos: all components that use the actions
     *         from this component
     */
    public List<ComponentInfo> getUsingComps(ComponentInfo compInfo) {
        Component comp = compInfo.getComponent();
        Set realizerSet = comp.getRealizers();
        // getRealizers includes comp in it's result. We don't need that.
        realizerSet.remove(comp);

        List<ComponentInfo> realizerList = new ArrayList<ComponentInfo>();
        Iterator i = realizerSet.iterator();
        while (i.hasNext()) {
            Component compNext = (Component)i.next();
            if (compNext.isVisible()) {
                ToolkitInfo tkNext = getToolkitInfo(compNext
                        .getToolkitDesriptor());
                realizerList.add(new ComponentInfo(compNext, tkNext));
            }
        }
        return realizerList;
    }

    /**
     * @param componentInfo
     *            A component info
     * @param newActions
     *            If <code>true</code>, returns all actions which the passed
     *            component in <code>componentInfo</code> defines. If
     *            <code>false</code>, returns all actions that the component
     *            inherits from super components.
     * @return The list of actions
     */
    public List<ActionInfo> getActions(ComponentInfo componentInfo,
        boolean newActions) {
        return getActions(componentInfo, newActions, false);
    }

    /**
     * @param componentInfo
     *            the componentinfo
     * @param newActions
     *            whether to return new actions (<code>true</code>), or
     *            inherited actions (<code>false</code>)
     * @param deprecated
     *            whether to return deprecated actions or not
     * @return a list of actions as specified
     */
    public List<ActionInfo> getActions(ComponentInfo componentInfo,
        boolean newActions, boolean deprecated) {
        List<ActionInfo> actions = new ArrayList<ActionInfo>();

        for (Iterator it = componentInfo.getComponent().getActions()
                .iterator(); it.hasNext();) {
            Action action = (Action)it.next();
            if ((deprecated && action.isDeprecated())
                    || (!deprecated && !action.isDeprecated())) {
                ComponentInfo defining = getDefiningComp(componentInfo, action);
                boolean inherited = defining.getComponent() != componentInfo
                        .getComponent();
                if ((newActions && !inherited) || (!newActions && inherited)) {
                    actions.add(new ActionInfo(action, defining));
                }
            }
        }
        return actions;
    }

    /**
     * @return a list of all deprecated actions
     */
    public List<ActionInfo> getDeprecatedActions() {
        List<ActionInfo> deprecated = new ArrayList<ActionInfo>();

        for (Iterator i = m_compSystem.getComponents().iterator();
            i.hasNext();) {
            Component comp = (Component)i.next();
            ComponentInfo ci = new ComponentInfo(comp, getToolkitInfo(comp
                    .getToolkitDesriptor()));
            // we only want the non-inherited new actions (I think)
            List<ActionInfo> actions = getActions(ci, true, true);
            for (Iterator<ActionInfo> j = actions.iterator(); j.hasNext();) {
                ActionInfo ai = j.next();
                deprecated.add(ai);
            }
        }

        return deprecated;
    }

    /**
     * Returns the comp system
     * @return the comp system
     */
    public CompSystem getCompSystem() {
        return m_compSystem;
    }
}