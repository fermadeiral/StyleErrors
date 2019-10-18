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
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;



/**
 * @author marcell
 * @created Oct 14, 2010
 */
public abstract class Quickfix extends WorkbenchMarkerResolution {
    
    /** */
    private static IMarker source = null;
    
    @Override
    public IMarker[] findOtherMarkers(IMarker[] markers) {
        return MarkerHandler.getInstance().findOtherMarker(markers, source);
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return null;
    }
    
    /**
     * @return the source of this quickfix
     */
    public IMarker getSource() {
        return source;
    }
    
    /**
     * @param src  
     */
    public void setSource(IMarker src) {
        source = src;
    }
    
    /**
     * @param marker marker which caused the quickfix
     * @return object or null, if no object is specified with this marker
     */
    public Object getObject(IMarker marker) {
        if (MarkerHandler.getInstance().getProblemFromMarker(marker) == null) {
            return null;
        }
        return MarkerHandler.getInstance().getProblemFromMarker(marker).getPO();
    }

}
