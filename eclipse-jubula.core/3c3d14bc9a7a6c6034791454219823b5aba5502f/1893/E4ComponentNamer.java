/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.rcp.e4.namer;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;

/**
 * Interface for naming the components of an e4 application model.
 * Known implementing classes:
 *    {@link org.eclipse.jubula.rc.rcp.e4.swt.namer.E4SwtComponentNamer}
 * @see org.eclipse.jubula.rc.rcp.e4.starter.AbstractEventBrokerListener#getE4ComponentNamer()
 * @see org.eclipse.jubula.rc.rcp.e4.swt.starter.EventBrokerListener#getE4ComponentNamer()
 */
public interface E4ComponentNamer {

    /**
     * This abstract method is called, when a new tool bar is created in the
     * application model. Implement this method to react on this event.
     * @param mToolBar The created model tool bar.
     */
    public abstract void onModelToolBarCreated(MToolBar mToolBar);

    /**
     * This abstract method is called, when a new tool item is created in the
     * application model. Implement this method to react on this event.
     * @param mToolItem The created model tool item.
     */
    public abstract void onModelToolItemCreated(MToolItem mToolItem);

    /**
     * This abstract method is called, when a new part stack is created in the
     * application model. Implement this method to react on this event.
     * @param mPartStack The created model part stack.
     */
    public abstract void onModelPartStackCreated(MPartStack mPartStack);

    /**
     * This abstract method is called, when a new window is created in the
     * application model. Implement this method to react on this event.
     * @param mPart The created model part.
     */
    public abstract void onModelPartCreated(MPart mPart);

}
