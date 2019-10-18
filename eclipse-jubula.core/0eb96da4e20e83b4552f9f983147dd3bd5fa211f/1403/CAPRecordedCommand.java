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
package org.eclipse.jubula.client.core.commands;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.compcheck.CompletenessGuard;
import org.eclipse.jubula.client.core.events.IRecordListener;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.CAPRecordedMessage;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.communication.internal.message.ShowObservInfoMessage;
import org.eclipse.jubula.communication.internal.message.ShowRecordedActionMessage;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.objects.MappingConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used by record mode of Jubula. Server sends a recorded CAP,
 * which is added to specification store. (CAP + Objectmapping)
 * 
 * @author BREDEX GmbH
 * @created 02.09.2004
 * 
 */
public class CAPRecordedCommand implements ICommand {
    
    /** symbol for single quote */
    public static final char COMMENT_SYMBOL = 
        TestDataConstants.COMMENT_SYMBOL;
    /**
     * The logger
     */
    private static final Logger LOG = 
        LoggerFactory.getLogger(CAPRecordedCommand.class);

    /** The TestCase to record in */
    private static ISpecTestCasePO recSpecTestCase;
    
    /** The component names cache for adding new component names */
    private static IWritableComponentNameCache compNamesCache;

    /**
     * The Editor/ContentProvider listening to recorded Caps
     */
    private static IRecordListener recordListener;

    /**
     * The message.
     */
    private CAPRecordedMessage m_capRecordedMessage;
        
    /** name of recorded action */
    private String m_recAction = null;
    
    /** additional Message for Observation Console */
    private String m_extraMsg = null;
    
    /** info Message for actions that are not supported for selected toolkit */
    private String m_wrongToolkit =
        Messages.CurrenActionNotAvailabelForSelectedToolkit;
    
    /**
     * get name of recorded action
     * @return String
     */
    public String getRecAction() {
        return m_recAction;
    }
    /**
     * set name of recorded action
     * @param recAction String
     */
    public void setRecAction(String recAction) {
        m_recAction = recAction;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_capRecordedMessage;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_capRecordedMessage = (CAPRecordedMessage)message;
    }
    
    /**
     * call the method of the implementation class per reflection
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        MessageCap messageCap = m_capRecordedMessage.getMessageCap();
        //only actions supported by selected toolkit recorded
        String compType = getComponentType(messageCap);
        boolean belongsToToolkit = belongsToToolkit(compType);
        if (belongsToToolkit) {
            try {
                // here handle to Save CAP
                setCapIntoSpecModel(messageCap, compType);            
                String recAction = m_recAction;
                String extraMsg = m_extraMsg;
                return new ShowRecordedActionMessage(true, recAction, extraMsg);
            } catch (IllegalArgumentException e) {
                LOG.error(Messages.ExecutedFailed, e);
            }
            return new ShowRecordedActionMessage(false);
        }
        return new ShowObservInfoMessage(m_wrongToolkit);
    }
    
    /**
     * Sets a recorded CAP into the Specification-Model.
     * @param messageCap the recorded CAP
     * @param compType String
     */
    private void setCapIntoSpecModel(MessageCap messageCap, String compType) {
        ICapPO cap = buildCapPO(messageCap, compType);
        // GUI updates
        if (recordListener != null) {
            recordListener.capRecorded(cap, messageCap.getCi(),
                    messageCap.hasDefaultMapping());
        }
    }
    
    /**
     * Builds a CapPO instance from the given MessageCap.
     * @param messageCap the MessageCap.
     * @param componentType String
     * @return a CapPO instance, or <code>null</code> if the Test Step could
     *         not be created.
     */
    private ICapPO buildCapPO(MessageCap messageCap, String componentType) {
        String componentName = null;
        boolean isAppAction = isApplication(componentType);
        if (isAppAction) {
            componentName = null;
        } else {
            componentName = getOrCreateLogicalName(messageCap);
        }
        String actionName = messageCap.getAction().getName();
        String capName = null;
        capName = CompSystemI18n.getString(actionName);
        if (isAppAction || messageCap.hasDefaultMapping()) {
            capName = CompSystemI18n.getString(actionName);
        } else {
            capName = CompSystemI18n.getString(actionName);
            String normName = removeMnemonics(
                    messageCap.getCi().getComponentName());
            String altName = removeMnemonics(
                    messageCap.getCi().getAlternativeDisplayName());
            capName = altName == null ? capName.concat(" on " //$NON-NLS-1$
                    + minimizeCapName(normName)) : capName.concat(" on " //$NON-NLS-1$
                            + altName);        
        }
        m_recAction = capName;
        m_extraMsg = messageCap.getExtraMessage();
        ICapPO recCap = NodeMaker.createCapPO(capName, componentName,
                componentType, actionName);
        // Set the Component Name to null so that the Component Name 
        // cache doesn't remove the instance of reuse 
        // for <code>componentName</code>.
        recCap.setComponentName(null);
        
        if (!messageCap.hasDefaultMapping()) {
            ComponentNamesBP.setCompName(recCap, componentName, 
                    CompNameCreationContext.STEP, compNamesCache);
        }
        recSpecTestCase.addNode(recCap);
        List params = messageCap.getMessageParams();
        addTestData(recCap, params);
        return recCap;
    }
    
    /**
     * gets componentType of recorded Action
     * @param messageCap MessageCap
     * @return component type of action
     */
    private String getComponentType(MessageCap messageCap) {
        String componentType = messageCap.getCi().getSupportedClassName();
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        Component component = null;        
        if (isApplication(componentType)) {
            component = compSystem.findComponent(messageCap.getCi()
                .getComponentClassName());
        } else {
            if (componentType.equals(MappingConstants.SWT_MENU_CLASSNAME)) {
                componentType = MappingConstants
                    .SWT_MENU_DEFAULT_MAPPING_CLASSNAME;
            }
            if (componentType.equals(MappingConstants.SWING_MENU_CLASSNAME)) {
                componentType = MappingConstants
                    .SWING_MENU_DEFAULT_MAPPING_CLASSNAME;
            }
            component = getComponentToUse(
                    messageCap, compSystem, componentType);
        }
        while (component != null && (!component.isVisible()
                || !component.isObservable())
                && !component.getRealized().isEmpty()) {
            List realizedComponents = component.getRealized();
            component = (Component)realizedComponents.get(0);
        }
        
        componentType = getAbstrCompType(component, messageCap);
        
        return componentType;
    }
    
    /**
     * Check if current Action is supported by current toolkit
     * @param compType Type of Component
     * @return true if action is supported, false otherwise
     */
    private boolean belongsToToolkit(String compType) {
        CompSystem cs = ComponentBuilder.getInstance().getCompSystem();
        String toolkit = GeneralStorage.getInstance().getProject().getToolkit();
        String[] toolkitTypes = cs.getComponentTypes(toolkit);
        for (int i = 0; i < toolkitTypes.length; i++) {
            String kitType = toolkitTypes[i];
            if (kitType.equals(compType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add the appropriate Test Data to the Test Step.
     * 
     * @param recCap The recorded Test Step.
     * @param params The Test Step's Parameters.
     */
    private void addTestData(ICapPO recCap, List params) {
        ITDManager tdManager = recCap.getDataManager();
        GeneralStorage genStorage = GeneralStorage.getInstance();
        int paramNumber = 0;
        Iterator msgParamIt = params.iterator();
        Iterator paramIt = recCap.getMetaAction().getParams().iterator();
        while (msgParamIt.hasNext() && paramIt.hasNext()) {
            MessageParam msgParam = (MessageParam)msgParamIt.next();
            String value = msgParam.getValue();
            if (value != null && !(value.equals(StringUtils.EMPTY))) {
                tdManager.updateCell(value, 0, paramNumber);
            }
            boolean bool = 
                !(value == null) && !(value.equals(StringUtils.EMPTY));
            CompletenessGuard.setCompletenessTestData(recCap, bool);
            paramNumber++;
        }
    }
    /**
     * @param componentType The type to check.
     * @return <code>true</code> if <code>componentType</code> is an
     *         "Application" component. Otherwise, <code>false</code>.
     */
    private boolean isApplication(String componentType) {
        return componentType.equals(
                MappingConstants.SWING_APPLICATION_CLASSNAME)
            || componentType.equals(
                MappingConstants.SWING_APPLICATION_COMPONENT_IDENTIFIER)    
            || componentType.equals(
                MappingConstants.SWT_APPLICATION_CLASSNAME)
            || componentType.equals(
                    MappingConstants.WEB_APPLICATION_CLASSNAME)
            || componentType.equals(
                    MappingConstants.CONCR_APPLICATION_CLASSNAME);
    }    
    
    /**
     * @param comp Component
     * @param mc MessageCap
     * @return String of more AbstractType then the old concrete one
     */
    private String getAbstrCompType(Component comp, MessageCap mc) {
        String compTyp = null;
        String [] argArray = createArgTypeArray(
                UnmodifiableList.decorate(mc.getMessageParams()));
        String actionName = comp.findActionByMethodSignature(
                mc.getMethod(), 
                argArray).getName();        
        if (actionName.equals("CompSystem.Click") //$NON-NLS-1$
                || actionName.equals("CompSystem.VerifyEnabled") //$NON-NLS-1$
                || actionName.equals("CompSystem.VerifyExists") //$NON-NLS-1$
                || actionName.equals("CompSystem.VerifyFocus") //$NON-NLS-1$
                || actionName.equals("CompSystem.VerifyProperty") //$NON-NLS-1$
                || actionName.equals("CompSystem.PopupSelectByTextPath")) { //$NON-NLS-1$
            compTyp = "guidancer.abstract.Widget"; //$NON-NLS-1$
        } else if (actionName.equals("CompSystem.InputTextDirect") //$NON-NLS-1$
                || actionName.equals("CompSystem.InputText") //$NON-NLS-1$
                || actionName.equals("CompSystem.VerifyEditable")) { //$NON-NLS-1$
            compTyp = "guidancer.abstract.TextInputSupport"; //$NON-NLS-1$
            
        } else if (actionName.equals("CompSystem.VerifyText")) { //$NON-NLS-1$
            compTyp = "guidancer.abstract.TextVerifiable"; //$NON-NLS-1$
            
        } else if (actionName.equals("CompSystem.VerifySelected")) { //$NON-NLS-1$
            compTyp = "guidancer.abstract.ButtonComp"; //$NON-NLS-1$
            
        } else {
            compTyp = comp.getType();
        }
        
        return compTyp;
    }
    
    /**
     * check if component has already a logical name and use it or generate
     * new one
     * @param messageCap MessageCap
     * @return new or existing logical name
     */
    private String getOrCreateLogicalName(MessageCap messageCap) {
        String compName = null;        

        IAUTMainPO connectedAut = TestExecution.getInstance().getConnectedAut();
        if (connectedAut != null) {            
            
            boolean checkTechNameExists = connectedAut.getObjMap()
                    .existTechnicalName(messageCap.getCi());            
            
            if (checkTechNameExists) {
                for (IObjectMappingAssoziationPO oma : connectedAut.getObjMap()
                        .getMappings()) {
                    Set<String> logicalNames = oma.getLogicalNames();
                    if (!(logicalNames.isEmpty())
                            && oma.getTechnicalName() != null
                            && oma.getTechnicalName()
                                    .equals(messageCap.getCi())) {
                        for (String compNameGuid : logicalNames) {
                            IComponentNamePO compNamePo = compNamesCache.
                                    getResCompNamePOByGuid(compNameGuid);
                            if (compNamePo != null) {
                                if (compNamePo.getParentProjectId().equals(
                                        GeneralStorage.getInstance()
                                                .getProject().getId())) {
                                    compName = compNamePo.getName();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (compName == null) {
            compName = messageCap.getLogicalName();            
            if (compName == null) {
                compName = messageCap.getCi().generateLogicalName();
            }
        }
        return compName;
    }
    
    /**
     * @param messageCap MessageCap
     * @param compSystem Component
     * @param componentType String
     * @return the highest component that supports the current action (e.g.
     *         gdSelect on TreeTable will return Tree)
     */
    private Component getComponentToUse(MessageCap messageCap,
            CompSystem compSystem, String componentType) {
        List<Component> supportedComponents = 
            compSystem.findComponents(componentType);
        
        Set<Component> compsWithAction = new HashSet<Component>();
        for (Component c : supportedComponents) {
            List<Action> actionList = 
                new LinkedList<Action>(c.getActions());
            
            if (actionList.contains(messageCap.getAction())) {
                compsWithAction.add(c);
            }              
        }

        Component theComponentToUse = null;
        for (Component c : compsWithAction) {
            Set<Component> realizedIntersection = 
                new HashSet<Component>(c.getAllRealized());
            realizedIntersection.retainAll(compsWithAction);
            if (realizedIntersection.isEmpty()) {
                theComponentToUse = c;
                break;
            }
        }

        return theComponentToUse;
    }
    
    
    /**
     * minimizes the component name, e.g. javax.swing.JButton_1 to Button_1
     * @param capName String
     * @return the minimized CapName
     */
    private String minimizeCapName(String capName) {
        String minCapName = capName;
        String[] nameParts = null;
        nameParts = minCapName.split("\\(", 2); //$NON-NLS-1$
        
        if (nameParts[0].lastIndexOf(".") > -1 //$NON-NLS-1$
                && nameParts[0].length() > (nameParts[0].lastIndexOf(".") + 1)) { //$NON-NLS-1$
            nameParts[0] = nameParts[0].substring(
                    nameParts[0].lastIndexOf(".") + 1); //$NON-NLS-1$
        }
        minCapName = nameParts[0];
        if (nameParts.length > 1) {
            minCapName = minCapName + "(" + nameParts[1]; //$NON-NLS-1$
        }
        return minCapName;
    }
    
    /**
     * @param name String
     * @return name of component without mnemonics-sign "&"
     */
    public String removeMnemonics(String name) {
        String fixedName = name;
        if (fixedName != null) {
            fixedName = fixedName.replaceAll("&", StringConstants.EMPTY); //$NON-NLS-1$
        }             
        return fixedName;        
    }
    
    /**
     * @param argList  The arguments as a list.
     * @return An array containing the types of the given arguments.
     */
    private String[] createArgTypeArray(List argList) {
        String [] argTypeArray = new String [argList.size()];
        for (int i = 0; i < argTypeArray.length; i++) {
            MessageParam param = (MessageParam)argList.get(i);
            argTypeArray[i] = param.getType();
        }

        return argTypeArray;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + Messages.TimeoutCalled);
    }
    /**
     * @param r The recSpecTestCase to set.
     */
    public static void setRecSpecTestCase(ISpecTestCasePO r) {
        CAPRecordedCommand.recSpecTestCase = r;
    }
    /**
     * 
     * @return the recSpecTestCase
     */
    public static ISpecTestCasePO getRecSpecTestCase() {
        return CAPRecordedCommand.recSpecTestCase;
    }
    /**
     * @param compCache The comp names cache to set.
     */
    public static void setCompNamesCache(
            IWritableComponentNameCache compCache) {
        
        CAPRecordedCommand.compNamesCache = compCache;
    }
    
    /**
     * sets the RecordListener
     * @param rc
     *      IRecordListener
     */
    public static void setRecordListener(IRecordListener 
        rc) {
        recordListener = rc;
    }

    /**
     * 
     * @return IRecordListener
     */
    public static IRecordListener getRecordListener() {
        return recordListener;
    }

    
    /**
     * Checks if there is a Aut and obseravtion mode running
     * @return true, if observation mode is running, false otherwise
     */
    public static boolean isObserving() {
        if (TestExecution.getInstance().getConnectedAut() != null) {
            switch (AUTModeChangedCommand.getAutMode()) {
                case ChangeAUTModeMessage.RECORD_MODE:
                case ChangeAUTModeMessage.CHECK_MODE:
                    return true;
                default : 
                    return false;
            }
        }
        return false;
    }
}
