/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * Information about a toolkit and its components
 * 
 * @author BREDEX GmbH
 * @created 15.10.2014
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ToolkitInfo {
    
    /**
     * Allows adding of a tester class for a component class into a toolkit
     * @param componentClassName fully qualified name of the component class
     * @param testerClassName fully qualified name of the tester class
     * @return previously registered tester class for the component class
     *         or <code>null</code> if there was none
     * @since 4.0
     */
    @Nullable public String registerTesterClass(
            @NonNull String componentClassName,
            @NonNull String testerClassName);
    
    /**
     * Allows removing of a tester class for a component class from a toolkit
     * @param componentClassName fully qualified name of the component class
     * @return previously registered tester class for the component class
     *         or <code>null</code> if there was none
     * @since 4.0
     */
    @Nullable public String deregisterTesterClass(
            @NonNull String componentClassName);
    
    /**
     * Allows combining of toolkit information
     * 
     * @param otherToolkit
     *            the other toolkit to merge into this
     * @since 4.0
     */
    public void merge(@NonNull ToolkitInfo otherToolkit);
}