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
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITabbedComponent;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
/**
 * Implementation of the Interface <code>ITabPane</code> as a
 * adapter for the <code>CTabFolder</code> component.
 * @author BREDEX GmbH
 *
 */
public class CTabFolderAdapter extends ControlAdapter 
    implements ITabbedComponent {

    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(CTabFolderAdapter.class);

    /** the CTabFolder from the AUT */
    private CTabFolder m_tabFolder;
    
    /**
     * 
     * @param objectToAdapt the component from the AUT
     */
    public CTabFolderAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_tabFolder = (CTabFolder) objectToAdapt;
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
                        final CTabItem item = m_tabFolder.getItem(index);
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
                    Control control = m_tabFolder.getItem(index).getControl();
                    if (control == null) {
                        // FIXME zeb: Strange workaround for CTabFolders,
                        // which somehow never seem to have an associated
                        // Control.
                        log.debug(this + ".getControl() returned null."); //$NON-NLS-1$
                        return Boolean.TRUE;
                    }

                    return control.isEnabled();
                
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
