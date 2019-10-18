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
package org.eclipse.jubula.rc.common.driver;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.eclipse.jubula.rc.common.exception.OsNotSupportedException;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;


/**
 * This interface represents a Robot which performs mouse clicks, mouse moves
 * and keyboard inputs. <br>
 * The interface might be implemented by using Graphics-API specific Robot
 * implementations. When <code>IRobot</code> is used to control a AWT/Swing
 * application, the implementing Robot class delegates to {@link java.awt.Robot}
 * .<br>
 * It is important to know that the Robot intercepts the mouse clicks and moves
 * by examining the Graphics API specific event queue. The robot stops the
 * current thread until the expected event has been posted into the event queue
 * or until a timeout occurs. <br>
 * See {@link org.eclipse.jubula.rc.swing.driver.RobotFactoryConfig} to learn
 * more about the robot programming model.
 * 
 * @author BREDEX GmbH
 * @created 17.03.2005
 * @param <CONSTRAINT_TYPE>
 *            the type of constraints
 */
public interface IRobot <CONSTRAINT_TYPE> {

    /**
     * Clicks with the given click count and the given button at the current 
     * mouse position.
     * 
     * @param graphicsComponent the component used for event confirmation.
     * @param clickCount number of clicks to perform
     * @param button mouse button
     * @throws RobotException If the mouse click fails
     */
    public void clickAtCurrentPosition(Object graphicsComponent, 
            int clickCount, int button) throws RobotException;
    
    /**
     * Performs a single mouse click with the first mouse button on the 
     * graphics component. The <code>constraints</code> object 
     * may be interpreted in different ways
     * depending on the implementing class. The mouse click is being confirmed
     * by waiting for a <code>CLICKED</code> mouse event. If the graphics
     * component resides inside a scroll pane and is not visible, it will be
     * scrolled to visible before the mouse click is performed.
     * @param graphicsComponent The graphics component to click on
     * @param constraints A constraints object used by the Robot implementation.
     *                    Any coordinates contained in the constraints must be 
     *                    <em>relative</em> to <code>graphicsComponent</code>'s 
     *                    coordinate system. May be <code>null</code>.
     * @throws RobotException If the mouse click fails
     */
    public void click(Object graphicsComponent, CONSTRAINT_TYPE constraints)
        throws RobotException;

    /**
     * Performs a mouse click with the specified mouse button on the
     * graphics component. The click is configured by <code>clickOptions</code>.
     * @param graphicsComponent The graphics component to click on
     * @param constraints A constraints object used by the Robot implementation.
     *                    Any coordinates contained in the constraints must be 
     *                    <em>relative</em> to <code>graphicsComponent</code>'s 
     *                    coordinate system. May be <code>null</code>.
     * @param clickOptions clickOptions The click options
     * @param xPos xPos in component 
     * @param xAbsolute absolute true if position should be absolute   
     * @param yPos yPos in component
     * @param yAbsolute absolute true if position should be absolute   
     * @throws RobotException If the mouse click fails
     */
    public void click(Object graphicsComponent, CONSTRAINT_TYPE constraints,
        ClickOptions clickOptions, 
        int xPos, boolean xAbsolute, int yPos, boolean yAbsolute) 
        throws RobotException;
    
    /**
     * Performs a mouse click with the first mouse button on the graphics 
     * component. The click is configured by <code>clickOptions</code>. If
     * <code>constraints</code> is not <code>null</code>, the click will occur
     * in the center of the <code>constraints</code>. Otherwise, the click will
     * occur in the center of <code>graphicsComponent</code>.
     * @param graphicsComponent The graphics component to click on
     * @param constraints A constraints object used by the Robot implementation.
     *                    Any coordinates contained in the constraints must be 
     *                    <em>relative</em> to <code>graphicsComponent</code>'s 
     *                    coordinate system. May be <code>null</code>.
     * @param clickOptions The click options
     * @throws RobotException If the mouse click fails
     */
    public void click(Object graphicsComponent, CONSTRAINT_TYPE constraints,
        ClickOptions clickOptions) throws RobotException;

    /**
     * Moves the mouse pointer onto the graphics component. The mouse move is
     * confirmed by waiting for a <code>MOVED</code> event. 
     * @param graphicsComponent The graphics component to move to
     * @param constraints A constraints object used by the Robot implementation.
     *                    Any coordinates contained in the constraints must be 
     *                    <em>relative</em> to <code>graphicsComponent</code>'s 
     *                    coordinate system. May be <code>null</code>.
     * @throws RobotException If the mouse move fails
     */
    public void move(Object graphicsComponent, CONSTRAINT_TYPE constraints)
        throws RobotException;

    /**
     * Types a single character at the current cursor location. Usually, the
     * typing is performed by posting key events into the event queue. To use
     * this method correctly, the cursor has to be set into the graphics
     * component prior to calling this method. This can be done by calling the
     * move() and click() methods. 
     * @param graphicsComponent The graphics component the character is typed in. Must not be <code>null</code>
     * @param character The character to type.
     * @throws RobotException If the typing fails.
     */
    public void type(Object graphicsComponent, char character)
        throws RobotException;

    /**
     * Types the string <code>text</code> at the current cursor location.
     * @param graphicsComponent  The graphics component the string is typed in. Must not be <code>null</code>
     * @param text The string to type
     * @throws RobotException If the typing fails.
     */
    public void type(Object graphicsComponent, String text)
        throws RobotException;
    /**
     * Types the passed key code at the current cursor location. The key code is
     * not a key character, but a Graphics API specific code that represents a
     * key, for example <code>KeyEvent.VK_ENTER</code> that represents the
     * Enter-Key in AWT. The <code>graphicsComponent</code> can be
     * <code>null</code>. If it is <code>null</code>, the Robot confirms
     * the event keycode, but not the event source, that means the graphic component.
     * @param graphicsComponent The graphics component the key code is typed in, may be <code>null</code>
     * @param keycode The key code.
     * @throws RobotException If the key typing fails.
     */
    public void keyType(Object graphicsComponent, int keycode)
        throws RobotException;
    /**
     * This method works like {@link #keyType(Object, int)}, but just presses
     * the key without releasing it. This method may be used for example to hold
     * the CTRL key while the mouse is being clicked. 
     * @param graphicsComponent The graphics component the key code is typed in, may be <code>null</code>
     * @param keycode The key code.
     * @throws RobotException If the key press fails.
     */
    public void keyPress(Object graphicsComponent, int keycode)
        throws RobotException;
    /**
     * This method releases a key with the passed key code.
     * @param graphicsComponent The graphics component the key code is typed in, may be <code>null</code>
     * @param keycode The key code.
     * @throws RobotException If the key release fails.
     */
    public void keyRelease(Object graphicsComponent, int keycode)
        throws RobotException;
    
    /**
     * Performs the passed key stroke specification. The key stroke
     * specification is a widget set dependent specification of a combination of
     * modifier keys and a primary keycode. Regardless if an additional
     * "pressed", "typed" or "released" eventtype is specified, the key
     * combination is always first pressed and then released. 
     * @param keyStrokeSpecification The key code.
     * @throws RobotException If the key stroke action fails.
     */
    public void keyStroke(String keyStrokeSpecification)
        throws RobotException;
    /**
     * Scrolls the passed graphics component to visible. This covers all
     * scenarios where the component resides inside a scroll pane and has been
     * scrolled into an invisible area. The component will be scrolled into the
     * visible area so that a later mouse click etc. will perform successfully.
     * Implementors of the Robot may interpret the <code>constraints</code> as
     * a subarea of the passed component. In this case, this subarea will be
     * scrolled to visible. 
     * @param graphicsComponent The graphics component that will be scrolled to visible.
     * @param constraints A constraints object used by the Robot implementation, may be <code>null</code>.
     * @throws RobotException If the scrolling fails.
     */   
    public void scrollToVisible(Object graphicsComponent,
            CONSTRAINT_TYPE constraints) throws RobotException;
    
    /**
     * a method to turn the toggle keys caps-lock, num-lock and scroll-lock on and off.
     * @param obj Component
     * @param key to set key Event
     * @param activated boolean
     */
    public void keyToggle(Object obj, int key, boolean activated) 
        throws OsNotSupportedException;

    /**
     * activates the application with the given method 
     * @param method the method
     * @throws RobotException on error
     */
    public void activateApplication(String method) throws RobotException;
    
    
    /**
     * @return The current mouse position as a Point
     */
    public Point getCurrentMousePosition();
    
    /**
     * Presses and holds (!) the given mouse button on the graphics 
     * component. 
     * @param graphicsComponent The graphics component.
     * @param constraints A constraints object used by the Robot implementation.
     *                    Any coordinates contained in the constraints must be 
     *                    <em>relative</em> to <code>graphicsComponent</code>'s 
     *                    coordinate system. May be <code>null</code>.
     * @param button the mouse button.
     * @throws RobotException If the mouse press fails.
     */
    public void mousePress(Object graphicsComponent,
            CONSTRAINT_TYPE constraints, int button) throws RobotException;
    
    
    /**
     * Releases and the given mouse button on the graphics 
     * component. 
     * @param graphicsComponent The graphics component.
     * @param constraints A constraints object used by the Robot implementation.
     *                    Any coordinates contained in the constraints must be 
     *                    <em>relative</em> to <code>graphicsComponent</code>'s 
     *                    coordinate system. May be <code>null</code>.
     * @param button the mouse button.
     * @throws RobotException If the mouse release fails.
     */
    public void mouseRelease(Object graphicsComponent,
            CONSTRAINT_TYPE constraints, int button) throws RobotException;
    
    /**
     * 
     * @param graphicsComponent The component to use for the check.
     * @return <code>true</code> if the mouse pointer lies within the bounds
     *         of <code>graphicsComponent</code>. Otherwise, <code>false</code>
     */
    public boolean isMouseInComponent(Object graphicsComponent);
    
    /**
     * Gets System Default Modifier Specification (MOD1)
     * @return <code>control</code> for Windows/Linux, <code>meta</code> for
     * Mac OS
     */
    public String getSystemModifierSpec();
 
    /**
     * @param graphicsComponent the component used for property retrieval
     * @param propertyName
     *            the name of the property value to return
     * @return the property value
     * @throws RobotException
     *             If the given property is not accessible or not found
     */
    public String getPropertyValue(Object graphicsComponent,
        String propertyName) throws RobotException;
    
    /**
     * 
     * @param component the component for which the information
     *            about its bounds is seeked
     * @return an Rectangle object containing the x, y, height
     *            and width value of the component
     */
    public Rectangle getComponentBounds (IComponent component);
    
    /**
     * Create an image containing pixels read from the full screen.
     * 
     * @return The captured image
     */
    public BufferedImage createFullScreenCapture();

    /**
     * Move the mouse pointer from its current position to a few points in its
     * proximity. This is used to initiate a drop operation.
     * 
     */
    public void shakeMouse();

}