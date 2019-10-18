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

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.ICondStructPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class is the PropertySource of Conditional Structures.
 * It is used to display and edit the properties in the Properties View.
 *
 * @author BREDEX GmbH
 */
public class CondStructGUIPropertySource extends AbstractNodePropertySource {
    
    /** expected values of negate */
    private static String[] negateOptions = {Boolean.FALSE.toString(),
            Boolean.TRUE.toString()};
    
    /**
     * @param condition the node
     */
    public CondStructGUIPropertySource(INodePO condition) {
        super(condition);
    }

    /** {@inheritDoc} */
    protected void initPropDescriptor() {
        if (!getPropertyDescriptorList().isEmpty()) {
            clearPropertyDescriptors();
        }
        // Name
        String name = ""; //$NON-NLS-1$
        if (getNode() instanceof IConditionalStatementPO) {
            name = Messages.ConditionGUIPropertySourceName;
        } else if (getNode() instanceof IDoWhilePO) {
            name = Messages.DoWhileGUIPropertySourceName;
        } else if (getNode() instanceof IWhileDoPO) {
            name = Messages.WhileDoGUIPropertySourceName;
        }
        addPropertyDescriptor(new TextPropertyDescriptor(
                new ElementNameController(), name));
        // Comment
        super.initPropDescriptor();
        // Negate
        addPropertyDescriptor(new ComboBoxPropertyDescriptor(
                new ConditionNegateController(),
                Messages.ConditionGUIPropertySourceNegate,
                negateOptions));
    }
    
    /**
     * @author BREDEX GmbH
     */
    public class ConditionNegateController extends AbstractPropertyController {
        
        /**
         * @param value index of the new value
         * @return <code>true</code> if setting operation was successful.
         */
        public boolean setProperty(Object value) {
            if (value == null) {
                return false;
            }
            boolean isNegate = Boolean.parseBoolean(negateOptions[(int)value]);
            ((ICondStructPO)getPoNode()).setNegate(isNegate);
            DataEventDispatcher.getInstance().firePropertyChanged(false);
            return true;
        }

        /**
         * @return the index of value of negate
         */
        public Object getProperty() {
            boolean isNegate = ((ICondStructPO)getPoNode()).isNegate();
            for (int i = 0; i < negateOptions.length; i++) {
                if (negateOptions[i].equals(String.valueOf(isNegate))) {
                    return Integer.valueOf(i);
                }
            }
            return Integer.valueOf(0);
        }
    }
}
