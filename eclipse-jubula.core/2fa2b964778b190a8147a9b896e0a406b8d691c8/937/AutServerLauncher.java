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
package org.eclipse.jubula.rc.common;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

import org.eclipse.jubula.tools.internal.constants.AUTServerExitConstants;


/**
 * @author BREDEX GmbH
 * @created 10.11.2005
 */
public class AutServerLauncher {
    
    /** <code>PATH_SEPARATOR</code> */
    public static final String PATH_SEPARATOR = System.getProperty("path.separator"); //$NON-NLS-1$
    
    /** 
     * name for environment variable that should be set if old/classic class 
     * loading should be used 
     */
    private static final String ENV_VAR_USE_CLASSIC_CLASSLOADER = 
        "TEST_USE_CLASSIC_CL"; //$NON-NLS-1$

    /**
     * hidden constructor
     */
    private AutServerLauncher() {
        super();
    }

    /**
     * Sets the classpaths, <br>
     * creates a ClassLoader for the AUTServer with its own classpath,<br>
     * loads the AUTServer with the created ClassLoader <br>
     * and calls the main-method of the AUTServer via Reflection.<br>
     * @param args Arguments of the main method
     * {@inheritDoc}
     */
    public static void main(String[] args) {
        // create separate classloader for the AUT-Server 
        // with Ext-ClassLoader as parent (if using "old/classic" class loading)
        // or App-ClassLoader as parent (if using "new/default" class loading).
        // ClassPath is AUT-Server-Path WITHOUT AUT-Path!
        URL[] urls = PathSplitter.createUrls(
                args[Constants.ARG_AUTSERVER_CLASSPATH]);
        URLClassLoader autServerClassLoader;
        
        String useClassicClassLoaderValue = null;
        try {
            // Unfortunately, we cannot use our EnvironmentUtils here
            // because it's not on the classpath yet.
            useClassicClassLoaderValue =
                System.getenv(ENV_VAR_USE_CLASSIC_CLASSLOADER);
        } catch (Throwable t) {
            // OK, looks like we won't be able to access environment variables.
            // We'll just assume that the variable wasn't set.
        }       
        if (useClassicClassLoaderValue == null) {
            // Use JVM property as fallback
            useClassicClassLoaderValue = 
                System.getProperty(ENV_VAR_USE_CLASSIC_CLASSLOADER);
        }     
        if (useClassicClassLoaderValue != null) {
            // Use the old class loading
            autServerClassLoader = new UrlClassicClassLoader(urls, 
                    ClassLoader.getSystemClassLoader().getParent());
        } else {
            // Use the new class loading
            autServerClassLoader = new UrlDefaultClassLoader(urls, 
                    ClassLoader.getSystemClassLoader());
        }        
        Thread.currentThread().setContextClassLoader(autServerClassLoader);
        try {
            // the AUTServer is loaded with a separate ClassLoader because
            // the AUT MUST NOT know the classpath of the AUTServer!!!
            Class autServerClass = autServerClassLoader
                .loadClass(Constants.AUTSERVER_CLASSNAME);
            Method mainMethod = autServerClass.getMethod("main",  //$NON-NLS-1$
                new Class[] {args.getClass()});
            mainMethod.invoke(null, new Object[] {args});            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(AUTServerExitConstants.AUT_START_ERROR_CNFE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(AUTServerExitConstants.AUT_START_ERROR_IACCE);
        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(AUTServerExitConstants
                .EXIT_SECURITY_VIOLATION_REFLECTION);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(AUTServerExitConstants.AUT_START_ERROR_NSME);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.exit(AUTServerExitConstants.AUT_START_ERROR_IARGE);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.exit(AUTServerExitConstants.AUT_START_ERROR_INVTE);
        } 
    }
    
    /**
     * This ClassLoader tries to load the classes first.
     * It overrides the parent delegation!
     *
     * @author BREDEX GmbH
     * @created 16.01.2006
     */
    private static class UrlClassicClassLoader extends URLClassLoader {
        /**
         * Constructor
         * @param urls URL[]
         * @param parent ClassLoader
         */
        public UrlClassicClassLoader(URL[] urls, ClassLoader parent) {
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

    /**
     * This ClassLoader tries to load the classes first.
     * It overrides the parent delegation!
     *
     * @author BREDEX GmbH
     * @created 01.12.2009
     */
    private static class UrlDefaultClassLoader extends URLClassLoader {
        
        /**
         * Constructor
         * @param urls URL[]
         * @param parent ClassLoader
         */
        public UrlDefaultClassLoader(URL[] urls, ClassLoader parent) {
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
                    // overriding parent delegation!
                    c = findClass(name);
                }  catch (ClassNotFoundException e) {
                    // if not found, normal behaviour of class loading
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

        /**
         * Overrides the standard way of finding a resource, which is to first
         * check the parent class loader. Instead, we first try to find the 
         * resource ourselves. If we are unable to find it, we then fall back
         * to the parent class loader.
         * 
         * {@inheritDoc}
         */
        public URL getResource(String name) {
            URL resourceUrl = findResource(name);
            return resourceUrl != null ? resourceUrl : super.getResource(name);
        }

    }
    
    /**
     * @author BREDEX GmbH
     * @created 27.10.2005
     */
    private static class PathSplitter {

        /**
         * utility constructor
         */
        private PathSplitter() {
            // utility class
        }

        /**
         * Splits the given String with paths separated with 
         * Operating-System-Path-Separators into an Array of Strings
         * @param paths the paths to split
         * @return an Array of split paths
         */
        private static String[] split(String paths) {
            String pathSeparator = System.getProperty("path.separator"); //$NON-NLS-1$
            StringTokenizer pathElems = new StringTokenizer(paths, 
                    pathSeparator);
            String[] pathElements = new String[pathElems.countTokens()];
            int i = 0;
            while (pathElems.hasMoreTokens()) {
                pathElements[i] = pathElems.nextToken();
                i++;
            }
            return pathElements;
        }
        
        /**
         * @param classpath a classpath separated with OS-depending separators
         * @return an URL array of the given classpath.
         */
        public static URL[] createUrls(String classpath) {
            String[] paths = PathSplitter.split(classpath);
            final int pathLength = paths.length;
            URL[] urls = new URL[pathLength];
            try {
                for (int i = 0; i < pathLength; i++) {
                    File file = new File(paths[i]);
                    URL url = file.toURI().toURL();
                    urls[i] = url;
                }
            } catch (MalformedURLException e) {
                return new URL[0];
            }
            return urls;
        }
    }
    
}