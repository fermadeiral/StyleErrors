/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.tester.util;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.adaptable.ITextRendererAdapter;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swing.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swing.driver.RobotFactoryConfig;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Util class for Swing specific aspects.
 * 
 * @author BREDEX GmbH
 */
public class TesterUtil {
    
    /**
     * <code>RENDERER_FALLBACK_TEXT_GETTER_METHOD_1</code>
     */
    public static final String RENDERER_FALLBACK_TEXT_GETTER_METHOD_1 = "getTestableText"; //$NON-NLS-1$

    /**
     * <code>RENDERER_FALLBACK_TEXT_GETTER_METHOD_2</code>
     */
    public static final String RENDERER_FALLBACK_TEXT_GETTER_METHOD_2 = "getText"; //$NON-NLS-1$
  
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            TesterUtil.class);
    
    /** the high lighter for object mapping */
    private static final HighLighter HIGHLIGHTER = new HighLighter();
    
    /**
     * Is true, if a popup menu is shown
     */
    public static class PopupShownCondition implements
        EventListener.Condition {

    /**
     * the popup menu
     */
        private JPopupMenu m_popup = null;
        
    /**
     *
     * @return the popup menu
     */
        public JPopupMenu getPopup() {
            return m_popup;
        }

    /**
     * {@inheritDoc}
     * @param event event
     * @return result of the condition
     */
        public boolean isTrue(AWTEvent event) {
            if (event.getID() != ContainerEvent.COMPONENT_ADDED) {
                return false;
            }
            ContainerEvent ce = (ContainerEvent)event;
            if (ce.getChild() instanceof JPopupMenu) {
                m_popup = (JPopupMenu)ce.getChild();
                return true;
            } else if (ce.getChild() instanceof Container) {
                Container popupContainer = (Container)ce.getChild();
                final int length = popupContainer.getComponents().length;
                for (int i = 0; i < length; i++) {
                    if (popupContainer.getComponents()[i]
                                                       instanceof JPopupMenu) {
        
                        m_popup = (JPopupMenu)popupContainer.getComponents()[i];
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    /**
     * The robot factory.
     */
    private static IRobotFactory robotFactory;

    /**
     * 
     */
    private TesterUtil() { }
    
    /**
     * 
     * @return the Robot
     */
    public static IRobot getRobot() {
        return AUTServer.getInstance().getRobot();
    }

    /**
     * Gets the Robot factory. The factory is created once per instance.
     *
     * @return The Robot factory.
     */
    protected static IRobotFactory getRobotFactory() {
        if (robotFactory == null) {
            robotFactory = new RobotFactoryConfig().getRobotFactory();
        }
        return robotFactory;
    }
    /**
     * @return The event thread queuer.
     */
    protected static IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    
    /**
     * Presses or releases the given modifier.
     * @param modifier the modifier.
     * @param press if true, the modifier will be pressed.
     * if false, the modifier will be released.
     */
    public static void pressOrReleaseModifiers(String modifier, boolean press) {
        final IRobot robot = getRobot();
        final StringTokenizer modTok = new StringTokenizer(
                KeyStrokeUtil.getModifierString(modifier), " "); //$NON-NLS-1$
        while (modTok.hasMoreTokens()) {
            final String mod = modTok.nextToken();
            final int keyCode = KeyCodeConverter.getKeyCode(mod);
            if (press) {
                robot.keyPress(null, keyCode);
            } else {
                robot.keyRelease(null, keyCode);
            }
        }
    }
     
    /**
     * Casts the passed renderer component to a known type and extracts the
     * rendered text.
     *
     * @param renderer
     *            The renderer.
     * @param queueInEventThread
     *            If <code>true</code>, the text extraction is executed in
     *            the event queue thread.
     * @return The rendered text.
     * @throws StepExecutionException
     *             If the passed renderer is not supported. Supported types are
     *             <code>JLabel</code>, <code>JToggleButton</code>,
     *             <code>AbstractButton</code> and <code>JTextComponent</code>
     *
     */
    public static String getRenderedText(final Component renderer,
        boolean queueInEventThread) throws StepExecutionException {

        if (queueInEventThread) {
            return getEventThreadQueuer().invokeAndWait(
                    "getRenderedText", new IRunnable<String>() { //$NON-NLS-1$
                        public String run() {
                            return getRenderedText(renderer);
                        }
                    });
        }

        return getRenderedText(renderer);
    }
    
    /**
     * @param renderer
     *            The component which is used as the renderer
     * @return The string that the renderer displays.
     * @throws StepExecutionException
     *             If the renderer component is not of type <code>JLabel</code>,
     *             <code>JToggleButton</code>, <code>AbstractButton</code>,
     *             <code>JTextComponent</code> or supports one of the fallback
     *             methods
     */
    public static String getRenderedText(Component renderer)
        throws StepExecutionException {
        String renderedText = resolveRenderedText(renderer);
        if (renderedText != null) {
            return renderedText;
        }
        return StringConstants.EMPTY;
    }
    
    /**
     * @param renderer
     *            The component which is used as the renderer
     * @return The string that the renderer displays or <code>null</code> if it
     *         could not be resolved.
     */
    private static String resolveRenderedText(Component renderer) 
        throws StepExecutionException {
        if (renderer instanceof JLabel) {
            return ((JLabel)renderer).getText();
        } else if (renderer instanceof JToggleButton) {
            return ((JToggleButton)renderer).isSelected() ? Boolean.TRUE
                .toString() : Boolean.FALSE.toString();
        } else if (renderer instanceof AbstractButton) {
            return ((AbstractButton)renderer).getText();
        } else if (renderer instanceof JTextComponent) {
            return ((JTextComponent)renderer).getText();
        } 
        // Check if an adapter exists
        ITextRendererAdapter textRendererAdapter = 
            ((ITextRendererAdapter) AdapterFactoryRegistry
                .getInstance().getAdapter(
                        ITextRendererAdapter.class, renderer));
        if (textRendererAdapter != null) {
            return textRendererAdapter.getText();
        } else if (renderer != null) {
            String[] methodNames = new String[] {
                RENDERER_FALLBACK_TEXT_GETTER_METHOD_1,
                RENDERER_FALLBACK_TEXT_GETTER_METHOD_2 };
            for (int i = 0; i < methodNames.length; i++) {
                String text;
                try {
                    text = getTextFromComponent(
                        renderer, methodNames[i]);
                    return text;
                } catch (SecurityException e) {
                    // ignore - continue with next fall back approach
                } catch (IllegalArgumentException e) {
                    // ignore - continue with next fall back approach
                } catch (NoSuchMethodException e) {
                    // ignore - continue with next fall back approach
                } catch (IllegalAccessException e) {
                    // ignore - continue with next fall back approach
                } catch (InvocationTargetException e) {
                    // ignore - continue with next fall back approach
                }
            }
        }
        log.warn("Renderer not supported: " + renderer.getClass());  //$NON-NLS-1$
        throw new StepExecutionException(
            "Renderer not supported: " + renderer.getClass(), //$NON-NLS-1$
            EventFactory.createActionError(
                    TestErrorEvent.RENDERER_NOT_SUPPORTED));
    }
    
    /**
     * @param obj
     *            the object to invoke the method for
     * @param getterName
     *            the name of the getter Method for string retrieval
     * @return the return value of the given method name
     * @throws NoSuchMethodException
     *             may arise during reflection
     * @throws SecurityException
     *             may arise during reflection
     * @throws InvocationTargetException
     *             may arise during reflection
     * @throws IllegalAccessException
     *             may arise during reflection
     * @throws IllegalArgumentException
     *             may arise during reflection
     */
    private static String getTextFromComponent(Object obj, String getterName)
        throws SecurityException, NoSuchMethodException,
        IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
        Method getter = null;
        Class objClass = obj.getClass();
        try {
            getter = objClass.getDeclaredMethod(getterName, null);
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (SecurityException e) {
            // ignore
        }
        if (getter == null) {
            getter = objClass.getMethod(getterName, null);
        }
        getter.setAccessible(true);
        if (String.class == getter.getReturnType()) {
            return (String) getter.invoke(obj, null);
        }
        throw new NoSuchMethodException();
    }
    
    /**
     * High light the given component, called during object mapping
     * @param component the component to high light
     * @param border the color we want to highlight with
     */
    public static void highLight(Component component, Color border) {
        try {
            final Component comp = component;
            final Color col = border;
            getEventThreadQueuer().invokeLater(
                    "highLight", new Runnable() { //$NON-NLS-1$
                        public void run() {
                            HIGHLIGHTER.highLight(comp, col);
                        }
                    });
        } catch (StepExecutionException bsee) {
            log.error(bsee);
        }
    }

    /**
     * Low light the given component, called during object mapping
     * @param component the component to remove the 'highlight'
     */
    public static void lowLight(Component component) {
        try {
            final Component comp = component;
            getEventThreadQueuer().invokeLater(
                    "lowLight", new Runnable() { //$NON-NLS-1$
                        public void run() {
                            HIGHLIGHTER.lowLight(comp);
                        }
                    });
        } catch (StepExecutionException bsee) {
            log.error(bsee);
        }
    }
}