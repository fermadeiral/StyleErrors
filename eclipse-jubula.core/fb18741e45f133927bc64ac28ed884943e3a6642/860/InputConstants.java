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
package org.eclipse.jubula.tools.internal.constants;

/**
 * @author BREDEX GmbH
 * @created Sep 17, 2009
 */
public interface InputConstants {

    // Types of possible input
    /** key press */
    public static int TYPE_KEY_PRESS = 1;

    /** mouse click */
    public static int TYPE_MOUSE_CLICK = 2;

    /** Constant for no input (no key pressed or no button clicked) */
    public int NO_INPUT = -1;
    
}
