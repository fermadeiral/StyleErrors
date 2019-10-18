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
package org.eclipse.jubula.autagent.gui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jubula.autagent.gui.ObjectMappingSettingsFrame;
import org.eclipse.jubula.client.core.constants.Constants;
import org.eclipse.jubula.client.core.constants.InputCodeHelper.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this class is managing the keys for the key combination which are saved
 * and loaded from a properties file. It also manages the default keys and
 * sets them if there is no properties for the key combination.
 * @author BREDEX GmbH
 */
public class AgentOMKeyProperitesUtils {

    /** the properties for the key settings */
    public static final Properties KEY_PROPERTIES = new Properties();
    /** the properties key for the input type */
    private static final String INPUT_TYPE = "INPUT_TYPE"; //$NON-NLS-1$
    /** the properties key for the input */
    private static final String INPUT = "INPUT"; //$NON-NLS-1$
    /** the properties key for the modifier */
    private static final String MODIFIER = "MODIFIER"; //$NON-NLS-1$

    /** the path to the properties file */
    private static final String PROPERTIES_PATH =
            System.getProperty("user.home") //$NON-NLS-1$
                    + "/.jubula/OMagent.props"; //$NON-NLS-1$
    /** the logger */
    private static final Logger LOG =
            LoggerFactory.getLogger(AgentOMKeyProperitesUtils.class);

    static {
        loadPropertiesFromFile();
    }

    /**
     * Utils
     */
    private AgentOMKeyProperitesUtils() {
        // hide
    }

    /**
     * loading the properties from the file given by the
     * {@link ObjectMappingSettingsFrame#PROPERTIES_PATH} if its exists. This method is only
     * logging if there is an error
     */
    public static void loadPropertiesFromFile() {
        File file = new File(PROPERTIES_PATH);
        if (file.exists()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                KEY_PROPERTIES.load(inputStream);
            } catch (Exception e) {
                LOG.error("Exception during reading of properties", e); //$NON-NLS-1$
            }
        }
    }

    /**
     * Writes to properties to the file located {@link AgentOMKeyProperitesUtils#PROPERTIES_PATH}
     */
    public static void writePropertiesToFile() {
        File file = new File(PROPERTIES_PATH);
        if (!file.exists() && !file.isDirectory() && file.canWrite()) {
            try {
                file.createNewFile();
            } catch (IOException ie) {
                LOG.error("Except during creation of File", ie); //$NON-NLS-1$
                return;
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            KEY_PROPERTIES.store(outputStream, "objectMappingKeys"); //$NON-NLS-1$
        } catch (IOException ie) {
            LOG.error("Exception during writing of OM keys", ie); //$NON-NLS-1$
        }
    }

    /**
     * get the Modifier keyCode to press from the properties or the default one
     * @return the modifier keycode
     */
    public static int getModifier() {
        try {
            return Integer.parseInt(KEY_PROPERTIES.getProperty(MODIFIER));
        } catch (Exception e) {
            return Constants.MAPPINGMOD1_KEY_DEFAULT;
        }

    }

    /**
     * gets the {@link UserInput} to press from the properties or the default one
     * @return the {@link UserInput}
     */
    public static UserInput getInput() {
        try {
            int keyCode = Integer.parseInt(
                    KEY_PROPERTIES.getProperty(INPUT));
            int keyType = Integer.parseInt(
                    KEY_PROPERTIES.getProperty(INPUT_TYPE));
            return new UserInput(keyCode, keyType);
        } catch (Exception e) {
            return new UserInput(Constants.MAPPING_TRIGGER_DEFAULT,
                    Constants.MAPPING_TRIGGER_TYPE_DEFAULT);
        }

    }
    
    /**
     * @param modifier the modifier KeyCode to set
     */
    public static void setModifier(int modifier) {
        KEY_PROPERTIES.setProperty(MODIFIER, Integer.toString(modifier));
    }

    /**
     * @param input the {@link UserInput} to set
     */
    public static void setInput(UserInput input) {
        KEY_PROPERTIES.setProperty(INPUT,
                Integer.toString(input.getCode()));
        KEY_PROPERTIES.setProperty(INPUT_TYPE,
                Integer.toString(input.getType()));
    }
}
