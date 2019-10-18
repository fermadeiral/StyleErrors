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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider.decorators;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.provider.labelprovider.decorators.AbstractLightweightLabelDecorator;


/**
 * @author BREDEX GmbH
 * @created 03.03.2009
 */
public class ExternalDataDecorator extends AbstractLightweightLabelDecorator {
    /**
     * {@inheritDoc}
     */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof IParamNodePO) {
            IParamNodePO pnpo = (IParamNodePO)element;
            if (!StringUtils.isEmpty(pnpo.getDataFile())) {
                decoration.addOverlay(
                        IconConstants.EXCEL_DATA_IMAGE_DESCRIPTOR);
            } else if (pnpo.getReferencedDataCube() != null) {
                decoration.addOverlay(
                        IconConstants.TDC_DECORATION_IMAGE_DESCRIPTOR);
            }
        }
    }
}
