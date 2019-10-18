/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools.internal.constants;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author BREDEX GmbH
 *
 */
public class MappingKeyConstants {
    /** Key for int value to be stored as a resource property */
    public static final int MAPPINGMOD1_KEY_DEFAULT = 
        InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
    /** Key for int value to be stored as a resource property */
    public static final int MAPPING_TRIGGER_DEFAULT = KeyEvent.VK_Q;
    
    /** Default value for Object Mapping trigger type */
    public static final int MAPPING_TRIGGER_TYPE_DEFAULT = 
        InputConstants.TYPE_KEY_PRESS;
    /**
     *  utility
     */
    private MappingKeyConstants() {
        // utility
    }

}
