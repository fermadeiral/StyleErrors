/*******************************************************************************
 * Copyright (c) 2019 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/

package org.eclipse.jubula.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author BREDEX GmbH
 *
 */
public enum RCPKeyboardRegistry {
    /** */
    INSTANCE;

    /** */
    private Map<String, Properties> m_nameToKeyBinding =
            new HashMap<String, Properties>();

    /**
     * 
     * @return the {@link Map} from local to the keybinding properties
     */
    public Map<String, Properties> getNameToKeyBinding() {
        return m_nameToKeyBinding;
    }

    /**
     * adds the keyboard mapping to the map with the localNameCode as the key
     * @param localNameCode the localCode
     * @param keyBinding the keybinding Properties
     */
    public void addKeyboardMapping(String localNameCode,
            Properties keyBinding) {
        m_nameToKeyBinding.put(localNameCode, keyBinding);
    }

    /**
     * 
     * @param localNameCode the local code like EN_US
     * @return the keybinding properties for the local
     */
    public Properties getPropertiesForLocalCode(String localNameCode) {
        return m_nameToKeyBinding.get(localNameCode);
    }
}
