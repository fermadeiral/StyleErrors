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
package org.eclipse.jubula.client.ui.constants;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.utils.ImageUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 31.07.2006
 */
public class IconConstants {
    /** m_imageCache */
    public static final Map<ImageDescriptor, Image> 
        CACHE = new HashMap<ImageDescriptor, Image>();
    /** error image */
    public static final Image ERROR_IMAGE = getImage("error.gif"); //$NON-NLS-1$
    /** warning image */
    public static final Image WARNING_IMAGE = getImage("warning.gif"); //$NON-NLS-1$
    /** warning small image */
    public static final ImageDescriptor WARNING_IMAGE_DESCRIPTOR = PlatformUI
            .getWorkbench().getSharedImages()
            .getImageDescriptor(ISharedImages.IMG_DEC_FIELD_WARNING);
    
    /** PROFILE_FILTER_OFF image descriptor */
    public static final ImageDescriptor PROFILE_FILTER_OFF_DESCRIPTOR = getImageDescriptor("find.gif"); //$NON-NLS-1$
    
    /** PROFILE_FILTER_ON image descriptor */
    public static final ImageDescriptor PROFILE_FILTER_ON_DESCRIPTOR = getImageDescriptor("filterOff.gif"); //$NON-NLS-1$
    
    /** info image descriptor */
    public static final ImageDescriptor INFO_IMAGE_DESCRIPTOR = getImageDescriptor("info.gif"); //$NON-NLS-1$
    /** link with editor image descriptor */
    public static final ImageDescriptor LINK_WITH_EDITOR_DESCRIPTOR = getImageDescriptor("linkWithEditor.gif"); //$NON-NLS-1$
    /** info image */
    public static final Image INFO_IMAGE = INFO_IMAGE_DESCRIPTOR.createImage();
    /** testSuiteeditor image */
    public static final Image DISABLED_TS_EDITOR_IMAGE = getImage("testSuiteEditor_disabled.gif"); //$NON-NLS-1$
    /** testSuiteeditor image */
    public static final Image TS_EDITOR_IMAGE = getImage("testSuiteEditor.gif"); //$NON-NLS-1$
    /** long running image */
    public static final Image LONG_RUNNING_IMAGE = getImage("longRunning.gif"); //$NON-NLS-1$
    /** testCaseEditor image */
    public static final Image DISABLED_TC_EDITOR_IMAGE = getImage("specTcEditor_disabled.gif"); //$NON-NLS-1$
    /** testCaseEditor image */
    public static final Image TC_EDITOR_IMAGE = getImage("specTcEditor.gif"); //$NON-NLS-1$
    /** disabled test job editor image */
    public static final Image DISABLED_TJ_EDITOR_IMAGE = getImage("tjEditor_disabled.gif"); //$NON-NLS-1$
    /** disabled central test data editor image */
    public static final Image DISABLED_CTD_EDITOR_IMAGE = getImage("ctdEditor_disabled.gif"); //$NON-NLS-1$
    /** ObjectMappingEditor image */
    public static final Image DISABLED_OM_EDITOR_IMAGE = getImage("omEditor_disabled.gif"); //$NON-NLS-1$
    /** delete image */
    public static final Image DELETE_IMAGE = getImage("delete.gif"); //$NON-NLS-1$
    /** disabled delete image */
    public static final Image DELETE_IMAGE_DISABLED = 
        getImage("delete_disabled.gif"); //$NON-NLS-1$
    /** new TestCase imageDescriptor */
    public static final ImageDescriptor NEW_TC_IMAGE_DESCRIPTOR = getImageDescriptor("newTestCaseIcon.gif"); //$NON-NLS-1$
    /** new TestCase_disabled imageDescriptor */
    public static final ImageDescriptor NEW_TC_DISABLED_IMAGE_DESCRIPTOR = getImageDescriptor("newTestCaseIcon_disabled.gif"); //$NON-NLS-1$
    /** refresh imageDescriptor */
    public static final ImageDescriptor REFRESH_IMAGE_DESCRIPTOR = getImageDescriptor("refresh.gif"); //$NON-NLS-1$
    /** refresh_diabled imageDescriptor */
    public static final ImageDescriptor REFRESH_DISABLED_IMAGE_DESCRIPTOR = getImageDescriptor("refresh_disabled.gif"); //$NON-NLS-1$
    /** add EventHandler image */
    public static final Image ADD_EH_IMAGE = getImage("addEH.gif"); //$NON-NLS-1$
    /** add new referenced TestCase imageDescriptor */
    public static final ImageDescriptor NEW_REF_TC_IMAGE_DESCRIPTOR = getImageDescriptor("testCaseRefNew.gif"); //$NON-NLS-1$
    /** add new referenced TestCase_diabled imageDescriptor */
    public static final ImageDescriptor NEW_REF_TC_DISABLED_IMAGE_DESCRIPTOR = getImageDescriptor("testCaseRefNew_disabled.gif"); //$NON-NLS-1$
    /** delete project dialog-image image */
    public static final Image DELETE_PROJECT_DIALOG_IMAGE = getImage("deleteProject_big.gif"); //$NON-NLS-1$
    /** name for import project image */
    public static final Image IMPORT_PROJECT = getImage("importProject.gif"); //$NON-NLS-1$
    /** ITE log view image */
    public static final Image ITE_LOG_VIEW = getImage("clientLogView.gif"); //$NON-NLS-1$
    /** rc log view image */
    public static final Image RC_LOG_VIEW = getImage("serverLogView.gif"); //$NON-NLS-1$
    /** name for new CAP dialog image */
    public static final String NEW_CAP_DIALOG_STRING = "newCAPDialog.gif"; //$NON-NLS-1$
    /** new test data cube dialog image */
    public static final Image NEW_TESTDATAMANAGER_DIALOG_IMAGE = getImage("newTestDataCubeDialog.gif"); //$NON-NLS-1$
    /** rename test data cube dialog image */
    public static final Image RENAME_TESTDATAMANAGER_DIALOG_IMAGE = getImage("renameTestDataCubeDialog.gif"); //$NON-NLS-1$
    /** name for new component dialog image */
    public static final String NEW_COMPONENT_DIALOG_STRING = "addLogicalNameDialog.gif"; //$NON-NLS-1$
    /** open project dialog-image image */
    public static final Image OPEN_PROJECT_DIALOG_IMAGE = getImage("chooseProject.gif"); //$NON-NLS-1$
    /** name for observe TestCase dialog image */
    public static final String OBSERVE_TC_DIALOG_STRING = "recordTestCaseDialog.gif"; //$NON-NLS-1$
    /** name for big project image */
    public static final String BIG_PROJECT_STRING = "bigProject.gif"; //$NON-NLS-1$
    /** The Step Testing image */
    public static final ImageDescriptor STEP_TESTING_IMAGE_DESCRIPTOR = getImageDescriptor("StepTesting.gif"); //$NON-NLS-1$
    /** The Step Testing image */
    public static final Image STEP_TESTING_IMAGE = STEP_TESTING_IMAGE_DESCRIPTOR
            .createImage();
    /** The StepSkipped descriptor */
    public static final ImageDescriptor STEP_SKIPPED_IMAGE_DESCRIPTOR = getImageDescriptor("StepSkipped.gif"); //$NON-NLS-1$
    /** The StepSkipped image */
    public static final Image STEP_SKIPPED_IMAGE = STEP_SKIPPED_IMAGE_DESCRIPTOR
            .createImage();
    /** The StepOkContainsSkipped descriptor */
    public static final ImageDescriptor STEP_SUCCESS_SKIPPED_IMAGE_DESCRIPTOR = getImageDescriptor("StepOKContainsSkipped.gif"); //$NON-NLS-1$
    /** The StepSkipped image */
    public static final Image STEP_SUCCESS_SKIPPED_IMAGE = 
            STEP_SUCCESS_SKIPPED_IMAGE_DESCRIPTOR.createImage();
    /** The StepNotOK  */
    public static final ImageDescriptor STEP_NOT_OK_IMAGE_DESCRIPTOR = getImageDescriptor("StepNotOK.gif"); //$NON-NLS-1$
    /** The StepNotOK  */
    public static final Image STEP_NOT_OK_IMAGE = STEP_NOT_OK_IMAGE_DESCRIPTOR
            .createImage();
    /** The stepOK descriptor */
    public static final ImageDescriptor STEP_OK_IMAGE_DESCRIPTOR = getImageDescriptor("StepOK.gif"); //$NON-NLS-1$
    /** The stepOK descriptor */
    public static final Image STEP_OK_IMAGE = STEP_OK_IMAGE_DESCRIPTOR
            .createImage();
    /** Step failed */
    public static final ImageDescriptor STEP_FAILED_IMAGE_DESCRIPTOR = getImageDescriptor("StepFailed.gif"); //$NON-NLS-1$
    /** Step failed */
    public static final Image STEP_FAILED_IMAGE = STEP_FAILED_IMAGE_DESCRIPTOR
            .createImage();
    /** The retryingStep descriptor */
    public static final ImageDescriptor STEP_RETRY_IMAGE_DESCRIPTOR = getImageDescriptor("StepRetry.png"); //$NON-NLS-1$
    /** The retryingStep image */
    public static final Image STEP_RETRY_IMAGE = STEP_RETRY_IMAGE_DESCRIPTOR
            .createImage();
    /** The retryStepOK descriptor */
    public static final ImageDescriptor STEP_RETRY_OK_IMAGE_DESCRIPTOR = getImageDescriptor("StepRetryOK.png"); //$NON-NLS-1$
    /** The retryStepOK descriptor */
    public static final Image STEP_RETRY_OK_IMAGE = 
        STEP_RETRY_OK_IMAGE_DESCRIPTOR.createImage();
    /** reference value image */
    public static final Image REF_VALUE_IMAGE = getImage("refValue.gif"); //$NON-NLS-1$
    /** deprecated action image */
    public static final Image DEPRECATED_IMAGE = getImage("depricated.gif"); //$NON-NLS-1$
    /** read only image */
    public static final Image READ_ONLY_IMAGE = getImage("readonly.gif"); //$NON-NLS-1$
    /** incomplete data image */
    public static final Image INCOMPLETE_DATA_IMAGE = getImage("StepNotOK.gif"); //$NON-NLS-1$
    /** incomplete data image */
    public static final Image OPTIONAL_DATA_IMAGE = getImage("optionalData.gif"); //$NON-NLS-1$
    /** original data image */
    public static final Image ORIGINAL_DATA_IMAGE = getImage("orginalData.gif"); //$NON-NLS-1$
    /** overwritten data image */
    public static final Image OVERWRITTEN_DATA_IMAGE = getImage("overwrittenData.gif"); //$NON-NLS-1$
    /** complete data imageDescriptor */
    public static final ImageDescriptor ERROR_IMAGE_DESCRIPTOR = getImageDescriptor("incomplData.gif"); //$NON-NLS-1$
    /** excel data imageDescriptor */
    public static final ImageDescriptor EXCEL_DATA_IMAGE_DESCRIPTOR = getImageDescriptor("excelData.gif"); //$NON-NLS-1$
    /** greenDot imageDescriptor */
    public static final ImageDescriptor GREEN_DOT_IMAGE_DESCRIPTOR = getImageDescriptor("greenDot.gif"); //$NON-NLS-1$
    /** redDot imageDescriptor */
    public static final ImageDescriptor RED_DOT_IMAGE_DESCRIPTOR = getImageDescriptor("redDot.gif"); //$NON-NLS-1$
    /** yellowDot imageDescriptor */
    public static final ImageDescriptor YELLOW_DOT_IMAGE_DESCRIPTOR = getImageDescriptor("yellowDot.gif"); //$NON-NLS-1$
    /** greenDot imageDescriptor */
    public static final Image TECH_NAME_OK_IMAGE = getImageDescriptor("techNameOK.gif").createImage(); //$NON-NLS-1$
    /** redDot imageDescriptor */
    public static final Image TECH_NAME_ERROR_IMAGE = getImageDescriptor("techNameERR.gif").createImage(); //$NON-NLS-1$
    /** yellowDot imageDescriptor */
    public static final Image TECH_NAME_WARNING_IMAGE = getImageDescriptor("techNameWARN.gif").createImage(); //$NON-NLS-1$
    /** excel data image */
    public static final Image EXCEL_DATA_IMAGE = 
        EXCEL_DATA_IMAGE_DESCRIPTOR.createImage();
    /** new event handler dialog-image */
    public static final Image NEW_EH_DIALOG_IMAGE = getImage("newEventHandlerDialog.gif"); //$NON-NLS-1$
    /** name for class path image */
    public static final String CLASS_PATH_STRING = "classpath.png"; //$NON-NLS-1$
    /** up arrow image */
    public static final Image UP_ARROW_IMAGE = getImage("upArrow.gif"); //$NON-NLS-1$
    /** down arrow image */
    public static final Image DOWN_ARROW_IMAGE = getImage("downArrow.gif"); //$NON-NLS-1$
    /** up arrow disabled image */
    public static final Image UP_ARROW_DIS_IMAGE = getImage("upArrow_disabled.gif"); //$NON-NLS-1$
    /** down arrow disabled image */
    public static final Image DOWN_ARROW_DIS_IMAGE = getImage("downArrow_disabled.gif"); //$NON-NLS-1$
    /** right arrow image */
    public static final Image RIGHT_ARROW_IMAGE = getImage("rightArrow.gif"); //$NON-NLS-1$
    /** left arrow image */
    public static final Image LEFT_ARROW_IMAGE = getImage("leftArrow.gif"); //$NON-NLS-1$
    /** right arrow disabled image */
    public static final Image RIGHT_ARROW_DIS_IMAGE = getImage("rightArrow_disabled.gif"); //$NON-NLS-1$
    /** left arrow disabled image */
    public static final Image LEFT_ARROW_DIS_IMAGE = getImage("leftArrow_disabled.gif"); //$NON-NLS-1$
    /** double right arrow disabled image */
    public static final Image DOUBLE_RIGHT_ARROW_DIS_IMAGE = getImage("allRightArrow_disabled.gif"); //$NON-NLS-1$
    /** double left arrow disabled image */
    public static final Image DOUBLE_LEFT_ARROW_DIS_IMAGE = getImage("allLeftArrow_disabled.gif"); //$NON-NLS-1$
    /** swap arrow disabled image */
    public static final Image SWAP_ARROW_DIS_IMAGE = getImage("swapArrow_disabled.gif"); //$NON-NLS-1$
    /** double right arrow image */
    public static final Image DOUBLE_RIGHT_ARROW_IMAGE = getImage("allRightArrow.gif"); //$NON-NLS-1$
    /** double left arrow image */
    public static final Image DOUBLE_LEFT_ARROW_IMAGE = getImage("allLeftArrow.gif"); //$NON-NLS-1$
    /** swap arrow image */
    public static final Image SWAP_ARROW_IMAGE = getImage("swapArrow.gif"); //$NON-NLS-1$
    /** db login dialog-image */
    public static final Image DB_LOGIN_DIALOG_IMAGE = getImage("dblogin.gif"); //$NON-NLS-1$
    /** import dialog-image */
    public static final Image IMPORT_DIALOG_IMAGE = getImage("import_big.gif"); //$NON-NLS-1$
    /** import dialog-image descriptor */
    public static final ImageDescriptor IMPORT_DIALOG_IMAGE_DESCRIPTOR = getImageDescriptor("import_big.gif"); //$NON-NLS-1$
    /** new cap dialog-image */
    public static final Image NEW_CAP_DIALOG_IMAGE = getImage(
            NEW_CAP_DIALOG_STRING); 
    /** new component dialog-image */
    public static final Image NEW_COMPONENT_DIALOG_IMAGE = 
        getImage(NEW_COMPONENT_DIALOG_STRING); 
    /** server port dialog-image */
    public static final Image SERVER_PORT_DIALOG_IMAGE = getImage("port.gif"); //$NON-NLS-1$
    /** project dialog-image */
    public static final Image PROJECT_DIALOG_IMAGE = getImage("projectAction.gif"); //$NON-NLS-1$
    /** add test case dialog-image */
    public static final Image ADD_TC_DIALOG_IMAGE = getImage("addTC.gif"); //$NON-NLS-1$
    /** open test case dialog-image */
    public static final Image OPEN_TC_DIALOG_IMAGE = getImage("openTC.gif"); //$NON-NLS-1$
    /** event handler CAP image */
    public static final Image EH_CAP_IMAGE = getImage("EventHandlerCap.gif"); //$NON-NLS-1$
    /** clock image */
    public static final Image CLOCK_IMAGE = getImage("longRunning.gif"); //$NON-NLS-1$
    /** missing project image */
    public static final Image MISSING_PROJECT_IMAGE = getImage("missingReusedProject.gif"); //$NON-NLS-1$
    /** aut running image */
    public static final Image AUT_RUNNING_IMAGE = getImage("AUTup.gif"); //$NON-NLS-1$
    /** propagate image */
    public static final Image PROPAGATE_IMAGE = getImage("propagate.gif"); //$NON-NLS-1$
    /** global name image */
    public static final Image GLOBAL_NAME_IMAGE = getImage("globalName.gif"); //$NON-NLS-1$
    /** global name_disabled image */
    public static final Image GLOBAL_NAME_DISABLED_IMAGE = getImage("globalName_disabled.gif"); //$NON-NLS-1$
    /** local name image */
    public static final Image LOCAL_NAME_IMAGE = getImage("localName.gif"); //$NON-NLS-1$
    /** local name_disabled image */
    public static final Image LOCAL_NAME_DISABLED_IMAGE = getImage("localName_disabled.gif"); //$NON-NLS-1$
    /** global name image */
    public static final Image AUT_COMP_NAME_IMAGE = getImage("autCompName.gif"); //$NON-NLS-1$
    /** global name_disabled image */
    public static final Image AUT_COMP_NAME_DISABLED_IMAGE = getImage("autCompName_disabled.gif"); //$NON-NLS-1$
    /** project wizard imageDescriptor */
    public static final ImageDescriptor PROJECT_WIZARD_IMAGE_DESCRIPTOR = getImageDescriptor("ProjectWizard.gif"); //$NON-NLS-1$
    /** name for move test case dialog-image */
    public static final String MOVE_TC_DIALOG_STRING = "moveTestCaseDialog.gif"; //$NON-NLS-1$
    /** name for new test case dialog-image */
    public static final String NEW_TC_DIALOG_STRING = "newTestCaseDialog.gif"; //$NON-NLS-1$
    /** name for new test suite dialog-image */
    public static final String NEW_TS_DIALOG_STRING = "newTestSuiteDialog.gif"; //$NON-NLS-1$
    /** name for new test job dialog-image */
    public static final String NEW_TJ_DIALOG_STRING = "newTestJobDialog.gif"; //$NON-NLS-1$
    /** name for test job dialog-image */
    public static final String TJ_DIALOG_STRING = "testJobDialog.gif"; //$NON-NLS-1$
    /** name for new category dialog-image */
    public static final String NEW_CAT_DIALOG_STRING = "newCategoryDialog.gif"; //$NON-NLS-1$
    /** CAP image */
    public static final Image CAP_IMAGE = getImage("cap.gif"); //$NON-NLS-1$
    /** category image */
    public static final Image CATEGORY_IMAGE = getImage("category.gif"); //$NON-NLS-1$
    /** event handler image */
    public static final Image EH_IMAGE = getImage("execEventHandler.gif"); //$NON-NLS-1$
    /** event handler image */
    public static final Image RESULT_EH_IMAGE = getImage("EventHandler.gif"); //$NON-NLS-1$
    /** referenced testCase image */
    public static final Image TC_REF_IMAGE = getImage("testCaseRef.gif"); //$NON-NLS-1$
    /** referenced testSuite image */
    public static final Image TS_REF_IMAGE = getImage("testSuiteRef.gif"); //$NON-NLS-1$
    /** testSuite validate image */
    public static final Image TS_VAL_IMAGE = getImage("TestSuite_validate.gif"); //$NON-NLS-1$
    /** logical name image */
    public static final Image LOGICAL_NAME_IMAGE = getImage("OMLogName.gif"); //$NON-NLS-1$
    /** logical name image */
    public static final Image PROPAGATED_LOGICAL_NAME_IMAGE = getImage("PropagatedOMLogName.gif"); //$NON-NLS-1$
    /** technical name image */
    public static final Image TECHNICAL_NAME_IMAGE = getImage("OMTecName.gif"); //$NON-NLS-1$
    /** project image */
    public static final Image PROJECT_IMAGE = getImage("project.gif"); //$NON-NLS-1$
    /** testSuite image */
    public static final Image TS_IMAGE = getImage("testSuiteNode.gif"); //$NON-NLS-1$
    /** test data cube decorator image descriptor */
    public static final ImageDescriptor TDC_DECORATION_IMAGE_DESCRIPTOR = 
        getImageDescriptor("testDataCubeDecoration.gif"); //$NON-NLS-1$
    /** test data cube image */
    public static final Image TDC_IMAGE = getImage("testDataCube.gif"); //$NON-NLS-1$
    /** comment image */
    public static final Image COMMENT_IMAGE = getImage("file_obj.png"); //$NON-NLS-1$
    /** conditional statement image */
    public static final Image CONDITION = getImage("condition.png"); //$NON-NLS-1$
    /** do while image */
    public static final Image DO_WHILE = getImage("dowhile.png"); //$NON-NLS-1$
    /** while do image */
    public static final Image WHILE_DO = getImage("whiledo.png"); //$NON-NLS-1$
    /** container statement image */
    public static final Image CONTAINER = getImage("container.png"); //$NON-NLS-1$
    /** iterate image */
    public static final Image ITERATE = getImage("repeat.png"); //$NON-NLS-1$
    /** comment image */
    public static final ImageDescriptor COMMANDLOG_IMAGE_DESCRIPTOR = getImageDescriptor("dataDecorator.png"); //$NON-NLS-1$
    /** testJob image */
    public static final Image TJ_IMAGE = getImage("testJobNode.gif"); //$NON-NLS-1$
    /** test case image */
    public static final Image TC_IMAGE = getImage("testCase.gif"); //$NON-NLS-1$
    /** testCase_disabled image */
    /** test case image */
    public static final Image ROOT_IMAGE = getImage("root.gif"); //$NON-NLS-1$
    /** test case image */
    public static final Image PROBLEM_CAT_IMAGE = getImage("problemCategory.gif"); //$NON-NLS-1$
    /** name for new test case dialog-image */
    public static final String RENAME_TC_DIALOG_STRING = "renameTC.gif"; //$NON-NLS-1$
    /** name for new test suite dialog-image */
    public static final String RENAME_TS_DIALOG_STRING = "renameTS.gif"; //$NON-NLS-1$
    /** name for new category dialog-image */
    public static final String RENAME_CAT_DIALOG_STRING = "category_big.gif"; //$NON-NLS-1$
    /** name for new category dialog-image */
    public static final String RENAME_CAP_DIALOG_STRING = "renameCAP.gif"; //$NON-NLS-1$
    /** name for rename logical name dialog image */
    public static final String RENAME_COMPONENT_DIALOG_STRING = "renameLogicalName.gif"; //$NON-NLS-1$
    /** name for new test case dialog-image */
    public static final String RENAME_EH_DIALOG_STRING = "renameEH.gif"; //$NON-NLS-1$
    /** name for new test suite dialog-image */
    public static final String RENAME_PROJECT_DIALOG_STRING = "renameProject.gif"; //$NON-NLS-1$
    /** Mail image */
    public static final ImageDescriptor MAIL = getImageDescriptor("eMail.gif"); //$NON-NLS-1$
    /** merge component name dialog image */
    public static final Image MERGE_COMPONENT_NAME_DIALOG_IMAGE = getImage("mergeLogicalNameDialog.gif"); //$NON-NLS-1$
    /** new component dialog-image */
    public static final Image RENAME_COMPONENT_DIALOG_IMAGE = 
        getImage(RENAME_COMPONENT_DIALOG_STRING);

    /** no server image */
    public static final Image NO_SERVER_IMAGE = getImage("NoServer.gif"); //$NON-NLS-1$
    /** no connection image */
    public static final Image NO_CONNECTION_IMAGE = getImage("NoSC.gif"); //$NON-NLS-1$
    /** camera image */
    public static final Image CAM_IMAGE = getImage("cam.gif"); //$NON-NLS-1$
    /** checkcamera image */
    public static final Image CHECK_CAM_IMAGE = getImage("checkcam.gif"); //$NON-NLS-1$
    /** map image */
    public static final Image MAP_IMAGE = getImage("map.gif"); //$NON-NLS-1$
    /** pause image */
    public static final Image PAUSE_IMAGE = getImage("pause.gif"); //$NON-NLS-1$
    /** no aut image */
    public static final Image NO_AUT_IMAGE = getImage("NoAUT.gif"); //$NON-NLS-1$
    /** TestResultSummaryView details image */
    public static final Image TRSV_DETAILS = getImage("TRSV_Details.png"); //$NON-NLS-1$
    /** TestResultSummaryView no details image */
    public static final Image TRSV_NODETAILS = getImage("TRSV_NoDetails.png"); //$NON-NLS-1$
    /** Start OM gif, used for CTDS search result decoration as well */
    public static final Image START_OM = getImage("startOM.gif"); //$NON-NLS-1$
    /** Stop OM gif, used for CTDS search result decoration as well */
    public static final Image STOP_OM = getImage("stopOM.gif"); //$NON-NLS-1$
    /** icon for specific profile **/
    public static final ImageDescriptor TRIANGLE_OVERLAY = getImageDescriptor("triangleOverlay.gif"); //$NON-NLS-1$
    
    /** to prevent instantiation */
    private IconConstants() {
        // do nothing
    }
    
    /** 
     * @param fileName Object
     * @return Image
     */
    public static Image getImage(String fileName) {
        ImageDescriptor descriptor = null;
        descriptor = getImageDescriptor(fileName);
        // obtain the cached image corresponding to the descriptor
        Image image = CACHE.get(descriptor);
        if (image == null) {
            image = descriptor.createImage();
            CACHE.put(descriptor, image);
        }
        return image;
    }

    /**
     * @param name String
     * @return ImageDescriptor from URL
     */
    public static ImageDescriptor getImageDescriptor(String name) {
        return ImageUtils.getImageDescriptor(
            Plugin.getDefault().getBundle(), name);
    }
    
    /**
     * Puts the given overlay on top of another image
     * @param image the image
     * @param overlay the overlay
     * @param quadrant the position
     * @return image
     */
    public static Image decorateImage(Image image, ImageDescriptor overlay,
            int quadrant) {
        DecorationOverlayIcon icon = new DecorationOverlayIcon(image, overlay,
                quadrant);
        Image img = CACHE.get(icon);
        if (img == null) {
            img = icon.createImage();
            CACHE.put(icon, img);
        }
        return img;
    }
}