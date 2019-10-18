/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.adaptable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.rc.common.classloader.DefaultUrlLocator;
import org.eclipse.jubula.rc.common.classloader.IUrlLocator;
import org.eclipse.jubula.tools.internal.utils.ClassPathHacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton to register adapter factories
 */
public class AdapterFactoryRegistry {
    /** the name of the package to search for adapters */
    public static final String ADAPTER_PACKAGE_NAME = "org.eclipse.jubula.rc.common.adapter"; //$NON-NLS-1$

    /** the name of the package to search for extension adapters */
    public static final String EXT_ADAPTER_PACKAGE_NAME = "org.eclipse.jubula.ext.rc.common.adapter"; //$NON-NLS-1$
    
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(AdapterFactoryRegistry.class);

    /**
     * Singleton instance of this class
     */
    private static AdapterFactoryRegistry instance = 
        new AdapterFactoryRegistry();

    /**
     * Map that manages the registration. Key is always a class Value is a
     * collection of IAdapterFactory
     */
    private Map<Class, Collection<IAdapterFactory>> m_registrationMap = 
        new HashMap<Class, Collection<IAdapterFactory>>();

    /**
     * Call Constructor only by using getInstance
     */
    private AdapterFactoryRegistry() {
    }

    /**
     * Return the singleton of this class
     * 
     * @return singleton
     */
    public static AdapterFactoryRegistry getInstance() {
        return instance;
    }

    /**
     * Register adapter factory with all its supported classes
     * 
     * @param factory
     *            adapter factory that should be registered
     */
    public void registerFactory(IAdapterFactory factory) {
        Class[] supportedClasses = factory.getSupportedClasses();
        for (int i = 0; i < supportedClasses.length; i++) {
            Collection<IAdapterFactory> registeredFactories = m_registrationMap
                    .get(supportedClasses[i]);
            if (registeredFactories == null) {
                registeredFactories = new HashSet<IAdapterFactory>();
            }
            registeredFactories.add(factory);
            m_registrationMap.put(supportedClasses[i], registeredFactories);
        }
    }

    /**
     * Sign off adapter factory from all its supported classes
     * 
     * @param factory
     *            adapter factory that should be signed off
     */
    public void signOffFactory(IAdapterFactory factory) {
        Class[] supportedClasses = factory.getSupportedClasses();
        for (int i = 0; i < supportedClasses.length; i++) {
            final Class supportedClass = supportedClasses[i];
            Collection<IAdapterFactory> registeredFactories = 
                m_registrationMap.get(supportedClass);
            if (registeredFactories == null) {
                return;
            }
            registeredFactories.remove(factory);
            m_registrationMap.remove(supportedClass);
        }
    }

    /**
     * @param targetAdapterClass
     *            Type of the adapter
     * @param objectToAdapt
     *            object that should be adapted
     * @return Returns an adapter for the objectToAdapt of type
     *         targetAdapterClass. The collection of all supported adapter
     *         factories is iterated. The first value that is not null will be
     *         returned. <code>Null</code> will only be returned if no adapter
     *         can be found for the targetAdapterClass, none of the given
     *         factories can handle the objectToAdapt or the objectToAdapt
     *         itself is <code>null</code>.
     */
    public Object getAdapter(Class targetAdapterClass, Object objectToAdapt) {
        if (objectToAdapt == null) {
            return null;
        }
        Collection<IAdapterFactory> registeredFactories = null;
        Class superClass = objectToAdapt.getClass();
        while (registeredFactories == null && superClass != Object.class) {
            registeredFactories = m_registrationMap.get(superClass);
            superClass = superClass.getSuperclass();
        }
        if (registeredFactories == null) {
            return null;
        }
        for (Iterator<IAdapterFactory> iterator = registeredFactories
            .iterator(); iterator.hasNext();) {
            IAdapterFactory adapterFactory = iterator.next();
            Object object = adapterFactory.getAdapter(targetAdapterClass,
                objectToAdapt);

            if (object != null) {
                return object;
            }
        }
        return null;
    }

    /**
     * Use this method in eclipse environments.
     * Must be called to initialize the registration of adapters.
     * @param urlLocator The URL location converter needed in eclipse environments.
     */
    public static void initRegistration(IUrlLocator urlLocator) {
        Class[] adapterFactories = findClassesOfType(urlLocator,
                ADAPTER_PACKAGE_NAME, IAdapterFactory.class);
        Class[] externalAdapterFactories = findClassesOfType(urlLocator,
                EXT_ADAPTER_PACKAGE_NAME, IAdapterFactory.class);
        
        List<Class> allFactories = new ArrayList<Class>(
                Arrays.asList(adapterFactories));
        allFactories.addAll(Arrays.asList(externalAdapterFactories));
        
        // Register all found factories
        for (Class c : allFactories) {
            try {
                IAdapterFactory factory = (IAdapterFactory) c.newInstance();
                getInstance().registerFactory(factory);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            } catch (NoClassDefFoundError e) {
                log.error(e.getLocalizedMessage(), e); 
                // because there might be classes which are not available in a specific AUT
            }
        }
    }

    /**
     * Use this method outside of eclipse environments. Must be called to
     * initialize the registration of adapters. This method directly
     * calls {@link AdapterFactoryRegistry#initRegistration(IUrlLocator)} with
     * the {@link DefaultUrlLocator}.
     */
    public static void initRegistration() {
        initRegistration(new DefaultUrlLocator());
    }
    
    /**
     * Investigate a package of subclasses of a specific superclass
     * @param urlLocator
     *            The URL location converter needed in eclipse environments.
     * @param packageName
     *            name of the package
     * @param superclass
     *            parent class for found classes
     * @return found classes
     */
    private static Class[] findClassesOfType(IUrlLocator urlLocator,
            String packageName, Class<IAdapterFactory> superclass) {
        try {
            Class[] allClasses = getClasses(urlLocator, packageName);

            List<Class> assignableClasses = new ArrayList<Class>();
            for (int i = 0; i < allClasses.length; i++) {
                if (superclass.isAssignableFrom(allClasses[i]) 
                        && superclass != allClasses[i]) {
                    assignableClasses.add(allClasses[i]);
                }
            }
            return castListToClassArray(assignableClasses);
        } catch (ClassNotFoundException e) {
            return new Class[0];
        } catch (IOException e) {
            log.warn("error", e);
            return new Class[0];
        }
    }

    /**
     * Cast a list of classes to an array of classes
     * 
     * @param classes
     *            List of classes
     * @return array of classes
     */
    private static Class[] castListToClassArray(List<Class> classes) {
        Class[] arrayClasses = new Class[classes.size()];
        for (int i = 0; i < arrayClasses.length; i++) {
            arrayClasses[i] = classes.get(i);
        }
        return arrayClasses;
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and sub packages.
     * @param urlLocator
     *            The URL location converter needed in eclipse environments.
     * @param packageName
     *            The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(IUrlLocator urlLocator,
            String packageName)
        throws ClassNotFoundException, IOException {
        ClassLoader classLoader = AdapterFactoryRegistry.class.getClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<URL> dirs = new ArrayList<URL>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            try {
                resource = urlLocator.convertUrl(resource);
                dirs.add(resource);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        List<Class> classes = new ArrayList<Class>();
        for (int i = 0; i < dirs.size(); i++) {
            if (dirs.get(i).toString().startsWith("jar:")) { //$NON-NLS-1$
                classes.addAll(ClassPathHacker.
                        findClassesInJar(dirs.get(i), packageName,
                                classLoader));
            } else {
                classes.addAll(ClassPathHacker.findClasses(
                        dirs.get(i), packageName));
            }
        }
        return castListToClassArray(classes);
    }

    /**
     * Checks if the given factory is already registered
     * @param factory the factory to check
     * @return true if the factory is already registered, false otherwise
     */
    public boolean isRegistered(IAdapterFactory factory) {
        Class[] supportedClasses = factory.getSupportedClasses();
        for (int i = 0; i < supportedClasses.length; i++) {
            final Class supportedClass = supportedClasses[i];
            Collection<IAdapterFactory> registeredFactories = m_registrationMap
                    .get(supportedClass);
            if (registeredFactories == null) {
                return false;
            }
            for (IAdapterFactory iAdapterFactory : registeredFactories) {
                if (iAdapterFactory.getClass().equals(factory.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }
}