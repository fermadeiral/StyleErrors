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

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * @author Markus Tiede
 * @created Jul 18, 2011
 */
public class CleanupComponentNamesDialog extends TitleAreaDialog {
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;
    
    /**
     * <code>m_cbtv</code>
     */
    private CheckboxTableViewer m_cbtv = null;
    
    /**
     * <code>m_checkedElements</code>
     */
    private Object[] m_checkedElements = ArrayUtils.EMPTY_OBJECT_ARRAY;

    /**
     * <code>m_unusedNames</code>
     */
    private final List<IComponentNamePO> m_unusedNames;

    /**
     * @param parentShell
     *            the parent shell to use
     * @param unusedNames
     *            a list of unused component names to delete from this object
     *            mapping editor
     */
    public CleanupComponentNamesDialog(Shell parentShell,
            List<IComponentNamePO> unusedNames) {
        super(parentShell);
        m_unusedNames = unusedNames;
        setBlockOnOpen(true);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.CleanupComponentNamesDialogTitle);
        setMessage(Messages.CleanupComponentNamesDialogMessage);
        getShell().setText(Messages.CleanupComponentNamesDialogTitle);
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = 1;
        gridLayoutParent.verticalSpacing = 2;
        gridLayoutParent.marginWidth = 2;
        gridLayoutParent.marginHeight = 2;
        parent.setLayout(gridLayoutParent);

        LayoutUtil.createSeparator(parent);

        final Composite area = new Composite(parent, SWT.NULL);
        // use Gridlayout
        final GridLayout gridLayout = new GridLayout();

        area.setLayout(gridLayout);

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;

        area.setLayoutData(gridData);

        setCbtv(CheckboxTableViewer.newCheckList(area,
                SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL));
        
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.heightHint = WIDTH_HINT;
        LayoutUtil.addToolTipAndMaxWidth(layoutData, getCbtv().getControl());
        getCbtv().getControl().setLayoutData(layoutData);
        getCbtv().setUseHashlookup(true);
        WritableList wl = new WritableList(m_unusedNames, 
                IComponentNamePO.class);
        ViewerSupport.bind(getCbtv(), wl, PojoProperties.value("name")); //$NON-NLS-1$
        
        getCbtv().setComparator(new ViewerComparator() {
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof String && e2 instanceof String) {
                    ((String)e1).compareTo((String)e2);
                }
                return super.compare(viewer, e1, e2);
            }
        });
        
        getCbtv().setAllChecked(true);
        addButtons(area);
        return area;
    }
    
    /**
     * @param area the area
     */
    private void addButtons(Composite area) {
        LayoutUtil.createSeparator(area);
        GridData btn1GridData = new GridData(SWT.END, SWT.FILL, true, false);
        GridData btn2GridData = new GridData(SWT.END, SWT.FILL, false, false);
        // create the two buttons for selecting everything or nothing
        Button selectAllBtn = new Button(area, SWT.PUSH);
        selectAllBtn.setText(Messages.SelectAll);
        selectAllBtn.setLayoutData(btn1GridData);
        selectAllBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                getCbtv().setAllChecked(true);
            }
        });
        Button deselectAllBtn = new Button(area, SWT.PUSH);
        deselectAllBtn.setText(Messages.DeselectAll);
        deselectAllBtn.setLayoutData(btn2GridData);
        deselectAllBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                getCbtv().setAllChecked(false);
            }
        });
        LayoutUtil.createSeparator(area);        
    }

    /**
     * {@inheritDoc}
     */
    public boolean close() {
        m_checkedElements = getCbtv().getCheckedElements();
        return super.close();
    }
    
    /**
     * @return a list of checked = toDelete Elements
     */
    public Object[] getCheckedElements() {
        return m_checkedElements;
    }

    /**
     * @param cbtv the cbtv to set
     */
    protected void setCbtv(CheckboxTableViewer cbtv) {
        m_cbtv = cbtv;
    }

    /**
     * @return the cbtv
     */
    protected CheckboxTableViewer getCbtv() {
        return m_cbtv;
    }
}
