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

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 * @created Jan 31, 2008
 */
public class DragAndDropHelper {

    /** the singleton-instance */
    private static DragAndDropHelper instance = null;
    
    /** The mouseButton */
    private int m_mouseButton = -1;
    
    /** the modifier */
    private String m_modifier = StringConstants.EMPTY;
    
    /** Flag if the Mouse is in drag mode (pressed key) */
    private boolean m_isDragMode = false;
    
    /** The Component to drag */
    private Object m_dragComponent = null;
    
    
    /**
     * hidden singleton constructor.
     */
    private DragAndDropHelper() {
        // nothing
    }
      
    /**
     * @return the DragAndDropHelper instance.
     */
    public static DragAndDropHelper getInstance() {
        if (instance == null) {
            instance = new DragAndDropHelper();
        }
        return instance;
    }

    /**
     * @return the mouseButton pressed during drag and drop.
     */
    public int getMouseButton() {
        return m_mouseButton;
    }

    /**
     * @param mouseButton the mouseButton pressed during drag and drop.
     */
    public void setMouseButton(int mouseButton) {
        m_mouseButton = mouseButton;
    }

    /**
     * @return the modifier pressed during drag and drop. 
     */
    public String getModifier() {
        return m_modifier;
    }

    /**
     * @param modifier the modifier pressed during drag and drop. 
     */
    public void setModifier(String modifier) {
        m_modifier = modifier;
    }

    /**
     * @return true if the mouse is in drag mode (a mouse key is pressed),
     * false otherwise.
     */
    public boolean isDragMode() {
        return m_isDragMode;
    }
    
    /**
     * Sets the flag for drag mode of the mouse.
     * @param drag set to true when a mouse button is pressed. Set to false
     * if a the pressed mouse button is released.
     */
    public void setDragMode(boolean drag) {
        m_isDragMode = drag;
    }
    
    /**
     * @return the dragComponent
     */
    public Object getDragComponent() {
        return m_dragComponent;
    }

    /**
     * @param dragComponent the dragComponent to set
     */
    public void setDragComponent(Object dragComponent) {
        m_dragComponent = dragComponent;
    }
    
}
