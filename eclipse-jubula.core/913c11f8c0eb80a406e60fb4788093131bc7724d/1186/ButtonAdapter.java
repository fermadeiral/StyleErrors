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
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IButtonComponent;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.widgets.Button;
/**
 * Implements the Button interface for adapting a <code>SWT.Button</code>
 * 
 *  @author BREDEX GmbH
 */
public class ButtonAdapter extends ControlAdapter implements IButtonComponent {
    
    /** the Button from the AUT */
    private Button m_button;
    
    /**
     * 
     * @param objectToAdapt graphics component which will be adapted
     */
    public ButtonAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_button = (Button) objectToAdapt;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return getEventThreadQueuer().invokeAndWait(
                "getText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        return CAPUtil.getWidgetText(m_button,
                                SwtUtils.removeMnemonics(m_button.getText()));
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected() {
        return getEventThreadQueuer().invokeAndWait(
                "isSelected", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return m_button.getSelection();
                    }
                });
    }
    
    /** {@inheritDoc} */
    public String readValue(String variable) {        
        return getText();
    }
}