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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Adapter for the SplitPane.
 * 
 * @author BREDEX GmbH
 * @created 30.05.2014
 */
public class SplitPaneAdapter extends JavaFXComponentAdapter<SplitPane>
        implements IContainerAdapter {

    /**
     * 
     * @param objectToAdapt
     *            the SplitPane to Adapt
     */
    public SplitPaneAdapter(SplitPane objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public List<Node> getContent() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getContent", //$NON-NLS-1$
                new Callable<List<Node>>() {

                    @Override
                    public List<Node> call() throws Exception {
                        return getRealComponent().getItems();
                    }
                });
    }
}
