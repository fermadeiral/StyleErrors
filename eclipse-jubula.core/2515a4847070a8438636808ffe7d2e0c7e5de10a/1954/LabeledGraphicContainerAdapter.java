package org.eclipse.jubula.rc.javafx.tester.adapter;

/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.stage.Window;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Container Adapter for Components which are derived from
 * javafx.scene.control.Labeled. It returns the referenced node of the
 * GraphicNode property, to allow the mapping of this Node.
 * 
 * @author BREDEX GmbH
 * @created 2.2.2015
 * @param <T>
 */
public class LabeledGraphicContainerAdapter<T extends Labeled> extends
        AbstractComponentAdapter<T> implements IContainerAdapter {

    /**
     * Constructor
     * @param objectToAdapt the object to adapt
     */
    public LabeledGraphicContainerAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public List<Node> getContent() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getContent", //$NON-NLS-1$
                new Callable<List<Node>>() {

                    @Override
                    public List<Node> call() throws Exception {
                        List<Node> r = new ArrayList<Node>();
                        r.add(getRealComponent().getGraphic());
                        return r;
                    }
                });
    }

    @Override
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
