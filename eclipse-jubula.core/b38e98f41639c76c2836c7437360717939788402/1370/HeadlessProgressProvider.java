/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.cmd.progess;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;

/**
 * @author BREDEX GmbH
 * @created Apr 1, 2011
 */
public class HeadlessProgressProvider extends ProgressProvider {
    /**
     * {@inheritDoc}
     */
    public IProgressMonitor createMonitor(Job job) {
        return new HeadlessProgressMonitor();
    }
}
