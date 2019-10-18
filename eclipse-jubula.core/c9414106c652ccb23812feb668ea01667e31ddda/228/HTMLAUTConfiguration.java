/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.html.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.toolkit.base.config.AbstractOSAUTConfiguration;
import org.eclipse.jubula.toolkit.html.Browser;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public class HTMLAUTConfiguration extends AbstractOSAUTConfiguration {
    /** the URL to open */
    @NonNull private URL m_url;
    /** the browser to use */
    @NonNull private Browser m_browser;
    /** the browser path to use */
    @Nullable private String m_browserPath;
    /** the AUT window mode */
    private boolean m_singleWindow = true;
    /** whether to use webdriver */
    private boolean m_webdriver = true;
    /** the name of the attribute used to retrieve a unique identifier */
    @Nullable private String m_idAttributeName;
    /** the size of browser to request */
    @Nullable private String m_browserSize;
    
    /**
     * Constructor for AUT Configuration using default Selenium
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param workingDir
     *            the working directory for the AUT process. If a relative path
     *            is given the base path is relative to the process working
     *            directory of the connected
     *            {@link org.eclipse.jubula.client.AUTAgent AUTAgent}
     * @param sURL
     *            the URL to open
     * @param browser
     *            the browser type to use
     * @param browserPath
     *            the path to the browser executable
     * @param singleWindow
     *            whether the AUT should be opened in single window mode
     * @param idAttributeName
     *            the name of the attribute used to retrieve a unique identifier
     * @throws MalformedURLException
     *             If the URL string specifies an unknown protocol.
     */
    public HTMLAUTConfiguration(
            @Nullable String name, 
            @NonNull String autID,
            @NonNull String workingDir,
            @NonNull String sURL,
            @NonNull Browser browser,
            @Nullable String browserPath,
            boolean singleWindow,
            @Nullable String idAttributeName)
                    throws MalformedURLException {
        super(name, autID, workingDir);
        
        Validate.notNull(sURL, "The URL must not be null"); //$NON-NLS-1$
        m_url = new URL(sURL);

        Validate.notNull(browser, "The Browser must not be null"); //$NON-NLS-1$
        m_browser = browser;
        
        if (browserPath != null && Browser.InternetExplorer.equals(browser)) {
            throw new IllegalArgumentException("Setting of browser path is not supported for " + browser); //$NON-NLS-1$
        }
        
        m_browserPath = browserPath;
        m_singleWindow = singleWindow;
        m_idAttributeName = idAttributeName;

        // Toolkit specific information
        add(AutConfigConstants.AUT_URL, sURL);
        add(AutConfigConstants.BROWSER, browser.toString());
        add(AutConfigConstants.BROWSER_PATH, browserPath);
        add(AutConfigConstants.SINGLE_WINDOW_MODE, 
                String.valueOf(singleWindow));
        add(AutConfigConstants.WEBDRIVER_MODE, String.valueOf(false));
        add(AutConfigConstants.WEB_ID_TAG, idAttributeName);
        add(ToolkitConstants.ATTR_TOOLKITID, CommandConstants.HTML_TOOLKIT);
    }
    
    /**
     * Constructor for AUT Configuration using Selenium WebDriver
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param workingDir
     *            the working directory for the AUT process. If a relative path
     *            is given the base path is relative to the process working
     *            directory of the connected
     *            {@link org.eclipse.jubula.client.AUTAgent AUTAgent}
     * @param sURL
     *            the URL to open
     * @param browser
     *            the browser type to use
     * @param browserPath
     *            the path to the browser executable
     * @param idAttributeName
     *            the name of the attribute used to retrieve a unique identifier
     * @param browserSize
     *            size of the browser
     * @throws MalformedURLException
     *             If the URL string specifies an unknown protocol.
     * @since 4.0
     */
    public HTMLAUTConfiguration(
            @Nullable String name, 
            @NonNull String autID,
            @NonNull String workingDir,
            @NonNull String sURL,
            @NonNull Browser browser,
            @Nullable String browserPath,
            @Nullable String idAttributeName,
            @Nullable String browserSize) throws MalformedURLException {
        super(name, autID, workingDir);
        
        Validate.notNull(sURL, "The URL must not be null"); //$NON-NLS-1$
        m_url = new URL(sURL);

        Validate.notNull(browser, "The Browser must not be null"); //$NON-NLS-1$
        m_browser = browser;
        
        if (browserPath != null && Browser.InternetExplorer.equals(browser)) {
            throw new IllegalArgumentException("Setting of browser path is not supported for " + browser); //$NON-NLS-1$
        }
        
        m_browserPath = browserPath;
        m_idAttributeName = idAttributeName;

        // Toolkit specific information
        add(AutConfigConstants.AUT_URL, sURL);
        add(AutConfigConstants.BROWSER, browser.toString());
        add(AutConfigConstants.BROWSER_PATH, browserPath);
        add(AutConfigConstants.WEBDRIVER_MODE, String.valueOf(true));
        add(AutConfigConstants.BROWSER_SIZE, browserSize);
        add(AutConfigConstants.WEB_ID_TAG, idAttributeName);
        add(ToolkitConstants.ATTR_TOOLKITID, CommandConstants.HTML_TOOLKIT);
    }

    /**
     * @return the URL
     */
    @NonNull
    public URL getUrl() {
        return m_url;
    }

    /**
     * @return the browser
     */
    @NonNull
    public Browser getBrowser() {
        return m_browser;
    }

    /**
     * @return the browserPath
     */
    @Nullable
    public String getBrowserPath() {
        return m_browserPath;
    }

    /**
     * @return the singleWindow
     */
    public boolean isSingleWindow() {
        return m_singleWindow;
    }

    /**
     * @return whether to use webdriver
     * @since 4.0
     */
    public boolean useWebdriver() {
        return m_webdriver;
    }

    /**
     * @return the idAttributeName
     */
    @Nullable
    public String getIdAttributeName() {
        return m_idAttributeName;
    }
}