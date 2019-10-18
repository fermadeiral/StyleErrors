/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;

/**
 * This interface defines basic functionality for nearly all UI components.
 * 
 * @author BREDEX GmbH
 */
public interface IWidgetComponent extends IComponent {
    /**
     * @return <code>true</code> if the component is visible
     */
    public boolean isShowing();
    
    /**
     * @return <code>true</code> if the component is enabled
     */
    public boolean isEnabled();
    
    /**
     * @return <code>true</code> if the component has focus
     */
    public boolean hasFocus();

    /**
     * @param propertyname
     *            the name of the property value to return
     * @return the property value
     */
    public String getPropteryValue(String propertyname);
    
    /**
     * Shows and returns the popup menu
     * 
     * @param xPos
     *            what x position
     * @param xUnits
     *            should x position be pixel or percent values
     * @param yPos
     *            what y position
     * @param yUnits
     *            should y position be pixel or percent values
     * @param button
     *            MouseButton
     * @return the popup menu
     * @throws StepExecutionException
     *             error
     */
    public AbstractMenuTester showPopup(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, final int button)
        throws StepExecutionException;
    
    /**
     * Shows and returns the popup menu
     * 
     * @param button
     *            MouseButton
     * @return the popup menu
     */
    public AbstractMenuTester showPopup(int button);
    
    /**
     * Simulates a tooltip for demonstration purposes.
     * 
     * @param text
     *            The text to show in the tooltip
     * @param textSize
     *            The size of the text in points
     * @param timePerWord
     *            The amount of time, in milliseconds, used to display a single
     *            word. A word is defined as a string surrounded by whitespace.
     * @param windowWidth
     *            The width of the tooltip window in pixels.
     */
    public void showToolTip(final String text, final int textSize,
            final int timePerWord, final int windowWidth);
    
    /**
     * Performs a Drag. Moves into the middle of the Component and presses and
     * holds the given modifier and the given mouse button.
     * 
     * @param mouseButton
     *            the mouse button.
     * @param modifier
     *            the modifier, e.g. shift, ctrl, etc.
     * @param xPos
     *            what x position
     * @param xUnits
     *            should x position be pixel or percent values
     * @param yPos
     *            what y position
     * @param yUnits
     *            should y position be pixel or percent values
     */
    public void rcDrag(int mouseButton, String modifier, int xPos,
            String xUnits, int yPos, String yUnits);
    
    /**
     * Performs a Drop. Moves into the middle of the Component and releases the
     * modifier and mouse button pressed by rcDrag.
     * 
     * @param xPos
     *            what x position
     * @param xUnits
     *            should x position be pixel or percent values
     * @param yPos
     *            what y position
     * @param yUnits
     *            should y position be pixel or percent values
     * @param delayBeforeDrop
     *            the amount of time (in milliseconds) to wait between moving
     *            the mouse to the drop point and releasing the mouse button
     */
    public void rcDrop(int xPos, String xUnits, int yPos, String yUnits,
            int delayBeforeDrop);
    
    /**
     * Gets the key code for a specific modifier
     * 
     * @param mod
     *            the modifier
     * @return the integer key code value
     */
    public int getKeyCode(String mod);

}