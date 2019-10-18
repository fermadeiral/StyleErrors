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
package org.eclipse.jubula.rc.swing.tester.adapter;

import java.awt.Rectangle;

import javax.swing.JTabbedPane;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITabbedComponent;

/**
 * Implementation of the Interface <code>ITabPaneAdapter</code> as a adapter for
 * the <code>JTabbedPane</code> component.
 * 
 * @author BREDEX GmbH
 * 
 */
public class JTabbedPaneAdapter extends JComponentAdapter 
    implements ITabbedComponent {
    /** The JTabbedPane on which the actions are performed. */
    private JTabbedPane m_pane;
    
    /**
     * 
     * @param objectToAdapt 
     */
    public JTabbedPaneAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_pane = (JTabbedPane) objectToAdapt;
    }

    /**
     * {@inheritDoc}
     */
    public int getTabCount() {
        return getEventThreadQueuer().invokeAndWait(
                "getTabCount", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        return m_pane.getTabCount();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public String getTitleofTab(final int index) {
        return getEventThreadQueuer().invokeAndWait(
                "getTitleOfTab", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        return m_pane.getTitleAt(index);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Object getBoundsAt(final int index) {
        return getEventThreadQueuer().invokeAndWait(
                "getBoundsAt", new IRunnable<Rectangle>() { //$NON-NLS-1$
                    public Rectangle run() {
                        return m_pane.getBoundsAt(index);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabledAt(final int index) {
        return getEventThreadQueuer().invokeAndWait(
                "isEnabledAt", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return m_pane.isEnabledAt(index);
                    }
                }); 
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        return getEventThreadQueuer().invokeAndWait(
                "getSelectedIndex", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        return m_pane.getSelectedIndex();
                    }
                });
    }
}