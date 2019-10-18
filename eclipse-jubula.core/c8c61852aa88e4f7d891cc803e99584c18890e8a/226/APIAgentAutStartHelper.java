/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.eclipse.jubula.autagent.common.commands.AbstractStartJavaAut;
import org.eclipse.jubula.autagent.common.utils.IAUTStartHelper;

/**
 * @author BREDEX GmbH
 *
 */
public class APIAgentAutStartHelper implements IAUTStartHelper {
    
    /** dir where extensions should be stored */
    public static final String EXTENSION_DIR = "ext"; //$NON-NLS-1$
    /** file extension of jars*/
    private static final String JAR = ".jar"; //$NON-NLS-1$
    /** Fragment Host key in manifest.mf*/
    private static final String MANIFEST_HOST_KEY = "Fragment-Host"; //$NON-NLS-1$
    /** {@link FilenameFilter} for '.jar' files */
    private static final FilenameFilter JARFILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            if (dir.exists() && name.endsWith(JAR)) {
                return true;
            }
            return false;
        }
    };
    /**
     * {@inheritDoc}
     */
    @Override
    public File getInstallationDirectory() {
        CodeSource codeSource = APIAgentAutStartHelper.class
                .getProtectionDomain().getCodeSource();
        File file = new File(codeSource.getLocation().getPath());
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMonitoringAgent(Map<String, String> parameters) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getClasspathEntriesForBundleId(String bundleId) {
        final File f = getInstallationDirectory();
        String path = f.getAbsolutePath();
        List<String> jarList = new ArrayList<>(1);
        jarList.add(path
                + FileSystems.getDefault().getSeparator() + bundleId + JAR);
        getFragmentsforBundleId(bundleId, f);
        return jarList.toArray(new String[jarList.size()]);
    }
    
    /**
     * 
     * @param bundleId the bundle id
     * @param installDir the directory where the fragments should be searched in
     * @return the list of fragment jar corresponding to the bundle
     */
    private List<File> getFragmentsforBundleId(String bundleId,
            final File installDir) {
        List<File> files = new ArrayList<>();
        File[] jars = installDir.listFiles(JARFILTER);
        if (jars == null) {
            return files;
        }
        for (File file : jars) {
            try (JarFile jar = new JarFile(file)) {
                Attributes attributes = jar.getManifest().getMainAttributes();
                String value = attributes.getValue(MANIFEST_HOST_KEY);
                if (value != null && value.equals(bundleId)) {
                    files.add(file);
                }
            } catch (IOException e) {
                // ignore
            }
        }
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getFragmentPathforBundleID(String rcBundleID) {
        Map<String, String> fragmentMap = new HashMap<>();
        File installDir = AbstractStartJavaAut.getInstallDir();
        
        File extDir = new File(installDir, EXTENSION_DIR);
        List<File> fragmentsfromExt =
                getFragmentsforBundleId(rcBundleID, extDir);
        for (File file : fragmentsfromExt) {
            fragmentMap.put(file.getAbsolutePath(), file.getName());
        }
        List<File> fragmentsfromMain =
                getFragmentsforBundleId(rcBundleID, installDir);
        for (File file : fragmentsfromMain) {
            fragmentMap.put(file.getAbsolutePath(), file.getName());
        }
        return fragmentMap;
    }

}
