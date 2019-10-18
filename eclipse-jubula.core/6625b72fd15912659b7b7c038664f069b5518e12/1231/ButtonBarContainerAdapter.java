/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.j8u40.tester.adapter;

import java.util.List;
import java.util.concurrent.Callable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.stage.Window;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.adapter.AbstractComponentAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.IContainerAdapter;

/**
 * @author BREDEX GmbH
 * @param <T>
 *            a sub-type of Dialog
 */
public class ButtonBarContainerAdapter<T extends ButtonBar> extends
        AbstractComponentAdapter<T> implements IContainerAdapter {

    /**
     * @param objectToAdapt
     *            the object to adapt
     */
    public ButtonBarContainerAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    /** {@inheritDoc} */
    public List<Node> getContent() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getContent", //$NON-NLS-1$
                new Callable<List<Node>>() {

                    @Override
                    public List<Node> call() throws Exception {
                        return getRealComponent().getButtons();
                    }
                });
    }

    /** {@inheritDoc} */
    public ReadOnlyObjectProperty<? extends Window> getWindow() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getWindow", //$NON-NLS-1$
                new Callable<ReadOnlyObjectProperty<? extends Window>>() {

                    @Override
                    public ReadOnlyObjectProperty<? extends Window> call()
                            throws Exception {
                        Scene scene = getRealComponent().getScene();
                        if (scene == null) {
                            return null;
                        }
                        return scene.windowProperty();
                    }
                });
    }

}
