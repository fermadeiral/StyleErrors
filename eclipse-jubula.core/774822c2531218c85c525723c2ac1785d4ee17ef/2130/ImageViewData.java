/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.views.imageview;

import org.eclipse.swt.graphics.Image;

/**
 * Class representing an image from the image view and its data 
 * @author BREDEX GmbH
 * @created Nov 08, 2013
 */
public class ImageViewData {
    /**
     * the image
     */
    private Image m_img;
    
    /**
     * the name of the image
     */
    private String m_imgName;
    
    /**
     * the date of the image
     */
    private String m_imgDate;
    
    /**
     * constructor
     * @param img the image 
     * @param name the name of the image
     * @param date the date of the image
     */
    public ImageViewData(Image img, String name, String date) {
        m_img = img;
        m_imgName = name;
        m_imgDate = date;
    }
    
    /**
     * returns the image
     * @return the image
     */
    public Image getImage() {
        return m_img;
    }
    
    /**
     * returns the name of the image
     * @return the name of the image
     */
    public String getImageName() {
        return m_imgName;
    }

    /**
     * returns the date of the image
     * @return the date of the image
     */
    public String getImageDate() {
        return m_imgDate;
    }

    /**
     * Disposes the image.
     */
    public void dispose() {
        m_img.dispose();
    }
}
