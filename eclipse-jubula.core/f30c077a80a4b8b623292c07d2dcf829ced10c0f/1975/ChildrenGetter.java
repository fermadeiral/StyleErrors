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
import java.util.Collection;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * This class handles the receiving of children from different components from a
 * Hierarchical point of view.
 *
 * @author BREDEX GmbH
 * @created 25.10.2013
 */
public class ChildrenGetter {

    /**
     * Private Constructor
     */
    private ChildrenGetter() {
        // private
    }

    /**
     * Returns a list of children of a given Object
     *
     * @param o
     *            the object
     * @return the children in a List of Objects.
     */
    public static List<EventTarget> getAsList(EventTarget o) {
        List<EventTarget> result = new ArrayList<EventTarget>();
        if (o instanceof Menu) {
            result.addAll(getFrom((Menu) o));
        } else if (o instanceof Parent) {
            if (o instanceof ScrollPane) {
                add(result, getFrom((ScrollPane) o).getValue());
            } else {
                result.addAll(getFrom((Parent) o));
            }
        } else if (o instanceof Window) {
            add(result, getFrom((Window) o).getValue());
        } else if (o instanceof Scene) {
            add(result, getFrom((Scene) o).getValue());
        } else if (o instanceof ContextMenu) {
            add(result, getFrom((ContextMenu) o).getValue());
        }

        return result;
    }

    /**
     * Gets the child of the <code>ContextMenu</code>, its <code>Scene</code>
     * 
     * @param c
     *            the <code>ContextMenu</code>
     * @return the <code>Scene</code>
     */
    public static ReadOnlyObjectProperty<Scene> getFrom(ContextMenu c) {
        return c.sceneProperty();
    }

    /**
     * Adds <code>toAdd</code> to <code>collection</code> if
     * <code>toAdd != null</code>.
     * 
     * @param collection
     *            The collection to be modified by this call.
     * @param toAdd
     *            The element to add.
     * @return <code>true</code> if <code>collection</code> changed as a result
     *         of this call.
     */
    private static boolean add(Collection<EventTarget> collection, 
            EventTarget toAdd) {
        if (toAdd != null) {
            return collection.add(toAdd);
        }

        return false;
    }

    /**
     * Returns either an ObservableList of children or an
     * <Code>ObjectProperty</Code> of a given Object
     *
     * @param o
     *            the object
     * @return the children in a List of Objects.
     */
    public static Object getAsRealType(Object o) {
        Object result = null;
        if (o instanceof Menu) {
            result = getFrom((Menu) o);
        } else if (o instanceof Parent) {
            if (o instanceof ScrollPane) {
                result = getFrom((ScrollPane) o);
            } else {
                result = getFrom((Parent) o);
            }
        } else if (o instanceof Stage) {
            result = getFrom((Stage) o);
        } else if (o instanceof Scene) {
            result = getFrom((Scene) o);
        } else if (o instanceof ContextMenu) {
            result = getFrom((ContextMenu) o);
        }
        return result;
    }

    /**
     * Returns the Root Property of a Scene.
     *
     * @param scene
     *            the Scene
     * @return Root Property
     */
    public static ReadOnlyObjectProperty<Parent> getFrom(Scene scene) {
        return scene.rootProperty();
    }

    /**
     * Returns the Child Property of a Stage, the Scene Property.
     *
     * @param window
     *            the window
     * @return the Scene Property
     */
    public static ReadOnlyObjectProperty<Scene> getFrom(Window window) {
        return window.sceneProperty();
    }

    /**
     * Returns the Children of a Parent Node, but without the nodes that belong
     * to the Skin.
     *
     * @param parent
     *            the Parent
     * @return List with the child nodes
     */
    public static ObservableList<Node> getFrom(Parent parent) {
        return parent.getChildrenUnmodifiable();
    }

    /**
     * Even though a ScrollPane is an Instance of Parent, the Hierarchical child
     * Nodes of a ScrollPane aren't in the Children list, but accessible over
     * getContent. The Return vale will be one node.
     *
     * @param scPane
     *            the ScrollPane
     * @return List with one Node, the content from the ScrollPane
     */
    public static ObjectProperty<? extends EventTarget> 
        getFrom(ScrollPane scPane) {
        return scPane.contentProperty();
    }

    /**
     * Returns a list of MenuItems which also could be a Menu and therefore have
     * children.
     *
     * @param menu
     *            the Menu
     * @return List with MenuItems
     */
    public static ObservableList<MenuItem> getFrom(Menu menu) {
        ObservableList<MenuItem> result = FXCollections.observableArrayList();
        result.addAll(menu.getItems());
        return result;
    }
}
