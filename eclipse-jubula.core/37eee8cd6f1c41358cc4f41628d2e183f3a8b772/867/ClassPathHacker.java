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
package org.eclipse.jubula.tools.internal.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class to edit classpath in runtime
 * @author BREDEX GmbH
 *
 */
public class ClassPathHacker {

    /**
     * parameters
     */
    private static final Class[] PARAMETERS = new Class[]{URL.class};

    /**
     * hide contructor
     *
     */
    private ClassPathHacker() {
        //nothing
    }
    

    /**
     * AddFile
     * @param f file
     * @throws IOException Error
     */
    public static void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }

    /**
     * AddFile
     * @param u URL
     * @throws IOException Error
     */
    public static void addURL(URL u) throws IOException {
            
        URLClassLoader sysloader = 
            (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.
                getDeclaredMethod("addURL", PARAMETERS); //$NON-NLS-1$
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{ u });
        } catch (NoSuchMethodException t) {
            // no log available here
            throw new IOException(
                "Error, could not add URL to system classloader"); //$NON-NLS-1$
        } catch (IllegalAccessException t) {
            // no log available here
            throw new IOException(
                "Error, could not add URL to system classloader"); //$NON-NLS-1$
        } catch (InvocationTargetException t) {
            // no log available here
            throw new IOException(
                "Error, could not add URL to system classloader"); //$NON-NLS-1$
        }

    }
    
    /**
     * Add URL to given Classloader
     * 
     * @param u
     *            URL
     * @param classLoader
     *            the Classloader
     * @throws IOException
     *             Error
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void addURL(URL u, ClassLoader classLoader)
            throws IOException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Class sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL", PARAMETERS); //$NON-NLS-1$
        method.setAccessible(true);
        method.invoke(classLoader, new Object[] { u });
    }
    
    /**
     * Recursive method used to find all classes in a given directory and
     * subdirectories.
     * 
     * @param directoryUrl
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     * @throws MalformedURLException 
     */
    public static List<Class> findClasses(URL directoryUrl, String packageName)
        throws ClassNotFoundException, MalformedURLException {
        List<Class> classes = new ArrayList<Class>();
        File directory = new File(directoryUrl.getFile());
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String fileName = file.getName();
            if (file.isDirectory()) {
                classes.addAll(findClasses(file.toURI().toURL(),
                        packageName + '.' + fileName));
            } else if (fileName.endsWith(".class")) { //$NON-NLS-1$
                classes.add(Class.forName(packageName + '.'
                        + fileName.substring(0, fileName.length() - 6)));
            }
        }
        return classes;
    }
    
    /**
     * method to find all classes in a given jar
     * 
     * @param resource
     *            The URL to the jar file
     * @param pkgname
     *            The package name for classes found inside the base directory
     * @param classLoader
     *            the Classloader which should be used
     * @return The classes
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    @SuppressWarnings("nls")
    public static List<Class> findClassesInJar(URL resource, String pkgname,
            ClassLoader classLoader) 
            throws IOException, ClassNotFoundException {
        String relPath = pkgname.replace('.', '/');
        String path = resource.getPath()
                .replaceFirst("[.]jar[!].*", ".jar")
                .replaceFirst("file:", "");
        path = path.replaceFirst("bundleFile[!].*", "bundleFile");
            //Important for RCP accessor bundles
        path = URLDecoder.decode(path, "utf-8");
        List<Class> classes = new ArrayList<Class>();
        JarFile jarFile = null;
        try {            
            jarFile = new JarFile(path);        
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                String className = null;
                if (entryName.endsWith(".class")
                        && entryName.startsWith(relPath)) {
                    className = entryName.replace('/', '.').replace('\\', '.')
                            .replaceAll(".class", "");
                
                    if (className != null) {
                        classes.add(Class.forName(
                                className, true, classLoader));
                    }
                }
            }
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
        return classes;
    }
}
