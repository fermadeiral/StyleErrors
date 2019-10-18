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
package org.eclipse.jubula.rc.swt.driver;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.widgets.Control;


/**
 * @author BREDEX GmbH
 * @created Feb 6, 2008
 */
@Deprecated
public class DragAndDropHelperSwt {

    /** the singleton-instance */
    private static DragAndDropHelperSwt instance = null;
    
    /** The mouseButton */
    private int m_mouseButton = -1;
    
    /** the modifier */
    private String m_modifier = StringConstants.EMPTY;
    
    /** The Component to drag */
    private Control m_dragComponent = null;
   
    /**
     * hidden singleton constructor.
     */
    private DragAndDropHelperSwt() {
        // nothing yet
    }
    
    
    /**
     * @return the DragAndDropHelper instance.
     */
    public static DragAndDropHelperSwt getInstance() {
        if (instance == null) {
            instance = new DragAndDropHelperSwt();
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
     * @return the dragComponent
     */
    public Control getDragComponent() {
        return m_dragComponent;
    }


    /**
     * @param dragComponent the dragComponent to set
     */
    public void setDragComponent(Control dragComponent) {
        m_dragComponent = dragComponent;
    }

}
