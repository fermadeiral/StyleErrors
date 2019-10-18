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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;


/**
 * This class configures a mouse click performed by the remote control component.
 *
 * @author BREDEX GmbH
 * @created 21.03.2005
 */
public class ClickOptions {
    /**
     * This class defines how a mouse click performed by the
     * JubulaRobot is confirmed when the Graphics-API specific
     * event queue is observed. If the click type <code>CLICKED</code>
     * is used, the Robot confirms the mouse click when a
     * CLICKED mouse event occurs. However, the robot waits for a
     * <code>RELEASED</code> mouse event when the corresponding
     * click type is used.
     */
    public static class ClickType {
        /**
         * This click type causes the Robot to confirm
         * <code>CLICKED</code> mouse events.
         */
        public static final ClickType CLICKED = new ClickType();
        /**
         * This click type causes the Robot to confirm
         * <code>RELEASED</code> mouse events.
         */
        public static final ClickType RELEASED = new ClickType();
        /**
         * Default constructor.
         */
        private ClickType() {
            // Default constructor
        }
    }

    /**
     * Modifier keys to use during click
     * 
     * The recognized modifiers keys are M1, M2, M3, and M4. The "M" modifier
     * keys are a platform-independent way of representing keys.
     * 
     * @author BREDEX GmbH
     * @created Jul 22, 2010
     */
    public static class ClickModifier {
        /**
         * <code>NO_MODIFIER</code> indicates that no modifier should be used
         */
        public static final int NO_MODIFIER = 0x00;
        
        /**
         * <code>M1</code> is the COMMAND key on MacOS X, and the CTRL key on
         * most other platforms.
         */
        public static final int M1 = 0x01;

        /**
         * <code>M2</code> is the SHIFT key.
         */
        public static final int M2 = 0x02;

        /**
         * <code>M3</code> is the Option key on MacOS X, and the ALT key on most
         * other platforms.
         */
        public static final int M3 = 0x04;

        /**
         * <code>M4</code> is the CTRL key on MacOS X, and is undefined on other
         * platforms.
         */
        public static final int M4 = 0x08;

        /**
         * <code>m_bitmask</code>
         */
        private int m_bitmask = 0;
        
        /**
         * Constructor.
         * @param bitmask the bitmask to use
         */
        private ClickModifier(int bitmask) {
            m_bitmask = bitmask;
        }

        /**
         * Creates a new <code>ClickModifier</code> instance.
         * @param bitmask the modifiers to use: e.g. create(M1 | M4)
         * @return A new <code>ClickModifier</code> object with the given
         *         modifiers.
         */
        public static ClickModifier create(int bitmask) {
            return new ClickModifier(bitmask);
        }
        
        /**
         * Creates a new <code>ClickModifier</code> instance.
         * @return A new <code>ClickModifier</code> object with the default
         * modifier <code>NO_MODIFIER</code>
         */
        public static ClickModifier create() {
            return create(NO_MODIFIER);
        }

        /**
         * @param mod the modifier to check for
         * @return true if the clickModifier instance has the given modifier(s).
         * You may also use e.g. hasModifier(M1 | M4)
         */
        public boolean hasModifiers(int mod) {
            return new EqualsBuilder().append(m_bitmask & mod, mod).isEquals();
        }
        
        /**
         * @param bitmask the modifier bitmask to add e.g. M2
         * @return the click modifier instance
         */
        public ClickModifier add(int bitmask) {
            m_bitmask |= bitmask;
            return this;
        }
    }
    
    /** The click count. */
    private int m_clickCount = 1;
    
    /**
     * Configures whether the affected component will be scrolled to visible
     * before the click is performed.
     */
    private boolean m_scrollToVisible = true;
    
    /** The click type. */
    private ClickType m_clickType = ClickType.CLICKED;
    
    /** Configures the mouse movement strategy */
    private boolean m_isMoveInSteps = true;
    
    /** Configures whether the performed click should be confirmed */
    private boolean m_confirmClick = true; 
    
    /** which mouse button to use */
    private int m_mouseButton = InteractionMode.primary.rcIntValue();
    
    /**
     * <code>m_clickModifier</code> specifies which modifiers should be pressed
     * before clicking and release after clicking
     */
    private ClickModifier m_clickModifier = ClickModifier.create();
    
    /** which axis first */
    private boolean m_firstHorizontal = true;
    
    /**
     * Creates a new <code>ClickOptions</code> instance.
     * 
     * @return A new <code>ClickOptions</code> object with the default options:
     *         single confirmed left click (scroll to visible)
     */
    public static ClickOptions create() {
        return new ClickOptions();
    }
    /**
     * @return The click count.
     */
    public int getClickCount() {
        return m_clickCount;
    }
    /**
     * @return The click type.
     */
    public ClickType getClickType() {
        return m_clickType;
    }
    /**
     * @return <code>true</code> if the affected component will be scrolled to
     *         visible before the click is performed, defaults to
     *         <code>true</code>.
     */
    public boolean isScrollToVisible() {
        return m_scrollToVisible;
    }
    /**
     * Sets the click count. This method may be used in a
     * builder pattern manner.
     * 
     * @param clickCount The click count.
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setClickCount(int clickCount) {
        m_clickCount = clickCount;
        return this;
    }
    /**
     * Sets the click type. This method may be used in a
     * builder pattern manner.
     * 
     * @param clickType The click type
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setClickType(ClickType clickType) {
        m_clickType = clickType;
        return this;
    }
    /**
     * Configures whether the affected component will be scrolled to visible
     * before the click is performed.
     * 
     * @param scrollToVisible
     *            <code>true</code> if the affected component will be scrolled
     *            to visible before the click is performed.
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setScrollToVisible(boolean scrollToVisible) {
        m_scrollToVisible = scrollToVisible;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        String clickType = m_clickType == ClickType.CLICKED 
            ? "CLICKED" : "RELEASED"; //$NON-NLS-1$ //$NON-NLS-2$
        String str = this.getClass().getName() + " ClickCount: " + m_clickCount //$NON-NLS-1$
            + " ClickType: " + clickType; //$NON-NLS-1$
        return str;
        
    }
    
    /**
     * Configures the mouse movement strategy.
     * 
     * @param isMoveInSteps   <code>true</code> if the movement strategy 
     *                        should be executed in steps. Otherwise, 
     *                        <code>false</code> (the pointer should 
     *                        "jump" from its current location to the
     *                        target location without any intermediate
     *                        steps).
     *            
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setStepMovement(boolean isMoveInSteps) {
        m_isMoveInSteps = isMoveInSteps;
        return this;
    }
    
    /**
     * Configures on which axis the movement begins. Standard is horizontal.
     * Also on the x axis.
     * 
     * @param firstHorizontal  <code>true</code> if the movement strategy 
     *                        should be executed by going first on the x
     *                        axis.
     *                        <code>false</code> when first going on the y
     *                        axis
     *            
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setFirstHorizontal(boolean firstHorizontal) {
        m_firstHorizontal = firstHorizontal;
        return this;
    }
    
    /**
     * @return <code>true</code> if the movement strategy should be executed in 
     *         steps. Otherwise, <code>false</code> (the pointer should "jump" 
     *         from its current location to the target location without any 
     *         intermediate steps).
     */
    public boolean getStepMovement() {
        return m_isMoveInSteps;
    }
    
    /**
     * @param confirmClick the confirmClick to set
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setConfirmClick(boolean confirmClick) {
        m_confirmClick = confirmClick;
        return this;
    }
    /**
     * @return the confirmClick
     */
    public boolean isConfirmClick() {
        return m_confirmClick;
    }
    /**
     * @param mouseButton the mouseButton to set
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setMouseButton(int mouseButton) {
        m_mouseButton = mouseButton;
        return this;
    }
    
    /**
     * invoke to set the mouse button to "LEFT"
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions left() {
        return setMouseButton(InteractionMode.primary.rcIntValue());
    }
    
    /**
     * @return the mouseButton
     */
    public int getMouseButton() {
        return m_mouseButton;
    }
    /**
     * @param clickModifier the clickModifier to set
     * @return The <code>ClickOptions</code> instance.
     */
    public ClickOptions setClickModifier(ClickModifier clickModifier) {
        m_clickModifier = clickModifier;
        return this;
    }
    /**
     * @return the clickModifier
     */
    public ClickModifier getClickModifier() {
        return m_clickModifier;
    }
    
    /**
     * 
     * @return <code>true</code> if the mouse movement should go first
     *          along the x axis.
     */
    public boolean getFirstHorizontal() {
        return m_firstHorizontal;
    }
}
