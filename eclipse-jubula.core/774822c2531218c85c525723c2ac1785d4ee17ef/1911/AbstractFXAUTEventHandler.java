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

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.listener.AUTEventListener;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.HighlightNode;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.stage.Window;

/**
 * @author BREDEX GmbH
 * @created 15.10.2013
 */
public abstract class AbstractFXAUTEventHandler implements AUTEventListener,
    ListChangeListener<Window> {

    /** The current Node **/
    private Node m_currentNode;

    /**
     * Sets the current <code>Node</code> that will be, or is, highlighted
     *
     * @param n
     *            the <code>Node</code> to be highlighted
     */
    public void setCurrentNode(Node n) {
        m_currentNode = n;
    }

    /**
     * Returns the current <code>Node</code> that will be, or is, highlighted
     *
     * @return the currentNode
     */
    public Node getCurrentNode() {
        return m_currentNode;
    }

    /**
     * Highlights the current Node
     */
    public void highlightCurrentNode() {
        if (m_currentNode != null) {
            EventThreadQueuerJavaFXImpl.invokeAndWait("cleanUp", //$NON-NLS-1$
                    new Callable<Void>() {
    
                    @Override
                    public Void call() throws Exception {
                        HighlightNode.drawHighlight(m_currentNode);
                        return null;
                    }
                });
        }
    }

    /**
     * Lowlights the current Node
     */
    public void lowlightCurrentNode() {
        if (m_currentNode != null) {
            EventThreadQueuerJavaFXImpl.invokeAndWait("cleanUp", //$NON-NLS-1$
                    new Callable<Void>() {
    
                    @Override
                    public Void call() throws Exception {
                        HighlightNode.removeHighlight(m_currentNode);
                        return null;
                    }
                });
        }
    }

    /**
     * Adds a <code>MouseHandler</code> to the given stage
     *
     * @param s
     *            the Stage
     */
    public abstract void addHandler(Window s);

    /**
     * Removes a <code>MouseHandler</code> from the given stage
     *
     * @param s
     *            the Stage
     */
    public abstract void removeHandler(Window s);
        
    @Override
    public void cleanUp() {
        lowlightCurrentNode();
    }

    @Override
    public void update() {

    }

    @Override
    public boolean highlightComponent(IComponentIdentifier comp) {
        Node n = ComponentHandler.findNodeByID(comp);
        if (n != null) {
            setCurrentNode(n);
            // Highlight only in JAVAFX Thread
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    highlightCurrentNode();
                }
            });

            return true;
        }
        return false;
    }

    @Override
    public long[] getEventMask() {

        return null;
    }
    
    @Override
    public void onChanged(ListChangeListener.Change<? extends Window> change) {
        change.next();
        List<? extends Window> changedWindows = change.getAddedSubList();
        for (final Window win : changedWindows) {
            addHandler(win);
        }
        changedWindows = change.getRemoved();
        for (final Window win : changedWindows) {
            removeHandler(win);
        }
    }
}
