/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.tester;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.tester.JavaReflectionTester;
import org.eclipse.jubula.rc.swing.driver.EventThreadQueuerAwtImpl;
/**
 * Implementation for the tester class, currently it is only necessary to
 * implement the getEventThreadQueuer method. Because, all other methods are
 * toolkit independent
 * 
 * @author BREDEX GmbH
 * @created 3.11.2015
 */
public class SwingReflectionTester extends JavaReflectionTester {

    @Override
    protected IEventThreadQueuer getEventThreadQueuer() {
        return new EventThreadQueuerAwtImpl();
    }

}
