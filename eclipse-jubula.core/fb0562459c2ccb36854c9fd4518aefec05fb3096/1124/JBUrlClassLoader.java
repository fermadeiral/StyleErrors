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
package org.eclipse.jubula.rc.common.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * This ClassLoader tries to load the classes first.
 * It overrides the parent delegation!
 *
 * @author BREDEX GmbH
 * @created 16.01.2006
 */
public class JBUrlClassLoader extends URLClassLoader {
    /**
     * Constructor
     * @param urls URL[]
     * @param parent ClassLoader
     */
    public JBUrlClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Tries to load the given class first.<br>
     * <b>This method is the first of the ClassLoader hierarchy to load the class.
     * It overrides the parent-delegation!</b><br>
     * If this method fails to load the class, it calls super.loadClass(...).
     * {@inheritDoc}
     * @param name
     * @param resolve
     * @return
     * @throws ClassNotFoundException
     */
    protected synchronized Class loadClass(String name, boolean resolve) 
        throws ClassNotFoundException {
        
        // First, check if the class has already been loaded
        Class c = findLoadedClass(name);
        if (c == null) {
            try {
                // don't override parent delegation, if class is from a special package
                // e.g. "org.elipse.swt." 
                // FIXME Clemens: pass a list of special packages as parameter in constructors
                if (name.startsWith("org.eclipse.swt")) { //$NON-NLS-1$
                    return super.loadClass(name, resolve);
                }
                // overriding parent delegation!
                c = findClass(name);
            }  catch (ClassNotFoundException e) {
                // if not found, normal behavior of class loading
                return super.loadClass(name, resolve);
            } catch (SecurityException e) {
                return super.loadClass(name, resolve);
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    /**
     * {@inheritDoc}
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

}
