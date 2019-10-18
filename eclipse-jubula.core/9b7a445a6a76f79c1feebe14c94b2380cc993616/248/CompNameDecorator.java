/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider.decorators;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.provider.labelprovider.decorators.AbstractLightweightLabelDecorator;

/**
 * Decorates Component Names with type errors
 * @author BREDEX GmbH
 *
 */
public class CompNameDecorator extends AbstractLightweightLabelDecorator {

    @Override
    public void decorate(Object element, IDecoration decoration) {
        IComponentNamePO cN = (IComponentNamePO) element;
        if (cN.getTypeProblem() != null) {
            decoration.addOverlay(IconConstants.ERROR_IMAGE_DESCRIPTOR);
        }
    }
}