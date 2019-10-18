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
package org.eclipse.jubula.tools.internal.utils.generator;

import org.eclipse.jubula.tools.internal.xml.businessmodell.AbstractComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ParamValueSet;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Property;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;
import org.eclipse.jubula.tools.internal.xml.businessprocess.ConfigVersion;
import org.eclipse.jubula.tools.internal.xml.businessprocess.XStreamXmlAttributeConverter;

import com.thoughtworks.xstream.XStream;

/**
 * Class to create the XStream
 *
 * @author BREDEX GmbH
 * @created 08.10.2007
 */
public class XStreamGenerator {

    
    /**
     * Hidden utility Constructor.
     */
    private XStreamGenerator() {
        // Nothing
    }
    
    /**
     * Creates a <code>XStream</code> object and configures it.
     * @return A new <code>XStream</code>
     */
    public static XStream createXStream() {
        XStream stream = new XStream();
        registerConverter(stream);
        
        stream.alias("compSystem", CompSystem.class); //$NON-NLS-1$
        stream.aliasField("configVersion", CompSystem.class, "m_configVersion"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.addImplicitCollection(CompSystem.class, "m_concreteComponents", "toolkitComponent", ConcreteComponent.class); //$NON-NLS-1$ //$NON-NLS-2$
        stream.addImplicitCollection(CompSystem.class, "m_concreteComponents", "concreteComponent", ConcreteComponent.class); //$NON-NLS-1$ //$NON-NLS-2$         
        stream.addImplicitCollection(CompSystem.class, "m_abstractComponents", "abstractComponent", AbstractComponent.class); //$NON-NLS-1$ //$NON-NLS-2$
     
        stream.alias("configVersion", ConfigVersion.class); //$NON-NLS-1$
        stream.aliasField("majorVersion", ConfigVersion.class, "m_majorV"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("minorVersion", ConfigVersion.class, "m_minorV"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("majorCustomVersion", ConfigVersion.class, "m_majorCustomV"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("minorCustomVersion", ConfigVersion.class, "m_minorCustomV"); //$NON-NLS-1$ //$NON-NLS-2$
                
        stream.addImplicitCollection(Component.class, "m_realizedTypes", "realizes", String.class); //$NON-NLS-1$ //$NON-NLS-2$
        stream.addImplicitCollection(Component.class, "m_extendedTypes", "extends", String.class); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("type", Component.class, "m_type"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("descriptionKey", Component.class, "m_descriptionKey"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.addImplicitCollection(Component.class, "m_actions", "action", Action.class); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("apiMostConcrete", Component.class, "m_apiMostConcrete"); //$NON-NLS-1$ //$NON-NLS-2$        
        stream.aliasField("since", Component.class, "m_since"); //$NON-NLS-1$ //$NON-NLS-2$        
        
        stream.alias("abstractComponent", AbstractComponent.class); //$NON-NLS-1$
        stream.aliasField("type", AbstractComponent.class, "m_type"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("visible", AbstractComponent.class, "m_visible"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("apiMostConcrete", AbstractComponent.class, "m_apiMostConcrete"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("observable", AbstractComponent.class, "m_observable"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("deprecated", AbstractComponent.class, "m_deprecated"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("since", AbstractComponent.class, "m_since"); //$NON-NLS-1$ //$NON-NLS-2$
        
        stream.alias("toolkitComponent", ConcreteComponent.class); //$NON-NLS-1$
        stream.alias("concreteComponent", ConcreteComponent.class); //$NON-NLS-1$
        stream.aliasField("type", ConcreteComponent.class, "m_type"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("testerClass", ConcreteComponent.class, "m_testerClass"); //$NON-NLS-1$ //$NON-NLS-2$           
        stream.aliasField("visible", ConcreteComponent.class, "m_visible"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("apiMostConcrete", ConcreteComponent.class, "m_apiMostConcrete"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("hasDefaultMapping", ConcreteComponent.class, "m_hasDefaultMapping"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("observable", ConcreteComponent.class, "m_observable"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("deprecated", ConcreteComponent.class, "m_deprecated"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("since", ConcreteComponent.class, "m_since"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("supported", ConcreteComponent.class, "m_isSupported"); //$NON-NLS-1$ //$NON-NLS-2$
        
        stream.addImplicitCollection(ConcreteComponent.class, "m_compClass", "componentClass", ComponentClass.class); //$NON-NLS-1$ //$NON-NLS-2$
        stream.alias("componentClass", ComponentClass.class); //$NON-NLS-1$
        stream.aliasField("name", ComponentClass.class, "m_name"); //$NON-NLS-1$ //$NON-NLS-2$
        
        stream.addImplicitCollection(ComponentClass.class, "m_properties", "property", Property.class); //$NON-NLS-1$ //$NON-NLS-2$
        stream.alias("property", Property.class); //$NON-NLS-1$
        stream.aliasField("name", Property.class, "m_name"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("value", Property.class, "m_value"); //$NON-NLS-1$ //$NON-NLS-2$
             
        aliasActionClass(stream);
        stream.addImplicitCollection(Action.class, "m_params", "param", Param.class); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("name", Param.class, "m_name"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("descriptionKey", Param.class, "m_descriptionKey"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("type", Param.class, "m_type"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("defaultValue", Param.class, "m_defaultValue"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("valueSet", Param.class, "m_valueSet"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("optional", Param.class, "m_optional"); //$NON-NLS-1$ //$NON-NLS-2$
        
        stream.alias("valueSet", ParamValueSet.class); //$NON-NLS-1$
        stream.aliasField("valueSet", ParamValueSet.class, "m_valueSet"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("combinable", ParamValueSet.class, "m_isCombinable"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.addImplicitCollection(ParamValueSet.class, "m_valueSet", "element", ValueSetElement.class); //$NON-NLS-1$ //$NON-NLS-2$
        
        stream.alias("element", ValueSetElement.class); //$NON-NLS-1$
        stream.aliasField("value", ValueSetElement.class, "m_value"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("name", ValueSetElement.class, "m_name");  //$NON-NLS-1$//$NON-NLS-2$
        return stream;
    }

    /**
     * @param stream the XStream.
     */
    private static void registerConverter(XStream stream) {
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
            Component.class, 
            new String[] {"m_type", "m_descriptionKey", "m_visible", "m_observable", "m_deprecated", "m_since", "m_apiMostConcrete"})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
            AbstractComponent.class, 
            new String[] {"m_type", "m_descriptionKey", "m_visible", "m_observable", "m_deprecated", "m_since", "m_apiMostConcrete"}));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
            ConcreteComponent.class, 
            new String[] {"m_type", "m_descriptionKey", "m_visible", "m_observable", "m_deprecated", "m_hasDefaultMapping", "m_since", "m_isSupported", "m_apiMostConcrete"}));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
            Action.class, 
            new String[] {"m_name", "m_descriptionKey", "m_clientAction", "m_apiAction", "m_deprecated", "m_since"})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
            Param.class, 
            new String[] {"m_name", "m_descriptionKey", "m_optional"})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
                ParamValueSet.class, "m_isCombinable")); //$NON-NLS-1$
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
            ValueSetElement.class, new String[] { "m_name", "m_value" }));  //$NON-NLS-1$//$NON-NLS-2$        
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
            ConfigVersion.class, new String[] { "m_majorV",  //$NON-NLS-1$
                "m_minorV",  "m_majorCustomV", "m_minorCustomV" }));   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
                ComponentClass.class, "m_name")); //$NON-NLS-1$
        stream.registerConverter(XStreamXmlAttributeConverter.create(stream,
                Property.class, new String[] { "m_name", "m_value" })); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * @param stream the XStream
     */
    private static void aliasActionClass(XStream stream) {
        stream.aliasField("name", Action.class, "m_name"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("descriptionKey", Action.class, "m_descriptionKey"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("method", Action.class, "m_method"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("deprecated", Action.class, "m_deprecated"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("postExecutionCommand", Action.class, "m_postExecutionCommand"); //$NON-NLS-1$ //$NON-NLS-2$           
        stream.aliasField("clientAction", Action.class, "m_clientAction"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("apiAction", Action.class, "m_apiAction"); //$NON-NLS-1$ //$NON-NLS-2$
        stream.aliasField("since", Action.class, "m_since"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
}
