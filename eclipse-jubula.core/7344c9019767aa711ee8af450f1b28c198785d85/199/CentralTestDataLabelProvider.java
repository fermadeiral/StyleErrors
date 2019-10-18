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

import java.util.Iterator;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created Jun 28, 2010
 */
public class CentralTestDataLabelProvider extends GeneralLabelProvider {
    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        if (element instanceof IParameterInterfacePO) {
            IParameterInterfacePO tdc = (IParameterInterfacePO)element;
            StringBuilder info = new StringBuilder(tdc.getName());
            Iterator iter = tdc.getParameterList().iterator();
            boolean parameterExist = false;
            if (iter.hasNext()) {
                parameterExist = true;
                info.append(OPEN_BRACKED);
            }
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    IParamDescriptionPO descr = (IParamDescriptionPO)iter
                            .next();
                    info.append(
                            CompSystemI18n.getString(descr.getType(), true));
                    info.append(StringConstants.COLON);
                    info.append(descr.getName());
                    if (iter.hasNext()) {
                        info.append(SEPARATOR);
                    }
                }
            }
            if (parameterExist) {
                info.append(CLOSE_BRACKED);
            }
            return info.toString();
        }
        
        if (element instanceof ITestDataCategoryPO) {
            return ((ITestDataCategoryPO)element).getName();
        }
        
        return super.getText(element);
    }
    
    /**
     * {@inheritDoc}
     */
    public Image getImage(Object element) {
        if (element instanceof ITestDataCubePO) {
            return IconConstants.TDC_IMAGE;
        }
        
        if (element instanceof ITestDataCategoryPO) {
            return IconConstants.CATEGORY_IMAGE;
        }
        
        return super.getImage(element);
    }
}
