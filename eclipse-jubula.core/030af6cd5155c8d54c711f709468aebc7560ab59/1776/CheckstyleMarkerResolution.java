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
package org.eclipse.jubula.client.teststyle.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jubula.client.teststyle.gui.MarkerHandler;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;


/**
 * @author marcell
 * @created Oct 14, 2010
 */
public class CheckstyleMarkerResolution implements IMarkerResolutionGenerator {

    /**
     * {@inheritDoc}
     */
    public IMarkerResolution[] getResolutions(IMarker marker) {
        return MarkerHandler.getInstance().getResolutions(marker);
    }
}
