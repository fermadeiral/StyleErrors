/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.internal.message;

import java.util.SortedSet;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * This message is send from JubulaClient to AUTServer for changing the mode of
 * the AUTServer. <br>
 * 
 * The new mode is given in the member variable m_mode. <br>
 * Valid values are (see also the constants) <lu> <li>OBJECT_MAPPING</li> <li>
 * TESTING</li> </lu>
 * 
 * The AutServer responses with a AUTModeChangedMessage.
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
 */

public class ChangeAUTModeMessage extends Message {
    /** constant for mode, in which the test is performed */
    public static final int TESTING = 1;

    /** constant for mode for object mapping */
    public static final int OBJECT_MAPPING = 2;

    /** constant for mode for recording */
    public static final int RECORD_MODE = 3;

    /** constant for mode for checking */
    public static final int CHECK_MODE = 4;
    
    /** constant for mode for object mapping */
    public static final int AGENT_OBJECT_MAPPING = 5;

    /** Static version */
    public static final double VERSION = 1.0;

    /** the mode to change to */
    private int m_mode;

    /** modifier to map an item/record */
    private int m_mappingKeyModifier = 0;

    /** modifier to map an item/record together with its parents */
    private int m_mappingWithParentsKeyModifier = 0;

    /** key to map an item/record */
    private int m_mappingKey = 0;
    
    /** key to map an item/record together with its parents */
    private int m_mappingWithParentsKey = 0;

    /** mouse button to map an item/record */
    private int m_mappingMouseButton = 0;

    /** mouse button to map an item/record */
    private int m_mappingWithParentsMouseButton = 0;

    /** modifier to record Application */
    private int m_key2Modifier = 0;

    /** key to record Application */
    private int m_key2 = 0;

    /** modifier to start/stop check mode */
    private int m_checkModeKeyModifier = 0;

    /** key to start/stop check mode */
    private int m_checkModeKey = 0;

    /** modifier for check current component */
    private int m_checkCompKeyModifier = 0;

    /** key for check current component */
    private int m_checkCompKey = 0;

    /** true if recorded actions dialog should be open, false otherwise */
    private boolean m_dialogOpen;

    /** singleLineTrigger for Observation Mode */
    private SortedSet m_singleLineTrigger;

    /** multiLineTrigger for Observation Mode */
    private SortedSet m_multiLineTrigger;

    /** default constructor */
    public ChangeAUTModeMessage() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.CHANGE_AUT_MODE_COMMAND;
    }

    /**
     * @return Returns the mode.
     */
    public int getMode() {
        return m_mode;
    }

    /**
     * @param mode
     *            The mode to set.
     */
    public void setMode(int mode) {
        m_mode = mode;
    }

    /**
     * @return Returns the key to map an item/record.
     */
    public int getMappingKey() {
        return m_mappingKey;
    }

    /**
     * @param key
     *            The key to set.
     */
    public void setMappingKey(int key) {
        m_mappingKey = key;
    }

    /**
     * @return Returns the key to map an item/record.
     */
    public int getMappingWithParentsKey() {
        return m_mappingWithParentsKey;
    }

    /**
     * @param key
     *            The key to set.
     */
    public void setMappingWithParentsKey(int key) {
        m_mappingWithParentsKey = key;
    }

    /**
     * @return the mouse button used for object mapping
     */
    public int getMappingMouseButton() {
        return m_mappingMouseButton;
    }

    /**
     * @param mouseButton
     *            the mouse button to use for object mapping
     */
    public void setMappingWithParentsMouseButton(int mouseButton) {
        m_mappingWithParentsMouseButton = mouseButton;
    }

    /**
     * @return the mouse button used for object mapping
     */
    public int getMappingWithParentsMouseButton() {
        return m_mappingWithParentsMouseButton;
    }

    /**
     * @param mouseButton
     *            the mouse button to use for object mapping
     */
    public void setMappingMouseButton(int mouseButton) {
        m_mappingMouseButton = mouseButton;
    }

    /**
     * @return Returns the keyModifier.
     */
    public int getMappingKeyModifier() {
        return m_mappingKeyModifier;
    }

    /**
     * @param keyModifier
     *            The keyModifier to set.
     */
    public void setMappingKeyModifier(int keyModifier) {
        m_mappingKeyModifier = keyModifier;
    }

    /**
     * @return Returns the keyModifier.
     */
    public int getMappingWithParentsKeyModifier() {
        return m_mappingWithParentsKeyModifier;
    }

    /**
     * @param keyModifier
     *            The keyModifier to set.
     */
    public void setMappingWithParentsKeyModifier(int keyModifier) {
        m_mappingWithParentsKeyModifier = keyModifier;
    }

    /**
     * @return Returns the key for Application record
     */
    public int getKey2() {
        return m_key2;
    }

    /**
     * @param key2
     *            The key to set.
     */
    public void setKey2(int key2) {
        m_key2 = key2;
    }

    /**
     * @return Returns the keyMod for Application component.
     */
    public int getKey2Modifier() {
        return m_key2Modifier;
    }

    /**
     * @param mod
     *            The keyMod to set.
     */
    public void setKey2Modifier(int mod) {
        m_key2Modifier = mod;
    }

    /**
     * @return Returns the key for checkMode
     */
    public int getCheckModeKey() {
        return m_checkModeKey;
    }

    /**
     * @param checkModeKey
     *            The checkModeKey to set.
     */
    public void setCheckModeKey(int checkModeKey) {
        m_checkModeKey = checkModeKey;
    }

    /**
     * @return Returns the checkModeKeyMod for checkMode.
     */
    public int getCheckModeKeyModifier() {
        return m_checkModeKeyModifier;
    }

    /**
     * @param checkModeKeyMod
     *            the checkModeKeyMod to set.
     */
    public void setCheckModeKeyModifier(int checkModeKeyMod) {
        m_checkModeKeyModifier = checkModeKeyMod;
    }

    /**
     * @return the checkCompKey
     */
    public int getCheckCompKey() {
        return m_checkCompKey;
    }

    /**
     * @param checkCompKey
     *            the checkCompKey to set
     */
    public void setCheckCompKey(int checkCompKey) {
        m_checkCompKey = checkCompKey;
    }

    /**
     * @return the checkCompKeyMod
     */
    public int getCheckCompKeyModifier() {
        return m_checkCompKeyModifier;
    }

    /**
     * @param checkCompKeyMod
     *            the checkCompKeyMod to set
     */
    public void setCheckCompKeyModifier(int checkCompKeyMod) {
        m_checkCompKeyModifier = checkCompKeyMod;
    }

    /**
     * @return true if recorded actions dialog should be open, false otherwise
     */
    public boolean getRecordDialogOpen() {
        return m_dialogOpen;
    }

    /**
     * @param dialogOpen
     *            set state of recorded actions dialog
     */
    public void setRecordDialogOpen(boolean dialogOpen) {
        m_dialogOpen = dialogOpen;
    }

    /**
     * @return singleLineTrigger for Observation Mode
     */
    public SortedSet getSingleLineTrigger() {
        return m_singleLineTrigger;
    }

    /**
     * @param singleLineTrigger
     *            singleLineTrigger for Observation Mode
     */
    public void setSingleLineTrigger(SortedSet singleLineTrigger) {
        m_singleLineTrigger = singleLineTrigger;
    }

    /**
     * @return multiLineTrigger for Observation Mode
     */
    public SortedSet getMultiLineTrigger() {
        return m_multiLineTrigger;
    }

    /**
     * @param multiLineTrigger
     *            multiLineTrigger for Observation Mode
     */
    public void setMultiLineTrigger(SortedSet multiLineTrigger) {
        m_multiLineTrigger = multiLineTrigger;
    }
}