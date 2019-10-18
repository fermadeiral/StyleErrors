/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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

import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.NodeTraverseHelper;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Cell;

/**
 * Adapter for getting the Text of a Cell or the Properties and respectively of
 * components within the cell
 * 
 * @author BREDEX GmbH
 * @created 30.3.2016
 */
public class CellAdapter extends JavaFXComponentAdapter<Cell> 
    implements ITextComponent {

    /**
     * Constructor
     * @param objectToAdapt the ui component to adapt
     */
    public CellAdapter(Cell objectToAdapt) {
        super(objectToAdapt);
        
    }

    @Override
    public String getText() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getProperty", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        Cell cell = getRealComponent();
                        String txt = cell.getText();
                        if (txt == null) {
                            List<String> strings = NodeTraverseHelper
                                    .findStrings(cell);
                            txt = String.join(" ", strings); //$NON-NLS-1$
                        }
                        return txt;
                    }
                });
    }

    @Override
    public String getPropteryValue(String propertyname) {
        Object prop = EventThreadQueuerJavaFXImpl.invokeAndWait("getProperty", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        RobotException originalException;
                        Cell cell = getRealComponent();
                        try {
                            return getRobot().getPropertyValue(
                                    cell, propertyname);
                        } catch (RobotException e) {
                            // Do nothing here. We are trying to check if
                            // there is a component within the cell and
                            // under the mouse which has that property
                            originalException = e;
                        }

                        List<Node> childNodes = NodeTraverseHelper
                                .getInstancesOf(cell, Node.class);
                        Point2D mousePos = new Point2D(
                                getRobot().getCurrentMousePosition().getX(),
                                getRobot().getCurrentMousePosition().getY());
                        for (Node n : childNodes) {
                            if (NodeBounds.checkIfContains(mousePos, n)) {
                                try {
                                    return getRobot()
                                            .getPropertyValue(n, propertyname);
                                } catch (RobotException e) {
                                    // Do nothing here. Just check more components
                                }
                            }

                        }
                        // We haven't found the Property in the cell or a
                        // subcomponent, therefore this exception is thrown
                        throw new StepExecutionException(
                                originalException.getMessage(),
                                EventFactory.createActionError(
                                        TestErrorEvent
                                        .PROPERTY_NOT_ACCESSABLE));
                    }
                });
        return String.valueOf(prop);
    }
    
    

}
