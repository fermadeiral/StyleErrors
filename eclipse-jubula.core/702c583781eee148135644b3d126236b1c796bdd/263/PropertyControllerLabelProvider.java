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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.ui.controllers.propertysources.IPropertyController;
import org.eclipse.swt.graphics.Image;


/**
 * Label provider for {@link IPropertyController}s.
 *
 * @author BREDEX GmbH
 * @created Apr 8, 2010
 */
public class PropertyControllerLabelProvider extends LabelProvider {

    /**
     * {@inheritDoc}
     */
    public Image getImage(Object element) {
        if (element instanceof IPropertyController) {
            IPropertyController controller = 
                (IPropertyController)element;
            return controller.getImage();
        }
        
        return super.getImage(element);
    }
    
}
