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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;


/**
 * @author BREDEX GmbH
 * @created 03.11.2005
 */
public class CapBP {
   
    /**
     * Utility class
     */
    private CapBP() {
        // do nothing
    }
    
    /**
     * Creates a new CapPO with the given attributes and the 
     * default parameters if existent.
     * @param capName the name
     * @param componentName the name of the component
     * @param componentType the type of the component
     * @param actionName describes actionName corresponding to component
     * @return a new CapPO
     */
    public static ICapPO createCapWithDefaultParams(String capName, 
        String componentName, String componentType, String actionName)  {

        ICapPO cap = null;
        cap = NodeMaker.createCapPO(capName, componentName, componentType,
            actionName);
        IProjectPO project = GeneralStorage.getInstance().getProject();
        Action action = getAction(cap);
        for (String paramName : action.getParamNames()) {
            Param parameter = action.findParam(paramName);
            cap.getDataManager().updateCell(parameter.getDefaultValue(), 
                    0, paramName);
        }
        return cap;
    }
    
    /**
     * Gets the Component of the given CapPO
     * @param cap the CapPO
     * @return the Component of the given CapPO
     */
    public static Component getComponent(ICapPO cap) {
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        String compType = cap.getComponentType();
        return compSystem.findComponent(compType);
    }
    
    /**
     * Gets the Action of the given CapPO
     * @param cap the CapPO
     * @return the Action
     */
    public static Action getAction(ICapPO cap) {
        Component component = getComponent(cap);
        return component.findAction(cap.getActionName());
    }
    
    
    /**
     * Gets all {@link ICapPO}s which are direct or indirect children of the 
     * given nodePo.
     * @param nodePo an {@link INodePO} which children are to get. 
     * @param resultList the result-List for the {@link ICapPO}s
     */
    public static void getCaps(INodePO nodePo, List<ICapPO> resultList) {
        if (nodePo instanceof ICapPO) {
            resultList.add((ICapPO)nodePo);
        } else {
            for (Iterator nodeListIterator = nodePo.getNodeListIterator(); 
                nodeListIterator.hasNext();) {
                
                final INodePO currNodePo = (INodePO)nodeListIterator.next();
                getCaps(currNodePo, resultList);
            }
        }
    }
    
}
