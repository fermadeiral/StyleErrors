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
package org.eclipse.jubula.rc.swt.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.components.AUTComponent;
import org.eclipse.jubula.rc.common.components.AUTHierarchy;
import org.eclipse.jubula.rc.common.components.HierarchyContainer;
import org.eclipse.jubula.rc.common.exception.ComponentNotManagedException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.listener.ComponentHandler;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;



/**
 * This class holds a hierarchy of the components of the AUT. <br>
 * The hierarchy is composed with <code>HierarchyContainer</code>s. For every
 * component from the AUT a hierarchy container is created. The names for the
 * components are stored in the appropriate hierarchy containers, instead of the
 * components itself. Thus the AUTServer does not affect the instances from the AUT. <br>
 * In SWT the ShellClosed event is not delivered properly, so a shell
 * listener is added to any opened window listening to
 * <code>ShellEvent.ShellCLosed</code>.<br><p>
 * <b>Interferences with the AUT</b><ul>
 * <li>The SwtAUTHierarchy is registered as a dispose listener to every
 * widget from the AUT (but not to the hierarchy container).</li>
 * <li>The SwtAUTHierarchy is registered as a window listener to every shell
 * from the AUT.</li></ul>
 * @author BREDEX GmbH
 * @created 30.08.2004
 */
public class SwtAUTHierarchy extends AUTHierarchy<Widget> {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        SwtAUTHierarchy.class);

    /**
     * Businessprocess for getting components
     */
    private static FindSWTComponentBP findBP = new FindSWTComponentBP();
    
    /** Mapping from Shells to their corresponding listeners */
    private Map<Shell, ShellClosingListener> m_shellToListenerMap = 
        new HashMap<Shell, ShellClosingListener>();
    
    /** The current active window */
    private Shell m_activeWindow;
    
    // methods operating on meta data, parameters are from the AUT
    /**
     * Adds the complete hierarchy of the given <code>window</code> to the
     * hierarchy. <br>
     * @param window a new (and opened) Shell
     */
    public void add(Shell window) {
        if (window == null || window.isDisposed()) {
            return;
        }
        // if window has no parent, its a new top level container, otherwise
        // the parent is already in the AutHierarchy, 
        // NO!: creating a Window without a parent, calling show()
        // -> window.getParent() == SwingUtilities$1
        if (log.isInfoEnabled()) {
            log.info("adding window " + window); //$NON-NLS-1$            
        }
        // don't add, if in hierarchy map yet
        if (getRealMap().get(window) != null) {
            return;
        }
        if (getHierarchyContainer(window) != null) {
            return;
        }
        // create a new HierarchyContainer for window 
        HierarchyContainer<Widget> hierarchyWindow = new SwtHierarchyContainer(
                new SwtComponent(window));
        // update the hash table
        addToHierachyMap(hierarchyWindow);
        // add a window listener for window closed events
        registerAsWindowListener(window);
        // get the parent of window, if any
        Composite parent = window.getParent();
        if (parent != null) {
            HierarchyContainer<Widget> hierarchyParent = 
                getHierarchyContainer(parent);
            if (hierarchyParent == null) {
                // a new container, see comment at top of the method
                hierarchyParent = new SwtHierarchyContainer(
                    new SwtComponent(parent));
                name(hierarchyParent);
            }
            // add the new container for the window to hierarchyParent
            hierarchyParent.add(hierarchyWindow);
            name(hierarchyWindow);
            // update m_hierarchyMap
            addToHierachyMap(hierarchyParent);
            addToHierarchyUp(hierarchyParent, parent);
        }
        // registering this class as a container listener happens in
        // addToHierarchy
        addToHierarchyDown(hierarchyWindow, window);
        
    }
        
    /**
     * Removes the given window from the hierarchy.
     * 
     * @param window the window to remove.
     */
    private void remove(Shell window) {
        // remove the shell closing listener, if it is still registered
        // remove window from the map
        // remove the container from hierarchyMap
        if (log.isInfoEnabled()) {
            log.info("deregistering window listener from window " + window); //$NON-NLS-1$
        }
        if (!window.isDisposed() && m_shellToListenerMap.containsKey(window)) {
            window.removeShellListener(
                m_shellToListenerMap.get(window));
        }
        m_shellToListenerMap.remove(window);

        // Only remove the window if it is currently listed in the hierarchy
        // (i.e. don't try to remove it twice)
        if (getRealMap().get(window) != null) {
            HierarchyContainer<Widget> windowContainer = 
                getHierarchyMap()
                .get(getRealMap().get(window));
            
            if (windowContainer != null) {
                // remove the windowContainer from its parent in the hierarchy, if
                // any
                HierarchyContainer<Widget> parentContainer = 
                    windowContainer.getPrnt();
                if (parentContainer != null) {
                    parentContainer.remove(windowContainer);
                }
                
                // Remove recursively all hierarchy container from the maps and
                // remove all listener from the container of the AUT. If the window
                // is displayed again, the complete hierarchy is rebuild.
                removeFromHierarchy(windowContainer);            
            } else {
                // window is not in the hierarchy map
                // -> log this as an error
                log.error("an unmanaged window was closed: " + window); //$NON-NLS-1$   
            }
        }
    }
    
    /**
     * Investigates the given <code>component</code> for an identifier. To
     * obtain this identifier the name of the component and the container
     * hierarchy is used.
     * @param component the component to create an identifier for, must not be null.
     * @throws ComponentNotManagedException if component is null or <br> (one of the) component(s) in the hierarchy is not managed
     * @return the identifier for <code>component</code>
     */
    public synchronized IComponentIdentifier getComponentIdentifier(
            Widget component) 
        throws ComponentNotManagedException {
        
        IComponentIdentifier result = new ComponentIdentifier();

        try {
            // fill the componentIdentifier
            result.setComponentClassName(component.getClass().getName());
            result.setSupportedClassName(AUTServerConfiguration
                .getInstance().getTestableClass(
                    component.getClass()).getName());
            List<String> hierarchy = getPathToRoot(component);
            result.setHierarchyNames(hierarchy);
            result.setNeighbours(getComponentContext(component));
            HierarchyContainer<Widget> container = 
                    getHierarchyContainer(component);
            setAlternativeDisplayName(container, component, result);
            if (component.equals(findBP.findComponent(result,
                    ComponentHandler.getAutHierarchy()))) {
                result.setEqualOriginalFound(true);
            }
            return result;
        } catch (IllegalArgumentException iae) {
            // from getPathToRoot()
            log.error(iae);
            throw new ComponentNotManagedException(
                    "getComponentIdentifier() called for an unmanaged component " //$NON-NLS-1$
                    + component, MessageIDs.E_COMPONENT_NOT_MANAGED);
            // let pass the ComponentNotManagedException from getPathToRoot()
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected List<String> getComponentContext(Widget comp) {
        List<String> context = new ArrayList<String>();
        Widget widgetParent = SwtUtils.getWidgetParent(comp);
        if (widgetParent != null) {
            HierarchyContainer<Widget> parent = 
                    getHierarchyContainer(widgetParent);
            if (parent != null) {
                HierarchyContainer<Widget>[] comps = parent.getComps();
                for (int i = 0; i < comps.length; i++) {
                    Widget child = comps[i].getCompID().getComponent();
                    if (!child.equals(comp)) {
                        
                        String toAdd = child.getClass().getName() 
                            + Constants.CLASS_NUMBER_SEPERATOR + 1; 
                        while (context.contains(toAdd)) {
                            int lastCount = Integer.valueOf(
                                toAdd.substring(toAdd.lastIndexOf(
                                        Constants.CLASS_NUMBER_SEPERATOR) + 1)).
                                    intValue(); 
                            toAdd = child.getClass().getName() 
                                + Constants.CLASS_NUMBER_SEPERATOR 
                                + (lastCount + 1);
                        }
                        context.add(toAdd);
                    }
                }
            }
        }
        return context;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized IComponentIdentifier[] getAllComponentId() {
        List<IComponentIdentifier> result = new Vector<IComponentIdentifier>();
        Set<? extends AUTComponent<Widget>> keys = getHierarchyMap().keySet();
        for (Iterator<? extends AUTComponent<Widget>> iter = keys.iterator(); 
                iter.hasNext();) {
            Widget component = iter.next().getComponent();
            try {
                if (AUTServerConfiguration.getInstance().isSupported(
                        component)) {
                    
                    result.add(getComponentIdentifier(component));
                }
            } catch (IllegalArgumentException iae) {
                // from isSupported -> log
                log.error("hierarchy map contains null values", iae); //$NON-NLS-1$   
                // and continue
            } catch (ComponentNotManagedException e) {
                // from isSupported -> log
                log.error("component '" + component.getClass().getName() + "' not found!", e); //$NON-NLS-1$ //$NON-NLS-2$                    
                // and continue
            }
        }
        return result
                .toArray(new IComponentIdentifier[result.size()]);
    }
    
    /**
     * Search for the component in the AUT with the given <code>componentIdentifier</code>.
     * @param componentIdentifier the identifier created in object mapping mode
     * @throws IllegalArgumentException if the given identifier is null or <br>
     *         the hierarchy is not valid: empty or containing null elements
     * @throws InvalidDataException if the hierarchy in the componentIdentifier does not consist of strings
     * @throws ComponentNotManagedException if no component could be found for the identifier
     * @return the instance of the component of the AUT 
     */
    public Widget findComponent(IComponentIdentifier componentIdentifier)
        throws IllegalArgumentException, ComponentNotManagedException,
        InvalidDataException {
    
        final Widget comp = (Widget)findBP.findComponent(componentIdentifier,
                ComponentHandler.getAutHierarchy()); 
        if (comp != null) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Shell shell = SwtUtils.getShell(comp);
                    try {
                        if (shell != null && shell.isVisible()) {
                            Shell activeShell = shell.getDisplay()
                                    .getActiveShell();
                            if (activeShell != shell
                                    && !SwtUtils.isDropdownListShell(
                                            activeShell)) {
                                shell.setActive();
                            }
                        }
                    } catch (SWTException swte) {
                        // do nothing if a shell is disposed while accessing
                    }
                }
            });
            return comp; 
        }
        throw new ComponentNotManagedException(
            "unmanaged component with identifier: '" //$NON-NLS-1$
                + componentIdentifier.toString() + "'.", //$NON-NLS-1$ 
                MessageIDs.E_COMPONENT_NOT_MANAGED); 
    }
    
    /**
     * Returns the path from the given component to root. The List contains
     * Strings (the name of the components).
     * @param component the component to start, it's an instance from the AUT, must not be null
     * @throws IllegalArgumentException if component is null
     * @throws ComponentNotManagedException if no hierarchy container exists for the component
     * @return the path to root, the first elements contains the root, the last element contains the component itself.
     */
    private List<String> getPathToRoot(Widget component) 
        throws IllegalArgumentException, ComponentNotManagedException {
        
        if (log.isInfoEnabled()) {
            log.info("pathToRoot called for " + component); //$NON-NLS-1$            
        }
        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$ 
        List<String> hierarchy = new ArrayList<String>();
        HierarchyContainer<Widget> autContainer = 
                getHierarchyContainer(component);
        if (autContainer != null) {
            // add the name of the container itself
            hierarchy.add(autContainer.getName());
            HierarchyContainer<Widget> parent = autContainer.getPrnt();
            // prepend the name of the container up to the root container
            while (parent != null) {
                ((ArrayList<String>)hierarchy).add(0, parent.getName());
                parent = parent.getPrnt();
            }
        } else {
            log.error("component '" + component //$NON-NLS-1$ 
                    + "' is not managed by this hierarchy"); //$NON-NLS-1$
            throw new ComponentNotManagedException(
                    "unmanaged component " //$NON-NLS-1$ 
                    + component.toString(), 
                    MessageIDs.E_COMPONENT_NOT_MANAGED); 
        }
        return hierarchy;
    }
    
    /**
     * Refresh all children components for the given shell
     * @param shell The shell to refresh.
     * 
     */
    public void refreshShell(Shell shell) {
        if (m_shellToListenerMap.containsKey(shell) 
            || getRealMap().containsKey(shell)) {
            remove(shell);
        }
        add(shell);
    }

    /**
     * Adds the given component to the hierarchy if it does
     * not already exist there. Overwrites the current entry in the hierarchy if
     * there is already an entry and that component is <em>not</em> equal to 
     * the given component. Otherwise does nothing.
     * @param toRefresh The component to refresh
     */
    public synchronized void refreshComponent(Widget toRefresh) {
        HierarchyContainer<Widget> currentHierarchyContainer = 
            getHierarchyContainer(toRefresh);
        if (currentHierarchyContainer == null) {
            componentAdded(toRefresh);
        } else if (toRefresh 
            != currentHierarchyContainer.getCompID().getComponent()) {
            
            componentRemoved(toRefresh);
            componentAdded(toRefresh);
        } else {
            // hierarchy container exists and represents same component, so
            // refresh the children
            HierarchyContainer<Widget> [] childContainers = 
                    currentHierarchyContainer.getComps();
            for (int i = 0; i < childContainers.length; i++) {
                removeFromHierarchy(childContainers[i]);
            }
            
            addToHierarchyDown(currentHierarchyContainer, toRefresh);
        }
    }
    
    /**
     * 
     * @param toRefresh The component for which the name should be refreshed.
     */
    public synchronized void refreshComponentName(Widget toRefresh) {
        rename(getHierarchyContainer(toRefresh));
    }

    /**
     * Check if a the Widget is a Control and is not visible.
     * An SWTException is not thrown, if the widget is disposed.
     * @param widget The widget to check.
     * @return True, if the Widget is a Control and it is not visible, otherwise false.
     */
    private boolean isInstanceofControlAndNotVisible(Widget widget) {
        if (widget instanceof Control) {
            try {
                Control c = (Control) widget;
                return !c.isVisible();
            } catch (SWTException swte) {
                // do nothing
            }
        }
        return false;
    }
    
    /**
     * Check if a shell is visible. An SWTException is not thrown, if the shell is disposed.
     * @param shell The shell to check.
     * @return True, if the shell is currently visible, otherwise false.
     */
    private boolean isShellVisible(Shell shell) {
        try {
            return shell.isVisible();
        } catch (SWTException swte) {
            // do nothing
        }
        return false;
    }

    /**
     * Add the new component to the hierarchy if it is not already there.
     * @param toAdd The component to add to the hierarchy.
     */
    public synchronized void componentAdded(Widget toAdd) {
        if (toAdd == null || toAdd.isDisposed()) {
            // Do not add null or disposed components
            return;
        }
        if (isInstanceofControlAndNotVisible(toAdd)) {
            // Do not add invisible components
            return;
        }
        if (toAdd instanceof Shell) {
            add((Shell)toAdd);
        }
        if (getHierarchyContainer(toAdd) != null) {
            return;
        }
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
            this.getClass().getClassLoader());
        try {
            Widget container = SwtUtils.getWidgetParent(toAdd);
            pruneHierarchy(container);
            if (container == null || container.isDisposed()) {
                // Parent for added component does not exist or has been
                // disposed, so do not add the component.
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("component '" + toAdd //$NON-NLS-1$
                    + "' added to '" + container //$NON-NLS-1$
                    + "'"); //$NON-NLS-1$
            }
            // get the hierarchy container for container, must be there!
            HierarchyContainer<Widget> hierarchyContainer = null;
            if (toAdd instanceof Shell) {
                hierarchyContainer = getHierarchyContainer(toAdd);
            } else {
                hierarchyContainer = getHierarchyContainer(container);
            }
            if (hierarchyContainer == null) {
                // This can happen if a new composite is created after a window
                // is activated. The widgets within the composite fire paint
                // events, but the composite itself never does.
                log.info("component added to unmanaged container '" //$NON-NLS-1$
                    + container + "'; adding the container."); //$NON-NLS-1$
                componentAdded(container);
                hierarchyContainer = getHierarchyContainer(container);
                if (hierarchyContainer == null) {
                    log.info("addition of container '" //$NON-NLS-1$
                        + container + "' failed. This may be because the " //$NON-NLS-1$
                        + "container is not visible."); //$NON-NLS-1$
                }
            }
            if (hierarchyContainer != null 
                    && getHierarchyContainer(toAdd) == null) {
                // create new hierarchy container for child, name, update hashtable, put
                // them together,
                HierarchyContainer<Widget> hierarchyChild = 
                    new SwtHierarchyContainer(new SwtComponent(toAdd));
                addToHierachyMap(hierarchyChild);
                hierarchyContainer.add(hierarchyChild);
                toAdd.addDisposeListener(new ComponentDisposingListener());
                name(hierarchyChild);
                addToHierarchyDown(hierarchyChild, toAdd);
            }
        } finally {            
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
    /**
     * Follows the from the given container to the root element in the 
     * component hierarchy, removing non-visible child elements along the
     * way.
     * 
     * @param container The component to start at.
     */
    private void pruneHierarchy(Widget container) {
        Widget currentWidget = container;
        while (currentWidget != null) {
            pruneChildren(currentWidget);
            currentWidget = SwtUtils.getWidgetParent(currentWidget);
        }
    }

    /**
     * Removes all non-visible children of the given container from the 
     * component hierarchy.
     * 
     * @param container The container to look through.
     */
    private void pruneChildren(Widget container) {
        HierarchyContainer<Widget> hierarchyContainer = 
            getHierarchyContainer(container);
        if (hierarchyContainer != null) {
            HierarchyContainer<Widget>[] childContainers = 
                hierarchyContainer.getComps();
            for (int i = 0; i < childContainers.length; i++) {
                Widget child = childContainers[i].getCompID().getComponent();
                if (child.isDisposed() 
                        || isInstanceofControlAndNotVisible(child)) {
                    componentRemoved(child);
                }
            }
        }
    }

    /**
     * Remove the given component from the hierarchy.
     * @param toRemove The component to remove
     */
    public synchronized void componentRemoved(Widget toRemove) {
        if (toRemove instanceof Shell) {
            remove((Shell)toRemove);
            return;
        }
        final ClassLoader originalCL = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
            this.getClass().getClassLoader());
        try {
            
            if (log.isDebugEnabled()) {
                log.debug("removing component '" + toRemove + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // remove the child from hash table
            final Object componentToRemove = getRealMap().get(toRemove);
            if (componentToRemove != null) {
                HierarchyContainer<Widget> hierarchyContainer = 
                        getHierarchyMap().remove(componentToRemove);
                // update the hierarchy and deletes the container and component from maps
                removeFromHierarchy(hierarchyContainer);
            } else {
                // child was not in the hierarchy map
                // -> log this
                log.debug("an unmanaged component was removed: " + toRemove); //$NON-NLS-1$
            }
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
    /**
     * register a window listener to <code>window</code>.<br> deregistering happens in
     * <code>ShellClosingListener.shellClosed()</code>. 
     * @param window the window to register to
     */
    private void registerAsWindowListener(Shell window) {
        if (log.isInfoEnabled()) {
            log.info("registering window listener to shell " //$NON-NLS-1$
                    + window);
        }
        if (isListening(window)) {
            return;
        }
        ShellClosingListener listener = new ShellClosingListener();
        m_shellToListenerMap.put(window, listener);
        window.addShellListener(listener);
        window.addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                remove((Shell)e.widget);
            }
            
        });
    }
    
    /**
     * 
     * @param window The window to check
     * @return <code>true</code> if the window currently has an associated
     *         <code>ShellClosingListener</code>. Otherwise <code>false</code>.
     */
    private boolean isListening(Shell window) {
        return m_shellToListenerMap.containsKey(window);
    }
    
    /**
     * Adds the parent(s) of the given container to the hierarchy recursively. <br> 
     * Recursion stops if the top level container is reached or a parent
     * container is already known. 
     * @param hierarchyContainer the responding hierarchyContainer of container
     * @param container  the container from the AUT
     */
    private synchronized void addToHierarchyUp(
            HierarchyContainer<Widget> hierarchyContainer,
            Composite container) {
        
        if (log.isInfoEnabled()) {
            log.info("addToHierarchyUp: " //$NON-NLS-1$
                    + hierarchyContainer
                    + "," + container); //$NON-NLS-1$
        }

        Composite parent = container.getParent();
        if (parent != null) { // root not reached
            HierarchyContainer<Widget> hierarchyParent = 
                getHierarchyContainer(container);
            if (hierarchyParent == null) {
                // unknown hierarchyContainer for parent:
                // create new hierarchy container, name it, 
                // add current hierarchyContainer to parent hierarchy,
                // update map m_hierarchyMap
                // register listener 
                // recursion
                hierarchyParent = new SwtHierarchyContainer(
                    new SwtComponent(parent));
                hierarchyParent.add(hierarchyContainer);
                name(hierarchyParent);
                addToHierachyMap(hierarchyParent);
                addToHierarchyUp(hierarchyParent, parent);
            }
        }
    }
    
    /**
     * adds the children of the given container to the hierarchy.
     * @param hierarchyContainer the responding container (meta data)
     * @param container the container from the AUT, which children are to be added
     */
    private synchronized void addToHierarchyDown(
            HierarchyContainer<Widget> hierarchyContainer, 
            Widget container) {

        Widget hierarchyComponent = 
            hierarchyContainer.getCompID().getComponent();
        if (container == null || container.isDisposed() 
            || hierarchyComponent == null || hierarchyComponent.isDisposed()) {
     
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("addToHierarchyDown: " //$NON-NLS-1$
                    + hierarchyContainer
                    + "," + container); //$NON-NLS-1$
        }
        name(hierarchyContainer);

        Collection<Widget> collection = getComponents(container);
        for (Iterator<Widget> iter = collection.iterator(); iter.hasNext();) {
            Widget comp = iter.next();
            if (comp == null) {
                continue;
            }
            if (getHierarchyContainer(comp) != null) {
                return;
            }
            // add the container
            componentAdded(comp);
        }
    }
    
    /**
     * removes recursively all containers from <code>container</code><br><p>
     * deregisters this from the container from AUT. <br>
     * updates also the internal hierarchy map. 
     * @param container the container to start
     */
    private void removeFromHierarchy(HierarchyContainer<Widget> container) {
        if (container == null) {
            return;
        }
        
        HierarchyContainer<Widget> parentContainer = container.getPrnt();
        if (parentContainer != null) {
            parentContainer.remove(container);
        }
        
        AUTComponent<Widget> autCompID = container.getCompID();
        Widget autComp = autCompID.getComponent();
        
        if (autComp == null) {
            log.error("invalid component for removal:" //$NON-NLS-1$
                + autCompID.toString());
        }
        removeFromHierachyMap(container);
        if (!autComp.isDisposed()) {
            Collection<Widget> childs = getComponents(autComp);
            Widget widget;
            for (Iterator<Widget> iter = childs.iterator(); iter.hasNext();) {
                widget = iter.next();
                if (widget != null) {
                    removeFromHierarchy(getHierarchyContainer(widget));
                }
            }
        }
    }
    
    /**
     * Returns the hierarchy container for <code>component</code>. 
     * @param component the component from the AUT, must no be null
     * @throws IllegalArgumentException if component is null
     * @return the hierachy container or null if the component is not yet managed
     */
    public HierarchyContainer<Widget> getHierarchyContainer(Widget component) 
        throws IllegalArgumentException {
        
        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$
        HierarchyContainer<Widget> result = null;
        try {
            SwtComponent compID = (SwtComponent)getRealMap().get(component);
            if (compID != null) {
                result = getHierarchyMap().get(compID);
            }
        } catch (ClassCastException cce) {
            log.error(cce);
        } catch (NullPointerException npe) {
            log.error(npe);
        }
        return result;
    }
    
    /**
     * @param widget the component to check, whether it's disappeared or not
     * @return true, if the component disappeared
     */
    public boolean isComponentInHierarchy(Widget widget) {
        
        if (widget == null) {
            return false;
        }
        
        return getHierarchyContainer(widget) != null;
    }
    
    /**
     * Names the given hierarchy container. <br> 
     * If the managed component has a unique name, this name is used. Otherwise
     * a name (unique for the hierarchy level) is created. 
     * @param hierarchyContainer the hierarchyContainer to name, if hierarchyContainer is null,
     *            no action is performed and no exception is thrown.
     */
    private synchronized void name(
            HierarchyContainer<Widget> hierarchyContainer) {
        if (hierarchyContainer != null) {
            Widget component = 
                hierarchyContainer.getCompID().getComponent();
            HierarchyContainer<Widget> hierarchyParent = null;
            Widget parent = SwtUtils.getWidgetParent(component);
            if (parent != null) {
                hierarchyParent = getHierarchyContainer(parent);
                // for some reason, this check is necessary in order to prevent
                // orphaning the hierarchy container. 
                // https://bxapps.bredex.de/bugzilla/show_bug.cgi?id=216 
                // occurred when this null-check was not present.
                if (hierarchyParent != null) {
                    hierarchyContainer.setPrnt(hierarchyParent);
                }
            }
            if (StringUtils.isEmpty(hierarchyContainer.getName())) {
                rename(hierarchyContainer);
            }
        }
    }
    
    /**
     * Renames the given hierarchy container (or just names it, if it did not
     * previously have a name).
     * 
     * @param hierarchyContainer The container to rename.
     */
    private synchronized void rename(
            HierarchyContainer<Widget> hierarchyContainer) {
        if (hierarchyContainer != null) {
            Widget component = 
                hierarchyContainer.getCompID().getComponent();
            
            String compName = FindSWTComponentBP.getComponentName(component);
            HierarchyContainer<Widget> hierarchyParent = 
                    hierarchyContainer.getPrnt();
            
            // isUniqueName is null safe, see description there
            int count = 1;
            String originalName = null;
            String newName = null;
            Object rcpCompId = component
                .getData(SwtToolkitConstants.RCP_NAME);
            boolean newNameGenerated = false;
            if (compName != null) {
                newName = compName.toString();
                originalName = compName.toString();
            } else if (rcpCompId != null) {
                newName = rcpCompId.toString();
                originalName = rcpCompId.toString();
            }
            if (newName == null) {
                newNameGenerated = true;
                while (!isUniqueName(hierarchyParent, component, newName)) {
                    newName = createName(component, count);
                    count++;
                }
            } else {
                while (!isUniqueName(hierarchyParent, component, newName)) {
                    newName = createName(originalName, count);
                    count++;
                }
            }
            hierarchyContainer.setName(newName, newNameGenerated);
        }
    }
    
    /**
     * Checks for uniqueness of <code>name</code> for the components in
     * <code>parent</code>.<br>
     * If parent is null every name is unique, a null name is NEVER unique. If
     * both parameters are null, false is returned. <br>
     * @param parent the hierarchy container containing the components which are checked.
     * @param widget the widget that might receive the checked name
     * @param name the name to check
     * @return true if the name is treated as unique, false otherwise.
     */
    private boolean isUniqueName(HierarchyContainer<Widget> parent,
            Widget widget, String name) {
        if (name == null) {
            return false;
        }
        if (parent == null) {
            return true;
        }
        HierarchyContainer<Widget>[] compIDs = parent.getComps();
        final int length = compIDs.length;
        
        for (int index = 0; index < length; index++) {
            Widget childWidget = 
                compIDs[index].getCompID().getComponent();
            
            if (childWidget != null && !childWidget.isDisposed()) {
                String childWidgetName = FindSWTComponentBP
                        .getComponentName(childWidget);
                
                if ((childWidget != widget) && name.equals(childWidgetName)) {
                    return false;
                }
            }
        }
        
        for (int index = 0; index < length; index++) {
            if (name.equals(compIDs[index].getName())) {
                return false;
            }
        }
        
        return true;
    }
    
    // methods operating on meta data or on the instances of the AUT, depending
    // on the given parameter

    /**
     * Returns all descendants of the given <code>component</code>
     * @param component a <code>Widget</code> 
     * @return a collection of all components in the hierarchy empty <code>collection</code> if nothing was found or <code>c</code> is null.
     */
    private Collection<Widget> getComponents(Widget component) {
        List<Widget> children = new LinkedList<Widget>();
        if (component instanceof Composite) {
            Composite cont = (Composite) component;
            children.addAll(Arrays.asList(cont.getChildren()));
        }
        children.addAll(Arrays.asList(SwtUtils.getMappableItems(component)));
        return children;
    }
    
    /**
     * Registered to all components that are added to the hierarchy.
     * Removes the component from the hierarchy (along with all child
     * components) if the corresponding widget is disposed.
     * 
     * @author BREDEX GmbH
     * @created Jul 13, 2007
     */
    private class ComponentDisposingListener implements DisposeListener {

        /**
         * {@inheritDoc}
         */
        public void widgetDisposed(DisposeEvent event) {
            // BE CAREFUL, the widget (i.e. event.widget) is disposed at this
            // time, so a lot of methods are not allowed to be called,
            // removeDisposeListener() among them
            componentRemoved(event.widget);
        }

    }
    
    /**
     * A shell listener listening to Shell closed, registered to any opened
     * shell. <br>
     * 
     * @author BREDEX GmbH
     * @created 05.10.2004
     */
    private class ShellClosingListener extends ShellAdapter {
        
        /**
         * {@inheritDoc}
         */
        public void shellClosed(ShellEvent event) {
            ClassLoader originalCL = Thread.currentThread()
                .getContextClassLoader();
            Thread.currentThread().setContextClassLoader(
                AUTServer.getInstance().getClass().getClassLoader());
            try {
                Shell window = (Shell)event.widget;    
                remove(window);
            } finally {
                Thread.currentThread().setContextClassLoader(originalCL);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void shellDeactivated(ShellEvent e) {
            ClassLoader originalCL = Thread.currentThread()
                .getContextClassLoader();
            Thread.currentThread().setContextClassLoader(
                AUTServer.getInstance().getClass().getClassLoader());
            try {
                Shell window = (Shell)e.widget;  
                if (!isShellVisible(window)) {
                    remove(window);
                }
            } finally {
                Thread.currentThread().setContextClassLoader(originalCL);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void shellActivated(ShellEvent e) {
            ClassLoader originalCL = Thread.currentThread()
                .getContextClassLoader();
            Thread.currentThread().setContextClassLoader(
                AUTServer.getInstance().getClass().getClassLoader());
            try {
                Shell window = (Shell)e.widget; 
                m_activeWindow = window;
                if (isShellVisible(window)
                    && getHierarchyContainer(window) == null) {
                    add(window);
                }
            } finally {
                Thread.currentThread().setContextClassLoader(originalCL);
            }
        }
    }
    
    @Override
    public boolean isInActiveWindow(Widget component) {

        if (component == null) {
            return false;
        }
        
        boolean searchTopParentComponent = true;
        Widget componentToCheck = component;
        while (searchTopParentComponent) {
            Widget parent = SwtUtils.getWidgetParent(componentToCheck);
            if (parent == null) {
                searchTopParentComponent = false;
            } else {
                componentToCheck = parent;
            }
        }
        if (componentToCheck != null && m_activeWindow != null
                && componentToCheck.equals(m_activeWindow)) {
            return true;
        }

        return false;

    }

}