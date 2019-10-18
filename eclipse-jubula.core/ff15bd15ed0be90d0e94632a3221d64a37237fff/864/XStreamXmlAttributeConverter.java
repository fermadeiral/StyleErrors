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
package org.eclipse.jubula.tools.internal.xml.businessprocess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * XStream doesn't support XML tag attributes so far. As the client
 * configuration file contains attributes, this converter adds attribute support
 * to the deserialization mechanism of XStream.
 * 
 * @author BREDEX GmbH
 * @created 25.07.2005
 *
 */
public class XStreamXmlAttributeConverter extends ReflectionConverter {
    /**
     * The reflection provider used to write the attribute into
     * the object field.
     */
    private ReflectionProvider m_reflectionProvider;
    /**
     * The XStream facade object.
     */
    private XStream m_stream;
    /**
     * The mapper is used to find the alias definitions of the object fields.
     */
    private Mapper m_mapper;
    /**
     * The type of the object which is deserialized.
     */
    private Class m_type;
    /**
     * A list of field names which are deserialized from XML attributes.
     */
    private List m_fieldNames;
    /**
     * The constructor.
     * 
     * @param stream
     *            The XStream
     * @param mapper
     *            The alias mapper
     * @param provider
     *            The reflection provider to write the object field
     * @param type
     *            The type of the object that is deserialized
     * @param fieldNames
     *            The array of field names that will be deserialized
     */
    private XStreamXmlAttributeConverter(XStream stream, Mapper mapper,
        ReflectionProvider provider, Class type, String[] fieldNames) {
        super(mapper, provider);
        m_stream = stream;
        m_reflectionProvider = provider;
        m_mapper = mapper;
        m_type = type;
        m_fieldNames = Arrays.asList(fieldNames);
    }
    /**
     * Creates a new attribute converter.
     * 
     * @param stream
     *            The XStream
     * @param type
     *            The type of the object that is deserialized
     * @param fieldName
     *            The field name that will be deserialized
     * @return A new converter instance
     */
    public static XStreamXmlAttributeConverter create(XStream stream,
        Class type, String fieldName) {
        return create(stream, type, new String[] {fieldName});
    }
    /**
     * Creates a new attribute converter.
     * 
     * @param stream
     *            The XStream
     * @param type
     *            The type of the object that is deserialized
     * @param fieldNames
     *            The array of field names that will be deserialized
     * @return A new converter instance
     */
    public static XStreamXmlAttributeConverter create(XStream stream,
        Class type, String[] fieldNames) {
        ReflectionProvider provider = new PureJavaReflectionProvider();
        XStreamXmlAttributeConverter converter =
            new XStreamXmlAttributeConverter(
                stream, stream.getMapper(), provider, type,
                fieldNames);
        return converter;
    }
    /**
     * Calls the protected declared method <code>fromString</code> of the
     * passed converter instance with the value as parameter.
     * 
     * @param converter
     *            The converter
     * @param value
     *            The value to convert
     * @return The converted value
     */
    private Object fromString(Converter converter, String value) {
        try {
            Method method = converter.getClass().getDeclaredMethod("fromString", //$NON-NLS-1$
                new Class[] { String.class });
            method.setAccessible(true);
            return method.invoke(converter, new Object[] { value });
        } catch (SecurityException e) {
            throw new StreamException(e);
        } catch (IllegalArgumentException e) {
            throw new StreamException(e);
        } catch (NoSuchMethodException e) {
            throw new StreamException(e);
        } catch (IllegalAccessException e) {
            throw new StreamException(e);
        } catch (InvocationTargetException e) {
            throw new StreamException(e);
        }
    }
    /**
     * {@inheritDoc}
     * @param type The type to check
     * @return <code>true</code> if the type passed to the constructor is
     *         assignable from the parameter <code>type</code>.
     */
    public boolean canConvert(Class type) {
        return m_type.isAssignableFrom(type);
    }
    /**
     * Writes the <code>source</code> into the XML writer with the fields as
     * XML attributes.
     * 
     * {@inheritDoc}
     *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     *      com.thoughtworks.xstream.converters.MarshallingContext)
     * @param source
     * @param writer
     * @param context
     */
    public void marshal(Object source, final HierarchicalStreamWriter writer,
        MarshallingContext context) {
        m_reflectionProvider.visitSerializableFields(source,
            new ReflectionProvider.Visitor() {
                public void visit(String field, Class fieldType, Class type,
                    Object value) {
                    if (m_fieldNames.contains(field)) {
                        writer.addAttribute(m_mapper.serializedMember(m_type,
                            field), value.toString());
                    }
                }
            });
        super.marshal(source, writer, context);
    }
    /**
     * Reads object from the XML source and writes the XML attributes into the
     * object fields.
     * 
     * {@inheritDoc}
     *      com.thoughtworks.xstream.converters.UnmarshallingContext)
     * @param reader
     * @param context
     * @return
     */
    public Object unmarshal(HierarchicalStreamReader reader,
        UnmarshallingContext context) {
        String[] attributes = new String[m_fieldNames.size()];
        for (int i = 0; i < m_fieldNames.size(); i++) {
            String fieldName = (String)m_fieldNames.get(i);
            attributes[i] = reader.getAttribute(m_mapper.serializedMember(
                m_type, fieldName));
        }

        Object target = super.unmarshal(reader, context);
        ConverterLookup lookup = m_stream.getConverterLookup();
        
        for (int i = 0; i < attributes.length; i++) {
            String attribute = attributes[i];
            if (attribute != null) {
                String fieldName = (String)m_fieldNames.get(i);
                Class fieldType = m_reflectionProvider.getFieldType(target,
                    fieldName, null);
                Converter converter = lookup.lookupConverterForType(fieldType);
                if (converter.canConvert(fieldType)) {
                    m_reflectionProvider.writeField(target, fieldName,
                        fromString(converter, attribute), null);
                }
            }
        }
        return target;
    }
}