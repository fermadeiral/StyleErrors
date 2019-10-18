/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.html;

/**
 * http://en.wikipedia.org/wiki/Display_resolution#/media/File:Vector_Video_Standards8.svg
 * @since 3.2
 */
public enum BrowserSize {

    /** Fullscreen */
    FULLSCREEN(0, 0),
    /** VGA */
    VGA(640, 480),
    /** SVGA */
    SVGA(800, 600),
    /** XGA */
    XGA(1024, 768),
    /** 1152x768 */
    _1152x768(1152, 768),
    /** SXGA */ 
    SXGA(1280, 1024),
    /** 1440x900 */
    _1440x900(1440, 900),
    /** 1600x900 */ 
    _1600x900(1600, 900),
    /** UXGA */ 
    UXGA(1600, 1200), 
    /** HD_1080 */
    HD_1080(1920, 1080),
    /** WUXGA */ 
    WUXGA(1920, 1200),
    /** WQHD */ 
    WQHD(2560, 1440), 
    /** UHD */
    UHD(3840, 2160),
    /** 4K */ 
    _4K(4096, 2160);
    
    /** width */
    private final int m_width;
    /** height */
    private final int m_height;

    /**
     * @param width the width
     * @param height the heigth
     */
    BrowserSize(int width, int height) {
        this.m_width = width;
        this.m_height = height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return m_width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return m_height;
    }
}
