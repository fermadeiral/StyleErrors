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
package org.eclipse.jubula.app;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jubula.client.ui.utils.ImageUtils;
import org.eclipse.jubula.version.Vn;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.eclipse.jubula.app"; //$NON-NLS-1$

    /**
     * <code>IMAGE_PNG_JB_128_128_ID</code>
     */
    public static final String IMAGE_PNG_JB_128_128_ID = "IMAGE_PNG_JB_128_128_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_PNG_JB_64_64_ID</code>
     */
    public static final String IMAGE_PNG_JB_64_64_ID = "IMAGE_PNG_JB_64_64_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_PNG_JB_48_48_ID</code>
     */
    public static final String IMAGE_PNG_JB_48_48_ID = "IMAGE_PNG_JB_48_48_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_PNG_JB_32_32_ID</code>
     */
    public static final String IMAGE_PNG_JB_32_32_ID = "IMAGE_PNG_JB_32_32_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_PNG_JB_16_16_ID</code>
     */
    public static final String IMAGE_PNG_JB_16_16_ID = "IMAGE_PNG_JB_16_16_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_GIF_JB_128_128_ID</code>
     */
    public static final String IMAGE_GIF_JB_128_128_ID = "IMAGE_GIF_JB_128_128_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_GIF_JB_64_64_ID</code>
     */
    public static final String IMAGE_GIF_JB_64_64_ID = "IMAGE_GIF_JB_64_64_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_GIF_JB_48_48_ID</code>
     */
    public static final String IMAGE_GIF_JB_48_48_ID = "IMAGE_GIF_JB_48_48_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_GIF_JB_32_32_ID</code>
     */
    public static final String IMAGE_GIF_JB_32_32_ID = "IMAGE_GIF_JB_32_32_ID"; //$NON-NLS-1$

    /**
     * <code>IMAGE_GIF_JB_16_16_ID</code>
     */
    public static final String IMAGE_GIF_JB_16_16_ID = "IMAGE_PNG_JB_16_16_ID"; //$NON-NLS-1$

    /** 
     * Key for retrieving this bundle's version number from 
     * the JVM's Properties. The property is set during bundle activation. 
     */
    public static final String VERSION_PROPERTY_KEY = "org.eclipse.jubula.ite.version"; //$NON-NLS-1$
    
    /** The shared instance */
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
        // empty
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        System.setProperty(VERSION_PROPERTY_KEY, 
                Vn.getDefault().getVersion().toString());
        System.setProperty("org.eclipse.jetty.util.log.class", //$NON-NLS-1$
                "org.eclipse.jetty.util.log.StdErrLog"); //$NON-NLS-1$
        System.setProperty("org.eclipse.jetty.LEVEL", "WARN"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        registry.put(IMAGE_PNG_JB_128_128_ID,
                getImageDescriptor("jubula128x128.png")); //$NON-NLS-1$
        registry.put(IMAGE_PNG_JB_64_64_ID,
                getImageDescriptor("jubula64x64.png")); //$NON-NLS-1$
        registry.put(IMAGE_PNG_JB_48_48_ID,
                getImageDescriptor("jubula48x48.png")); //$NON-NLS-1$
        registry.put(IMAGE_PNG_JB_32_32_ID,
                getImageDescriptor("jubula32x32.png")); //$NON-NLS-1$
        registry.put(IMAGE_PNG_JB_16_16_ID,
                getImageDescriptor("jubula16x16.png")); //$NON-NLS-1$
        registry.put(IMAGE_GIF_JB_128_128_ID,
                getImageDescriptor("jubula128x128.gif")); //$NON-NLS-1$
        registry.put(IMAGE_GIF_JB_64_64_ID,
                getImageDescriptor("jubula64x64.gif")); //$NON-NLS-1$
        registry.put(IMAGE_GIF_JB_48_48_ID,
                getImageDescriptor("jubula48x48.gif")); //$NON-NLS-1$
        registry.put(IMAGE_GIF_JB_32_32_ID,
                getImageDescriptor("jubula32x32.gif")); //$NON-NLS-1$
        registry.put(IMAGE_GIF_JB_16_16_ID,
                getImageDescriptor("jubula16x16.gif")); //$NON-NLS-1$
    }
    
    /**
     * @param name
     *            the file name URL
     * @return the image descriptor for the given file url
     */
    private ImageDescriptor getImageDescriptor(String name) {
        return ImageUtils.getImageDescriptor(Platform.getBundle(PLUGIN_ID),
                name);
    }
}
