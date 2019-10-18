/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.views.logview;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.views.ContextBasedView;
import org.eclipse.jubula.client.ui.views.IJBPart;
import org.eclipse.jubula.client.ui.views.NonSortedPropertySheetPage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.IPropertySheetPage;
/**
 * View for log entries in TestResultNodes
 * @author BREDEX GmbH
 *
 */
public class LogView extends ContextBasedView 
    implements IJBPart, ISelectionProvider {
    /** id of the view must be the same as in the plugin.xml */
    public static final String VIEW_ID = "org.eclipse.jubula.client.ui.views.Log"; //$NON-NLS-1$
    /**
     * the scrolled composite
     */
    private ScrolledComposite m_scrollComposite;
    
    /**
     * the child
     */
    private Composite m_child;
    
    /**
     * <code>log</code>
     */
    private Text m_logWidget;
    
    /**
     * <code>
     */
    private String m_commandLog;
    
    /**
     * <code>m_oldSelection</code>
     */
    private ISelection m_currSelection = null;
    
    /**
     * sets the context id
     */
    public LogView() {
        super(Constants.LOGVIEW_DISPLAYS_LOG);
    }
    
    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        parent.setLayout(new FillLayout());
        m_scrollComposite = new ScrolledComposite(parent,
                SWT.V_SCROLL | SWT.H_SCROLL);
        m_child = new Composite(m_scrollComposite, SWT.NONE);
        m_child.setLayout(new FillLayout());
        m_logWidget = new Text(m_child, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL 
                | SWT.V_SCROLL);
        m_logWidget.setEditable(false);
        m_scrollComposite.setExpandHorizontal(true);
        m_scrollComposite.setExpandVertical(true);
        m_scrollComposite.setMinSize(m_child.computeSize(
                SWT.DEFAULT, SWT.DEFAULT));
        m_scrollComposite.setContent(m_child);
        
        // Create menu manager
        MenuManager contextManager = new MenuManager();
        contextManager.setRemoveAllWhenShown(true);
        // Create context menu
        Menu contextMenu = contextManager.createContextMenu(m_logWidget);
        m_logWidget.setMenu(contextMenu);
        // Register context menu
        getSite().registerContextMenu(contextManager, this);
        super.createPartControl(parent);
        handleSelection(getSelectionService().getSelection());
        getSite().setSelectionProvider(this);
    }

     /**
     * {@inheritDoc}
     */
    public void setFocus() {
        m_logWidget.setFocus();
    }

     /**
     * {@inheritDoc}
     */
    public void addSelectionChangedListener(
            ISelectionChangedListener listener) {
     // empty 
        
    }

     /**
     * {@inheritDoc}
     */
    public ISelection getSelection() {
        return m_currSelection;
    }

     /**
     * {@inheritDoc}
     */
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
     // empty 
        
    }

     /**
     * {@inheritDoc}
     */
    public void setSelection(ISelection selection) {
        //empty
    }
    
    /**
     * 
     * @param selection the selection to react on
     */
    protected void handleSelection(ISelection selection) {
        LogProvider provider = null;
        if (selection != null && selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection)selection;
            if (ss.size() > 0) {
                Object object = ss.getFirstElement();

                if (m_currSelection != null
                        && ObjectUtils.equals(object,
                                ((IStructuredSelection)m_currSelection)
                                        .getFirstElement())) {
                    return;
                }
                m_currSelection = ss;
                provider = Platform.getAdapterManager().getAdapter(
                        object, LogProvider.class);
            }
        }
        
        if (provider != null) {
            handleSelection(provider);
        }
    }
    
    /**
     * handles the selection with the correct provider
     * @param provider the {@link LogProvider}
     */
    private void handleSelection(LogProvider provider) {
        String string = provider.getLogString();
        String newline = StringConstants.NEWLINE;
        String systemNewLine = System.getProperty("line.separator"); //$NON-NLS-1$
        if (StringUtils.isNotBlank(string)) {
            // since it is saved unix newline is must be converted to system newline
            if (!newline.equals(systemNewLine)) {
                string = string.replace(newline, systemNewLine);
            }
            m_commandLog = string;
            m_logWidget.setText(m_commandLog);
            m_logWidget.setEnabled(true);
            setStatusOfContext(true);
            m_logWidget.redraw();
        } else {
            m_commandLog = StringConstants.EMPTY;
            m_logWidget.setText(m_commandLog);
            m_logWidget.setEnabled(false);
            setStatusOfContext(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (m_logWidget != null || !m_logWidget.isDisposed()) {
            m_logWidget.dispose();
        }
        super.dispose();
    }
    
    /**
     * 
     * @return the current shown command log
     */
    public String getCommandLog() {
        return m_commandLog;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getAdapter(Class key) {
        if (key.equals(IPropertySheetPage.class)) {
            return new NonSortedPropertySheetPage();
        }
        return super.getAdapter(key);
    }
}
