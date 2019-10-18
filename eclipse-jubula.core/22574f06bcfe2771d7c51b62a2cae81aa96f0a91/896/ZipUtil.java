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
package org.eclipse.jubula.tools.internal.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
/**
 * This Util class contains methods to ZIP and unzip directories 
 *
 * @author BREDEX GmbH
 * @created 13.08.2010
 */
public class ZipUtil {
    
    /** file extension for JAR files, in lower case */
    private static final String JAR_FILE_EXT = ".jar"; //$NON-NLS-1$
    
    /** mapping from a ZIP file to its extracted temporary JAR files */
    private static Map<File, File[]> zipToTempJars = 
            new HashMap<File, File[]>();
    
    /**
     * 
     * @author BREDEX GmbH
     * @created 23.06.2011
     */
    public static interface IZipEntryFilter {

        /**
         * 
         * @param entry The ZIP entry to filter.
         * @return <code>true</code> if the ZIP entry should be accepted.
         *         Otherwise, <code>false</code>.
         */
        public boolean accept(ZipEntry entry);
        
    }
    
    /** to prevent instantiation */
    private ZipUtil() {
        //do nothing
    }
    /**
     * This method converts a directory into a zip file
     * @param directory The directory to zip
     * @param zip The destination zip file
     * @throws IOException
     */
    public static final void zipDirectory(File directory, File zip)
        throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        zip(directory, directory, zos);
        zos.close();
    }

    /**
     * This method converts a directory into a zip file
     * @param directory The directory to zip
     * @param base The directory to zip
     * @param zos A ZipOutputStream
     * @throws IOException
     */
    private static final void zip(File directory, File base, 
        ZipOutputStream zos)
        throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[8192];
        int read = 0;
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zos);
            } else {
                FileInputStream in = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(
                        base.getPath().length() + 1));
                zos.putNextEntry(entry);
                while (-1 != (read = in.read(buffer))) {
                    zos.write(buffer, 0, read);
                }
                in.close();
            }
        }
    }
    /**
     * This method unzip's a zip file into a given folder
     * @param zip The zip file
     * @param extractTo The destination folder
     * @throws IOException
     */
    public static final void unzip(File zip, File extractTo) 
        throws IOException {

        unzipFiles(zip, extractTo, new IZipEntryFilter() {
            public boolean accept(ZipEntry entry) {
                return true;
            }
        });
    }
    
    /**
     * Extracts all JAR files from the given ZIP file into temporary JAR files. 
     * The directory structure of the extracted contents is not maintained.
     * A mapping from ZIP file to extracted JARs is maintained by this class,
     * so multiple calls to this method for a single ZIP file will extract JAR 
     * files once and return references to those files for each subsequent call.
     * The extracted JAR files are deleted on VM exit.
     * @param srcZip The ZIP file to extract.
     * @return all extracted files.
     * @throws IOException
     */
    public static File[] unzipTempJars(File srcZip) 
        throws IOException {

        if (zipToTempJars.containsKey(srcZip)) {
            return zipToTempJars.get(srcZip);
        }
        
        IZipEntryFilter filter = new IZipEntryFilter() {
            public boolean accept(ZipEntry entry) {
                return entry.getName().toLowerCase().endsWith(JAR_FILE_EXT);
            }
        };
        ZipFile archive = new ZipFile(srcZip);
        Enumeration<? extends ZipEntry> e = archive.entries();
        List<File> extractedFiles = new ArrayList<File>();
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            if (filter.accept(entry)) {
                if (!entry.isDirectory()) {
                    String prefix = entry.getName().substring(
                            entry.getName().lastIndexOf("/") + 1,  //$NON-NLS-1$
                            entry.getName().toLowerCase().lastIndexOf(
                                JAR_FILE_EXT));
                    File file = File.createTempFile(
                            StringUtils.rightPad(prefix, 3), 
                            JAR_FILE_EXT);
                    extractedFiles.add(file);
                    file.deleteOnExit();
                    unzipFile(archive, file, entry);
                }
            }
        }

        File [] files = extractedFiles.toArray(
                new File[extractedFiles.size()]);
        zipToTempJars.put(srcZip, files);
        return files;
    }
    
    /**
     * Unzips the contents of the given zip file into the given directory.
     * 
     * @param srcZip The zip file to extract.
     * @param targetDir The base directory for extracted files.
     * @param filter Only files accepted by this filter will be extracted.
     * @return all extracted files.
     * @throws IOException
     */
    public static File[] unzipFiles(File srcZip, File targetDir, 
            IZipEntryFilter filter) throws IOException {
        
        ZipFile archive = new ZipFile(srcZip);
        Enumeration e = archive.entries();
        List<File> extractedFiles = new ArrayList<File>();
        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry)e.nextElement();
            if (filter.accept(entry)) {
                File file = new File(targetDir, entry.getName());
                if (entry.isDirectory() && !file.exists()) {
                    file.mkdirs();
                } else {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    
                    extractedFiles.add(file);
                    unzipFile(archive, file, entry);
                }
            }
        }
        return extractedFiles.toArray(new File[extractedFiles.size()]);
    }
    
    /**
     * 
     * @param archive The zip file from which to extract.
     * @param targetFile The file to which the entry contents will be extracted.
     * @param entry The entry to extract.
     * @throws IOException
     */
    private static void unzipFile(ZipFile archive, File targetFile, 
            ZipEntry entry) throws IOException {
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = archive.getInputStream(entry);
            out = new BufferedOutputStream(
                    new FileOutputStream(targetFile));
            
            byte[] buffer = new byte[8192];
            int read;
            
            while (-1 != (read = in.read(buffer))) {
                out.write(buffer, 0, read);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}