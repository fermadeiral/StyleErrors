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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;
import org.eclipse.jubula.client.core.utils.NullValidator;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.AbstractNodePropertySource.AbstractParamValueController;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;



/**
 * Label provider for Parameter values (for example, in Test Steps). 
 *
 * @author BREDEX GmbH
 * @created Apr 6, 2010
 */
public class ParameterValueLabelProvider extends LabelProvider 
        implements IColorProvider {

    /** image to use for missing test data */
    private Image m_missingDataImage;
    
    /**
     * Constructor
     * 
     * @param missingDataImage The image to use for missing test data.
     */
    public ParameterValueLabelProvider(Image missingDataImage) {
        Validate.notNull(missingDataImage);
        m_missingDataImage = missingDataImage;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        if (element instanceof AbstractParamValueController) {
            AbstractParamValueController controller = 
                (AbstractParamValueController)element;
            return String.valueOf(controller.getProperty());
        }
        return super.getText(element);
    }
    
    /**
     * {@inheritDoc}
     */
    public Image getImage(Object element) {

        if (element instanceof AbstractParamValueController) {
            AbstractParamValueController controller = 
                (AbstractParamValueController)element;
            IParamNodePO paramNode = controller.getParamNode();
    
            if (controller.getPropertySource().isReadOnly()) {
                return IconConstants.READ_ONLY_IMAGE;
            }
            
            if (StringUtils.isNotEmpty(paramNode.getDataFile())) {
                return IconConstants.EXCEL_DATA_IMAGE;
            }
            
            String property = controller.getProperty();
            if (StringConstants.EMPTY.equals(property)) {
                return m_missingDataImage;
            }
            if (paramNode.getReferencedDataCube() != null) {
                return IconConstants.TDC_IMAGE;
            }
            
            ParamValueConverter conv = new GuiParamValueConverter(
                    property, paramNode, 
                    controller.getParamDesc(), new NullValidator());
            if (conv.containsReferences()) {
                if (paramNode.getSpecAncestor() instanceof ITestSuitePO) {
                    return m_missingDataImage;
                }
                return IconConstants.REF_VALUE_IMAGE;
            } else if (paramNode instanceof IExecTestCasePO) {
                IExecTestCasePO exTc = (IExecTestCasePO)paramNode;
                
                if (exTc.getHasReferencedTD()) {
                    return IconConstants.ORIGINAL_DATA_IMAGE;
                }
                return IconConstants.OVERWRITTEN_DATA_IMAGE;
            }

            return null;
        }
        
        return super.getImage(element);
    
    }

    /**
     * {@inheritDoc}
     */
    public Color getBackground(Object element) {
        // Background doesn't yet interest us.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getForeground(Object element) {
        if (element instanceof AbstractParamValueController) {
            AbstractParamValueController controller = 
                (AbstractParamValueController)element;
            IParamNodePO paramNode = controller.getParamNode();

            if (paramNode.getReferencedDataCube() != null) {
                return LayoutUtil.GRAY_COLOR;
            }
        }
        return null;
    }
    
}
