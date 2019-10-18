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
package org.eclipse.jubula.rc.javafx.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.rc.javafx.tester.util.WindowsUtil;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Provides access to all instantiated windows, by accessing a private field in
 * the <code>Stage</code> class with reflection. Whenever a <code>Stage</code>
 * is instantiated or closed a reference is stored in this field automatically
 * by JavaFX.
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 *
 */
public class CurrentStages {
    /** environment variable */
    public static final String JUBULA_FX_POLLING_RATE = "JUBULA_FX_POLLING_RATE"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(CurrentStages.class);

    /** The Window list **/
    private static ObservableList<Window> windows = 
            FXCollections.observableArrayList();

    /** private Constructor **/
    private CurrentStages() {
        // private Constructor
    }

    static {
        String env = EnvironmentUtils
                .getProcessOrSystemProperty(JUBULA_FX_POLLING_RATE);
        double pollingRate = 5;
        if (env != null) {
            try {
                pollingRate = Double.parseDouble(env);
            } catch (NumberFormatException nf) {
                LOG.info("Could not convert the value." //$NON-NLS-1$
                        + "using standard polling rate 5ms"); //$NON-NLS-1$
            }
        }
        Timeline checkWindowList = new Timeline(new KeyFrame(
                Duration.millis(pollingRate), new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        // USE OF DEPRECATED API
                        Iterator<Window> it = WindowsUtil.getWindowIterator();
                        List<Window> tempWin = new ArrayList<>();

                        // "Convert" iterator to list for removing and add new
                        // windows
                        while (it.hasNext()) {
                            Window w = it.next();
                            tempWin.add(w);
                            if (!windows.contains(w)) {
                                windows.add(w);
                            }
                        }
                        // Now iterate over the windows list and find windows
                        // which can be removed
                        it = windows.listIterator();
                        while (it.hasNext()) {
                            Window w = it.next();
                            if (!tempWin.contains(w)) {
                                it.remove();
                            }
                        }
                    }
                }));
        checkWindowList.setCycleCount(Timeline.INDEFINITE);
        checkWindowList.play();
    }

    /**
     * Gets the first Window in the list
     *
     * @return the Window
     */
    public static Window getfirstStage() {
        for (Window window : windows) {
            if (window instanceof Stage) {
                return window;
            }
        }
        return null;
    }

    /**
     * Returns the complete list of windows
     * 
     * @return the Window list
     */
    public static List<Window> getWindowList() {
        return windows;
    }

    /**
     * Gets the Window with focus in the list
     *
     * @return the Window
     */
    public static Window getfocusStage() {
        Window fStage = null;
        for (Window win : windows) {
            if (win.isFocused() && win instanceof Stage) {
                fStage = win;
            }
        }
        return fStage;
    }

    /**
     * Adds a <code>ListChangeListener</code> to the windows-List
     *
     * @param listener
     *            the listener
     */
    public static void addStagesListener(ListChangeListener<Window> listener) {
        windows.addListener(listener);
    }

    /**
     * Removes a <code>ListChangeListener</code> from the windows-List
     *
     * @param listener
     *            the listener
     */
    public static void removeStagesListener(
            ListChangeListener<Window> listener) {
        windows.removeListener(listener);
    }
}