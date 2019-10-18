/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.CleanupComponentNamesDialog;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.delete.DeleteTreeItemHandlerOMEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.contentprovider.objectmapping.OMEditorTreeContentProvider;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowResponsibleNodeForComponentName;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Tiede
 * @created Jul 13, 2011
 */
public class OMEDeleteUnusedComponentNamesHandler extends AbstractHandler {
    /** standard logging */
    static final Logger LOG = LoggerFactory
            .getLogger(OMEDeleteUnusedComponentNamesHandler.class);

    /**
     * @author Markus Tiede
     * @created Jul 13, 2011
     */
    public class OMEUsedComponentNameSearch {
        /**
         * <code>m_aut</code> the aut to search for usage for
         */
        private IAUTMainPO m_aut = null;
        
        /**
         * <code>m_result</code>
         */
        private Map<Object, Boolean> m_result = new HashMap<Object, Boolean>();

        /**
         * @param aut
         *            the aut to search for usage for
         */
        public OMEUsedComponentNameSearch(IAUTMainPO aut) {
            m_aut = aut;
        }
        
        /**
         * @param elements the elements to init the cache with
         * @param monitor
         *            the progress monitor to use
         */
        public void performSearch(Object[] elements, IProgressMonitor monitor) {
            int noOfUnused = 0;
            for (Object o : elements) {
                if (monitor.isCanceled()) {
                    return;
                }
                if (!getResult().containsKey(o) && isUsed(o, monitor)
                        && o instanceof IComponentNamePO) {
                    noOfUnused++;
                    monitor.subTask(NLS.bind(Messages.
                            SearchingUnusedComponentNames, noOfUnused));
                }
                monitor.worked(1);
            }
        }

        /**
         * @param element the element to check for usage
         * @param monitor the progress monitor to use
         * @return true if used in TS for the current AUT, false otherwise
         */
        private boolean isUsed(Object element, IProgressMonitor monitor) {
            if (getResult().containsKey(element)) {
                return getResult().get(element);
            }
            boolean select = true;
            if (element instanceof IComponentNamePO) {
                IComponentNamePO compName = (IComponentNamePO)element;
                ShowResponsibleNodeForComponentName query =
                        new ShowResponsibleNodeForComponentName(
                                compName, m_aut);
                query.run(monitor);
                select = query.isEmpty();
            }
            getResult().put(element, select);
            return select;
        }

        /**
         * @return the result
         */
        public Map<Object, Boolean> getResult() {
            return m_result;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ObjectMappingMultiPageEditor editor = 
            ((ObjectMappingMultiPageEditor)HandlerUtil
                .getActivePartChecked(event));
        if (editor.getEditorHelper().requestEditableState() 
                != EditableState.OK) {
            return null;
        }
        // run in UI thread
        List<Object> allTreeElements = new ArrayList<Object>();
        for (final TreeViewer tv : editor.getTreeViewers()) {
            OMEditorTreeContentProvider ometcp = 
                (OMEditorTreeContentProvider)tv.getContentProvider();
            for (Object o : ometcp.getElements(tv.getInput())) {
                allTreeElements.addAll(getAllElements(o, ometcp));
            }
        }
        final Object[] aObjects = allTreeElements.toArray();
        final Shell activeShell = HandlerUtil.getActiveShell(event).getShell();
        // run in background
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(activeShell);
        try {
            dialog.run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) {
                    monitor.beginTask(Messages.Searching,
                            aObjects.length);
                    final OMEUsedComponentNameSearch vf = 
                        new OMEUsedComponentNameSearch(
                            editor.getAut());
                    vf.performSearch(aObjects, monitor);
                    
                    final List<IComponentNamePO> unusedNames = 
                        new ArrayList<IComponentNamePO>();

                    for (Object o : vf.getResult().keySet()) {
                        if (o instanceof IComponentNamePO) {
                            if (vf.getResult().get(o)) {
                                unusedNames.add((IComponentNamePO)o);
                            }
                        }
                    }
                    if (unusedNames.size() > 0) {
                        if (!monitor.isCanceled()) {
                            openDialog(unusedNames, editor);
                        }
                    } else {
                        Plugin.getDisplay().syncExec(new Runnable() {
                            public void run() {
                                MessageDialog.openInformation(activeShell,
                                    Messages.CleanCompNamesNoResultDialogTitle,
                                    Messages.CleanCompNamesNoResultDialogMsg);
                            }
                        });
                    }
                    monitor.done();
                }
            });
        } catch (InvocationTargetException e) {
            LOG.error(e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * run in UI thread
     * 
     * @param unusedNames
     *            a list of unused Component names
     * @param ome
     *            the current instance of the object mapping editor
     */
    protected void openDialog(final List<IComponentNamePO> unusedNames, 
            final ObjectMappingMultiPageEditor ome) {
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                CleanupComponentNamesDialog dialog = 
                    new CleanupComponentNamesDialog(
                        Plugin.getActiveWorkbenchWindowShell(), unusedNames);
                int returnCode = dialog.open();
                if (returnCode == Window.OK) {
                    DeleteTreeItemHandlerOMEditor.deleteMultipleElements(
                            dialog.getCheckedElements(), ome);
                }
            }
        });
    }

    /**
     * @param o
     *            the object to begin traversing at
     * @param ometcp
     *            the OMEditorTreeContentProvider
     * @return all elements of the tree
     */
    private List<Object> getAllElements(Object o,
            OMEditorTreeContentProvider ometcp) {
        List<Object> treeElements = new ArrayList<Object>();
        treeElements.add(o);
        if (ometcp.hasChildren(o)) {
            for (Object child : ometcp.getChildren(o)) {
                treeElements.addAll(getAllElements(child, ometcp));
            }
        }
        return treeElements;
    }
}
