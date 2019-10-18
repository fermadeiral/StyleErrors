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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * JarResources: JarResources maps all resources included in a ZIP or JAR file.
 * Additionally, it provides a method to extract one as a blob.
 * 
 * !!! this class is needed by MainCassLocator !!!
 */
public final class JarResources {
    // don't use logging because this may not work due to class loading
    // problems and therefore may crash the application.
    // private static final Logger LOG = LoggerFactory.getLogger(JarResources.class);
    /**
     * jar resource mapping tables
     */ 
    private Map<String, Integer> m_htSizes = new Hashtable<String, Integer>();

    /**
     * jar resource mapping tables
     */ 
    private Map<String, byte[]> m_htJarContents = 
            new Hashtable<String, byte[]>();

    /**
     * a jar file
     */
    private String m_jarFileName;

    /**
     * creates a JarResources. It extracts all resources from a Jar into an
     * internal hashtable, keyed by resource names.
     * 
     * @param jFileName
     *            a jar or ZIP file
     */
    public JarResources(String jFileName) {
        this.m_jarFileName = jFileName;
        init();
    }

    /**
     * Extracts a jar resource as a blob.
     * 
     * @param name
     *            a resource name.
     * @return byte[]
     */
    public byte[] getResource(String name) {
        return m_htJarContents.get(name);
    }

    /** initializes internal hash tables with Jar file resources. */
    private void init() {
        ZipInputStream zis = null;
        try {
            // extracts just sizes only.
            ZipFile zf = new ZipFile(m_jarFileName);
            try {
                Enumeration e = zf.entries();
                while (e.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry) e.nextElement();

                    m_htSizes.put(ze.getName(), new Integer((int)ze.getSize()));
                }
            } finally {
                zf.close();
            }

            // extract resources and put them into the hashtable.
            FileInputStream fis = new FileInputStream(m_jarFileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            zis = new ZipInputStream(bis);
            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    continue;
                }

                int size = (int) ze.getSize();
                // -1 means unknown size.
                if (size == -1) {
                    size = m_htSizes.get(ze.getName()).intValue();
                }

                byte[] b = new byte[size];
                int rb = 0;
                int chunk = 0;
                while ((size - rb) > 0) {
                    chunk = zis.read(b, rb, size - rb);
                    if (chunk == -1) {
                        break;
                    }
                    rb += chunk;
                }

                // add to internal resource hashtable
                m_htJarContents.put(ze.getName(), b);
            }
        // (AL, 2007-03-21) Analysis of this code:
        // The following catch statements provide worst case fallbacks:
        // all information gathered until an exception happens are returned,
        // further processing is aborted. Therefore the calling methods may
        // not find all the data which is available in the jar, but this
        // should only happen if the jar is in some way corrupt.
        // Since it's not clear where this class may be used (i.e.
        // in a server context), no logging is done to avoid
        // class loader problems
        } catch (NullPointerException e) { // NOPMD by al on 3/21/07 10:58 AM
            // do nothing
        } catch (FileNotFoundException e) { // NOPMD by al on 3/21/07 10:58 AM
            // do nothing
        } catch (IOException e) { // NOPMD by al on 3/21/07 10:58 AM
            // do nothing
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
            } catch (IOException e) { // NOPMD by al on 3/19/07 2:00 PM
                // closing file failed, ignored
                // not logged because this may cause classloader problems
                // in the AUTServer                
            }
        }
    }

} // End of JarResources class.
