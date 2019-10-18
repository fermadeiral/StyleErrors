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
package org.eclipse.jubula.client.ui.filter;

import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Base Patter Filter for all Jubula filter
 * 
 * @author BREDEX GmbH
 * @created Sep 3, 2010
 */
public class JBPatternFilter extends PatternFilter {
    /**
     * Constructor
     */
    public JBPatternFilter() {
        super();
        setIncludeLeadingWildcard(true);
    }
}
