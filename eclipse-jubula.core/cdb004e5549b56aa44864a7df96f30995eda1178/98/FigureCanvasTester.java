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
package org.eclipse.jubula.rc.rcp.e3.gef.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.WidgetTester;
import org.eclipse.jubula.rc.common.util.Comparer;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.MenuUtilBase;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.rcp.e3.gef.factory.DefaultEditPartAdapterFactory;
import org.eclipse.jubula.rc.rcp.e3.gef.identifier.IEditPartIdentifier;
import org.eclipse.jubula.rc.rcp.e3.gef.identifier.IDirectionalEditPartAnchorIdentifier;
import org.eclipse.jubula.rc.rcp.e3.gef.listener.GefPartListener;
import org.eclipse.jubula.rc.rcp.e3.gef.util.FigureCanvasUtil;
import org.eclipse.jubula.rc.swt.driver.DragAndDropHelperSwt;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.swt.tester.adapter.ControlAdapter;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.toolkit.enums.ValueSets.AnchorType;
import org.eclipse.jubula.toolkit.enums.ValueSets.Unit;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * Implementation class for Figure Canvas (Eclipse GEF).
 *
 * @author BREDEX GmbH
 * @created May 13, 2009
 */
public class FigureCanvasTester extends WidgetTester {    
    /**
     * the viewer that contains the EditParts corresponding to the FigureCanvas
     */
    private GraphicalViewer m_viewer = null;
    
    /**
     *
     * @return the viewer associated with the canvas to test.
     */
    private GraphicalViewer getViewer() {
        return m_viewer;
    }

    /**
     *
     * @return the control for the canvas to test.
     */
    private Control getViewerControl() {
        return getViewer().getControl();
    }

    /**
     *
     * @return the root edit part of the viewer.
     */
    private RootEditPart getRootEditPart() {
        return getViewer().getRootEditPart();
    }

    /**
     *
     * @return the root of the palette viewer (tool palette).
     */
    private RootEditPart getPaletteRoot() {
        return getViewer().getEditDomain().getPaletteViewer().getRootEditPart();
    }

    /**
     *
     * @param textPath The path to the tool.
     * @param operator The operator used for matching.
     * @return the EditPart found at the end of the given path. Returns
     *         <code>null</code> if no EditPart can be found for the given path
     *         or if the EditPart found is not a GraphicalEditPart.
     */
    private GraphicalEditPart findPaletteEditPart(
            String textPath, String operator) {

        final String[] pathItems = MenuUtilBase.splitPath(textPath);
        boolean isExisting = true;

        EditPart currentEditPart = getPaletteRoot().getContents();

        for (int i = 0; i < pathItems.length && currentEditPart != null; i++) {
            List<?> effectiveChildren = currentEditPart.getChildren();

            EditPart [] children =
                effectiveChildren.toArray(
                    new EditPart[effectiveChildren.size()]);
            boolean itemFound = false;
            for (int j = 0; j < children.length && !itemFound; j++) {
                Object model = children[j].getModel();
                if (model instanceof PaletteEntry) {
                    String entryLabel = ((PaletteEntry)model).getLabel();
                    if (entryLabel != null
                        && MatchUtil.getInstance().match(
                            entryLabel, pathItems[i], operator)) {
                        itemFound = true;
                        currentEditPart = children[j];
                    }
                }
            }
            if (!itemFound) {
                isExisting = false;
                break;
            }

        }

        return isExisting && currentEditPart instanceof GraphicalEditPart
            ? (GraphicalEditPart)currentEditPart : null;

    }

    /**
     * {@inheritDoc}
     */
    public void setComponent(final Object graphicsComponent) {        
        Composite composite = (Composite) new RobotFactoryConfig()
            .getRobotFactory().getEventThreadQueuer()
                .invokeAndWait(getClass().getName() + ".setComponent", new IRunnable() { //$NON-NLS-1$

                    public Object run() throws StepExecutionException {
                        FigureCanvas figureCanvas = 
                                (FigureCanvas)graphicsComponent;
                        Composite parent = figureCanvas;
                        while (parent != null
                                && !(parent.getData(
                                       GefPartListener
                                           .TEST_GEF_VIEWER_DATA_KEY)
                                                instanceof GraphicalViewer)) {
                            parent = parent.getParent();
                        }
        
                        if (parent != null) {
                            m_viewer =
                                (GraphicalViewer)parent.getData(
                                        GefPartListener
                                            .TEST_GEF_VIEWER_DATA_KEY);
                            return parent;
                        }
                        return null;
                    }

                });
        setAdapter(new ControlAdapter(composite));
    }

    /**
     * Checks whether the figure for the EditPart for the given path exists and
     * is visible.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @param exists   Whether the figure is expected to exist.
     * @param timeout the time to wait for the status to occur
     */
    public void rcCheckFigureExists(
            final String textPath, final String operator,
            final boolean exists, int timeout) {
        invokeAndWait("rcCheckFigureExists", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                boolean isExisting = FigureCanvasUtil.
                        findFigure(findEditPart(textPath, operator)) != null;
                if (!isExisting) {
                    // See if there's a connection anchor at the given path
                    isExisting = findConnectionAnchor(textPath, operator) 
                            != null;
                }
                
                Verifier.equals(exists, isExisting);
            }
        });
    }

    /**
     * Checks the given property of the figure at the given path.
     *
     * @param textPath The path to the figure.
     * @param textPathOperator The operator used for matching the text path.
     * @param propertyName The name of the property
     * @param expectedPropValue The value of the property as a string
     * @param valueOperator The operator used to verify
     * @param timeout the time to wait for the status to occur
     */
    public void rcVerifyFigureProperty(final String textPath,
            final String textPathOperator, final String propertyName,
            final String expectedPropValue, final String valueOperator,
            int timeout) {
        invokeAndWait("rcVerifyFigureProperty", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                IFigure figure = FigureCanvasUtil.
                        findFigure(findEditPart(textPath, textPathOperator));
                nullCheckFigure(figure);
                String propToStr = getPropertyFromFigure(propertyName, figure);
                Verifier.match(propToStr, expectedPropValue, valueOperator);
            }
        });
    }
    
    /**
     * 
     * @param figure the figure to check
     * @throws StepExecutionException if figure is <code>null</code>
     */
    private void nullCheckFigure(IFigure figure) {
        if (figure == null) {
            throw new StepExecutionException(
                    "No figure could be found for the given text path.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.NOT_FOUND));
        }
    }

    /**
     * Checks whether the tool for the given path exists and
     * is visible.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @param exists   Whether the figure is expected to exist.
     * @param timeout the time to wait for the status to occur
     */
    public void rcCheckToolExists(final String textPath, final String operator,
            final boolean exists, int timeout) {
        invokeAndWait("rcCheckToolExists", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                boolean isExisting = 
                        findPaletteFigure(textPath, operator) != null;
                
                Verifier.equals(exists, isExisting);
            }
        });
    }

    /**
     * Finds and clicks the figure for the given path.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @param count The number of times to click.
     * @param button The mouse button to use for the click.
     */
    public void rcClickFigure(String textPath, String operator,
            int count, int button) {

        IFigure figure = FigureCanvasUtil.findFigure(
                findEditPart(textPath, operator));
        if (figure == null) {
            // it might be a ConnectionAnchor
            getRobot().click(getViewerControl(),
                    getFigureBoundsChecked(textPath, operator),
                    ClickOptions.create().setScrollToVisible(false)
                        .setClickCount(count).setMouseButton(button));
            return;
        }
        clickFigure(count, button, figure);
        
    }

    /**
     * Finds and clicks on a connection between a source figure and a
     * target figure.
     *
     * @param sourceTextPath The path to the source figure.
     * @param sourceOperator The operator to use for matching the source
     *                       figure path.
     * @param targetTextPath The path to the target figure.
     * @param targetOperator The operator to use for matching the target
     *                       figure path.
     * @param count The number of times to click.
     * @param button The mouse button to use for the click.
     */
    public void rcClickConnection(String sourceTextPath, String sourceOperator,
            String targetTextPath, String targetOperator,
            int count, int button) {

        IFigure connectionFigure = getConnectionFigure(sourceTextPath,
                sourceOperator, targetTextPath, targetOperator);
        nullCheckFigure(connectionFigure);
        clickFigure(count, button, connectionFigure);
    }

    /**
     * 
     * @param count The number of times to click.
     * @param button The mouse button to use for the click.
     * @param figure the figure to click
     */
    private void clickFigure(int count, int button, IFigure figure) {
        ClickOptions clickOptions = ClickOptions.create()
                .setScrollToVisible(false).setClickCount(count)
                .setMouseButton(button);
        if (figure instanceof Connection) {
            Point midpoint = ((Connection) figure).getPoints()
                    .getMidpoint();
            figure.translateToAbsolute(midpoint);
            getRobot().click(getViewerControl(), null, clickOptions, midpoint.x,
                    true, midpoint.y, true);
        } else {
            getRobot().click(getViewerControl(), getBounds(figure),
                    clickOptions);
        }
    }

    /**
     * Gets the {@link IFigure} from a {@link ConnectionEditPart} if there is
     * one between the {@link EditPart} given by the source and target path.
     * 
     * @param sourceTextPath
     *            the source path to an {@link EditPart} or its
     *            {@link ConnectionAnchor}
     * @param sourceOperator
     *            the source operator
     * @param targetTextPath
     *            the target path to an {@link EditPart} or its
     *            {@link ConnectionAnchor}
     * @param targetOperator
     *            the target operator
     * @return a {@link I Figure} from the found {@link ConnectionEditPart} or
     *         {@link StepExecutionException} will occur.
     */
    private IFigure getConnectionFigure(String sourceTextPath,
            String sourceOperator, String targetTextPath,
            String targetOperator) {
        ConnectionAnchor sourceConnectionAnchor = findConnectionAnchor(
                sourceTextPath, sourceOperator);
        GraphicalEditPart sourceEditPart = getPartWithAnchor(sourceTextPath,
                sourceOperator, sourceConnectionAnchor != null);

        ConnectionAnchor targetConnectionAnchor = findConnectionAnchor(
                targetTextPath, targetOperator);
        GraphicalEditPart targetEditPart = getPartWithAnchor(targetTextPath,
                targetOperator, targetConnectionAnchor != null);

        ConnectionEditPart connectionEditPart = null;

        if (sourceEditPart != null) {
            ConnectionEditPart[] sourceConnections =
                    getSourceConnectionEditParts(sourceEditPart);
            for (int i = 0; i < sourceConnections.length; i++) {
                if (sourceConnections[i].getTarget() == targetEditPart) {
                    ConnectionEditPart connection = checkConnectionWithAnchor(
                            sourceConnections[i], sourceConnectionAnchor,
                            targetConnectionAnchor);
                    if (connection != null) {
                        connectionEditPart = connection;
                        break;
                    }
                }
            }
        } else if (targetEditPart != null) {

            ConnectionEditPart[] targetConnections =
                    getTargetConnectionEditParts(targetEditPart);
            for (int i = 0; i < targetConnections.length
                    && connectionEditPart == null; i++) {
                if (targetConnections[i].getSource() == targetEditPart) {
                    ConnectionEditPart connection = checkConnectionWithAnchor(
                            targetConnections[i],
                            sourceConnectionAnchor, targetConnectionAnchor);
                    if (connection != null) {
                        connectionEditPart = connection;
                        break;
                    }
                }
            }
        } else {
            throw new StepExecutionException(
                    "No figures could be found for the given text paths.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }

        IFigure connectionFigure = FigureCanvasUtil
                .findFigure(connectionEditPart);
        if (connectionFigure == null) {
            String missingEnd = sourceEditPart == null ? "source" : "target"; //$NON-NLS-1$ //$NON-NLS-2$
            throw new StepExecutionException(
                    "No connection could be found for the given " + missingEnd + " figure.", //$NON-NLS-1$ //$NON-NLS-2$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }

        // Scrolling
        revealEditPart(connectionEditPart);
        return connectionFigure;
    }

    /**
     * Clicks the specified position within the given figure.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @param count amount of clicks
     * @param button what button should be clicked
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException if step execution fails.
     */
    public void rcClickInFigure(String textPath, String operator,
        int count, int button, int xPos, String xUnits,
        int yPos, String yUnits) throws StepExecutionException {

        getRobot().click(getViewerControl(),
                getFigureBoundsChecked(textPath, operator),
                ClickOptions.create().setScrollToVisible(false)
                    .setClickCount(count).setMouseButton(button),
                xPos, xUnits.equalsIgnoreCase(
                        Unit.pixel.rcValue()),
                yPos, yUnits.equalsIgnoreCase(
                        Unit.pixel.rcValue()));
    }

    /**
     * Simulates the beginning of a Drag. Moves to the specified position
     * within the given figure and stores information related to the drag to
     * be used later by a Drop operation.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @param mouseButton the mouse button.
     * @param modifier the modifier, e.g. shift, ctrl, etc.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     */
    public void rcDragFigure(String textPath, String operator,
            int mouseButton, String modifier, int xPos,
            String xUnits, int yPos, String yUnits) {
        // Only store the Drag-Information. Otherwise the GUI-Eventqueue
        // blocks after performed Drag!
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        rcClickInFigure(textPath, operator, 0, mouseButton,
                xPos, xUnits, yPos, yUnits);
    }

    /**
     * Performs a Drop. Moves to the specified location within the given figure
     * and releases the modifier and mouse button pressed by the previous drag
     * operation.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void rcDropOnFigure(final String textPath, final String operator,
            final int xPos, final String xUnits, final int yPos,
            final String yUnits, int delayBeforeDrop) {

        final DragAndDropHelperSwt dndHelper =
            DragAndDropHelperSwt.getInstance();
        final IRobot robot = getRobot();
        final String modifier = dndHelper.getModifier();
        final int mouseButton = dndHelper.getMouseButton();
        // Note: This method performs the drag AND drop action in one runnable
        // in the GUI-Eventqueue because after the mousePress, the eventqueue
        // blocks!
        try {
            pressOrReleaseModifiers(modifier, true);

            getEventThreadQueuer().invokeAndWait("gdStartDragFigure", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null,
                            mouseButton);

                    CAPUtil.shakeMouse();

                    // drop
                    rcClickInFigure(textPath, operator, 0,
                            mouseButton, xPos, xUnits, yPos, yUnits);

                    return null;
                }
            });

            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, mouseButton);
            pressOrReleaseModifiers(modifier, false);
        }
    }

    /**
     * Returns the bounds for the figure for the given path. If no such
     * figure can be found, a {@link StepExecutionException} will be thrown.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @return the bounds of the figure for the given path.
     */
    private Rectangle getFigureBoundsChecked(String textPath, String operator) {
        GraphicalEditPart editPart =
            findEditPart(textPath, operator);
        IFigure figure = FigureCanvasUtil.findFigure(editPart);
        ConnectionAnchor anchor = null;

        if (figure == null) {
            // Try to find a connection anchor instead
            anchor = findConnectionAnchor(textPath, operator);
            if (anchor != null) {
                final String[] pathItems = MenuUtilBase.splitPath(textPath);
                final String[] editPartPathItems =
                    new String[pathItems.length - 1];
                System.arraycopy(
                        pathItems, 0, editPartPathItems, 0,
                        editPartPathItems.length);
                editPart = findEditPart(operator, editPartPathItems);
            }
            if (anchor == null || FigureCanvasUtil
                    .findFigure(editPart) == null) {
                throw new StepExecutionException(
                        "No figure could be found for the given text path.", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
            }

        }

        // Scrolling
        revealEditPart(editPart);

        return figure != null ? getBounds(figure) : getBounds(anchor);
    }

    /**
     * Clicks the tool found at the given path.
     *
     * @param textPath The path to the tool.
     * @param operator The operator used for matching.
     * @param count The number of times to click.
     */
    public void rcSelectTool(String textPath, String operator, int count) {
        Control paletteControl = getPaletteControl();
        IFigure figure =
            findPaletteFigureChecked(textPath, operator);
        getRobot().click(paletteControl, getBounds(figure),
                ClickOptions.create().setScrollToVisible(false)
                    .setClickCount(count));
    }

    /**
     * @return the control associated with the palette viewer.
     */
    private Control getPaletteControl() {
        EditDomain editDomain = getViewer().getEditDomain();
        if (editDomain == null) {
            return null;
        }

        PaletteViewer paletteViewer = editDomain.getPaletteViewer();
        if (paletteViewer == null) {
            return null;
        }

        return paletteViewer.getControl();
    }

    /**
     *
     * @param figure The figure for which to find the bounds.
     * @return the bounds of the given figure.
     */
    private Rectangle getBounds(IFigure figure) {
        org.eclipse.draw2d.geometry.Rectangle figureBounds =
            new org.eclipse.draw2d.geometry.Rectangle(figure.getBounds());

        // Take scrolling and zooming into account
        figure.translateToAbsolute(figureBounds);

        return new Rectangle(
                figureBounds.x, figureBounds.y,
                figureBounds.width, figureBounds.height);
    }

    /**
     *
     * @param anchor The anchor for which to find the bounds.
     * @return the "bounds" of the given anchor. Since the location of an
     *         anchor is defined as a single point, the bounds are a small
     *         rectangle with that point at the center.
     */
    private Rectangle getBounds(ConnectionAnchor anchor) {
        Validate.notNull(anchor);
        Point refPoint = anchor.getReferencePoint();

        return new Rectangle(
                refPoint.x - 1, refPoint.y - 1, 3, 3);
    }

    /**
     *
     * @param textPath The path to the GraphicalEditPart.
     * @param operator The operator used for matching.
     * @return the GraphicalEditPart for the given path. Returns
     *         <code>null</code> if no EditPart exists for the given path or if
     *         the found EditPart is not a GraphicalEditPart.
     */
    private GraphicalEditPart findEditPart(String textPath, String operator) {
        final String[] pathItems = MenuUtilBase.splitPath(textPath);
        return findEditPart(operator, pathItems);
    }

    /**
     * @param operator The operator used for matching.
     * @param pathItems The path to the GraphicalEditPart. Each element in the
     *                  array represents a single segment of the path.
     * @return the GraphicalEditPart for the given path. Returns
     *         <code>null</code> if no EditPart exists for the given path or if
     *         the found EditPart is not a GraphicalEditPart.
     */
    private GraphicalEditPart findEditPart(String operator,
            final String[] pathItems) {
        boolean isExisting = true;
        EditPart currentEditPart = getRootEditPart().getContents();

        for (int i = 0; i < pathItems.length && currentEditPart != null; i++) {
            List<EditPart> childrens =
                    new ArrayList<EditPart>(currentEditPart.getChildren());
            if (currentEditPart instanceof GraphicalEditPart) {
                GraphicalEditPart graph = (GraphicalEditPart) currentEditPart;
                childrens.addAll(graph.getSourceConnections());
            }
            EditPart[] children =
                    childrens.toArray(new EditPart[childrens.size()]);
            boolean itemFound = false;
            for (int j = 0; j < children.length && !itemFound; j++) {
                IEditPartIdentifier childFigureIdentifier =
                        DefaultEditPartAdapterFactory
                                .loadFigureIdentifier(children[j]);
                if (children[j] instanceof ConnectionEditPart) {
                    ConnectionEditPart connection =
                            (ConnectionEditPart) children[j];
                    Entry<String, ConnectionAnchor> anchorWithIdentifier =
                            FigureCanvasUtil.getConnectionAnchor(connection);
                    if (anchorWithIdentifier != null && MatchUtil.getInstance()
                            .match(anchorWithIdentifier.getKey(), pathItems[i],
                            operator)) {
                        if (childFigureIdentifier != null) {
                            String figureId =
                                    childFigureIdentifier.getIdentifier();
                            if (figureId != null && !(pathItems.length < i + 2)
                                    && MatchUtil.getInstance().match(figureId,
                                            pathItems[i + 1], operator)) {
                                itemFound = true;
                                currentEditPart = children[j];
                                i++;
                            }
                        }
                    }

                } else if (childFigureIdentifier != null) {
                    String figureId = childFigureIdentifier.getIdentifier();
                    if (figureId != null && MatchUtil.getInstance()
                            .match(figureId, pathItems[i], operator)) {
                        itemFound = true;
                        currentEditPart = children[j];
                    }
                }
            }
            if (!itemFound) {
                isExisting = false;
                break;
            }

        }

        return isExisting && currentEditPart instanceof GraphicalEditPart
                ? (GraphicalEditPart) currentEditPart : null;
    }

    /**
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @return the figure for the GraphicalEditPart for the given path within
     *         the palette. Returns <code>null</code> if no EditPart exists
     *         for the given path or if the found EditPart does not have a
     *         figure.
     */
    private IFigure findPaletteFigure(String textPath, String operator) {
        GraphicalEditPart editPart = findPaletteEditPart(textPath, operator);

        // Scrolling
        revealEditPart(editPart);

        return FigureCanvasUtil.findFigure(editPart);
    }

    /**
     * Finds and returns the palette figure for the given path. If no such
     * figure can be found, a {@link StepExecutionException} will be thrown.
     *
     * @param textPath The path to the figure.
     * @param operator The operator used for matching.
     * @return the figure for the GraphicalEditPart for the given path within
     *         the palette.
     */
    private IFigure findPaletteFigureChecked(String textPath, String operator) {
        IFigure figure = findPaletteFigure(textPath, operator);
        if (figure == null) {
            throw new StepExecutionException(
                    "No palette figure could be found for the given text path.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        return figure;

    }

    /**
     * Attempts to find a connection anchor at the given textpath.
     *
     * @param textPath The path to the anchor.
     * @param operator The operator used for matching.
     * @return the anchor found at the given text path, or <code>null</code>
     *         if no such anchor exists.
     */
    private ConnectionAnchor findConnectionAnchor(
            String textPath, String operator) {

        final String[] pathItems = MenuUtilBase.splitPath(textPath);
        final String[] editPartPathItems = new String[pathItems.length - 1];
        System.arraycopy(
                pathItems, 0, editPartPathItems, 0, editPartPathItems.length);
        GraphicalEditPart editPart = findEditPart(operator, editPartPathItems);
        if (editPart != null) {
            String anchorPathItem = pathItems[pathItems.length - 1];
            IEditPartIdentifier editPartIdentifier =
                DefaultEditPartAdapterFactory.loadFigureIdentifier(editPart);
            if (editPartIdentifier != null) {
                Map<String, ConnectionAnchor> anchorMap =
                    editPartIdentifier.getConnectionAnchors();
                if (anchorMap != null) {
                    Iterator<String> anchorMapIter =
                        anchorMap.keySet().iterator();
                    while (anchorMapIter.hasNext()) {
                        Object anchorMapKey = anchorMapIter.next();
                        Object anchorMapValue =
                            anchorMap.get(anchorMapKey);
                        if (anchorMapKey instanceof String
                                && anchorMapValue instanceof ConnectionAnchor
                                && MatchUtil.getInstance().match(
                                    (String)anchorMapKey, anchorPathItem,
                                    operator)) {

                            return (ConnectionAnchor)anchorMapValue;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Reveals the given {@link EditPart} within its viewer.
     *
     * @param editPart the {@link EditPart} to reveal.
     */
    private void revealEditPart(final EditPart editPart) {
        if (editPart != null) {
            getEventThreadQueuer().invokeAndWait(getClass().getName() + ".revealEditPart", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    editPart.getViewer().reveal(editPart);
                    return null;
                }

            });
        }
    }
    
    /**
     * Finds and checks if a connection between a source figure and a
     * target figure exists.
     *
     * @param sourceTextPath The path to the source figure.
     * @param sourceOperator The operator to use for matching the source
     *                       figure path.
     * @param targetTextPath The path to the target figure.
     * @param targetOperator The operator to use for matching the target
     *                       figure path.
     * @param exists whether the connection is expected to exist.
     * @param timeout the time to wait for the status to occur
     */
    public void rcCheckConnectionExists(final String sourceTextPath,
            final String sourceOperator, final String targetTextPath,
            final String targetOperator, final boolean exists, int timeout) {
        invokeAndWait("rcCheckConnectionExists", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                boolean found = true;
                try {
                    getConnectionFigure(sourceTextPath,
                            sourceOperator, targetTextPath, targetOperator);
                } catch (StepExecutionException see) {
                    found = false;
                }
                Verifier.equals(exists, found);
            }
        });
    }

    /**
     * This is a helper method to find a {@link EditPart} which has an {@link ConnectionAnchor} as last path parameter.
     * If there is no {@link ConnectionAnchor} 
     * @param path the complete path including the {@code Anchor} part
     * @param operator the operator for the search
     * @param pathWithAnchor if there is an {@link ConnectionAnchor} found for this part
     * @return the {@link EditPart}
     */
    private GraphicalEditPart getPartWithAnchor(String path,
            String operator, boolean pathWithAnchor) {
        String[] pathItems = MenuUtilBase.splitPath(path);
        if (pathWithAnchor) {
            final String[] editPartPathItems = new String[pathItems.length - 1];
            System.arraycopy(pathItems, 0, editPartPathItems, 0,
                    editPartPathItems.length);
            pathItems = editPartPathItems; // strip of last part because we know
                                           // it is an anchor
        }
        return findEditPart(operator, pathItems);
    }

    /**
     * Checks if if the <code>connection</code> is connected to the
     * <code>sourceConnectionAnchor</code> and
     * <code>targetConnectionAnchor</code>. I there are no
     * {@link ConnectionAnchor} given the {@code connection} is returned.
     *
     * @param connection
     *            the {@link ConnectionEditPart} which should be checked if it
     *            is connected to the {@link ConnectionAnchor}a
     * @param sourceConnectionAnchor
     *            the source {@link ConnectionAnchor} which the
     *            {@link ConnectionEditPart} should be connected to. Could be
     *            <code>null</code> if there is no {@link ConnectionAnchor} or
     *            we are only checking if there is any connection between the
     *            {@link EditPart}
     * @param targetConnectionAnchor
     *            the target {@link ConnectionAnchor} which the
     *            {@link ConnectionEditPart} should be connected to. Could be
     *            <code>null</code> if there is no {@link ConnectionAnchor} or
     *            we are only checking if there is any connection between the
     *            {@link EditPart}
     * @return <code>null</code> or the <code>connection </code>
     */
    private ConnectionEditPart checkConnectionWithAnchor(
            ConnectionEditPart connection,
            ConnectionAnchor sourceConnectionAnchor,
            ConnectionAnchor targetConnectionAnchor) {
        EditPart source = connection.getSource();
        EditPart target = connection.getTarget();
        boolean isSourceCorrect = false;
        boolean isTargetCorrect = false;
        if (source instanceof NodeEditPart && sourceConnectionAnchor != null) {
            NodeEditPart node = (NodeEditPart) source;
            ConnectionAnchor anchor = node
                    .getSourceConnectionAnchor(
                            connection);
            if (sourceConnectionAnchor == anchor) {
                isSourceCorrect = true;
            }

        }
        if (target instanceof NodeEditPart && targetConnectionAnchor != null) {
            NodeEditPart node = (NodeEditPart) target;
            ConnectionAnchor anchor = node
                    .getTargetConnectionAnchor(
                            connection);
            if (targetConnectionAnchor == anchor) {
                isTargetCorrect = true;
            }
        }
        if (isSourceCorrect && isTargetCorrect
                || isSourceCorrect && (targetConnectionAnchor == null)
                || isTargetCorrect && (sourceConnectionAnchor == null)
                || (targetConnectionAnchor == null)
                    && (sourceConnectionAnchor == null)) {
            return connection;
        }
        return null;
        // this is still valid since there are simple connections
    }

    /**
     * 
     * @param textPath
     *            the textpath to the {@link GraphicalEditPart}
     * @param operator
     *            the operator to find the editpart should be a rcValue from
     *            {@link org.eclipse.jubula.toolkit.enums.ValueSets.Operator}
     * @param anchorType
     *            the anchor type should be a rc value from {@link AnchorType}
     * @param count
     *            the count to compare
     * @param comparisonMethod
     *            the comparison method should be a rc value from
     *            {@link org.eclipse.jubula.toolkit.enums.ValueSets.NumberComparisonOperator}
     * @param timeout 
     *            the time to wait for the status to occur
     */
    public void rcCheckNumberOfAnchors(final String textPath,
            final String operator, final String anchorType, 
            final int count, final String comparisonMethod, int timeout) {
        invokeAndWait("rcCheckNumbersOfAnchors", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                GraphicalEditPart editPart = findEditPart(textPath, operator);
                if (editPart == null) {
                    throw new StepExecutionException(
                            "No Edit Part could be found for the given text path.", //$NON-NLS-1$
                            EventFactory.createActionError(
                                    TestErrorEvent.NOT_FOUND));
                }
                IEditPartIdentifier editPartIdentifier =
                        DefaultEditPartAdapterFactory
                                .loadFigureIdentifier(editPart);
                int connectionCount = 0;

                if (anchorType.equals(AnchorType.both.rcValue())) {
                    connectionCount =
                            editPartIdentifier.getConnectionAnchors().size();
                } else if (editPartIdentifier 
                        instanceof IDirectionalEditPartAnchorIdentifier) {
                    IDirectionalEditPartAnchorIdentifier extended =
                            (IDirectionalEditPartAnchorIdentifier) 
                            editPartIdentifier;

                    if (anchorType.equals(AnchorType.incoming.rcValue())) {
                        connectionCount =
                                extended.getIncomingConnectionAnchors().size();
                    } else if (anchorType
                            .equals(AnchorType.outgoing.rcValue())) {
                        connectionCount =
                                extended.getOutgoingConnectionAnchors().size();
                    }
                } else {
                    throw new StepExecutionException(
                            "GraphicalEditPart does not support the anchor type" //$NON-NLS-1$
                                    + anchorType,
                            EventFactory.createActionError());
                }
                Comparer.compare(Integer.toString(connectionCount),
                        Integer.toString(count), comparisonMethod);
            }
        });
    }

    /**
     *
     * @param textPath
     *            the textpath to the {@link ConnectionAnchor}
     * @param operator
     *            the operator to find the editpart should be a rcValue from
     *            {@link org.eclipse.jubula.toolkit.enums.ValueSets.Operator}
     * @param hasConnection
     *            if the anchor has a connection or not
     * @param timeout 
     *            the time to wait for the status to occur
     */
    public void rcCheckAnchorConnection(final String textPath,
            final String operator, final boolean hasConnection,
            int timeout) {
        invokeAndWait("rcCheckAnchorConnection", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                ConnectionAnchor anchor =
                        findConnectionAnchor(textPath, operator);
                if (anchor == null) {
                    throw new StepExecutionException(
                            "No Anchor could be found for the given text path.", //$NON-NLS-1$
                            EventFactory.createActionError(
                                    TestErrorEvent.NOT_FOUND));
                }
                GraphicalEditPart anchorEditPart =
                        getPartWithAnchor(textPath, operator, true);

                ConnectionEditPart[] sourceConnections =
                        getSourceConnectionEditParts(anchorEditPart);
                ConnectionEditPart[] targetConnections =
                        getTargetConnectionEditParts(anchorEditPart);

                boolean connection = false;
                for (int i = 0; i < sourceConnections.length; i++) {
                    ConnectionEditPart connectiontest =
                            checkConnectionWithAnchor(sourceConnections[i],
                                    anchor, null);
                    if (connectiontest != null) {
                        connection = true;
                        break;
                    }
                }
                if (!connection) {
                    for (int i = 0; i < targetConnections.length; i++) {
                        ConnectionEditPart connectiontest =
                                checkConnectionWithAnchor(targetConnections[i],
                                        null, anchor);
                        if (connectiontest != null) {
                            connection = true;
                            break;
                        }
                    }
                }
                Verifier.equals(hasConnection, connection);
            }
        });
    }

    /**
     * gets an array of connections which the {@link GraphicalEditPart} is the source of
     * @param editPart the edit part
     * @return the connections which are coming from the edit part
     */
    private ConnectionEditPart[] getSourceConnectionEditParts(
            GraphicalEditPart editPart) {
        List<?> sourceConnectionList = editPart
                .getSourceConnections();
        ConnectionEditPart[] sourceConnections = sourceConnectionList
                .toArray(new ConnectionEditPart[sourceConnectionList
                                                .size()]);
        return sourceConnections;
    }

    /**
     * gets an array of connections which target the {@link GraphicalEditPart}
     * @param editPart the edit part
     * @return the connections which are going to another edit part
     */
    private ConnectionEditPart[] getTargetConnectionEditParts(
            GraphicalEditPart editPart) {
        List<?> targetConnectionList = editPart
                .getTargetConnections();
        ConnectionEditPart[] targetConnections = targetConnectionList
                .toArray(new ConnectionEditPart[targetConnectionList
                                                .size()]);
        return targetConnections;
    }
    
    /**
     * Finds and checks if a connection between a source figure and a
     * target figure exists.
     *
     * @param sourceTextPath The path to the source figure.
     * @param sourceOperator The operator to use for matching the source
     *                       figure path.
     * @param targetTextPath The path to the target figure.
     * @param targetOperator The operator to use for matching the target
     *                       figure path.
     * @param propertyName The name of the property
     * @param expectedPropValue The value of the property as a string
     * @param valueOperator The operator used to verify
     * @param timeout the time to wait for the status to occur
     */
    public void rcVerifyConnectionProperty(final String sourceTextPath,
            final String sourceOperator, final String targetTextPath,
            final String targetOperator, final String propertyName,
            final String expectedPropValue, final String valueOperator,
            int timeout) {
        invokeAndWait("rcVerifyConnectionProperty", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                final IFigure figure = getConnectionFigure(sourceTextPath,
                        sourceOperator, targetTextPath, targetOperator);        
                final String propToStr = 
                        getPropertyFromFigure(propertyName, figure);
                Verifier.match(propToStr, expectedPropValue, valueOperator);
            }
        });
    }

    /**
     * 
     * @param propertyName The name of the property
     * @param figure The figure we want the property from
     * @return The property in its String representation
     */
    private String getPropertyFromFigure(final String propertyName,
            final IFigure figure) {
        Object prop = getEventThreadQueuer().invokeAndWait("getProperty",  //$NON-NLS-1$
            new IRunnable<Object>() {

                public Object run() throws StepExecutionException {
                    try {
                        return PropertyUtils.getProperty(figure, propertyName);
                    } catch (IllegalAccessException e) {
                        throw new StepExecutionException(
                            e.getMessage(),
                            EventFactory.createActionError(
                                TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
                    } catch (InvocationTargetException e) {
                        throw new StepExecutionException(
                            e.getMessage(),
                            EventFactory.createActionError(
                                TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
                    } catch (NoSuchMethodException e) {
                        throw new StepExecutionException(
                            e.getMessage(),
                            EventFactory.createActionError(
                                TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
                    }
                }

            });
        final String propToStr = String.valueOf(prop);
        return propToStr;
    }
}
