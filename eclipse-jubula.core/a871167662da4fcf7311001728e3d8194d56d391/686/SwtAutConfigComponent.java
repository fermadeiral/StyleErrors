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
package org.eclipse.jubula.client.ui.rcp.widgets.autconfig;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.rcp.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created 13.02.2006
 * 
 */
public class SwtAutConfigComponent extends JavaAutConfigComponent {
    /**
     * The Combo to choose the keyboard layout.
     */
    private ComboViewer m_keyboardLayoutCombo;
    
    /**
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param autConfig data to be displayed/edited
     * @param autName the name of the AUT that will be using this configuration.
     */
    public SwtAutConfigComponent(Composite parent, int style,
        Map<String, String> autConfig, String autName) {
        
        super(parent, style, autConfig, autName);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void createAdvancedArea(Composite advancedAreaComposite) {
        super.createAdvancedArea(advancedAreaComposite);


        UIComponentHelper.createLabel(advancedAreaComposite,
                "SwtAutConfigComponent.KEYBOARD_LAYOUT"); //$NON-NLS-1$

        m_keyboardLayoutCombo = 
            new ComboViewer(advancedAreaComposite, SWT.READ_ONLY);
        m_keyboardLayoutCombo.setContentProvider(new ArrayContentProvider());
        m_keyboardLayoutCombo.setLabelProvider(
                new KeyboardLayoutLabelProvider());
        m_keyboardLayoutCombo.setComparator(new ViewerComparator());
        m_keyboardLayoutCombo.setInput(
                Languages.getInstance().getKeyboardLayouts());
        
        Combo keyboardLayoutCombo = m_keyboardLayoutCombo.getCombo();
        GridData comboGrid = new GridData(GridData.FILL, GridData.CENTER, 
            true, false, 2, 1);
        LayoutUtil.addToolTipAndMaxWidth(comboGrid, keyboardLayoutCombo);
        keyboardLayoutCombo.setLayoutData(comboGrid);
        ((GridData)keyboardLayoutCombo.getLayoutData()).widthHint = 
            COMPOSITE_WIDTH;
        
        // if new aut config, use defaults.
        String keyboardLayout = getConfigValue(
                AutConfigConstants.KEYBOARD_LAYOUT);
        if (StringUtils.isEmpty(keyboardLayout)) {
            m_keyboardLayoutCombo.setSelection(new StructuredSelection(
                    ObjectUtils.toString(Locale.getDefault())));
        } else {
            m_keyboardLayoutCombo.setSelection(
                    new StructuredSelection(keyboardLayout));
        }

        m_keyboardLayoutCombo.addSelectionChangedListener(
                new KeyboardLayoutComboListener());
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void checkAll(java.util.List<DialogStatusParameter> paramList) {
        super.checkAll(paramList);
        addError(paramList, modifyKeyboardLayout());
    }
    
    /**
     * 
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyKeyboardLayout() {
        
        final String layout = ObjectUtils.toString(
            ((StructuredSelection)m_keyboardLayoutCombo.getSelection())
                .getFirstElement());
        if (StringUtils.isNotEmpty(layout)) {
            putConfigValue(AutConfigConstants.KEYBOARD_LAYOUT, 
                    layout.toString());
        }
        
        return null;
    }
    
    /**
     * @author BREDEX GmbH
     * @created 25.07.2007
     */
    protected class KeyboardLayoutComboListener 
            implements ISelectionChangedListener {

        /**
         * 
         * {@inheritDoc}
         */
        public void selectionChanged(SelectionChangedEvent event) {
            checkAll();
        }
    }
    
    /**
     * Represents elements as the display name of a Locale, if possible.
     * Otherwise, delegates to the default label provider.
     * 
     * @author BREDEX GmbH
     * @created 03.08.2011
     */
    private static class KeyboardLayoutLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof Locale) {
                return ((Locale)element).getDisplayName();
            }

            try {
                Locale locale = 
                    LocaleUtils.toLocale(ObjectUtils.toString(element));
                if (locale != null) {
                    return getText(locale);
                }
            } catch (IllegalArgumentException iae) {
                // element does not represent a Locale
                // fall through to return a normal label
            }
            
            return super.getText(element);
        }
    }
}