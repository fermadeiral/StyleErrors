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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.RemoteFileStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.internal.ide.dialogs.IFileStoreFilter;


/**
 * Selection dialog to select files and/or folders on a remote 
 * the file system. Use setInput to set input to an IFileStore that 
 * points to a folder.
 * 
 */
public class RemoteFileBrowserDialog extends ElementTreeSelectionDialog {
    /**
     * Label provider for IFileStore objects.
     */
    private static class FileLabelProvider extends LabelProvider {
        /** folder icon */
        private static final Image IMG_FOLDER = PlatformUI.getWorkbench()
                .getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        /** file icon */
        private static final Image IMG_FILE = PlatformUI.getWorkbench()
                .getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

        /**
         * {@inheritDoc}
         */
        public Image getImage(Object element) {
            if (element instanceof IFileStore) {
                IFileStore curr = (IFileStore) element;
                if (curr.fetchInfo().isDirectory()) {
                    return IMG_FOLDER;
                }
                return IMG_FILE;
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String getText(Object element) {
            if (element instanceof IFileStore) {
                return ((IFileStore) element).getName();
            }
            return super.getText(element);
        }
    }

    /**
     * Content provider for IFileStore objects.
     */
    private static class FileContentProvider implements ITreeContentProvider {
        /** empty result */
        private static final Object[] EMPTY = new Object[0];
        /** filter */
        private IFileStoreFilter m_fileFilter;

        /**
         * Creates a new instance of the receiver.
         * 
         * @param showFiles
         *            <code>true</code> files and folders are returned by the
         *            receiver. <code>false</code> only folders are returned.
         */
        public FileContentProvider(final boolean showFiles) {
            m_fileFilter = new IFileStoreFilter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.ui.internal.ide.dialogs.IFileStoreFilter#accept(org.eclipse.core.filesystem.IFileStore)
                 */
                public boolean accept(IFileStore file) {
                    if (!file.fetchInfo().exists()) {
                        return false;
                    }
                    return !(!file.fetchInfo().isDirectory() && !showFiles);
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IFileStore) {
                IFileStore[] children = IDEResourceInfoUtils.listFileStores(
                        (IFileStore) parentElement, m_fileFilter,
                        new NullProgressMonitor());
                if (children != null) {
                    return children;
                }
            }
            return EMPTY;
        }

        /**
         * 
         * {@inheritDoc}
         */
        public Object getParent(Object element) {
            if (element instanceof IFileStore) {
                return ((IFileStore) element).getParent();
            }
            return null;
        }

        /**
         * 
         * {@inheritDoc}
         */
        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public Object[] getElements(Object element) {
            return getChildren(element);
        }

        /**
         * 
         * {@inheritDoc}
         */
        public void dispose() {
            // nothing here
        }

        /**
         * 
         * {@inheritDoc}
         */
        public void inputChanged(Viewer viewer, Object oldInput, 
                Object newInput) {
            // nothing here
        }
    }

    /**
     * Viewer sorter that places folders first, then files.
     */
    private static class FileViewerSorter extends ViewerComparator {

        /**
         * 
         * {@inheritDoc}
         */
        public int category(Object element) {
            if (element instanceof IFileStore
                    && !((IFileStore) element).fetchInfo().isDirectory()) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Validates the selection based on the multi select and folder setting.
     */
    private static class FileSelectionValidator implements
            ISelectionStatusValidator {
        /** allow multiple selection */
        private boolean m_multiSelect;
        /** accept folders */
        private boolean m_acceptFolders;

        /**
         * Creates a new instance of the receiver.
         * 
         * @param multiSelect
         *            <code>true</code> if multi selection is allowed.
         *            <code>false</code> if only single selection is allowed.
         * @param acceptFolders
         *            <code>true</code> if folders can be selected in the
         *            dialog. <code>false</code> only files and be selected.
         */
        public FileSelectionValidator(boolean multiSelect, 
            boolean acceptFolders) {
            this.m_multiSelect = multiSelect;
            this.m_acceptFolders = acceptFolders;
        }

        /**
         * 
         * {@inheritDoc}
         */
        public IStatus validate(Object[] selection) {
            int nSelected = selection.length;
            String pluginId = IDEWorkbenchPlugin.IDE_WORKBENCH;

            if (nSelected == 0 || (nSelected > 1 && !m_multiSelect)) {
                return new Status(IStatus.ERROR, pluginId, IStatus.ERROR,
                        IDEResourceInfoUtils.EMPTY_STRING, null);
            }
            for (int i = 0; i < selection.length; i++) {
                Object curr = selection[i];
                if (curr instanceof IFileStore) {
                    IFileStore file = (IFileStore) curr;
                    if (!m_acceptFolders
                            && file.fetchInfo().isDirectory()) {
                        return new Status(IStatus.ERROR, pluginId,
                                IStatus.ERROR,
                                IDEResourceInfoUtils.EMPTY_STRING, null);
                    }

                }
            }
            return Status.OK_STATUS;
        }
    }

    /** gui element for remote filesystem roots */
    private ListViewer m_fsRootBrowser;
    /** temporary storage for the model input for the fsRootBrowser */
    private List<String> m_fsRootsInput;


    /**
     * Creates a new instance of the receiver.
     * 
     * @param parent parent shell or null if none
     * @param multiSelect
     *            <code>true</code> if multi selection is allowed.
     *            <code>false</code> if only single selection is allowed.
     * @param type
     *            one or both of <code>IResource.FILE</code> and
     *            <code>IResource.FOLDER</code>, ORed together. If
     *            <code>IResource.FILE</code> is specified files and folders
     *            are displayed in the dialog. Otherwise only folders are
     *            displayed. If <code>IResource.FOLDER</code> is specified
     *            folders can be selected in addition to files.
     */
    @SuppressWarnings("synthetic-access")
    public RemoteFileBrowserDialog(Shell parent, boolean multiSelect, 
        int type) {
        super(parent, new FileLabelProvider(), new FileContentProvider(
                (type & IResource.FILE) != 0));
        setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL);
        
        setComparator(new FileViewerSorter());
        setValidator(new FileSelectionValidator(multiSelect,
                (type & IResource.FOLDER) != 0));
        setEmptyListMessage(Messages.AUTConfigComponentRemoteDirEmpty);
    }

    /**
     * {@inheritDoc}
     */
    protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
        
        m_fsRootBrowser = createFSRootSelector(parent, style);
        TreeViewer tv = super.doCreateTreeViewer(parent, style);

        return tv;
    }

    /**
     * @param parent Widget parent
     * @param style Widget style
     * @return a TableViewer for showing remote filesystem root elements.
     */
    private ListViewer createFSRootSelector(Composite parent, int style) {
        ListViewer lv = new ListViewer(parent, style);
        lv.setContentProvider(new ArrayContentProvider());
        lv.setInput(m_fsRootsInput);
        lv.addDoubleClickListener(new IDoubleClickListener() {

            @SuppressWarnings("synthetic-access")
            public void doubleClick(DoubleClickEvent event) {
                RemoteFileStore oldFS = (RemoteFileStore)getTreeViewer()
                        .getInput();
                final RemoteFileStore newRemoteFS = new RemoteFileStore(oldFS
                        .getCommunicator(),
                        (String)((StructuredSelection)(event.getSelection()))
                                .getFirstElement(), true);
                setInput(newRemoteFS);
                getTreeViewer().setInput(newRemoteFS);
                getTreeViewer().getTree().setFocus();
            }
        });

        GridData data = new GridData(GridData.FILL_BOTH);

        org.eclipse.swt.widgets.List list = lv.getList();
        list.setLayoutData(data);
        list.setFont(parent.getFont());

        return lv;
    }

    /**
     * @param remoteFSRoots data for filesystem roots
     */
    public void setFSRoots(List<String> remoteFSRoots) {
        m_fsRootsInput = remoteFSRoots;
    }

    /**
     * {@inheritDoc}
     */
    public void setInput(Object input) {
        super.setInput(input);
    }

    /**
     * {@inheritDoc}
     */
    public void create() {
        super.create();
        selectRoot();
        getTreeViewer().getTree().setFocus();
    }

    /**
     * if allowed by the current creation state select a root entry
     */
    private void selectRoot() {
        final TreeViewer treeViewer = getTreeViewer();
        if (treeViewer != null) {
            final Object input = treeViewer.getInput();
            if (input != null && input instanceof RemoteFileStore) {
                RemoteFileStore fs = (RemoteFileStore)input;
                if (m_fsRootBrowser != null) {
                    m_fsRootBrowser.setSelection(new StructuredSelection(fs
                            .getPath()));
                }
            }
        }
    }


    
    
}
