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
package org.eclipse.jubula.rc.javafx.driver;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.swing.UIManager;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.ClickOptions.ClickModifier;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IMouseMotionTracker;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;
import org.eclipse.jubula.rc.common.driver.KeyTyper;
import org.eclipse.jubula.rc.common.driver.MouseMovementStrategy;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.util.LocalScreenshotUtil;
import org.eclipse.jubula.rc.common.util.PointUtil;
import org.eclipse.jubula.rc.common.util.PropertyUtil;
import org.eclipse.jubula.rc.javafx.components.CurrentStages;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.Rounding;
import org.eclipse.jubula.rc.javafx.tester.util.compatibility.KeyCodeUtil;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * <p>
 * JavaFX implementation but similar to the AWT/Swing implementation of the
 * <code>IRobot</code> interface. It uses the {@link java.awt.Robot}to move the
 * mouse and perform clicks. Any mouse move or click is intercepted and
 * confirmed using the appropriate AWT/Swing implementations of
 * {@link org.eclipse.jubula.rc.swing.driver.IRobotEventInterceptor}and
 * {@link org.eclipse.jubula.rc.swing.driver.IRobotEventConfirmer}.
 * </p>
 *
 * <p>
 * The <code>click()</code> and <code>move()</code> implementations expect that
 * the graphics component is of type {@link java.awt.Component}and the
 * constraints object is <code>null</code> or of type {@link java.awt.Rectangle}
 * .
 * </p>
 *
 * @author BREDEX GmbH
 * @created 31.10.2013
 */
public class RobotJavaFXImpl implements IRobot<Rectangle> {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            RobotJavaFXImpl.class);
    /** ID of Metal Look and Feel */
    private static final String METAL_LAF_ID = "Metal"; //$NON-NLS-1$
    /** The AWT Robot instance. */
    private Robot m_robot;
    /** The event interceptor. */
    private RobotEventInterceptorJavaFXImpl m_interceptor;
    /** The mouse motion tracker. */
    private IMouseMotionTracker m_mouseMotionTracker;
    /** The event thread queuer. */
    private IEventThreadQueuer m_queuer;

    /**
     * Scrolls to a component, to make it visible. Currently only ListView and
     * TableView are supported.
     */
    private class Scroller {
        /** The component to scroll to visible. */
        private Node m_component;

        /**
         * @param component
         *            The component to scroll to visible.
         */
        public Scroller(Node component) {
            m_component = component;
        }

        /**
         * Scrolls the component to visible.
         *
         * @param component
         *            the component to scroll to
         */
        private void scrollObjectToVisible(final Node component) {
            // scroll all parent scroll panes
            List<ScrollPane> panes2Scroll = new ArrayList<ScrollPane>();
            Parent p = component.getParent();
            while (p != null) {
                if (p instanceof ScrollPane) {
                    panes2Scroll.add((ScrollPane) p);
                }
                p = p.getParent();
            }
            
            // scroll inner panes before outer
            for (int i = 0; i < panes2Scroll.size(); i++) {
                ScrollPane nextPane = panes2Scroll.get(i);
                scrollToNode(nextPane, component);
            }
            
            Parent parent = component.getParent();
            Node scrollNode = component;
            for (; (parent != null) 
                    && !(parent instanceof ListView)
                    && !(parent instanceof TableView)
                    && !(parent instanceof TreeView)
                    && !(parent instanceof ScrollPane); 
                parent = parent.getParent()) {
                if (parent instanceof TreeCell) {
                    scrollNode = parent;
                }
            }
            if (parent instanceof ListView) {
                ((ListView<Node>) parent).scrollTo(scrollNode);
            } else if (parent instanceof TableView) {
                ((TableView<Node>) parent).scrollTo(scrollNode);
            } else if (parent instanceof TreeView) {
                if (scrollNode instanceof TreeCell) {
                    final TreeView<?> treeView = (TreeView<?>) parent;
                    treeView.scrollTo(treeView.getRow(
                        ((TreeCell) scrollNode).getTreeItem()));
                }
            }
        }

        /**
         * @param sPane
         *            the scroll pane to scroll within
         * @param scrollNode
         *            the node to scroll to
         */
        private void scrollToNode(final ScrollPane sPane, 
            final Node scrollNode) {
            final Node sPaneContent = sPane.getContent();

            if (scrollNode == sPaneContent) {
                return;
            }
            
            Bounds nodeInScrollPaneContent = scrollNode.getBoundsInLocal();
            // translate local node bounds to ScrollPane content node relative bounds
            Node currentNode = scrollNode;
            do {
                Parent nextParent = currentNode.getParent();

                boolean cornerCase = false;
                if (nextParent instanceof Group) {
                    // BEGIN: skip skins in between
                    Parent parentLookup = nextParent.getParent();
                    while (parentLookup != null
                            && !(parentLookup instanceof ScrollPane)) {
                        parentLookup = parentLookup.getParent();
                    }
                    // END: skip skins in between
                    
                    // there is a corner case if:
                    // - a group is the direct content node of a scroll pane
                    if (parentLookup != null) {
                        ScrollPane potentialGroupParent =
                                (ScrollPane) parentLookup;
                        if (potentialGroupParent.getContent() == nextParent) {
                            cornerCase = true;
                        }
                    }
                }

                if (!cornerCase) {
                    nodeInScrollPaneContent = currentNode
                            .localToParent(nodeInScrollPaneContent);
                }
                
                currentNode = nextParent;
            } while (currentNode != sPaneContent);

            // determine left upper corner of node to scroll to
            final double nodeX = nodeInScrollPaneContent.getMinX();
            final double nodeY = nodeInScrollPaneContent.getMinY();

            // determine scrolling scaling factor - defaults to 1.0
            double hmin = sPane.getHmin();
            final double scaleH = sPane.getHmax() - hmin;
            double vmin = sPane.getVmin();
            final double scaleV = sPane.getVmax() - vmin;

            // determine the actually scrollable distance in x and y direction
            final Bounds viewPortBounds = sPane.getViewportBounds();
            final Bounds contentBounds = sPaneContent.getBoundsInLocal();
            final double actuallyScrollableHDistance = 
                contentBounds.getWidth() - viewPortBounds.getWidth();
            final double actuallyScrollableVDistance = 
                contentBounds.getHeight() - viewPortBounds.getHeight();
            
            // scroll ScrollPane programmatically
            sPane.setHvalue(hmin
                    + (nodeX / actuallyScrollableHDistance) * scaleH);
            sPane.setVvalue(vmin
                    + (nodeY / actuallyScrollableVDistance) * scaleV);
        }

        /**
         * Scrolls the component passed to the constructor to visible.
         *
         */
        public void scrollToVisible() {
            scrollObjectToVisible(m_component);
        }
    }

    /**
     * Creates a new instance.
     *
     * @param factory
     *            The Robot factory instance.
     * @throws RobotException
     *             If the AWT-Robot cannot be created.
     */
    public RobotJavaFXImpl(RobotFactoryJavaFXImpl factory) 
        throws RobotException {
        try {
            m_robot = new Robot();
            m_robot.setAutoWaitForIdle(true);
            m_robot.setAutoDelay(0);
        } catch (AWTException awte) {
            log.error(awte);
            m_robot = null;
            throw new RobotException(awte);
        } catch (SecurityException se) {
            log.error(se);
            m_robot = null;
            throw new RobotException(se);
        }
        m_interceptor = factory.getRobotEventInterceptor();
        m_mouseMotionTracker = factory.getMouseMotionTracker();
        m_queuer = factory.getEventThreadQueuer();
    }

    /**
     * Gets a location inside the component. If <code>offset</code> is
     * <code>null</code>, it returns the middle of the component otherwise it
     * adds the offset to the upper left corner.
     *
     * @param comp
     *            the component to get the location for
     * @param offset
     *            the offset
     * @throws IllegalArgumentException
     *             if <code>component</code> is null
     * @return the <b>global </b> coordinates of <code>component</code>
     */
    private Point getLocation(Node comp, final Point offset)
        throws IllegalArgumentException {

        Validate.notNull(comp, "component must not be null"); //$NON-NLS-1$

        Scene s = comp.getScene();
        s.getRoot().layout();

        Point2D pos = comp.localToScreen(0, 0);

        double x = pos.getX();
        double y = pos.getY();
        if (offset == null) {
            final Bounds boundsInParent = comp.getBoundsInParent();
            x += boundsInParent.getWidth() / 2;
            y += boundsInParent.getHeight() / 2;
        } else {
            x += offset.x;
            y += offset.y;
        }

        return new Point(Rounding.round(x), Rounding.round(y));
    }

    /**
     * Implementation of the mouse click. The mouse is moved into the graphics
     * component by calling <code>moveImpl()</code> before performing the click.
     *
     * @param graphicsComponent
     *            The graphics component to click on
     * @param constraints
     *            The constraints, must be a <code>java.awt.Rectangle</code> or
     *            <code>null</code>. The constraints are <em>relative</em> to
     *            the location/origin of the <code>graphicsComponent</code>.
     * @param clickOptions
     *            The click options
     * @param xPos
     *            xPos in component
     * @param yPos
     *            yPos in component
     * @param yAbsolute
     *            true if y-position should be absolute
     * @param xAbsolute
     *            true if x-position should be absolute
     * @throws RobotException
     *             If the click delay is interrupted or the event confirmation
     *             receives a timeout.
     */
    private void clickImpl(Object graphicsComponent, Rectangle constraints,
            ClickOptions clickOptions, int xPos, boolean xAbsolute, int yPos,
            boolean yAbsolute) throws RobotException {
        moveImpl(graphicsComponent, constraints, xPos, xAbsolute,
                yPos, yAbsolute, clickOptions);
        clickImpl(graphicsComponent, clickOptions);
    }

    /**
     * Clicks at the current mouse position.
     *
     * @param graphicsComp
     *            The component used for confirming the click.
     * @param clickOp
     *            Configuration for the click.
     */
    private void clickImpl(Object graphicsComp, ClickOptions clickOp) {

        int buttonMask = getButtonMask(clickOp.getMouseButton());
        int clickCount = clickOp.getClickCount();
        int[] modifierMask = getModifierMask(clickOp.getClickModifier());
        if (clickCount > 0) {
            IRobotEventConfirmer confirmer = null;
            if (clickOp.isConfirmClick()) {
                InterceptorOptions options = new InterceptorOptions(
                        new long[] { AWTEvent.MOUSE_EVENT_MASK });
                confirmer = m_interceptor.intercept(options);
            }
            try {
                pressModifier(modifierMask);
                RobotTiming.sleepPreClickDelay();

                for (int i = 0; i < clickCount; i++) {
                    m_robot.mousePress(buttonMask);
                    RobotTiming.sleepPostMouseDownDelay();

                    m_robot.mouseRelease(buttonMask);
                    RobotTiming.sleepPostMouseUpDelay();
                }
                if (confirmer != null) {
                    confirmer.waitToConfirm(null,
                            new ClickJavaFXEventMatcher(clickOp));
                }
            } finally {
                releaseModifier(modifierMask);
            }
        }
    }

    /**
     * @param modifierMask
     *            array of modifiers to press before click
     */
    private void pressModifier(int[] modifierMask) {
        for (int i = 0; i < modifierMask.length; i++) {
            keyPress(null, modifierMask[i]);
        }
    }

    /**
     * @param modifierMask
     *            array of modifiers release after click
     */
    private void releaseModifier(int[] modifierMask) {
        for (int i = 0; i < modifierMask.length; i++) {
            keyRelease(null, modifierMask[i]);
        }
    }

    /**
     * @param clickModifier
     *            the click modifier to use for this click
     * @return an array of modifiers to press before click and release after
     *         click
     */
    private int[] getModifierMask(ClickModifier clickModifier) {
        int[] modifier = new int[0];
        if (clickModifier.hasModifiers(ClickModifier.M1)) {
            modifier = ArrayUtils.add(modifier, 
                    KeyCodeUtil.getKeyCode(KeyCode.CONTROL));
        }
        if (clickModifier.hasModifiers(ClickModifier.M2)) {
            modifier = ArrayUtils.add(modifier, 
                    KeyCodeUtil.getKeyCode(KeyCode.SHIFT));
        }
        if (clickModifier.hasModifiers(ClickModifier.M3)) {
            modifier = ArrayUtils.add(modifier, 
                    KeyCodeUtil.getKeyCode(KeyCode.ALT));
        }
        if (clickModifier.hasModifiers(ClickModifier.M4)) {
            modifier = ArrayUtils.add(modifier, 
                    KeyCodeUtil.getKeyCode(KeyCode.META));
        }
        return modifier;
    }

    /**
     * Checks if the mouse has to be moved on <code>p</code> or if the mouse
     * pointer already resides on this location.
     *
     * @param p
     *            The point to move to
     * @return <code>true</code> if the mouse pointer resides on a different
     *         point, otherwise <code>false</code>.
     */
    private boolean isMouseMoveRequired(Point p) {
        boolean result = true;
        Point point = getCurrentMousePosition();
        if (point != null) {
            result = !point.equals(p);
            if (log.isDebugEnabled()) {
                log.debug("Last converted screen point  : " + point); //$NON-NLS-1$
                log.debug("Required screen point        : " + p); //$NON-NLS-1$
                log.debug("Mouse move required?         : " + result); //$NON-NLS-1$
            }
        }
        return result;
    }

    /**
     * Implementation of the mouse move. The mouse is moved into the graphics
     * component.
     *
     * @param graphicsComponent
     *            The component to move to
     * @param constraints
     *            The more specific constraints. Use this, for example when you
     *            want the click point to be relative to a part of the component
     *            (e.g. tree node, table cell, etc) rather than the overall
     *            component itself. May be <code>null</code>.
     * @param xPos
     *            xPos in component
     * @param yPos
     *            yPos in component
     * @param xAbsolute
     *            true if x-position should be absolute
     * @param yAbsolute
     *            true if y-position should be absolute
     * @param clickOptions
     *            The click options
     * @throws StepExecutionException
     *             If the click delay is interrupted or the event confirmation
     *             receives a timeout.
     */
    private void moveImpl(final Object graphicsComponent,
            final Rectangle constraints, final int xPos,
            final boolean xAbsolute, final int yPos, final boolean yAbsolute,
            final ClickOptions clickOptions) throws StepExecutionException {
        Rectangle bounds = getComponentBounds(graphicsComponent, clickOptions);
        if (constraints != null) {
            bounds.x += constraints.x;
            bounds.y += constraints.y;
            bounds.height = constraints.height;
            bounds.width = constraints.width;
        }
        Point p = PointUtil.calculateAwtPointToGo(xPos, xAbsolute, yPos,
                yAbsolute, bounds);
        boolean isInside = true;
        if (graphicsComponent instanceof Node) {
            isInside = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "CheckIfContains", new Callable<Boolean>() { //$NON-NLS-1$
                    @Override
                    public Boolean call() throws Exception {
                        return NodeBounds.checkIfContains(new Point2D(
                                    p.x, p.y), (Node) graphicsComponent);
                    }
                });
        }
        if (!isInside) {
            throw new StepExecutionException(
                    TestErrorEvent.CLICKPOINT_INVALID,
                    EventFactory.createActionError(
                            TestErrorEvent.CLICKPOINT_INVALID));
        }
        // Move if necessary
        if (isMouseMoveRequired(p)) {
            if (log.isDebugEnabled()) {
                log.debug("Moving mouse to: " + p); //$NON-NLS-1$
            }
            Point startpoint = m_mouseMotionTracker.getLastMousePointOnScreen();
            if (startpoint == null) {
                // If there is no starting point the center of the root
                // component is used
                if (graphicsComponent instanceof Stage) {
                    Stage s = (Stage) graphicsComponent;
                    Node root = s.getScene().getRoot();
                    startpoint = (root != null) ? getLocation(root, null)
                            : new Point(Rounding.round(s.getWidth() / 2),
                                    Rounding.round(s.getHeight() / 2));
                } else {
                    Node node = (Node) graphicsComponent;
                    Node root = node.getScene().getRoot();
                    Node c = (root != null) ? root : node;
                    startpoint = getLocation(c, null);
                }
            }
            IRobotEventConfirmer confirmer = null;
            InterceptorOptions options = new InterceptorOptions(
                    new long[] { AWTEvent.MOUSE_MOTION_EVENT_MASK });
            //For drag Events we have to register the confirmer earlier
            //because the drag event is thrown when the movement starts
            if (DragAndDropHelper.getInstance().isDragMode()) {
                confirmer = m_interceptor.intercept(options); 
            }
            final Point[] mouseMove = MouseMovementStrategy.getMovementPath(
                    startpoint, p, clickOptions.getStepMovement(),
                    clickOptions.getFirstHorizontal());
            for (int i = 0; i < mouseMove.length - 1; i++) {                
                m_robot.mouseMove(mouseMove[i].x, mouseMove[i].y);
                m_robot.waitForIdle();
            }
            if (!DragAndDropHelper.getInstance().isDragMode()) {
                confirmer = m_interceptor.intercept(options); 
            }           
            Point endPoint = mouseMove[mouseMove.length - 1];
            m_robot.mouseMove(endPoint.x, endPoint.y);
            m_robot.waitForIdle();
            if (confirmer != null) {
                confirmMove(confirmer, graphicsComponent);
            }
        }
    }

    /**
     * Confirms a move, either a normal move or a drag move.
     *
     * @param confirmer
     *            the confirmer
     * @param comp
     *            the component to confirm for
     */
    private void confirmMove(IRobotEventConfirmer confirmer, Object comp) {
        if (DragAndDropHelper.getInstance().isDragMode()) {
            confirmer.waitToConfirm(null, new MouseMovedEventMatcher(
                    MouseEvent.MOUSE_DRAGGED));
        } else {
            confirmer.waitToConfirm(null, new MouseMovedEventMatcher(
                    MouseEvent.MOUSE_MOVED));
        }
    }

    /**
     * Refreshes the complete layout and returns the bounds of the given
     * Component.
     *
     * @param comp
     *            the Component
     * @param clickOp
     *            not used
     * @return Rectangle with the Bounds
     */
    private Rectangle getComponentBounds(final Object comp,
            ClickOptions clickOp) {

        ComponentHandler.syncStageResize();
        Rectangle bounds = null;
        if (comp instanceof Stage) {
            Stage s = (Stage) comp;
            bounds = new Rectangle(new Point(Rounding.round(s.getX()),
                    Rounding.round(s.getY())));

            // This is not multi display compatible
            Screen screen = Screen.getPrimary();
            final Rectangle2D screenBounds = screen.getBounds();
            int displayWidth = Rounding.round(screenBounds.getWidth());
            int displayHeight = Rounding.round(screenBounds.getHeight());
            if (s.isFullScreen()) {
                bounds.width = Rounding.round(displayWidth);
                bounds.height = Rounding.round(displayHeight);
            } else if (s.isMaximized()) {
                int x = Rounding.round(s.getX());
                int y = Rounding.round(s.getY());
                // trimming the bounds to the display if necessary
                bounds.width = Rounding.round(s.getWidth());
                bounds.height = Rounding.round(s.getHeight());
                if (x < 0 || y < 0) {
                    bounds.x = 0;
                    bounds.y = 0;
                    if (bounds.width > displayWidth) {
                        bounds.width = displayWidth;
                    }
                    if (bounds.height > displayHeight) {
                        bounds.height = displayHeight;
                    }
                }
            } else {
                bounds.width = Rounding.round(s.getWidth());
                bounds.height = Rounding.round(s.getHeight());
            }
        } else {
            final Node node = (Node) comp;
            if (clickOp != null 
                    && clickOp.isScrollToVisible()) {
                ensureComponentVisible(node);
            }
            bounds = EventThreadQueuerJavaFXImpl.invokeAndWait(
                    "Robot get node bounds", new Callable<Rectangle>() { //$NON-NLS-1$

                        @Override
                        public Rectangle call() throws Exception {
                            Parent parent = node.getParent();
                            if (parent != null) {
                                parent.requestLayout();
                                parent.layout();
                            }
                            return NodeBounds.getAbsoluteBounds(node);
                        }
                    });

        }
        return bounds;
    }

    /**
     * {@inheritDoc}
     */
    public void click(Object graphicsComponent, Rectangle constraints)
        throws RobotException {
        click(graphicsComponent, constraints, ClickOptions.create());
    }

    /**
     * {@inheritDoc}
     */
    public void click(Object graphicsComponent, Rectangle constraints,
            ClickOptions clickOptions) throws RobotException {

        clickImpl(graphicsComponent, constraints, clickOptions, 50, false, 50,
                false);
    }

    /**
     * Gets the InputEvent-ButtonMask of the given mouse button number
     *
     * @param button
     *            the button number
     * @return the InputEvent button mask
     */
    private int getButtonMask(int button) {
        if (button == InteractionMode.primary.rcIntValue()) {
            return java.awt.event.InputEvent.BUTTON1_MASK;
        }
        if (button == InteractionMode.tertiary.rcIntValue()) {
            return java.awt.event.InputEvent.BUTTON2_MASK;
        }
        if (button == InteractionMode.secondary.rcIntValue()) {
            return java.awt.event.InputEvent.BUTTON3_MASK;
        }
        throw new RobotException("unsupported mouse button", null); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void clickAtCurrentPosition(Object graphicsComponent,
            int clickCount, int button) {
        ClickOptions clickOptions = new ClickOptions();
        clickOptions.setClickCount(clickCount);
        clickOptions.setMouseButton(button);
        clickImpl(graphicsComponent, clickOptions);
    }

    /**
     * {@inheritDoc}
     *
     */
    public void move(Object graphicsComponent, Rectangle constraints)
        throws RobotException {

        moveImpl(graphicsComponent, constraints, 50, false, 50,
                false, ClickOptions.create());
    }

    /**
     * {@inheritDoc} <br>
     * <b>* Currently delegates the key type to the Robot </b>
     */
    public void type(final Object graphicsComponent, char c) 
        throws RobotException {
        
        Validate.notNull(graphicsComponent,
                "The graphic component must not be null"); //$NON-NLS-1$

        final KeyEvent event = new KeyEvent(
                KeyEvent.KEY_TYPED, String.valueOf(c), 
                StringUtils.EMPTY, null, false, false, false, false);

        InterceptorOptions options = new InterceptorOptions(
                new long[] { AWTEvent.KEY_EVENT_MASK });
        IRobotEventConfirmer confirmer = m_interceptor.intercept(options);

        m_queuer.invokeLater("Type character", new Runnable() { //$NON-NLS-1$
            @Override
            public void run() {
                final Scene scene;
                if (graphicsComponent instanceof Stage) {
                    scene = ((Stage)graphicsComponent).getScene();
                } else {
                    scene = ((Node)graphicsComponent).getScene();
                }
                
                Node focusOwner = scene.getFocusOwner();
                EventTarget eventTarget = 
                        focusOwner != null ? focusOwner : scene;
                
                Event.fireEvent(eventTarget, event);
            }
        });
        
        confirmer.waitToConfirm(graphicsComponent,
                new KeyJavaFXEventMatcher(KeyEvent.KEY_TYPED));

    }
    
    /**
     * {@inheritDoc}
     */
    public void type(Object graphicsComponent, String text)
        throws RobotException {
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                type(graphicsComponent, ch);
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void keyType(Object graphicsComponent, int keycode) {
        keyType(graphicsComponent, keycode, false);
    }

    /**
     * @param graphicsComponent The graphics component the key code is typed in, may be null
     * @param keycode The key code.
     * @param isUpperCase Boolean whether character is upper case.
     */
    public void keyType(final Object graphicsComponent, final int keycode, 
            final boolean isUpperCase)
        throws RobotException {
        try {
            InterceptorOptions options = new InterceptorOptions(
                    new long[] { AWTEvent.KEY_EVENT_MASK });
            IRobotEventConfirmer confirmer = m_interceptor.intercept(options);
            try {
                if (isUpperCase) {
                    m_robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
                }
                m_robot.keyPress(keycode);
                confirmer.waitToConfirm(graphicsComponent,
                        new KeyJavaFXEventMatcher(KeyEvent.KEY_PRESSED));
            } finally {
                m_robot.keyRelease(keycode);
                if (isUpperCase) {
                    m_robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
                }
            }
            confirmer.waitToConfirm(graphicsComponent,
                    new KeyJavaFXEventMatcher(KeyEvent.KEY_RELEASED));
        } catch (IllegalArgumentException e) {
            throw new RobotException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemModifierSpec() {
        String keyStrokeSpec = ValueSets.Modifier.control.rcValue();
        if (!(UIManager.getLookAndFeel().getID().equals(METAL_LAF_ID))) {
            if (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                    == java.awt.Event.META_MASK) {
                keyStrokeSpec = ValueSets.Modifier.meta.rcValue();
            } else if (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                    == java.awt.Event.ALT_MASK) {
                keyStrokeSpec = ValueSets.Modifier.alt.rcValue();
            }
        }
        return keyStrokeSpec;
    }

    /**
     * Implements the key press or release.
     *
     * @param graphicsComponent
     *            The component, may be <code>null</code>
     * @param keyCode
     *            The key code
     * @param press
     *            If <code>true</code>, the key is pressed, otherwise released
     */
    private void keyPressReleaseImpl(Object graphicsComponent, int keyCode,
            boolean press) {

        InterceptorOptions options = new InterceptorOptions(
                new long[] { AWTEvent.KEY_EVENT_MASK });
        IRobotEventConfirmer confirmer = m_interceptor.intercept(options);
        if (press) {
            m_robot.keyPress(keyCode);
        } else {
            m_robot.keyRelease(keyCode);
        }
        confirmer.waitToConfirm(graphicsComponent, new KeyJavaFXEventMatcher(
                press ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED));
    }

    /**
     * {@inheritDoc}
     */
    public void keyPress(Object graphicsComponent, int keycode)
        throws RobotException {

        keyPressReleaseImpl(graphicsComponent, keycode, true);
    }

    /**
     * {@inheritDoc}
     */
    public void keyRelease(Object graphicsComponent, int keycode)
        throws RobotException {

        keyPressReleaseImpl(graphicsComponent, keycode, false);
    }

    /**
     * a method to turn the toggle keys caps-lock, num-lock and scroll-lock on
     * and off. If the given key code is one of these buttons otherwise this is
     * a normal button press.
     *
     * @param obj
     *            Component
     * @param key
     *            to set key Event
     * @param activated
     *            boolean
     */
    public void keyToggle(Object obj, int key, boolean activated) {
        keyPressReleaseImpl(null, key, true);
        keyPressReleaseImpl(null, key, false);
    }

    /**
     * {@inheritDoc}
     */
    public void keyStroke(String keyStrokeSpec) throws RobotException {
        try {
            KeyTyper.getInstance().type(keyStrokeSpec, m_interceptor,
                    new KeyJavaFXEventMatcher(KeyEvent.KEY_PRESSED),
                    new KeyJavaFXEventMatcher(KeyEvent.KEY_RELEASED));
        } catch (AWTException e) {
            throw new RobotException(e);
        }
    }

    /**
     * Ensures that the passed component is visible.
     *
     * @param component
     *            The component.
     * @throws RobotException
     *             If the component's screen location cannot be calculated.
     */
    private void ensureComponentVisible(final Node component)
        throws RobotException {
        m_queuer.invokeAndWait("ensureVisible", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() {
                Scroller scroller = new Scroller(component);
                scroller.scrollToVisible();
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void scrollToVisible(Object graphicsComponent, Rectangle constraints)
        throws RobotException {

        ensureComponentVisible((Node) graphicsComponent);
    }

    /**
     * {@inheritDoc}
     */
    public void activateApplication(String method) throws RobotException {
        try {
            final Window window = getActiveWindow();
            if (window == null) {
                return;
            }
            WindowActivationMethod wam = WindowActivationMethod
                    .createWindowActivationMethod(method, m_robot, m_queuer);
            wam.activate(window);

            // Verify that window was successfully activated
            Window activeWindow = m_queuer.invokeAndWait("getActiveWindow", //$NON-NLS-1$
                    new IRunnable<Window>() {
                        public Window run() throws StepExecutionException {

                            if (window.isFocused()) {
                                return window;
                            }
                            return null;
                        }
                    });
            if (activeWindow != window) {
                throw new StepExecutionException(
                        I18n.getString(TestErrorEvent.WINDOW_ACTIVATION_FAILED,
                                true),
                        EventFactory
                                .createActionError(
                                        TestErrorEvent.
                                        WINDOW_ACTIVATION_FAILED));
            }

        } catch (Exception exc) {
            throw new RobotException(exc);
        }
    }

    /**
     * @return The current mouse position as a Point {@inheritDoc}
     */
    public Point getCurrentMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    /**
     * Guesses the active window. Returns null if no active window is found.
     *
     * @return the active window
     */
    private Window getActiveWindow() {
        return m_queuer.invokeAndWait("getActiveWindow", //$NON-NLS-1$
                new IRunnable<Window>() {
                    public Window run() throws StepExecutionException {
                        Window w = CurrentStages.getfocusStage();
                        if (w == null) {
                            w = CurrentStages.getfirstStage();
                            ((Stage) w).toFront();
                        }
                        return w;
                    }
                });
    }

    /**
     *
     * {@inheritDoc}
     */
    public boolean isMouseInComponent(final Object graphicsComponent) {
        final Point currMousePos = getCurrentMousePosition();
        return EventThreadQueuerJavaFXImpl.invokeAndWait("isMouseInComponent", //$NON-NLS-1$
                new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        if (graphicsComponent instanceof Node) {
                            Node comp = (Node) graphicsComponent;
                            comp.getScene().getRoot().layout();

                            if (currMousePos == null) {
                                return false;
                            }
                            return NodeBounds.checkIfContains(new Point2D(
                                    currMousePos.x, currMousePos.y), comp);
                        }
                        Stage comp = (Stage) graphicsComponent;
                        comp.getScene().getRoot().layout();
                        Bounds stageBounds = new BoundingBox(comp.getX(),
                                comp.getY(), comp.getWidth(), comp
                                        .getHeight());
                        return stageBounds.contains(new Point2D(
                                currMousePos.x, currMousePos.y));
                    }
                });

    }

    /**
     * Presses the given mouse button on the given component in the given
     * constraints. <br>
     * <b>Note:</b> Use only for Drag and Drop! To click with the mouse, use
     * click-methods!
     *
     * @param graphicsComponent
     *            the component where to press the mouse button. If null, the
     *            mouse is pressed at the current location.
     * @param constraints
     *            A constraints object used by the Robot implementation, may be
     *            <code>null</code>.
     * @param button
     *            the mouse button which is to be pressed.
     */
    public void mousePress(Object graphicsComponent, Rectangle constraints,
            int button) {
        DragAndDropHelper.getInstance().setDragMode(true);
        if (graphicsComponent != null) {
            move(graphicsComponent, constraints);
        }

        RobotTiming.sleepPreClickDelay();

        m_robot.mousePress(getButtonMask(button));
    }

    /**
     * Releases the given mouse button on the given component in the given
     * constraints. <br>
     * <b>Note:</b> Use only for Drag and Drop! To click with the mouse, use
     * click-methods!
     *
     * @param graphicsComponent
     *            The graphics component. If null, the mouse button is released
     *            at the current location.
     * @param constraints
     *            A constraints object used by the Robot implementation, may be
     *            <code>null</code>.
     * @param button
     *            the mouse button.
     */
    public void mouseRelease(Object graphicsComponent, Rectangle constraints,
            int button) throws RobotException {
        if (graphicsComponent != null) {
            move(graphicsComponent, constraints);
        }
        RobotTiming.sleepPreClickDelay();
        m_robot.mouseRelease(getButtonMask(button));
        DragAndDropHelper.getInstance().setDragMode(false);
    }

    /**
     * {@inheritDoc}
     */
    public void click(Object graphicsComponent, Rectangle constraints,
            ClickOptions clickOptions, int xPos, boolean xAbsolute, int yPos,
            boolean yAbsolute) throws RobotException {

        clickImpl(graphicsComponent, constraints, clickOptions, xPos,
                xAbsolute, yPos, yAbsolute);
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValue(Object graphicsComp, String propertyName)
        throws RobotException {
        return PropertyUtil.getPropertyValue(graphicsComp, propertyName);
    }

    /** {@inheritDoc} */
    public BufferedImage createFullScreenCapture() {
        return LocalScreenshotUtil.createFullScreenCapture();
    }

    /**
     * Return the currently used EventInterceptor
     * @return the Interceptor
     */
    public RobotEventInterceptorJavaFXImpl getInterceptor() {
        return m_interceptor;
    }

    /**
     * {@inheritDoc}
     */
    public void shakeMouse() {
        /** number of pixels by which a "mouse shake" offsets the mouse cursor */
        final int mouseShakeOffset = 10;

        Point origin = getCurrentMousePosition();
        try {
            m_robot.mouseMove(
                    origin.x + mouseShakeOffset, 
                    origin.y + mouseShakeOffset);
            m_robot.mouseMove(
                    origin.x - mouseShakeOffset, 
                    origin.y - mouseShakeOffset);
        } finally {
            m_robot.mouseMove(origin.x, origin.y);
        }
    }

    @Override
    public Rectangle getComponentBounds(IComponent component) {
        return getComponentBounds(component.getRealComponent(), null);
    }

}