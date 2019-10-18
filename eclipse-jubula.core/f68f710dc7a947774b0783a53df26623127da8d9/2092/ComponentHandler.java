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
package org.eclipse.jubula.rc.swing.listener;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.util.EventListener;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.SystemUtils;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.common.exception.ComponentNotManagedException;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swing.components.AUTSwingHierarchy;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;


/**
 * This class is responsible for handling the components of the AUT. <br>
 * This class implements the AWTEventListener interface, listening to
 * <code>WindowEvent.WINDOW_OPENED</code>. 
 *  
 * A instance of <code>AUTHierarchy</code> is notified for WindowEvents. <br>
 * 
 * The static methods for fetching an identifier for a component and getting the
 * component for an identifer delegates to this AUTHierarchy.
 * 
 * @author BREDEX GmbH
 * @created 24.08.2004
 */
public class ComponentHandler extends BaseAWTEventListener 
    implements BaseAUTListener {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        ComponentHandler.class);
    
    /** the event mask for the events this listener is interesting in */
    private static final long[] EVENT_MASK = new long[]{
        AWTEvent.WINDOW_EVENT_MASK, AWTEvent.CONTAINER_EVENT_MASK, 
        AWTEvent.COMPONENT_EVENT_MASK};

    /** 
     * Name of System Property to print thread trace output when a component
     * cannot be found. This can be used in order to track down racing 
     * conditions in the RC.
     * 
     * We do not advertise this property. It may be used internally for 
     * tracking down tricky timing issues, but there's no real other use case 
     * for it. It may be removed at any time.
     */
    private static final String PROP_TRACE_COMPONENT_NOT_FOUND =
            "org.eclipse.jubula.rc.traceComponentNotFound"; //$NON-NLS-1$
    
    /**
     * Whether to print thread trace output when a component cannot be found. 
     * This can be used in order to track down racing conditions in the RC.
     */
    private static final boolean TRACE_COMPONENT_NOT_FOUND =
            Boolean.parseBoolean(EnvironmentUtils.getProcessOrSystemProperty(
                    PROP_TRACE_COMPONENT_NOT_FOUND));
    
    /** the Container hierarchy of the AUT*/
    private static AUTSwingHierarchy autHierarchy = new AUTSwingHierarchy();
    
    /**
     * Investigates the given <code>component</code> for an identifier. It
     * must be distinct for the whole AUT. To obtain this identifier the
     * AUTHierarchy is queried. 
     * @param component the component to get an identifier for
     * @throws NoIdentifierForComponentException if an identifer could not created for <code>component</code>.
     * @return the identifier, containing the identification 
     */
    public static IComponentIdentifier getIdentifier(Component component) 
        throws NoIdentifierForComponentException {
        
        try {
            return autHierarchy.getComponentIdentifier(component);
        } catch (ComponentNotManagedException cnme) {
            log.warn(cnme);
            throw new NoIdentifierForComponentException(
                    "unable to create an identifier for '" //$NON-NLS-1$
                    + component + "'", //$NON-NLS-1$
                    MessageIDs.E_COMPONENT_ID_CREATION); 
        }
    }
    /**
     * returns an array of all componentIdentifier of (supported) components,
     * which are currently instantiated by the AUT. <br>
     * delegate to AUTHierarchy.getAllComponentId()
     * 
     * @return array with componentIdentifier, never null
     */
    public static IComponentIdentifier[] getAllComponentId() {
        return autHierarchy.getAllComponentId();
    }
    
    /**
     * Searchs the component in the AUT, which belongs to the given
     * <code>componentIdentifier</code>.
     * 
     * @param componentIdentifier
     *            the identifier of the component to search for
     * @param retry number of tries to get object
     * @param timeout
     *      timeout for retries
     * @throws ComponentNotFoundException
     *             if no component is found for the given identifier.
     * @throws IllegalArgumentException
     *             if the identifier is null or contains invalid data
     * {@inheritDoc}
     * @return the found component
     */
    public static Component findComponent(
        IComponentIdentifier componentIdentifier, boolean retry, int timeout)
        throws ComponentNotFoundException, IllegalArgumentException {

        long start = System.currentTimeMillis();

        // FIXME : waitForComponent
        try {
            return autHierarchy.findComponent(componentIdentifier);
        } catch (ComponentNotManagedException cnme) {
            if (retry) {
                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        Thread.sleep(TimingConstantsServer
                                .POLLING_DELAY_FIND_COMPONENT);
                        return autHierarchy.findComponent(componentIdentifier); 
                    } catch (InterruptedException e) {
                        // ok
                    }  catch (ComponentNotManagedException e) { // NOPMD by zeb on 10.04.07 15:25
                        // OK, we will throw a corresponding exception later
                        // if we really can't find the component
                    } catch (InvalidDataException ide) { // NOPMD by zeb on 10.04.07 15:25
                        // OK, we will throw a corresponding exception later
                        // if we really can't find the component
                    }
                }
            }
            logStacktrace();
            throw new ComponentNotFoundException(
                        cnme.getMessage(), MessageIDs.E_COMPONENT_NOT_FOUND);
        } catch (IllegalArgumentException iae) {
            log.error(iae);
            throw iae;
        } catch (InvalidDataException ide) {
            log.error(ide);
            throw new ComponentNotFoundException(
                    ide.getMessage(), MessageIDs.E_COMPONENT_NOT_FOUND);
        }
    }
    
    /**
     * Checks the component in the AUT, which belongs to the given
     * <code>componentIdentifier</code> is disappeared or nor.
     * 
     * @param componentIdentifier
     *            the identifier of the component to search for
     * @param timeout
     *      timeout for retry
     * @throws ComponentNotFoundException
     *             if no component is found for the given identifier.
     * @throws IllegalArgumentException
     *             if the identifier is null or contains invalid data
     * {@inheritDoc}
     * @return true if the component is disappeared else false
     */
    public static boolean isComponentDisappeared(
        IComponentIdentifier componentIdentifier, int timeout)
        throws ComponentNotFoundException, IllegalArgumentException {

        long start = System.currentTimeMillis();

        try {
            Component component = autHierarchy
                    .findComponent(componentIdentifier);

            while (System.currentTimeMillis() - start < timeout) {

                TimeUtil.delay(
                        TimingConstantsServer.POLLING_DELAY_FIND_COMPONENT);

                boolean isComponentDisappeared = !autHierarchy
                        .isComponentInHierarchy(component);
                if (isComponentDisappeared) {
                    return true;
                }
            }
            return false;
        } catch (ComponentNotManagedException cnme) {
            logStacktrace();
            return true;
        } catch (IllegalArgumentException iae) {
            log.error(iae);
            throw iae;
        } catch (InvalidDataException ide) {
            log.error(ide);
            throw new ComponentNotFoundException(
                    ide.getMessage(), MessageIDs.E_COMPONENT_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     */
    public long[] getEventMask() {
        long[] eventMask = EVENT_MASK; // see findBugs
        return eventMask;
    }

    // implementing method from interface AWTEventListener
    /**
     * {@inheritDoc}
     */
    public void eventDispatched(AWTEvent event) {
        final ClassLoader originalCL = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass()
            .getClassLoader());
        try {
            if (log.isDebugEnabled()) {
                log.debug(event.paramString());
            }
            final int id = event.getID();
            ComponentEvent componentEvent;
            switch (id) {
                case WindowEvent.WINDOW_ACTIVATED:
                case WindowEvent.WINDOW_OPENED:
                    // add recursivly all components to AUTHierarchy 
                    // and create names for unnamed components
                    Window window = ((WindowEvent) event).getWindow();
                    autHierarchy.add(window);
                    break;
                case ContainerEvent.COMPONENT_ADDED:
                    checkContainerListener((ContainerEvent)event);
                    break;
                case ComponentEvent.COMPONENT_HIDDEN:
                    componentEvent = (ComponentEvent)event; 
                    if (!hasListener(componentEvent.getComponent(), 
                            ComponentListener.class)) {
                        autHierarchy.componentHidden(componentEvent);
                    }
                    break;
                case ComponentEvent.COMPONENT_SHOWN:
                    componentEvent = (ComponentEvent)event; 
                    if (!hasListener(componentEvent.getComponent(), 
                            ComponentListener.class)) {
                        autHierarchy.componentShown(componentEvent);
                    }
                    break;
                default:
                    // do nothing
            }
            if (AUTServer.getInstance().getMode() 
                == ChangeAUTModeMessage.OBJECT_MAPPING) {
                
                AUTServer.getInstance().updateHighLighter();
            }
        } catch (Throwable t) {
            log.error("exception during ComponentHandler", t); //$NON-NLS-1$
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }

    /**
     * Checks if there is already a listener of an AUTHierarchy instance
     * at the container of the added component. If there is no listener and if the 
     * container is known by the AUTHierarchy, the component will be added 
     * to the AUTHierarchy via this method by calling the componentAdded method
     * of the AUTHierarchy. <br> <br>
     * <b>Note:</b> This is only a workaround, because some applications may 
     * clean the listeners of the container inclusive the listeners, so 
     * we cannot notice added components anymore. 
     * Therefor the global AWTEventListener at the Toolkit has to check this.
     * 
     * @param event the ContainerEvent.COMPONENT_ADDED
     */
    private void checkContainerListener(ContainerEvent event) {
        if (!hasListener(event.getContainer(), ContainerListener.class) 
                && (autHierarchy.getHierarchyContainer(event.getContainer()) 
                        != null)) {
            if (log.isInfoEnabled()) {
                log.info("ComponentHandler called: autHierarchy.componentAdded"); //$NON-NLS-1$
            }
            autHierarchy.componentAdded(event);
        }
    }

    /**
     * @param component the component to check for a listener.
     * @param listenerClass The class of listener for which to check.
     * @return <code>true</code> if an <code>AUTSwingHierarchy</code> is 
     *         registered as a <code>listenerClass</code> listener on the
     *         given <code>component</code>. Otherwise, <code>false</code>.
     */
    private boolean hasListener(
            Component component, 
            Class<? extends EventListener> listenerClass) {
        
        EventListener[] listener = component.getListeners(listenerClass);
        int length = listener.length;
        for (int i = 0; i < length; i++) {
            if (listener[i] instanceof AUTSwingHierarchy) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return the AUT Hierarchy
     */
    public static AUTSwingHierarchy getAutHierarchy() {
        return autHierarchy;
    }

    /**
     * Pretty prints the stack traces of all currently running threads to the 
     * log.
     */
    private static void logStacktrace() {
        if (TRACE_COMPONENT_NOT_FOUND) {
            StringBuilder builder = new StringBuilder();
            builder.append("Logging stacktrace:" + SystemUtils.LINE_SEPARATOR); //$NON-NLS-1$
            Thread currentThread = Thread.currentThread();
            Map<Thread, StackTraceElement[]> stackTraces = 
                    Thread.getAllStackTraces();
            for (Entry<Thread, StackTraceElement[]> stackTrace 
                    : stackTraces.entrySet()) {
                
                Thread thread = stackTrace.getKey();
                
                if (thread == currentThread) {
                    builder.append("[current-thread] - "); //$NON-NLS-1$
                }
                
                builder.append(thread.getName() + ":" + SystemUtils.LINE_SEPARATOR); //$NON-NLS-1$
                for (StackTraceElement e : stackTrace.getValue()) {
                    builder.append("\t" + e + SystemUtils.LINE_SEPARATOR); //$NON-NLS-1$
                }
                
            }
            
            builder.append(SystemUtils.LINE_SEPARATOR);
            log.warn(builder);
        }
    }
}
