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
package org.eclipse.jubula.examples.aut.adder.javafx.gui;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This class represents the AUT-Frame. It combines the CalcMenuBar and
 * CalculatorPanel.
 * 
 */
public class AutFrame {

    /**
     * AUT-Frame Layout
     */
    private BorderPane m_borderPane;

    /**
     * AUT-Frame Stage
     */
    private Stage m_stage;

    /**
     * AUT-Frame Scene
     */
    private Scene m_scene;

    /**
     * Calculator panel
     */
    private CalculatorPanel m_calcPanel;

    /**
     * Menu Bar
     */
    private CalcMenuBar m_calcBar;

    /**
     * Constructor that Initializes the AUT Frame
     * 
     * @param stage
     *            The Stage on which the AUT Frame should be visualized
     */
    public AutFrame(Stage stage) {
        m_borderPane = new BorderPane();
        m_stage = stage;
        m_calcPanel = new CalculatorPanel();
        initialize();
    }

    /**
     * Manages the Layout of the AUT Frame
     */
    private void initialize() {
        m_stage.setTitle("Adder"); //$NON-NLS-1$
        m_scene = new Scene(m_borderPane, 300, 250);

        m_borderPane.setCenter(m_calcPanel);

        m_calcBar = new CalcMenuBar();

        m_borderPane.setTop(m_calcBar);
    }

    /**
     * Makes the AUT Frame visible on the Stage
     */
    public void show() {
        m_stage.setScene(m_scene);
        m_stage.show();
    }

    /**
     * @return Returns the CalculationPanel
     */
    public CalculatorPanel getCalcPanel() {
        return m_calcPanel;
    }

    /**
     * @return Returns the MenuBar
     */
    public CalcMenuBar getCalcBar() {
        return m_calcBar;
    }
}
