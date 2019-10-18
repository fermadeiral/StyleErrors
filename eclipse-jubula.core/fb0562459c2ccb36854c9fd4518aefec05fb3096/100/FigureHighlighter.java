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
package org.eclipse.jubula.rc.rcp.e3.gef.inspector;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jubula.rc.rcp.e3.gef.factory.DefaultEditPartAdapterFactory;
import org.eclipse.jubula.rc.rcp.e3.gef.identifier.IEditPartIdentifier;
import org.eclipse.jubula.rc.rcp.e3.gef.util.FigureCanvasUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;


/**
 * Highlights figures as the mouse moves over them.
 *
 * @author BREDEX GmbH
 * @created Jun 26, 2009
 */
public class FigureHighlighter implements MouseMoveListener {

    /** the viewer containing figures that can be highlighted */
    private GraphicalViewer m_viewer;

    /** the color to use for highlighting */
    private Color m_highlightColor;

    /** transparency for highlighting */
    private int m_highlightAlpha;

    /** the current highlighted bounds */
    private Rectangle m_currentBounds;

    /** the pain listener that performs the actual highlighting */
    private PaintListener m_paintListener;

    /**
     * Constructor
     *
     * @param viewer The viewer containing figures to highlight.
     */
    public FigureHighlighter(GraphicalViewer viewer) {
        m_viewer = viewer;
        m_highlightColor =
            viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_BLUE);
        m_highlightAlpha = 100;
        m_currentBounds = null;
        m_paintListener = new PaintListener() {

            public void paintControl(PaintEvent e) {
                if (m_currentBounds != null) {
                    Color bgColor = e.gc.getBackground();
                    int alpha = e.gc.getAlpha();
                    e.gc.setAlpha(m_highlightAlpha);
                    e.gc.setBackground(m_highlightColor);
                    e.gc.fillRectangle(m_currentBounds);
                    e.gc.setAlpha(alpha);
                    e.gc.setBackground(bgColor);
                }
            }

        };
        viewer.getControl().addPaintListener(m_paintListener);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMove(MouseEvent e) {
        Point cursorLocation =
            new Point(e.x, e.y);
        EditPart editPart =
            FigureCanvasUtil.findAtCurrentMousePosition(e.display, m_viewer);
        if (editPart == m_viewer.getContents().getRoot()
                || editPart == null) {
            if (m_currentBounds != null) {
                m_currentBounds = null;
                m_viewer.getControl().redraw();
            }

            return;
        }

        // Check for connection anchor
        Rectangle anchorBounds =
            getConnectionAnchorBounds(
                DefaultEditPartAdapterFactory.loadFigureIdentifier(
                        editPart), cursorLocation);

        if (anchorBounds == null
                && editPart instanceof ConnectionEditPart) {

            ConnectionEditPart connEditPart =
                (ConnectionEditPart)editPart;
            EditPart srcEditPart = connEditPart.getSource();
            EditPart targetEditPart = connEditPart.getTarget();
            anchorBounds = getConnectionAnchorBounds(
                DefaultEditPartAdapterFactory.loadFigureIdentifier(srcEditPart),
                cursorLocation);
            if (anchorBounds == null) {
                anchorBounds = getConnectionAnchorBounds(
                        DefaultEditPartAdapterFactory
                        .loadFigureIdentifier(targetEditPart),
                        cursorLocation);
            }
        }

        Rectangle bounds = anchorBounds;
        if (bounds == null
                && editPart != m_viewer.getContents().getRoot()
                && !(editPart instanceof ConnectionEditPart)
                && editPart instanceof GraphicalEditPart) {
            IFigure figure = ((GraphicalEditPart)editPart).getFigure();
            org.eclipse.draw2d.geometry.Rectangle figureBounds =
                new org.eclipse.draw2d.geometry.Rectangle(figure.getBounds());
            figure.translateToAbsolute(figureBounds);
            bounds = new Rectangle(figureBounds.x, figureBounds.y,
                    figureBounds.width, figureBounds.height);
        }
        m_currentBounds = bounds;
        m_viewer.getControl().redraw();

    }

    /**
     *
     * @param editPartIdentifier Provides a list of possible anchors.
     * @param cursorLocation The location at which to look for the anchor.
     * @return the bounds for the connection anchor at the given location, or
     *         <code>null</code> if no such anchor can be found.
     */
    private Rectangle getConnectionAnchorBounds(
            IEditPartIdentifier editPartIdentifier, Point cursorLocation) {

        if (editPartIdentifier != null) {
            Map<String, ConnectionAnchor> anchorMap =
                editPartIdentifier.getConnectionAnchors();
            if (anchorMap != null) {
                Iterator<String> iter = anchorMap.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    ConnectionAnchor value = anchorMap.get(key);
                    Point refPoint = value.getReferencePoint();

                    // The "bounds" of an anchor is considered to be
                    // 3 pixels in all directions.
                    Rectangle refBounds = new Rectangle(
                            refPoint.x - 3, refPoint.y - 3, 7, 7);
                    if (refBounds.contains(cursorLocation.getSWTPoint())) {
                        return refBounds;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Removes all listeners added by this highlighter.
     */
    public void removeAddedListeners() {
        Control control = m_viewer.getControl();
        if (!control.isDisposed()) {
            control.removePaintListener(m_paintListener);
            control.redraw();
        }
    }

}
