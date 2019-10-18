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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class represents the about dialog.
 * 
 */
public class AboutDialog {

    /**
     * About-Dialog Stage
     */
    private Stage m_dialogStage;

    /**
     * About-Dialog Text
     */
    private Label m_aboutText;

    /**
     * About-Dialog Layout
     */
    private BorderPane m_borderPane;

    /**
     * About-Dialog Scene
     */
    private Scene m_aboutScene;

    /**
     * Constructor that Initializes the AboutDialog
     */
    public AboutDialog() {

        m_dialogStage = new Stage();
        m_dialogStage.initModality(Modality.APPLICATION_MODAL);

        m_aboutText = new Label("Application under Test\n" + "\ncopyright by " //$NON-NLS-1$ //$NON-NLS-2$
                + "BREDEX Software GmbH"); //$NON-NLS-1$
        m_borderPane = new BorderPane();

        initialize();
    }

    /**
     * Manages the Layout of the AboutDialog
     */
    private void initialize() {
        m_borderPane.setPadding(new Insets(10));
        m_aboutScene = new Scene(m_borderPane);

        Button okBtn = new Button("OK"); //$NON-NLS-1$
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_dialogStage.close();
            }
        });

        m_dialogStage.setTitle("about"); //$NON-NLS-1$

        m_borderPane.setCenter(m_aboutText);
        m_borderPane.setBottom(okBtn);
        BorderPane.setAlignment(okBtn, Pos.CENTER);
    }

    /**
     * Makes the AUT Frame visible on the Stage
     */
    public void show() {
        m_dialogStage.setScene(m_aboutScene);
        m_dialogStage.showAndWait();
    }

}
