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
package org.eclipse.jubula.client.core.model;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;


/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public abstract class NodeMaker {

    /** A constant handler to mark conditions */
    public static final IEventExecTestCasePO COND_EVENT_EXECTC =
            new EventExecTestCasePO(ReentryProperty.CONDITION);
    
    /** hide */
    private NodeMaker() {
    // hide for factory class
    }

    /**
     * factory method to replace constructor
     * @param capName capName
     * @param componentName componentName
     * @param componentType componentType
     * @param actionName actionName
     * @return ICapPO
     */
    public static ICapPO createCapPO(String capName, String componentName,
        String componentType, String actionName) {
        
        return new CapPO(capName, componentName, componentType, actionName, 
                false);
    }
    
    /**
     * factory method to replace constructor
     * @param capName capName
     * @param componentName componentName
     * @param componentType componentType
     * @param actionName actionName
     * @param project project
     * @return ICapPO
     */
    public static ICapPO createCapPO(String capName, String componentName, 
        String componentType, String actionName, IProjectPO project) {
        
        return new CapPO(capName, componentName, componentType, actionName, 
            project, false);
    }
    
    /**
     * factory method to replace constructor
     * @param capName capName
     * @param componentName componentName
     * @param componentType componentType
     * @param actionName actionName
     * @param project project
     * @param guid guid
     * @return ICapPO
     */
    public static ICapPO createCapPO(String capName, String componentName, 
        String componentType, String actionName, IProjectPO project, 
        String guid) {
        
        return new CapPO(capName, componentName, componentType, actionName, 
            project, guid, false);
    }

    /**
     * factory method to replace constructor
     * @param name name
     * @return ICategoryPO
     */
    public static ICategoryPO createCategoryPO(String name) {
        return new CategoryPO(name, false);
    }

    /**
     * factory method to replace constructor
     * @param name name 
     * @param guid guid
     * @return ICategoryPO
     */
    public static ICategoryPO createCategoryPO(String name, String guid) {
        return new CategoryPO(name, guid, false);
    }
    
    /**
     * factory method to replace constructor
     * @param name name
     * @param isGenerated isGenerated
     * @return ICategroyPO
     */
    public static ICategoryPO createCategoryPO(String name, 
            boolean isGenerated) {
        return new CategoryPO(name, isGenerated);
    }


    /**
     * factory method to replace constructor
     * @param specTC specTC
     * @param assocNode assocNode
     * @return IEventExecTestCasePO
     */
    public static IEventExecTestCasePO createEventExecTestCasePO(
        ISpecTestCasePO specTC, INodePO assocNode) {
        return new EventExecTestCasePO(specTC, assocNode, false);
    }

    /**
     * factory method to replace constructor
     * @param specTC specTC
     * @param assocNode assocNode
     * @param guid guid
     * @return IEventExecTestCasePO
     */
    public static IEventExecTestCasePO createEventExecTestCasePO(
        ISpecTestCasePO specTC, INodePO assocNode, String guid) {
        return new EventExecTestCasePO(specTC, assocNode, guid, false);
    }

    /**
     * factory method to replace constructor
     * @param specTCGuid specTCGuid
     * @param projectGuid projectGuid
     * @param assocNode assocNode
     * @param guid guid
     * @return IEventExecTestCasePO
     */
    public static IEventExecTestCasePO createEventExecTestCasePO(
        String specTCGuid, String projectGuid, INodePO assocNode, String guid) {
        return new EventExecTestCasePO(specTCGuid, projectGuid, 
            assocNode, guid, false);
    }

    /**
     * factory method to replace constructor
     * @param specTCGuid specTCGuid
     * @param projectGuid projectGuid
     * @param assocNode assocNode
     * @return IEventExecTestCasePO
     */
    public static IEventExecTestCasePO createEventExecTestCasePO(
        String specTCGuid, String projectGuid, INodePO assocNode) {
        return new EventExecTestCasePO(specTCGuid, projectGuid, 
            assocNode, false);
    }

    /**
     * factory method to replace constructor
     * @param specTestCase specTestCase
     * @return IExecTestCasePO
     */
    public static IExecTestCasePO createExecTestCasePO(
        ISpecTestCasePO specTestCase) {
        return new ExecTestCasePO(specTestCase, false);
    }

    /**
     * factory method to replace constructor
     * @param specTestCase specTestCase
     * @param guid guid
     * @return IExecTestCasePO
     */
    public static IExecTestCasePO createExecTestCasePO(
        ISpecTestCasePO specTestCase, String guid) {
        return new ExecTestCasePO(specTestCase, guid, false);
    }
    
    /**
     * factory method to replace constructor
     * @param specTestCase specTestCase
     * @param isGenerated isGenerated
     * @return IExecTestCasePO
     */
    public static IExecTestCasePO createExecTestCasePO(
        ISpecTestCasePO specTestCase, boolean isGenerated) {
        return new ExecTestCasePO(specTestCase, isGenerated);
    }

    /**
     * factory method to replace constructor
     * @param testcaseGuid testcaseGuid
     * @param projectGuid projectGuid
     * @return IExecTestCasePO
     */
    public static IExecTestCasePO createExecTestCasePO(String testcaseGuid, 
        String projectGuid) {
        
        return new ExecTestCasePO(testcaseGuid, projectGuid, false);
    }


    /**
     * factory method to replace constructor
     * @param specTestCaseGuid specTEstCaseGuid
     * @param projectGuid projectGuid
     * @param guid guid
     * @return IExecTestCase
     */
    public static IExecTestCasePO createExecTestCasePO(
        String specTestCaseGuid, String projectGuid, String guid) {
        return new ExecTestCasePO(specTestCaseGuid, projectGuid, guid, false);
    }

    /**
     * factory method to replace constructor
     * @param name name
     * @param metadataVersion metadataVersion
     * @return IProjectPO
     */
    public static IProjectPO createProjectPO(String name,
        Integer metadataVersion) {
        return new ProjectPO(name, metadataVersion, false);
    }

    /**
     * factory method to replace constructor, This will generate a new GUID
     * @param metadataVersion metadataVersion
     * @param majorNumber majorNumber
     * @param minorNumber minorNumber
     * @param microNumber The micro version number for this project
     * @param versionQualifier The version qualifier for this project
     * @return IProjectPO
     */
    public static IProjectPO createProjectPO(Integer metadataVersion,
            Integer majorNumber, Integer minorNumber, Integer microNumber,
            String versionQualifier) {

        return new ProjectPO(metadataVersion, majorNumber, minorNumber,
                microNumber, versionQualifier, 
                PersistenceUtil.generateUUID(), false);
    }
    /**
     * factory method to replace constructor
     * @param metadataVersion metadataVersion
     * @param majorNumber majorNumber
     * @param minorNumber minorNumber
     * @param microNumber The micro version number for this project
     * @param versionQualifier The version qualifier for this project
     * @param guid guid
     * @return IProjectPO
     */
    public static IProjectPO createProjectPO(Integer metadataVersion,
            Integer majorNumber, Integer minorNumber, Integer microNumber,
            String versionQualifier, String guid) {

        return new ProjectPO(metadataVersion, majorNumber, minorNumber,
                microNumber, versionQualifier, guid, false);
    }

    /**
     * factory method to replace constructor
     * @param testCaseName testCaseName
     * @return ISpecTestCasePO
     */
    public static ISpecTestCasePO createSpecTestCasePO(String testCaseName) {
        return new SpecTestCasePO(testCaseName, false);
    }

    /**
     * factory method to replace constructor
     * @param testCaseName testCaseName
     * @param guid guid
     * @return ISpecTestCasePO
     */
    public static ISpecTestCasePO createSpecTestCasePO(String testCaseName, 
        String guid) {
        
        return new SpecTestCasePO(testCaseName, guid, false);
    }
    
    /**
     * factory method to replace constructor
     * @param testCaseName testCaseName
     * @param isGenerated isGenerated
     * @return ISpecTestCasePO
     */
    public static ISpecTestCasePO createSpecTestCasePO(String testCaseName, 
            boolean isGenerated) {
        return new SpecTestCasePO(testCaseName, isGenerated);
    }

    /**
     * factory method to replace constructor
     * @param testSuiteName testSuiteName
     * @return ITestSuitePO
     */
    public static ITestSuitePO createTestSuitePO(String testSuiteName) {
        return new TestSuitePO(testSuiteName, false);
    }

    /**
     * factory method to replace constructor
     * @param testSuiteName testSuiteName
     * @param guid guid
     * @return ITestSuitePO
     */
    public static ITestSuitePO createTestSuitePO(String testSuiteName, 
        String guid) {
        
        return new TestSuitePO(testSuiteName, guid, false);
    }

    /**
     * factory method to replace constructor
     * @param testJobName testJobName
     * @return ITestJobPO
     */
    public static ITestJobPO createTestJobPO(String testJobName) {
        return new TestJobPO(testJobName, false);
    }

    /**
     * factory method to replace constructor
     * @param testJobName testJobName
     * @param guid guid
     * @return ITestJobPO
     */
    public static ITestJobPO createTestJobPO(String testJobName, 
        String guid) {
        
        return new TestJobPO(testJobName, guid, false);
    }

    /**
     * create a new TestSuite reference
     * @param ts TestSuite to be used
     * @param autId AutId for this entry
     * @return a reference to the TS
     */
    public static IRefTestSuitePO createRefTestSuitePO(ITestSuitePO ts, 
            String autId) {        
        return new RefTestSuitePO(null, ts.getGuid(), autId);
    }
    
    /**
     * create a new TestSuite reference
     * 
     * @param ts
     *            TestSuite to be used
     * @return a reference to the TS
     */
    public static IRefTestSuitePO createRefTestSuitePO(ITestSuitePO ts) {
        return new RefTestSuitePO(null, ts.getGuid(), null);
    }

     /**
      * recreate a TestSuite reference
      * 
      * @param name old name
      * @param guid old guid
      * @param tsGuid old ref guid
      * @param autId old autId
      * @return a reference to the TS
      */
    public static IRefTestSuitePO createRefTestSuitePO(String name,
            String guid, String tsGuid, String autId) {
        return new RefTestSuitePO(name, guid, tsGuid, autId);
    }
    
    /**
     * recreate a TestSuite reference
     * 
     * @param name old name
     * @param tsGuid old ref guid
     * @param autId old autId
     * @return a reference to the TS
     */
    public static IRefTestSuitePO createRefTestSuitePO(String name,
            String tsGuid, String autId) {
        return new RefTestSuitePO(name, tsGuid, autId);
    }
    
    /**
     * creates a TestCasePO (which could be a {@link ISpecTestCasePO} or {@link IExecTestCasePO} but we do not know.
     * This is needed for the TestResult since we only have the name and the UUID of the {@link INodePO}.
     * @param name the name
     * @param uuid the guid
     * @return a TestCasePO which is generated and has only a name and a uuid
     */
    public static ITestCasePO createTransientTestCasePO(String name,
            String uuid) {
        return new TestCasePO(name, uuid, true) {
            // empty since this should only be a transient object used for TestResults
        };
    }
    /**
     * creates a new instance of {@link TestResultAdditionPO}
     * @param commandLineText the text from the system error and output
     * @return new instance of {@link TestResultAdditionPO}
     */
    public static ITestResultAdditionPO createTestResultAddtionPO(
            String commandLineText) {
        return new TestResultAdditionPO(commandLineText);
    }
    
    
    /**
     * creates a new instance of {@link TestResultAdditionPO}
     * @param isJunitTestSuite boolean ofr determining whether the Testcase is to be used as a JUnitTestSuite
     * @return new instance of {@link TestResultAdditionPO} 
     */
    public static ITestResultAdditionPO creaTestResultAdditionPO(
            boolean isJunitTestSuite) {
        return new TestResultAdditionPO(isJunitTestSuite);
    }

    /**
     * factory method to replace constructor
     * @param comment comment
     * @return ICommentPO
     */
    public static ICommentPO createCommentPO(String comment) {
        return new CommentPO(comment);
    }

    /**
     * factory method to replace constructor
     * @param comment comment
     * @param guid guid
     * @return ICommentPO
     */
    public static ICommentPO createCommentPO(String comment, String guid) {
        return new CommentPO(comment, guid);
    }
    
    /**
     * creates a new instance of {@link ConditionalStatementPO}
     * @return the new instance of {@link ConditionalStatementPO}
     */
    public static IConditionalStatementPO createConditionalStatementPO() {
        return new ConditionalStatementPO(Messages.IfThenElseName);
    }
    
    /**
     * creates a new instance of {@link ConditionalStatementPO}
     * @param name name
     * @param guid guid
     * @return the new instance of {@link ConditionalStatementPO}
     */
    public static IConditionalStatementPO createConditionalStatementPO(
            String name, String guid) {
        return new ConditionalStatementPO(name, guid);
    }

    /**
     * creates a new instance of {@link DoWhilePO}
     * @return the new instance of {@link DoWhilePO}
     */
    public static IDoWhilePO createDoWhilePO() {
        return new DoWhilePO(Messages.DoWhileName);
    }

    /**
     * creates a new instance of {@link DoWhilePO}
     * @param name the name
     * @param guid the guid
     * @return the new instance of {@link DoWhilePO}
     */
    public static IDoWhilePO createDoWhilePO(String name, String guid) {
        return new DoWhilePO(name, guid);
    }
    
    /**
     * creates a new instance of {@link DoWhilePO}
     * @param name the name
     * @return the new instance of {@link DoWhilePO}
     */
    public static IDoWhilePO createDoWhilePO(String name) {
        return new DoWhilePO(name);
    }
    
    /**
     * creates a new instance of {@link WhileDoPO}
     * @return the new instance of {@link WhileDoPO}
     */
    public static IWhileDoPO createWhileDoPO() {
        return new WhileDoPO(Messages.WhileDoName);
    }

    /**
     * creates a new instance of {@link WhileDoPO}
     * @param name the name
     * @param guid the guid
     * @return the new instance of {@link WhileDoPO}
     */
    public static IWhileDoPO createWhileDoPO(String name, String guid) {
        return new WhileDoPO(name, guid);
    }
    
    /**
     * creates a new instance of {@link WhileDoPO}
     * @param name the name
     * @return the new instance of {@link WhileDoPO}
     */
    public static IWhileDoPO createWhileDoPO(String name) {
        return new WhileDoPO(name);
    }
    
    /**
     * Creates the same type of container as cont
     * @param cont the container
     * @return the new (empty) container
     */
    public static IControllerPO createControllerPO(IControllerPO cont) {
        IControllerPO copy = null;
        if (cont instanceof IConditionalStatementPO) {
            copy = createConditionalStatementPO(cont.getName());
        } else if (cont instanceof IDoWhilePO) {
            copy = createDoWhilePO(cont.getName());
        } else if (cont instanceof IWhileDoPO) {
            copy = createWhileDoPO(cont.getName());
        } else if (cont instanceof IIteratePO) {
            copy = createIteratePO(cont.getName());
            ((IIteratePO) cont).getDataManager().deepCopy(
                    ((IIteratePO) copy).getDataManager());
        } else {
            throw new UnsupportedOperationException("Unknown Controller type."); //$NON-NLS-1$
        }
        for (int i = 0; i < cont.getUnmodifiableNodeList().size(); i++) {
            copy.getUnmodifiableNodeList().get(i).setName(
                    cont.getUnmodifiableNodeList().get(i).getName());
        }
        return copy;
    }

    
    /**
     * creates a new instance of {@link WhileDoPO}
     * @return the new instance of {@link WhileDoPO}
     */
    public static IIteratePO createIteratePO() {
        return new IteratePO(Messages.RepeatName);
    }
    
    /**
     * creates a new instance of {@link IteratePO}
     * @param name the name
     * @return the new instance of {@link IteratePO}
     */
    public static IIteratePO createIteratePO(String name) {
        return new IteratePO(name);
    }
    
    /**
     * creates a new instance of {@link WhileDoPO}
     * @param name the name
     * @param guid the guid
     * @return the new instance of {@link WhileDoPO}
     */
    public static IIteratePO createIteratePO(String name, String guid) {
        return new IteratePO(name, guid);
    }
    
    /**
     * Creates a ContainerPO
     * @param name the name
     * @return the new ContainerPO with generated GUID
     */
    public static IAbstractContainerPO createContainerPO(String name) {
        return new ContainerPO(name);
    }
    
    /**
     * Creates a ContainerPO
     * @param name the name
     * @param guid the guid
     * @return the new ContainerPO with generated GUID
     */
    public static IAbstractContainerPO createContainerPO(
            String name, String guid) {
        return new ContainerPO(name, guid);
    }
    
    /**
     * creates a new instance of {@link ConditionalStatementPO}
     * @param name name
     * @return the new instance of {@link ConditionalStatementPO}
     */
    public static IConditionalStatementPO createConditionalStatementPO(
            String name) {
        return new ConditionalStatementPO(name);
    }
    
    /**
     * get the class instance of NodePO (needed by Persistor)
     * @return the class instance of NodePO
     */
    public static Class<NodePO> getNodePOClass() {
        return NodePO.class;
    }
    
    
    /**
     * get the class instance of ProjectPO (needed by XML deserialization)
     * @return the class instance of ProjectPO
     */
    public static Class<ProjectPO> getProjectPOClass() {
        return ProjectPO.class;
    }
    
    /**
     * get the class instance of TestSuitePO (needed by XML deserialization)
     * @return the class instance of TestSuitePO
     */
    public static Class<TestSuitePO> getTestSuitePOClass() {
        return TestSuitePO.class;
    }

    /**
     * get the class instance of TestJobPO (needed by XML deserialization)
     * @return the class instance of TestJobPO
     */
    public static Class<TestJobPO> getTestJobPOClass() {
        return TestJobPO.class;
    }

    /**
     * get the class instance of ParamNodePO (needed by Persistor)
     * @return the class instance of ParamNodePO
     */
    public static Class<ParamNodePO> getParamNodePOClass() {
        return ParamNodePO.class;
    }

    /**
     * get the class instance of CategoryPO (needed by Persistor)
     * @return the class instance of CategoryPO
     */
    public static Class<CategoryPO> getCategoryPOClass() {
        return CategoryPO.class;
    }

    /**
     * get the class instance of RefTestSuitePO (needed by Persistor)
     * @return the class instance of RefTestSuitePO
     */
    public static Class<RefTestSuitePO> getRefTestSuitePOClass() {
        return RefTestSuitePO.class;
    }

    /**
     * get the class instance of CapPO (needed by Persistor)
     * @return the class instance of CapPO
     */
    public static Class<CapPO> getCapPOClass() {
        return CapPO.class;
    }

    /**
     * get the class instance of TestCasePO (needed by Persistor)
     * @return the class instance of TestCasePO
     */
    public static Class<TestCasePO> getTestCasePOClass() {
        return TestCasePO.class;
    }

    /**
     * get the class instance of ExecTestCasePO (needed by Persistor)
     * @return the class instance of ExecTestCasePO
     */
    public static Class<ExecTestCasePO> getExecTestCasePOClass() {
        return ExecTestCasePO.class;
    }

    /**
     * get the class instance of SpecTestCasePO (needed by Persistor)
     * @return the class instance of SpecTestCasePO
     */
    public static Class<SpecTestCasePO> getSpecTestCasePOClass() {
        return SpecTestCasePO.class;
    }

    /**
     * get the class instance of EventExecTestCasePO (needed by Persistor)
     * @return the class instance of EventExecTestCasePO
     */
    public static Class<EventExecTestCasePO> getEventExecTestCasePOClass() {
        return EventExecTestCasePO.class;
    }

    /**
     * get the class instance of CommentPO (needed by Persistor)
     * @return the class instance of CommentPO
     */
    public static Class<CommentPO> getCommentPOClass() {
        return CommentPO.class;
    }
}
