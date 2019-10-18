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
package org.eclipse.jubula.extensions.wizard;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fx.ide.rrobot.RRobot;
import org.eclipse.fx.ide.rrobot.model.task.RobotTask;
import org.eclipse.fx.ide.rrobot.model.task.Variable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.extensions.wizard.i18n.Messages;
import org.eclipse.jubula.extensions.wizard.model.Action;
import org.eclipse.jubula.extensions.wizard.model.Parameter;
import org.eclipse.jubula.extensions.wizard.model.Storage;
import org.eclipse.jubula.extensions.wizard.model.Toolkit;
import org.eclipse.jubula.extensions.wizard.utils.Tools;
import org.eclipse.jubula.extensions.wizard.view.NewJubulaExtensionWizardPageFour;
import org.eclipse.jubula.extensions.wizard.view.NewJubulaExtensionWizardPageOne;
import org.eclipse.jubula.extensions.wizard.view.NewJubulaExtensionWizardPageThree;
import org.eclipse.jubula.extensions.wizard.view.NewJubulaExtensionWizardPageTwo;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * The New Jubula Extension Wizard.
 * 
 * @author BREDEX GmbH
 */
public final class NewJubulaExtensionWizard extends Wizard 
    implements INewWizard {

    /** The wizard's title */
    private static final String WINDOW_TITLE = Messages.WindowTitle;
    
    /** The wizard's first page */
    private NewJubulaExtensionWizardPageOne m_pageOne;
    
    /** The wizard's second page */
    private NewJubulaExtensionWizardPageTwo m_pageTwo;
    
    /** The wizard's third page */
    private NewJubulaExtensionWizardPageThree m_pageThree;
    
    /** The wizard's fourth page */
    private NewJubulaExtensionWizardPageFour m_pageFour;
    
    /** The storage instance of this wizard instance */
    private final Storage m_storage;
    
    /** The constructor */
    public NewJubulaExtensionWizard() {
        m_storage = new Storage();
        setWindowTitle(WINDOW_TITLE);
        ImageDescriptor image = 
                AbstractUIPlugin
                    .imageDescriptorFromPlugin(
                            Messages.WizardQualifier,
                            Messages.JubulaImage);
        setDefaultPageImageDescriptor(image);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // Not needed
    }

    @Override
    public void addPages() {
        m_pageOne = new NewJubulaExtensionWizardPageOne(m_storage);
        m_pageTwo = new NewJubulaExtensionWizardPageTwo(m_storage);
        m_pageThree = new NewJubulaExtensionWizardPageThree(m_storage);
        m_pageFour = new NewJubulaExtensionWizardPageFour(m_storage);
        
        addPage(m_pageOne);
        addPage(m_pageTwo);
        addPage(m_pageThree);
        addPage(m_pageFour);
    }
    
    @Override
    public boolean performFinish() {
        Toolkit toolkit = m_storage.getToolkit();
        Bundle b = FrameworkUtil.getBundle(getClass());
        BundleContext ctx = b.getBundleContext();
        ServiceReference<RRobot> ref = ctx.getServiceReference(RRobot.class);
        final RRobot r = ctx.getService(ref);

        ResourceSet set = new ResourceSetImpl();
        Resource resource = 
                set.getResource(URI
                        .createPlatformPluginURI(toolkit.getRobotUri(), true), 
                        true);
        
        final RobotTask task = (RobotTask) resource.getContents().get(0);
        
        initializeRobotValues(task);
        
        WorkspaceModifyOperation w = new WorkspaceModifyOperation() {
            @Override
            protected void execute(IProgressMonitor monitor) {
                Map<String, Object> map = new HashMap<String, Object>();
                r.executeTask(monitor, task, map);
            }
        };
        
        WorkspaceModifyOperation wTargetPlatform = null;
        
        if (m_storage.getTargetPlatform()) {
            Resource resourceTargetPlatform = 
                    set.getResource(URI.createPlatformPluginURI(
                            StringConstants.SLASH 
                            + Messages.WizardQualifier 
                            + StringConstants.SLASH
                            + Messages.TargetPlatformRTask, true),
                            true);
            
            final RobotTask taskTargetPlatform = 
                    (RobotTask) resourceTargetPlatform.getContents().get(0);
            wTargetPlatform = new WorkspaceModifyOperation() {
                @Override
                protected void execute(IProgressMonitor monitor) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    r.executeTask(monitor, taskTargetPlatform, map);
                }
            };
        }
        
        try {
            PlatformUI.getWorkbench().getProgressService().run(true, false, w);
            if (wTargetPlatform != null) {
                PlatformUI.getWorkbench().getProgressService()
                    .run(true, false, wTargetPlatform);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        openFilesInEditor();
        
        return true;
    }

    /**
     * Opens some of the created files in the editor after the wizard
     * has finished.
     */
    @SuppressWarnings("nls")
    private void openFilesInEditor() {
        Tools.openFileInEditor(StringConstants.SLASH 
                + m_storage.getProjectName()
                + ".toolkit" + Messages.ComponentConfigurationFileURI);
        Tools.openFileInEditor(StringConstants.SLASH 
                + m_storage.getProjectName()  
                + ".rc" + "/src/" +  m_storage.getID() + "/rc/"
                + m_storage.getToolkit().getName() + "/tester/"
                + m_storage.getClassName() + ".java");
        if (m_storage.getTargetPlatform()) {
            Tools.openFileInEditor(
                    StringConstants.SLASH + Messages.RelengQualifier 
                    + StringConstants.SLASH
                    + Messages.TargetPlatformFile);
        }
    }

    /**
     * Resolves the tester class qualifier of the component's type
     * @param componentTypeQualifier the component's type qualifier
     * @return the tester class qualifier or <code>null</code> if it could not
     * be found
     */
    public static String resolveTesterClass(
            final String componentTypeQualifier) {
        
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        Component typeComponent = compSystem
                .findComponent(componentTypeQualifier);
        
        if (typeComponent instanceof ConcreteComponent) {
            return ((ConcreteComponent) typeComponent)
                    .getTesterClass();
        }
        
        return null;
    }
    
    /**
     * Looks up the tester class for abstract and concrete components in the
     * tester class map
     * @param componentTypeQualifier the component type's qualifier
     * @return the looked up tester class qualifier or <code>null</code> if it
     * could not be found
     */
    public static String lookupTesterClassInMap(
            final String componentTypeQualifier) {
        
        Properties prop = new Properties();
        
        try (InputStream input = NewJubulaExtensionWizard.class
                .getResourceAsStream(
                    Messages.TesterClassMapProperties)) {
            
            prop.load(input);
            return prop.getProperty(componentTypeQualifier);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //CHECKSTYLE:OFF

    /**
     * Initializes the robot values
     * @param task the RobotTask to initialize
     */
    @SuppressWarnings("nls")
    private void initializeRobotValues(RobotTask task) {
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        final String componentQualifier;
        final String componentName;
        final String componentTypeQualifier;
        final String componentTypeName;
        final String superClassQualifier;
        final String superClassName;
        final String realizer;
        final String toolkitComponentType;
        
        componentQualifier = m_storage.getComponent();
        componentName = componentQualifier.substring(
                componentQualifier.lastIndexOf('.') + 1);
        
        componentTypeQualifier = m_storage.getComponentType();
        componentTypeName = componentTypeQualifier.substring(
                componentTypeQualifier.lastIndexOf('.') + 1);
        
        if (!m_storage.isComponentCustom()) {
            Component component = compSystem.findComponent(componentQualifier);
            String testerClass = ((ConcreteComponent) component)
                    .getTesterClass();
            
            superClassQualifier = testerClass;
            superClassName = superClassQualifier
                    .substring(superClassQualifier.lastIndexOf('.') + 1);
            
            realizer = componentQualifier;
            toolkitComponentType = componentQualifier + ".extended";
        } else {
            Component typeComponent = compSystem
                    .findComponent(componentTypeQualifier);
            String testerClass = resolveTesterClass(componentTypeQualifier);
            if (testerClass != null) {
                superClassQualifier = testerClass;
                superClassName = superClassQualifier
                        .substring(superClassQualifier.lastIndexOf('.') + 1);
            } else {
                String testerClassQualifier = 
                        lookupTesterClassInMap(componentTypeQualifier);
                
                if (testerClassQualifier != null) {
                    superClassQualifier = testerClassQualifier;
                    superClassName = superClassQualifier
                            .substring(superClassQualifier
                                    .lastIndexOf('.') + 1);
                } else {
                    superClassQualifier = null;
                    superClassName = null;
                }
            }
            
            realizer = componentTypeQualifier;
            toolkitComponentType = componentQualifier;
        }
        
        final String methods = getMethods();
        
        final String actions = createComponenConfigurationActions();
        
        final String i18n = createI18n(componentQualifier, componentName);
        
        final Toolkit toolkit = m_storage.getToolkit();
        
        task.getVariables().forEach(new Consumer<Variable>() {
            @Override
            public void accept(Variable var) {
                switch (var.getKey()) {
                    case "BundleProject_projectName":
                        if (m_storage.getProjectName() != null) {
                            var.setDefaultValue(m_storage.getProjectName()); 
                        }
                        break;
                    case "BundleProject_ID":
                        if (m_storage.getID() != null) {
                            var.setDefaultValue(m_storage.getID());
                        }
                        break;
                    case "BundleProject_name":
                        if (m_storage.getName() != null) {
                            var.setDefaultValue(m_storage.getName());
                        }
                        break;
                    case "BundleProject_vendor":
                        if (m_storage.getVendor() != null) {
                            var.setDefaultValue(m_storage.getVendor());
                        }
                        break;
                    case "BundleProject_version":
                        if (m_storage.getVersion() != null) {
                            var.setDefaultValue(m_storage.getVersion());
                        }
                        break;
                    case "BundleProject_executionEnvironment":
                        if (m_storage.getExecutionEnvironment() != null) {
                            var.setDefaultValue(m_storage.getExecutionEnvironment()); 
                        }
                        break;
                    case "ComponentName":
                        if (componentName != null) {
                            var.setDefaultValue(componentName);
                        }
                        break;
                    case "ComponentQualifier":
                        if (componentQualifier != null) {
                            var.setDefaultValue(componentQualifier);
                        }
                        break;
                    case "ComponentTypeName":
                        if (componentTypeName != null) {
                            var.setDefaultValue(componentTypeName);
                        }
                        break;
                    case "ComponentTypeQualifier":
                        if (componentTypeQualifier != null) {
                            var.setDefaultValue(componentTypeQualifier);
                        }
                        break;
                    case "SuperClassQualifier":
                        if (superClassQualifier != null) {
                            var.setDefaultValue(superClassQualifier);
                        }
                        break;
                    case "SuperClassName":
                        if (superClassName != null) {
                            var.setDefaultValue(superClassName);
                        }
                        break;
                    case "TesterClassName":
                        String className = m_storage.getClassName();
                        if (className != null) {
                            if (className.length() > 0) {
                                var.setDefaultValue(className);
                            } else {
                                String newClassName = "My" + componentName
                                        + "Tester";
                                m_storage.setClassName(newClassName);
                                var.setDefaultValue(newClassName);
                            }
                        } else {
                            String newClassName = "My" + componentName 
                                    + "Tester";
                            m_storage.setClassName(newClassName);
                            var.setDefaultValue(newClassName);
                        }
                        break;
                    case "Methods":
                        if (methods != null) {
                            var.setDefaultValue(methods);
                        }
                        break;
                    case "i18n":
                        if (i18n != null) {
                            var.setDefaultValue(i18n);
                        }
                        break;
                    case "Actions":
                        if (actions != null) {
                            var.setDefaultValue(actions);
                        }
                        break;
                    case "Realizer":
                        if (realizer != null) {
                            var.setDefaultValue(realizer);
                        }
                        break;
                    case "ToolkitComponentType":
                        if (toolkitComponentType != null) {
                            var.setDefaultValue(toolkitComponentType);
                        }
                        break;
                    case "ToolkitDependency":
                        if (toolkit.getToolkitDependency() != null) {
                            var.setDefaultValue(toolkit.getToolkitDependency());
                        }
                        break;
                    case "ToolkitExtensionName":
                        if (toolkit.getToolkitExtensionName() != null) {
                            var.setDefaultValue(toolkit
                                    .getToolkitExtensionName());
                        }
                        break;
                    case "FragmentHost":
                        if (toolkit.getFragmentHost() != null) {
                            var.setDefaultValue(toolkit.getFragmentHost());
                        }
                        break;
                    case "ToolkitName":
                        if (toolkit.getName() != null) {
                            var.setDefaultValue(toolkit.getName());
                        }
                        break;
                    case "Adapter":
                        if (toolkit.getAdapter() != null) {
                            var.setDefaultValue(toolkit.getAdapter());
                        }
                        break;
                    case "AdapterPackage":
                        if (toolkit.getAdapterPackage() != null) {
                            var.setDefaultValue(toolkit.getAdapterPackage());
                        }
                        break;
                    case "AdditionalImports":
                        if (toolkit.getAdditionalImports() != null) {
                            var.setDefaultValue(toolkit.getAdditionalImports());
                        }
                        break;
                    default: break;
                }
            }
        });
    }

    
    
    //CHECKSTYLE:ON
    
    /**
     * Creates the component configuration actions for the 
     * ComponentConfiguration.xml
     * @return the created component configuration actions
     */
    @SuppressWarnings("nls")
    private String createComponenConfigurationActions() {
        StringBuilder sb = new StringBuilder();
        List<Action> actions = m_storage.getActions();
        for (Action action : actions) {
            sb.append("        <action name=\"UserExtension.");
            sb.append(Tools.getCamelCase(action.getName()));
            sb.append("\">");
            sb.append(StringConstants.NEWLINE);
            sb.append("            <method>");
            sb.append(Tools.getCamelCase(action.getName()));
            sb.append("</method>");
            sb.append(StringConstants.NEWLINE);
            
            List<Parameter> parameters = action.getParameters();
            for (Parameter parameter : parameters) {
                sb.append("            <param name=\"UserExtension");
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(action.getName()));
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(parameter.getName()));
                sb.append("\">");
                sb.append(StringConstants.NEWLINE);
                sb.append("                <type>java.lang.");
                sb.append(parameter.getType());
                sb.append("</type>");
                sb.append(StringConstants.NEWLINE);
                sb.append(createParameterValueSets(parameter, action));
                sb.append("            </param>");
                sb.append(StringConstants.NEWLINE);
            }
            sb.append("        </action>");
            sb.append(StringConstants.NEWLINE);
        }
        
        return sb.toString();
    }
    
    /**
     * Creates the value set for the componentconfiguration.xml
     * @param parameter the parameter
     * @param action the action
     * @return the value set for the componentconfiguration.xml
     */
    @SuppressWarnings("nls")
    private String createParameterValueSets(Parameter parameter, 
            Action action) {
        Set<String> values = parameter.getValueSet().getSet();

        if (values.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("                <defaultValue>");
            sb.append(values.toArray(new String[0])[0]);
            sb.append("</defaultValue>");
            sb.append(StringConstants.NEWLINE);
            sb.append("                <valueSet>");
            sb.append(StringConstants.NEWLINE);
            for (String val : values) {
                sb.append("                    <element name=\"");
                sb.append("UserExtension");
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(action.getName()));
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(parameter.getName()));
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(val) + "\" value=\"");
                sb.append(val);
                sb.append("\"/>");
                sb.append(StringConstants.NEWLINE);
            }
            sb.append("                </valueSet>");
            sb.append(StringConstants.NEWLINE);
            return sb.toString();
        }
        return StringConstants.EMPTY;
    }

    /**
     * Generates the i18n file
     * @param componentQualifier the component qualifier
     * @param componentName the component name
     * @return the crated i18n file
     */
    @SuppressWarnings("nls")
    private String createI18n(String componentQualifier, String componentName) {
        List<Action> actions = m_storage.getActions();
        StringBuilder sb = new StringBuilder();
        for (Action action : actions) {
            sb.append("UserExtension");
            sb.append(StringConstants.DOT);
            sb.append(Tools.getCamelCase(action.getName()));
            sb.append(StringConstants.EQUALS_SIGN);
            sb.append(action.getName());
            sb.append(StringConstants.NEWLINE);
            
            List<Parameter> parameters = action.getParameters();
            for (Parameter parameter: parameters) {
                sb.append("UserExtension");
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(action.getName()));
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(parameter.getName()));
                sb.append(StringConstants.EQUALS_SIGN);
                sb.append(parameter.getName());
                sb.append(StringConstants.NEWLINE);
                sb.append(createValueSetI18n(parameter, action));
            }
        }
        
        if (m_storage.isComponentCustom()) {
            sb.append(componentQualifier);
            sb.append(StringConstants.EQUALS_SIGN);
            sb.append(componentName);
            sb.append(StringConstants.NEWLINE);
        } else {
            sb.append(componentQualifier);
            sb.append(StringConstants.DOT);
            sb.append("extended");
            sb.append(StringConstants.EQUALS_SIGN);
            sb.append(componentName);
            sb.append(StringConstants.NEWLINE);
        }
        
        return sb.toString();
    }
    
    /**
     * Creates the i18n for the value set
     * @param parameter the parameter
     * @param action the action
     * @return the i18n for the value set
     */
    @SuppressWarnings("nls")
    private String createValueSetI18n(Parameter parameter, Action action) {
        Set<String> values = parameter.getValueSet().getSet();

        if (values.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String val : values) {
                sb.append("UserExtension");
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(action.getName()));
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(parameter.getName()));
                sb.append(StringConstants.DOT);
                sb.append(Tools.getCamelCase(val));
                sb.append(StringConstants.EQUALS_SIGN);
                sb.append(val);
                sb.append(StringConstants.NEWLINE);
            }
            return sb.toString();
        }
        return StringConstants.EMPTY;
    }

    /**
     * @return the methods for the tester class
     */
    @SuppressWarnings("nls")
    private String getMethods() {
        List<Action> actions = m_storage.getActions();
        if (actions.size() > 0) {
            StringBuilder sb = new StringBuilder();
            
            for (Action action : actions) {
                sb.append("    public void ");
                sb.append(Tools.getCamelCase(action.getName()));
                sb.append(action.getMethodParametersAsString());
                sb.append(" {");
                sb.append(StringConstants.NEWLINE);
                //CHECKSTYLE:OFF
                sb.append("        //TODO: Auto-generated method stub\n    }");
                //CHECKSTYLE:ON
                sb.append(StringConstants.NEWLINE);
            }
            
            return sb.toString();
        }
        return StringConstants.EMPTY;
    }
}
