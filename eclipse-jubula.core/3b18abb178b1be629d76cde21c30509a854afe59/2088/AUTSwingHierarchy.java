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
package org.eclipse.jubula.rc.swing.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.components.AUTComponent;
import org.eclipse.jubula.rc.common.components.AUTHierarchy;
import org.eclipse.jubula.rc.common.components.HierarchyContainer;
import org.eclipse.jubula.rc.common.exception.ComponentNotManagedException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swing.SwingAUTServer;
import org.eclipse.jubula.rc.swing.listener.ComponentHandler;
import org.eclipse.jubula.rc.swing.utils.WorkerRunnable;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.MappingConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;



/**
 * This class holds a hierarchy of the components of the AUT. <br>
 * 
 * The hierarchy is composed with <code>HierarchyContainer</code>s. For every
 * component from the AUT a hierarchy container is created. The names for the
 * components are stored in the appropriate hierarchy containers, instead of the
 * components itself. Thus the AUTServer does not affect the instances from the
 * AUT. <br>
 * 
 * In JRE 1.3 the WINDOW_CLOSED event is not delivered properly, so a window
 * listener is added to any opened window listening to
 * <code>WindowEvent.WINDOW_CLOSED</code>.<br>
 * <p>
 * <b>Interferences with the AUT</b>
 * <ul>
 * <li>The AUTHierarchy is registered as a container listener to every
 * container from the AUT (but not to the hierarchy container).</li>
 * <li>The AUTHierarchy is registered as a window listener to every window
 * from the AUT.</li>
 * </ul>
 * 
 * @author BREDEX GmbH
 * @created 30.08.2004
 *
 */
public class AUTSwingHierarchy extends AUTHierarchy<Component>
    implements ContainerListener, ComponentListener {

    /** 
     * name of environment variable / Java property that should be set to 
     * "true" (case-insensitive) if Swing/AWT listeners should be 
     * (de-)registered directly in the thread that handles Swing / AWT events
     */
    private static final String ENV_VAR_SYNC_REGISTER_LISTENERS = 
        "JB_SYNC_REG_SWING_LISTENERS"; //$NON-NLS-1$

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        AUTSwingHierarchy.class);

    /** businessprocess for getting components */
    private static FindSwingComponentBP findBP = new FindSwingComponentBP();

    /** 
     * the worker responsible for handling (de-)registration of 
     * Swing/AWT listeners
     */
    private WorkerRunnable m_listenerRegistrationWorker = new WorkerRunnable();

    /** 
     * whether Swing/AWT listeners should be (de-)registered directly in the 
     * thread that handles Swing / AWT events. if not, then a worker thread is
     * used.  
     */
    private boolean m_syncListenerRegistration = false;

    /**
     * Constructor
     */
    public AUTSwingHierarchy() {
        String syncListenersRegistrationValue = EnvironmentUtils
                .getProcessOrSystemProperty(ENV_VAR_SYNC_REGISTER_LISTENERS);

        m_syncListenerRegistration = Boolean.valueOf(
                syncListenersRegistrationValue).booleanValue();

        if (!m_syncListenerRegistration) {
            Thread registrationThread = 
                new Thread(m_listenerRegistrationWorker, 
                        "Jubula Listener Registration"); //$NON-NLS-1$
            registrationThread.setDaemon(true);
            registrationThread.start();
        }
    }
    
    /**
     * Adds the complete hierarchy of the given <code>window</code> to the
     * hierarchy. <br>
     * @param window a new (and opened) window
     */
    public void add(Window window) {
        // if window has no parent, its a new top level container, otherwise
        // the parent is already in the AutHierarchy, 
        // NO!: creating a Window without a parent, calling show()
        // -> window.getParent() == SwingUtilities$1
        // don't add, if in hierarchy map yet
        
        if (getRealMap().get(window) == null
            || getHierarchyContainer(window) == null) {
               
            if (log.isInfoEnabled()) {
                log.info("adding window " + window); //$NON-NLS-1$            
            }
                        
            // create a new SwingHierarchyContainer for window 
            AUTComponent<Component> componentID = new SwingComponent(window);
            HierarchyContainer<Component> hierarchyWindow = 
                new SwingHierarchyContainer(componentID);
            // update the hash table
            addToHierachyMap(hierarchyWindow);
            // add a window listener for window closed events
            registerAsWindowListener(window);
            // get the parent of window, if any
            Container parent = window.getParent();
            if (parent != null) {
                HierarchyContainer<Component> hierarchyParent = 
                    getHierarchyContainer(parent);
                if (hierarchyParent == null) {
                    // a new container, see comment at top of the method
                    hierarchyParent = new SwingHierarchyContainer(
                        new SwingComponent(parent));
                }
                name(hierarchyParent);
                // add the new container for the window to hierarchyParent
                hierarchyParent.add(hierarchyWindow);
                hierarchyWindow.setPrnt(hierarchyParent);
                name(hierarchyWindow);
                // update m_hierarchyMap
                addToHierachyMap(hierarchyParent);
                addToHierarchyUp(hierarchyParent, parent);
            }
        }
        // registering this class as a container listener happens in addToHierarchy
        addToHierarchyDown(getHierarchyContainer(window), window);
    }
        
    /**
     * Removes the given window from the hierarchy.
     * @param window the window to remove.
     */
    private void remove(Window window) {
        // remove window from the map
        // remove the container from hierarchyMap
        // deregistering the listener from the window happens in window listener itself
        if (getRealMap().get(window) != null) {
            HierarchyContainer<Component> windowContainer = getHierarchyMap()
                    .get(getRealMap().get(window));
            if (windowContainer != null) {
                // remove the windowContainer from its parent in the hierarchy, if any
                HierarchyContainer<Component> parentContainer = 
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
     * @throws ComponentNotManagedException if component is null or <br>
     *      (one of the) component(s) in the hierarchy is not managed
     * @return the identifier for <code>component</code>
     */
    public IComponentIdentifier getComponentIdentifier(
            Component component) 
        throws ComponentNotManagedException {
        checkDispatchThread();
        IComponentIdentifier result = new ComponentIdentifier();
        try {
            // fill the componentIdentifier
            result.setComponentClassName(component.getClass().getName());
            result.setSupportedClassName(AUTServerConfiguration.getInstance()
                .getTestableClass(component.getClass()).getName());
            List<String> hierarchy = getPathToRoot(component);
            result.setHierarchyNames(hierarchy);
            result.setNeighbours(getComponentContext(component));
            HierarchyContainer<Component> container = 
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
                    "getComponentIdentifier() called for an unmanaged component: " //$NON-NLS-1$
                    + component, MessageIDs.E_COMPONENT_NOT_MANAGED);
            // let pass the ComponentNotManagedException from getPathToRoot()
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected List<String> getComponentContext(Component comp) {
        List<String> context = new ArrayList<String>();
        if (comp.getParent() != null) {
            HierarchyContainer<Component> parent = getHierarchyContainer(
                    comp.getParent());
            if (parent != null) {
                HierarchyContainer<Component>[] comps = parent.getComps();
                for (int i = 0; i < comps.length; i++) {
                    Component child = comps[i].getCompID()
                        .getComponent();
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
    public IComponentIdentifier[] getAllComponentId() {
        checkDispatchThread();
        List<IComponentIdentifier> result = new Vector<IComponentIdentifier>();
        Set<? extends AUTComponent<Component>> keys = 
                getHierarchyMap().keySet();
        for (Iterator<? extends AUTComponent<Component>> 
            iter = keys.iterator(); iter.hasNext();) {
            
            Component component = iter.next().getComponent();
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
                log.error("component '" + component.getName() + "' not found!", e); //$NON-NLS-1$ //$NON-NLS-2$                    
                // and continue
            }
        }
        return result.toArray(new IComponentIdentifier[result.size()]);
    }
    
    /**
     * Search for the component in the AUT with the given
     * <code>componentIdentifier</code>.
     * @param componentIdentifier the identifier created in object mapping mode
     * @throws IllegalArgumentException if the given identifier is null or <br>the hierarchy is not valid: empty or containing null elements
     * @throws InvalidDataException if the hierarchy in the componentIdentifier does not consist of strings
     * @throws ComponentNotManagedException if no component could be found for the identifier
     * @return the instance of the component of the AUT 
     */
    public Component findComponent(
        IComponentIdentifier componentIdentifier)
        throws IllegalArgumentException, ComponentNotManagedException,
        InvalidDataException {
        Component comp = (Component)findBP.findComponent(componentIdentifier,
                ComponentHandler.getAutHierarchy());

        if (comp != null && comp.isShowing()) {
            Window window = SwingUtilities.getWindowAncestor(comp);
            if (window != null && window.isShowing() && !window.isActive()) {
                window.toFront();
            }
            return comp;
        }
        throw new ComponentNotManagedException(
            "unmanaged component with identifier: '" //$NON-NLS-1$
                + componentIdentifier.toString() + "'.", //$NON-NLS-1$ 
                MessageIDs.E_COMPONENT_NOT_MANAGED); 
    }
    
    /**
     * @param comp the component to check, whether it's disappeared or not
     * @return true, if the component disappeared
     */
    public boolean isComponentInHierarchy(Component comp) {
        
        if (comp == null) {
            return false;
        }
        
        return getHierarchyContainer(comp) != null;
    }

    /**
     * Returns the path from the given component to root. The List contains
     * Strings (the name of the components).
     * @param component the component to start, it's an instance from the AUT, must not be null
     * @throws IllegalArgumentException if component is null
     * @throws ComponentNotManagedException if no hierarchy container exists for the component
     * @return the path to root, the first elements contains the root, the last element contains the component itself.
     */
    public List<String> getPathToRoot(Component component) 
        throws IllegalArgumentException, ComponentNotManagedException {
        
        if (log.isInfoEnabled()) {
            log.info("pathToRoot called for " + component); //$NON-NLS-1$            
        }
        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$ 
        List<String> hierarchy = new ArrayList<String>();
        HierarchyContainer<Component> parent;
        HierarchyContainer<Component> autContainer = 
                getHierarchyContainer(component);
        if (autContainer != null) {
            // add the name of the container itself
            hierarchy.add(autContainer.getName());
            final String className = component.getClass().getName();
            if (MappingConstants.SWING_APPLICATION_CLASSNAME.equals(className)
                || MappingConstants.SWING_MENU_DEFAULT_MAPPING_CLASSNAME
                    .equals(className)
                || MappingConstants.SWING_MENU_CLASSNAME.equals(className)) {
                
                return hierarchy;
            }
            parent = getHierarchyContainer(component.getParent());
            autContainer.setPrnt(parent);
            // prepend the name of the container up to the root container
            while (parent != null) {
                ((ArrayList<String>)hierarchy).add(0, parent.getName());
                Component compo = parent.getCompID().getComponent();
                parent = parent.getPrnt();
                if (parent == null && compo != null 
                        && compo.getParent() != null) {
                    
                    AUTComponent<Component> comp = 
                            new SwingComponent(compo.getParent());
                    HierarchyContainer<Component> container = 
                        new SwingHierarchyContainer(comp);
                    name(container);
                    parent = container;
                    addToHierachyMap(container);
                }
            }
        } else {
            log.error("component '" + component //$NON-NLS-1$ 
                    + "' is not managed by this hierarchy"); //$NON-NLS-1$
            throw new ComponentNotManagedException(
                    "unmanaged component " + component.toString(), //$NON-NLS-1$                    
                    MessageIDs.E_COMPONENT_NOT_MANAGED); 
        }
        return hierarchy;
    }
    
    /**
     * Add the new component to the hierarchy.
     * {@inheritDoc}
     */
    public void componentAdded(ContainerEvent event) {
        checkDispatchThread();
        addComponent(event.getChild());
    }

    /**
     * Add the new component to the hierarchy.
     * 
     * @param toAdd The component to add to the hierarchy.
     */
    private void addComponent(Component toAdd) {
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass()
                .getClassLoader());
        try {
            // Don't add an invisible component
            if (!toAdd.isShowing()) {
                return;
            }
            Container container = toAdd.getParent();
            if (log.isDebugEnabled()) {
                log.debug("component '" + toAdd //$NON-NLS-1$
                    + "' added to '" + container + "'"); //$NON-NLS-1$ //$NON-NLS-2$                  
            }
            // get the hierarchy container for container, must be there!
            HierarchyContainer<Component> hierarchyContainer = null;
            if (toAdd instanceof Window) {
                hierarchyContainer = getHierarchyContainer(toAdd);
            } else {
                hierarchyContainer = getHierarchyContainer(container);
            }

            if (hierarchyContainer == null) {
                // Parent container not managed at this time.
                // Do not clutter up the hierarchy with orphan components.
                return;
            }
            // create new hierarchy container for child, name, update hashtable, put
            // them together,
            if (getHierarchyContainer(toAdd) != null) {
                return;
            }
            // create new hierarchy container for child, name, update hashtable, put
            // them together,
            HierarchyContainer<Component> hierarchyChild = 
                new SwingHierarchyContainer(new SwingComponent(toAdd));
            hierarchyContainer.add(hierarchyChild);
            hierarchyChild.setPrnt(hierarchyContainer);
            name(hierarchyChild);
            addToHierachyMap(hierarchyChild);
            if (toAdd instanceof Container) {
                Container cont = (Container)toAdd;
                // call addTohierachyDown()
                addToHierarchyDown(hierarchyChild, cont);
            }
        } finally {            
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
    /**
     * Remove the removed component from the hierarchy.
     * {@inheritDoc}
     */
    public void componentRemoved(ContainerEvent event) {
        checkDispatchThread();
        removeComponent(event.getChild(), event.getContainer());
    }

    /**
     * Remove the removed component from the hierarchy.
     * 
     * @param toRemove The component to remove from the hierarchy.
     * @param parent The parent of the component to remove.
     */
    private void removeComponent(Component toRemove, Container parent) {
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
            this.getClass().getClassLoader());
        try {
            if (log.isDebugEnabled()) {
                log.debug("component '" + toRemove //$NON-NLS-1$
                    + "' removed from '" + parent + "'"); //$NON-NLS-1$ //$NON-NLS-2$ 
            }
            // deregister as listener
            if (toRemove instanceof Container) {
                deregisterAsContainerListener((Container)toRemove);
            }
            HierarchyContainer<Component> hierarchyChild = 
                    getHierarchyContainer(toRemove);
            // update the hierarchy
            if (hierarchyChild != null) {
                HierarchyContainer<Component> hierarchyParent = hierarchyChild
                    .getPrnt();
                if (hierarchyParent != null) {
                    hierarchyParent.remove(hierarchyChild);
                } else {
                    // child was not in the hierarchy 
                    // -> log this is an error
                    log.error("hierarchy structure corrupted, " //$NON-NLS-1$
                        + "child has no parent: " + hierarchyChild); //$NON-NLS-1$
                        
                }
                // remove children recursively
                removeFromHierarchy(hierarchyChild);
            } else {
                // child was not in the hierarchy map
                // -> log this as an error
                log.debug("an unmanaged component was removed: " + toRemove); //$NON-NLS-1$                  
            }
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
    /**
     * Register the AutHierarchy as a container listener to <code>container</code>.
     * @param container the container to register to
     */
    private void registerAsContainerListener(final Container container) {
        Runnable registrationRunnable = new Runnable() {
            public void run() {
                if (log.isInfoEnabled()) {
                    log.info("registering as listener to container " + container); //$NON-NLS-1$               
                }

                ContainerListener[] listener = 
                    container.getContainerListeners();
                for (int i = 0; i < listener.length; i++) {
                    if (listener[i] instanceof AUTSwingHierarchy) {
                        return;
                    }
                }
                container.addContainerListener(AUTSwingHierarchy.this);
            }
        };

        registerListener(registrationRunnable);

    }

    /**
     * (De-)Registers a Swing / AWT listener, using the given {@link Runnable}.
     * A worker thread may be used in order to perform the (de-)registration 
     * asynchronously, depending on how the receiver is configured.
     * 
     * @param registrationRunnable The work to perform in order to 
     *                             (de-)register the listener.
     */
    private void registerListener(Runnable registrationRunnable) {
        if (m_syncListenerRegistration) {
            registrationRunnable.run();
        } else {
            m_listenerRegistrationWorker.addWork(registrationRunnable);
        }
    }
    
    /**
     * remove the AutHierarchy as a container listener from <code>container</code>.
     * @param container the container to deregister from
     */
    private void deregisterAsContainerListener(final Container container) {
        Runnable deregistrationRunnable = new Runnable() {
            public void run() {
                if (log.isInfoEnabled()) {
                    log.info("deregistering as listener from container " + container); //$NON-NLS-1$       
                }
                container.removeContainerListener(AUTSwingHierarchy.this);
            }
        };

        registerListener(deregistrationRunnable);
    }
    
    /**
     * register a window listener to <code>window</code>.<br> deregistering happens in
     * <code>WindowClosingListener.windowClosed()</code>.
     * @param window the window to register to
     */
    private void registerAsWindowListener(final Window window) {
        Runnable registrationRunnable = new Runnable() {

            public void run() {
                if (log.isInfoEnabled()) {
                    log.info("registering window listener to window " //$NON-NLS-1$
                            + window);
                }
                WindowListener[] listener = window.getWindowListeners();
                for (int i = 0; i < listener.length; i++) {
                    if (listener[i] instanceof WindowClosingListener) {
                        return;
                    }
                }
                window.addWindowListener(new WindowClosingListener());
            }
        };

        registerListener(registrationRunnable);
    }
    
    /**
     * Adds the parent(s) of the given container to the hierarchy recursively. <br>
     * Recursion stops if the top level container is reached or a parent
     * container is already known.
     * 
     * @param hierarchyContainer
     *            the responding SwingHierarchyContainer of container
     * @param container
     *            the container from the AUT
     */
    private void addToHierarchyUp(
            HierarchyContainer<Component> hierarchyContainer,
            Container container) {
        
        checkDispatchThread();
        if (log.isInfoEnabled()) {
            log.info("addToHierarchyUp: " //$NON-NLS-1$
                    + hierarchyContainer + "," + container); //$NON-NLS-1$
        }
        registerAsContainerListener(container);
        Container parent = container.getParent();
        if (parent != null) { // root not reached
            HierarchyContainer<Component> hierarchyParent = 
                getHierarchyContainer(container);            
            if (hierarchyParent == null) {
                // unknown SwingHierarchyContainer for parent:
                // create new hierarchy container, name it, 
                // add current SwingHierarchyContainer to parent hierarchy,
                // update map m_hierarchyMap
                // register listener 
                // recursion
                hierarchyParent = new SwingHierarchyContainer(
                        new SwingComponent(parent));
                hierarchyParent.add(hierarchyContainer);
                hierarchyContainer.setPrnt(hierarchyParent);
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
    private void addToHierarchyDown(
            HierarchyContainer<Component> hierarchyContainer,
            Container container) {

        checkDispatchThread();
        if (log.isInfoEnabled()) {
            log.info("addToHierarchyDown: " + hierarchyContainer + "," + container); //$NON-NLS-1$ //$NON-NLS-2$
        }
        registerAsContainerListener(container);
        Collection<Component> collection = getComponents(container);
        for (Component component : collection) {
            // Don't add if the component is already in our hierarchy or 
            // if it is invisible.
            if (getHierarchyContainer(component) != null 
                || !component.isShowing()) {
                continue;
            }
            
            if (component instanceof Window) {
                add((Window)component);
            } else {
                // add the container
                HierarchyContainer<Component> newHierarchyContainer = 
                    new SwingHierarchyContainer(new SwingComponent(component));
                name(newHierarchyContainer);
                // update the hash table
                newHierarchyContainer.setPrnt(hierarchyContainer);
                hierarchyContainer.add(newHierarchyContainer);
                addToHierachyMap(newHierarchyContainer);
                if (component instanceof Container) {
                    // recursively down
                    addToHierarchyDown(newHierarchyContainer, 
                        (Container)component);
                }         
            }
        }
        name(hierarchyContainer);
    }
    
    /**
     * removes recursively all containers from <code>container</code><br>
     * <p> deregisters this from the container from AUT. <br> updates also the internal hierarchy map.
     * @param container the container to start
     */
    private void removeFromHierarchy(HierarchyContainer<Component> container) {
        if (container == null) {
            return;
        }
        AUTComponent<Component> autCompID = container.getCompID();
        Component autComp = autCompID.getComponent();
        
        if (autComp == null) {
            log.error("invalid component for removal:"  //$NON-NLS-1$
                + autCompID.toString()); 
        }
        removeFromHierachyMap(container);
        if (autComp instanceof Container) {
            deregisterAsContainerListener((Container)autComp);
        }
        Collection<Component> childs = getComponents(autComp);
        for (Iterator<Component> iter = childs.iterator(); iter.hasNext();) {
            removeFromHierarchy(getHierarchyContainer(iter.next()));
        }
    }
    
    /**
     * Returns the hierarchy container for <code>component</code>.
     * @param component the component from the AUT, must no be null
     * @throws IllegalArgumentException if component is null
     * @return the hierarchy container or null if the component is not yet managed
     */
    public HierarchyContainer<Component> getHierarchyContainer(
            Component component) 
        throws IllegalArgumentException {
        
        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$
        HierarchyContainer<Component> result = null;
        try {
            AUTComponent<Component> compID = getRealMap().get(component);
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
     * Names the given hierarchy container. <br>
     * If the managed component has a unique name, this name is used. Otherwise
     * a name (unique for the hierarchy level) is created.
     * @param hierarchyContainer the SwingHierarchyContainer to name, if SwingHierarchyContainer is null,
     *            no action is performed and no exception is thrown.
     */
    private void name(HierarchyContainer<Component> hierarchyContainer) {
        checkDispatchThread();
        if (hierarchyContainer != null) {
            final Component component = hierarchyContainer.getCompID()
                .getComponent(); 
            String compName = component.getName();
            // SPECIAL HANDLING !!! -----------------------------------
            if (component instanceof Dialog && compName != null 
                    && compName.startsWith("dialog")) { //$NON-NLS-1$
                
                compName = null;
            } else if (component instanceof JToolBar && compName != null 
                    && compName.startsWith("Tool Bar ")) { //$NON-NLS-1$
                
                compName = null;
            }
            // --------------------------------------------------------
            HierarchyContainer<Component> hierarchyParent = null;
            final Container parent = component.getParent();
            if (parent != null) {
                hierarchyParent = getHierarchyContainer(parent);
            }
            if (hierarchyContainer.getName() != null 
                    && hierarchyContainer.getName().length() != 0) {
                
                return;
            }
            // isUniqueName is null safe, see description there
            int count = 1;
            String originalName = null;
            String newName = null;
            boolean newNameGenerated = (compName == null);
            if (compName != null) {
                originalName = compName;
                newName = compName;
            }
            if (newName == null) {
                while (!isUniqueName(hierarchyParent, newName, component)) {
                    newName = createName(component, count);
                    count++;
                }
            } else {
                while (!isUniqueName(hierarchyParent, newName, component)) {
                    count++;
                    newName = createName(originalName, count);
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
     * @param name the name to check
     * @param swingComponent The component for which the name is being checked.
     * @return true if the name is treated as unique, false otherwise.
     */
    private boolean isUniqueName(HierarchyContainer<Component> parent, 
            String name, Component swingComponent) {
        
        if (name == null) {
            return false;
        }
        if (parent == null) {
            return true;
        }
        HierarchyContainer<Component>[] compIDs = parent.getComps();
        final int length = compIDs.length;
        
        for (int index = 0; index < length; index++) {
            Component childComponent = 
                compIDs[index].getCompID().getComponent();
            Object childName = childComponent.getName();
            
            if (name.equals(childName) && childComponent != swingComponent) {
                return false;
            }
        }
        
        for (int index = 0; index < length; index++) {
            if (name.equals(compIDs[index].getName())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Returns all descendants of the given <code>component</code>
     * @param c a <code>component</code> value
     * @return a <code>collection</code> of the component's descendants or an
     * empty <code>collection</code> if nothing was found or <code>c</code> is null.
     */
    private Collection<Component> getComponents(Component c) {
        if (c instanceof Container) {
            Container cont = (Container) c;
            List<Component> list = new ArrayList<Component>();
            list.addAll(Arrays.asList(cont.getComponents()));
            if (c instanceof JMenu) {
                list.add(((JMenu) c).getPopupMenu());
            } else if (c instanceof Window) {
                list.addAll(Arrays.asList(((Window) c).getOwnedWindows()));
            } else if (c instanceof JDesktopPane) {
                // add iconified frames, which are otherwise unreachable
                // for consistency, they are still considered children of
                // the desktop pane.
                int count = cont.getComponentCount();
                for (int i = 0; i < count; i++) {
                    Component child = cont.getComponent(i);
                    if (child instanceof JInternalFrame.JDesktopIcon) {
                        JInternalFrame frame = 
                            ((JInternalFrame.JDesktopIcon) child)
                                .getInternalFrame();
                        if (frame != null) {
                            list.add(frame);
                        }
                    }
                }
            }
            return list;
        }
        // an empty ArrayList
        return new ArrayList<Component>();
    }
    
    /**
     * A window listener listening to window closed, registered to any opened window. <br>
     * @author BREDEX GmbH
     * @created 05.10.2004
     *
     */
    private class WindowClosingListener extends WindowAdapter {
        
        /**
         * {@inheritDoc}
         */
        public void windowOpened(WindowEvent e) {
            windowActivated(e);
        }

        /**
         * {@inheritDoc}
         */
        public void windowClosed(final WindowEvent event) {
            Runnable deregistrationRunnable = new Runnable() {
                public void run() {
                    ClassLoader originalCL = 
                        Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(
                            ((SwingAUTServer)AUTServer.getInstance()).getClass()
                            .getClassLoader());
                    try {
                        Window window = event.getWindow();    
                        remove(window);
                        if (log.isInfoEnabled()) {
                            log.info("deregistering window listener from window "  + window); //$NON-NLS-1$
                        }
                        window.removeWindowListener(WindowClosingListener.this);
                    } finally {
                        Thread.currentThread()
                            .setContextClassLoader(originalCL);
                    }
                }
            };

            registerListener(deregistrationRunnable);
        }

        /**
         * {@inheritDoc}
         */
        public void windowDeactivated(WindowEvent e) {
            ClassLoader originalCL = Thread.currentThread()
                .getContextClassLoader();
            Thread.currentThread().setContextClassLoader(
                ((SwingAUTServer)AUTServer.getInstance()).getClass()
                    .getClassLoader());
            try {
                Window window = e.getWindow();  
                if (!window.isVisible()) {
                    remove(window);
                }
            } finally {
                Thread.currentThread().setContextClassLoader(originalCL);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void windowActivated(WindowEvent e) {
            ClassLoader originalCL = Thread.currentThread()
                .getContextClassLoader();
            Thread.currentThread().setContextClassLoader(
                    ((SwingAUTServer)AUTServer.getInstance()).getClass()
                        .getClassLoader());
            try {
                Window window = e.getWindow(); 
                if (window.isVisible() 
                    && getHierarchyContainer(window) == null) {
                    
                    add(window);
                }
            } finally {
                Thread.currentThread().setContextClassLoader(originalCL);
            }
        }  
    }

    /**
     * {@inheritDoc}
     */
    public void componentHidden(ComponentEvent e) {
        checkDispatchThread();
        Component component = e.getComponent();
        if (component instanceof Window) {
            remove((Window) component);
        } else {
            removeComponent(component, component.getParent());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void componentMoved(ComponentEvent e) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void componentResized(ComponentEvent e) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void componentShown(ComponentEvent e) {
        checkDispatchThread();
        Component component = e.getComponent();
        if (component instanceof Window) {
            add((Window) component);
        } else {
            addComponent(component);
        }
    }

    /**
     * Checks whether the current thread is the dispatch thread, and logs an
     * error if it is not.
     */
    private void checkDispatchThread() {
        if (!EventQueue.isDispatchThread()) {
            // throw and catch an exception so we can get a stack trace
            try {
                throw new Exception();
            } catch (Exception e) {
                log.error("Method called outside of the dispatch thread. This may indicate a potential error in the AUT.", e); //$NON-NLS-1$
            }
        }
    }
    
    @Override
    public boolean isInActiveWindow(Component component) {
       
        if (component == null) {
            return false;
        }
        
        Window windowAncestor = SwingUtilities
                .getWindowAncestor(component);
        return (windowAncestor != null && windowAncestor.isActive());
        
    }
}