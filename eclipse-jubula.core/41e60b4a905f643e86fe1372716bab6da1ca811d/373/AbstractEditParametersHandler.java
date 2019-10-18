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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.model.IModifiableParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jul 14, 2010
 */
public abstract class AbstractEditParametersHandler extends AbstractHandler {
    
    /**
     * 
     * @param event The execution event containing the context in which the
     *              receiver was executed.
     * @return the editor on which the receiver should operate.
     */
    protected IJBEditor getEditor(ExecutionEvent event) {
        // Use activePart rather than activeEditor because we want to make sure
        // that the editor is the active part. It is possible, for example, for
        // an editor to be the active editor (editor label is rendered with
        // a different background color than the labels for the other editors)
        // even though it is *not* the active part because a view is currently 
        // active (view's label is highlighted).
        final IWorkbenchPart activeEditor = HandlerUtil.getActivePart(event);
        if (activeEditor != null) {
            final Object adapter = 
                    activeEditor.getAdapter(IJBEditor.class);
            if (adapter != null) {
                return (IJBEditor)adapter;
            }
        }
        return null;
    }

    /**
     * Gets the new index of the Parameter with the given paramDesc. 
     * @param paramDesc the paramDesc
     * @param paramList the List of Parameters
     * @return the zero based index or -1 if not found.
     */
    protected static int getNewParamIndex(IParamDescriptionPO paramDesc, 
            List<Parameter> paramList) {
        // 1. search for GUID
        for (int i = 0; i < paramList.size(); i++) {
            String paramGuid = paramList.get(i).getGuid();
            if ((paramGuid != null) 
                    && (paramGuid.equals(paramDesc.getUniqueId()))) {
                return i;
            }
        }
        // 2. search for same name
        for (int i = 0; i < paramList.size(); i++) {
            if (paramList.get(i).getName().equals(paramDesc.getName())) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * @param <T> The type of the main persistence object to modify, e.g.
     *            {@link ISpecTestCase} or {@link ITestDataCubePO}
     * @param paramIntObj
     *            the {@link IModifiableParameterInterfacePO} which is to modify.
     * @param parameters
     *            the
     *            {@link org.eclipse.jubula.client.ui.rcp.dialogs.EditParametersDialog.Parameter}
     *            .
     * @param isInterfaceLocked
     *            the Lock Interface flag
     * @param mapper
     *            for management of param names
     * @param paramInterfaceBP
     *            the param interface business process to use for model changes
     * @return if occurs any modification of parameters
     */
    protected static
        <T extends IModifiableParameterInterfacePO> boolean editParameters(
            T paramIntObj,
            List<Parameter> parameters,
            boolean isInterfaceLocked,
            ParamNameBPDecorator mapper,
            AbstractParamInterfaceBP<T> paramInterfaceBP) {
        Map<String, IParamDescriptionPO> oldParamsMap =
                createOldParamsMap(paramIntObj);
        // find new parameters
        List<Parameter> paramsToAdd = new ArrayList<Parameter>();
        List<Parameter> params = new ArrayList<Parameter>(parameters);
        for (Parameter parameter : parameters) {
            if (parameter.getGuid() == null) {
                paramsToAdd.add(parameter);
                params.remove(parameter);
            }
        }
        // Find renamed parameters and parameters, which changed the usage,
        // if they have the same new name.
        Map<IParamDescriptionPO, String> paramsToRename =
            new HashMap<IParamDescriptionPO, String>();
        Map<IParamDescriptionPO, String> paramsToChangeUsage =
                new HashMap<IParamDescriptionPO, String>();
        for (Parameter param : params) {
            IParamDescriptionPO paramDescr = oldParamsMap.get(param.getGuid());
            if (paramDescr != null) {
                if (!(paramDescr.getName().equals(param.getName()))) {
                    Parameter paramNotRenamed = getNotRenamedParamWithSameName(
                            oldParamsMap, params, param);
                    if (paramNotRenamed == null) {
                        // rename the first usage
                        paramsToRename.put(paramDescr, param.getName());
                    } else {
                        // change the usage to the first parameter with the same name
                        paramsToChangeUsage.put(paramDescr,
                                paramNotRenamed.getGuid());
                    }
                }
            } else {
                Assert.notReached(Messages.UnexpectedError 
                    + StringConstants.COLON + StringConstants.SPACE
                    + Messages.ModificationOfNonExistingParameter 
                    + StringConstants.DOT);
            }
        }
        // find parameters to remove
        List<IParamDescriptionPO> paramsToRemove = findParamsToRemove(
                parameters, oldParamsMap);
        boolean isInterfaceLockedChanged = false;
        ISpecTestCasePO specTc = null;
        if (paramIntObj instanceof ISpecTestCasePO) {
            specTc = (ISpecTestCasePO)paramIntObj;
            isInterfaceLockedChanged = !((specTc).isInterfaceLocked() 
                    == isInterfaceLocked);
            TestCaseParamBP.setInterfaceLocked(specTc, isInterfaceLocked);
        }
        // update model
        updateModel(paramIntObj, mapper, paramInterfaceBP, paramsToAdd,
                paramsToRename, paramsToChangeUsage, paramsToRemove);
        final boolean moved = moveParameters(paramIntObj, parameters);
        // changes have been made, if one or more lists/maps are not empty
        return     !paramsToRemove.isEmpty()
                || !paramsToAdd.isEmpty()
                || !paramsToRename.isEmpty()
                || !paramsToChangeUsage.isEmpty()
                || moved
                || isInterfaceLockedChanged;
    }

    /**
     * Notify the abstract parameter interface BP of the calculated parameters,
     * which are new, removed, renamed or the usage have been changed.
     * @param <T> The type of the node working at.
     * @param paramIntObj The node working at.
     * @param mapper The parameter name mapping.
     * @param paramInterfaceBP The parameter interface BP.
     * @param paramsToAdd The list of parameters to add.
     * @param paramsToRename The map of renamed parameters.
     * @param paramsToChangeUsage The map of changed parameters.
     * @param paramsToRemove The list of removed parameter descriptions.
     */
    private static <T extends IModifiableParameterInterfacePO> void updateModel(
            T paramIntObj, ParamNameBPDecorator mapper,
            AbstractParamInterfaceBP<T> paramInterfaceBP,
            List<Parameter> paramsToAdd,
            Map<IParamDescriptionPO, String> paramsToRename,
            Map<IParamDescriptionPO, String> paramsToChangeUsage,
            List<IParamDescriptionPO> paramsToRemove) {
        // add
        for (Parameter addParam : paramsToAdd) {
            paramInterfaceBP.addParameter(addParam.getName(), addParam
                    .getType(), paramIntObj, mapper);
        }
        // remove
        for (IParamDescriptionPO desc : paramsToRemove) {
            paramInterfaceBP.removeParameter(desc, paramIntObj);
        }
        // rename
        for (IParamDescriptionPO desc : paramsToRename.keySet()) {
            paramInterfaceBP.renameParameters(
                    desc, paramsToRename.get(desc), mapper);
        }
        // usage changed
        for (IParamDescriptionPO desc : paramsToChangeUsage.keySet()) {
            paramInterfaceBP.changeUsageParameter(paramIntObj,
                    desc, paramsToChangeUsage.get(desc), mapper);
        }
    }

    /**
     * @param parameters The list of new parameters.
     * @param oldParams The map of old parameter GUIDs to
     *                  their parameter description.
     * @return The list of parameter descriptions to be removed.
     */
    private static List<IParamDescriptionPO> findParamsToRemove(
            List<Parameter> parameters,
            Map<String, IParamDescriptionPO> oldParams) {
        List<String> oldGuids = new ArrayList<String>(oldParams.keySet());
        for (Parameter parameter : parameters) {
            oldGuids.remove(parameter.getGuid());
        }
        List<IParamDescriptionPO> paramsToRemove =
            new ArrayList<IParamDescriptionPO>();
        for (String oldGuid : oldGuids) {
            paramsToRemove.add(oldParams.get(oldGuid));
        }
        return paramsToRemove;
    }

    /**
     * @param paramIntObj The node working on.
     * @return The map of parameter description UUIDs to it parameter description.
     */
    private static Map<String, IParamDescriptionPO> createOldParamsMap(
            IModifiableParameterInterfacePO paramIntObj) {
        Map<String, IParamDescriptionPO> oldParams =
            new HashMap<String, IParamDescriptionPO>();
        List<IParamDescriptionPO> paramList = paramIntObj.getParameterList();
        for (IParamDescriptionPO oldDesc : paramList) {
            oldParams.put(oldDesc.getUniqueId(), oldDesc);
        }
        return oldParams;
    }

    /**
     * @param oldParamsMap The map of GUIDs to old parameter descriptions.
     * @param params The list of new parameters.
     * @param param The parameter searching for in the list.
     * @return The first different not renamed parameter with the same name
     *         as the given parameter, or null, if it has not been found.
     */
    private static Parameter getNotRenamedParamWithSameName(
            Map<String, IParamDescriptionPO> oldParamsMap,
            List<Parameter> params,
            Parameter param) {
        for (Parameter paramSearch : params) {
            if (paramSearch != param) {
                IParamDescriptionPO oldParam =
                        oldParamsMap.get(paramSearch.getGuid());
                if (oldParam.getName().equals(param.getName())) {
                    return paramSearch;
                }
            }
        }
        return null;
    }

    /**
     * Moves the Parameters of the given IModifiableParameterInterfacePO into
     * the order of the given parameter List (parameters).<br>
     * <b>Note: Call this method after all other model changes!</b>
     * 
     * @param paramIntObj
     *            the IParameterInterfacePO.
     * @param parameters
     *            the Parameter List with the new order.
     * @return true if oe or more parameters were moved, false otherwise.
     */
    private static boolean moveParameters(
            IModifiableParameterInterfacePO paramIntObj,
            List<Parameter> parameters) {

        boolean moved = false;
        final List<IParamDescriptionPO> paramList = 
            new LinkedList<IParamDescriptionPO>(
                paramIntObj.getParameterList());
        for (IParamDescriptionPO paramDesc : paramList) {
            final int currIdx = paramList.indexOf(paramDesc);
            final int newIdx = getNewParamIndex(paramDesc, parameters);
            if (currIdx != newIdx) {
                paramIntObj.moveParameter(paramDesc.getUniqueId(), newIdx);
                moved = true;
            }
        }
        return moved;
    }
    
    /**
     * @param paramIntObj
     *            the {@link IModifiableParameterInterfacePO} which is to
     *            modify.
     * @param parameters
     *            the
     *            {@link org.eclipse.jubula.client.ui.rcp.dialogs.EditParametersDialog.Parameter}
     *            .
     * @param mapper
     *            for management of param names
     * @param paramInterfaceBP
     *            the param interface business process to use for model changes
     * @return if occurs any modification of parameters
     */
    public static boolean editParameters(
            ITestDataCubePO paramIntObj,
            List<Parameter> parameters, ParamNameBPDecorator mapper,
            AbstractParamInterfaceBP<ITestDataCubePO> paramInterfaceBP) {
        return editParameters(paramIntObj, parameters, false, mapper,
                paramInterfaceBP);
    }
    
}
