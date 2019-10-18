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
package org.eclipse.jubula.client.archive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.archive.converter.json.AddTimeoutToCAPConverter;
import org.eclipse.jubula.client.archive.converter.json.RemoveDoubledUniqueIds;
import org.eclipse.jubula.client.archive.converter.utils.AbstractConverter;
import org.eclipse.jubula.client.archive.converter.utils.IConverter;
import org.eclipse.jubula.client.archive.dto.AutConfigDTO;
import org.eclipse.jubula.client.archive.dto.AutDTO;
import org.eclipse.jubula.client.archive.dto.CapDTO;
import org.eclipse.jubula.client.archive.dto.CategoryDTO;
import org.eclipse.jubula.client.archive.dto.CheckActivatedContextDTO;
import org.eclipse.jubula.client.archive.dto.CheckAttributeDTO;
import org.eclipse.jubula.client.archive.dto.CheckConfigurationDTO;
import org.eclipse.jubula.client.archive.dto.CommentDTO;
import org.eclipse.jubula.client.archive.dto.ComponentNameDTO;
import org.eclipse.jubula.client.archive.dto.ComponentNamesPairDTO;
import org.eclipse.jubula.client.archive.dto.ConditionalStatementDTO;
import org.eclipse.jubula.client.archive.dto.DataRowDTO;
import org.eclipse.jubula.client.archive.dto.DefaultEventHandlerDTO;
import org.eclipse.jubula.client.archive.dto.EventTestCaseDTO;
import org.eclipse.jubula.client.archive.dto.ExecCategoryDTO;
import org.eclipse.jubula.client.archive.dto.ExportInfoDTO;
import org.eclipse.jubula.client.archive.dto.IterateDTO;
import org.eclipse.jubula.client.archive.dto.MapEntryDTO;
import org.eclipse.jubula.client.archive.dto.MonitoringValuesDTO;
import org.eclipse.jubula.client.archive.dto.NamedTestDataDTO;
import org.eclipse.jubula.client.archive.dto.NodeDTO;
import org.eclipse.jubula.client.archive.dto.ObjectMappingDTO;
import org.eclipse.jubula.client.archive.dto.ObjectMappingProfileDTO;
import org.eclipse.jubula.client.archive.dto.OmCategoryDTO;
import org.eclipse.jubula.client.archive.dto.OmEntryDTO;
import org.eclipse.jubula.client.archive.dto.ParamDescriptionDTO;
import org.eclipse.jubula.client.archive.dto.ProjectDTO;
import org.eclipse.jubula.client.archive.dto.RefTestCaseDTO;
import org.eclipse.jubula.client.archive.dto.RefTestSuiteDTO;
import org.eclipse.jubula.client.archive.dto.ReportingRuleDTO;
import org.eclipse.jubula.client.archive.dto.ReusedProjectDTO;
import org.eclipse.jubula.client.archive.dto.TDManagerDTO;
import org.eclipse.jubula.client.archive.dto.TechnicalNameDTO;
import org.eclipse.jubula.client.archive.dto.TestCaseDTO;
import org.eclipse.jubula.client.archive.dto.TestDataCategoryDTO;
import org.eclipse.jubula.client.archive.dto.TestJobDTO;
import org.eclipse.jubula.client.archive.dto.TestSuiteDTO;
import org.eclipse.jubula.client.archive.dto.TestresultSummaryDTO;
import org.eclipse.jubula.client.archive.dto.UsedToolkitDTO;
import org.eclipse.jubula.client.archive.dto.WhileDTO;
import org.eclipse.jubula.client.archive.i18n.Messages;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP.ToolkitPluginError;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP.ToolkitPluginError.ERROR;
import org.eclipse.jubula.client.core.model.IALMReportingRulePO;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICheckConfContPO;
import org.eclipse.jubula.client.core.model.ICheckConfPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.ICondStructPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IObjectMappingProfilePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.IUsedToolkitPO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.TestResultSummaryPM;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.ReportRuleType;
import org.eclipse.jubula.client.core.utils.TrackingUnit;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;
import org.eclipse.jubula.tools.internal.objects.MonitoringValue;
import org.eclipse.jubula.tools.internal.version.IVersion;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.osgi.util.NLS;

/** @author BREDEX GmbH */
public class JsonImporter {
    /** Remember which instance belongs to the id used in the DTO element */
    private Map<String, IAUTMainPO> m_autRef = 
        new HashMap<String, IAUTMainPO>();
    
    /** Remember which instance belongs to the id/guid used in the DTO element */
    private Map<String, ISpecTestCasePO> m_tcRef = 
        new HashMap<String, ISpecTestCasePO>();
    
    /** Mapping between old and new GUIDs. Only used when assigning new GUIDs */
    private Map<String, String> m_oldToNewGuids = 
        new HashMap<String, String>();

    /** The progress monitor for this importer. */
    private IProgressMonitor m_monitor;

    /** whether to skip the import of tracked data */
    private boolean m_skipTrackingInformation = false;
    
    /** The import output. */
    private IProgressConsole m_io;

    /** */
    private ExportInfoDTO m_exportInfo;
    
    /**
     * @param monitor 
     * @param io console
     * @param skipTrackingInformation 
     * @param exportInfo the information about the 
     */
    public JsonImporter(IProgressMonitor monitor, IProgressConsole io,
            boolean skipTrackingInformation, ExportInfoDTO exportInfo) {
        m_monitor = monitor;
        m_io = io;
        m_skipTrackingInformation = skipTrackingInformation;
        m_exportInfo = exportInfo;
    }
    
    /**
     * @param projectDTO storage of the project
     * @param assignNewGuid <code>true</code> if the project and all subnodes
     *                      should be assigned new GUIDs. Otherwise 
     *                      <code>false</code>.
     * @param assignNewVersion if <code>true</code> the project will have
     *                      a new project version number, otherwise it will
     *                      have the stored project version from the dto.
     * @param paramNameMapper 
     * @param compNameCache 
     * @return IProjectPO 
     * @throws InvalidDataException
     * @throws InterruptedException
     * @throws JBVersionException 
     * @throws ToolkitPluginException 
     */
    public IProjectPO createProject(ProjectDTO projectDTO,
            boolean assignNewGuid, boolean assignNewVersion,
            IParamNameMapper paramNameMapper, 
            IWritableComponentNameCache compNameCache)
                    throws InvalidDataException, InterruptedException,
                    JBVersionException, ToolkitPluginException {

        m_monitor.subTask(Messages.ImportJsonImportReqCheck);
        documentRequiredProjects(projectDTO);
        checkSupportedToolkits(projectDTO.getUsedToolkits());
        checkUsedToolkits(projectDTO);
       
        
        applyConverters(projectDTO);
        
        IProjectPO proj = initProject(projectDTO, assignNewGuid,
                assignNewVersion);
        EntityManager attrDescSession = Persistor.instance().openSession();
        try {
            fillProject(proj, projectDTO, attrDescSession, assignNewGuid,
                    paramNameMapper, compNameCache);
        } finally {         
            Persistor.instance().dropSession(attrDescSession);
        }
        return proj;
    }
    
    /**
     * @param projectDTO the project to convert
     */
    private void applyConverters(ProjectDTO projectDTO) {
        List<AbstractConverter<ProjectDTO>> converters = 
                new ArrayList<AbstractConverter<ProjectDTO>>(1);
        converters.add(new RemoveDoubledUniqueIds(m_exportInfo));
        converters.add(new AddTimeoutToCAPConverter(m_exportInfo));
        
        for (IConverter<ProjectDTO> converter : converters) {
            converter.convert(projectDTO);
        }
    }

    /**
     * Check, whether the supported toolkits are supported.
     * @param usedToolkits collection of the used toolkits
     * @throws ToolkitPluginException if an unsupported toolkit is referenced
     */
    private void checkSupportedToolkits(
            List<UsedToolkitDTO> usedToolkits) throws ToolkitPluginException {
        List<String> toolkitIds = ComponentBuilder.getInstance()
                .getLevelToolkitIds();
        
        StringBuilder errorMsg = new StringBuilder();
        for (UsedToolkitDTO usedToolkit : usedToolkits) {
         
            if (!ComponentBuilder.getInstance().getLevelToolkitIds()
                    .contains(usedToolkit.getName())) {
                try {
                    ToolkitConstants.LEVEL_TOOLKIT.equals(ToolkitSupportBP
                                .getToolkitLevel(usedToolkit.getName()));
                } catch (ToolkitPluginException e) {
                    errorMsg.append(StringConstants.NEWLINE);
                    errorMsg.append(StringConstants.TAB);
                    errorMsg.append(usedToolkit.getName());
                }
            }
        }
        if (StringUtils.isNotBlank(errorMsg.toString())) {
            throw new ToolkitPluginException(NLS
                    .bind(Messages.UnsupportedToolkits, errorMsg.toString()));
        }
    }
    
    /**
     * @param dto ProjectDTO storage of the project
     * @param assignNewGuid <code>true</code> if the project and all subnodes
     *                      should be assigned new GUIDs. Otherwise 
     *                      <code>false</code>.
     * @param assignNewVersion if <code>true</code> the project will have
     *                      a new project version number, otherwise it will
     *                      have the stored project version from the dto.
     * @return a new IProjectPO
     */
    private IProjectPO initProject(ProjectDTO dto, boolean assignNewGuid,
            boolean assignNewVersion) {
        m_monitor.subTask(Messages.ImportJsonImportProjectInit);
        IProjectPO proj = null;
        if (dto.getUuid() != null) {
            
            Integer majorProjVersion = 1;
            Integer minorProjVersion = 0;
            Integer microProjVersion = null;
            String postFixProjVersion = null;
            
            if (!assignNewVersion) {
                majorProjVersion = dto.getMajorProjectVersion();
                minorProjVersion = dto.getMinorProjectVersion();
                microProjVersion = dto.getMicroProjectVersion();
                postFixProjVersion = dto.getProjectVersionQualifier();
            }
            
            if (assignNewGuid) {
                proj = NodeMaker.createProjectPO(
                        IVersion.JB_CLIENT_METADATA_VERSION, majorProjVersion,
                        minorProjVersion, microProjVersion, postFixProjVersion);
            } else {
                proj = NodeMaker.createProjectPO(
                        IVersion.JB_CLIENT_METADATA_VERSION, majorProjVersion,
                        minorProjVersion, microProjVersion, postFixProjVersion,
                        dto.getUuid());
            }
            ProjectNameBP.getInstance().setName(proj.getGuid(), dto.getName(),
                    false);
        } else {
            proj = NodeMaker.createProjectPO(dto.getName(), IVersion
                .JB_CLIENT_METADATA_VERSION);
        }
        if (assignNewGuid) {
            m_oldToNewGuids.put(dto.getUuid(), proj.getGuid());
        }
        return proj;
    }
    

    
    /**
     * @param proj The project that will be filled.
     * @param attrDescSession The attribute session.
     * @param dto ProjectDTO
     * @param assignNewGuid <code>true</code> if the project and all subnodes
     *                      should be assigned new GUIDs. Otherwise 
     *                      <code>false</code>.
     * @param mapper a mapper
     * @param cNC the component name cache to use during project creation
     * @throws InvalidDataException
     * @see createProject(Project xml, boolean assignNewGuid,
     *   IParamNameMapper mapper)
     */
    private void fillProject(IProjectPO proj, ProjectDTO dto,
        EntityManager attrDescSession, boolean assignNewGuid, 
        IParamNameMapper mapper, IWritableComponentNameCache cNC)
        throws InterruptedException, InvalidDataException {
        
        m_monitor.beginTask(Messages.ImportFileBPImporting,
                getWorkToImport(dto.getCategories().size()));

        m_monitor.subTask(Messages.ImportJsonImportProjectLoad);
        IProjectPropertiesPO projectProperties = 
                fillProjectProperties(proj, dto);
        if (dto.getTestResultDetailsCleanupInterval() != null) {
            proj.setTestResultCleanupInterval(dto
                    .getTestResultDetailsCleanupInterval());
        } else {
            proj.setTestResultCleanupInterval(IProjectPO.CLEANUP_DEFAULT);
        }
        m_monitor.worked(1);
        
        for (ReusedProjectDTO reusedProj : dto.getReusedProjects()) {
            proj.addUsedProject(createReusedProject(reusedProj));
        }
        m_monitor.worked(1);
        for (AutDTO aut : dto.getAuts()) {
            proj.addAUTMain(createAUTMain(aut, assignNewGuid));
        }
        m_monitor.worked(1);
        for (TestDataCategoryDTO testDataCategory 
                : dto.getTestDataCategories()) {
            ImportExportUtil.checkCancel(m_monitor);
            proj.getTestDataCubeCont().addCategory(createTestDataCategory(
                    testDataCategory, assignNewGuid, mapper));
        }
        m_monitor.worked(1);
        for (NamedTestDataDTO testDataCube : dto.getNamedTestDatas()) {
            ImportExportUtil.checkCancel(m_monitor);
            proj.getTestDataCubeCont().addTestData(createTestDataCube(
                    testDataCube, assignNewGuid, mapper));
        }
        for (NodeDTO node : dto.getCategories()) {
            m_monitor.worked(1);
            ImportExportUtil.checkCancel(m_monitor);
            if (node instanceof CategoryDTO) {
                proj.getSpecObjCont().addNode(createCategory(proj,
                        (CategoryDTO)node, assignNewGuid, mapper));
            } else if (node instanceof TestCaseDTO) {
                proj.getSpecObjCont().addNode(createTestCaseBase(proj,
                        (TestCaseDTO)node, assignNewGuid, mapper));
            }
        }
        if (assignNewGuid) {
            generateRefTestCase(dto.getCategories(), proj, assignNewGuid);
        }
        handleTestSuitesAndTestJobsAndCategories(proj, dto, assignNewGuid);
        m_monitor.worked(1);
        for (CheckConfigurationDTO dtoConf : dto.getCheckConfigurations()) {
            initCheckConf(dtoConf, projectProperties.getCheckConfCont());
        }
        m_monitor.worked(1);
        createComponentNames(dto, proj, cNC, assignNewGuid);
        m_monitor.worked(1);
    }
    
    /**
     * @param nodes test cases or steps
     * @param proj currently projectPO
     * @param assignNewGuid need we a new Uuid or not
     * @throws InvalidDataException
     */
    private void generateRefTestCase(List<NodeDTO> nodes, IProjectPO proj,
            boolean assignNewGuid) throws InvalidDataException {
        
        for (NodeDTO node : nodes) {
            if (node instanceof CategoryDTO) {
                generateRefTestCase(((CategoryDTO)node).getNodes(), proj,
                        assignNewGuid);
            } else if (node instanceof TestCaseDTO) {
                generateRefTestCase((TestCaseDTO)node, proj, null,
                        assignNewGuid);
            }
        }
    }
    
    /**
     * @param dto test case dto
     * @param proj currently projectPO
     * @param stcPo if assignNewGuid is false then it is need the spec test case
     * @param newGuid need we a new Guid or not
     * @throws InvalidDataException
     */
    private void generateRefTestCase(TestCaseDTO dto, IProjectPO proj,
            ISpecTestCasePO stcPo, boolean newGuid)
                    throws InvalidDataException {

        try {
            ISpecTestCasePO tc = !newGuid && stcPo != null ? stcPo
                    : m_tcRef.get(m_oldToNewGuids.get(dto.getUuid()));
            for (NodeDTO stepDto : dto.getTestSteps()) {
                if (stepDto instanceof CapDTO) {
                    tc.addNode(createCap(proj, (CapDTO)stepDto, newGuid));
                } else if (stepDto instanceof RefTestCaseDTO) {
                    tc.addNode(createExecTestCase(
                        proj, (RefTestCaseDTO)stepDto, newGuid));
                } else if (stepDto instanceof CommentDTO) {
                    tc.addNode(createComment((CommentDTO) stepDto,
                            newGuid));
                } else if (stepDto instanceof ConditionalStatementDTO) {
                    tc.addNode(createConditionalStatement(
                            (ConditionalStatementDTO)stepDto, proj, newGuid));
                } else if (stepDto instanceof WhileDTO) {
                    tc.addNode(createWhile(proj, (WhileDTO)stepDto, newGuid));
                } else if (stepDto instanceof IterateDTO) {
                    tc.addNode(createIterate(proj, (IterateDTO)stepDto,
                            newGuid));
                }
            }
            for (EventTestCaseDTO evTcDto : dto.getEventTestcases()) {
                tc.addEventTestCase(createEventExecTestCase(
                    proj, tc, evTcDto, newGuid));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * @param dto test case dto
     * @param proj currently projectPO
     * @param newGuid need we a new Guid or not
     * @return an condition
     * @throws InvalidDataException
     */
    private IConditionalStatementPO createConditionalStatement(
            ConditionalStatementDTO dto, IProjectPO proj,
            boolean newGuid) throws InvalidDataException {
        IConditionalStatementPO con = getConditionalStatement(dto, newGuid);
        con.setNegate(dto.isNegated());
        con.setGenerated(dto.getGenerated());
        con.setComment(dto.getComment());
        con.setTaskId(dto.getTaskId());
        con.setDescription(dto.getDescription());
        con.setActive(dto.isActive());
        
        List<NodeDTO> nodes = dto.getNodes();
        
        if (nodes != null && nodes.size() == 3) {
            fillContainer(nodes.get(0), con.getCondition(), proj, newGuid);
            fillContainer(nodes.get(1), con.getThenBranch(),
                    proj, newGuid);
            fillContainer(nodes.get(2), con.getElseBranch(),
                    proj, newGuid);
        }
        return con;
    }
    
    /**
     * @param dto container dto
     * @param po container po
     * @param proj project
     * @param assignNewGuid 
     * @throws InvalidDataException
     */
    private void fillContainer(NodeDTO dto, IAbstractContainerPO po,
           IProjectPO proj, boolean assignNewGuid) {
        po.setGenerated(dto.getGenerated());
        po.setComment(dto.getComment());
        po.setTaskId(dto.getTaskId());
        po.setDescription(dto.getDescription());
        for (NodeDTO node : dto.getNodes()) {
            ImportExportUtil.checkCancel(m_monitor);
            if (node instanceof CapDTO) {
                po.addNode(createCap(proj, (CapDTO)node, assignNewGuid));
            } else if (node instanceof RefTestCaseDTO) {
                po.addNode(createExecTestCase(
                    proj, (RefTestCaseDTO)node, assignNewGuid));
            } else if (node instanceof CommentDTO) {
                po.addNode(createComment((CommentDTO) node, assignNewGuid));
            }
        }
    }
    
    /**
     * @param dto 
     * @param assignNewGuid 
     * @return condition
     */
    private IConditionalStatementPO getConditionalStatement(
            ConditionalStatementDTO dto, boolean assignNewGuid) {
        if (dto.getUuid() != null && !assignNewGuid) {
            return NodeMaker.createConditionalStatementPO(dto.getName(),
                    dto.getUuid());
        }
        return NodeMaker.createConditionalStatementPO(dto.getName());
    }

    /**
     * Creates the instance of the persistent object which is defined by the
     * XML element used as parameter. The method generates all dependent objects
     * as well.
     * @param dtoProj the XML-Project
     * @param proj the IProjectPO
     * @param compNameCache The cache for storing and retrieving 
     *                      Component Names in memory.
     * @param assignNewGuid <code>true</code> if the project and all subnodes
     *      should be assigned new GUIDs. Otherwise <code>false</code>.
     */
    private void createComponentNames(ProjectDTO dtoProj, IProjectPO proj, 
            IWritableComponentNameCache compNameCache, boolean assignNewGuid) {
        
        final List<ComponentNameDTO> componentNamesList =
                dtoProj.getComponentNames();
        final Map<String, String> oldToNewGUID = new HashMap<String, String>(
                componentNamesList.size());
        Set<IComponentNamePO> createdCompNames = 
            new HashSet<IComponentNamePO>();
        for (ComponentNameDTO compName : componentNamesList) {
            String guid = compName.getUuid();
            if (assignNewGuid) {
                final String newGuid = PersistenceUtil.generateUUID();
                oldToNewGUID.put(guid, newGuid);
                guid = newGuid;
            }
            final String name = compName.getCompName();
            final String type = compName.getCompType();
            final String creationContext = compName.getCreationContext();
            final CompNameCreationContext ctx = CompNameCreationContext
                .forName(creationContext);
            final IComponentNamePO componentNamePO = PoMaker
                .createComponentNamePO(guid, name, type, ctx, proj.getId());
            componentNamePO.setReferencedGuid(compName.getRefUuid());
            createdCompNames.add(componentNamePO);
            compNameCache.addCompNamePO(componentNamePO);
        }
        
        if (assignNewGuid) {
            for (IComponentNamePO createdName : createdCompNames) {
                String newGuid = oldToNewGUID.get(
                        createdName.getReferencedGuid());
                if (newGuid != null) {
                    createdName.setReferencedGuid(newGuid);
                }
            }
            ImportExportUtil.switchCompNamesGuids(proj, oldToNewGUID);
        }
    }
    
    /**
     * 
     * @param dto the DTO to 
     * @param assignNewUuid if there should be a new Uuid
     * @return a {@link ICommentPO}
     */
    private ICommentPO createComment(CommentDTO dto,
            boolean assignNewUuid) {
        ICommentPO comment;
        if (dto.getUuid() != null && !assignNewUuid) {
            comment = NodeMaker.createCommentPO(dto.getName(), dto.getUuid());
        } else {
            comment = NodeMaker.createCommentPO(dto.getName());
        }
        comment.setActive(dto.isActive());
        return comment;
    }
    
    /**
     * @param componentType component type name
     * @return true if the component has a default mapping and therefore has no
     *         component name
     */
    private boolean componentHasDefaultMapping(String componentType) {
        Component component = ComponentBuilder.getInstance()
                .getCompSystem().findComponent(componentType);
        if (component.isConcrete()) {
            return ((ConcreteComponent)component).hasDefaultMapping();
        }        
        return false;
    }

    /**
     * @param proj The project to which the test result summaries belongs.
     * @param trsListDtos The DTO element for the test result summaries
     * @param monitor 
     * @throws InterruptedException 
     */
    public void initTestResultSummaries(IProgressMonitor monitor,
            List<TestresultSummaryDTO> trsListDtos, IProjectPO proj)
                    throws InterruptedException {
        monitor.beginTask(Messages.ImportFileBPImporting, trsListDtos.size());
        List<ITestResultSummaryPO> summaries =
                new ArrayList<ITestResultSummaryPO>(ImportExportUtil.PAGE_SIZE);
        int countOfTestResult = 0;
        for (TestresultSummaryDTO dto : trsListDtos) {
            ImportExportUtil.checkCancel(m_monitor);
            countOfTestResult++;
            monitor.worked(1);
            monitor.subTask(Messages.ImportJsonImportResult + countOfTestResult
                    + StringConstants.SLASH + trsListDtos.size());
            
            ITestResultSummaryPO summary = PoMaker.createTestResultSummaryPO();
            summary.setInternalProjectGuid(proj.getGuid());
            
            fillTestresultSummary(summary, dto);
            
            List<MonitoringValuesDTO> tmpList = dto.getMonitoringValues();
            Map<String, IMonitoringValue> tmpMap = 
                new HashMap<String, IMonitoringValue>();            
            for (int countOfValue = 0; countOfValue
                    < tmpList.size(); countOfValue++) {
                MonitoringValuesDTO tmpMon = tmpList.get(countOfValue);
                MonitoringValue tmp = new MonitoringValue();
                tmp.setCategory(tmpMon.getCategory());
                tmp.setSignificant(tmpMon.isSignificant());
                tmp.setType(tmpMon.getType());
                tmp.setValue(tmpMon.getValue());
                tmpMap.put(tmpMon.getKey(), tmp);                
                
            }            
            summary.setMonitoringValues(tmpMap);
            if (!TestResultSummaryPM.doesTestResultSummaryExist(summary)) {
                summaries.add(summary);
            }
            
            if (summaries.size() == ImportExportUtil.PAGE_SIZE) {
                TestResultSummaryPM.storeTestResultSummariesInDB(summaries);
                summaries.clear();
            }
        }
        
        if (!summaries.isEmpty()) {
            TestResultSummaryPM.storeTestResultSummariesInDB(summaries);
        }
    }
    
    /**
     * @param po empty test summary object
     * @param dto test summary dto
     */
    public void fillTestresultSummary(ITestResultSummaryPO po,
            TestresultSummaryDTO dto) {
        po.setAlmReportStatus(dto.getAlmStatus());
        po.setAutAgentName(dto.getAutAgentName());
        po.setAutCmdParameter(dto.getAutCmdParameter());
        po.setAutConfigName(dto.getAutConfigName());
        po.setAutHostname(dto.getAutHostname());
        po.setAutId(dto.getAutId());
        po.setAutName(dto.getAutName());
        po.setAutOS(dto.getAutOS());
        po.setAutToolkit(dto.getAutToolkit());
        po.setCommentDetail(dto.getCommentDetail());
        po.setCommentTitle(dto.getCommentTitle());
        po.setInternalAutConfigGuid(dto.getAutConfigUuid());
        po.setInternalAutGuid(dto.getAutUuid());
        po.setInternalMonitoringId(dto.getMonitoringId());
        po.setInternalProjectGuid(dto.getProjectUuid());
        po.setInternalProjectID(dto.getProjectID());
        po.setInternalTestJobGuid(dto.getTestJobUuid());
        po.setInternalTestsuiteGuid(dto.getTestsuiteUuid());
        po.setMonitoringValue(dto.getMonitoringValue());
        po.setMonitoringValueType(dto.getMonitoringValueType());
        po.setProjectMajorVersion(dto.getProjectMajorVersion());
        po.setProjectMinorVersion(dto.getProjectMinorVersion());
        po.setProjectMicroVersion(dto.getProjectMicroVersion());
        po.setProjectName(dto.getProjectName());
        po.setProjectVersionQualifier(dto.getProjectVersionQualifier());
        po.setReportWritten(dto.isBlobWritten());
        po.setTestJobName(dto.getTestJobName());
        po.setTestJobStartTime(dto.getTestJobStartTime());
        po.setTestsuiteDate(dto.getTestsuiteDate());
        po.setTestsuiteDuration(dto.getTestsuiteDuration());
        po.setTestsuiteEndTime(dto.getTestsuiteEndTime());
        po.setTestsuiteEventHandlerTeststeps(
                dto.getTestsuiteEventHandlerTeststeps());
        po.setTestsuiteExecutedTeststeps(dto.getTestsuiteExecutedTeststeps());
        po.setTestsuiteExpectedTeststeps(dto.getTestsuiteExpectedTeststeps());
        po.setTestsuiteFailedTeststeps(dto.getTestsuiteFailedTeststeps());
        po.setTestsuiteName(dto.getTestsuiteName());
        po.setTestsuiteStartTime(dto.getTestsuiteStartTime());
        po.setTestsuiteStatus(dto.getTestsuiteStatus());
    }

    /**
     * @param dtoConf The source of the check configuration
     * @param checkConfCont The destiny of the check configuration (will be persisted)
     */
    private void initCheckConf(CheckConfigurationDTO dtoConf,
            ICheckConfContPO checkConfCont) {
        if (dtoConf.getSeverity().matches("(0|1|2|3)")) { //$NON-NLS-1$
            return; // its an old exported xml, just don't create the conf
        }
        ICheckConfPO chkConf = checkConfCont.createCheckConf();
        chkConf.setSeverity(dtoConf.getSeverity());
        chkConf.setActive(dtoConf.isActivated());
        
        for (CheckAttributeDTO dtoAttr : dtoConf.getCheckAttributes()) {
            chkConf.getAttr().put(dtoAttr.getName(), dtoAttr.getValue());
        }
        for (CheckActivatedContextDTO dtoCxt
                : dtoConf.getCheckActivatedContextes()) {
            boolean active = dtoCxt.isActive();
            chkConf.getContexts().put(dtoCxt.getClazz(), active);
        }
        
        checkConfCont.addCheckConf(dtoConf.getCheckId(), chkConf);
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param dto Abstraction of the DTO element
     * @param assignNewGuid <code>true</code> if the category and all subnodes
     *              should be assigned new GUIDs. Otherwise <code>false</code>.
     * @param mapper mapper to resolve param names
     * @return a persistent object generated from the information in the DTO
     *              element
     * @throws InvalidDataException if some data is invalid when constructing
     *              an object. This should not happen for exported project,
     *              but may happen when someone generates DTO project description
     *              outside of GUIdancer.
     */
    private INodePO createCategory(IProjectPO proj, CategoryDTO dto,
            boolean assignNewGuid, IParamNameMapper mapper)
                throws InvalidDataException {
        ICategoryPO cat;
        if (dto.getUuid() != null && !assignNewGuid) {
            cat = NodeMaker.createCategoryPO(dto.getName(), dto.getUuid());
        } else {
            cat = NodeMaker.createCategoryPO(dto.getName());
        }
        cat.setGenerated(dto.getGenerated());
        cat.setComment(dto.getComment());
        cat.setTaskId(dto.getTaskId());
        
        for (NodeDTO node : dto.getNodes()) {
            ImportExportUtil.checkCancel(m_monitor);
            if (node instanceof CategoryDTO) {
                cat.addNode(createCategory(proj, (CategoryDTO)node,
                        assignNewGuid, mapper));
            } else if (node instanceof TestCaseDTO) {
                cat.addNode(createTestCaseBase(proj, (TestCaseDTO)node,
                        assignNewGuid, mapper));
            }
        }
        return cat;
    }

    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param dto Abstraction of the DTO element
     * @param assignNewGuid <code>true</code> if the testcase 
     *              should be assigned a new GUID. Otherwise <code>false</code>.
     * @param mapper mapper to resolve param names
     * @return a persistent object generated from the information in the DTO element
     * @throws InvalidDataException 
     */
    private ISpecTestCasePO createTestCaseBase(IProjectPO proj,
        TestCaseDTO dto, boolean assignNewGuid, IParamNameMapper mapper)
                throws InvalidDataException {
        
        ISpecTestCasePO tc;
        if (assignNewGuid) {
            tc = NodeMaker.createSpecTestCasePO(dto.getName());
            m_tcRef.put(tc.getGuid(), tc);
            m_oldToNewGuids.put(dto.getUuid(), tc.getGuid());
        } else {
            tc = NodeMaker.createSpecTestCasePO(dto.getName(), dto.getUuid());
            m_tcRef.put(dto.getUuid(), tc);
        }
        tc.setComment(dto.getComment());
        tc.setDescription(dto.getDescription());
        tc.setGenerated(dto.getGenerated());
        tc.setTaskId(dto.getTaskId());
        tc.setInterfaceLocked(dto.isInterfaceLocked());
        tc.setDataFile(dto.getDatafile());
        fillTrackedChangesInformation(tc, dto);
        
        if (dto.getReferencedTestData() != null) {
            String referencedDataName = dto.getReferencedTestData();
            for (IParameterInterfacePO testDataCube 
                    : TestDataCubeBP.getAllTestDataCubesFor(proj)) {
                if (referencedDataName.equals(testDataCube.getName())) {
                    tc.setReferencedDataCube(testDataCube);
                    break;
                }
            }
        }
        for (ParamDescriptionDTO pdDto : dto.getParameterDescription()) {
            String uniqueId = pdDto.getUuid();

            if (assignNewGuid) {
                IParamDescriptionPO paramDesc = 
                    tc.addParameter(pdDto.getType(), pdDto.getName(), mapper);
                m_oldToNewGuids.put(uniqueId, paramDesc.getUniqueId());
            } else {
                if (uniqueId != null
                    && Pattern.matches(
                        "[0-9a-fA-F]{" + ImportExportUtil.UUID_LENGTH + "}", uniqueId)) { //$NON-NLS-1$ //$NON-NLS-2$
                    // use the existent guid for parameter
                    tc.addParameter(pdDto.getType(), pdDto.getName(), uniqueId,
                        mapper);
                } else {
                    // creates a new GUID for parameter (only for conversion of
                    // old projects)
                    tc.addParameter(pdDto.getType(), pdDto.getName(), mapper);
                }
            }
        }
        
        tc.setDataManager(createTDManager(tc, dto.getTDManager(),
                assignNewGuid));
        
        if (!assignNewGuid) {
            generateRefTestCase(dto, proj, tc, assignNewGuid);
        }
        return tc;
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param tc Testcase which holds the newly created EventExecTC.
     * @param dto Abstraction of the DTO element
     * @param assignNewGuid <code>true</code> if the test case
     *              should be assigned a new GUID. Otherwise <code>false</code>.
     * @return a persistent object generated from the information in the DTO
     *              element
     * @throws InvalidDataException if some data is invalid when constructing
     *              an object. This should not happen for exported project, but
     *              may happen when someone generates DTO project description
     *              outside of GUIdancer.
     */
    private IEventExecTestCasePO createEventExecTestCase(IProjectPO proj,
        ISpecTestCasePO tc, EventTestCaseDTO dto, boolean assignNewGuid)
        throws InvalidDataException {

        IEventExecTestCasePO evTc;
        ISpecTestCasePO refTc = ImportExportUtil.findReferencedTCByGuid(
            dto.getTestcaseUuid(), dto.getProjectUuid(),
            proj, assignNewGuid, m_oldToNewGuids, m_tcRef);

        if (refTc == null) {
            // SpectTC is not yet available in this DB
            if (assignNewGuid) {
                evTc = NodeMaker.createEventExecTestCasePO(
                    dto.getTestcaseUuid(), dto.getProjectUuid(), tc);
            } else {
                evTc = NodeMaker.createEventExecTestCasePO(
                    dto.getTestcaseUuid(), dto.getProjectUuid(), 
                    tc, dto.getUuid());
            }
        } else {
            if (dto.getUuid() != null && !assignNewGuid) {
                evTc = NodeMaker.createEventExecTestCasePO(
                    refTc, tc, dto.getUuid());
            } else {
                evTc = NodeMaker.createEventExecTestCasePO(
                    refTc, tc);
            }
        }
        fillExecTestCase(proj, dto, evTc, assignNewGuid);
        evTc.setEventType(dto.getEventType());
        ReentryProperty reentryProperty = 
                ReentryProperty.getPropertyFromName(dto.getReentryProperty());
        evTc.setReentryProp(reentryProperty);
        if (reentryProperty == ReentryProperty.RETRY) {
            evTc.setMaxRetries(dto.getMaxRetries() != null
                    ? dto.getMaxRetries() : 1);
        }
        // Clear the cached specTc to avoid LazyInitializationExceptions
        evTc.clearCachedSpecTestCase();
        
        return evTc;
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param dto Abstraction of the DTO element
     * @param assignNewGuid <code>true</code> if the cap
     *              should be assigned a new GUID. Otherwise <code>false</code>.
     * @return a persistent object generated from the information in the DTO
     *              element
     */
    private ICapPO createCap(IProjectPO proj, CapDTO dto,
            boolean assignNewGuid) {

        final ICapPO cap;
        String componentname = dto.getComponentName();
        if (componentHasDefaultMapping(dto.getComponentType())) {
            componentname = null;
        }
        if (dto.getUuid() != null && !assignNewGuid) {
            // GUID is available
            cap = NodeMaker.createCapPO(
                dto.getName(), componentname, dto.getComponentType(), 
                dto.getActionName(), proj, dto.getUuid());
        } else {
            cap = NodeMaker.createCapPO(dto.getName(), 
                componentname, dto.getComponentType(), 
                dto.getActionName(), proj);
        }
        cap.setDataFile(dto.getDatafile());
        cap.setActive(dto.isActive());
        cap.setComment(dto.getComment());
        if (dto.getTDManager() != null) {
            ITDManager tdman = createTDManager(cap, dto.getTDManager(),
                    assignNewGuid);
            cap.setDataManager(tdman);                
        }
        return cap;
    }
    
    /**
     * 
     * @param proj the project
     * @param dto the {@link WhileDTO}
     * @param assignNewGuid should there be new guids assigned
     * @return and instance of either {@link IWhileDoPO} or {@link IDoWhilePO}
     */
    private ICondStructPO createWhile(IProjectPO proj, WhileDTO dto,
            boolean assignNewGuid) {
        ICondStructPO whilePO = null;
        boolean needsNewGUID = assignNewGuid || dto.getUuid() == null;
        if (dto.isDoWhile()) {
            if (assignNewGuid) {
                whilePO = NodeMaker.createDoWhilePO(dto.getName());
            } else {
                whilePO = NodeMaker
                        .createDoWhilePO(dto.getName(), dto.getUuid());
            }
        } else {
            if (assignNewGuid) {
                whilePO = NodeMaker.createWhileDoPO(dto.getName());
            } else {
                whilePO = NodeMaker
                        .createWhileDoPO(dto.getName(), dto.getUuid());
            }
        }
        whilePO.setNegate(dto.isNegated());
        whilePO.setGenerated(dto.getGenerated());
        whilePO.setComment(dto.getComment());
        whilePO.setTaskId(dto.getTaskId());
        whilePO.setDescription(dto.getDescription());
        whilePO.setActive(dto.isActive());

        List<NodeDTO> nodes = dto.getNodes();
        if (nodes != null && nodes.size() == 2) {
            if (dto.isDoWhile()) {
                fillContainer(nodes.get(0), whilePO.getDoBranch(), proj,
                        assignNewGuid);
                fillContainer(nodes.get(1), whilePO.getCondition(), proj,
                        assignNewGuid);
            } else {
                fillContainer(nodes.get(0), whilePO.getDoBranch(), proj,
                        assignNewGuid);
                fillContainer(nodes.get(1), whilePO.getCondition(), proj,
                        assignNewGuid);
            }
        }
        return whilePO;
    }
    
    /**
     * 
     * @param proj the project
     * @param dto the {@link IterateDTO}
     * @param assignNewGuid should there be new GUIDs assigned
     * @return the created and filled {@link IIteratePO}
     */
    private IIteratePO createIterate(IProjectPO proj, IterateDTO dto,
            boolean assignNewGuid) {
        IIteratePO iteratePO = null;
        if (dto.getUuid() != null && !assignNewGuid) {
            iteratePO =
                    NodeMaker.createIteratePO(dto.getName());
        } else {
            iteratePO =
                    NodeMaker.createIteratePO(dto.getName(), dto.getUuid());
        }
        
        iteratePO.setGenerated(dto.getGenerated());
        iteratePO.setComment(dto.getComment());
        iteratePO.setTaskId(dto.getTaskId());
        iteratePO.setDescription(dto.getDescription());
        iteratePO.setActive(dto.isActive());
        
        iteratePO.setDataManager(createTDManager(iteratePO, dto.getTDManager(),
                assignNewGuid));
        
        List<NodeDTO> nodes = dto.getNodes();
        if (nodes != null && nodes.size() == 1) {
            fillContainer(nodes.get(0), iteratePO.getDoBranch(), proj,
                assignNewGuid);
        }
        return iteratePO;
    }

    /**
     * @param poNode the persistent object to fill
     * @param dto the dto node to read from
     */
    private void fillTrackedChangesInformation(INodePO poNode, NodeDTO dto) {
        SortedMap<Long, String> trackedModificationList = 
                dto.getTrackedModifications();
        if (!trackedModificationList.isEmpty() && !m_skipTrackingInformation) {
            poNode.setTrackedChangesMap(trackedModificationList);
        }
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param dto Abstraction of the DTO element
     * @param assignNewGuids <code>true</code> if the parameters were given
     *        new unique IDs. Otherwise <code>false</code>.
     * @param mapper Mapper to resolve param names.
     * @return a persistent object generated from the information in the DTO
     *         element
     */
    private ITestDataCubePO createTestDataCube(NamedTestDataDTO dto,
            boolean assignNewGuids, IParamNameMapper mapper) {

        ITestDataCubePO testDataCube = 
            PoMaker.createTestDataCubePO(dto.getName());
        for (ParamDescriptionDTO dtoParamDesc 
                : dto.getParameterDescriptions()) {
            if (assignNewGuids) {
                IParamDescriptionPO paramDesc = 
                    testDataCube.addParameter(dtoParamDesc.getType(), 
                            dtoParamDesc.getName(), mapper);
                m_oldToNewGuids.put(dtoParamDesc.getUuid(), 
                        paramDesc.getUniqueId());
            } else {
                testDataCube.addParameter(dtoParamDesc.getType(), dtoParamDesc
                        .getName(), dtoParamDesc.getUuid(), mapper);
            }
        }
        testDataCube.setDataManager(createTDManager(testDataCube,
                dto.getTDManager(), assignNewGuids));
        return testDataCube;
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param dto Abstraction of the DTO element
     * @param assignNewGuids <code>true</code> if the parameters were given
     *        new unique IDs. Otherwise <code>false</code>.
     * @param mapper Mapper to resolve param names.
     * @return a persistent object generated from the information in the DTO
     *         element
     */
    private ITestDataCategoryPO createTestDataCategory(TestDataCategoryDTO dto, 
            boolean assignNewGuids, IParamNameMapper mapper) {
        
        ITestDataCategoryPO testDataCategory = 
                PoMaker.createTestDataCategoryPO(dto.getName());
        
        for (TestDataCategoryDTO subCategory : dto.getTestDataCategories()) {
            testDataCategory.addCategory(createTestDataCategory(
                    subCategory, assignNewGuids, mapper));
        }
        
        for (NamedTestDataDTO testData : dto.getNamedTestDatas()) {
            testDataCategory.addTestData(
                    createTestDataCube(testData, assignNewGuids, mapper));
        }

        return testDataCategory;
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param dto Abstraction of the DTO element
     * @return a persistent object generated from the information in the DTO
     *              element
     */
    private IReusedProjectPO createReusedProject(ReusedProjectDTO dto) {
        Integer majorProjVersion = dto.getMajorProjectVersion();
        Integer minorProjVersion = dto.getMinorProjectVersion();
        Integer microProjVersion = dto.getMicroProjectVersion();
        String versionQualifier = dto.getProjectVersionQualifier();
        IReusedProjectPO reusedProject = PoMaker.createReusedProjectPO(
                dto.getProjectUuid(), majorProjVersion, minorProjVersion,
                microProjVersion, versionQualifier);
        return reusedProject;
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param dto Abstraction of the DTO element
     * @param assignNewGuid <code>true</code> if the AUT and all corresponding 
     *              AUT Configs should be assigned new GUIDs. Otherwise
     *              <code>false</code>.
     * @return a persistent object generated from the information in the DTO
     *              element
     */
    private IAUTMainPO createAUTMain(AutDTO dto, boolean assignNewGuid) {
        IAUTMainPO aut = null;
        if (dto.getUuid() != null && !assignNewGuid) {
            // UUID is available
            aut = PoMaker.createAUTMainPO(dto.getName(), dto.getUuid());
        } else {
            aut = PoMaker.createAUTMainPO(dto.getName());
        }

        aut.setToolkit(dto.getToolkit());
        aut.setGenerateNames(dto.isGenerateNames());
        m_autRef.put(dto.getId(), aut);
        aut.setObjMap(createOM(dto));
        for (AutConfigDTO confdto : dto.getConfigs()) {
            aut.addAutConfigToSet(createAUTConfig(confdto, assignNewGuid));
        }
        for (String autId : dto.getAutIds()) {
            aut.getAutIds().add(autId);
        }
        Map<String, String> propertyMap = dto.getPropertyMap();
        for (String key : propertyMap.keySet()) {
            aut.getPropertyMap().put(key, propertyMap.get(key));
        }
        return aut;
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param dto Abstraction of the DTO element
     * @param assignNewGuid <code>true</code> if the AUT Config
     *              should be assigned new GUIDs. Otherwise <code>false</code>.
     * @return a persistent object generated from the information in the DTO
     *              element
     */
    private IAUTConfigPO createAUTConfig(AutConfigDTO dto,
            boolean assignNewGuid) {
        IAUTConfigPO conf = null;
        if (dto.getUuid() != null && !assignNewGuid) {
            // GUID is available
            conf = PoMaker.createAUTConfigPO(dto.getUuid());
        } else {
            conf = PoMaker.createAUTConfigPO();
        }
        
        final List<MapEntryDTO> confAttrMapList = dto.getConfAttrMapEntry();
        for (MapEntryDTO entry : confAttrMapList) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            conf.setValue(key, value);
        }
        return conf;
    }

    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param dto Abstraction of the DTO element
     * @return a persistent object generated from the information in the DTO
     *              element
     */
    private IObjectMappingPO createOM(AutDTO dto) {
        IObjectMappingPO om = PoMaker.createObjectMappingPO();
        ObjectMappingDTO omDto = dto.getObjectMapping();
        ObjectMappingProfileDTO profileSto = omDto.getProfile();
        if (profileSto != null) {
            // Use the profile defined in the imported project
            IObjectMappingProfilePO profilePo = PoMaker
                    .createObjectMappingProfile();
            profilePo.setContextFactor(profileSto.getContextFactor());
            profilePo.setNameFactor(profileSto.getNameFactor());
            profilePo.setPathFactor(profileSto.getPathFactor());
            profilePo.setThreshold(profileSto.getThreshold());
            om.setProfile(profilePo);
        }

        OmCategoryDTO mappedCategoryDto = omDto.getMapped();
        if (mappedCategoryDto != null) {
            fillObjectMappingCategory(
                    mappedCategoryDto, om.getMappedCategory());
        }

        OmCategoryDTO unmappedComponentCategory = omDto.getUnmappedComponent();
        if (unmappedComponentCategory != null) {
            fillObjectMappingCategory(unmappedComponentCategory, 
                    om.getUnmappedLogicalCategory());
        }
        
        OmCategoryDTO unmappedTechnicalCategory = omDto.getUnmappedTechnical();
        if (unmappedTechnicalCategory != null) {
            fillObjectMappingCategory(unmappedTechnicalCategory, 
                    om.getUnmappedTechnicalCategory());
        }
        
        return om;
    }
    
    /**
     * Write the information from the DTO element to its corresponding Object.
     * @param categoryDto The DTO element which contains the information
     * @param category The persistent object Object
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void fillObjectMappingCategory(OmCategoryDTO categoryDto,
            IObjectMappingCategoryPO category) {

        category.setName(categoryDto.getName());
        for (OmCategoryDTO subcategoryDto : categoryDto.getCategories()) {
            IObjectMappingCategoryPO subcategory = 
                PoMaker.createObjectMappingCategoryPO(subcategoryDto.getName());
            category.addCategory(subcategory);
            fillObjectMappingCategory(subcategoryDto, subcategory);
        }
        
        for (OmEntryDTO assocDTO : categoryDto.getAssociations()) {
            TechnicalNameDTO tecNameDto = assocDTO.getTechnicalName();
            List<String> logNames = assocDTO.getLogicalNames();
            IComponentIdentifier tecName = null;
            if (tecNameDto != null) {
                tecName = new ComponentIdentifier();
                tecName.setComponentClassName(tecNameDto
                    .getComponentClassName());
                tecName.setSupportedClassName(tecNameDto
                    .getSupportedClassName());
                tecName.setAlternativeDisplayName(tecNameDto
                    .getAlternativeDisplayName());
                tecName.setNeighbours(
                        new ArrayList(tecNameDto.getNeighbours()));
                tecName.setHierarchyNames(
                        new ArrayList(tecNameDto.getHierarchyNames()));
                ObjectMappingProfileDTO omp = tecNameDto
                        .getObjectMappingProfile();
                if (omp != null) {
                    Profile p = new Profile(omp.getName(), omp.getNameFactor(),
                            omp.getPathFactor(), omp.getContextFactor(),
                            omp.getThreshold());
                    tecName.setProfile(p);
                }
            }

            // It is necessary to create a new (cloneable) list from the list
            // of component names because the list itself is not cloneable.
            // If the list is used directly, then 
            IObjectMappingAssoziationPO assoc = 
                PoMaker.createObjectMappingAssoziationPO(tecName, 
                        new HashSet<String>(logNames));
            assoc.setType(assocDTO.getType());
            category.addAssociation(assoc);
        }
    }

    /**
     * @param proj the project
     * @param dto the project
     * @return the project properties
     */
    public IProjectPropertiesPO fillProjectProperties(IProjectPO proj,
            ProjectDTO dto) {
        proj.setComment(dto.getComment());
        proj.setMarkupLanguage(dto.getMarkupLanguage());
        proj.setToolkit(dto.getAutToolKit());
        proj.setIsReusable(dto.isReusable());
        proj.setIsProtected(dto.isProtected());
        IProjectPropertiesPO projProperties = proj.getProjectProperties();
        projProperties.setALMRepositoryName(dto.getAlmRepositoryName());
        projProperties.setIsReportOnSuccess(dto.isReportOnSuccess());
        projProperties.setIsReportOnFailure(dto.isReportOnFailure());
        projProperties.setDashboardURL(dto.getDashboardURL());
        projProperties.getCheckConfCont().setEnabled(
                dto.isTeststyleEnabled());
        
        projProperties.setIsTrackingActivated(dto.isTrackingEnabled());
        projProperties.setTrackChangesSignature(dto.getTrackingAttribute());
        if (dto.getTrackingUnit() != null) {
            projProperties.setTrackChangesUnit(
                    TrackingUnit.valueOf(dto.getTrackingUnit()));
        }
        projProperties.setTrackChangesSpan(dto.getTrackingSpan());
        
        List<IALMReportingRulePO> reportingRules = 
                new ArrayList<IALMReportingRulePO>();
        for (ReportingRuleDTO rule : dto.getReportingRules()) {
            IALMReportingRulePO newReportingRule = createReportingRule(rule);
            reportingRules.add(newReportingRule);
        }
        projProperties.setALMReportingRules(reportingRules);
        
        return projProperties;
    }

    /**
     * converts alm reporting rule from dto to po
     * @param dto rule from ReportingRuleDTO
     * @return the converted rule
     */
    private IALMReportingRulePO createReportingRule(ReportingRuleDTO dto) {
        String name = dto.getName();
        String fieldID = dto.getFieldID();
        String value = dto.getValue();
        String dtoType = dto.getType();
        ReportRuleType type = null;
        if (dtoType.equals(ReportRuleType.ONSUCCESS.toString())) {
            type = ReportRuleType.ONSUCCESS;
        } else if (dtoType.equals(ReportRuleType.ONFAILURE.toString())) {
            type = ReportRuleType.ONFAILURE;
        }
        IALMReportingRulePO rule = PoMaker.createALMReportingRulePO(
                name, fieldID, value, type);
        return rule;
    }
    
    /**
     * @param proj the project po
     * @param dto the projectDTO
     * @param assignNewGuid flag to indicate whether new ids should be assigned
     * @throws InterruptedException in case of interruption
     * @throws InvalidDataException in case of invalid data
     */
    private void handleTestSuitesAndTestJobsAndCategories(IProjectPO proj,
            ProjectDTO dto, boolean assignNewGuid) throws InterruptedException,
            InvalidDataException {
        
        for (NodeDTO node : dto.getExecCategories()) {
            ImportExportUtil.checkCancel(m_monitor);
            proj.getExecObjCont().addNode(
                    createTestSuitesOrTestJobsOrCategories(proj, node,
                            assignNewGuid));
        }
    }
    
    /**
     * @param proj the project po
     * @param dto the projectDTO
     * @param assignNewGuid 
     * @return IExecPersistable 
     * @throws InvalidDataException
     */
    private INodePO createTestSuitesOrTestJobsOrCategories(
            IProjectPO proj, NodeDTO dto, boolean assignNewGuid)
                    throws InvalidDataException {
        
        if (dto instanceof ExecCategoryDTO) {
            return createExecCategory(proj, (ExecCategoryDTO)dto,
                    assignNewGuid);
        } else if (dto instanceof TestSuiteDTO) {
            return createTestSuite(proj, (TestSuiteDTO)dto, assignNewGuid);
        } else if (dto instanceof TestJobDTO) {
            return createTestJob((TestJobDTO)dto, assignNewGuid);
        }
        
        return null;
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param dto Abstraction of the DTO element
     * @param assignNewGuid <code>true</code> if the test suite
     *              hould be assigned a new GUID. Otherwise <code>false</code>.
     * @return a persistent object generated from the information in the DTO
     *              element
     * @throws InvalidDataException if some data is invalid when constructing
     *              an object. This should not happen for exported project,
     *              but may happen when someone generates DTO project description
     *              outside of GUIdancer.
     */
    private INodePO createTestJob(TestJobDTO dto,
            boolean assignNewGuid) throws InvalidDataException {

        ITestJobPO tj;
        if (dto.getUuid() != null && !assignNewGuid) {
            tj = NodeMaker.createTestJobPO(dto.getName(), dto.getUuid());
        } else {
            tj = NodeMaker.createTestJobPO(dto.getName());
        }
        tj.setComment(dto.getComment());
        tj.setDescription(dto.getDescription());
        tj.setTaskId(dto.getTaskId());
        fillTrackedChangesInformation(tj, dto);

        for (NodeDTO dtoRefs : dto.getRefTestSuites()) {
            if (dtoRefs instanceof RefTestSuiteDTO) {
                RefTestSuiteDTO dtoRts = (RefTestSuiteDTO) dtoRefs;
                IRefTestSuitePO rts;
                if (assignNewGuid) {
                    // Only Test Suites from the same project can be referenced,
                    // and all Test Suites for this Project have already been
                    // initialized (so they have already been entered into the
                    // old to new GUID map). This is why we can simply directly
                    // use
                    // the old to new GUID map.
                    String testSuiteGuid =
                            m_oldToNewGuids.get(dtoRts.getTsUuid());
                    if (testSuiteGuid == null) {
                        throw new InvalidDataException(
                                "Test Suite Reference: No new GUID found for Test Suite with old GUID: " //$NON-NLS-1$
                                        + dtoRts.getTsUuid(),
                                MessageIDs.E_IMPORT_PROJECT_XML_FAILED);
                    }
                    rts = NodeMaker.createRefTestSuitePO(dtoRts.getName(),
                            testSuiteGuid, dtoRts.getAutId());
                } else {
                    rts = NodeMaker.createRefTestSuitePO(dtoRts.getName(),
                            dtoRts.getUuid(), dtoRts.getTsUuid(),
                            dtoRts.getAutId());

                }
                rts.setComment(dtoRts.getComment());
                rts.setDescription(dtoRts.getDescription());
                tj.addNode(rts);
            } else if (dtoRefs instanceof CommentDTO) {
                tj.addNode(createComment((CommentDTO) dtoRefs, assignNewGuid));
            }
        }
        return tj;
    }

    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param dto Abstraction of the ExecCategoryDTO element
     * @param assignNewGuid <code>true</code> if the category and all subnodes
     *              should be assigned new GUIDs. Otherwise <code>false</code>.
     * @return a persistent object generated from the information in the DTO
     *              element
     * @throws InvalidDataException if some data is invalid when constructing
     *              an object. This should not happen for exported project,
     *              but may happen when someone generates DTO project.
     */
    private INodePO createExecCategory(IProjectPO proj, 
        ExecCategoryDTO dto, boolean assignNewGuid) 
        throws InvalidDataException {
        
        ICategoryPO cat;
        
        if (dto.getUuid() != null && !assignNewGuid) {
            cat = NodeMaker.createCategoryPO(dto.getName(), dto.getUuid());
        } else {
            cat = NodeMaker.createCategoryPO(dto.getName());
        }
        
        cat.setGenerated(dto.getGenerated());
        cat.setComment(dto.getComment());
        cat.setDescription(dto.getDescription());
        cat.setTaskId(dto.getTaskId());
        
        for (NodeDTO node  : dto.getNodes()) {
            cat.addNode(createTestSuitesOrTestJobsOrCategories(proj, node,
                    assignNewGuid));
        }
        
        return cat;
    }

    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param dto Abstraction of the TestSuiteDTO element
     * @param assignNewGuid <code>true</code> if the test suite
     *              should be assigned a new GUID. Otherwise <code>false</code>.
     * @return a persistent object generated from the information in the DTO
     *              element
     */
    private INodePO createTestSuite(IProjectPO proj, TestSuiteDTO dto, 
        boolean assignNewGuid) throws InvalidDataException {
        
        ITestSuitePO ts;
        if (dto.getUuid() != null && !assignNewGuid) {
            ts = NodeMaker.createTestSuitePO(dto.getName(), dto.getUuid());
        } else {
            ts = NodeMaker.createTestSuitePO(dto.getName());
        }
        
        if (assignNewGuid) {
            m_oldToNewGuids.put(dto.getUuid(), ts.getGuid());
        }
        
        ts.setComment(dto.getComment());
        ts.setDescription(dto.getDescription());
        ts.setTaskId(dto.getTaskId());
        ts.setTrackedChangesMap(dto.getTrackedModifications());
        if (dto.getSelectedAut() != null) {
            ts.setAut(m_autRef.get(dto.getSelectedAut()));
        }
        for (NodeDTO ref : dto.getUsedTestCases()) {
            if (ref instanceof RefTestCaseDTO) {
                ts.addNode(createExecTestCase(proj, (RefTestCaseDTO) ref,
                        assignNewGuid));
            } else if (ref instanceof CommentDTO) {
                ts.addNode(createComment((CommentDTO) ref, assignNewGuid));
            } else if (ref instanceof ConditionalStatementDTO) {
                ts.addNode(createConditionalStatement(
                        (ConditionalStatementDTO)ref, proj, assignNewGuid));
            } else if (ref instanceof WhileDTO) {
                ts.addNode(createWhile(proj, (WhileDTO)ref, assignNewGuid));
            } else if (ref instanceof IterateDTO) {
                ts.addNode(createIterate(proj, (IterateDTO)ref,
                        assignNewGuid));
            }
        }
        
        Map<String, Integer> defaultEventHandler = 
            new HashMap<String, Integer>();
        for (DefaultEventHandlerDTO evh : dto.getEventHandlers()) {
            defaultEventHandler.put(evh.getEvent(),
                    ReentryProperty.getPropertyFromName(
                            evh.getReentryProperty()).getValue());
            
        }
        ts.setDefaultEventHandler(defaultEventHandler);
        ts.setStepDelay(dto.getStepDelay());
        ts.setRelevant(dto.isRelevant());
        return ts;
    }

    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param dto Abstraction of the RefTestCaseDTO element
     * @param assignNewGuid <code>true</code> if the testcase and all subnodes
     *              should be assigned new GUIDs. Otherwise <code>false</code>.
     * @return a persistent object generated from the information in the
     *              RefTestCaseDTO element
     */
    private IExecTestCasePO createExecTestCase(IProjectPO proj,
        RefTestCaseDTO dto, boolean assignNewGuid) {
        
        IExecTestCasePO exec;
        ISpecTestCasePO refTc = ImportExportUtil.findReferencedTCByGuid(
                dto.getTestcaseUuid(), dto.getProjectUuid(),
                proj, assignNewGuid, m_oldToNewGuids, m_tcRef);
        
        if (refTc == null) {
            // SpectTC is not yet available in this DB
            if (!assignNewGuid) {
                exec = NodeMaker.createExecTestCasePO(
                    dto.getTestcaseUuid(), dto.getProjectUuid(), dto.getUuid());
            } else {
                exec = NodeMaker.createExecTestCasePO(
                    dto.getTestcaseUuid(), dto.getProjectUuid());
            }
        } else {
            if (dto.getUuid() != null && !assignNewGuid) {
                // GUID is available
                exec = NodeMaker.createExecTestCasePO(refTc, dto.getUuid());
            } else {
                exec = NodeMaker.createExecTestCasePO(refTc);
            }
        }
        
        fillExecTestCase(proj, dto, exec, assignNewGuid);

        // Clear the cached specTc to avoid LazyInitializationExceptions
        exec.clearCachedSpecTestCase();
        
        return exec;
    }

    /**
     * Shared method for setting values into ExecTCs and their subclasses.
     * @param proj The IProjectPO which is currently build. The instance is
     *              needed by some objects to verify that their data confirms
     *              to project specification (for instance languages).
     * @param dto Abstraction of the XML element (see Apache XML Beans)
     * @param exec TC to be initialized
     * @param assignNewGuid <code>true</code> if nodes are being assigned
     *              new GUIDs. Otherwise <code>false</code>.
     */
    private void fillExecTestCase(IProjectPO proj, RefTestCaseDTO dto,
        IExecTestCasePO exec, boolean assignNewGuid) {
        
        exec.setName(dto.getName());
        exec.setComment(dto.getComment());
        exec.setDescription(dto.getDescription());
        exec.setGenerated(dto.getGenerated());
        exec.setTaskId(dto.getTaskId());
        exec.setActive(dto.isActive());
        exec.setDataFile(dto.getDatafile());
        exec.setJUnitTestSuite(dto.isJunitSuite());
        if (dto.getReferencedTestData() != null) {
            String referencedDataName = dto.getReferencedTestData();
            for (IParameterInterfacePO testDataCube 
                    : TestDataCubeBP.getAllTestDataCubesFor(proj)) {
                if (referencedDataName.equals(testDataCube.getName())) {
                    exec.setReferencedDataCube(testDataCube);
                    break;
                }
            }
        }

        if (dto.isHasOwnTestdata()) {
            // ExecTestCasePO doesn't have an own parameter list.
            // It uses generally the parameter from the associated
            // SpecTestCase.
            exec.setDataManager(createTDManager(exec, dto.getTDManager(),
                    assignNewGuid));
        }
        for (ComponentNamesPairDTO overridden : dto.getOverriddenNames()) {
            final ICompNamesPairPO compName = PoMaker.createCompNamesPairPO(
                    overridden.getOriginalName(),
                    overridden.getNewName(), null);
            compName.setPropagated(overridden.isPropagated());
            exec.addCompNamesPair(compName);
        }
    }
    
    /**
     * Creates the instance of the persistent object which is defined by the
     * DTO element used as parameter. The method generates all dependent objects
     * as well.
     * @param owner The ParamNode which holds this TDManager
     * @param dto Abstraction of the DTO element
     *              the test data, otherwise a new TDManager is created.
     * @return a persistent object generated from the information in the DTO
     *              element
     * @param assignNewGuids <code>true</code> if the parameters were given
     *              new unique IDs. Otherwise <code>false</code>.
     */
    private ITDManager createTDManager(IParameterInterfacePO owner, 
        TDManagerDTO dto, boolean assignNewGuids) {

        if (dto == null) {
            return PoMaker.createTDManagerPO(owner);
        }
        
        final ITDManager tdman;
        List<String> uniqueIds = new ArrayList<String>(
            dto.getUniqueIds());

        if (assignNewGuids) {
            // Update list of unique IDs
            List<String> newUniqueIds = new ArrayList<String>();
            for (String id : uniqueIds) {
                if (Pattern.matches(
                    "[0-9a-fA-F]{" + ImportExportUtil.UUID_LENGTH + "}", id) //$NON-NLS-1$ //$NON-NLS-2$
                        && m_oldToNewGuids.containsKey(id)) {
                    // Use new GUID
                    newUniqueIds.add(m_oldToNewGuids.get(id));
                } else {
                    // Leave as-is
                    newUniqueIds.add(id);
                }
            }
            uniqueIds = newUniqueIds;
        }

        if (uniqueIds.isEmpty()) {
            tdman = PoMaker.createTDManagerPO(owner);        
        } else {
            tdman = PoMaker.createTDManagerPO(owner, uniqueIds);
        }
        for (DataRowDTO row : dto.getDataSets()) {
            final List<String> td = new ArrayList<String>(row.getColumns()
                    .size());
            for (String column : row.getColumns()) {
                td.add(readData(column, owner));
            }
            tdman.insertDataSet(PoMaker.createListWrapperPO(td), 
                    tdman.getDataSetCount());
        }
        return tdman;
    }

    /**
     * @param column associated cell from import
     * @param owner The owner of the data.
     * @return the list of test data
     */
    private String readData(String column, IParameterInterfacePO owner) {
        if (column != null) {
            try {
                // Since we are not using the converter for anything other than
                // parsing, we can use null for paramDesc
                ModelParamValueConverter converter = 
                        new ModelParamValueConverter(
                                column, owner, 
                                null);
                
                if (!converter.containsErrors()) {
                    // Only try to replace reference GUIDs if the 
                    // string could be successfully parsed.
                    // Otherwise, the model string will be overwritten with
                    // the empty string because no tokens were created 
                    // during parsing. 
                    converter.replaceUuidsInReferences(m_oldToNewGuids);
                }
                
                return converter.getModelString();
            } catch (IllegalArgumentException iae) {
                // Do nothing.
                // The i18nValue uses the old format and can therefore
                // not be parsed. This value will be converted in V1M42Converter.
            }
        }
        return column;
    }

    /**
     * @param dto the datasource to get additional information from
     */
    private void documentRequiredProjects(ProjectDTO dto) {
        
        if (dto.getReusedProjects().size() > 0) {
            String msg = NLS.bind(Messages.XmlImporterProjectDependency,
                    new Object[] {dto.getName(), new ProjectVersion(
                            dto.getMajorProjectVersion(),
                            dto.getMinorProjectVersion(),
                            dto.getMicroProjectVersion(),
                            dto.getProjectVersionQualifier())}); 
            
            for (ReusedProjectDTO rp : dto.getReusedProjects()) {
                msg += StringConstants.NEWLINE + StringConstants.TAB;
                ProjectVersion version = new ProjectVersion(
                        rp.getMajorProjectVersion(),
                        rp.getMinorProjectVersion(),
                        rp.getMicroProjectVersion(),
                        rp.getProjectVersionQualifier());
                msg += rp.getProjectName() != null 
                    ? NLS.bind(Messages.XmlImporterRequiredProject,
                            new Object[] { rp.getProjectName(), version})
                    : NLS.bind(Messages.XmlImporterRequiredProjectWithoutName,
                            new Object[] { rp.getProjectUuid(), version});
            }
            Status s = new Status(IStatus.INFO, Activator.PLUGIN_ID, msg);
            m_io.writeStatus(s);
        }
    }

    /**
     * @param dto the xml project
     * @throws JBVersionException in case of version conflict between used
     *              toolkits of imported project and the installed Toolkit Plugins
     */
    private void checkUsedToolkits(ProjectDTO dto) throws JBVersionException {
        Set<IUsedToolkitPO> usedTK = new HashSet<IUsedToolkitPO>();
        for (UsedToolkitDTO usedToolkit : dto.getUsedToolkits()) {
            usedTK.add(PoMaker.createUsedToolkitsPO(usedToolkit.getName(), 
                usedToolkit.getMajorVersion(), 
                usedToolkit.getMinorVersion(), 
                null));
        }
        List<String> errorMsgs = new ArrayList<String>();
        if (!validateToolkitVersion(usedTK, dto.getName(), errorMsgs)) {
            throw new JBVersionException(
                Messages.IncompatibleToolkitVersion,
                MessageIDs.E_LOAD_PROJECT_TOOLKIT_MAJOR_VERSION_ERROR, 
                errorMsgs);
        }
    }

    /**
     * @param usedTK toolkits used from project to import
     * @param projName name of project to import
     * @param errorMsgs list with strings of detailed error messages
     * @return if project uses toolkits which client supports 
     */
    private boolean validateToolkitVersion(Set<IUsedToolkitPO> usedTK, 
        String projName, List<String> errorMsgs) {
        List<ToolkitPluginError> errors = 
            UsedToolkitBP.getInstance().checkUsedToolkitPluginVersions(usedTK);
        if (errors.isEmpty()) {
            return true;
        } 
        boolean loadProject = true;        
        for (ToolkitPluginError error : errors) {
            final StringBuilder strBuilder = new StringBuilder();
            String toolkitId = error.getToolkitId();
            ToolkitDescriptor desc = 
                ComponentBuilder.getInstance().getCompSystem()
                .getToolkitDescriptor(toolkitId);
            String toolkitName = desc != null ? desc.getName() : toolkitId;
            strBuilder.append(Messages.OpenProjectActionToolkitVersionConflict2)
                .append(toolkitName)
                .append(Messages.XmlImporterToolkitVersionConflict3a)
                .append(projName)
                .append(Messages.XmlImporterToolkitVersionConflict3b);
            
            final ERROR errorType = error.getError();
            final String descr = Messages
                .OpenProjectActionToolkitVersionConflict5;
            switch (errorType) {
                case MAJOR_VERSION_ERROR:
                    strBuilder.append(Messages
                            .OpenProjectActionToolkitVersionConflict4a);
                    strBuilder.append(descr);
                    errorMsgs.add(strBuilder.toString());
                    loadProject = false;
                    break;
    
                case MINOR_VERSION_HIGHER:
                    strBuilder.append(Messages
                            .OpenProjectActionToolkitVersionConflict4b);
                    strBuilder.append(descr);
                    errorMsgs.add(strBuilder.toString());
                    loadProject = false;
                    
                    break;
                    
                case MINOR_VERSION_LOWER:
                    break;
                    
                default:
                    Assert.notReached(Messages.UnknownErrorType
                        + String.valueOf(errorType));
            }
        }
        return loadProject;
    }

    /**
     * @param numberOfCategoryOfProject number of test result summaries
     * @return number of category of project
     */
    private int getWorkToImport(int numberOfCategoryOfProject) {
        // Project loading
        int work = 1;

        // Reused projects loading
        work++;

        // AUTs loading
        work++;
        
        // Test data categories loading
        work++;
        
        // number Of category of project
        work += numberOfCategoryOfProject;
        
        // Test suites and jobs loading
        work++;
        
        // Check configurations loading
        work++;
        
        // Component names loading
        work++;

        return work;
    }
}
