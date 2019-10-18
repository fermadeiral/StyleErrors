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
package org.eclipse.jubula.rc.swt.driver;

import java.util.List;

import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;


/**
 * @author BREDEX GmbH
 * @created 02.04.2007
 */
public class MoveSwtEventMatcher extends DefaultSwtEventMatcher {

    /**
     * @param eventId The SWT event type.
     */
    public MoveSwtEventMatcher(int eventId) {
        super(eventId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects, Object comp) {
        // unfortunately we get no events from the MenuBar, 
        // so we have nothing to check!
        if (comp instanceof MenuItem) {
            return true;
        // Similar situation with ToolItem. We receive no MouseUp event from
        // a mouse click on the ToolItem's chevron.
        } else if (comp instanceof ToolItem) {
            return true;
        } else if (comp instanceof Combo) {
            // FIXME zeb Must be some way to check if a menu open/close event occurred
            return true;
        // We receive no Mouse events on Table and Tree(Table) headers.
        // This will supposedly be fixed for SWT 3.4. 
        // See http://eclip.se/17871
        } else if ((comp instanceof Table
            || comp instanceof Tree)
            && SwtUtils.invokeGetWidgetAtCursorLocation() 
            == comp) {

            // Assuming that if a Table or Tree was the target component and 
            // the mouse pointer is currently within the bounds for that Table, 
            // then that is enough confirmation.
            return true;
        }
        
        return super.isFallBackEventMatching(eventObjects, comp);
    }
}
