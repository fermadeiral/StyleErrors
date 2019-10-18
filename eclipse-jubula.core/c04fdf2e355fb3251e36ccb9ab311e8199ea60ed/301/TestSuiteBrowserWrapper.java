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
package org.eclipse.jubula.client.ui.rcp.search.data;

import org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser;
import org.eclipse.swt.widgets.Display;

/**
 * This class is used to retrieve the instance of the {@link TestSuiteBrowser}
 * from outside of the GUI thread.
 *
 * @author BREDEX GmbH
 * @created April 23, 2013
 */
public class TestSuiteBrowserWrapper extends TestSuiteBrowser
        implements Runnable {

    /** The abstract tree view. */
    private TestSuiteBrowser m_tsb;

    /**
     * @return The instance of {@link TestSuiteBrowser}, or null,
     *         if not available.
     */
    private TestSuiteBrowser getInstanceWrapper() {
        // implicitly call run() and wait until finished
        Display.getDefault().syncExec(this);
        return m_tsb;
    }

    /**
     * Executed by the GUI thread to retrieve the selection.
     */
    public void run() {
        m_tsb = TestSuiteBrowser.getInstance();
    }

    /**
     * @return The structured selection from the Test Suite Browser or null,
     *         if not available.
     */
    public static TestSuiteBrowser getInstance() {
        TestSuiteBrowserWrapper tsbWrapper =
                new TestSuiteBrowserWrapper();
        return tsbWrapper.getInstanceWrapper();
    }

}
