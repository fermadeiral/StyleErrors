/**
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.vorto.repository.web.api.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.eclipse.vorto.model.ModelId;
import org.eclipse.vorto.repository.core.IUserContext;
import org.eclipse.vorto.repository.core.ModelInfo;
import org.eclipse.vorto.repository.core.TenantNotFoundException;
import org.eclipse.vorto.repository.core.impl.UserContext;
import org.eclipse.vorto.repository.domain.Tenant;
import org.eclipse.vorto.repository.importer.Context;
import org.eclipse.vorto.repository.importer.FileUpload;
import org.eclipse.vorto.repository.importer.IModelImportService;
import org.eclipse.vorto.repository.importer.IModelImporter;
import org.eclipse.vorto.repository.importer.UploadModelResult;
import org.eclipse.vorto.repository.tenant.ITenantService;
import org.eclipse.vorto.repository.tenant.TenantDoesntExistException;
import org.eclipse.vorto.repository.web.AbstractRepositoryController;
import org.eclipse.vorto.repository.web.api.v1.dto.ImporterInfo;
import org.eclipse.vorto.repository.web.core.exceptions.UploadTooLargeException;
import org.eclipse.vorto.repository.workflow.IWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Alexander Edelmann - Robert Bosch (SEA) Pte. Ltd.
 */
@RestController
@RequestMapping(value = "/api/v1/importers")
public class ImportController extends AbstractRepositoryController {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  private final String UPLOAD_VALID = "%s is valid and ready for import.";
  private final String UPLOAD_FAIL = "%s has errors. Cannot import.";
  private final String UPLOAD_WARNING = "Warning! You are about to overwrite an existing model!";

  @Autowired
  private IModelImportService importerService;

  @Value("${repo.config.maxModelSize}")
  private long maxModelSize;

  @Autowired
  private IWorkflowService workflowService;
  
  @Autowired
  private ITenantService tenantService;

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasRole('MODEL_CREATOR')")
  public ResponseEntity<UploadModelResult> uploadModel(
      @ApiParam(value = "The vorto model file to upload",
          required = true) @RequestParam("file") MultipartFile file,
      @RequestParam("key") String key,  @RequestParam(required=true,value="targetNamespace") String targetNamespace) {
    if (file.getSize() > maxModelSize) {
      throw new UploadTooLargeException("model", maxModelSize);
    }

    LOGGER.info("uploadModel: [" + file.getOriginalFilename() + "]");
    try {
      IModelImporter importer = importerService.getImporterByKey(key).get();
      
      UploadModelResult result = importer.upload(
          FileUpload.create(file.getOriginalFilename(), file.getBytes()), Context.create(getUserContext(targetNamespace),Optional.of(targetNamespace)));
      

      if (!result.isValid()) {
        result.setMessage(String.format(UPLOAD_FAIL, file.getOriginalFilename()));
      } else {
        if (result.hasWarnings()) {
          result.setMessage(String.format(UPLOAD_WARNING, file.getOriginalFilename()));
        } else {
          result.setMessage(String.format(UPLOAD_VALID, file.getOriginalFilename()));
        }
      }
       return new ResponseEntity<UploadModelResult>(result,HttpStatus.OK);
    } catch (IOException e) {
      return new ResponseEntity<UploadModelResult>(new UploadModelResult(null, e.getMessage(), Collections.emptyList()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/{handleId:.+}", method = RequestMethod.PUT)
  @PreAuthorize("hasRole('MODEL_CREATOR')")
  public ResponseEntity<List<ModelInfo>> doImport(
      @ApiParam(value = "The file name of uploaded model",
          required = true) final @PathVariable String handleId,
      @RequestParam("key") String key, @RequestParam(required=true,value="targetNamespace") String targetNamespace) {
    LOGGER.info("Importing Model with handleID " + handleId);
    try {

      IModelImporter importer = importerService.getImporterByKey(key).get();
      
      List<ModelInfo> importedModels = importer.doImport(handleId, Context.create(getUserContext(targetNamespace),Optional.of(targetNamespace)));
      for (ModelInfo modelInfo : importedModels) {
        workflowService.start(modelInfo.getId(), getUserContext(modelInfo.getId()));
      }

      return new ResponseEntity<List<ModelInfo>>(importedModels, HttpStatus.OK);
    } catch (Exception e) {
      LOGGER.error("Error Importing model. " + handleId, e);
      throw new IllegalArgumentException("Could not import with handle ID "+handleId, e);
    }
  }

  @ApiOperation(value = "Returns a list of supported importers")
  @PreAuthorize("hasRole('MODEL_CREATOR')")
  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  public List<ImporterInfo> getImporters() {
    List<ImporterInfo> importers = new ArrayList<ImporterInfo>();
    this.importerService.getImporters().stream().forEach(importer -> {
      importers.add(new ImporterInfo(importer.getKey(), importer.getSupportedFileExtensions(),
          importer.getShortDescription()));
    });

    return importers;
  }

  private UserContext getUserContext(String namespace) {
    Optional<Tenant> tenant = this.tenantService.getTenantFromNamespace(namespace);
    if (tenant.isPresent()) {
      return UserContext.user(SecurityContextHolder.getContext().getAuthentication(),tenant.get().getDefaultNamespace());
    } else {
      throw new TenantNotFoundException(namespace);
    }
  }
  
  private IUserContext getUserContext(ModelId id) {
    String tenant = tenantService.getTenantFromNamespace(id.getNamespace())
        .map(tn -> tn.getTenantId())
        .orElseThrow(() -> TenantDoesntExistException.missingForNamespace(id.getNamespace()));
    return UserContext.user(SecurityContextHolder.getContext().getAuthentication(), tenant);
  }
}
