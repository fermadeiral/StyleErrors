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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.toolkit.base.components.GraphicsComponent;
import org.eclipse.jubula.toolkit.concrete.components.Application;
import org.eclipse.jubula.toolkit.concrete.components.MenuBarComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.toolkit.rcp.config.RCPAUTConfiguration;
import org.eclipse.jubula.toolkit.swt.SwtComponents;
import org.eclipse.jubula.toolkit.swt.components.Button;
import org.eclipse.jubula.toolkit.swt.components.Label;
import org.eclipse.jubula.toolkit.swt.components.Text;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author BREDEX GmbH */
public class TestSimpleAdderRCPAUT {
    /** AUT-Agent host name to use */
    public static final String AGENT_HOST = "localhost"; //$NON-NLS-1$
    /** AUT-Agent port to use */
    public static final int AGENT_PORT = 5051;
    /** the value1 */
    private static TextInputComponent value1;
    /** the value2 */
    private static TextInputComponent value2;
    /** the button */
    private static GraphicsComponent button;
    /** the result */
    private static TextComponent result;
    /** the AUT-Agent */
    private AUTAgent m_agent;
    /** the AUT */
    private AUT m_aut;

    /** global prepare */
    @BeforeClass
    public static void loadObjectMapping() throws Exception {
        URL input = TestSimpleAdderRCPAUT.class
            .getClassLoader().getResource(
                "objectMapping_SimpleAdderRCP.properties"); //$NON-NLS-1$
        
        ObjectMapping om = MakeR.createObjectMapping(
                input.openStream());
        
        ComponentIdentifier<Text> val1Id = om.get("value1"); //$NON-NLS-1$
        ComponentIdentifier<Text> val2Id = om.get("value2"); //$NON-NLS-1$
        ComponentIdentifier<Button> buttonId = om.get("equalsButton"); //$NON-NLS-1$
        ComponentIdentifier<Label> sumId = om.get("sum"); //$NON-NLS-1$
        
        if (val1Id != null 
            && val2Id != null 
            && buttonId != null
            && sumId != null) {
            value1 = SwtComponents.createText(val1Id);
            value2 = SwtComponents.createText(val2Id);
            button = SwtComponents.createButton(buttonId);
            result = SwtComponents.createTextComponent(sumId);            
        }
    }
    
    /** prepare */
    @Before
    public void setUp() throws Exception {
        m_agent = MakeR.createAUTAgent(AGENT_HOST, AGENT_PORT);
        m_agent.connect();
        
        final String autID = "SimpleAdder_rcp";  //$NON-NLS-1$
        AUTConfiguration config = new RCPAUTConfiguration(
            "api.aut.conf.simple.adder.rcp",  //$NON-NLS-1$
            autID,
            "SimpleAdder.exe", //$NON-NLS-1$
            "..\\examples\\AUTs\\SimpleAdder\\rcp\\win32\\win32\\x86\\", //$NON-NLS-1$ 
            null, 
            Locale.getDefault());
        
        AUTIdentifier id = m_agent.startAUT(config);
        if (id != null) {
            m_aut = m_agent.getAUT(id, SwtComponents
                    .getToolkitInformation());
            m_aut.connect();
        } else {
            Assert.fail("AUT start has failed!"); //$NON-NLS-1$
        }
    }

    /** cleanup */
    @After
    public void tearDown() throws Exception {
        m_aut.disconnect();
        m_agent.stopAUT(m_aut.getIdentifier());
        m_agent.disconnect();
    }
    
    /** the actual test method */
    @Test
    public void testMenubar() throws Exception {
        MenuBarComponent menu = SwtComponents.createMenu();
        m_aut.execute(menu.checkEnablementOfEntryByIndexpath(
                "1/1", true), null); //$NON-NLS-1$
        
    }
    
    /** the actual test method */
    @Test
    public void testApplication() throws Exception {
        Application app = SwtComponents.createApplication();
        m_aut.execute(app.delay(1000), null);
        m_aut.execute(app.waitForWindow(".*", Operator.matches, 1000, 100), null); //$NON-NLS-1$
    }
    
    /** the actual test method */
    @SuppressWarnings("null")
    @Test(expected = CheckFailedException.class)
    public void testTestFirstSimpleAdderSteps() throws Exception {
        
        final int firstValue = 17;
        List<Result<String>> results = new ArrayList<Result<String>>();
        try {
            for (int i = 1; i < 5; i++) {
                results.add(m_aut.execute(
                        value1.replaceText(
                            String.valueOf(firstValue)), 
                            "Entering first value")); //$NON-NLS-1$
                results.add(m_aut.execute(
                        value2.replaceText(
                            String.valueOf(i)), 
                            "Entering second value")); //$NON-NLS-1$
                results.add(m_aut.execute(
                        button.click(
                            1, InteractionMode.primary), 
                            "Invoking addition")); //$NON-NLS-1$
                results.add(m_aut.execute(
                        result.checkText(
                            String.valueOf(firstValue + i),
                            Operator.equals), 
                            "Checking computed result")); //$NON-NLS-1$
            }
        } finally {
            Assert.assertTrue(results.size() == 15);
        }
    }
}