/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.api.gen.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.toolkit.api.gen.ActionHandlerGenerator;
import org.eclipse.jubula.toolkit.api.gen.ComponentClassGenerator;
import org.eclipse.jubula.toolkit.api.gen.FactoryGenerator;
import org.eclipse.jubula.toolkit.api.gen.ToolkitInfoGenerator;
import org.eclipse.jubula.toolkit.api.gen.internal.genmodel.CommonGenInfo;
import org.eclipse.jubula.toolkit.api.gen.internal.genmodel.CompInfoForFactoryGen;
import org.eclipse.jubula.toolkit.api.gen.internal.genmodel.CompInfoForToolkitGen;
import org.eclipse.jubula.toolkit.api.gen.internal.genmodel.ComponentGenInfo;
import org.eclipse.jubula.toolkit.api.gen.internal.genmodel.FactoryGenInfo;
import org.eclipse.jubula.toolkit.api.gen.internal.genmodel.ToolkitGenInfo;
import org.eclipse.jubula.toolkit.api.gen.internal.utils.ConfigLoader;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitConfig;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitInfo;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;

/**
 * Generates classes for components from comp system
 */
public class APIGenerator {
    
    /** class generator */
    private static ComponentClassGenerator componentClassGenerator =
            new ComponentClassGenerator();

    /** action handler class generator */
    private static ActionHandlerGenerator actionHandlerGenerator = 
            new ActionHandlerGenerator();
    
    /** factory generator */
    private static FactoryGenerator factoryGenerator =
            new FactoryGenerator();

    /** factory generator */
    private static ToolkitInfoGenerator toolkitInfoGenerator =
            new ToolkitInfoGenerator();
    
    /** containing information for factory generation.
     *  will be reseted for each toolkit */
    private static FactoryGenInfo factoryGenInfo;

    /** containing information for toolkit information generation.
     *  will be reseted for each toolkit */
    private static ToolkitGenInfo tookitGenInfo;
    
    /** whether a class containing information about a toolkit needs to be generated */
    private static boolean toolkitNeedsInfoClass = false;
    
    /** 
     * map containing component name mappings
     * will be reseted for each component
     */
    private static Map<String, String> compNameMap =
            new TreeMap<String, String>();
    
    /**
     * Constructor
     */
    private APIGenerator() {
        // hidden
    }

    /** 
     * main
     * @param args args
     */
    public static void main(String[] args) {
        ConfigLoader loader = ConfigLoader.getInstance();
        String generationBaseDir = loader.getGenerationDir();
        ToolkitConfig config = loader.getToolkitConfig();
        CompSystemProcessor processor = new CompSystemProcessor(config);
                
        List<ToolkitInfo> toolkitInfos = processor.getToolkitInfos();
        
        String converterInfoDir = loader.getConverterInfoDir();

        // Clean up
        File converterDirectory = new File(converterInfoDir);
        if (converterDirectory.exists()) {
            try {
                FileUtils.cleanDirectory(converterDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }            
        } else {
            converterDirectory.mkdirs();
        }
        for (ToolkitInfo tkInfo : toolkitInfos) {
            cleanUp(tkInfo, generationBaseDir);
        }
        
        // Generate classes and interfaces toolkit by toolkit
        for (ToolkitInfo tkInfo : toolkitInfos) {
            factoryGenInfo = new FactoryGenInfo();
            tookitGenInfo = new ToolkitGenInfo();
            toolkitNeedsInfoClass = false;
            List<ComponentInfo> compInfos = processor.getCompInfos(
                    tkInfo.getType(), tkInfo.getShortType());
            for (ComponentInfo compInfo : compInfos) {
                compNameMap.clear();
                Component component = compInfo.getComponent();
                // generate interface
                createClass(component, generationBaseDir, true);
                //generate implementation class
                createClass(component, generationBaseDir, false);
                createComponentNameInfoFile(component, compNameMap,
                        converterInfoDir);
            }
            // Generate a component factory and an information class for each toolkit
            CompSystem compSystem = processor.getCompSystem();
            ToolkitDescriptor toolkitDesriptor = compSystem
                    .getToolkitDescriptor(tkInfo.getType());
            CommonGenInfo genInfoForFactory =
                    new CommonGenInfo(toolkitDesriptor, false);
            if (toolkitNeedsInfoClass) {
                CommonGenInfo genInfoForToolkit =
                        new CommonGenInfo(toolkitDesriptor, true);
                createToolkitInfo(genInfoForToolkit, generationBaseDir);
                factoryGenInfo.setToolkitInfoName(
                        genInfoForToolkit.getToolkitName(),
                        genInfoForToolkit.getFqClassName());
            }
            createFactory(genInfoForFactory, generationBaseDir);
        }
    }

    /** 
     * Creates the info file for a component for the converter
     * @param component the component
     * @param map name info map
     * @param dirPath path where to put info file
     */
    private static void createComponentNameInfoFile(Component component,
            Map<String, String> map, String dirPath) {
        StringBuffer content = new StringBuffer();
        for (String key : map.keySet()) {
            content.append(key + StringConstants.EQUALS_SIGN
                    + map.get(key) + "\n"); //$NON-NLS-1$
        }
        File dir = new File(dirPath);
        File file = new File(dirPath + StringConstants.SLASH
                + component.getType() + ".properties"); //$NON-NLS-1$
        createFile(dir, file, content.toString());
    }

    /**
     * Deletes all generated content of a given toolkit
     * @param tkInfo the toolkit
     * @param generationBaseDirTemplate location of generated content
     */
    private static void cleanUp(ToolkitInfo tkInfo,
            String generationBaseDirTemplate) {
        
        String name = tkInfo.getShortType().toLowerCase()
                .replace("abstract", "base"); //$NON-NLS-1$ //$NON-NLS-2$
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {name});
        File dir = new File(generationBaseDir);
        emptyDirectory(dir);
    }

    /**
     * Empties a directory recursively
     * @param dir the directory
     */
    private static void emptyDirectory(File dir) {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
    }

    /** 
     * creates class for component
     * @param component the component
     * @param generationBaseDirTemplate directory for generation
     * @param generateInterface whether an interface should be generated
     */
    private static void createClass(Component component,
            String generationBaseDirTemplate, Boolean generateInterface) {
        CommonGenInfo genInfo = new CommonGenInfo(component);
        ComponentGenInfo compInfo = new ComponentGenInfo(component,
                generateInterface, genInfo.getToolkitPackageName(),
                genInfo.getClassName(), compNameMap);
        genInfo.setSpecificInformation(compInfo);
        String path = StringConstants.EMPTY;
        if (generateInterface) {
            path = compInfo.getInterfaceDirectoryPath();
        } else {
            path = genInfo.getClassDirectoryPath();
        }
        String className = genInfo.getClassName();
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {genInfo.getToolkitPackageName()});
        File dir = new File(generationBaseDir + path);
        File file = new File(dir, className + ".java"); //$NON-NLS-1$
        String content = componentClassGenerator.generate(genInfo);

        createFile(dir, file, content);

        dir = new File(generationBaseDir + path + "/handler"); //$NON-NLS-1$
        file = new File(dir, className + "ActionHandler.java"); //$NON-NLS-1$
        content = actionHandlerGenerator.generate(genInfo);
        createFile(dir, file, content);
        
        /* After generating an impl class, add component information
         *  to factory and toolkit generation information */
        if (!generateInterface) {
            ComponentClass componentClass = null;
            String testerClass = null;
            if (component instanceof ConcreteComponent) {
                ConcreteComponent concreteComponent =
                        (ConcreteComponent) component;
                componentClass = concreteComponent.getComponentClass();
                testerClass = concreteComponent.getTesterClass();
                if (componentClass != null && testerClass != null) {
                    toolkitNeedsInfoClass = true;
                }
            }
            
            if (!compInfo.hasDefaultMapping()
                    || (componentClass != null 
                        && !componentClass.getName().isEmpty())) {
                CompInfoForFactoryGen compInfoForFactory = 
                    new CompInfoForFactoryGen(
                        genInfo.getClassName(),
                        genInfo.getClassPackageName(),
                        componentClass,
                        compInfo.hasDefaultMapping(),
                        compInfo.getMostSpecificVisibleSuperTypeName());
                String sinceC = component.getSince();
                if (StringUtils.isNotBlank(sinceC)) {
                    compInfoForFactory.setSince(sinceC);
                }
                factoryGenInfo.addCompInformation(compInfoForFactory);
            }
            
            tookitGenInfo.addCompInformation(new CompInfoForToolkitGen(
                    componentClass, testerClass));
        }
    }

    /** 
     * creates factory for toolkit
     * @param tkGenInfo the generation information for the toolkit
     * @param generationBaseDirTemplate directory for generation
     */
    private static void createFactory(CommonGenInfo tkGenInfo,
            String generationBaseDirTemplate) {
        tkGenInfo.setSpecificInformation(factoryGenInfo);
        String path = tkGenInfo.getClassDirectoryPath();
        String className = tkGenInfo.getClassName();
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {tkGenInfo.getToolkitPackageName()});
        File dir = new File(generationBaseDir + path);
        File file = new File(dir, className + ".java"); //$NON-NLS-1$
        String content = factoryGenerator.generate(tkGenInfo);

        createFile(dir, file, content); 
    }
    
    /** 
     * creates toolkit information class
     * @param tkGenInfo the generation information for the toolkit
     * @param generationBaseDirTemplate directory for generation
     */
    private static void createToolkitInfo(CommonGenInfo tkGenInfo,
            String generationBaseDirTemplate) {
        tkGenInfo.setSpecificInformation(tookitGenInfo);
        String path = tkGenInfo.getClassDirectoryPath();
        String className = tkGenInfo.getClassName();
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {tkGenInfo.getToolkitPackageName()});
        File dir = new File(generationBaseDir + path);
        File file = new File(dir, className + ".java"); //$NON-NLS-1$
        String content = toolkitInfoGenerator.generate(tkGenInfo);

        createFile(dir, file, content); 
    }

    /** creates a file with given content in a given directory
     * @param dir the directory
     * @param file the file
     * @param content the content
     */
    private static void createFile(File dir, File file, String content) {
        if (!file.exists()) {
            try {
                dir.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            try (FileOutputStream fop = new FileOutputStream(file)) {
                byte[] contentInBytes = content.getBytes();
                IOUtils.write(contentInBytes, fop);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: " + file.getName() + " already exists!"); //$NON-NLS-1$ //$NON-NLS-2$
            System.exit(1);
        }
    }
}