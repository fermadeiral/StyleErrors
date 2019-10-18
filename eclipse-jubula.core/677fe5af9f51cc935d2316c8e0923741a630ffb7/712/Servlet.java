/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.alm.ui.i18n.Messages;
import org.eclipse.jubula.client.core.constants.Constants;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The dashboard replacing servlet */
public class Servlet extends HttpServlet {
    /** standard logging */
    static final Logger LOG = LoggerFactory.getLogger(Servlet.class);
    /** id */
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        handleDashboardRequestParameter(req.getParameterMap());
        PrintWriter writer;
        try {
            writer = resp.getWriter();
            writer.write(
                    NLS.bind(Messages.ServletResponseText, 
                            System.getProperty("user.name"))); //$NON-NLS-1$
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    /**
     * @param parameterMap
     *            the parameter map to handle
     */
    private void handleDashboardRequestParameter(
            Map<String, String[]> parameterMap) {
        String[] summaryIdParameter = parameterMap
                .get(Constants.DASHBOARD_SUMMARY_PARAM);
        String[] resultNodeParameter = parameterMap
                .get(Constants.DASHBOARD_RESULT_NODE_PARAM);
        Long nodeCount = 1L;
        if (resultNodeParameter != null) {
            try {
                nodeCount = Long.valueOf(resultNodeParameter[0]);
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        if (summaryIdParameter != null) {
            try {
                Long summaryId = Long.valueOf(summaryIdParameter[0]);
                openTestResultDetailAndSelectNode(summaryId, nodeCount);
            } catch (NumberFormatException nfe) {
                // ignore
            }
        }
    }

    /**
     * @param summaryId
     *            the summary id
     * @param nodeCount
     *            the index of the node to select
     */
    private void openTestResultDetailAndSelectNode(final Long summaryId,
            final Long nodeCount) {
        new OpenTestResultDetailsJob(
                NLS.bind(Messages.OpeningTestResultDetailsJobName, summaryId),
                summaryId, nodeCount).schedule(1000);
    }

    /**
     * @author BREDEX GmbH
     */
    private static class OpenTestResultDetailsJob extends UIJob {
        /**
         * the id of the summary
         */
        private Long m_summaryId;

        /**
         * the node to select by index (count)
         */
        private Long m_nodeCount;

        /**
         * Constructor
         * 
         * @param name
         *            the name of the job
         * @param nodeCount
         *            the node to select
         * @param summaryId
         *            the summary id to open the details for
         */
        public OpenTestResultDetailsJob(String name, Long summaryId,
                Long nodeCount) {
            super(name);
            m_summaryId = summaryId;
            m_nodeCount = nodeCount;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {
            final Command projectPropertiesCommand = CommandHelper
                    .getCommandService()
                    .getCommand(CommandIDs.OPEN_TEST_RESULT_DETAIL_COMMAND_ID);
            final Map<String, String> parameters = 
                    new HashMap<String, String>();
            parameters.put(
                CommandIDs.OPEN_TEST_RESULT_DETAIL_COMMAND_PARAMETER_SUMMARY_ID,
                m_summaryId.toString());
            parameters.put(
                CommandIDs.OPEN_TEST_RESULT_DETAIL_COMMAND_PARAMETER_NODE_ID,
                m_nodeCount.toString());
            CommandHelper.executeParameterizedCommand(ParameterizedCommand
                    .generateCommand(projectPropertiesCommand, parameters));
            return Status.OK_STATUS;
        }
    }
}