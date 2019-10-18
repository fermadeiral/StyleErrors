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
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITabbedComponent;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
/**
 * Implementation of the Interface <code>ITabPane</code> as a
 * adapter for the <code>TabFolder</code> component.
 * @author BREDEX GmbH
 *
 */
public class TabFolderAdapter extends ControlAdapter
    implements ITabbedComponent {

    /** the tabFolder from the AUT */
    private TabFolder m_tabFolder;
    
    /**
     * 
     * @param objectToAdapt the component from the AUT
     */
    public TabFolderAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_tabFolder = (TabFolder) objectToAdapt;
    }

    /**
     * {@inheritDoc}
     */
    public int getTabCount() {
        return getEventThreadQueuer().invokeAndWait("getTabCount", //$NON-NLS-1$
                new IRunnable<Integer>() {
                    public Integer run() throws StepExecutionException {
                        return m_tabFolder.getItemCount();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String getTitleofTab(final int index) {
        return getEventThreadQueuer().invokeAndWait("getTitleofTab", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() throws StepExecutionException {
                        final TabItem item = m_tabFolder.getItem(index);
                        return CAPUtil.getWidgetText(item,
                                SwtUtils.removeMnemonics(item.getText()));
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public Object getBoundsAt(final int index) {
        return getEventThreadQueuer().invokeAndWait("getBoundsAt", //$NON-NLS-1$
                new IRunnable<Rectangle>() {
                    public Rectangle run() throws StepExecutionException {
                        return SwtUtils.getRelativeWidgetBounds(
                                m_tabFolder.getItem(index), m_tabFolder);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabledAt(final int index) {
        return getEventThreadQueuer().invokeAndWait("isEnabledAt", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() throws StepExecutionException {
                        return m_tabFolder.getItem(index).getControl()
                                .isEnabled();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        return getEventThreadQueuer().invokeAndWait("getSelectedIndex", //$NON-NLS-1$
                new IRunnable<Integer>() {
                    public Integer run() throws StepExecutionException {
                        return m_tabFolder.getSelectionIndex();
                    }
                });
    }
}
