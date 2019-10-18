/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.qa.api.om;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.qa.api.factories.TestComponentFactories;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OM_factories {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(OM_factories.class);
    
    /** the input stream */
    private static InputStream stream;
    
    /** load input stream */
    static {
        URL resourceURL = TestComponentFactories.class.getClassLoader()
                .getResource("objectMapping_factories.properties"); //$NON-NLS-1$
                try {
            stream = resourceURL.openStream();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
    
    /** the object mapping */
    private static ObjectMapping objectMapping = MakeR.createObjectMapping(stream);
    
    /** The component identifier for "ComboBox_disabled_cbx"*/
    public static final ComponentIdentifier ComboBox_disabled_cbx = objectMapping.get("ComboBox_disabled_cbx"); //$NON-NLS-1$
    /** The component identifier for "Button_button_btn"*/
    public static final ComponentIdentifier Button_button_btn = objectMapping.get("Button_button_btn"); //$NON-NLS-1$
    /** The component identifier for "TextField_pfr_txf"*/
    public static final ComponentIdentifier TextField_pfr_txf = objectMapping.get("TextField_pfr_txf"); //$NON-NLS-1$
    /** The component identifier for "List_selectEntryValue_lst"*/
    public static final ComponentIdentifier List_selectEntryValue_lst = objectMapping.get("List_selectEntryValue_lst"); //$NON-NLS-1$
    /** The component identifier for "Tree_existing_tre"*/
    public static final ComponentIdentifier Tree_existing_tre = objectMapping.get("Tree_existing_tre"); //$NON-NLS-1$
    /** The component identifier for "Table_bigTable_tbl"*/
    public static final ComponentIdentifier Table_bigTable_tbl = objectMapping.get("Table_bigTable_tbl"); //$NON-NLS-1$
    /** The component identifier for "aa_TextArea_Back_btc"*/
    public static final ComponentIdentifier aa_TextArea_Back_btc = objectMapping.get("aa_TextArea_Back_btc"); //$NON-NLS-1$
    /** The component identifier for "TabbedPane_existing_tpn"*/
    public static final ComponentIdentifier TabbedPane_existing_tpn = objectMapping.get("TabbedPane_existing_tpn"); //$NON-NLS-1$
    
    private OM_factories() {
        // private
    }
}
