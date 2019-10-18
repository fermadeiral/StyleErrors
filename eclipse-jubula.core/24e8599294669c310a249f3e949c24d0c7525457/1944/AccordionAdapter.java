/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.Rectangle;
import java.util.concurrent.Callable;

import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITabbedComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;

/**
 * Adapter for a {@link Accordion}.
 * 
 */
public class AccordionAdapter extends JavaFXComponentAdapter<Accordion>
        implements ITabbedComponent {

    /**
     * 
     * @param objectToAdapt 
     */
    public AccordionAdapter(Accordion objectToAdapt) {
        super(objectToAdapt);
    }

    /**
     * {@inheritDoc}
     */
    public int getTabCount() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTabCount", new Callable<Integer>() { //$NON-NLS-1$
                    public Integer call() {
                        return getRealComponent().getPanes().size();
                    }
                }); 
    }
    
    /**
     * {@inheritDoc}
     */
    public String getTitleofTab(final int index) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTitleOfTab", new Callable<String>() { //$NON-NLS-1$
                    public String call() {
                        return getRealComponent().getPanes().get(index)
                                .getText();
                    }
                });        
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getBoundsAt(final int index) {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getBoundsAt", new Callable<Rectangle>() { //$NON-NLS-1$
                    public Rectangle call() {
                        getRealComponent().requestLayout();
                        getRealComponent().layout();    
                        return NodeBounds.getRelativeBounds(
                                getRealComponent().getPanes().get(index), 
                                getRealComponent());
                    }

                }); 
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabledAt(final int index) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isEnabledAt", new Callable<Boolean>() { //$NON-NLS-1$
                    public Boolean call() {
                        return !getRealComponent().getPanes().get(index)
                                .isDisabled();
                    }
                }); 
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
            "getSelectedIndex", new Callable<Integer>() { //$NON-NLS-1$
                @Override
                public Integer call() throws Exception {
                    TitledPane expandedPane = 
                            getRealComponent().getExpandedPane();
                    if (expandedPane == null) {
                        return -1;
                    }

                    return getRealComponent().getPanes().indexOf(expandedPane);
                }
            });
    }

}
