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
package org.eclipse.jubula.client.ui.rcp.views.problemview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jubula.client.core.businessprocess.CalcTypes;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.handlers.project.ProjectPropertiesHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Creates and returns resolutions for Jubula-related markers/problems.
 *
 * @author BREDEX GmbH
 * @created 02.06.2008
 */
public class JBMarkerResolutionGenerator implements IMarkerResolutionGenerator {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(JBMarkerResolutionGenerator.class);

    /**
     * Opens the editor for a specific test suite.
     * 
     * @author BREDEX GmbH
     * @created 06.06.2008
     */
    private static class OpenNodeInEditorMarkerResolution 
            implements IMarkerResolution {

        /** the name of the node for which to open the editor */
        private String m_nodeName;
        /** the GUID of the node for which to open the editor */
        private String m_nodeGUID;

        /**
         * Constructor
         * 
         * @param nodeName
         *            the name of the node
         * @param nodeGUID
         *            the GUID of the node
         */
        public OpenNodeInEditorMarkerResolution(String nodeName, 
                String nodeGUID) {
            m_nodeName = nodeName;
            m_nodeGUID = nodeGUID;
        }
        
        /** {@inheritDoc} */
        public String getLabel() {
            return NLS.bind(Messages.ProblemViewOpenEditor, m_nodeName);
        }

        /** {@inheritDoc} */
        public void run(IMarker marker) {
            INodePO node = NodePM.getNode(GeneralStorage.getInstance()
                    .getProject().getId(), m_nodeGUID);
            INodePO editorRoot = node;
            while (editorRoot != null && !(NodeBP.isEditable(editorRoot) 
                    && (editorRoot instanceof ITestSuitePO
                    || editorRoot instanceof ITestJobPO
                    || editorRoot instanceof ISpecTestCasePO))) {
                editorRoot = editorRoot.getParentNode();
            }
            AbstractOpenHandler.openEditorAndSelectNode(editorRoot, node);
        }
    }

    /**
     * Opens two editors to resolve an incompatibility
     * @author BREDEX GmbH
     */
    private static class TypeIncompResolver implements IMarkerResolution {

        /** The component name */
        private IComponentNamePO m_cN;
        
        /** The info - .get(2) and .get(3) contains the relevant guids */
        private List<String> m_info;
        
        /**
         * Constructor
         * @param cN the component name
         * @param info the info
         */
        private TypeIncompResolver(IComponentNamePO cN, List<String> info) {
            m_cN = cN;
            m_info = info;
        }
        
        /** {@inheritDoc} */
        public String getLabel() {
            return NLS.bind(Messages.ProblemViewOpenEditor, m_cN.getName());
        }

        /** {@inheritDoc} */
        public void run(IMarker marker) {
            INodePO node = NodePM.getNode(GeneralStorage.getInstance()
                    .getProject().getId(), m_info.get(2));
            if (node == null) {
                return;
            }
            AbstractOpenHandler.openEditorAndSelectNode(
                    node.getSpecAncestor(), node);
            if (m_cN.getTypeProblem().getProblemType().equals(
                    ProblemType.REASON_INCOMPATIBLE_MAP_TYPE)) {
                for (IAUTMainPO aut : GeneralStorage.getInstance().
                        getProject().getAutCont().getAutMainList()) {
                    if (aut.getGuid().equals(m_info.get(3))) {
                        AbstractOpenHandler.openEditor(aut);
                        break;
                    }
                }    
            } else {
                node = NodePM.getNode(GeneralStorage.getInstance()
                        .getProject().getId(), m_info.get(3));
                if (node == null) {
                    return;
                }
                AbstractOpenHandler.openEditorAndSelectNode(
                        node.getSpecAncestor(), node);
            }
        }
    }
    
    /**
     * Opens the editor for a specific test suite.
     * 
     * @author BREDEX GmbH
     * @created 06.06.2008
     */
    private static class OpenOMEditorMarkerResolution 
            implements IMarkerResolution {

        /** the displayed name of the test suite for which to open the editor */
        private String m_autName;

        /**
         * Constructor
         * 
         * @param autName the name of the aut
         */
        public OpenOMEditorMarkerResolution(String autName) {
            m_autName = autName;
        }
        
        /** {@inheritDoc} */
        public String getLabel() {
            return NLS.bind(Messages.ProblemViewOpenObjectMappingEditor, 
                    m_autName);
        }

        /**
         * {@inheritDoc}
         */
        public void run(IMarker marker) {
            List<ITestSuitePO> tsList = TestSuiteBP.getListOfTestSuites();
            for (ITestSuitePO ts : tsList) {
                if (ts.getAut().getName().equals(m_autName)) {
                    ObjectMappingMultiPageEditor editor =
                        (ObjectMappingMultiPageEditor)
                        AbstractOpenHandler.openEditor(ts.getAut());
                    editor.getSite().getPage().activate(editor);
                    break;
                }
            }
        }
    }

    /** {@inheritDoc} */
    public IMarkerResolution[] getResolutions(IMarker marker) {
        
        Object reasonInt;
        try {
            reasonInt = marker.getAttribute(Constants.JB_REASON);
            if (!(reasonInt instanceof Integer)
                    || reasonInt == null) {
                return new IMarkerResolution[0];
            }
            
            ProblemType type = ProblemType.values()[((Integer)reasonInt)
                                                    .intValue()];

            String objectName = 
                (String) marker.getAttribute(Constants.JB_OBJECT_NAME);
            String nodeGUID = 
                    (String) marker.getAttribute(Constants.JB_NODE_GUID);
            
            return getResolutions(type, objectName, nodeGUID);
        } catch (CoreException ce) {
            log.info(Messages
                    .ErrorOccurredWhileFindingResolutionsForProblemMarker, ce);
            return new IMarkerResolution[0];
        } 
    }

    /**
     * Returns a group of possible resolutions for a problem with the given
     * attributes.
     * 
     * @param type
     *            The type of the problem.
     * @param objectName
     *            name of the object (will be displayed in the quickfix dialog)
     * @param nodeGUID
     *            the GUID of the node, may be null
     * @return resolutions for a problem with the given type and object.
     */
    private IMarkerResolution[] getResolutions(ProblemType type, 
            String objectName, String nodeGUID) {

        switch (type) {
            case REASON_EMPTY_TESTSUITE:
            case REASON_NO_COMPTYPE:
            case REASON_TD_INCOMPLETE:
            case REASON_DEPRECATED_ACTION:
            case REASON_DEPRECATED_COMP:
            case REASON_MISSING_SPEC_TC:
            case REASON_UNUSED_TESTDATA:
            case REASON_IF_WITHOUT_TEST:
                return getOpenNodeInEditorResolutions(objectName, nodeGUID);
            case REASON_NO_AUT_FOR_PROJECT_EXISTS:
            case REASON_NO_AUTCONFIG_FOR_SERVER_EXIST:
            case REASON_NO_JAR_FOR_AUTCONFIG:
            case REASON_NO_SERVER_FOR_AUTCONFIG:
                return getNoAUTResolutions();
            case REASON_NO_TESTSUITE:
                return getNoTestSuiteResolutions();
            case REASON_OM_INCOMPLETE:
                return getOMIncompleteResolutions(objectName);
            case REASON_NO_SERVER_DEFINED:
                return getNoServerDefinedResolutions();
            case REASON_PROJECT_DOES_NOT_EXIST:
                return getMissingProjectResolutions();
            case REASON_INCOMPATIBLE_MAP_TYPE:
            case REASON_INCOMPATIBLE_USAGE_TYPE:
                return getIncompatibleTypeResolutions(nodeGUID);
            default:
                return new IMarkerResolution[0];
        }
    }

    /**
     * 
     * @return resolutions for a missing project.
     */
    private IMarkerResolution[] getMissingProjectResolutions() {
        return new IMarkerResolution[] { new IMarkerResolution() {

            public String getLabel() {
                return Messages.ProblemViewOpenReusedProjectSettings;
            }

            public void run(IMarker marker) {
                Command projectPropertiesCommand = CommandHelper
                        .getCommandService().getCommand(
                                RCPCommandIDs.PROJECT_PROPERTIES);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put(ProjectPropertiesHandler.SECTION_TO_OPEN,
                        Constants.REUSED_PROJECT_PROPERTY_ID);
                CommandHelper.executeParameterizedCommand(ParameterizedCommand
                        .generateCommand(projectPropertiesCommand, parameters));
            }
        } };
    }

    /**
     * @return resolutions for no AUT defined.
     */
    private IMarkerResolution[] getNoAUTResolutions() {
        return new IMarkerResolution[] { new IMarkerResolution() {

            public String getLabel() {
                return Messages.ProblemViewOpenAutSettings;
            }

            public void run(IMarker marker) {
                Command projectPropertiesCommand = CommandHelper
                        .getCommandService().getCommand(
                                RCPCommandIDs.PROJECT_PROPERTIES);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put(ProjectPropertiesHandler.SECTION_TO_OPEN,
                        Constants.AUT_PROPERTY_ID);
                CommandHelper.executeParameterizedCommand(ParameterizedCommand
                        .generateCommand(projectPropertiesCommand, parameters));
            }
        } };
    }
    
    /**
     *
     * @return resolutions for no AutStarter host defined in workspace 
     *         configuration.
     */ 
    private IMarkerResolution[] getNoServerDefinedResolutions() {
        return new IMarkerResolution[] {
            new IMarkerResolution() {

                public String getLabel() {
                    return Messages.ProblemViewOpenAutAgentPreferences;
                }

                public void run(IMarker marker) {
                    PreferenceManager pm = Plugin.getDefault()
                        .getWorkbench().getPreferenceManager();
                    for (Object obj : pm.getElements(
                            PreferenceManager.PRE_ORDER)) {
                        PreferenceNode node = (PreferenceNode)obj;
                        if (node.getId().equals(
                                Constants.JB_PREF_PAGE_AUTAGENT)) {
                            PreferenceDialog dialog = PreferencesUtil.
                                    createPreferenceDialogOn(
                                    null, Constants.JB_PREF_PAGE_AUTAGENT, 
                                    null, null);
                            DialogUtils.setWidgetNameForModalDialog(dialog);
                            dialog.open();
                            break;
                        }
                    }
                }
                
            }
        };
    }

    /**
     * @param nodeName displayed name of the node for the quickfix dialog
     * @return resolutions for incomplete object mapping.
     */
    private IMarkerResolution[] getOMIncompleteResolutions(String nodeName) {
        return new IMarkerResolution[] { new OpenOMEditorMarkerResolution(
                nodeName) };
    }

    /**
     * @param nodeName the name of the node for the quickfix
     * @param nodeGUID the guid of the node for the quickfix
     * @return resolutions for problems that require opening a node in it's 
     * corresponding editor.
     */
    private IMarkerResolution[] getOpenNodeInEditorResolutions(String nodeName,
            String nodeGUID) {
        return new IMarkerResolution[] { new OpenNodeInEditorMarkerResolution(
                nodeName, nodeGUID) };
    }
    
    /**
     * Returns an incompatible type resolver
     * @param guid the guid
     * @return the resolver
     */
    private IMarkerResolution[] getIncompatibleTypeResolutions(String guid) {
        IComponentNamePO cN = CompNameManager.getInstance().
                getResCompNamePOByGuid(guid);
        if (cN == null || cN.getTypeProblem() == null) {
            return new IMarkerResolution[] {};
        }
        CalcTypes calc = new CalcTypes(CompNameManager.getInstance(), null);
        calc.calculateTypes();
        List<String> info = calc.getProblemInfo(guid);
        // info.get(2) is the last type change place, 3 is the conflict source (see CalcTypes)
        if (info == null || info.size() != 4) {
            return new IMarkerResolution[] {};
        }
        return new IMarkerResolution[] {new TypeIncompResolver(cN, info)};
    }
 
    /**
     * 
     * @return resolutions for no test suite defined.
     */
    private IMarkerResolution[] getNoTestSuiteResolutions() {
        return new IMarkerResolution[] { new IMarkerResolution() {

            public String getLabel() {
                return Messages.ProblemViewCreateTestSuite;
            }

            public void run(IMarker marker) {
                CommandHelper
                        .executeCommand(RCPCommandIDs.NEW_TESTSUITE);
            }
        }
        };
    }
}
