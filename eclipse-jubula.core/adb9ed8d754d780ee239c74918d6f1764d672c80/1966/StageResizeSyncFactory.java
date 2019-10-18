/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.listener.sync;

import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;

/**
 * Provides instances of {@link IStageResizeSync}.
 * 
 */
public final class StageResizeSyncFactory {

    /** 
     * The System Property key for defining which concrete type of
     * {@link IStageResizeSync} this factory will instantiate.
     */
    private static final String STAGE_RESIZE_SYNC_KEY = 
            "org.eclipse.jubula.rc.javafx.stageResizeSync"; //$NON-NLS-1$

    /**
     * System Property Value explicitly defining that no synchronization
     * should occur.
     */
    private static final String SYNC_VALUE_NOOP = "none"; //$NON-NLS-1$

    /**
     * System Property Value explicitly defining that synchronization
     * should consist of waiting a set amount of time.
     */
    private static final String SYNC_VALUE_TIMEOUT = "timeout"; //$NON-NLS-1$
    
    /** private constructor to prevent external instantiation */
    private StageResizeSyncFactory() {
        // prevent external instantiation
    }

    /**
     * 
     * @return an instance of {@link IStageResizeSync}.
     */
    public static IStageResizeSync instance() {
        String stageResizeSyncValue = System.getProperty(STAGE_RESIZE_SYNC_KEY);
        if (SYNC_VALUE_TIMEOUT.equalsIgnoreCase(stageResizeSyncValue)) {
            return new StageResizeTimeoutSync();
        } else if (SYNC_VALUE_NOOP.equalsIgnoreCase(stageResizeSyncValue)) {
            return new StageResizeNoopSync();
        }
        
        return defaultInstance();
    }

    /**
     * 
     * @return a default {@link IStageResizeSync} instance. This defines what 
     * type will be used if no stageResizeSync is explicitly defined.
     */
    private static IStageResizeSync defaultInstance() {
        if (EnvironmentUtils.isLinuxOS()) {
            // a non-noop default for Linux (GTK) is necessary due to:
            // http://bugzilla.bredex.de/1393
            return new StageResizeTimeoutSync();
        }
        
        return new StageResizeNoopSync();
    }
}
