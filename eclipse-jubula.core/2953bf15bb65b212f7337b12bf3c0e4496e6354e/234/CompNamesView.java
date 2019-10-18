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
package org.eclipse.jubula.client.ui.rcp.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.views.dataset.DataSetView;
import org.eclipse.jubula.client.ui.views.IJBPart;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.PropertySheet;


/**
 * This view shows a table with first (old) and second (new) component names.
 * The view is active for test execution nodes. The table contains check boxes.
 * If one is checked, the new name will be propagated, that means, it is visible
 * by the parent test execution node.
 * 
 * @author BREDEX GmbH
 * @created 06.09.2005
 */
public class CompNamesView extends PageBookView 
        implements ISelectionListener, IContributedContentsView {
    
    /**
     * {@inheritDoc}
     */
    protected IPage createDefaultPage(PageBook book) {
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(Messages.CompNamesViewNoCompNameInfo);
        return page;
    }

    /**
     * {@inheritDoc}
     */
    protected PageRec doCreatePage(IWorkbenchPart part) {
        if (part instanceof AbstractJBTreeView
                || part instanceof IJBEditor
                || part instanceof SearchView) {
                
            CompNamesPage page = new CompNamesPage();
            initPage(page);
            page.createControl(getPageBook());
            return new PageRec(part, page);
        } else if (part instanceof IJBPart) {
            return new PageRec(part, createDefaultPage(getPageBook()));
        }

        // Use the default page     
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
        pageRecord.page.dispose();
        pageRecord.dispose();
    }

    /**
     * {@inheritDoc}
     */
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null) {
            return page.getActivePart();
        } 
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isImportant(IWorkbenchPart part) {
        return !(part == this
                || part instanceof DataSetView
                || part instanceof PropertySheet);
    }

    /**
     * {@inheritDoc}
     */
    public void init(IViewSite site) throws PartInitException {
        site.getPage().addSelectionListener(this);
        super.init(site);
    }

    /**
     * {@inheritDoc}
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        // we ignore "unimportant" selections or null selection
        if (!isImportant(part) || selection == null) {
            return;
        }

        IPage currentPage = getCurrentPage();
        if (currentPage instanceof ISelectionListener) {
            // pass the selection to the page       
            ((ISelectionListener)currentPage).selectionChanged(part, selection);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        Plugin.showStatusLine(this);
        super.setFocus();
    }

    /**
     * @return The parent composite of this workbench part.
     */
    public Composite getParentComposite() {
        return (Composite)getCurrentPage().getControl();
    }

    /**
     * {@inheritDoc}
     */
    public IWorkbenchPart getContributingPart() {
        return getCurrentContributingPart();
    }

}