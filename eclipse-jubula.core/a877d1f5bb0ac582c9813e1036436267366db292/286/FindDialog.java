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
package org.eclipse.jubula.client.ui.rcp.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.UINodeBP;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.query.Operation;
import org.eclipse.jubula.client.ui.rcp.search.query.TextFinder;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.client.ui.utils.TreeViewerIterator;
import org.eclipse.jubula.client.ui.views.IMultiTreeViewerContainer;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;



/**
 * This class creates a dialog, where you can search a TreeViewer for specified
 * string. The search is performed by using content- and labelprovider of the 
 * treeviewer.
 * 
 * @author BREDEX GmbH
 * @created 22.02.2005
 * @param <NODE> INodePO or TestResultNodeGUI
 */
public class FindDialog <NODE> implements DisposeListener {

    /** recent queries */
    private static final List <String> RECENT = new ArrayList<String>(5);
    /** recent option */
    private static boolean lastforward = true;
    /** recent option */
    private static boolean lastwrapsearch = true;
    /** recent option */
    private static boolean lastregex = false;
    /** recent option */
    private static boolean lastcasesensitive = false;
    /** the shell depends on the object that is selected */
    private Shell m_shell;
    /** find listener for callback */
    private ITreeViewerContainer m_treeViewContainer;
    /** search Text Field */
    private Combo m_searchStringCombo;
    /** Directions Group */
    private Group m_directionGroup;
    /** Combo to select Find Forward */
    private Button m_findForwardButton;
    /** Combo to select Find Backward */
    private Button m_findBackwardButton;
    /** Options Group */
    private Group m_optionsGroup;
    /** CheckbBox to select Wrap Search */
    private Button m_wrapSearchCheck;
    /** CheckbBox to select use regular expression */
    private Button m_useRegExCheck;
    /** CheckbBox to select use search case sensitiv */
    private Button m_caseSensitivCheck;
    /** help Link */
    private Link m_helpLink;
    /** find Button */ 
    private Button m_findButton;
    /** cancel Button */
    private Button m_cancelButton;
    /** Label for showing errors */
    private Label m_errorLabel;
    /**
     * @param parentShell The parent shell.
     * @param listener IFindListener
     */
    public FindDialog(Shell parentShell, ITreeViewerContainer listener) {
        m_shell = new Shell(parentShell);
        setTreeViewContainer(listener);
        init();
        Plugin.getHelpSystem().setHelp(parentShell, ContextHelpIds.FIND_DIALOG);
    }

    /**
     * initializes search window
     */
    private void init() {
        m_shell.setText(Messages.FindDialogTitle);
        createContent();
        Point location = m_shell.getDisplay().getCursorLocation();
        if ((location.x + m_shell.getSize().x) > m_shell.getDisplay().
                getPrimaryMonitor().getBounds().width) {
            location.x = m_shell.getDisplay().getPrimaryMonitor().
                getBounds().width - m_shell.getSize().x;
        }
        if ((location.y + m_shell.getSize().y) > m_shell.getDisplay().
                getPrimaryMonitor().getBounds().height) {
            location.y = m_shell.getDisplay().getPrimaryMonitor().
                getBounds().height - m_shell.getSize().y;
        }
        m_shell.setLocation(location);
    }

    /**
     * creates all the dialog content
     */
    private void createContent() {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 10;
        m_shell.setLayout(layout);
        Label findLabel = new Label(m_shell, SWT.NONE);
        findLabel.setText(Messages.FindDialogFind + StringConstants.COLON);
        FormData findLabelData = new FormData();
        // set position and size with relative values
        findLabelData.left   = new FormAttachment(0, 0);
        findLabelData.top    = new FormAttachment(0, 10);
        findLabel.setLayoutData(findLabelData);
        m_searchStringCombo = new Combo(m_shell, SWT.BORDER);
        FormData searchStringTextData = new FormData();
        searchStringTextData.top = new FormAttachment(findLabel, 0, SWT.TOP);
        searchStringTextData.left = new 
            FormAttachment(findLabel, 10, SWT.RIGHT);
        searchStringTextData.right = new FormAttachment(100, 0);
        m_searchStringCombo.setLayoutData(searchStringTextData);
        m_searchStringCombo.setItems(RECENT.toArray
            (new String[RECENT.size()]));
        if (m_searchStringCombo.getItemCount() == 0) {
            m_searchStringCombo.setText(Messages.FindDialogPhrase);
        } else {
            m_searchStringCombo.select(0);
        }
        m_searchStringCombo.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == 13) {
                    doCallBack();
                }
            }
        });
        createDirectionGroup();
        createOptionsGroup();
        createButtons();
        createHelpLink();
        m_errorLabel = new Label(m_shell, SWT.NONE);
        m_errorLabel.setForeground(Display.getDefault().
                getSystemColor(SWT.COLOR_RED));
        FormData errorLabelData = new FormData();
        errorLabelData.top = new FormAttachment(m_findButton, 5, SWT.BOTTOM);
        errorLabelData.left = new FormAttachment(5, 0);
        errorLabelData.right = new FormAttachment(100, 0);
        m_errorLabel.setLayoutData(errorLabelData);
        m_shell.pack();
    }

    /**
     * creates the find and the close button
     */
    private void createButtons() {
        m_findButton = new Button(m_shell, SWT.PUSH);
        m_findButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                doCallBack();
            }
        });
        m_findButton.setText(Messages.FindDialogFind);
        FormData findButtonData = new FormData();
        findButtonData.top = new FormAttachment(m_optionsGroup, 15, SWT.BOTTOM);
        findButtonData.left = new FormAttachment(20, 0);
        findButtonData.right = new FormAttachment(58, 0);
        m_findButton.setLayoutData(findButtonData);
        m_cancelButton = new Button(m_shell, SWT.NONE);
        m_cancelButton.setText(Messages.FindDialogClose);
        m_cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                m_shell.close();
            }
        });
        FormData cancelButtonData = new FormData();
        cancelButtonData.top = new FormAttachment(m_optionsGroup, 15, 
                SWT.BOTTOM);
        cancelButtonData.left = new FormAttachment(62, 0);
        cancelButtonData.right = new FormAttachment(100, 0);
        m_cancelButton.setLayoutData(cancelButtonData);
    }
    
    /**
     * creates the help link
     * borrowed from TrayDialog
     * adapted for FormLayout
     */
    private void createHelpLink() {
        m_helpLink = new Link(m_shell, SWT.WRAP | SWT.NO_FOCUS);
        m_helpLink.setText("<a>" + IDialogConstants.HELP_LABEL + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        m_helpLink.setToolTipText(IDialogConstants.HELP_LABEL);
        FormData helpLinkData = new FormData();
        helpLinkData.bottom = new FormAttachment(m_findButton, 0, SWT.BOTTOM);
        helpLinkData.left = new FormAttachment(0, 0);
        helpLinkData.right = new FormAttachment(16, 0);
        m_helpLink.setLayoutData(helpLinkData);
        m_helpLink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                helpPressed();
            }
        });
    }
    
    /**
     * performs the the action for the help link
     * borrowed and adapted from TrayDialog
     */
    private void helpPressed() {
        if (m_shell != null) {
            Control c = m_shell.getDisplay().getFocusControl();
            while (c != null) {
                if (c.isListening(SWT.Help)) {
                    c.notifyListeners(SWT.Help, new Event());
                    break;
                }
                c = c.getParent();
            }
        }
    }

    /**
     * creates the Direction Group
     */
    private void createDirectionGroup() {
        m_directionGroup = new Group(m_shell, SWT.NONE);
        m_directionGroup.setText(Messages.FindDialogDirection);
        FormLayout groupLayout = new FormLayout();
        m_directionGroup.setLayout(groupLayout);
        groupLayout.marginHeight = 5;
        groupLayout.marginWidth = 5;
        FormData groupDirectionData = new FormData();
        groupDirectionData.top = new FormAttachment(m_searchStringCombo, 
                15, SWT.BOTTOM);
        groupDirectionData.right = new FormAttachment(100, 0);
        groupDirectionData.left = new FormAttachment(0, 0);
        m_directionGroup.setLayoutData(groupDirectionData);
        m_findForwardButton = new Button(m_directionGroup, SWT.RADIO);
        m_findForwardButton.setText(Messages.FindDialogForward);
        m_findForwardButton.setSelection(lastforward);
        FormData findForwardData = new FormData();
        findForwardData.left = new FormAttachment(0, 0);
        findForwardData.right = new FormAttachment(48, 0);
        m_findForwardButton.setLayoutData(findForwardData);
        m_findBackwardButton = new Button(m_directionGroup, SWT.RADIO);
        m_findBackwardButton.setText(Messages.FindDialogBackward);
        m_findBackwardButton.setSelection(!lastforward);
        FormData findBackwardData = new FormData();
        findBackwardData.left = new FormAttachment(52, 0);
        findBackwardData.right = new FormAttachment(100, 0);
        m_findBackwardButton.setLayoutData(findBackwardData);
    }

    /**
     * creates the Direction Group
     */
    private void createOptionsGroup() {
        m_optionsGroup = new Group(m_shell, SWT.NONE);
        m_optionsGroup.setText(Messages.FindDialogOptions);
        FormData groupDirectionData = new FormData();
        groupDirectionData.top = new FormAttachment(m_directionGroup, 10, 
                SWT.BOTTOM);
        groupDirectionData.right = new FormAttachment(100, 0);
        groupDirectionData.left = new FormAttachment(0, 0);
        m_optionsGroup.setLayoutData(groupDirectionData); 
        FormLayout layout = new FormLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        m_optionsGroup.setLayout(layout);
        m_caseSensitivCheck = new Button(m_optionsGroup, SWT.CHECK);
        m_caseSensitivCheck.setText(Messages.FindDialogCaseSen);
        m_caseSensitivCheck.setSelection(lastcasesensitive);
        FormData sensitiveData = new FormData();
        sensitiveData.left = new FormAttachment(0, 0);
        sensitiveData.right = new FormAttachment(48, 0);
        m_caseSensitivCheck.setLayoutData(sensitiveData); 
        m_wrapSearchCheck = new Button(m_optionsGroup, SWT.CHECK);
        m_wrapSearchCheck.setText(Messages.FindDialogWrap);
        m_wrapSearchCheck.setSelection(lastwrapsearch);
        FormData warpSearchData = new FormData();
        warpSearchData.left = new FormAttachment(52, 0);
        warpSearchData.right = new FormAttachment(100, 0);
        m_wrapSearchCheck.setLayoutData(warpSearchData);
        m_useRegExCheck = new Button(m_optionsGroup, SWT.CHECK);
        m_useRegExCheck.setText(Messages.FindDialogRegEx);
        m_useRegExCheck.setSelection(lastregex);
        FormData regExData = new FormData();
        regExData.left = new FormAttachment(0, 0);
        regExData.top = new FormAttachment(m_wrapSearchCheck, 5, SWT.BOTTOM);
        m_useRegExCheck.setLayoutData(regExData);
    }

    /**
     * opens the shell
     */
    public void open() {
        m_shell.open();
    }
    
    /**
     * @return true if shell is disposed
     */
    public boolean isDisposed() {
        return (m_shell == null) ? true : m_shell.isDisposed();
    }

    /**
     * calls find method on callback object
     */
    private void doCallBack() {
        if (!isValidPart(m_treeViewContainer)) {
            m_shell.close();
            return;
        }
        final String searchText = m_searchStringCombo.getText();
        
        lastcasesensitive = m_caseSensitivCheck.getSelection();
        lastregex = m_useRegExCheck.getSelection();
        lastwrapsearch = m_wrapSearchCheck.getSelection();
        lastforward = m_findForwardButton.getSelection();
        if (RECENT.contains(searchText)) {
            RECENT.remove(searchText);
        }
        m_findButton.setEnabled(false);
        m_errorLabel.setText(StringConstants.EMPTY);
        if (RECENT.size() > 4) {
            RECENT.remove(RECENT.size() - 1);
        }
        RECENT.add(0, searchText);
        
        ISelection treeSelection = getActiveTreeViewer().getSelection();
        final Object nodeToStart = treeSelection instanceof IStructuredSelection
                ? ((IStructuredSelection)treeSelection).getFirstElement() 
                        : null;
        final String jobName = Messages.UIJobFindingNodes;
        Job findingNodes = new Job(jobName) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                final boolean result = find(searchText, lastcasesensitive,
                        lastregex, lastwrapsearch, lastforward, nodeToStart,
                        monitor);
                Plugin.getDisplay().syncExec(new Runnable() {
                    public void run() {
                        if (!m_errorLabel.isDisposed()) {
                            if (!result) {
                                m_errorLabel.setText(Messages.FindDialogError);
                            } else {
                                m_errorLabel.setText(StringConstants.EMPTY);
                            }
                            m_searchStringCombo.setItems(RECENT
                                    .toArray(new String[RECENT.size()]));
                            m_searchStringCombo.select(0);
                            m_searchStringCombo.setFocus();
                            m_findButton.setEnabled(true);
                        }
                    }
                });
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        JobUtils.executeJob(findingNodes, null);
    }
    
    /**
     * @param queryString queryString
     * @param caseSensitive caseSensitive
     * @param useRegex useRegex
     * @param wrapSearch wrapSearch
     * @param forward forward
     * @param nodeToStart
     *            the node to start the search; may be null --> root is used as
     *            search start point
     * @param monitor the progress monitor to use to communicate progress
     * @return boolean
     */
    private boolean find(String queryString, boolean caseSensitive,
            boolean useRegex, boolean wrapSearch, boolean forward,
            Object nodeToStart, IProgressMonitor monitor) {
        final Object node = findGuiNode(queryString, caseSensitive, useRegex,
                wrapSearch, forward, nodeToStart, monitor);
        if (node != null) {
            // I don't know why it doesn't always work the first
            // time, but calling this method twice seems to fix the problem
            Plugin.getDisplay().syncExec(new Runnable() {
                public void run() {
                    UINodeBP.selectNodeInTree(node, getActiveTreeViewer());
                }
            });
            return true;
        }
        return false;
    }

    /**
     * 
     * @param treeViewerContainer The container to check.
     * @return <code>true</code> if the tree of the given container can be 
     *         operated on (i.e. not <code>null</code>, not disposed, etc.). 
     *         Otherwise, <code>false</code>.
     */
    private boolean isValidPart(ITreeViewerContainer treeViewerContainer) {
        return !(treeViewerContainer == null
                || treeViewerContainer.getTreeViewer() == null
                || treeViewerContainer.getTreeViewer().getTree().isDisposed());
    }

    /**
     * First flattens INodePO hierarchy to list, then iterate over list
     * 
     * @param queryString
     *            queryString
     * @param caseSensitive
     *            caseSensitive
     * @param useRegex
     *            useRegex
     * @param wrapSearch
     *            wrapSearch
     * @param forward
     *            forward
     * @param nodeToStart
     *            the node to start the search; may be null --> root is used as
     *            search start point
     * @param monitor the progress monitor to use to communicate progress
     * @return NODE
     */
    private Object findGuiNode(String queryString, 
            boolean caseSensitive, boolean useRegex, boolean wrapSearch, 
            boolean forward, Object nodeToStart, IProgressMonitor monitor) {
        TextFinder searcher = new TextFinder(
                queryString,
                Operation.create(caseSensitive, useRegex));
        if (monitor.isCanceled()) {
            return null;
        }
        if (wrapSearch || nodeToStart != null) {
            TreeViewerIterator iterator = new TreeViewerIterator(
                    getActiveTreeViewer(), nodeToStart,
                    forward);
            int noOfElements = iterator.getElements().size();
            monitor.beginTask(Messages.SearchingIn + StringConstants.SPACE
                    + noOfElements + StringConstants.SPACE + Messages.Elements
                    + StringConstants.DOT + StringConstants.DOT
                    + StringConstants.DOT, noOfElements);
            ILabelProvider labelProv = 
                (ILabelProvider)getActiveTreeViewer().getLabelProvider();
            while (iterator.hasNext()) {
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    return null;
                }
                Object o = iterator.next();
                if (searcher.matchSearchString(labelProv.getText(o))) {
                    monitor.done();
                    return o;
                }
            }
        }
        return null; 
    }
    
    /**
     * Listener method reacting on dispose events
     * @param e DisposeEvent
     */
    public void widgetDisposed(DisposeEvent e) {
        closeShell();
    }

    /**
     * Closes the shell for this dialog, if it is not already closed.
     */
    private void closeShell() {
        if (!isDisposed()) {
            m_shell.close();
        }
    }
    
    /**
     * Method for setting acutal part
     * @param viewContainer TreeViewContainer
     */
    public void setTreeViewContainer(ITreeViewerContainer viewContainer) {
        
        // Remove old listener, if necessary 
        if (isValidPart(m_treeViewContainer)) {
            m_treeViewContainer.getTreeViewer().getTree()
                .removeDisposeListener(this);
        }

        // Add new listener
        if (isValidPart(viewContainer)) {
            viewContainer.getTreeViewer().getTree()
                .addDisposeListener(this);
        }

        m_treeViewContainer = viewContainer;
    }

    /**
     * 
     * @return the tree viewer on which operations should be performed, or 
     *         <code>null</code> if no such tree viewer exists.
     */
    private TreeViewer getActiveTreeViewer() {
        if (m_treeViewContainer instanceof IMultiTreeViewerContainer) {
            return ((IMultiTreeViewerContainer)m_treeViewContainer)
                .getActiveTreeViewer();
        }

        return m_treeViewContainer.getTreeViewer();
    }
}