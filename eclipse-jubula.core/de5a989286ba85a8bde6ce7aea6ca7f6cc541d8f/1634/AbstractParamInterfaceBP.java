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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IModifiableParameterInterfacePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.IValueCommentPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.utils.ComboParamValidator;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.core.utils.IntegerParamValueValidator;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.NullValidator;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.VariableParamValueValidator;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;


/**
 * @author BREDEX GmbH
 * @created Jul 13, 2010
 * @param <T>
 *            parameterized type
 */
public abstract class AbstractParamInterfaceBP<T> {
    /**
     * Adds a new data set into the given row of the given IParameterInterface
     * 
     * @param obj
     *            the IParameterInterface object
     * @param row
     *            the row to insert
     */
    public void addDataSet(IParameterInterfacePO obj, int row) {
        ISpecTestCasePO spec = null;
        ITDManager dataManager = null;
        List<String> list = new ArrayList<String>();
        if (obj instanceof IExecTestCasePO) {
            IExecTestCasePO exec = (IExecTestCasePO) obj;
            spec = exec.getSpecTestCase();
            dataManager = exec.getDataManager();
        } else if (obj instanceof ISpecTestCasePO) {
            spec = (ISpecTestCasePO) obj;
            dataManager = spec.getDataManager();
        } else if (obj instanceof ITestDataCubePO) {
            dataManager = obj.getDataManager();
        }
        if (dataManager != null && spec != null) {
            List<String> uniqueIds = dataManager.getUniqueIds();
            for (int i = 0; i < uniqueIds.size(); i++) {
                String uuid = uniqueIds.get(i);
                IParamDescriptionPO uniqueID = obj
                        .getParameterForUniqueId(uuid);
                String value = getValueForSpecNodeWithParamDesc(uniqueID, spec);
                list.add(value);
            }
        }
        if (dataManager != null && spec == null) {
            List<String> uniqueIds = dataManager.getUniqueIds();
            for (int i = 0; i < uniqueIds.size(); i++) {
                String uuid = uniqueIds.get(i);
                IParamDescriptionPO uniqueID = obj
                        .getParameterForUniqueId(uuid);
                String value = StringConstants.EMPTY;
                if (uniqueID instanceof ITcParamDescriptionPO) {
                    value = ((ITcParamDescriptionPO) uniqueID).getValueSet()
                            .getDefaultValue();
                }
                list.add(value);
            }
        }
        if (list.size() > 0) {
            IDataSetPO dataSet = PoMaker.createListWrapperPO(list);
            obj.getDataManager().insertDataSet(dataSet, row);
        } else {
            obj.getDataManager().insertDataSet(row);
        }
    }
    
    /**
     * Removes a test data row in the test data manager of the passed node.
     * 
     * @param paramNode
     *            The parameter node
     * @param row
     *            The row to remove
     * @param mapper
     *            mapper to resolve param names
     */
    public void removeDataSet(IParameterInterfacePO paramNode, int row,
            IParamNameMapper mapper) {

        int colCount = paramNode.getDataManager().getColumnCount();
        for (int i = 0; i < colCount; i++) {
            final String uniqueId = paramNode.getDataManager().getUniqueIds()
                    .get(i);
            final IParamDescriptionPO desc = paramNode
                    .getParameterForUniqueId(uniqueId);
            if (desc != null) {
                GuiParamValueConverter conv = new GuiParamValueConverter(null,
                        paramNode, desc, AbstractParamInterfaceBP
                                .createParamValueValidator(
                                        desc.getType(), false));
                startParameterUpdate(conv, row, mapper);
            }
        }
        paramNode.getDataManager().removeDataSet(row);
    }
    
    /**
     * Updates the specified cell (row, column) in the test data manager of the
     * passed parameter object.
     * 
     * @param conv
     *            converter contains value to update
     * @param row
     *            The test data row
     * @param mapper
     *            mapper to resolve param names
     */
    public void startParameterUpdate(GuiParamValueConverter conv,
            int row, IParamNameMapper mapper)  {
        IParameterInterfacePO paramNode = conv.getCurrentNode();
        IParamDescriptionPO paramDescription = conv.getDesc();
        String paramGuid = paramDescription.getUniqueId();
        if (paramNode.getParameterList().contains(conv.getDesc()) 
                && !paramNode.getDataManager().getUniqueIds().contains(
                        paramGuid)) {
            // This prevents the scenario where a (new) parameter exists in 
            // the node's parameter list, but not in its data manager.
            paramNode.getDataManager().addUniqueId(paramGuid);
        }
        try {
            // do nothing, if parameter value is unchanged
            String value = 
                paramNode.getDataManager().getCell(row, paramDescription);
            final String modelString = conv.getModelString();
            if (modelString != null && modelString.equals(value)) {
                return;
            }
        } catch (IndexOutOfBoundsException e) { // NOPMD
            // nothing
        }
        updateParam(conv, mapper, row);
    }

    /**
     * get the gui representation for parameter value of given param description for first dataset
     * @param node current node
     * @param desc param description belonging to searched param value
     * @param rowCount datasetNumber - 1
     * @return gui representation of parameter value for given parameter description
     */
    public static String getGuiStringForParamValue(
            final IParameterInterfacePO node, final IParamDescriptionPO desc, 
            int rowCount) {

        String result = StringConstants.EMPTY;
        IParameterInterfacePO srcNode = node;
        IParamDescriptionPO srcDesc = desc;
        while (srcNode.getReferencedDataCube() != null) {
            srcNode = srcNode.getReferencedDataCube();
            srcDesc = srcNode.getParameterForName(srcDesc.getName());

            // Existence and type compatibility check
            if (srcDesc == null || !desc.getType().equals(srcDesc.getType())) {
                return result;
            }
        }
        if (srcDesc == null) {
            // Parameter is not present in the referenced data source.
            // Return empty test data.
            return result;
        }
        int col = srcNode.getDataManager().findColumnForParam(
                srcDesc.getUniqueId());
        boolean colNotExistend = false;
        boolean foundCol = false;
        int dataSetCount = srcNode.getDataManager().getDataSetCount();
        if (col > -1 && dataSetCount > rowCount) {
            IDataSetPO row = srcNode.getDataManager().getDataSet(rowCount);
            try {
                String td = row.getValueAt(col);
                ParamValueConverter conv = 
                    new ModelParamValueConverter(td,
                            srcNode, srcDesc);
                result = conv.getGuiString();
                foundCol = true;
            } catch (IndexOutOfBoundsException e) {
                // do nothing
                colNotExistend = true;
            }
        }
        if (col == -1 || colNotExistend 
                || (dataSetCount <= srcNode.getParameterListSize() 
                && StringUtils.isBlank(result) && !foundCol)) {
            if (srcNode instanceof IExecTestCasePO) {
                INodePO specNode = srcNode.getSpecificationUser();
                specNode = ((IExecTestCasePO) srcNode).getSpecTestCase();
                if (specNode instanceof ISpecTestCasePO) {
                    result = getValueForSpecNodeWithParamDesc(
                            srcDesc, (ISpecTestCasePO) specNode);
                }
            }
        }
        return result;
    }

    /**
     * Gets the value (including default value if there is a cap in the spec)
     * @param srcDesc the {@link IParamDescriptionPO} of the parameter to get the value for
     * @param specNode the spec node to check
     * @return the value for a combination of {@link ISpecTestCasePO} column and {@link IParamDescriptionPO}
     */
    public static String getValueForSpecNodeWithParamDesc(
            IParamDescriptionPO srcDesc, ISpecTestCasePO specNode) {
        List<INodePO> list = specNode.getUnmodifiableNodeList();
        if (list.size() == 1) {
            INodePO possibleCap = list.get(0);
            if (possibleCap instanceof ICapPO) {
                ICapPO cap = (ICapPO) possibleCap;
                List<IDataSetPO> datasets = cap.getDataManager().getDataSets();
                if (datasets.size() == 1) {
                    IDataSetPO set = datasets.get(0);
                    int i = 0;
                    for (String string : set.getColumnStringValues()) {
                        if (StringUtils.contains(string,
                                srcDesc.getUniqueId())) {
                            return getDefaultValue(cap, i);
                        }
                        i++;

                    }
                }
            }

        }
        IParamDescriptionPO paramForID =
                specNode.getParameterForUniqueId(srcDesc.getUniqueId());
        if (paramForID != null) {
            if (paramForID instanceof ITcParamDescriptionPO) {
                String defaultValue = ((ITcParamDescriptionPO) paramForID)
                        .getValueSet().getDefaultValue();
                if (StringUtils.isNotEmpty(defaultValue)) {
                    return defaultValue;
                }
            }
        }
        return null;
    }
    /**
     * gets the default value for a index from a action
     * @param cap the cap to get the default value from
     * @param index the index
     * @return the default value for the index or <code>null</code> 
     * if no default value is set
     */
    private static String getDefaultValue(ICapPO cap, int index) {
        List<Param> paramList = cap.getMetaAction().getParams();
        if (paramList.size() > index) {
            String defaultValue = paramList.get(index).getDefaultValue();
            if (StringUtils.isNotBlank(defaultValue)) {
                return defaultValue;
            }
        }
        return null;
    }
    
    /**
     * adds new parameter(s) to the parent, if the current node contains new 
     * references. This method doesn't validate, if it's allowed to change the interface.
     * This validation has to run before.
     * Updates the parametervalue of current node in model
     * hint: if the user has removed a reference in current parameter value, the 
     * corresponding parameter of parent won't be deleted. A removal of parameters
     * is only allowed, when the user calls the change parameters dialog.
     * 
     * @param conv converter containing parameter value to update
     * @param mapper mapper to resolve param names will be added
     * @param row current dataset number
     */
    protected abstract void updateParam(GuiParamValueConverter conv,
            IParamNameMapper mapper, int row);

    /**
     * Updates the test data manager of the passed node by writing the
     * value contained in converter into the appropriate cell.
     * 
     * @param conv converter contains parameter value to write
     * @param dataSetRow The row of the test data manager
     */
    protected void writeTestDataEntry(GuiParamValueConverter conv,
        int dataSetRow) {

        String oldTd = null;
        final IParamDescriptionPO desc = conv.getDesc();
        IParameterInterfacePO currentNode = conv.getCurrentNode();
        ITDManager dataManager = currentNode.getDataManager();
        try {
            oldTd = dataManager.getCell(dataSetRow,
                desc);
        } catch (IndexOutOfBoundsException e) { // NOPMD by al on 3/19/07 1:23 PM
            // Nothing to be done
        }
        String td = createOrUpdateTestData(oldTd, conv);
        if (dataManager.getDataSetCount() <= dataSetRow) {
            addDataSet(currentNode, dataSetRow);
        }
        dataManager.updateCell(td, dataSetRow,
            desc.getUniqueId());
    }
    
    
    /**
     * Creates a new test data instance, if the passed test data is
     * <code>null</code>, or updates the passed one with the given value.
     * 
     * @param testData The existing test data or <code>null</code>
     * @param conv converter with value to update
     * @return The (new) test data instance.
     *             If the creation of the Test Data fails
     */
    private String createOrUpdateTestData(String testData,
        GuiParamValueConverter conv) {
        // A new converter is instantiated and used here in order to cover
        // the corner case described in bug http://eclip.se/370718.
        GuiParamValueConverter newConv = new GuiParamValueConverter(
                conv.getGuiString(), conv.getCurrentNode(), 
                conv.getDesc(), 
                AbstractParamInterfaceBP.createParamValueValidator(
                        TestDataConstants.STR, false));
        return newConv.getModelString();
    }
    
    /**
     * @param name
     *            the new name of the parameter
     * @param type
     *            the type of the parameter
     * @param obj
     *            the object to add the parameter for
     * @param mapper
     *            the mapper to resolve param names
     */
    public void addParameter(String name, String type,
            IModifiableParameterInterfacePO obj, IParamNameMapper mapper) {
        obj.addParameter(type, name, mapper);
    }
    
    /**
     * @param name the new name of the parameter
     * @param type the type of the parameter
     * @param values the valuesets (value,comment)
     * @param defaultValue the default value
     * @param obj the object to add the parameter for
     * @param mapper the mapper to resolve param names
     */
    public void addParameter(String name, String type,
            Map<String, String> values, String defaultValue,
            IModifiableParameterInterfacePO obj, IParamNameMapper mapper) {
        IParamDescriptionPO addParameter = obj.addParameter(type, name, mapper);
        if (addParameter instanceof ITcParamDescriptionPO) {
            ITcParamDescriptionPO tcd = (ITcParamDescriptionPO) addParameter;
            tcd.getValueSet().setDefaultValue(defaultValue);
            List<IValueCommentPO> valueSet = tcd.getValueSet().getValues();
            for (Entry<String, String> element : values.entrySet()) {
                valueSet.add(PoMaker.createValueComment(element.getKey(),
                        element.getValue()));
            }
        }
    }

    /**
     * @param desc
     *            the param to remove
     * @param paramIntObj
     *            the object to remove the param from
     */
    public abstract void removeParameter(IParamDescriptionPO desc,
            T paramIntObj);

    /**
     * @param paramIntObj
     *            The object to change the parameter usage at.
     * @param desc
     *            The old parameter for changing the usage at.
     * @param guid
     *            The GUID of the new parameter usage.
     * @param mapper The parameter name mapping.
     */
    public abstract void changeUsageParameter(T paramIntObj,
            IParamDescriptionPO desc, String guid,
            ParamNameBPDecorator mapper);

    /**
     * @param desc
     *            the param to rename
     * @param newName
     *            the new name
     * @param mapper
     *            the mapper to use
     */
    public void renameParameters(IParamDescriptionPO desc,
            String newName, ParamNameBPDecorator mapper) {
        mapper.addNameToUpdate(desc.getUniqueId(), newName);
    }
    
    /**
     * @param type type of parameter
     * @param valuesAreCombinable
     *            whether combinations of the supplied values are allowed
     * @param values list of possible values for a parameter
     * @return validator fit to given type
     */
    public static IParamValueValidator createParamValueValidator(
        String type, boolean valuesAreCombinable, String... values) {
        if (TestDataConstants.INTEGER.equals(type)) {
            return new IntegerParamValueValidator(Integer.MIN_VALUE, 
                Integer.MAX_VALUE, values);
        }
        if (values.length > 0) {
            return new ComboParamValidator(values, valuesAreCombinable);
        }
        if (TestDataConstants.VARIABLE.equals(type)) {
            return new VariableParamValueValidator();
        }
        return new NullValidator();
    }
}
