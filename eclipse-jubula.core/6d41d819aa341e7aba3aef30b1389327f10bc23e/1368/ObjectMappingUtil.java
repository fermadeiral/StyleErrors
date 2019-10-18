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
package org.eclipse.jubula.client.core.utils;

import java.util.ArrayList;

import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * @author BREDEX GmbH
 * @created 17.04.2015
 */
public final class ObjectMappingUtil {
    /**
     * private constructor
     */
    private ObjectMappingUtil() {
        // nothing
    }
    
    /**
     * creates a component identifier from an object mapping association
     * @param asso the association
     * @return the component identifier
     */
    public static IComponentIdentifier createCompIdentifierFromAssoziation(
            IObjectMappingAssoziationPO asso) {
        IComponentIdentifier compId = null;
        IComponentIdentifier assoCompId = asso.getTechnicalName();
        if (assoCompId != null) {
            compId = new ComponentIdentifier();
            compId.setComponentClassName(
                    assoCompId.getComponentClassName());
            compId.setHierarchyNames(new ArrayList<String>(
                    assoCompId.getHierarchyNames()));
            compId.setNeighbours(new ArrayList<String>(
                    assoCompId.getNeighbours()));
            compId.setSupportedClassName(
                    assoCompId.getSupportedClassName());
            compId.setProfile(assoCompId.getProfile());
        }
        return compId;
    }
}
