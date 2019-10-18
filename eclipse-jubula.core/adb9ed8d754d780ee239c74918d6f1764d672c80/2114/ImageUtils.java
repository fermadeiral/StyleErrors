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
package org.eclipse.jubula.client.ui.utils;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.client.core.utils.BundleUtils;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.osgi.framework.Bundle;

/**
 * Utility class for swt images
 * 
 * @author BREDEX GmbH
 * @created Dec 17, 2009
 */
public class ImageUtils {
    /**
     * <code>IMAGE_FOLDER_NAME</code>
     */
    private static final String IMAGE_FOLDER_NAME = "/icons/"; //$NON-NLS-1$
    
    /** hide constructor */
    private ImageUtils() {
    // hide
    }

    /**
     * @param image
     *            the image data
     * @return the swt image data
     */
    public static ImageData getImageData(byte[] image) {
        ImageLoader imageLoader = new ImageLoader();
        ByteArrayInputStream stream = new ByteArrayInputStream(image);
        return imageLoader.load(stream)[0];
    }

    /**
     * @param b
     *            the bundle to resolve the image descriptor from
     * @param name
     *            String the file name URL
     * @return ImageDescriptor from URL
     */
    public static ImageDescriptor getImageDescriptor(Bundle b, String name) {
        return ImageDescriptor.createFromURL(BundleUtils.getFileURL(b,
                IMAGE_FOLDER_NAME + name));
    }
}
