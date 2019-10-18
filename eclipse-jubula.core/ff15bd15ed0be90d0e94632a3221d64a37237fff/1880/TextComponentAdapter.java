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

import javafx.scene.control.TextInputControl;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextInputComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Text input adapter
 *
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public class TextComponentAdapter extends
        JavaFXComponentAdapter<TextInputControl>
        implements ITextInputComponent {

    /**
     * Creates an object with the adapted TextInputControl.
     *
     * @param objectToAdapt
     *            this must be an object of the Type
     *            <code>TextInputControl</code>
     */
    public TextComponentAdapter(TextInputControl objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
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

    @Override
    public void setSelection(final int start) {
        EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<Void>() { //$NON-NLS-1$

                    @Override
                    public Void call() throws Exception {
                        getRealComponent().positionCaret(start);
                        return null;
                    }
                });
    }

    @Override
    public void setSelection(final int start, final int end) {
        EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<Void>() { //$NON-NLS-1$

                    @Override
                    public Void call() throws Exception {
                        getRealComponent().selectRange(start, end);
                        return null;
                    }
                });
    }

    @Override
    public String getSelectionText() {
        String text = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        return getRealComponent().getSelectedText();
                    }
                });
        return text;
    }

    @Override
    public void selectAll() {
        EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<Void>() { //$NON-NLS-1$

                    @Override
                    public Void call() throws Exception {
                        getRealComponent().selectAll();
                        return null;
                    }
                });
    }

    @Override
    public boolean isEditable() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isEditable", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        return getRealComponent().isEditable();
                    }
                });
        return result;
    }

}
