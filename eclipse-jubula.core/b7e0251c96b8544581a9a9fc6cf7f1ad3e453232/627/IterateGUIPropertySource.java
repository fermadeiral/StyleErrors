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
package org.eclipse.jubula.client.ui.rcp.controllers.propertysources;


import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.ui.rcp.factory.TestDataControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.ParameterValueLabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class is the PropertySource of a For Loop.
 * Its used to display and edit the properties in the Properties View.
 *
 * @author BREDEX GmbH
 *
 */
public class IterateGUIPropertySource extends AbstractNodePropertySource {

    /**
     * @param condition the node
     */
    public IterateGUIPropertySource(INodePO condition) {
        super(condition);
    }
    
    /** {@inheritDoc} */
    protected void initPropDescriptor() {
        clearPropertyDescriptors();
        addPropertyDescriptor(new TextPropertyDescriptor(
                new ElementNameController(),
                Messages.IterateGUIPropertySourceName));
        super.initPropDescriptor();
        for (IParamDescriptionPO desc : getParamNode().getParameterList()) {
            // Parameter Value
            ParameterValueController paramCtrl = new ParameterValueController(
                    this, desc, getActiveParamNameMapper());
            PropertyDescriptor descr = 
                TestDataControlFactory.createValuePropertyDescriptor(
                        paramCtrl, getParameterNameDescr(desc), 
                        new HashMap<>(), false);
            ILabelProvider labelProvider;
            labelProvider = new ParameterValueLabelProvider(
                    INCOMPL_DATA_IMAGE);
            descr.setLabelProvider(labelProvider);
            descr.setCategory("Parameters"); //$NON-NLS-1$                
            addPropertyDescriptor(descr);
        }
    }
    
    /**
     * @return the paramNode
     */
    IParamNodePO getParamNode() {
        return (IParamNodePO)getPoNode();
    }
    
    /**
     * Class to control parameter value
     * @author BREDEX GmbH
     * @created 07.01.2005
     */
    public class ParameterValueController extends
        AbstractParamValueController {
        
        /**
         * Constructor.
         * @param desc The parameter description
         * @param s AbstractNodePropertySource
         * @param paramNameMapper the param name mapper
         */
        public ParameterValueController(AbstractNodePropertySource s,
            IParamDescriptionPO desc, IParamNameMapper paramNameMapper) {
            super(s, desc, paramNameMapper);
        }
        
    }
    
}
