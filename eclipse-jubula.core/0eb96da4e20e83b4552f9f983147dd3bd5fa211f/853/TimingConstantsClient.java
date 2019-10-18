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
 * @created Oct 1, 2010
 */
public interface TimingConstantsClient {
    /** minimal step speed */
    public static final int MIN_STEP_SPEED = 0;
    
    /** delay (in ms) before starting a TS, allowing for minimizing 
     * effects to settle down */     
    public static final long START_TEST_SUITE_DELAY = 2000;
}
