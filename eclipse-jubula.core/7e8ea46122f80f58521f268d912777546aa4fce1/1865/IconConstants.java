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
package org.eclipse.jubula.client.inspector.ui.constants;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author BREDEX GmbH
 * @created Jul 10, 2009
 */
public class IconConstants {

    /** Mail image */
    public static final Image INSPECTOR = getImage("inspector.png"); //$NON-NLS-1$
    
    /** ID of Inspector Plugin */
    private static final String PLUGIN_ID = "org.eclipse.jubula.client.inspector.ui"; //$NON-NLS-1$
    
    /** 
     * Private constructor to prevent instantiation
     */
    private IconConstants() {
        // do nothing
    }

    /**
     * @param imageName The filename of the image for which to get the 
     *                  descriptor.
     * @return an image descriptor for the given filename, or the "mising" 
     *         image descriptor if the image could not be found. 
     *         Will not return <code>null</code>. 
     */
    private static ImageDescriptor getImageDescriptor(String imageName) {
        StringBuilder sb = new StringBuilder("icons/"); //$NON-NLS-1$
        sb.append(imageName);
        ImageDescriptor descriptor = 
            AbstractUIPlugin.imageDescriptorFromPlugin(
                    PLUGIN_ID, sb.toString());
        return descriptor != null ? descriptor 
                : ImageDescriptor.getMissingImageDescriptor();
    }

    /**
     * 
     * @param imageName The filename of the image.
     * @return an image for the given file name.
     */
    private static Image getImage(String imageName) {
        return getImageDescriptor(imageName).createImage();
    }
}
