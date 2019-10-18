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
package org.eclipse.jubula.tools.internal.serialisation;

import org.eclipse.jubula.tools.internal.exception.JBFatalAbortException;
import org.eclipse.jubula.tools.internal.exception.SerialisationException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * This interface represents a XML (de)serializer. Instances of implementing
 * classes are created by calling the <code>create()</code> method of the
 * inner class <code>Factory</code>.
 * 
 * @author BREDEX GmbH
 * @created 28.07.2005
 */
public interface IXmlSerializer {
    /**
     * The factory to create a (de)serializer instance.
     */
    public static class Factory {
        /**
         * The default constructor.
         */
        private Factory() {
            super();
        }

        /**
         * @return A new instance of a class which implements
         *         <code>IXmlSerializer</code>.
         */
        public static IXmlSerializer create() {
            String errMsg = "No Xml support defined."; //$NON-NLS-1$
            try {
                return (IXmlSerializer)Class
                    .forName(
                        "org.eclipse.jubula.tools.internal.serialisation.XStreamXmlSerializer") //$NON-NLS-1$
                    .newInstance();

            } catch (java.lang.ClassNotFoundException ex) {
                throw new JBFatalAbortException(errMsg, ex,
                    MessageIDs.E_CLASS_NOT_FOUND);
            } catch (java.lang.InstantiationException ex) {
                throw new JBFatalAbortException(errMsg, ex,
                    MessageIDs.E_CLASS_NOT_FOUND);
            } catch (java.lang.IllegalAccessException ex) {
                throw new JBFatalAbortException(errMsg, ex,
                    MessageIDs.E_CLASS_NOT_FOUND);
            }
        }
    }        

    /**
     * Deserializes the passed text and creates an object of the type of the
     * passed class.
     * 
     * @param text
     *            The XML string to deserialize
     * @param clazz
     *            The type of the object to create
     * @return The deserialized object
     * @throws SerialisationException
     *             If the deserialization fails
     */
    public Object deserialize(String text, Class clazz)
        throws SerialisationException;

    /**
     * Serializes the passed object into a XML content and returns it as a
     * string.
     * 
     * @param object
     *            The object to serialize
     * @param writeXmlHeader
     *            If <code>true</code>, the returned string contains a XML
     *            header, e.g.
     *            <code><?xml version="1.0" encoding="ISO-8859-1"?></code>.
     * @return The XML string
     * @throws SerialisationException
     *             If the serialization fails
     */
    public String serialize(Object object, boolean writeXmlHeader)
        throws SerialisationException;
    
    /**
     * get the implementation
     * 
     * @return object
     */
    public Object getImplementation();
}