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

import java.util.concurrent.Callable;

import javafx.scene.text.Text;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Text Adapter
 *
 * @author BREDEX GmbH
 * @created 07.03.2014
 */
public class TextAdapter extends JavaFXComponentAdapter<Text> implements
        ITextComponent {

    /**
     * Creates an object with the adapted Text.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>Text</code>
     */
    public TextAdapter(Text objectToAdapt) {
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
