/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.widgets.autconfig;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.rcp.businessprocess.RemoteFileBrowserBP;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * ConfigurationArea for Win toolkit projects
 */
public class WinAppsAutConfigComponent extends AutConfigComponent {

    /** text field for the app name that will launch the AUT */
    private Text m_modernUiAppName;

    /** text field for the aut_args */
    private Text m_autArgsTextField;

    /** the WidgetModifyListener */
    private WidgetModifyListener m_modifyListener;

    /**
     * @param parent
     *            {@inheritDoc}
     * @param style
     *            {@inheritDoc}
     * @param autConfig
     *            data to be displayed/edited
     * @param autName
     *            the name of the AUT that will be using this configuration.
     */
    public WinAppsAutConfigComponent(Composite parent, int style,
            Map<String, String> autConfig, String autName) {

        super(parent, style, autConfig, autName, false);
    }

    /**
     * {@inheritDoc}
     */
    protected void initState() {
        m_modernUiAppName.setEnabled(true);
        m_autArgsTextField.setEnabled(true);
        checkLocalhostServer();
        RemoteFileBrowserBP.clearCache(); // avoid all caches
    }

    /**
     * {@inheritDoc}
     */
    protected void createBasicArea(Composite basicAreaComposite) {
        super.createBasicArea(basicAreaComposite);
        Composite basicAreaModernUiApp = new Composite(basicAreaComposite,
                SWT.NONE);
        createLayout(basicAreaModernUiApp);
        final GridData gridDataArea = (GridData) basicAreaModernUiApp
                .getLayoutData();
        gridDataArea.horizontalAlignment = SWT.FILL;
        // modernUI app textfields
        UIComponentHelper.createLabel(basicAreaComposite,
                Messages.AUTConfigComponentAppName);
        m_modernUiAppName = UIComponentHelper.createTextField(
                basicAreaComposite, 2);

        LayoutUtil.setMaxChar(m_modernUiAppName,
                IPersistentObject.MAX_STRING_LENGTH);

        // AUT arguments
        ControlDecorator.createInfo(UIComponentHelper.createLabel(
                basicAreaComposite, Messages.AUTConfigComponentArguments),
                Messages.AUTConfigComponentArgumentsControlDecorator, false);
        m_autArgsTextField = UIComponentHelper.createTextField(
                basicAreaComposite, 2);
        createAutDirectoryEditor(basicAreaComposite);
    }

    /**
     * Populates GUI for the basic configuration section.
     *
     * @param data
     *            Map representing the data to use for population.
     */
    protected void populateBasicArea(Map<String, String> data) {
        super.populateBasicArea(data);
        // executable filename
        m_modernUiAppName.setText(StringUtils.defaultString(data
                .get(AutConfigConstants.EXECUTABLE)));
        // AUT arguments
        m_autArgsTextField.setText(StringUtils.defaultString(data
                .get(AutConfigConstants.AUT_ARGUMENTS)));
        // Modern UI App name
        m_modernUiAppName.setText(StringUtils.defaultString(data
                .get(AutConfigConstants.APP_NAME)));
    }

    @Override
    protected void createAdvancedArea(Composite advancedAreaComposite) {
        // unused
    }

    @Override
    protected void populateExpertArea(Map<String, String> data) {
        // unused
    }

    @Override
    protected void populateAdvancedArea(Map<String, String> data) {
        // unused
    }

    @Override
    protected boolean isJavaAut() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void installListeners() {
        super.installListeners();
        WidgetModifyListener modifyListener = getModifyListener();
        getAUTAgentHostNameCombo().addModifyListener(modifyListener);
        m_modernUiAppName.addModifyListener(modifyListener);
        m_autArgsTextField.addModifyListener(modifyListener);
    }

    /**
     * {@inheritDoc}
     */
    protected void deinstallListeners() {
        super.deinstallListeners();
        WidgetModifyListener modifyListener = getModifyListener();

        getAUTAgentHostNameCombo().removeModifyListener(modifyListener);
        m_modernUiAppName.removeModifyListener(modifyListener);
        m_autArgsTextField.removeModifyListener(modifyListener);
    }

    /**
     * @return the modifier listener.
     */
    @SuppressWarnings("synthetic-access")
    private WidgetModifyListener getModifyListener() {
        if (m_modifyListener == null) {
            m_modifyListener = new WidgetModifyListener();
        }
        return m_modifyListener;

    }

    /**
     * {@inheritDoc}
     */
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void modifyText(ModifyEvent e) {
            Object source = e.getSource();
            if (source.equals(m_modernUiAppName)
                    || source.equals(m_autArgsTextField)) {
                checkAll();
            } else if (source.equals(getAUTAgentHostNameCombo())) {
                checkLocalhostServer();
                checkAll();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void checkAll(java.util.List<DialogStatusParameter> paramList) {
        super.checkAll(paramList);
        addError(paramList, modifyAutParamFieldAction());
        addError(paramList, modifyModernUiAppName());
        addError(paramList, modifyAutParamFieldAction());
        // working dir gui is not necessary
        hideWorkingDirColumn();
    }

    /**
     * The action of the AUT parameter field.
     *
     * @return <code>null</code> if the new value is valid. Otherwise, returns a
     *         status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyAutParamFieldAction() {
        String params = m_autArgsTextField.getText();
        putConfigValue(AutConfigConstants.AUT_ARGUMENTS, params);
        return null;
    }

    /**
     * Check the Modern UI App name.
     * 
     * @return <code>null</code> if the new value is valid. Otherwise, returns a
     *         status parameter indicating the cause of the problem.
     */
    private DialogStatusParameter modifyModernUiAppName() {
        DialogStatusParameter error = null;
        boolean isAppNameEmpty = m_modernUiAppName.getText().length() == 0;
        if (!isValid(m_modernUiAppName, true) && !isAppNameEmpty) {
            error = createErrorStatus(Messages.
                    AUTConfigComponentWrongModernUiAppName);
        }
        putConfigValue(AutConfigConstants.APP_NAME,
                m_modernUiAppName.getText());
        return error;
    }

}
