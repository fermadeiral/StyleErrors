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
package org.eclipse.jubula.examples.aut.adder.javafx.businessprocess;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import org.eclipse.jubula.examples.aut.adder.javafx.gui.AutFrame;
import org.eclipse.jubula.examples.aut.adder.javafx.gui.CalcMenuBar;
import org.eclipse.jubula.examples.aut.adder.javafx.gui.CalculatorPanel;

/**
 * This class handles the business process for the AutFrame, concerning the
 * Initialization and the Event handling.
 * 
 */
public class AutFrameBP {

    /**
     * AUT frame
     */
    private AutFrame m_autFrame;

    /**
     * Calculator panel
     */
    private CalculatorPanel m_calcPanel;

    /**
     * Menu Bar
     */
    private CalcMenuBar m_cBar;

    /**
     * Constructor that Initializes the AUT business process
     * 
     * @param stage
     *            The Stage on which the AUT Frame should be visualized
     */
    public AutFrameBP(Stage stage) {
        m_autFrame = new AutFrame(stage);
        m_calcPanel = m_autFrame.getCalcPanel();
        m_cBar = m_autFrame.getCalcBar();

        m_calcPanel.getResultButton().setOnAction(
                new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        evaluate();
                    }

                });

        m_cBar.getReset().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_calcPanel.reset();
            }
        });

        m_cBar.getQuit().setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                javafx.application.Platform.exit();
            }

        });
    }

    /**
     * Sums up the Values and sets the result on the result TextField
     */
    private void evaluate() {
        try {
            int value1 = Integer.parseInt(m_calcPanel.getAddend1());
            int value2 = Integer.parseInt(m_calcPanel.getAddend2());

            if (value1 == 17 && value2 == 4) {
                m_calcPanel.setResult("jackpot"); //$NON-NLS-1$
            } else {
                m_calcPanel.setResult(String.valueOf(value1 + value2));
            }
        } catch (NumberFormatException nfe) {
            m_calcPanel.setResult("#error"); //$NON-NLS-1$
        }
    }

    /**
     * 
     */
    public void show() {
        m_autFrame.show();
    }

}
