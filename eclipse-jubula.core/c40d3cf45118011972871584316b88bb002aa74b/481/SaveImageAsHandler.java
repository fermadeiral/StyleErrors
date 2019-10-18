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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.views.imageview.ImageView;
import org.eclipse.jubula.client.ui.views.imageview.ImageViewData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created 11.09.2013
 */
public class SaveImageAsHandler extends AbstractSelectionBasedHandler {

    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event) {
        IViewPart view = HandlerUtil.getActiveSite(event).
                getPage().findView(Constants.IMAGEVIEW_ID);
        
        if (view != null && view instanceof ImageView) {            
            ImageView imageView = (ImageView)view;
            int maxFileNameLength = 255;
            
            ImageViewData imageViewData = imageView.getImageViewData();
            String imageName = imageViewData.getImageName();
            String imageDate = imageViewData.getImageDate();
                        
            FileDialog saveDialog = new FileDialog(getActiveShell(), SWT.SAVE);
            String fileEnding = "_" + imageDate + ".png"; //$NON-NLS-1$ //$NON-NLS-2$
            String fileName = StringUtils.substring(imageName, 0,
                    maxFileNameLength
                    - fileEnding.length()
                    - saveDialog.getFilterPath().length());
            fileName = fileName + fileEnding;
            
            saveDialog.setFileName(fileName);
            saveDialog.setFilterExtensions(new String[] { "*.png" }); //$NON-NLS-1$
            saveDialog.setOverwrite(true);
            String path = saveDialog.open();

            ImageData imageData = imageViewData.getImage().getImageData();
            
            if (path != null) {
                if (imageData != null) {
                    ImageLoader loader = new ImageLoader();
                    loader.data = new ImageData[] { imageData };
                    loader.save(path, SWT.IMAGE_PNG);
                }
            }
        }
        return null;
    }

}
