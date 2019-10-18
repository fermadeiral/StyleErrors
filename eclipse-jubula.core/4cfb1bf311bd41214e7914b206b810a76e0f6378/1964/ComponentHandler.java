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
package org.eclipse.jubula.rc.javafx.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.components.AUTComponent;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.common.exception.ComponentNotManagedException;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.javafx.components.AUTJavaFXHierarchy;
import org.eclipse.jubula.rc.javafx.components.CurrentStages;
import org.eclipse.jubula.rc.javafx.components.FindJavaFXComponentBP;
import org.eclipse.jubula.rc.javafx.components.JavaFXComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.sync.IStageResizeSync;
import org.eclipse.jubula.rc.javafx.listener.sync.StageResizeSyncFactory;
import org.eclipse.jubula.rc.javafx.tester.adapter.IContainerAdapter;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.NodeTraverseHelper;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Skinnable;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * This class is responsible for handling the components of the AUT. <br>
 *
 * The static methods for fetching an identifier for a component and getting the
 * component for an identifier delegates to this AUTHierarchy.
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 */
public class ComponentHandler implements ListChangeListener<Window>,
        BaseAUTListener {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            ComponentHandler.class);

    /** the Container hierarchy of the AUT */
    private static AUTJavaFXHierarchy hierarchy = new AUTJavaFXHierarchy();

    /** Businessprocess for getting components */
    private static FindJavaFXComponentBP findBP = new FindJavaFXComponentBP();

    /** used for synchronizing on stage resize events */
    private static IStageResizeSync stageResizeSync = 
            StageResizeSyncFactory.instance();
    
    /**lock for hierarchy access**/
    private static volatile ReentrantLock lock = AUTJavaFXHierarchy.getLock();
    
    /**
     * Constructor. Adds itself as ListChangeListener to the Stages-List
     */
    public ComponentHandler() {
        CurrentStages.addStagesListener(this);
    }

    @Override
    public void onChanged(Change<? extends Window> change) {
        change.next();

        for (final Window win : change.getRemoved()) {
            hierarchy.removeComponentFromHierarchy(win);
        }
        
        for (final Window win : change.getAddedSubList()) {
            if (win.isShowing()) {
                hierarchy.createHierarchyFrom(win);
                stageResizeSync.register(win);
            } else {
                win.addEventFilter(WindowEvent.WINDOW_SHOWN,
                        new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                hierarchy.createHierarchyFrom(win);
                                stageResizeSync.register(win);
                                win.removeEventFilter(WindowEvent.WINDOW_SHOWN,
                                        this);
                            }
                        });
            }
        }
    }

    @Override
    public long[] getEventMask() {
        return null;
    }

    /**
     * @return the Container hierarchy of the AUT
     */
    public static AUTJavaFXHierarchy getAutHierarchy() {
        return hierarchy;
    }

    /**
     * Searches the hierarchy-map (in the JavaFX-Thread) for components that are
     * assignable from the given type
     *
     * @param type the type to look for
     * @param <T> component type
     * @return List
     */
    public static <T> List<? extends T> getAssignableFrom(final Class<T> type) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getAssignableFrom", //$NON-NLS-1$
                new Callable<List<? extends T>>() {

                    @Override
                    public List<? extends T> call() throws Exception {
                        Set<JavaFXComponent> keys = (Set<JavaFXComponent>) 
                                hierarchy.getHierarchyMap().keySet();
                        List<T> result = new ArrayList<T>();
                        for (AUTComponent<EventTarget> object : keys) {
                            EventTarget component = object.getComponent();
                            if (type.isAssignableFrom(component.getClass())) {
                                result.add(type.cast(component));
                            }
                        }
                        return result;
                    }
                });
    }
    
    /**
     * Traverses the scene graph from the given parent and adds all nodes to a
     * result list if the following conditions are met: The node is visible and
     * the given position is within the bounds of this node
     * 
     * @param parent
     *            the parent
     * @param pos
     *            the position
     * @param resultList
     *            the result list
     * @return A list witch all nodes which are visible and the given position is
     *         within the bounds of this node
     */
    private static List<Node> getAllNodesforPos(Parent parent, Point2D pos,
            List<Node> resultList) {
        //Blame checkstyle for this extra list
        List<Node> result = resultList;
        if (parent.isVisible()) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                if (child.isVisible() 
                        && NodeBounds.checkIfContains(pos, child)) {
                    result.add(child);
                    if (child instanceof Parent) {
                        result = getAllNodesforPos((Parent) child, pos, result);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the node under the given point
     *
     * @param pos
     *            the point
     * @return the component
     */
    public static Node getComponentByPos(Point2D pos) {
        List<? extends Window> comps = getAssignableFrom(Window.class);
        Map<Window, List<Node>> matchesByWindows =
                new HashMap<Window, List<Node>>();
        Set<Window> shadowedWindows = new HashSet<>(); 
        List<Node> localNodes;
        for (Window window : comps) {
            localNodes = new ArrayList<Node>();
            matchesByWindows.put(window, localNodes);
            if ((window.isFocused() && window.isShowing()) 
                    || (window.isShowing()
                            && window instanceof PopupWindow)) {
                Parent root = window.getScene().getRoot();
                localNodes = getAllNodesforPos(root, pos, localNodes);
                if (!localNodes.isEmpty() && window instanceof PopupWindow) {
                    // if a popup window is under the cursor, it shadows its owner window
                    // we need this, because the owner window is both visible and has a focus
                    shadowedWindows.add(((PopupWindow) window).
                            getOwnerWindow());
                }
            }
        }
        List<Node> matches = new ArrayList<Node>();
        for (Window window : matchesByWindows.keySet()) {
            // we only keep not-shadowed window components
            if (!shadowedWindows.contains(window)) {
                matches.addAll(matchesByWindows.get(window));
            }
        }
        List<Node> result = new ArrayList<Node>();
        for (Node n : matches) {
            if (isMappable(n)) {
                result.add(n);
            }
        }
        if (result.size() == 0) {
            return null;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        // multiple matches, try filtering
        return filterMatches(result);
    }
    
    /**
     * Determines whether a node is mappable or not
     * @param n the node
     * @return the result
     */
    public static boolean isMappable(Node n) {
        boolean mappable = true;
        if (n.getScene() == null || !isSupported(n.getClass())
                || !n.isVisible()) {
            return false;
        }
        Parent parent = n.getParent();
        while (parent != null) {
            if (parent instanceof Skinnable || isContainer(parent)) {
                if (isContentNode(n, parent)) {
                    break;
                }
                Skin<?> skin = ((Skinnable) parent).getSkin();
                if (skin instanceof SkinBase) {
                    // We don't want skin nodes
                    if (isSkinNode(n, (SkinBase<?>) skin)) {
                        mappable = false;
                        break;
                    }
                } else {
                    parent = parent.getParent();
                }
            } else {
                parent = parent.getParent();
            }
        }
        return mappable;
    }

    /**
     * Checks if the given node is a container.
     * @param n the possible container
     * @return true if the node is a container false otherwise
     */
    private static boolean isContainer(Node n) {
        IContainerAdapter adapter = (IContainerAdapter) AdapterFactoryRegistry.
                getInstance().getAdapter(IContainerAdapter.class, n);
        return adapter != null;
    }
    
    /**
     * Checks if the given class is supported by the AUT-Server
     * @param c the class
     * @return true if the type is supported, false if not
     */
    private static boolean isSupported(Class<?> c) {
        Set<ComponentClass> supportedTypes = AUTServerConfiguration
            .getInstance().getSupportedTypes();
        Class<?> currentClass = c;
        while (currentClass != null) {
            for (Object object : supportedTypes) {
                if (((ComponentClass) object).getName().equals(
                    currentClass.getName())) {
                    return true;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return false;
    }
    
    /**
     * Checks if the given Node is Part of the Content of the given Parent.
     * This is checked for the following types:
     *  TitledPane
     *  ScrollPane
     *  SplitPane
     *  ToolBar
     * This Classes don't share a parent class which would make accessing 
     * the Content easier. Therefore this special (bad) code is necessary.
     * @param n the possible content node.
     * @param parent a parent of the above mentioned type
     * @return true if the given node is part of the content, false if not.
     */
    private static boolean isContentNode(Node n, Parent parent) {
        IContainerAdapter adapter = (IContainerAdapter) AdapterFactoryRegistry.
                getInstance().getAdapter(IContainerAdapter.class, parent);
        
        if (adapter != null) {
            List<? extends Node> content = adapter.getContent();
            if (content.contains(n)) {
                return true;
            }
            for (Node contentNode : content) {
                if (contentNode instanceof Parent 
                        && NodeTraverseHelper.isChildOf(
                                n, (Parent)contentNode)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if the given node is part of a skin
     * @param node the node
     * @param skin the skin
     * @return true if it is part the given skin, false if not
     */
    private static boolean isSkinNode(Node node, SkinBase<?> skin) {
        ObservableList<Node> skinChildren = skin.getChildren();
        for (Node n : skinChildren) {
            if (n == node) {
                return true;
            } else if (n instanceof Parent) {
                if (NodeTraverseHelper.isChildOf(node, (Parent) n)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Filters out all parent in a list of matches
     * @param matches the matches
     * @return the filtered list
     */
    private static Node filterMatches(List<Node> matches) {
        
        List<Node> filteredMatches = filterOutUnfocussedNodes(matches);
        if (filteredMatches.size() == 1) {
            return filteredMatches.get(0);
        }
        
        Node firstCommonAncestor = findFirstCommonAncestor(filteredMatches);
        /* Always of type Parent */
        if (firstCommonAncestor != null) {
            return topMostDescendant(
                    (Parent)firstCommonAncestor, filteredMatches);
        }
        return null;
    }
    
    /**
     * Filters out nodes from unfocused windows from a given list
     * @param matches the list
     * @return list containing only the nodes of focused window
     */
    private static List<Node> filterOutUnfocussedNodes(List<Node> matches) {
        List<Node> filteredMatches = new ArrayList<Node>();
        for (Node match : matches) {
            if (match.getScene().getWindow().isFocused()) {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    /**
     * Returns all instances of the type Node from a given list which are 
     * descendants of a given parent node
     * @param parent the parent
     * @param matches list of possible descendants
     * @return all descendants of the parent which also occur in the list
     */
    private static List<Node> filterDescendants(Parent parent, 
            List<Node> matches) {
        List<Node> descendants = new ArrayList<Node>();
        for (Node match : matches) {
            if (isDescendant(match, parent)) {
                descendants.add(match);
            }
        }
        return descendants;
    }
    
    /**
     * Returns the descendant of a parent node from a given list which has 
     * the highest "z-coordinate".
     * @param parent the parent
     * @param matches the list of descendants
     * @return the top-most descendant from the list
     */
    private static Node topMostDescendant(Parent parent, List<Node> matches) {
        ObservableList<Node> children = parent.getChildrenUnmodifiable();
        ArrayList<Node> revertedChildren = new ArrayList<Node>(children);
        Collections.reverse(revertedChildren);
        // Start by checking if the last child of the StackPane is among the matches
        for (Node child : revertedChildren) {
            if (child instanceof Parent) {
                List<Node> remainingMatches = 
                        filterDescendants((Parent)child, matches);
                if (remainingMatches.size() == 1) {
                    return remainingMatches.get(0);
                } else if (remainingMatches.size() > 1) {
                    return topMostDescendant(
                            ((Parent) child), remainingMatches);
                }
            } 
            if (matches.contains(child)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Checks whether a node is a descendant of a node
     * @param candidate the possible descendant
     * @param node the node
     * @return whether the candidate is a descendant of the other node
     */
    private static boolean isDescendant(Node candidate, Node node) {
        if (candidate == null) {
            return false;
        } else if (candidate == node) {
            return true;
        }
        return isDescendant(candidate.getParent(), node);
    }
    
    /**
     * Finds the first common ancestor of a list of nodes
     * @param nodelist the list
     * @return the first common ancestor
     */
    private static Node findFirstCommonAncestor(List<Node> nodelist) {
        if (nodelist == null || nodelist.size() <= 0) {
            return null;
        } else if (nodelist.size() == 1) {
            return nodelist.get(0);
        } else {
            return findFirstCommonAncestor(nodelist.get(0), 
                    findFirstCommonAncestor(nodelist.subList(
                            1, nodelist.size())));
        }
    }
    
    /**
     * Finds the first common ancestor of two nodes
     * @param node1 first node
     * @param node2 second node
     * @return their first common ancestor
     */
    private static Node findFirstCommonAncestor(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return null;
        }
        if (isDescendant(node1, node2)) {
            return node2;
        }
        return findFirstCommonAncestor(node1, node2.getParent());
    }


    /**
     * Investigates the given <code>component</code> for an identifier. It must
     * be distinct for the whole AUT. To obtain this identifier the AUTHierarchy
     * is queried.
     *
     * @param node
     *            the node to get an identifier for
     * @throws NoIdentifierForComponentException
     *             if an identifier could not created for <code>component</code>
     *             .
     * @return the identifier, containing the identification
     */
    public static IComponentIdentifier getIdentifier(Node node)
        throws NoIdentifierForComponentException {

        try {
            return hierarchy.getComponentIdentifier(node);
        } catch (ComponentNotManagedException cnme) {
            log.warn(cnme);
            throw new NoIdentifierForComponentException(
                    "unable to create an identifier for '" //$NON-NLS-1$
                            + node + "'", //$NON-NLS-1$
                    MessageIDs.E_COMPONENT_ID_CREATION);
        }
    }

    /**
     * Finds a Node by id
     *
     * @param id
     *            the id
     * @return the node ore null if there is nothing or something else than a
     *         node found
     */
    public static Node findNodeByID(IComponentIdentifier id) {
        Object comp = findBP.findComponent(id, hierarchy);
        if (comp != null && comp instanceof Node) {
            return (Node) comp;
        }
        return null;
    }

    /**
     * Searches the component in the AUT, which belongs to the given
     * <code>componentIdentifier</code>.
     *
     * @param componentIdentifier
     *            the identifier of the component to search for
     * @param retry
     *            number of tries to get object
     * @param timeout
     *            timeout for retries
     * @throws ComponentNotFoundException
     *             if no component is found for the given identifier.
     * @throws IllegalArgumentException
     *             if the identifier is null or contains invalid data
     *             {@inheritDoc}
     * @return the found component
     */
    public static Object findComponent(
        IComponentIdentifier componentIdentifier, boolean retry, int timeout)
        throws ComponentNotFoundException, IllegalArgumentException {
        long start = System.currentTimeMillis();
        try {
            lock.lock();
            return hierarchy.findComponent(componentIdentifier);
        } catch (ComponentNotManagedException cnme) {
            if (retry) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        lock.lock();
                        return hierarchy.findComponent(componentIdentifier);
                    } catch (ComponentNotManagedException e) { // NOPMD by zeb
                                                               // on 10.04.07
                                                               // 15:25
                        // OK, we will throw a corresponding exception later
                        // if we really can't find the component
                        if (lock.isHeldByCurrentThread()) {
                            lock.unlock();
                        }
                        try {
                            Thread.sleep(TimingConstantsServer.
                                    POLLING_DELAY_FIND_COMPONENT);
                        } catch (InterruptedException e1) {
                            // ok
                        }
                    } catch (InvalidDataException ide) { // NOPMD by zeb on
                                                         // 10.04.07 15:25
                        // OK, we will throw a corresponding exception later
                        // if we really can't find the component
                    }
                }
            }
            throw new ComponentNotFoundException(cnme.getMessage(),
                    MessageIDs.E_COMPONENT_NOT_FOUND);
        } catch (IllegalArgumentException iae) {
            log.error(iae);
            throw iae;
        } catch (InvalidDataException ide) {
            log.error(ide);
            throw new ComponentNotFoundException(ide.getMessage(),
                    MessageIDs.E_COMPONENT_NOT_FOUND);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }

        }
    }
    /**
     * Checks the component in the AUT, which belongs to the given
     * <code>componentIdentifier</code> is disappeared or nor.
     * 
     * @param componentIdentifier
     *            the identifier of the component to search for
     * @param timeout
     *      timeout for retry
     * @throws ComponentNotFoundException
     *             if no component is found for the given identifier.
     * @throws IllegalArgumentException
     *             if the identifier is null or contains invalid data
     * {@inheritDoc}
     * @return true if the component is disappeared else false
     */
    public static boolean isComponentDisappeared(
            IComponentIdentifier componentIdentifier, int timeout)
                    throws ComponentNotFoundException,
                    IllegalArgumentException {
        long start = System.currentTimeMillis();
        try {
            lock.lock();
            EventTarget component = (EventTarget) hierarchy
                    .findComponent(componentIdentifier);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            while (System.currentTimeMillis() - start < timeout) {
                lock.lock();
                TimeUtil.delay(
                        TimingConstantsServer.POLLING_DELAY_FIND_COMPONENT);
                boolean isComponentDisappeared = !hierarchy
                        .isComponentInHierarchy(component);
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                if (isComponentDisappeared) {
                    return true;
                }
            }
            return false;
        } catch (ComponentNotManagedException cnme) {
            return true;
        } catch (IllegalArgumentException iae) {
            log.error(iae);
            throw iae;
        } catch (InvalidDataException ide) {
            log.error(ide);
            throw new ComponentNotFoundException(ide.getMessage(),
                    MessageIDs.E_COMPONENT_NOT_FOUND);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }

        }
    }
    
    /**
     * Blocks the calling thread until the Stage has been sufficiently resized
     * to deliver reliable component bounds. May not be called on the FX Thread.
     */
    public static void syncStageResize() {
        stageResizeSync.await();
    }
}
