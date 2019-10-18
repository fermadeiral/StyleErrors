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
package org.eclipse.jubula.rc.swt.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.communication.internal.message.ObjectMappedMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.util.PropertyUtil;
import org.eclipse.jubula.rc.common.util.WorkaroundUtil;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The SWTEventListener for mode OBJECT_MAPPING. <br>
 * 
 * This listener listens to mouse- an key events. 
 *  The component is marked by calling the methods
 * highLight() and lowLight() respectively of the corresponding implementation
 * class. <br>
 * 
 * The key events are tapped for selecting the <code>m_currentComponent</code>
 * to be used for the object mapping. The method <code>accept(KeyEvent)</code>
 * from the <code>MappingAcceptor</code> is queried to decide, whether the
 * event suits the active configuration. <br>
 * 
 * A <code>ComponentHandler</code> is used to determine the identifaction of
 * the component. See the <code>ComponentHandler</code> for details.
 * 
 * @author BREDEX GmbH
 * @created 19.04.2006
 *
 */
public class MappingListener extends AbstractAutSwtEventListener {
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(MappingListener.class);

    /**
     * {@inheritDoc}
     */
    protected Color getBorderColor() {
        return new Color(null, Constants.MAPPING_R, Constants.MAPPING_G, 
                Constants.MAPPING_B);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void handleEvent(final Event event) {
        final Display display = ((SwtAUTServer)
                AUTServer.getInstance()).getAutDisplay();
        if (display != null) {
            display.syncExec(new Runnable() {
                public void run() {
                    if (event.equals(getLastEvent())) {
                        return;
                    }
                    setLastEvent(event);
                    switch (event.type) {
                        case SWT.MouseMove:
                        case SWT.MouseEnter:
                        case SWT.MouseDown:
                        case SWT.Arm:
                            setCurrentWidget();
                            if (!WorkaroundUtil.isHighlightingDisabled()) {
                                highlightComponent();
                            }
                            break;
                        case SWT.KeyDown:
                        case SWT.MouseUp:
                            handleKeyEvent(event);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    protected void handleKeyEvent(final Event event) {
        if (LOG.isInfoEnabled()) {
            LOG.info("handleKeyEvent: event = " + event.type); //$NON-NLS-1$                     
        }
        // is a component selected? AND the correct keys pressed?
        final Widget currComp = getCurrentComponent();
        if (currComp != null 
            && getAcceptor().accept(event) == KeyAcceptor.MAPPING_KEY_COMB) {
            try {
                Shell toolshell = getTooltipShell();
                List<IComponentIdentifier> list = 
                        new ArrayList<IComponentIdentifier>();
                if (toolshell != null) {
                    for (Control control : toolshell.getChildren()) {
                        try {
                            list.add(ComponentHandler.getIdentifier(control));
                        } catch (Exception e) {
                            // does not really interest in this point
                            LOG.info("no identifier for: " + control); //$NON-NLS-1$
                        }
                    }
                }
                
                IComponentIdentifier id = ComponentHandler.getIdentifier(
                    currComp);
                list.add(id);
                Map<String, String> componentProperties = PropertyUtil
                        .getMapOfComponentProperties(currComp);
                if (LOG.isInfoEnabled()) {
                    LOG.info("send a message with identifier " //$NON-NLS-1$
                            + "for the component '" + id //$NON-NLS-1$ 
                            + "'"); //$NON-NLS-1$
                }
                id.setComponentPropertiesMap(componentProperties);
                // send a message with the identifier of the selected component
                ObjectMappedMessage message = new ObjectMappedMessage();
                message.setComponentIdentifiers(
                        list.toArray(new IComponentIdentifier[list.size()]));
                AUTServer.getInstance().getCommunicator().send(message);
            } catch (NoIdentifierForComponentException nifce) { 
                
                // no identifier for the component, LOG this as an error
                LOG.error("no identifier for '" + currComp); //$NON-NLS-1$
            } catch (CommunicationException ce) {
                LOG.error(ce.getLocalizedMessage(), ce);
                // do nothing here: a closed connection 
                // is handled by the AUTServer
            }
        }
    }

    /**
     * This is getting the Tooltip {@link Shell} if it exists. This is not
     * defintive the Tooltip Shell. We are only looking for a Shell with a
     * specific Style. It might be that there is also another Shell with these
     * Styles.
     * 
     * @return probably the tooltip shell
     */
    private Shell getTooltipShell() {
        Shell toolshell = null;
        for (Shell shell : Display.getCurrent().getShells()) {
            if ((shell.getStyle() & (SWT.ON_TOP | SWT.TOOL | SWT.NO_FOCUS))
                    == (SWT.ON_TOP | SWT.TOOL | SWT.NO_FOCUS)) {
                toolshell = shell;
            }
        }
        return toolshell;
    }
}