/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.toolkit.ui.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.toolkit.ui.utils.ComponentActionPair;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitInfo;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.swt.widgets.DirectoryDialog;

/**
 * Generates list of supported actions from comp system
 */
public class ExportActionsHandler extends AbstractHandler {
        
    @Override
    protected Object executeImpl(ExecutionEvent event) {
        DirectoryDialog dialog = new DirectoryDialog(getActiveShell());
        dialog.setFilterPath(Utils.getLastDirPath());
        String targetDir = dialog.open();
        if (targetDir != null) {
            Utils.storeLastDirPath(dialog.getFilterPath());
            CompSystemProcessor processor = new CompSystemProcessor(
                    ComponentBuilder.getInstance().getCompSystem());
            
            List<ToolkitInfo> toolkitInfos = processor.getToolkitInfos();
            
            for (ToolkitInfo tkInfo : toolkitInfos) {
                // Generate classes and interfaces toolkit by toolkit
                SortedSet<ComponentActionPair> list =
                        new TreeSet<ComponentActionPair>();
                String tkName = tkInfo.getShortType();
                List<ComponentInfo> compInfos = processor.getCompInfos(
                        tkInfo.getType(), tkName);
                for (ComponentInfo compInfo : compInfos) {
                    Component component = compInfo.getComponent();
                    if (!component.isConcrete()
                            || ((ConcreteComponent)component).getTesterClass()
                            == null) {
                        continue;
                    }
                    for (Action action : component.getActions()) {
                        list.add(new ComponentActionPair(
                                ((ConcreteComponent) component)
                                        .getComponentClass().getName(),
                                CompSystemI18n.getString(action.getName())));
                    }
                }
                writeListToFile(targetDir, list, tkName);
            }
        }
        return null;
    }

    /**
     * @param dirPath path to directory
     * @param list list of component action pairs
     * @param tkName toolkitname
     */
    private static void writeListToFile(String dirPath,
            SortedSet<ComponentActionPair> list, String tkName) {
        StringBuffer content = new StringBuffer();
        for (ComponentActionPair pair : list) {
            content.append(pair.toString() + "\n"); //$NON-NLS-1$
        }
        File dir = new File(dirPath);
        File file = new File(dirPath + "/" + tkName.toLowerCase() //$NON-NLS-1$
                + "_expectedCAPs.txt"); //$NON-NLS-1$
        createFile(dir, file, content.toString());
    }

    /** creates a file with given content in a given directory
     * @param dir the directory
     * @param file the file
     * @param content the content
     */
    private static void createFile(File dir, File file, String content) {
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
    }
}