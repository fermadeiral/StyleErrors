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
package org.eclipse.jubula.client.core.progress;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

/**
 * @author BREDEX GmbH
 * @created Mar 31, 2011
 */
public abstract class JobChangeListener implements IJobChangeListener {

    /**
     * {@inheritDoc}
     */
    public void aboutToRun(IJobChangeEvent event) {
        // empty stub
    }

    /**
     * {@inheritDoc}
     */
    public void awake(IJobChangeEvent event) {
        // empty stub
    }

    /**
     * {@inheritDoc}
     */
    public void done(IJobChangeEvent event) {
        // empty stub
    }

    /**
     * {@inheritDoc}
     */
    public void running(IJobChangeEvent event) {
        // empty stub
    }

    /**
     * {@inheritDoc}
     */
    public void scheduled(IJobChangeEvent event) {
        // empty stub
    }

    /**
     * {@inheritDoc}
     */
    public void sleeping(IJobChangeEvent event) {
        // empty stub
    }
}
