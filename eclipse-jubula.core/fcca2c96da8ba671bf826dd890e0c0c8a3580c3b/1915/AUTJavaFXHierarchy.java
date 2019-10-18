/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.components.AUTComponent;
import org.eclipse.jubula.rc.common.components.AUTHierarchy;
import org.eclipse.jubula.rc.common.components.HierarchyContainer;
import org.eclipse.jubula.rc.common.exception.ComponentNotManagedException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;

/**
 * This class holds a hierarchy of the components of the AUT. <br>
 *
 * The hierarchy is composed with <code>JavaFXHierarchyContainer</code>s. For
 * every component from the AUT a hierarchy container is created. The names for
 * the components are stored in the appropriate hierarchy containers.<br>
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 *
 */
public class AUTJavaFXHierarchy extends AUTHierarchy<EventTarget> {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            AUTJavaFXHierarchy.class);

    /** Businessprocess for getting components */
    private static FindJavaFXComponentBP findBP = new FindJavaFXComponentBP();

    /** The lock for accessing the Hierarchy **/
    private static volatile ReentrantLock lock = new ReentrantLock(true);

    /**
     * Default constructor
     */
    public AUTJavaFXHierarchy() {
    }

    /**
     * Creates the Hierarchy from a given Object
     *
     * @param eventTarget
     *            the Object
     */
    public void createHierarchyFrom(EventTarget eventTarget) {
        lock.lock();
        try {
            Map<EventTarget, AUTComponent<EventTarget>> realMap = getRealMap();
            EventTarget parent = ParentGetter.get(eventTarget);

            EventTarget lastParent = parent;
            while (parent != null) {
                if (realMap.containsKey(parent)) {
                    lastParent = parent;
                    break;
                }
                lastParent = parent;
                parent = ParentGetter.get(parent);
            }
            createHierarchy(lastParent == null ? eventTarget : lastParent);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds <code>parent</code> and its sub-tree (children, children's 
     * children, etc) to the hierarchy.
     *
     * @param parent
     *            The component / sub-tree to add to the hierarchy. May be 
     *            <code>null</code>, in which case this method returns without
     *            modifying the hierarchy.
     */
    private void createHierarchy(EventTarget parent) {
        if (parent == null) {
            return;
        }

        final JavaFXHierarchyContainer parentCont;
        
        if (getRealMap().containsKey(parent)) {
            parentCont = getHierarchyContainer(parent);
        } else {
            parentCont = initContainer(parent);
            name(parentCont);
            addToHierachyMap(parentCont);
        }
        List<EventTarget> children = ChildrenGetter.getAsList(parent);
        for (EventTarget child : children) {
            createHierarchy(child);
            JavaFXHierarchyContainer childCont = getHierarchyContainer(child);
            if (!(parentCont.contains(childCont))) {
                parentCont.add(childCont);
                childCont.setPrnt(parentCont);
            } else if (childCont.getPrnt() == null) {
                childCont.setPrnt(parentCont);
            }
            name(childCont);
        }
    }

    /**
     * Convenience Method for creating Container.
     *
     * @param node
     *            the Object to create the Container for.
     * @return the Container
     */
    private JavaFXHierarchyContainer initContainer(EventTarget node) {
        AUTComponent<EventTarget> comp = new JavaFXComponent(node);
        JavaFXHierarchyContainer cont = new JavaFXHierarchyContainer(comp);
        return cont;
    }

    /**
     * Removes a component from the hierarchy. This means that the following
     * references will be removed: <br>
     * -Container of the component from the hierarchy Map <br>
     * -The given component from the real map <br>
     * -The reference from the parent container to the container of this
     * component <br>
     * -The actions mentioned above for all children of this component
     *
     * @param component
     *            the component that will be deleted
     */
    public void removeComponentFromHierarchy(EventTarget component) {
        if (component != null) {
            JavaFXHierarchyContainer cont = getHierarchyContainer(component);
            if (cont != null) {
                removeContainer(cont);
            }
        }
    }
    
    /**
     * @param comp the component to check, whether it's disappeared or not
     * @return true, if the component disappeared
     */
    public boolean isComponentInHierarchy(EventTarget comp) {
        if (comp != null) {
            return getHierarchyContainer(comp) != null;
        }
        return false;
    }

    /**
     * Removes a container from the hierarchy. This means that the following
     * references will be removed: <br>
     * -Container from the hierarchy Map <br>
     * -The component of the given container from the real map <br>
     * -The reference from the parent container to the given container <br>
     * -The actions mentioned above for all children of this container
     *
     * @param container
     *            the container that will be deleted
     */
    public void removeContainer(HierarchyContainer<EventTarget> container) {
        lock.lock();
        try {
            Map<? extends AUTComponent<EventTarget>, 
                    HierarchyContainer<EventTarget>> 
                        contMap = getHierarchyMap();

            Map<EventTarget, AUTComponent<EventTarget>> realMap = getRealMap();
            JavaFXComponent fxComp = (JavaFXComponent)container.getCompID();

            fxComp.removeChangeListener();
            contMap.remove(container.getCompID());
            realMap.remove(fxComp.getComponent());
            HierarchyContainer<EventTarget> parent = container.getPrnt();
            if (parent != null) {
                parent.remove(container);
            }
            
            HierarchyContainer<EventTarget>[] children = container.getComps();
            
            for (HierarchyContainer<EventTarget> child : children) {
                removeContainer(child);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IComponentIdentifier[] getAllComponentId() {
        List<IComponentIdentifier> result = new Vector<IComponentIdentifier>();
        Set<? extends AUTComponent<EventTarget>> keys = 
                getHierarchyMap().keySet();
        for (Iterator<? extends AUTComponent<EventTarget>> itr = keys
                .iterator(); itr.hasNext();) {
            AUTComponent<EventTarget> wrapComp = itr.next();
            EventTarget comp = wrapComp.getComponent();
            try {
                if (AUTServerConfiguration.getInstance().isSupported(comp)) {

                    result.add(getComponentIdentifier(comp));
                }
            } catch (IllegalArgumentException iae) {
                // from isSupported -> log
                log.error("hierarchy map contains null values", iae); //$NON-NLS-1$
                // and continue
            } catch (ComponentNotManagedException e) {
                // from isSupported -> log
                log.error("component '" + comp.getClass().getName() + "' not found!", e); //$NON-NLS-1$ //$NON-NLS-2$
                // and continue
            }
        }
        return result.toArray(new IComponentIdentifier[result.size()]);
    }

    /**
     * Investigates the given <code>component</code> for an identifier. To
     * obtain this identifier the name of the component and the container
     * hierarchy is used.
     *
     * @param component
     *            the component to create an identifier for, must not be null.
     * @throws ComponentNotManagedException
     *             if component is null or <br>
     *             (one of the) component(s) in the hierarchy is not managed
     * @return the identifier for <code>component</code>
     */
    public IComponentIdentifier getComponentIdentifier(EventTarget component)
        throws ComponentNotManagedException {

        IComponentIdentifier result = new ComponentIdentifier();
        try {
            // fill the componentIdentifier
            result.setComponentClassName(component.getClass().getName());
            result.setSupportedClassName(AUTServerConfiguration.getInstance()
                    .getTestableClass(component.getClass()).getName());
            List<String> hierarchy = getPathToRoot(component);
            result.setHierarchyNames(hierarchy);
            result.setNeighbours(getComponentContext(component));
            JavaFXHierarchyContainer cont = getHierarchyContainer(component);
            setAlternativeDisplayName(cont, component, result);
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
     * Searches for the component in the AUT with the given
     * <code>componentIdentifier</code>.
     *
     * @param componentIdentifier
     *            the identifier created in object mapping mode
     * @throws IllegalArgumentException
     *             if the given identifier is null or <br>
     *             the hierarchy is not valid: empty or containing null elements
     * @throws InvalidDataException
     *             if the hierarchy in the componentIdentifier does not consist
     *             of strings
     * @throws ComponentNotManagedException
     *             if no component could be found for the identifier
     * @return the instance of the component of the AUT
     */
    public Object findComponent(IComponentIdentifier componentIdentifier)
        throws IllegalArgumentException, ComponentNotManagedException,
            InvalidDataException {
        Object comp = findBP.findComponent(componentIdentifier,
                ComponentHandler.getAutHierarchy());

        if (comp != null) {
            return comp;
        }
        throw new ComponentNotManagedException(
                "unmanaged component with identifier: '" //$NON-NLS-1$
                        + componentIdentifier.toString() + "'.", //$NON-NLS-1$
                MessageIDs.E_COMPONENT_NOT_MANAGED);
    }

    /**
     * Returns the path from the given component to root. The List contains
     * Strings (the name of the containers).
     *
     * @param component
     *            the component to start, it's an instance from the AUT, must
     *            not be null
     * @throws IllegalArgumentException
     *             if component is null
     * @throws ComponentNotManagedException
     *             if no hierarchy container exists for the component
     * @return the path to root, the first elements contains the root, the last
     *         element contains the component itself.
     */
    public List<String> getPathToRoot(EventTarget component)
        throws IllegalArgumentException, ComponentNotManagedException {

        if (log.isInfoEnabled()) {
            log.info("pathToRoot called for " + component); //$NON-NLS-1$
        }
        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$
        ArrayList<String> hierarchy = new ArrayList<String>();
        JavaFXHierarchyContainer container = getHierarchyContainer(component);
        if (container == null) {
            log.error("component '" + component //$NON-NLS-1$ 
                    + "' is not managed by this hierarchy"); //$NON-NLS-1$
            throw new ComponentNotManagedException("unmanaged component " //$NON-NLS-1$ 
                    + component.toString(), MessageIDs.E_COMPONENT_NOT_MANAGED);
        }
        hierarchy.add(container.getName());
        JavaFXHierarchyContainer parent = (JavaFXHierarchyContainer) container
                .getPrnt();

        while (parent != null) {
            container = parent;
            hierarchy.add(0, container.getName());
            parent = (JavaFXHierarchyContainer) container.getPrnt();
        }
        return hierarchy;
    }

    @Override
    protected List<String> getComponentContext(EventTarget component) {
        JavaFXHierarchyContainer parent;
        JavaFXHierarchyContainer comp;
        List<String> context = new ArrayList<String>();
        if (component instanceof JavaFXHierarchyContainer) {
            comp = (JavaFXHierarchyContainer) component;

        } else {
            comp = getHierarchyContainer(component);
        }
        parent = (JavaFXHierarchyContainer) comp.getPrnt();

        if (parent != null) {
            HierarchyContainer<EventTarget>[] comps = parent.getComps();
            for (HierarchyContainer<EventTarget> child : comps) {
                if (!child.equals(comp)) {
                    String toAdd = child.getName();
                    context.add(toAdd);
                }
            }
        }
        return context;
    }

    /**
     * Returns the hierarchy container for <code>component</code>.
     *
     * @param component
     *            the component from the AUT, must no be null
     * @throws IllegalArgumentException
     *             if component is null
     * @return the hierarchy container or null if the component is not yet
     *         managed
     */
    public JavaFXHierarchyContainer getHierarchyContainer(EventTarget component)
        throws IllegalArgumentException {

        Validate.notNull(component, "The component must not be null"); //$NON-NLS-1$
        JavaFXHierarchyContainer result = null;
        try {
            AUTComponent<EventTarget> compID = getRealMap().get(component);

            if (compID != null) {
                result = (JavaFXHierarchyContainer) getHierarchyMap().get(
                        compID);
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
     *
     * @param hierarchyContainer
     *            the SwingHierarchyContainer to name, if
     *            SwingHierarchyContainer is null, no action is performed and no
     *            exception is thrown.
     */
    protected void name(JavaFXHierarchyContainer hierarchyContainer) {
        final AUTComponent<EventTarget> comp = hierarchyContainer.getCompID();
        String compName;
        EventTarget realComponent = comp.getComponent();
        if (realComponent instanceof Node) {
            compName = ((Node) realComponent).getId();
        } else if (realComponent instanceof MenuItem) {
            compName = ((MenuItem) realComponent).getId();
        } else {
            compName = null;
        }
        // Because stages don't have a list of children we can't include the
        // relationship between stages in the hierarchy. Therefore this
        // workaround is necessary to create a unique name for a stage container.
        if (realComponent instanceof Window) {
            List<Window> windows = CurrentStages.getWindowList();
            ArrayList<String> names = new ArrayList<>();
            for (Window win : windows) {
                JavaFXHierarchyContainer c = getHierarchyContainer(win);
                if (c != null && c != hierarchyContainer) {
                    names.add(c.getName());
                }
            }
            String name = null;
            int count = 0;
            while (!isUniqueName(name, names)) {
                count++;
                name = createName(realComponent, count);
            }
            comp.setName(name);
            hierarchyContainer.setName(name, true);
            return;
        }
        HierarchyContainer<EventTarget> hierParent = 
                hierarchyContainer.getPrnt();
        if (hierarchyContainer.getName() != null
                && hierarchyContainer.getName().length() != 0
                && !(hierarchyContainer.getName().trim().isEmpty())) {
            // In extra if, for logging purposes
            if (isUniqueName(hierParent, hierarchyContainer.getName(),
                    hierarchyContainer)) {
                return;
            } else if (log.isInfoEnabled()) {
                log.info("New name created for " + hierarchyContainer.getName() //$NON-NLS-1$
                        + "even though there was already a name!"); //$NON-NLS-1$
            }
        }
        int count = 0;
        String originalName = null;
        String newName = null;
        boolean newNameGenerated = (compName == null);
        if (!StringUtils.isBlank(compName)) {
            originalName = compName;
            newName = compName;
        }
        if (newName == null) {
            while (!isUniqueName(hierParent, newName, hierarchyContainer)) {
                count++;
                newName = createName(realComponent, count);
            }
        } else {
            while (!isUniqueName(hierParent, newName, hierarchyContainer)) {
                count++;
                newName = createName(originalName, count);
            }
        }
        comp.setName(newName);
        hierarchyContainer.setName(newName, newNameGenerated);
    }

    /**
     * Checks for uniqueness of <code>name</code> for the components in
     * <code>parent</code>.<br>
     * If parent is null every name is unique, a null name is NEVER unique. If
     * both parameters are null, false is returned. <br>
     *
     * @param parent
     *            the hierarchy container containing the components which are
     *            checked.
     * @param name
     *            the name to check
     * @param container
     *            The component for which the name is being checked.
     * @return true if the name is treated as unique, false otherwise.
     */
    protected boolean isUniqueName(HierarchyContainer<EventTarget> parent,
            String name, HierarchyContainer<EventTarget> container) {
        if (name == null) {
            return false;
        }
        if (parent == null) {
            return true;
        }
        
        HierarchyContainer<EventTarget>[] compIDs = parent.getComps();

        for (HierarchyContainer<EventTarget> childContainer: compIDs) {
            String childName = childContainer.getName();

            if (name.equals(childName) && childContainer != container) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks for uniqueness of given name using the given list
     * 
     * @param name the name to check
     * @param otherNames the list with the names
     * @return true or false
     */
    protected boolean isUniqueName(String name, List<String> otherNames) {
        if (name == null || otherNames == null) {
            return false;
        } else if (otherNames.isEmpty()) {
            return true;
        }
        return !otherNames.contains(name);
    }

    /**
     * Returns the lock of the hierarchy
     *
     * @return the lock
     */
    public static ReentrantLock getLock() {
        return lock;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isInActiveWindow(EventTarget component) {

        if (component != null && component instanceof Node) {
            Node node = (Node) component;
            Scene scene = node.getScene();
            if (scene != null) {
                Window window = scene.getWindow();
                if (window != null) {
                    return window.isFocused();
                }
            }
        }

        return false;
    }
}
