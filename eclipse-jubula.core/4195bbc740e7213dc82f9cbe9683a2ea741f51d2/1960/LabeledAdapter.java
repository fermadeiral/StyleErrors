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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.util.concurrent.Callable;

import javafx.scene.control.Labeled;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Label Adapter
 *
 * @param <T> a subtype of Labeled 
 *
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public class LabeledAdapter<T extends Labeled> extends
    JavaFXComponentAdapter<T> implements ITextComponent {

    /**
     * Creates an object with the adapted Label.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>Labeled</code>
     */
    public LabeledAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        String text = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        return getRealComponent().getText();
                    }
                });
        return text;
    }
}
