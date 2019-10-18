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
package org.eclipse.jubula.client.ui.rcp.preferences.utils;

import java.util.Arrays;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.tools.internal.constants.InputCodeHelper;
import org.eclipse.jubula.tools.internal.constants.InputCodeHelper.UserInput;
import org.eclipse.jubula.tools.internal.constants.InputConstants;
import org.eclipse.swt.widgets.Composite;


/**
 * Provides utility methods for creating and using "input" combo boxes. 
 * These combo boxes appear in preference pages and allow the user to choose
 * from different forms of input (ex. key press, mouse click, etc.).
 *
 * @author BREDEX GmbH
 * @created Sep 17, 2009
 */
public final class InputComboUtil {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private InputComboUtil() {
        // Nothing to initialize
    }

    /**
     * 
     * @param parent The parent for the combo box.
     * @param style The style for the combo box.
     * @return a new combo box containing only key inputs.
     */
    public static DirectCombo<UserInput> createKeyCombo(
            Composite parent, int style) {
        return new DirectCombo<UserInput>(parent, style, 
                Arrays.asList(InputCodeHelper.getInstance().getKeys()),
                Arrays.asList(InputCodeHelper.getInstance().getKeyStrings()),
                false, false);
    }
    
    /**
     * 
     * @param parent The parent for the combo box.
     * @param style The style for the combo box.
     * @return a new combo box containing key and mouse inputs.
     */
    public static DirectCombo<UserInput> createInputCombo(
            Composite parent, int style) {
        return new DirectCombo<UserInput>(parent, style, 
                Arrays.asList(InputCodeHelper.getInstance().getInputs()),
                Arrays.asList(InputCodeHelper.getInstance().getInputStrings()),
                false, false);
    }
    
    /**
     * Sets the selected value of the given combo box based on the provided
     * arguments.
     * 
     * @param combo The combo box for which to set the value.
     * @param inputCode The input code that should be represented by the 
     *                  newly selected element. The actual meaning behind the
     *                  code is dependant on the <code>inputType</code>.
     * @param inputType The input code that should be represented by the 
     *                  newly selected element. Use ordinals from the 
     *                  {@link InputCodeHelper.INPUT_TYPE}  enumeration.
     *                  
     */
    public static void setSelectedInput(DirectCombo<UserInput> combo, 
            int inputCode, int inputType) {

        combo.setSelectedObject(new UserInput(
                inputCode, inputType));
    }

    /**
     * Sets the selected value of the given combo box based on the provided
     * arguments.
     * 
     * @param combo The combo box for which to set the value.
     * @param inputCode The input code that should be represented by the 
     *                  newly selected element. This input code is treated
     *                  as that of a key press.
     *                  
     */
    public static void setSelectedKey(
            DirectCombo<UserInput> combo, int inputCode) {

        setSelectedInput(combo, inputCode, InputConstants.TYPE_KEY_PRESS);
    }

    /**
     * Sets the preference value for the given key based on the code of 
     * the current selection of the given combo box.
     * 
     * @param combo The combo box from which to obtain the value.
     * @param prefStore The preference store to use for persisting the 
     *                  preference.
     * @param codePrefKey The key for which to set the value in the preference
     *                    store.
     *                  
     */
    public static void setPrefCode(DirectCombo<UserInput> combo, 
            IPreferenceStore prefStore, String codePrefKey) {
        prefStore.setValue(codePrefKey, combo.getSelectedObject().getCode());
    }
    
    /**
     * Sets the preference value for the given key based on the type of 
     * the current selection of the given combo box.
     * 
     * @param combo The combo box from which to obtain the value.
     * @param prefStore The preference store to use for persisting the 
     *                  preference.
     * @param typePrefKey The key for which to set the value in the preference
     *                    store.
     *                  
     */
    public static void setPrefType(DirectCombo<UserInput> combo, 
            IPreferenceStore prefStore, String typePrefKey) {
        prefStore.setValue(typePrefKey, combo.getSelectedObject().getType());
    }

}
