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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jubula.communication.internal.message.InspectorComponentSelectedMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.listener.IAutListenerAppender;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.PropertyUtil;
import org.eclipse.jubula.rc.rcp.e3.gef.factory.DefaultEditPartAdapterFactory;
import org.eclipse.jubula.rc.rcp.e3.gef.identifier.IEditPartIdentifier;
import org.eclipse.jubula.rc.rcp.e3.gef.listener.GefPartListener;
import org.eclipse.jubula.rc.rcp.e3.gef.util.FigureCanvasUtil;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;


/**
 * Adds listeners for the Inspector function. These listeners will only
 * interact with GEF components.
 *
 * @author BREDEX GmbH
 * @created Jun 11, 2009
 */
public class GefInspectorListenerAppender implements IAutListenerAppender {

    /** the logger */
    private static final AutServerLogger LOG =
        new AutServerLogger(GefInspectorListenerAppender.class);

    /**
     * Responsible for adding highlighters to controls and managing those
     * highlighters.
     *
     * @author BREDEX GmbH
     * @created Jun 29, 2009
     */
    private static class FigureHighlightAppender implements Listener {

        /** mapping from control to figure highlighter */
        private Map<FigureCanvas, FigureHighlighter> m_canvasToListener =
            new HashMap<FigureCanvas, FigureHighlighter>();

        /**
         * {@inheritDoc}
         */
        public void handleEvent(Event event) {
            if (event.widget instanceof FigureCanvas) {
                FigureCanvas canvas = (FigureCanvas)event.widget;
                if (event.type == SWT.MouseEnter) {
                    handleMouseEnter(canvas);
                }
            }
        }

        /**
         * Removes all listeners added by this appender.
         */
        public void removeAddedListeners() {
            Iterator<FigureCanvas> iter = m_canvasToListener
                    .keySet().iterator();
            while (iter.hasNext()) {
                Control control = iter.next();
                if (!control.isDisposed()) {
                    FigureHighlighter highlighter =
                        m_canvasToListener.get(control);
                    highlighter.removeAddedListeners();
                    control.removeMouseMoveListener(highlighter);
                }
            }
            m_canvasToListener.clear();
        }

        /**
         * Adds a figure highlighter to the given canvas.
         *
         * @param canvas The figure canvas entered by the mouse.
         */
        private void handleMouseEnter(final FigureCanvas canvas) {
            if (m_canvasToListener.containsKey(canvas)) {
                // Highlighter already registered.
                return;
            }

            GraphicalViewer viewer = FigureCanvasUtil.getViewer(canvas);
            if (viewer != null) {
                if (viewer.getContents() instanceof GraphicalEditPart) {
                    FigureHighlighter highlighter =
                        new FigureHighlighter(viewer);
                    m_canvasToListener.put(canvas, highlighter);
                    canvas.addMouseMoveListener(highlighter);
                }
            }
        }

    }

    /**
     * Listens for a mouse click and:
     *  1. deregisters itself and any other provider listeners, and
     *  2. sends a "component selected" message
     *
     * if that mouse click was over a GEF component.
     *
     * @author BREDEX GmbH
     * @created Jun 11, 2009
     */
    private static class GefInspectorComponentSelectedListener
            implements Listener {

        /** the appender */
        private FigureHighlightAppender m_highlightAppender;

        /**
         * Constructor
         *
         * @param highlightAppender The appender to use.
         */
        public GefInspectorComponentSelectedListener(
                FigureHighlightAppender highlightAppender) {
            m_highlightAppender = highlightAppender;
        }

        /**
         *
         * @param editPartIdentifier Provides connection anchor IDs and
         *                           locations.
         * @param cursorLocation The location at which to search for an
         *                       anchor point.
         * @return the ID of the connection anchor at the given location, or
         *         <code>null</code> if there is no connection anchor at the
         *         given location.
         */
        private String getConnectionAnchorId(
                IEditPartIdentifier editPartIdentifier, Point cursorLocation) {

            if (editPartIdentifier != null) {
                Map<?, ?> anchorMap =
                    editPartIdentifier.getConnectionAnchors();
                if (anchorMap != null) {
                    Iterator<?> iter = anchorMap.keySet().iterator();
                    while (iter.hasNext()) {
                        Object key = iter.next();
                        Object value = anchorMap.get(key);
                        if (key instanceof String
                                && value instanceof ConnectionAnchor) {
                            Point refPoint =
                                ((ConnectionAnchor)value).getReferencePoint();

                            // A click is recognized as being "within the
                            // bounds" of an anchor if it is within 3 pixels
                            // in any direction.
                            Rectangle refBounds = new Rectangle(
                                    refPoint.x - 3, refPoint.y - 3, 7, 7);
                            if (refBounds.contains(cursorLocation)) {
                                return (String)key;
                            }
                        }
                    }
                }
            }

            return null;
        }

        /**
         *
         * {@inheritDoc}
         */
        public void handleEvent(Event event) {
            Display display = event.display;
            display.removeFilter(SWT.MouseDown, this);
            display.removeFilter(SWT.MouseEnter, m_highlightAppender);
            m_highlightAppender.removeAddedListeners();
            event.doit = false;
            event.type = SWT.None;
            Widget selectedWidget = event.widget;
            IComponentIdentifier compId = null;

            if (!(selectedWidget instanceof FigureCanvas)) {
                sendIdInfo(compId);
                return;
            }

            FigureCanvas figureCanvas = (FigureCanvas)selectedWidget;
            Composite parent = figureCanvas;
            String testGefViewerDataKey = 
                    GefPartListener.TEST_GEF_VIEWER_DATA_KEY;
            while (parent != null && !(parent.getData(
                    testGefViewerDataKey) instanceof GraphicalViewer)) {
                parent = parent.getParent();
            }

            if (parent == null) {
                sendIdInfo(compId);
                return;
            }

            Object gefData = parent.getData(testGefViewerDataKey);
            if (gefData instanceof EditPartViewer) {
                EditPartViewer viewer = (EditPartViewer)gefData;
                Point cursorLocation = new Point(display.map(null,
                        viewer.getControl(),
                        display.getCursorLocation()));
                EditPart editPart = viewer.findObjectAt(cursorLocation);
                EditPart primaryEditPart = FigureCanvasUtil.getPrimaryEditPart(
                        editPart, viewer.getRootEditPart());
                List<String> idStringList = Collections.EMPTY_LIST;
                Map<String, String> properties = null;

                if (primaryEditPart != null) {
                    idStringList = getPathToRoot(viewer.getRootEditPart(),
                            cursorLocation, primaryEditPart);
                    if (primaryEditPart instanceof GraphicalEditPart) {
                        GraphicalEditPart gep = 
                                (GraphicalEditPart) primaryEditPart;
                        properties = PropertyUtil.getMapOfComponentProperties(
                                FigureCanvasUtil.findFigure(gep));
                    }
                } else {
                    // No primary figure found.
                    // Check whether a tool was selected.
                    EditDomain editDomain = viewer.getEditDomain();
                    if (editDomain != null) {
                        PaletteViewer paletteViewer =
                            editDomain.getPaletteViewer();
                        if (paletteViewer != null) {
                            EditPart paletteEditPart =
                                paletteViewer.findObjectAt(new Point(
                                        display.map(viewer.getControl(),
                                                paletteViewer.getControl(),
                                                cursorLocation.getSWTPoint())));
                            if (paletteEditPart != null) {
                                idStringList = getToolPathToRoot(
                                        paletteViewer.getRootEditPart(),
                                        paletteEditPart);
                            }
                        }
                    }
                }
                compId = createCompId(idStringList, properties);
            }
            sendIdInfo(compId);
        }

        /**
         *
         * @param editPart The edit part for which to find the path.
         * @param root The root for <code>editPart</code>. This is used to
         *             avoid adding the root identifier to the returned list.
         * @param cursorLocation The location to check for nearby connection
         *                       anchors.
         * @return a list containing the identifier of each edit part between
         *         <code>editPart</code> and its root. The first element in the
         *         list will be the identifier for a connection anchor if
         *         <code>cursorLocation</code> is near such an anchor.
         */
        private List<String> getPathToRoot(RootEditPart root,
                Point cursorLocation, EditPart editPart) {

            List<String> idStringList = new ArrayList<String>();
            EditPart currentEditPart = editPart;

            // Check for connection anchor
            String connectionId =
                getConnectionAnchorId(
                    DefaultEditPartAdapterFactory.loadFigureIdentifier(
                            currentEditPart),
                    cursorLocation);

            if (connectionId == null
                    && currentEditPart instanceof ConnectionEditPart) {

                ConnectionEditPart connEditPart =
                    (ConnectionEditPart)editPart;
                EditPart srcEditPart = connEditPart.getSource();
                EditPart targetEditPart = connEditPart.getTarget();
                connectionId = getConnectionAnchorId(
                        DefaultEditPartAdapterFactory
                        .loadFigureIdentifier(srcEditPart),
                        cursorLocation);
                if (connectionId != null) {
                    currentEditPart = srcEditPart;
                } else {
                    connectionId = getConnectionAnchorId(
                            DefaultEditPartAdapterFactory
                            .loadFigureIdentifier(targetEditPart),
                            cursorLocation);
                    if (connectionId != null) {
                        currentEditPart = targetEditPart;
                    }
                }
            }

            if (connectionId != null) {
                idStringList.add(connectionId);
            }
            while (currentEditPart != root.getContents()
                    && currentEditPart != null) {
                IEditPartIdentifier identifier = DefaultEditPartAdapterFactory
                        .loadFigureIdentifier(currentEditPart);
                if (currentEditPart instanceof ConnectionEditPart) {
                    while (currentEditPart instanceof ConnectionEditPart) {
                        ConnectionEditPart connection =
                                (ConnectionEditPart) currentEditPart;
                        IEditPartIdentifier connectionIdentifier =
                                DefaultEditPartAdapterFactory
                                        .loadFigureIdentifier(connection);
                        idStringList.add(connectionIdentifier.getIdentifier());
                        Entry<String, ConnectionAnchor> anchorAndName =
                                FigureCanvasUtil
                                        .getConnectionAnchor(connection);
                        if (anchorAndName != null) {
                            idStringList.add(anchorAndName.getKey());
                        }
                        EditPart source = connection.getSource();
                        IEditPartIdentifier sourceIdentifier =
                                DefaultEditPartAdapterFactory
                                        .loadFigureIdentifier(source);
                        idStringList.add(sourceIdentifier.getIdentifier());
                        currentEditPart = source;
                    }

                } else if (identifier != null) {
                    idStringList.add(identifier.getIdentifier());
                }
                currentEditPart = currentEditPart.getParent();
            }

            return idStringList;

        }

        /**
         *
         * @param editPart The edit part for which to find the path.
         * @param root The root for <code>editPart</code>. This is used to
         *             avoid adding the root identifier to the returned list.
         * @return a list containing the identifier of each edit part between
         *         <code>editPart</code> and its root.
         */
        private List<String> getToolPathToRoot(RootEditPart root,
                EditPart editPart) {

            List<String> idStringList = new ArrayList<String>();
            EditPart currentEditPart = editPart;

            if (currentEditPart != null) {
                Object model = currentEditPart.getModel();
                while (model instanceof PaletteEntry
                        && currentEditPart != root.getContents()) {
                    idStringList.add(((PaletteEntry)model).getLabel());
                    currentEditPart =
                        currentEditPart.getParent();
                    model = currentEditPart.getModel();
                }
            }

            return idStringList;
        }

        /**
         * Sends the given ID information to the client.
         *
         * @param compId The component identifier to send. May be
         *               <code>null</code>.
         */
        private void sendIdInfo(IComponentIdentifier compId) {
            InspectorComponentSelectedMessage message =
                new InspectorComponentSelectedMessage();
            message.setComponentIdentifier(compId);
            try {
                AUTServer.getInstance().getCommunicator().send(message);
            } catch (CommunicationException e) {
                LOG.error("Error occurred while trying to send message to Client.", e); //$NON-NLS-1$
            }
        }

        /**
         *
         * @param idStringList
         *            The path to root for a specific edit part or connection
         *            anchor.
         * @param properties
         *            the properties
         * @return a component identifier for the given path, or
         *         <code>null</code> if no valid component identifier can be
         *         generated.
         */
        private IComponentIdentifier createCompId(List<String> idStringList,
                Map<String, String> properties) {
            IComponentIdentifier compId = null;
            if (!idStringList.isEmpty()) {
                Collections.reverse(idStringList);
                compId = new ComponentIdentifier();
                compId.setHierarchyNames(idStringList);
                compId.setComponentPropertiesMap(properties);
            }

            return compId;
        }
    }

    /** the listener responsible for appending highlight listeners */
    private FigureHighlightAppender m_highlightAppender;

    /** the listener responsible for handling mouse clicks */
    private GefInspectorComponentSelectedListener m_componentSelectedListener;

    /**
     * Constructor
     */
    public GefInspectorListenerAppender() {
        m_highlightAppender = new FigureHighlightAppender();
        m_componentSelectedListener =
            new GefInspectorComponentSelectedListener(m_highlightAppender);
    }

    /**
     * {@inheritDoc}
     */
    public void addAutListener() {
        final Display display = PlatformUI.getWorkbench().getDisplay();

        display.syncExec(new Runnable() {

            public void run() {
                display.addFilter(SWT.MouseEnter, m_highlightAppender);
                display.addFilter(SWT.MouseDown, m_componentSelectedListener);
            }

        });
    }

}
