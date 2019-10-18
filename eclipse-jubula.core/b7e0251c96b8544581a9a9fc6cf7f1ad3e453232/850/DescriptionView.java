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
package org.eclipse.jubula.client.wiki.ui.views;

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.wiki.ui.i18n.Messages;
import org.eclipse.jubula.client.wiki.ui.utils.DescriptionUtil;
import org.eclipse.jubula.client.wiki.ui.utils.ProjectMarkupUtil;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class DescriptionView extends ViewPart implements IDataChangedListener {
    /** the view id (should be same as in plugin.xml) */
    public static final String VIEW_ID = "org.eclipse.jubula.client.wiki.ui.views.DescriptionView"; //$NON-NLS-1$
    
    /** the viewer to display the documentation */
    private Browser m_browser;
    /** the markup parser to use */ 
    private MarkupParser m_markupParser;
    /** the selected Node  - needed for reloading reasons*/
    private INodePO m_selectedNode;
    
    /** the m_listener we register with the selection service */
    private ISelectionListener m_listener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart,
            ISelection selection) {
            // we ignore our own selections
            if (sourcepart != DescriptionView.this) {
                showSelection(selection);
            }
        }

        /**
         * @param selection
         *            the selection to show
         */
        private void showSelection(ISelection selection) {
            if (selection instanceof StructuredSelection) {
                StructuredSelection structuredSelection = 
                    (StructuredSelection) selection;
                Object firstElement = structuredSelection.getFirstElement();
                if (firstElement instanceof INodePO) {
                    m_selectedNode = (INodePO) firstElement;
                    setDescriptionForBrowser(m_selectedNode);
                    return;
                }
                setBrowserNoDescriptionAvailable();
                return;
            }
            setBrowserNoDescriptionAvailable();
        }
    };

    /** {@inheritDoc} */
    public void createPartControl(Composite parent) {
        m_browser = new Browser(parent, SWT.NONE);
        DataEventDispatcher.getInstance().addDataChangedListener(this, true);
        /*
         *  taken from 
         *      org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor
         *      line 247 and following
         *  bug http://eclip.se/260479: open hyperlinks in a browser
         */
        m_browser.addLocationListener(new LocationListener() {
            public void changed(LocationEvent event) {
                event.doit = false;
            }

            public void changing(LocationEvent event) {
                // if it looks like an absolute URL
                if (event.location.matches("([a-zA-Z]{3,8})://?.*")) { //$NON-NLS-1$

                    // workaround for browser problem (bug http://eclip.se/262043)
                    int idxOfSlashHash = event.location.indexOf("/#"); //$NON-NLS-1$
                    if (idxOfSlashHash != -1) {
                        // allow javascript-based scrolling to work
                        if (!event.location.startsWith("file:///#")) { //$NON-NLS-1$
                            event.doit = false;
                        }
                        return;
                    }
                    // workaround end

                    event.doit = false;
                    try {
                        PlatformUI.getWorkbench().getBrowserSupport().createBrowser("org.eclipse.ui.browser") //$NON-NLS-1$
                                .openURL(new URL(event.location));
                    } catch (Exception e) {
                        new URLHyperlink(new Region(0, 1), 
                            event.location).open();
                    }
                }
            }
        });
        
        m_markupParser = new MarkupParser();
        m_markupParser.setMarkupLanguage(ProjectMarkupUtil
                .getProjectMarkupLanguage());

        getSite().getWorkbenchWindow().getSelectionService()
            .addSelectionListener(m_listener);
    }

    /** {@inheritDoc} */
    public void setFocus() {
        m_browser.setFocus();
    }

    /** {@inheritDoc} */
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService()
            .removeSelectionListener(m_listener);
        super.dispose();
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        boolean isRefreshNecessary = false;
        for (DataChangedEvent dataChangedEvent : events) {
            if (dataChangedEvent.getPo() instanceof IProjectPropertiesPO) {
                m_markupParser.setMarkupLanguage(ProjectMarkupUtil
                        .getProjectMarkupLanguage());
                isRefreshNecessary = true;
            }
            if (m_selectedNode != null
                    && m_selectedNode.equals(dataChangedEvent.getPo())) {
                isRefreshNecessary = true;
            }
        }
        if (m_selectedNode != null && isRefreshNecessary) {
            setDescriptionForBrowser(m_selectedNode);
        }
    }
       
    /**
     * 
     * @param element the object to check if there is a description
     */
    private void setDescriptionForBrowser(INodePO element) {
        String description = m_selectedNode.getDescription();
        if (StringUtils.isBlank(description)) {
            description = DescriptionUtil
                    .getReferenceDescription(m_selectedNode);
        }
        if (StringUtils.isNotBlank(description)) {
            m_browser.setText(m_markupParser.parseToHtml(description));
            return;
        }
        setBrowserNoDescriptionAvailable();
    }
    /**
     * sets the Browser text to have noc description
     */
    private void setBrowserNoDescriptionAvailable() {
        m_browser.setText(Messages.NoDescriptionAvailable);
    }
}