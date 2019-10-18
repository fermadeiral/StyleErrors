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

package org.eclipse.jubula.client.core.preferences.database;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jubula.client.core.Activator;

/**
 * Initializes Database Connection preferences.
 * 
 * @author BREDEX GmbH
 * @created 02.02.2011
 */
public class DatabaseConnectionInitializer extends
        AbstractPreferenceInitializer {

    /** 
     * name for the default database connection (if no connections have yet 
     * been defined by user) 
     */
    private static final String DEFAULT_CONNECTION_NAME = "Default Embedded (H2)"; //$NON-NLS-1$
    

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences preferenceNode = 
            DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        preferenceNode.put(
            DatabaseConnectionConverter.PREF_DATABASE_CONNECTIONS, 
            DatabaseConnectionConverter.convert(
                new DatabaseConnection[] {new DatabaseConnection(
                        DEFAULT_CONNECTION_NAME, new H2ConnectionInfo())}));
    }

}
