/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.checks.contexts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;



/**
 * @author marcell
 * @created Nov 16, 2010
 */
public class ProjectContext extends BaseContext {

    /**
     * @param cls
     */
    public ProjectContext() {
        super(IProjectPO.class);
    }

    /**
     * @return All elements
     */
    public List<Object> getAll() {
        return new ArrayList<Object>() {
            /** */
            private static final long serialVersionUID = 1L;

            { 
                add(GeneralStorage.getInstance().getProject()); 
            }
        };
    }

    @Override
    public String getName() {
        return Messages.ContextProjectName;
    }

    @Override
    public String getDescription() {
        return Messages.ContextProjectDescription;
    }

}
