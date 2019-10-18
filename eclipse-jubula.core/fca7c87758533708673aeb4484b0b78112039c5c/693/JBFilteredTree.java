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
package org.eclipse.jubula.client.ui.rcp.filter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author BREDEX GmbH
 * @created Sep 10, 2010
 */
public class JBFilteredTree extends FilteredTree {
    /**
     * <code>REFRESH_DELAY</code>
     */
    private static final int REFRESH_DELAY = 500;

    /**
     * @param parent
     *            the parent
     * @param treeStyle
     *            the tree style
     * @param filter
     *            the filter
     * @param useNewLook
     *            the new look
     */
    public JBFilteredTree(Composite parent, int treeStyle,
            PatternFilter filter, boolean useNewLook) {
        super(parent, treeStyle, filter, useNewLook);
    }

    /**
     * {@inheritDoc}
     */
    protected long getRefreshJobDelay() {
        return REFRESH_DELAY;
    }
    
    /**
     * {@inheritDoc}
     */
    protected WorkbenchJob doCreateRefreshJob() {
        final WorkbenchJob wj = super.doCreateRefreshJob();
        wj.addJobChangeListener(new IJobChangeListener() {
            public void sleeping(IJobChangeEvent event) {
            // nothing needs to be done here
            }

            public void scheduled(IJobChangeEvent event) {
            // nothing needs to be done here
            }

            public void running(IJobChangeEvent event) {
            // nothing needs to be done here
            }

            public void done(IJobChangeEvent event) {
                if (treeViewer != null && treeViewer.getTree() != null
                        && !treeViewer.getTree().isDisposed()
                        && filterText != null && !filterText.isDisposed()) {
                    if (StringUtils.EMPTY.equals(filterText.getText())) {
                        treeViewer.expandToLevel(treeViewer
                                .getAutoExpandLevel());
                    }
                } else {
                    wj.removeJobChangeListener(this);
                }
            }

            public void awake(IJobChangeEvent event) {
            // nothing needs to be done here
            }

            public void aboutToRun(IJobChangeEvent event) {
            // nothing needs to be done here
            }
        });
        return wj;
    }
}
