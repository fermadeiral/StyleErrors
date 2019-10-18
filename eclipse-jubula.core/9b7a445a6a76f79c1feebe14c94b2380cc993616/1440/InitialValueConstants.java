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
package org.eclipse.jubula.client.core.constants;

/**
 * Constants for initial values e.g. for POs names.
 * 
 * @author BREDEX GmbH
 * @created May 18, 2010
 */
public final class InitialValueConstants {
    /** the default test case name */
    public static final String DEFAULT_TEST_CASE_NAME = "New Test Case"; //$NON-NLS-1$

    /** the default observed test case name */
    public static final String DEFAULT_TEST_CASE_NAME_OBSERVED = "Observed"; //$NON-NLS-1$
    
    /** the default test suite name */
    public static final String DEFAULT_TEST_SUITE_NAME = "New Test Suite"; //$NON-NLS-1$

    /** the default test job name */
    public static final String DEFAULT_TEST_JOB_NAME = "New Test Job"; //$NON-NLS-1$

    /** the default cap name */
    public static final String DEFAULT_CAP_NAME = "new Test Step"; //$NON-NLS-1$

    /** the default category name */
    public static final String DEFAULT_CATEGORY_NAME = "New category"; //$NON-NLS-1$
    
    /**
     * Private constructor to avoid instantiation of constants class.
     */
    private InitialValueConstants() {
        // Nothing to initialize.
    }
}
