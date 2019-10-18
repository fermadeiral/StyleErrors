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
package org.eclipse.jubula.client.teststyle.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jubula.client.teststyle.constants.Ext;
import org.eclipse.ui.views.markers.MarkerItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Property tester for MarkerItem
 * @author Markus Tiede
 * @created Jul 12, 2011
 */
public class MarkerItemPropertyTester extends PropertyTester {

    /** the id of the "isTeststyleMarker" property */
    public static final String IS_TESTSTYLE_MARKER = "isTeststyleMarkerItem"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(MarkerItemPropertyTester.class);

    /**
     * {@inheritDoc}
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        if (receiver instanceof MarkerItem) {
            MarkerItem mItem = (MarkerItem)receiver;
            if (property.equals(IS_TESTSTYLE_MARKER)) {
                boolean isTeststyleMarker = false;
                try {
                    IMarker marker = mItem.getMarker();
                    if (marker != null) {
                        isTeststyleMarker = marker.getType() == Ext.TSM_MARKER 
                            ? true : false;
                    }
                } catch (CoreException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                boolean expectedBoolean = expectedValue instanceof Boolean 
                    ? ((Boolean)expectedValue).booleanValue() : true;
                return isTeststyleMarker == expectedBoolean;
            }
        }
        return false;
    }
}
