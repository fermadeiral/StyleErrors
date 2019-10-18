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
package org.eclipse.jubula.client.core.constants;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.tools.internal.constants.InputConstants;


/**
 * Provides helper methods and classes for mapping input codes and input types 
 * to their corresponding inputs.
 *
 * @author BREDEX GmbH
 * @created 08.07.2005
 */
public class InputCodeHelper {

    /**
     * Encapsulates the information for a single input 
     * (ex. key press, mouse click).
     *
     * @author BREDEX GmbH
     * @created Sep 18, 2009
     */
    public static class UserInput {

        /** the type of input represented */
        private int m_type;

        /** 
         * a code containing additional information about the input
         * the actual meaning of the code is dependant on the input type
         */
        private int m_code;
        
        /**
         * Constructor
         * 
         * @param code Specific information about the content of the input.
         * @param type The type of input.
         */
        public UserInput(int code, int type) {
            m_code = code;
            m_type = type;
        }

        /**
         * 
         * @return type-specific information about the content of the input.
         */
        public int getCode() {
            return m_code;
        }
        
        /**
         * 
         * @return the type of the input.
         */
        public int getType() {
            return m_type;
        }

        /**
         * {@inheritDoc}
         */
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof UserInput) {
                UserInput input = (UserInput)obj;
                return new EqualsBuilder().append(getCode(), input.getCode())
                    .append(getType(), input.getType()).isEquals();
            }
            
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public int hashCode() {
            return new HashCodeBuilder().append(getCode())
                .append(getType()).toHashCode();
        }
    }
    
    /**
     * instance
     */
    private static InputCodeHelper instance = null;
    
    /**
     * modifier keycodes
     */
    private int[] m_modifier = new int[6];
    
    /** all key-press inputs */
    private UserInput[] m_keys;
    
    /** all inputs */
    private UserInput[] m_inputs;

    /**
     * general inputs text
     */
    private String[] m_inputStrings;
    
    /**
     * keys text
     */
    private String[] m_keyStrings;
    
    /**
     * modifier text
     */
    private String[] m_modifierString;
    
    
    /**
     * private constructor
     *
     */
    private InputCodeHelper() {
        
        m_modifier[0] = InputEvent.CTRL_DOWN_MASK;
        m_modifier[1] = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        m_modifier[2] = InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK;
        m_modifier[3] = InputEvent.ALT_DOWN_MASK;
        m_modifier[4] = InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        m_modifier[5] = 0; // no modifier pressed
        
        List<UserInput> inputList = new ArrayList<UserInput>();
        List<String> inputStringList = new ArrayList<String>();
        for (int i = KeyEvent.VK_0; i <= KeyEvent.VK_9; i++) {
            inputList.add(new UserInput(i, InputConstants.TYPE_KEY_PRESS));
            inputStringList.add(KeyEvent.getKeyText(i));
        }
        for (int i = KeyEvent.VK_A; i <= KeyEvent.VK_Z; i++) {
            inputList.add(new UserInput(i, InputConstants.TYPE_KEY_PRESS));
            inputStringList.add(KeyEvent.getKeyText(i));
        }
        for (int i = KeyEvent.VK_NUMPAD0; i <= KeyEvent.VK_ADD; i++) {
            inputList.add(new UserInput(i, InputConstants.TYPE_KEY_PRESS));
            inputStringList.add(KeyEvent.getKeyText(i));
        }
        for (int i = KeyEvent.VK_SUBTRACT; i <= KeyEvent.VK_DIVIDE; i++) {
            inputList.add(new UserInput(i, InputConstants.TYPE_KEY_PRESS));
            inputStringList.add(KeyEvent.getKeyText(i));
        }
        for (int i = KeyEvent.VK_F1; i <= KeyEvent.VK_F12; i++) {
            inputList.add(new UserInput(i, InputConstants.TYPE_KEY_PRESS));
            inputStringList.add(KeyEvent.getKeyText(i));
        }

        m_modifierString = new String[m_modifier.length];
        for (int i = 0; i < m_modifier.length; i++) {
            m_modifierString[i] = InputEvent.getModifiersExText(m_modifier[i]);
        }

        m_keys = inputList.toArray(new UserInput[inputList.size()]);
        m_keyStrings = 
            inputStringList.toArray(new String[inputStringList.size()]);
        
        inputList.add(new UserInput(
                InteractionMode.primary.rcIntValue(),
                InputConstants.TYPE_MOUSE_CLICK));
        inputList.add(new UserInput(
                InteractionMode.tertiary.rcIntValue(),
                InputConstants.TYPE_MOUSE_CLICK));
        inputList.add(new UserInput(
                InteractionMode.secondary.rcIntValue(),
                InputConstants.TYPE_MOUSE_CLICK));

        inputStringList.add(Messages.ObjectMappingPreferencePageMouseButton1);
        inputStringList.add(Messages.ObjectMappingPreferencePageMouseButton2);
        inputStringList.add(Messages.ObjectMappingPreferencePageMouseButton3);
        
        m_inputs = inputList.toArray(new UserInput[inputList.size()]);
        m_inputStrings = 
            inputStringList.toArray(new String[inputStringList.size()]);
    }
    
    
    /**
     * getInstance Method
     * @return KeyCommands
     */
    public static InputCodeHelper getInstance() {
        if (instance == null) {
            instance = new InputCodeHelper();
        }
        return instance;
    }
    
    /**
     * @return all available key-press inputs.
     */
    public UserInput[] getKeys() {
        return m_keys;
    }
    /**
     * @return all available inputs.
     */
    public UserInput[] getInputs() {
        return m_inputs;
    }

    /**
     * 
     * @return text corresponding to all available inputs. This text is 
     *         suitable for displaying to the user.
     */
    public String[] getInputStrings() {
        return m_inputStrings;
    }
    
    /**
     * 
     * @return text corresponding to all available key-press inputs. This 
     *         text is suitable for displaying to the user.
     */
    public String[] getKeyStrings() {
        return m_keyStrings;
    }
    
    /**
     * @return Returns the modifier.
     */
    public int[] getModifier() {
        return m_modifier;
    }
    /**
     * @return Returns the modifierString.
     */
    public String[] getModifierString() {
        return m_modifierString;
    }

    /**
     * gives you the index of a modifier code
     * @param modifier
     *      int
     * @return
     *      int
     */
    public int getIndexOfModifier(int modifier) {
        return ArrayUtils.indexOf(m_modifier, modifier);
    }
    
}
