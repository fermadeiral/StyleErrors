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
package org.eclipse.jubula.client.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.persistence.PersistenceException;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jubula.client.archive.dto.ExportInfoDTO;
import org.eclipse.jubula.client.archive.dto.ProjectDTO;
import org.eclipse.jubula.client.archive.dto.TestresultSummaryDTO;
import org.eclipse.jubula.client.archive.i18n.Messages;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/** @author BREDEX GmbH */
public class JsonStorage {
    /** Extension of project file */
    public static final String PJT = "pjt"; //$NON-NLS-1$
    
    /** Extension of test result summaries file */
    public static final String RST = "rst"; //$NON-NLS-1$

    /** Extension of info file */
    public static final String NFO = "nfo"; //$NON-NLS-1$
    
    /** */
    private static final String TMP_EXCHANGE_FOLDER_NAME = "JubArchiveTemp"; //$NON-NLS-1$
    
    /** Standard logging */
    private static Logger log = LoggerFactory.getLogger(JsonStorage.class);
    
    /**
     * Save a project as JUB to a file or return the serialized project as
     * an ProjectDTO, if fileName == null!
     * 
     * @param proj original project object
     * @param fileName Jubula file name
     * @param includeTestResultSummaries true if project contain test result summaries
     * @param monitor loader monitor
     * @param console 
     * @return ProjectDTO 
     * @throws PMException
     * @throws ProjectDeletedException 
     * @throws InterruptedException 
     */
    public static ProjectDTO save(IProjectPO proj, String fileName,
            boolean includeTestResultSummaries, IProgressMonitor monitor,
            IProgressConsole console)
                    throws PMException, ProjectDeletedException {
        
        monitor.beginTask(Messages.GatheringProjectData, 
                getWorkToSave(proj, includeTestResultSummaries));
        monitor.subTask(Messages.ImportJsonStoragePreparing);
        Validate.notNull(proj);
        try {
            if (fileName == null) {
                JsonExporter exporter = new JsonExporter(proj, monitor);
                return exporter.getProjectDTO();
            }
            writeToFile(proj, monitor, fileName, includeTestResultSummaries);
        } catch (FileNotFoundException e) {
            log.info(Messages.File + StringConstants.SPACE 
                    + Messages.NotFound);
            console.writeStatus(new Status(IStatus.WARNING,
                    Activator.PLUGIN_ID, Messages.NotFound));
            throw new PMSaveException(Messages.File + StringConstants.SPACE 
                    + fileName + Messages.NotFound + StringConstants.COLON 
                    + StringConstants.SPACE 
                    + e.toString(), MessageIDs.E_FILE_IO);
        } catch (IOException e) {
            // If the operation has been canceled, then this is just
            // a result of canceling the IO.
            if (!monitor.isCanceled()) {
                log.warn(Messages.GeneralIoExeption);
                console.writeStatus(new Status(IStatus.WARNING,
                        Activator.PLUGIN_ID, Messages.GeneralIoExeption));
                throw new PMSaveException(Messages.GeneralIoExeption 
                        + e.toString(), MessageIDs.E_FILE_IO);
            }
        } catch (PersistenceException e) {
            log.warn(Messages.CouldNotInitializeProxy 
                    + StringConstants.DOT);
            console.writeStatus(new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                    Messages.CouldNotInitializeProxy));
            throw new PMSaveException(e.getMessage(),
                MessageIDs.E_DATABASE_GENERAL);
        } catch (OperationCanceledException e) {
            // Operation was cancelled.
            log.info(Messages.ExportOperationCanceled);
            console.writeStatus(new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                    Messages.ExportOperationCanceled));
        }
        return null;
    }
    
    /** Save a project as JUB witch contains an info file about exportation, a project file
     *  and a result file
     *  
     * @param proj original project object
     * @param monitor loader monitor
     * @param fileName Jubula file name
     * @param includeTestResultSummaries true if project contain test result summaries
     * @throws ProjectDeletedException
     * @throws PMException
     * @throws IOException
     * @throws InterruptedException 
     */
    private static void writeToFile(IProjectPO proj, IProgressMonitor monitor,
            String fileName, boolean includeTestResultSummaries)
                    throws ProjectDeletedException, PMException, IOException {
        
        String dir = Files.createTempDirectory(TMP_EXCHANGE_FOLDER_NAME)
                .toString() + File.separatorChar;
        String infoFileName = dir + NFO;
        String projectFileName = dir + PJT;
        String testResultFileName = dir + RST;
        
        ArrayList<String> fileList = new ArrayList<String>();
        ObjectMapper mapper = new ObjectMapper(); 
        // changed when upgrading Jackson to 2.6.2 to 2.5
        // previously it was NON_EMPTY, but that resulted in 0 Integers
        // being not serialised with 2.6.2, so they behaved the same way as null
        // Integers. Non-serialised fields are initialised by the
        // default () constructor of DTOs.
        // NON-EMPTY: empty and null Strings, empty and null Collections and null Integers / Doubles are not serialised
        // NON-NULL: null Objects are not serialised
        mapper.setSerializationInclusion(Include.NON_NULL);

        ExportInfoDTO exportDTO = new ExportInfoDTO();
        exportDTO.setQualifier(
                ImportExportUtil.DATE_FORMATTER.format(
                        new Date()));
        exportDTO.setEncoding(StandardCharsets.UTF_8.name());
        exportDTO.setVersion(JsonVersion.CURRENTLY_JSON_VERSION);
        
        try (
            FileWriterWithEncoding infoWriter = new FileWriterWithEncoding(
                    infoFileName, StandardCharsets.UTF_8);
            FileWriterWithEncoding projectWriter = new FileWriterWithEncoding(
                    projectFileName, StandardCharsets.UTF_8);
            FileWriterWithEncoding resultWriter = new FileWriterWithEncoding(
                    testResultFileName, StandardCharsets.UTF_8)) {
            
            mapper.writeValue(infoWriter, exportDTO);
            fileList.add(infoFileName);
            JsonExporter exporter = new JsonExporter(proj, monitor);
            ProjectDTO projectDTO = exporter.getProjectDTO();

            mapper.writeValue(projectWriter, projectDTO);
            fileList.add(projectFileName);
            if (includeTestResultSummaries) {
                exporter.writeTestResultSummariesToFile(resultWriter);
                fileList.add(testResultFileName);
            }
            monitor.subTask(Messages.ImportJsonStorageCompress);
            
            zipIt(fileName, fileList);
        } catch (Exception e) {
            fileList.add(fileName);
            throw e;
        } finally {
            fileList.add(dir);
            deleteFiles(fileList);
        }
    }
    
    /** 
     * @param files what we need to delete
     * @throws IOException
     */
    private static void deleteFiles(List<String> files) {
        for (String fileSrt : files) {
            try {
                Files.deleteIfExists(new File(fileSrt).toPath());
            } catch (IOException e) {
                log.warn(Messages.CantDeleteFile + fileSrt);
            }
        }
    }

    /**
     * @param project The project for which the work is predicted.
     * @param includeTestResultSummaries true if test result summary needed.
     * @return The predicted amount of work required to save a project.
     */
    public static int getWorkToSave(IProjectPO project,
            boolean includeTestResultSummaries) {
        return JsonExporter.getPredictedWork(project,
                includeTestResultSummaries);
    }

    /**
     * @param projectsToSave The projects for which the work is predicted.
     * @return The predicted amount of work required to save the given projects.
     */
    public static int getWorkToSave(List<IProjectPO> projectsToSave) {
        int totalWork = 0;
        
        for (IProjectPO project : projectsToSave) {
            totalWork += getWorkToSave(project, false);
        }

        return totalWork;
    }
    
    /**
     * @param url of import file
     * @param paramNameMapper 
     * @param compNameCache 
     * @param assignNewGuid <code>true</code> if the project and all subnodes
     *                      should be assigned new GUIDs. Otherwise 
     *                      <code>false</code>.
     * @param assignNewVersion if <code>true</code> the project will have
     *                      a new project version number, otherwise it will
     *                      have the stored project version from the dto.
     * @param monitor 
     * @param io console
     * @return IProjectPO new project object 
     * @throws JBVersionException 
     * @throws PMReadException 
     * @throws InterruptedException 
     * @throws ToolkitPluginException 
     * @throws PMSaveException 
     */
    public IProjectPO readProject(URL url, ParamNameBPDecorator paramNameMapper,
            final IWritableComponentNameCache compNameCache,
            boolean assignNewGuid, boolean assignNewVersion,
            IProgressMonitor monitor, IProgressConsole io)
                    throws JBVersionException, PMReadException,
                    InterruptedException, ToolkitPluginException {

        SubMonitor subMonitor = SubMonitor.convert(monitor, Messages
                .ImportFileBPReading, 2);
        IProjectPO projectPO = null;
        monitor.subTask(Messages.ImportJsonStoragePreparing);
        try (InputStream urlInputStream = url.openStream();
             ZipInputStream zipInputStream = new ZipInputStream(
                     urlInputStream,
                     StandardCharsets.UTF_8);) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
            
            Map<String, Class> fileTypeMapping = new HashMap<>();
            fileTypeMapping.put(NFO, ExportInfoDTO.class);
            fileTypeMapping.put(PJT, ProjectDTO.class);
            TypeReference tr = new TypeReference<ArrayList<
                    TestresultSummaryDTO>>() { /* anonymous inner type */ };
            
            Map<String, Object> allDTOs = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                ZipEntry entry = zipInputStream.getNextEntry();
                String entryName = entry.getName();
                Class entryTypeMapping = fileTypeMapping.get(entryName);
                allDTOs.put(entryName, entryTypeMapping != null
                        ? mapper.readValue(zipInputStream, entryTypeMapping)
                                : mapper.readValue(zipInputStream, tr));
            }
            
            ExportInfoDTO exportDTO = (ExportInfoDTO) allDTOs.get(NFO);
            checkMinimumRequiredJSONVersion(exportDTO);

            ProjectDTO projectDTO = (ProjectDTO) allDTOs.get(PJT);
            
            if (!assignNewGuid && projectExists(projectDTO)) {
                existProjectHandling(io, projectDTO);
                return null;
            }
            projectPO = load(projectDTO, subMonitor.newChild(1), io,
                    assignNewGuid, assignNewVersion, paramNameMapper,
                    compNameCache, false, exportDTO);

            JsonImporter importer = new JsonImporter(monitor, io, false,
                    exportDTO);
            List<TestresultSummaryDTO> summaryDTOs = 
                    (List<TestresultSummaryDTO>) allDTOs.get(RST);
            
            importer.initTestResultSummaries(subMonitor.newChild(1),
                    summaryDTOs, projectPO);
        } catch (IOException e) {
            log.warn("error during import", e); //$NON-NLS-1$
            // If the operation has been canceled, then this is just
            // a result of canceling the IO.
            if (!monitor.isCanceled()) {
                log.info(Messages.GeneralIoExeption);
                throw new PMReadException(Messages.InvalidImportFile,
                        MessageIDs.E_IO_EXCEPTION);
            }
        }

        return projectPO;
    }
    
    /**
     * @param dto storage of the project
     * @param monitor 
     * @param io console
     * @param assignNewGuid <code>true</code> if the project and all subnodes
     *                      should be assigned new GUIDs. Otherwise 
     *                      <code>false</code>.
     * @param assignNewVersion if <code>true</code> the project will have
     *                      a new project version number, otherwise it will
     *                      have the stored project version from the dto.
     * @param paramNameMapper 
     * @param compNameCache 
     * @param skipTrackingInformation  
     * @param exportInfo the exported verison information
     * @return IProjectPO 
     * @throws JBVersionException
     * @throws InterruptedException
     * @throws PMReadException 
     * @throws ToolkitPluginException 
     */
    public static IProjectPO load(ProjectDTO dto, IProgressMonitor monitor,
            IProgressConsole io, boolean assignNewGuid,
            boolean assignNewVersion, IParamNameMapper paramNameMapper,
            IWritableComponentNameCache compNameCache,
            boolean skipTrackingInformation, ExportInfoDTO exportInfo)
                    throws JBVersionException, InterruptedException,
                    PMReadException, ToolkitPluginException {

        IProjectPO projectPO = null;
        try {
            JsonImporter importer = new JsonImporter(monitor, io,
                    skipTrackingInformation, exportInfo);
            projectPO = importer.createProject(dto, assignNewGuid,
                    assignNewVersion, paramNameMapper, compNameCache);
        } catch (InvalidDataException e) {
            throw new PMReadException(Messages.InvalidImportFile,
                e.getErrorId());
        } 
        
        return projectPO;
    }

    /**
     * @param io console
     * @param projectDTO 
     */
    private void existProjectHandling(IProgressConsole io,
            ProjectDTO projectDTO) {
        
        String msg = NLS.bind(Messages.ErrorMessageIMPORT_PROJECT_FAILED,
                new String [] {ProjectNameBP.getInstance().getName(
                        projectDTO.getUuid(), false)})
            + StringConstants.NEWLINE
            + NLS.bind(Messages.ErrorMessageIMPORT_PROJECT_FAILED_EXISTING,
                new String [] {projectDTO.getName(),
                        projectDTO.getProjectVersion().toString()});
        
        io.writeStatus(new Status(IStatus.WARNING, Activator.PLUGIN_ID, msg));
    }

    /**
     * @param dto the project dto
     * @throws JBVersionException
     *             in case of version conflict between given dto and minimum dto
     *             version number; if these versions do not fit the current
     *             available converter are not able to convert the given project
     *             dto properly.
     */
    private void checkMinimumRequiredJSONVersion(ExportInfoDTO dto)
        throws JBVersionException {
        if (dto.getVersion() == null
                || !JsonVersion.isCompatible(dto.getVersion())) {
            List<String> errorMsgs = new ArrayList<String>();
            errorMsgs.add(Messages.JubImporterProjectJUBTooOld);
            throw new JBVersionException(
                    Messages.JubImporterProjectJUBTooOld,
                    MessageIDs.E_LOAD_PROJECT_XML_VERSION_ERROR,
                    errorMsgs);
        }
    }

    /**
     * Zip it
     * @param zipFile output ZIP file location
     * @param fileList it contains files what we would like to compress
     * @throws IOException 
     */
    private static void zipIt(String zipFile, ArrayList<String> fileList)
            throws IOException {

        byte[] buffer = new byte[1024];
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
            
        for (String file : fileList) {
            String fileName = file.substring(file.lastIndexOf(
                    File.separator) + 1);
            ZipEntry ze = new ZipEntry(fileName);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(file);
           
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            in.close();
        }
        zos.closeEntry();
        zos.close();
    }
    
    /**
     * @param dto ProjectDTO what we wanted to import. 
     * @return <code>true</code> if another project with the same GUID and
     *         version number as the currently imported project already 
     *         exists in the database. Otherwise <code>false</code>.
     */
    private boolean projectExists(ProjectDTO dto) {
        
        return ProjectPM.doesProjectVersionExist(dto.getUuid(),
                dto.getMajorProjectVersion(), dto.getMinorProjectVersion(),
                dto.getMicroProjectVersion(), dto.getProjectVersionQualifier());
    }
}
