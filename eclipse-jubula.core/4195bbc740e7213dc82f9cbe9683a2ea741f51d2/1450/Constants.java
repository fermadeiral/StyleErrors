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
package org.eclipse.jubula.client.core.constants;

/**
 * Constants
 * 
 * @author BREDEX GmbH
 */
public final class Constants {
    /** the dashboard summaryId parameter name*/
    public static final String DASHBOARD_SUMMARY_PARAM = "summaryId"; //$NON-NLS-1$

    /** the dashboard result node parameter name*/
    public static final String DASHBOARD_RESULT_NODE_PARAM = "resultNode"; //$NON-NLS-1$

    /** exit code in case of invalid options */
    public static final int INVALID_VALUE = -2;
    /**
     * Private constructor to avoid instantiation of constants class.
     */
    private Constants() {
        // Nothing to initialize.
    }
}
