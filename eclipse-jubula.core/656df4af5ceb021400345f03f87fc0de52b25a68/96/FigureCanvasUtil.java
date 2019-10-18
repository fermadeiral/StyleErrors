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
package org.eclipse.jubula.rc.rcp.e3.gef.util;

import java.util.Map.Entry;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.jubula.rc.rcp.e3.gef.factory.DefaultEditPartAdapterFactory;
import org.eclipse.jubula.rc.rcp.e3.gef.identifier.IEditPartIdentifier;
import org.eclipse.jubula.rc.rcp.e3.gef.listener.GefPartListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


/**
 * Utility class for interacting with a FigureCanvas.
 *
 * @author BREDEX GmbH
 * @created Jun 26, 2009
 */
public class FigureCanvasUtil {

    /**
     * Private constructor
     */
    private FigureCanvasUtil() {
    // Nothing to initialize
    }

    /**
     *
     * @param figureCanvas
     *            The canvas for which to find the viewer.
     * @return the graphical viewer associated with the given canvas, or
     *         <code>null</code> if no such viewer could be found.
     */
    public static GraphicalViewer getViewer(FigureCanvas figureCanvas) {
        Composite parent = figureCanvas;
        while (parent != null
                && !(parent.getData(GefPartListener.TEST_GEF_VIEWER_DATA_KEY)
                        instanceof GraphicalViewer)) {
            parent = parent.getParent();
        }

        if (parent != null) {

            return (GraphicalViewer)parent
                    .getData(GefPartListener.TEST_GEF_VIEWER_DATA_KEY);
        }

        return null;
    }

    /**
     *
     * @param display The display containing the edit part to find.
     * @param viewer The viewer containing the edit part to find.
     * @return the edit part for the viewer at the current mouse pointer
     *         coordinates, or <code>null</code> if no such edit part can be
     *         found.
     */
    public static EditPart findAtCurrentMousePosition(
            Display display, EditPartViewer viewer) {
        Point cursorLocation = new Point(display.map(null, viewer.getControl(),
                display.getCursorLocation()));
        EditPart editPart = viewer.findObjectAt(cursorLocation);
        EditPart primaryEditPart = getPrimaryEditPart(editPart, viewer
                .getRootEditPart());

        return primaryEditPart;
    }

    /**
     * Searches the path to root for the first element considered to be testable
     * by Jubula. Testable in this case means that an ID can be acquired for
     * the edit part.
     *
     * @param editPart
     *            The starting edit part.
     * @param root
     *            The root for <code>editPart</code>. This is used to avoid
     *            identifying the root edit part as the primary edit part.
     * @return the first edit part on the path to root that has an ID.
     */
    public static EditPart getPrimaryEditPart(final EditPart editPart,
            RootEditPart root) {

        EditPart currentEditPart = editPart;

        while (currentEditPart != root.getContents()
                && currentEditPart != null) {
            IEditPartIdentifier identifier = DefaultEditPartAdapterFactory
                    .loadFigureIdentifier(currentEditPart);
            if (identifier != null) {
                return currentEditPart;
            }
            currentEditPart = currentEditPart.getParent();
        }

        return null;
    }

    /**
     *
     * @param editPart
     *            The EditPart for which to find the corresponding figure.
     * @return the (visible) figure corresponding to the given EditPart, or
     *         <code>null</code> if no visible figure corresponds to the given
     *         EditPart.
     */
    public static IFigure findFigure(GraphicalEditPart editPart) {
        if (editPart != null) {
            IFigure figure = editPart.getFigure();
            if (figure.isShowing()) {
                return figure;
            }
        }

        return null;
    }
    
    
    /**
     * gets an {@link Entry} which has the identifier as key and the anchor as value
     * @param connection
     *            the connection for that we want the anchor
     * @return {@link SimpleEntry} with the identifier and the
     *         {@link ConnectionAnchor} or <code>null</code> if not anchor was
     *         found
     */
    public static Entry<String, ConnectionAnchor> getConnectionAnchor(
            ConnectionEditPart connection) {
        if (connection != null) {
            IEditPartIdentifier connectionIdentifier =
                    DefaultEditPartAdapterFactory
                            .loadFigureIdentifier(connection);
            EditPart source = connection.getSource();
            if (source instanceof GraphicalEditPart) {
                GraphicalEditPart graphEditPart = (GraphicalEditPart) source;
                IEditPartIdentifier editPartIdentifier =
                        DefaultEditPartAdapterFactory
                                .loadFigureIdentifier(source);
                if (graphEditPart instanceof NodeEditPart) {
                    NodeEditPart nodeEditPart = (NodeEditPart) graphEditPart;
                    ConnectionAnchor anchor =
                            nodeEditPart.getSourceConnectionAnchor(connection);
                    editPartIdentifier.getConnectionAnchors();
                    for (Entry<String, ConnectionAnchor> entry 
                            : editPartIdentifier
                            .getConnectionAnchors().entrySet()) {
                        if (anchor.equals(entry.getValue())) {
                            return entry;
                        }
                    }
                    return new SimpleEntry<String, ConnectionAnchor>(
                            anchor.getClass().getName(), anchor);
                }
            }
        }

        return null;
    }
    
    /**
     * Helper class could be deleted and replaced with
     * <code>java.util.AbstractMap.SimpleEntry</code> when using java 1.6 and higher
     * FIXME
     * 
     * @param <K> the keys type
     * @param <V> the values type
     */
    private static class SimpleEntry<K, V> implements Entry<K, V> {
        /** key */
        private K m_key;
        /** value*/
        private V m_value;
        /**
         * 
         * @param key the key
         * @param value the value
         */
        public SimpleEntry(K key, V value) {
            this.m_key = key;
            this.m_value = value;
        }
        /** {@inheritDoc}  */
        public K getKey() {
            return m_key;
        }
        /** {@inheritDoc}  */
        public V getValue() {
            return m_value;
        }
        /** {@inheritDoc}  */
        public V setValue(V value) {
            V oldValue = value;
            this.m_value = value;
            return oldValue;
        }
        
    }
}
