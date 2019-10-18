/*******************************************************************************
 * Copyright (c) 2004, 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.search.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.businessprocess.treeoperations.CheckIfCAPisDeprecated;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.NodeNameUtil;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.NodeSearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;


/**
 * Search query for deprecated modules.
 * @author BREDEX GmbH
 * @created Aug 9, 2010
 */
public class DeprecatedModulesQuery extends AbstractQuery {
    
    /**
     */
    public DeprecatedModulesQuery() {
        super(null);
    }

    /**
     * Search in the whole project or in selected nodes for keywords using the
     * {@link TextFinder} depending on the {@link SearchOptions} given
     * to the constructor.
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor) {
        setMonitor(monitor);
        final List<SearchResultElement> deprecatedModules = 
                new ArrayList<SearchResultElement>();
        IProjectPO currentProject = GeneralStorage.getInstance().getProject();
        CheckIfCAPisDeprecated op = new CheckIfCAPisDeprecated();
        TreeTraverser traverser =
                new TreeTraverser(currentProject, op, true, true);
        traverser.traverse(true);
        Set<INodePO> deprecatedNodes = op.getDeprecatedNodes();
        for (INodePO iNodePO : deprecatedNodes) {
            String parentName = StringConstants.EMPTY;
            INodePO parentNode = iNodePO.getSpecAncestor();
            if (parentNode == null) {
                parentNode = iNodePO.getParentNode();
            }
            if (parentNode != null) {
                parentName = parentNode.getName();
            }
            String nodeName = iNodePO.getName();
            if (iNodePO instanceof IExecTestCasePO) {
                nodeName =
                        NodeNameUtil.getText((IExecTestCasePO) iNodePO, false);
            }
            deprecatedModules.add(new SearchResultElement<Long>(
                    NLS.bind(Messages.SearchResultPageElementLabel,
                            new Object[] { parentName, nodeName }),
                    iNodePO.getId(), GeneralLabelProvider.getImageImpl(iNodePO),
                    new NodeSearchResultElementAction(), iNodePO.getComment()));
        }
        
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        setSearchResult(deprecatedModules);
        monitor.done();
        return Status.OK_STATUS;
    }


    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTimestamp());
        sb.append(StringConstants.COLON);
        sb.append(StringConstants.SPACE);
        sb.append(Messages.UIJobSearchingDeprecatedModules);
        return sb.toString();
    }
}