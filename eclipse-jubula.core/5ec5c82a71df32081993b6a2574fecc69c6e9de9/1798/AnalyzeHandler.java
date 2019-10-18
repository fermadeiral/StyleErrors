/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle;

/**
 * 
 * @author marcell
 *
 */
public class AnalyzeHandler {
    
    /** Singleton instance */
    private static AnalyzeHandler instance;
    
    /** status of the handler */
    private static boolean active = false;

    /** private constructor because of Singleton */
    private AnalyzeHandler() {
        // Singleton
    }
    
    /** @return Singleton instance */
    public static AnalyzeHandler getInstance() {
        if (instance == null) {
            instance = new AnalyzeHandler();
        }
        return instance;
    }   
    
    /** starts the plugin */
    public void start() {
        ExtensionHelper.initAnalyzes();
        active = true;
    }
    
    /** */
    public void stop() {
        active = false;
    }
    
    /**
     * @return true, if active
     */
    public static boolean isActive() {
        return active;
    }
}
