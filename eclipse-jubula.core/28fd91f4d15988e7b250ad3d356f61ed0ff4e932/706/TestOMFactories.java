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
package org.eclipse.jubula.qa.api.factories;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.qa.api.om.OM;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponents;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.Test;
/**
 * Class for testing the OM factory
 */
public class TestOMFactories {

    /** the resource URL */
    private URL m_resourceURL = TestComponentFactories.class.getClassLoader()
            .getResource("objectMapping_SimpleAdder.properties"); //$NON-NLS-1$

    /**
     * test method
     * 
     * @throws IOException
     */
    @Test
    public void testFactoriesViaPropertiesFile() throws IOException {
        ObjectMapping om = MakeR.createObjectMapping(
                m_resourceURL.openStream());
        
        Assert.assertNotNull(om);
        
        /** The first text field */
        ComponentIdentifier identifierTextField1 =
                om.get("bound_SimpleAdder_inputField1_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierTextField1);
        
        /** The second text field */
        ComponentIdentifier identifierTextField2 =
                om.get("bound_SimpleAdder_inputField2_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierTextField2);
        
        /** The equals button */
        ComponentIdentifier identifierEqualsButton =
                om.get("bound_SimpleAdder_equals_btn"); //$NON-NLS-1$
        Assert.assertNotNull(identifierEqualsButton);
        
        /** The result text field */
        ComponentIdentifier identifierResultField =
                om.get("bound_SimpleAdder_resultField_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierResultField);
        
        TextInputComponent textField1 = ConcreteComponents
                .createTextInputComponent(identifierTextField1);
        Assert.assertNotNull(textField1);
        
        TextInputComponent textField2 = ConcreteComponents
                .createTextInputComponent(identifierTextField2);
        Assert.assertNotNull(textField2);
        
        ButtonComponent equalsButton = ConcreteComponents
                .createButtonComponent(identifierEqualsButton);
        Assert.assertNotNull(equalsButton);
        
        TextComponent resultField = ConcreteComponents
                .createTextComponent(identifierResultField);
        Assert.assertNotNull(resultField);
    
        CAP cap1 = textField1.replaceText("17"); //$NON-NLS-1$
        Assert.assertNotNull(cap1);
        CAP cap2 = textField2.replaceText("4"); //$NON-NLS-1$
        Assert.assertNotNull(cap2);
        CAP cap3 = equalsButton.click(1, InteractionMode.primary);
        Assert.assertNotNull(cap3);
        CAP cap4 = resultField.checkText("21", Operator.equals); //$NON-NLS-1$
        Assert.assertNotNull(cap4);
    }

    /**
     * test method
     */
    @Test
    public void testFactoriesViaPropertiesClass() {
        
        /** The first text field */
        ComponentIdentifier identifierTextField1 =
                OM.bound_SimpleAdder_inputField1_txf;
        Assert.assertNotNull(identifierTextField1);
        
        /** The second text field */
        ComponentIdentifier identifierTextField2 =
                OM.bound_SimpleAdder_inputField2_txf;
        Assert.assertNotNull(identifierTextField2);
        
        /** The equals button */
        ComponentIdentifier identifierEqualsButton =
                OM.bound_SimpleAdder_equals_btn;
        Assert.assertNotNull(identifierEqualsButton);
        
        /** The result text field */
        ComponentIdentifier identifierResultField =
                OM.bound_SimpleAdder_resultField_txf;
        Assert.assertNotNull(identifierResultField);
        
        TextInputComponent textField1 = ConcreteComponents
                .createTextInputComponent(identifierTextField1);
        Assert.assertNotNull(textField1);
        
        TextInputComponent textField2 = ConcreteComponents
                .createTextInputComponent(identifierTextField2);
        Assert.assertNotNull(textField2);
        
        ButtonComponent equalsButton = ConcreteComponents
                .createButtonComponent(identifierEqualsButton);
        Assert.assertNotNull(equalsButton);
        
        TextComponent resultField = ConcreteComponents
                .createTextComponent(identifierResultField);
        Assert.assertNotNull(resultField);
    
        CAP cap1 = textField1.replaceText("17"); //$NON-NLS-1$
        Assert.assertNotNull(cap1);
        CAP cap2 = textField2.replaceText("4"); //$NON-NLS-1$
        Assert.assertNotNull(cap2);
        CAP cap3 = equalsButton.click(1, InteractionMode.primary);
        Assert.assertNotNull(cap3);
        CAP cap4 = resultField.checkText("21", Operator.equals); //$NON-NLS-1$
        Assert.assertNotNull(cap4);
    }

    /**
     * @throws IOException
     * @throws LoadResourceException
     */
    @Test
    public void testComponentIdentifierViaInlinedStringIdentifier()
            throws IOException, IllegalArgumentException {
        ComponentIdentifier<TextComponent> value1 = MakeR.createCI("rO0ABXNyAD1vcmcuZWNsaXBzZS5qdWJ1bGEudG9vbHMuaW50ZXJuYWwub2JqZWN0cy5Db21wb25lbnRJZGVudGlmaWVyAAAAAAAABAcCAAlaABRtX2VxdWFsT3JpZ2luYWxGb3VuZEQAEW1fbWF0Y2hQZXJjZW50YWdlSQAhbV9udW1iZXJPZk90aGVyTWF0Y2hpbmdDb21wb25lbnRzTAAYbV9hbHRlcm5hdGl2ZURpc3BsYXlOYW1ldAASTGphdmEvbGFuZy9TdHJpbmc7TAAUbV9jb21wb25lbnRDbGFzc05hbWVxAH4AAUwAFW1fY29tcG9uZW50UHJvcGVydGllc3QAD0xqYXZhL3V0aWwvTWFwO0wAEG1faGllcmFyY2h5TmFtZXN0ABBMamF2YS91dGlsL0xpc3Q7TAAMbV9uZWlnaGJvdXJzcQB+AANMABRtX3N1cHBvcnRlZENsYXNzTmFtZXEAfgABeHAAv/AAAAAAAAD/////cHQAFmphdmF4LnN3aW5nLkpUZXh0RmllbGRwc3IAE2phdmEudXRpbC5BcnJheUxpc3R4gdIdmcdhnQMAAUkABHNpemV4cAAAAAZ3BAAAAAZ0AAZmcmFtZTB0ABdqYXZheC5zd2luZy5KUm9vdFBhbmVfMXQAEG51bGwubGF5ZXJlZFBhbmV0ABBudWxsLmNvbnRlbnRQYW5ldABBb3JnLmVjbGlwc2UuanVidWxhLmV4YW1wbGVzLmF1dC5hZGRlci5zd2luZy5ndWkuQ2FsY3VsYXRvclBhbmVsXzF0AAZ2YWx1ZTF4c3EAfgAGAAAACHcEAAAACHQAFGphdmF4LnN3aW5nLkpMYWJlbF8xdAAUamF2YXguc3dpbmcuSkxhYmVsXzJ0ABhqYXZheC5zd2luZy5KVGV4dEZpZWxkXzF0ABRqYXZheC5zd2luZy5KTGFiZWxfM3QAGGphdmF4LnN3aW5nLkpTZXBhcmF0b3JfMXQAGGphdmF4LnN3aW5nLkpUZXh0RmllbGRfMnQAFGphdmF4LnN3aW5nLkpMYWJlbF80dAAVamF2YXguc3dpbmcuSkJ1dHRvbl8xeHQAH2phdmF4LnN3aW5nLnRleHQuSlRleHRDb21wb25lbnQ=");  //$NON-NLS-1$
        
        Assert.assertEquals(MakeR.createObjectMapping(
                m_resourceURL.openStream()).get(
                    "bound_SimpleAdder_inputField1_txf"), //$NON-NLS-1$
                    value1);
    }
}
