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
package org.eclipse.jubula.rc.rcp.e4.starter;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.jubula.rc.rcp.e4.namer.E4ComponentNamer;
import org.eclipse.jubula.tools.internal.constants.AUTServerExitConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**us
 * This is an abstract e4 processor for the extension point
 * <code>org.eclipse.e4.workbench.model</code>, the containing
 * tag named processor and the attribute class for the implementing processor.
 * An implementation only needs to implement the method {@link #getEventBrokerListener()},
 * which returns an {@link AbstractEventBrokerListener}.
 */
public abstract class AbstractProcessor implements EventHandler {
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractProcessor.class);
    
    /**
     * Called by processor mechanism via extension point to register at
     * the event broker listening on changes in the application model.
     * It subscribes to event {@link TOPIC_WIDGET} for retrieving added
     * or removed elements of the application model.
     * Known implementation: {@link org.eclipse.jubula.rc.rcp.e4.swt.SwtProcessor}
     * @param eventBroker The event Broker
     */
    @Execute
    protected void hookListener(final IEventBroker eventBroker) {
        if (!eventBroker.subscribe(UIEvents.UIElement.TOPIC_WIDGET, this)) {
            LOG.error("Could not subscribe to event broker TOPIC_WIDGET!"); //$NON-NLS-1$
            System.exit(AUTServerExitConstants.AUT_START_ERROR);
        }
    }

    /**
     * This method listens directly on events from the event broker
     * channel {@link UIEvents.UIElement#TOPIC_WIDGET}.
     * An implementing class must only overwrite the methods
     * {@link #getE4ComponentNamer()} and
     * {@link #onModelWindowCreated(MWindow)}
     * to react on new created elements in the application model.
     * @param event The event containing information of the changed
     *              element in the application model.
     */
    public void handleEvent(final Event event) {
        final MUIElement changedModelElement = (MUIElement) event
                .getProperty(EventTags.ELEMENT);
        if (changedModelElement instanceof MWindow) {
            MWindow mWindow = (MWindow) changedModelElement;
            if (mWindow.getWidget() != null) {
                onModelWindowCreated(mWindow);
            }
        } else if (changedModelElement.getWidget() != null) {
            if (changedModelElement instanceof MPartStack) {
                getE4ComponentNamer()
                    .onModelPartStackCreated((MPartStack) changedModelElement);
            } else if (changedModelElement instanceof MToolBar) {
                getE4ComponentNamer()
                    .onModelToolBarCreated((MToolBar) changedModelElement);
            } else if (changedModelElement instanceof MToolItem) {
                getE4ComponentNamer()
                    .onModelToolItemCreated((MToolItem) changedModelElement);
            } else if (changedModelElement instanceof MPart) {
                getE4ComponentNamer()
                    .onModelPartCreated((MPart) changedModelElement);
            }
        }
    }

    /**
     * @return The implementation of an e4 component namer
     *         of a specific GUI toolkit, which names the
     *         components.
     */
    protected abstract E4ComponentNamer getE4ComponentNamer();

    /**
     * This abstract method is called, when a new window is created in the
     * application model. Implement this method to react on this event.
     * @param window The created model window.
     */
    protected abstract void onModelWindowCreated(MWindow window);

}
