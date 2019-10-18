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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 20.04.2010
 */
public class SerializedImage {
    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            SerializedImage.class);

    /**
     * <code>IMAGE_FORMAT</code>
     */
    private static final String IMAGE_FORMAT = "png"; //$NON-NLS-1$

    /**
     * <code>m_data</code> the image data
     */
    private byte[] m_data = null;

    /**
     * Constructor
     */
    private SerializedImage() {
    // empty
    }

    /**
     * @param img
     *            an java.awt.image.BufferedImage
     * @return a serializable image format
     */
    public static SerializedImage computeSerializeImage(BufferedImage img) {
        SerializedImage si = new SerializedImage();
        ByteArrayOutputStream imageByteOutputStream = new 
                ByteArrayOutputStream();
        try {
            ImageIO.write(img, IMAGE_FORMAT, imageByteOutputStream);
            imageByteOutputStream.flush();
            si.setData(imageByteOutputStream.toByteArray());
            imageByteOutputStream.close();
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return si;
    }

    /**
     * @param si
     *            the serialized image
     * @return an java.awt.image.BufferedImage or null in case of an I/O Problem
     */
    public static BufferedImage computeImage(SerializedImage si) {
        BufferedImage bi = null;
        try {
            ByteArrayInputStream imageByteInputStream = 
                new ByteArrayInputStream(
                    si.getData());
            bi = ImageIO.read(imageByteInputStream);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return bi;
    }
    
    /**
     * @param data
     *            the image data to set
     */
    private void setData(byte[] data) {
        m_data = data;
    }

    /**
     * @return the image data
     */
    public byte[] getData() {
        return m_data;
    }
}
