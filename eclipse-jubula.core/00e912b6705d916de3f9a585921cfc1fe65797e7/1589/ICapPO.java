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
package org.eclipse.jubula.client.core.model;

import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;

/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface ICapPO extends IParamNodePO, IComponentNameReuser {

    /**
     * @return Returns the GUID of the ComponentNamePO of the component
     */
    public abstract String getComponentName();

    /**
     * @param guid to set the GUID of the ComponentNamePO of the component
     */
    public abstract void setComponentName(String guid);

    /**
     * @return Returns the type.
     */
    public abstract String getComponentType();

    /**
     * @param type
     *            The type to set.
     *  
     */
    public abstract void setComponentType(String type);

    /**
     * @return Returns the metaAction.
     */
    public abstract Action getMetaAction();

    /**
     * 
     * @return Returns the metaComponent.
     */
    public abstract Component getMetaComponentType();

    /**
     * @return action name
     */
    public abstract String getActionName();

    /**
     * @param actionName
     *            The actionName to set.
     */
    public abstract void setActionName(String actionName);

    /**
     * not to use for CAPs
     * 
     * {@inheritDoc}
     */
    public abstract void addNode(INodePO childNode);
}