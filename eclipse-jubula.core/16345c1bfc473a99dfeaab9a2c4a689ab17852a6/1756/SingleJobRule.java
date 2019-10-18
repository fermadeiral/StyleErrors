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
package org.eclipse.jubula.client.core.rules;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * This is a rule which is implementing a semaphore. Only allowing one job to
 * run with the same {@link ISchedulingRule} object
 * 
 * @author BREDEX GmbH
 *
 */
public class SingleJobRule implements ISchedulingRule {

    /** the rule for the ui completeness Job */
    public static final ISchedulingRule COMPLETENESSRULE = new SingleJobRule();

    /** the rule for the teststyle Job */
    public static final ISchedulingRule TESTSTYLERULE = new SingleJobRule();

    /**
     * {@inheritDoc}
     */
    public boolean isConflicting(ISchedulingRule rule) {
        return rule == this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(ISchedulingRule rule) {
        return rule == this;
    }

}
