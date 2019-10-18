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
package org.eclipse.jubula.client.cmd;

import org.apache.commons.collections.MapUtils;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jubula.client.core.progress.ProgressConsoleRegistry;

/**
 * @author Markus Tiede
 * @created Jul 11, 2011
 */
public abstract class AbstractLauncher implements IApplication {
    /** {@inheritDoc} */
    public Object start(IApplicationContext context) throws Exception {
        AbstractCmdlineClient cmdC = getAbstractCmdLineClient();
        ProgressConsoleRegistry.INSTANCE.register(cmdC);
        return cmdC.run(
                (String[])MapUtils.getObject(context.getArguments(),
                        IApplicationContext.APPLICATION_ARGS, new String[0]));
    }

    /**
     * @return an instance of an command line client application
     */
    protected abstract AbstractCmdlineClient getAbstractCmdLineClient();

    /** {@inheritDoc} */
    public void stop() {
        ProgressConsoleRegistry.INSTANCE.deregister();
    }
}