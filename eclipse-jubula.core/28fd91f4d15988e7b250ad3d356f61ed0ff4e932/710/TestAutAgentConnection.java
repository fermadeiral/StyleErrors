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
package org.eclipse.jubula.qa.api;

import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.junit.Assert;
import org.junit.Test;

/** @author BREDEX GmbH */
public class TestAutAgentConnection {
    /** AUT-Agent host name to use */
    public static final String AGENT_HOST = "localhost"; //$NON-NLS-1$
    /** AUT-Agent port to use */
    public static final int AGENT_PORT_FIRST = 5051;
    /** AUT-Agent port to use */
    public static final int AGENT_PORT_SECOND = 5052;
    
    /** test aut agent connect and disconnect status
     * @throws Exception */
    @Test
    public void testConnectToAgent() throws Exception {
        AUTAgent agent = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT_FIRST);
        try {
            agent.connect();
            Assert.assertTrue(agent.isConnected());
            agent.disconnect();
            Assert.assertFalse(agent.isConnected());
            agent.connect();
            Assert.assertTrue(agent.isConnected());
        } finally {
            agent.disconnect();
            Assert.assertFalse(agent.isConnected());
        }
    }
   /** switches between two aut agents 
     * @throws Exception */
    @Test
    public void testConnectToMultipleAgents() throws Exception {
        AUTAgent agent1 = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT_FIRST);
        AUTAgent agent2 = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT_SECOND);
        try {
            agent1.connect();
            Assert.assertTrue(agent1.isConnected());
        } finally {
            agent1.disconnect();
            Assert.assertFalse(agent1.isConnected());
        }
        try {
            agent2.connect();
            Assert.assertTrue(agent2.isConnected());
        } finally {
            agent2.disconnect();
            Assert.assertFalse(agent2.isConnected());
        }
        try {
            agent1.connect();
            Assert.assertTrue(agent1.isConnected());
        } finally {
            agent1.disconnect();
            Assert.assertFalse(agent1.isConnected());
        }
    }
    
}