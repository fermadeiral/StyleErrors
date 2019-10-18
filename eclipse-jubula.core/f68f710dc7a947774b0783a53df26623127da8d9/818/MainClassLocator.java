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
package org.eclipse.jubula.tools.internal.jarutils;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * Tool-class for searching jar file's main class
 * Simply call getMainClass(File file) to get a List
 * of Main Classes in jar file.
 * 
 * @author BREDEX GmbH
 *  
 */
public class MainClassLocator {
    
    /** to prevent instantiation */
    private MainClassLocator() {
        // do nothing
    }
    /**
     * External called static method to get MainClass
     * 
     * @param file
     *            a jarfile
     * @return List
     * @throws IOException
     *             error
     */
    public static List<String> getMainClass(File file) throws IOException {
        MainClassLocator t = new MainClassLocator();
        return t.getMainClassName(file);
    }

    /**
     * tries to get mainclass from manifest file
     * 
     * @param url a jar file
     * @return List
     * @throws IOException error
     */
    private List<String> getMainClassName(File url) throws IOException {
        URL u = new URL("jar", //$NON-NLS-1$
                StringConstants.EMPTY, url.toURI().toURL()
                + "!/"); //$NON-NLS-1$
        JarURLConnection uc = (JarURLConnection) u.openConnection();
        Attributes attr = uc.getMainAttributes();
        List<String> returnValue = new Vector<String>();
        try {
            if ((attr != null)
                    && (attr.getValue(Attributes.Name.MAIN_CLASS) != null)) {
                returnValue.add(attr.getValue(Attributes.Name.MAIN_CLASS));
                return returnValue;
            }
            if (returnValue.isEmpty()) {
                return getMainClassNameParse(url.toURI().toURL());
            }
            return returnValue;
        } finally {
            uc.getJarFile().close();
        }
    }

    /**
     * tries to locate main class in a jar file
     * 
     * @param url a jar file
     * @return List 
     * @throws IOException error
     */
    private List<String> getMainClassNameParse(URL url) throws IOException {
        MyClassLoader cl = new MyClassLoader(url);
        List<String> main = cl.getMainMethod();
        cl.close();
        return main;
    }
    
    /**
     * inserted class
     * @author BREDEX GmbH
     *
     */
    private static class MyClassLoader extends ClassLoader {
        
        /**
         * JarResources
         */
        private JarResources m_res;

        /**
         * a jar file
         */
        private JarFile m_jar;

        /**
         * constructor of class
         * @param fileName filename as string
         * @throws IOException error
         */
        MyClassLoader(URL fileName) throws IOException {
            m_res = new JarResources(fileName.getFile());
            m_jar = new JarFile(new File(fileName.getFile()));
        }

        /**
         * finalizes the classloader
         *
         */
        public void close() {
            try {
                m_jar.close();
                m_res = null;
                m_jar = null;
            } catch (Throwable e) { // NOPMD by al on 3/21/07 11:02 AM
                // just cleanup
                // since it's not clear where this class may be used (i.e.
                // in a server context, no logging is done to avoid
                // class loader problems
            }
        }
        /**
         * returns the Main Method of a class
         * @return List
         */
        public List<String> getMainMethod() {
            List<String> returnValue = new Vector<String>();
            Enumeration entries = m_jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                if ((!entry.isDirectory())
                        && (entry.getName().endsWith(".class"))) { //$NON-NLS-1$
                    if (checkClass(entry.getName()) != null) { // NOPMD by al on 3/19/07 2:06 PM
                        returnValue.add(entry.
                                getName().replace('/', 
                                '.') 
                                .substring(
                                        0,
                                        entry.getName().
                                        replace('/',  
                                            '.') 
                                            .lastIndexOf(
                                                ".class"))); //$NON-NLS-1$
                    }
                }
            }

            return returnValue;
        }

        /**
         * checks if a class is a main-class
         * @param cNRaw classname
         * @return String
         * @throws ClassFormatError error
         */
        private String checkClass(String cNRaw) throws ClassFormatError {
            String cN = cNRaw;
            cN = cN.replace('/', '.');
            byte[] classBytes = m_res.getResource(cNRaw);
            try {
                Class c;
                try {
                    c = findClass(cN);
                } catch (ClassNotFoundException e) {
                    c = defineClass(
                        cN.substring(0, 
                                cN.lastIndexOf(".class")), //$NON-NLS-1$ 
                                classBytes, 
                        0, classBytes.length);
                }
                for (int i = 0; i < (c.getDeclaredMethods().length); i++) {
                    if (c.getDeclaredMethods()[i].
                            getName().equals("main")) { //$NON-NLS-1$
                        return cN;
                    }
                }
            } catch (NoClassDefFoundError e) { // NOPMD by al on 3/19/07 2:07 PM
                //nothing
            }
            return null;
        }
    }

}
