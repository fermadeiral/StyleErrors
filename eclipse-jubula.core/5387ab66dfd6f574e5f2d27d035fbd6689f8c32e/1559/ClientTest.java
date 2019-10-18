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
package org.eclipse.jubula.client.core;

/**
 * @author BREDEX GmbH
 * @created 21.07.2005
 */
public class ClientTest {

    /** ClientTestImpl instance */
    private static IClientTest clientTest;

    /** private constructor */
    private ClientTest() {
        //
    }

    /**
     * 
     * @return ClientTestImpl instance
     */
    public static IClientTest instance() {
        if (clientTest == null) {
            clientTest = new ClientTestImpl();
        }
        return clientTest;
    }
}
