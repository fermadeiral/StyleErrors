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
package org.eclipse.jubula.client.ui.views.imageview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.utils.ImageUtils;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created Apr 14, 2010
 */
public class TestResultNodeImageProvider implements ImageProvider {
    /**
     * <code>m_testResultNode</code>
     */
    private TestResultNode m_testResultNode;

    /**
     * @param testresultnode
     *            the test result node
     */
    public TestResultNodeImageProvider(TestResultNode testresultnode) {
        m_testResultNode = testresultnode;
    }
    
    /**
     * {@inheritDoc}
     */
    public ImageViewData getImageViewData(Device target) {
        byte[] screenshot = m_testResultNode.getScreenshot();
        if (screenshot != null) {
            Image img = new Image(target, ImageUtils.getImageData(screenshot));
            return new ImageViewData(img, generateImageName(), 
                    generateImageDate());
        }
        return null;
    }
    
    /**
     * generates the name of the image
     * @return the name of the image
     */
    private String generateImageName() {
        TestResultNode parent = m_testResultNode;
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        
        String imgName = "ErrorInTest_" + parent.getName() + "_" //$NON-NLS-1$ //$NON-NLS-2$
                              + m_testResultNode.getNode().getName();
        // eliminate whitespaces and characters which are illegal in a file name
        imgName = imgName.replaceAll("[\\s\\?\\\\/:|<>\\*\"]", ""); //$NON-NLS-1$ //$NON-NLS-2$
        return imgName;
    }
    
    /**
     * generates the date of the image
     * @return the date of the image
     */
    private String generateImageDate() {
        // get the date of test from time stamp
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS"); //$NON-NLS-1$
        String date = format.format(m_testResultNode.getTimeStamp());
        return date;
    }
}
