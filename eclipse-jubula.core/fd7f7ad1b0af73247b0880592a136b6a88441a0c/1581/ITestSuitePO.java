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
package org.eclipse.jubula.client.core.model;

import java.util.Map;

/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface ITestSuitePO extends INodePO, Comparable {
    /** 
     * Array of Reentry Properties that are valid for use with 
     * Default Event Handlers. This should consist only of Reentry Types
     * that are not sensitive to the context of test execution.
     */
    public static final ReentryProperty[] DEFAULT_REENTRY_PROPS = 
    {ReentryProperty.CONTINUE, ReentryProperty.EXIT, ReentryProperty.STOP};

    /**
     * @return Returns the stepDelay.
     */
    public abstract int getStepDelay();

    /**
     * @param stepDelay The stepDelay to set.
     */
    public abstract void setStepDelay(int stepDelay);
    
    /**
     * @return Returns whether test suite is relevant.
     */
    public abstract boolean getRelevant();

    /**
     * @param relevant whether test suite is relevant.
     */
    public abstract void setRelevant(boolean relevant);
    
    /**
     * @return Returns the AUT.
     */
    public abstract IAUTMainPO getAut();

    /**
     * @param aut The AUT to set.
     */
    public abstract void setAut(IAUTMainPO aut);

    /**
     * @return Returns the isStarted.
     */
    public abstract boolean isStarted();

    /**
     * @param isStarted The isStarted to set.
     */
    public abstract void setStarted(boolean isStarted);

    /**
     * @return Returns the defaultEventHandler.
     */
    public abstract Map<String, Integer> getDefaultEventHandler();

    /**
     * @param defaultEventHandler The defaultEventHandler to set.
     */
    public abstract void setDefaultEventHandler(
        Map<String, Integer> defaultEventHandler);

    /**
     * @return if this TestSuite is editable or not
     */
    public abstract boolean isEditable();
    
    /**
     * set TestSuite is editable or not
     * @param editable editable to set
     */
    public abstract void setEditable(boolean editable);
}