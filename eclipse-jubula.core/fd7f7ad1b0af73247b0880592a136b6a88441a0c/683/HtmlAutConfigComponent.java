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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IAUTConfigPO.ActivationMethod;
import org.eclipse.jubula.client.ui.rcp.businessprocess.RemoteFileBrowserBP;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.widgets.I18nEnumCombo;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.toolkit.html.Browser;
import org.eclipse.jubula.toolkit.html.BrowserSize;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created Nov 4, 2009
 */
public class HtmlAutConfigComponent extends AutConfigComponent {
    /** gui component */
    private Text m_autUrlTextField;
    /** gui field for browser */
    private Text m_browserTextField;
    /** gui field for driver */
    private Text m_driverTextField;
    /** gui field for aut id attribute text field */
    private Text m_autIdAttibuteTextField;
    /** gui button for browser path */
    private Button m_browserPathButton;
    /** gui button for driver path */
    private Button m_driverPathButton;
    /** gui checkbox for the singeWindowMode */
    private Button m_singleWindowCheckBox;
    /** gui checkbox for the using webdriver */
    private Button m_webdriverCheckBox;
    /** gui component */
    private I18nEnumCombo<Browser> m_browserCombo;
    /** gui component */
    private I18nEnumCombo<ActivationMethod> m_activationMethodCombo;
    /** gui component */
    private I18nEnumCombo<BrowserSize> m_browserSizeCombo;
    /** the WidgetModifyListener */
    private WidgetModifyListener m_modifyListener;
    /** the the WidgetSelectionListener */
    private WidgetSelectionListener m_selectionListener;


    /**
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param autConfig data to be displayed/edited
     * @param autName the name of the AUT that will be using this configuration.
     */
    public HtmlAutConfigComponent(Composite parent, int style,
        Map<String, String> autConfig, String autName) {
        
        super(parent, style, autConfig, autName, true);
    }

    /**
     * @param basicAreaComposite The composite that represents the basic area.
     */
    protected void createBasicArea(Composite basicAreaComposite) {
        super.createBasicArea(basicAreaComposite);

        // URL property
        Label urlLabel = UIComponentHelper.createLabel(
                basicAreaComposite, "WebAutConfigComponent.URL"); //$NON-NLS-1$
        urlLabel.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.urlLabel"); //$NON-NLS-1$
        
        m_autUrlTextField = UIComponentHelper.createTextField(
                basicAreaComposite, 2);
        m_autUrlTextField.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.autUrlTextField"); //$NON-NLS-1$
        
        // browser
        Label browserLabel = UIComponentHelper.createLabel(
                basicAreaComposite, "WebAutConfigComponent.browser"); //$NON-NLS-1$
        browserLabel.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.browserLabel"); //$NON-NLS-1$
        
        m_browserCombo = UIComponentHelper.createEnumCombo(
                basicAreaComposite, 2, "WebAutConfigComponent.Browser", //$NON-NLS-1$
                    Browser.class);
        m_browserCombo.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.browserCombo"); //$NON-NLS-1$
    }
    
    /**
     * {@inheritDoc}
     */
    protected void createAdvancedArea(Composite advancedAreaComposite) {
        super.createAdvancedArea(advancedAreaComposite);
        
        createBrowserAndDriverPathEditor(advancedAreaComposite);
        
        createWebdriverCheckBox(advancedAreaComposite);
        
        createSingleModeCheckBox(advancedAreaComposite);

        // browser window size
        Label browserSizeLabel = UIComponentHelper.createLabel(
                advancedAreaComposite, "WebAutConfigComponent.BrowserSize"); //$NON-NLS-1$
        browserSizeLabel.setData(SwtToolkitConstants.WIDGET_NAME,
                "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.browserSizeLabel"); //$NON-NLS-1$
        m_browserSizeCombo = UIComponentHelper.createEnumCombo(
                advancedAreaComposite, 2, "WebAutConfigComponent.BrowserSize", //$NON-NLS-1$
                BrowserSize.class);
        m_browserSizeCombo.setData(SwtToolkitConstants.WIDGET_NAME,
                "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.browserSizeCombo"); //$NON-NLS-1$
    }
    
    /**
     * Create this dialog's expert area component.
     * 
     * @param expertAreaComposite Composite representing the expert area.
     */
    protected void createExpertArea(Composite expertAreaComposite) {
        super.createExpertArea(expertAreaComposite);
        
        // activation method editor
        Label activationMethodLabel = UIComponentHelper.createLabel(
                expertAreaComposite, "AUTConfigComponent.activationMethod"); //$NON-NLS-1$
        activationMethodLabel.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.activationMethodLabel"); //$NON-NLS-1$
        
        m_activationMethodCombo = UIComponentHelper.createEnumCombo(
                expertAreaComposite, 2, "AUTConfigComponent.ActivationMethod", //$NON-NLS-1$
                    ActivationMethod.class);
        m_activationMethodCombo.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.activationMethodCombo"); //$NON-NLS-1$
        
        // AUT ID Attribute property
        Label autIdAttibuteLabel = UIComponentHelper.createLabel(
                expertAreaComposite, "HTMLAutConfigComponent.AutIdAttibuteLabel"); //$NON-NLS-1$
        autIdAttibuteLabel.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.autIdAttibuteLabel"); //$NON-NLS-1$
        
        m_autIdAttibuteTextField = UIComponentHelper.createTextField(
                expertAreaComposite, 2);
        m_autIdAttibuteTextField.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.autIdAttibuteTextField"); //$NON-NLS-1$
    }
    
    /**
     * Inits the browser path area.
     * 
     * @param parent The parent Composite.
     */
    protected void createBrowserAndDriverPathEditor(Composite parent) {
        
        Label browserPathLabel = UIComponentHelper.createLabel(parent, "WebAutConfigComponent.browserPath"); //$NON-NLS-1$ 
        browserPathLabel.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.browserPathLabel"); //$NON-NLS-1$
        ControlDecorator.createInfo(browserPathLabel,  
                I18n.getString("ControlDecorator.WebBrowserPath"), false); //$NON-NLS-1$
        
        m_browserTextField = UIComponentHelper.createTextField(
            parent, 1);
        m_browserTextField.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.BrowserTextField"); //$NON-NLS-1$
        
        m_browserPathButton = new Button(UIComponentHelper
                .createLayoutComposite(parent), SWT.PUSH);
        m_browserPathButton.setText(I18n.getString("AUTConfigComponent.browse"));  //$NON-NLS-1$
        m_browserPathButton.setLayoutData(BUTTON_LAYOUT);
        m_browserPathButton.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.browserPathButton"); //$NON-NLS-1$
        
        
        Label driverPathLabel = UIComponentHelper.createLabel(parent, "WebAutConfigComponent.driverPath"); //$NON-NLS-1$ 
        driverPathLabel.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.driverPathLabel"); //$NON-NLS-1$
        ControlDecorator.createInfo(driverPathLabel,  
                I18n.getString("ControlDecorator.WebDriverPath"), false); //$NON-NLS-1$
        
        m_driverTextField = UIComponentHelper.createTextField(
            parent, 1);
        m_driverTextField.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.DriverTextField"); //$NON-NLS-1$
        
        m_driverPathButton = new Button(UIComponentHelper
                .createLayoutComposite(parent), SWT.PUSH);
        m_driverPathButton.setText(I18n.getString("AUTConfigComponent.browse"));  //$NON-NLS-1$
        m_driverPathButton.setLayoutData(BUTTON_LAYOUT);
        m_driverPathButton.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.driverPathButton"); //$NON-NLS-1$
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void installListeners() {
        super.installListeners();
        WidgetModifyListener modifyListener = getModifyListener();
        WidgetSelectionListener selectionListener = getSelectionListener();
        
        getAUTAgentHostNameCombo().addModifyListener(modifyListener);
        m_autUrlTextField.addModifyListener(modifyListener);
        m_autIdAttibuteTextField.addModifyListener(modifyListener);
        m_browserTextField.addModifyListener(modifyListener);
        m_driverTextField.addModifyListener(modifyListener);
        m_browserPathButton.addSelectionListener(selectionListener);
        m_driverPathButton.addSelectionListener(selectionListener);
        m_browserCombo.addSelectionListener(selectionListener);
        m_activationMethodCombo.addSelectionListener(selectionListener);
        m_singleWindowCheckBox.addSelectionListener(selectionListener);
        m_webdriverCheckBox.addSelectionListener(selectionListener);
        m_browserSizeCombo.addSelectionListener(selectionListener);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void deinstallListeners() {
        super.deinstallListeners();
        WidgetModifyListener modifyListener = getModifyListener();
        WidgetSelectionListener selectionListener = getSelectionListener();
        
        getAUTAgentHostNameCombo().removeModifyListener(modifyListener);
        m_autUrlTextField.removeModifyListener(modifyListener);
        m_autIdAttibuteTextField.removeModifyListener(modifyListener);
        m_browserTextField.removeModifyListener(modifyListener);
        m_driverTextField.removeModifyListener(modifyListener);
        m_browserPathButton.removeSelectionListener(selectionListener);
        m_driverPathButton.removeSelectionListener(selectionListener);
        m_browserCombo.removeSelectionListener(selectionListener);
        m_activationMethodCombo.removeSelectionListener(selectionListener);
        m_singleWindowCheckBox.removeSelectionListener(selectionListener);
        m_webdriverCheckBox.removeSelectionListener(selectionListener);
        m_browserSizeCombo.removeSelectionListener(selectionListener);
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * 
     * @author BREDEX GmbH
     * @created 13.07.2005
     */
    private class WidgetSelectionListener implements SelectionListener {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            boolean checked = false;
            
            if (source.equals(m_activationMethodCombo)) {
                checked = true;
            } else if (source.equals(m_browserSizeCombo)) {
                checked = true;
            } else if (source.equals(m_browserCombo)) {
                handleBrowserDependentEnablement();
                checked = true;
            } else if (source.equals(m_browserPathButton)) {
                if (isRemoteRequest()) {
                    remoteBrowse(false, AutConfigConstants.BROWSER_PATH,
                            m_browserTextField,
                            I18n.getString("WebAutConfigComponent.SelectBrowserPath")); //$NON-NLS-1$
                } else {
                    FileDialog fileDialog = new FileDialog(getShell(),
                            SWT.APPLICATION_MODAL | SWT.ON_TOP);
                    //handleBrowserPathButtonEvent(fileDialog);
                    
                    fileDialog.setText(I18n.getString("WebAutConfigComponent.SelectBrowserPath")); //$NON-NLS-1$
                    String browserFile = fileDialog.open();
                    if (browserFile != null) {
                        m_browserTextField.setText(browserFile);
                    }
                }
                handleBrowserDependentEnablement();
                return;
            } else if (source.equals(m_driverPathButton)) {
                if (isRemoteRequest()) {
                    remoteBrowse(false, AutConfigConstants.DRIVER_PATH,
                            m_driverTextField,
                            I18n.getString("WebAutConfigComponent.SelectDriverPath")); //$NON-NLS-1$
                } else {
                    FileDialog fileDialog = new FileDialog(getShell(),
                            SWT.APPLICATION_MODAL | SWT.ON_TOP);
                    //handleBrowserPathButtonEvent(fileDialog);
                    
                    fileDialog.setText(I18n.getString("WebAutConfigComponent.SelectDriverPath")); //$NON-NLS-1$
                    String browserFile = fileDialog.open();
                    if (browserFile != null) {
                        m_driverTextField.setText(browserFile);
                    }
                }
                handleBrowserDependentEnablement();
                return;
            } else if (source.equals(m_singleWindowCheckBox)) {
                checked = true;
            } else if (source.equals(m_webdriverCheckBox)) {
                handleWebdriverDependentEnablement();
                checked = true;
            }
            if (checked) {
                checkAll();
                return;
            }
            Assert.notReached("Event activated by unknown widget(" + source + ")."); //$NON-NLS-1$ //$NON-NLS-2$    
        }
        
        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            // Do nothing
        }
    }
    
    /**
     * 
     */
    private void handleWebdriverDependentEnablement() {
        if (m_webdriverCheckBox.getSelection()) {
            m_browserSizeCombo.setEnabled(true);
            m_singleWindowCheckBox.setEnabled(false);
            m_driverPathButton.setEnabled(true);
            m_driverTextField.setEnabled(true);
        } else {
            m_browserSizeCombo.setEnabled(false);
            m_singleWindowCheckBox.setEnabled(true);
            m_driverPathButton.setEnabled(false);
            m_driverTextField.setEnabled(false);
        }
        handleBrowserDependentEnablement();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected boolean checkLocalhostServer() {
        boolean enable = super.checkLocalhostServer();
        boolean browseEnabled = enable || isRemoteRequest();
        m_browserPathButton.setEnabled(
                browseEnabled && m_browserTextField.getEnabled());
        return enable;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected boolean handleBrowserDependentEnablement() {
        boolean enable = super.checkLocalhostServer();
        boolean browseEnabled = enable || isRemoteRequest();
        boolean isIE = m_browserCombo.getSelectedObject().equals(
                Browser.InternetExplorer);
        Browser browser = m_browserCombo.getSelectedObject();
        m_browserPathButton.setEnabled(!isIE && browseEnabled);
        m_browserTextField.setEnabled(!isIE && browseEnabled);
        boolean isWebDriver = m_webdriverCheckBox.getSelection();
        if (isWebDriver) {
            if (browser.equals(Browser.Firefox)) {
                m_driverPathButton.setEnabled(false);
                m_driverTextField.setEnabled(false);
            } else {
                m_driverPathButton.setEnabled(browseEnabled);
                m_driverTextField.setEnabled(browseEnabled);
            }
        }
        return isIE;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void initState() {
        m_activationMethodCombo.setEnabled(true);
        m_activationMethodCombo.setEnabled(true);
        m_autUrlTextField.setEnabled(true);
        m_autIdAttibuteTextField.setEnabled(true);
        m_browserCombo.setEnabled(true);
        m_browserPathButton.setEnabled(true);
        m_browserTextField.setEnabled(true);
        m_driverPathButton.setEnabled(false);
        m_driverTextField.setEnabled(false);
        handleWebdriverDependentEnablement();
        checkLocalhostServer();
        handleBrowserDependentEnablement();
        RemoteFileBrowserBP.clearCache(); // avoid all caches
    }

    /**
     * This private inner class contains a new ModifyListener.
     * 
     * @author BREDEX GmbH
     * @created 22.11.2006
     */
    private class WidgetModifyListener implements ModifyListener {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void modifyText(ModifyEvent e) {
            Object source = e.getSource();
            boolean checked = false;
            if (source.equals(m_autUrlTextField)) {
                checked = true;
            } else if (source.equals(m_browserTextField)) {
                checked = true;
            } else if (source.equals(m_driverTextField)) {
                checked = true;
            } else if (source.equals(m_autIdAttibuteTextField)) {
                checked = true;
            } else if (source.equals(getAUTAgentHostNameCombo())) {
                checkLocalhostServer();
                checked = true;
            } else if (source.equals(m_browserCombo)) {
                handleBrowserDependentEnablement();
                checked = true;
            }
            if (checked) {
                checkAll();
                return;
            }
            Assert.notReached("Event activated by unknown widget."); //$NON-NLS-1$
        }

    }

    /** 
     * The action of the activation combo
     * @return true
     */
    boolean handleActivationComboEvent() {
        putConfigValue(AutConfigConstants.ACTIVATION_METHOD,
                ActivationMethod.getRCString(m_activationMethodCombo
                        .getSelectedObject()));
        return true;
    }

    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyUrlTextField() {
        DialogStatusParameter error = null;
        String urlText = m_autUrlTextField.getText();
        if (m_autUrlTextField.getText().length() == 0) {
            error = createErrorStatus(I18n.getString("WebAutConfigComponent.emptyUrl")); //$NON-NLS-1$
        } else {
            try {
                new URL(urlText);
            } catch (MalformedURLException e) {
                error = createErrorStatus(I18n.getString("WebAutConfigComponent.wrongUrl")); //$NON-NLS-1$
            }
        }

        putConfigValue(AutConfigConstants.AUT_URL, urlText);
        
        return error;
    }
    
    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyIDAttributeTextField() {
        DialogStatusParameter error = null;
        String idText = m_autIdAttibuteTextField.getText();
        if (!idText.matches("[a-zA-Z]*")) { //$NON-NLS-1$
            error = createErrorStatus(I18n
                    .getString("HTMLAutConfigComponent.wrongAutIdAttribute")); //$NON-NLS-1$
        } else {
            putConfigValue(AutConfigConstants.WEB_ID_TAG, idText);
        }
        return error;
    }
    
    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyBrowserPathTextField() {
        DialogStatusParameter error = null;
        String txt = m_browserTextField.getText();

        putConfigValue(AutConfigConstants.BROWSER_PATH, txt);
        
        return error;
    }
    
    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyDriverPathTextField() {
        DialogStatusParameter error = null;
        String txt = m_driverTextField.getText();

        putConfigValue(AutConfigConstants.DRIVER_PATH, txt);
        
        return error;
    }
    
    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyBrowser() {
        final Browser browser = m_browserCombo.getSelectedObject();
        if (browser != null) {
            putConfigValue(AutConfigConstants.BROWSER, browser.toString());
        }
        
        return null;
    }

    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns a
     *         status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyBrowserSize() {
        final BrowserSize browserSize = m_browserSizeCombo.getSelectedObject();
        if (browserSize == null || BrowserSize.FULLSCREEN == browserSize) {
            putConfigValue(AutConfigConstants.BROWSER_SIZE,
                    BrowserSize.FULLSCREEN.toString());
        } else {
            putConfigValue(AutConfigConstants.BROWSER_SIZE,
                    browserSize.toString());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected void populateBasicArea(Map<String, String> data) {
        super.populateBasicArea(data);
        
        String browser = data.get(AutConfigConstants.BROWSER);
        if (browser == null) {
            browser = Browser.InternetExplorer.toString();
        }
        m_browserCombo.setSelectedObject(Browser.valueOf(browser));

        if (!isDataNew(data)) {
            m_autUrlTextField.setText(
                StringUtils.defaultString(
                        data.get(AutConfigConstants.AUT_URL)));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void populateAdvancedArea(Map<String, String> data) {
        if (!isDataNew(data)) {
            m_browserTextField.setText(StringUtils.defaultString(data
                    .get(AutConfigConstants.BROWSER_PATH)));
            
            m_driverTextField.setText(StringUtils.defaultString(data
                    .get(AutConfigConstants.DRIVER_PATH)));

            String browserSize = data.get(AutConfigConstants.BROWSER_SIZE);
            if (StringUtils.isEmpty(browserSize)) {
                m_browserSizeCombo.setSelectedObject(BrowserSize.FULLSCREEN);
            } else {
                m_browserSizeCombo.setSelectedObject(BrowserSize
                        .valueOf(browserSize));
            }
            String selection = data.get(AutConfigConstants.SINGLE_WINDOW_MODE);
            boolean selected = false;
            if (StringUtils.isEmpty(selection)) {
                selected = true;
            } else {
                selected = Boolean.parseBoolean(selection);
            }
            m_singleWindowCheckBox.setSelection(selected);
            String webdriverSelection = data.get(
                    AutConfigConstants.WEBDRIVER_MODE);
            boolean webdriverSelected = false;
            if (StringUtils.isEmpty(webdriverSelection)) {
                webdriverSelected = false;
            } else {
                webdriverSelected = Boolean.parseBoolean(webdriverSelection);
            }
            m_webdriverCheckBox.setSelection(webdriverSelected);
        } else {
            m_singleWindowCheckBox.setSelection(true);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    protected void populateExpertArea(Map<String, String> data) {
        m_activationMethodCombo.setSelectedObject(
                ActivationMethod.getEnum(data
                        .get(AutConfigConstants.ACTIVATION_METHOD)));
        if (!isDataNew(data)) {
            String webIdTag = data.get(AutConfigConstants.WEB_ID_TAG);
            if (webIdTag == null) {
                webIdTag = StringConstants.EMPTY;
            }
            m_autIdAttibuteTextField.setText(webIdTag);
        }
    }

    /**
     * 
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
     * 
     * @return the single instance of the selection listener.
     */
    @SuppressWarnings("synthetic-access")
    private WidgetSelectionListener getSelectionListener() {
        if (m_selectionListener == null) {
            m_selectionListener = new WidgetSelectionListener();
        }
        
        return m_selectionListener;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void checkAll(java.util.List<DialogStatusParameter> paramList) {
        super.checkAll(paramList);
        addError(paramList, modifyUrlTextField());
        addError(paramList, modifyIDAttributeTextField());
        addError(paramList, modifyBrowser());
        addError(paramList, modifyBrowserPathTextField());
        addError(paramList, modifyDriverPathTextField());
        addError(paramList, modifySingleWindowCheckBox());
        addError(paramList, modifyWebdriverCheckBox());
        addError(paramList, modifyBrowserSize());
        handleActivationComboEvent();
    }
    
    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifySingleWindowCheckBox() {
        DialogStatusParameter error = null;
        Boolean checked = m_singleWindowCheckBox.getSelection();
        putConfigValue(AutConfigConstants.SINGLE_WINDOW_MODE,
                checked.toString());
        
        return error;
    }
    
    /**
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyWebdriverCheckBox() {
        DialogStatusParameter error = null;
        Boolean checked = m_webdriverCheckBox.getSelection();
        putConfigValue(AutConfigConstants.WEBDRIVER_MODE,
                checked.toString());
        
        return error;
    }

    /**
     * Inits the SingleWindowMode CheckBox which tells the server in which mode to run
     * @param parent The parent Composite.
     */
    protected void createSingleModeCheckBox(Composite parent) {
        Label singleWindowModeLabel = UIComponentHelper.createLabel(parent, "WebAutConfigComponent.singleWindowMode"); //$NON-NLS-1$ 
        singleWindowModeLabel.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.singleWindowModeLabel"); //$NON-NLS-1$
        ControlDecorator.createInfo(singleWindowModeLabel,  
                I18n.getString("ControlDecorator.SingleWindowMode"), false); //$NON-NLS-1$
        m_singleWindowCheckBox = UIComponentHelper
                .createToggleButton(parent, 2);
        m_singleWindowCheckBox.setData(SwtToolkitConstants.WIDGET_NAME, "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.SingleWindowCheckBox"); //$NON-NLS-1$ 
        
    }

    /**
     * Creates checkbox determining whether webdriver should be used
     * @param parent The parent Composite.
     */
    protected void createWebdriverCheckBox(Composite parent) {
        Label useWebdriverLabel = UIComponentHelper.createLabel(parent,
                "WebAutConfigComponent.webdriverMode"); //$NON-NLS-1$ 
        useWebdriverLabel.setData(SwtToolkitConstants.WIDGET_NAME,
                "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.WebdriverLabel"); //$NON-NLS-1$
        ControlDecorator.createInfo(useWebdriverLabel,  
                I18n.getString("ControlDecorator.WebdriverMode"), false); //$NON-NLS-1$
        m_webdriverCheckBox = UIComponentHelper
                .createToggleButton(parent, 2);
        m_webdriverCheckBox.setData(SwtToolkitConstants.WIDGET_NAME,
                "org.eclipse.jubula.toolkit.provider.html.gui.HtmlAutConfigComponent.WebdriverCheckBox"); //$NON-NLS-1$ 
    }
    
}
