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

import org.eclipse.swt.graphics.Device;

/**
 * @author BREDEX GmbH
 * @created Apr 14, 2010
 */
public interface ImageProvider {
    /**
     * This method returns an {@link ImageViewData}. The implementor must return
     * an ImageViewData with an image that can be disposed() by the caller. The
     * implementor is not given any notification that the dispose is occurring.
     * 
     * @param target
     *            The device the image will be displayed on.
     * 
     * @return {@link ImageViewData}
     */
    ImageViewData getImageViewData(Device target);
}
