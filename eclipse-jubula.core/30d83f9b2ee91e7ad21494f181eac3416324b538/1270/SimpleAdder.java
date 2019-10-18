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
package org.eclipse.jubula.examples.aut.adder.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

import org.eclipse.jubula.examples.aut.adder.javafx.businessprocess.AutFrameBP;

/**
 * Starter for the Application Under Test. It contains the main() - method.
 * 
 * @created 05.09.2013
 * 
 */
public class SimpleAdder extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            AutFrameBP autFrameBP = new AutFrameBP(primaryStage);
            autFrameBP.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}