package org.educama.shipment.boundary.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskAlreadyClaimedException;
import org.educama.common.exceptions.ResourceNotFoundException;
import org.educama.shipment.api.resource.InvoiceResource;
import org.educama.shipment.api.resource.ShipmentResource;
import org.educama.shipment.boundary.ShipmentBoundaryService;
import org.educama.shipment.control.ShipmentCaseControlService;
import org.educama.shipment.model.Invoice;
import org.educama.shipment.model.Flight;
import org.educama.shipment.model.Shipment;
import org.educama.shipment.process.ShipmentCaseEvaluator;
import org.educama.shipment.process.tasks.CompleteShipmentOrderTask;
import org.educama.shipment.process.tasks.CreateInvoiceTask;
import org.educama.shipment.repository.InvoiceRepository;
import org.educama.shipment.process.tasks.OrganizeFlightTask;
import org.educama.shipment.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

/**
 * Boundary service implementation for shipments.
 */
@Service
@Transactional(readOnly = false)
public class ShipmentBoundaryServiceImpl implements ShipmentBoundaryService {

    private CompleteShipmentOrderTask completeShipmentOrderTask;

    private OrganizeFlightTask organizeFlightTask;

    private CreateInvoiceTask createInvoiceTask;

    private ShipmentRepository shipmentRepository;

    private InvoiceRepository invoiceRepository;

    private ShipmentCaseControlService shipmentCaseControlService;

    private ShipmentCaseEvaluator shipmentCaseEvaluator;

    @Autowired
    public ShipmentBoundaryServiceImpl(CompleteShipmentOrderTask completeShipmentOrderTask,
                                       OrganizeFlightTask organizeFlightTask,
                                       CreateInvoiceTask createInvoiceTask,
                                       ShipmentRepository shipmentRepository,
                                       InvoiceRepository invoiceRepository,
                                       ShipmentCaseControlService shipmentCaseControlService,
                                       ShipmentCaseEvaluator shipmentCaseEvaluator,
                                       ProcessEngine processEngine) {
        this.completeShipmentOrderTask = completeShipmentOrderTask;
        this.organizeFlightTask = organizeFlightTask;
        this.createInvoiceTask = createInvoiceTask;
        this.shipmentRepository = shipmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.shipmentCaseControlService = shipmentCaseControlService;
        this.shipmentCaseEvaluator = shipmentCaseEvaluator;
    }

    @Override
    public Shipment createShipment(Shipment shipment) {
        shipment.trackingId = UUID.randomUUID().toString();
        Shipment createdShipment = shipmentRepository.saveAndFlush(shipment);
        shipmentCaseControlService.create(shipment.trackingId);
        return createdShipment;
    }

    @Override
    public Collection<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    @Override
    public ShipmentResource getShipment(String trackingId) {
        Shipment shipment = shipmentRepository.findOneBytrackingId(trackingId);
        ShipmentResource convertedShipment = new ShipmentResource().fromShipment(shipment);
        return convertedShipment;
    }

    @Override
    public InvoiceResource createInvoice(String trackingId, Invoice saveInvoiceResource) {
        Shipment shipment = shipmentRepository.findOneBytrackingId(trackingId);

        if (shipment == null) {
            throw new ResourceNotFoundException("Shipment not found");
        } else {

            if (createInvoiceTask.isActive(trackingId) && createInvoiceTask.canBeCompleted(trackingId)) {
                saveInvoiceResource.invoiceNumber = UUID.randomUUID().toString();
                saveInvoiceResource.shipmentId = shipment.getId();
                Invoice createdInvoice = invoiceRepository.saveAndFlush(saveInvoiceResource);
                createInvoiceTask.complete(trackingId);
                shipmentCaseEvaluator.reevaluateCase(trackingId);
                InvoiceResource convertedInvoice = new InvoiceResource().fromInvoice(createdInvoice);
                return convertedInvoice;
            } else {
                throw new ResourceNotFoundException("There is no active createInvoiceTask");
            }
        }
    }

    @Override
    public ShipmentResource updateShipment(String trackingId, Shipment saveShipmentResource) {
        Shipment shipment = shipmentRepository.findOneBytrackingId(trackingId);
        if (shipment == null) {
            throw new ResourceNotFoundException("Shipment not found");
        } else {
            shipment.customerTypeEnum = saveShipmentResource.customerTypeEnum;
            shipment.receiver = saveShipmentResource.receiver;
            shipment.sender = saveShipmentResource.sender;
            shipment.shipmentCargo = saveShipmentResource.shipmentCargo;
            shipment.shipmentServices = saveShipmentResource.shipmentServices;
            shipment = shipmentRepository.saveAndFlush(shipment);

            if (completeShipmentOrderTask.isActive(trackingId) && completeShipmentOrderTask.canBeCompleted(trackingId)) {
                completeShipmentOrderTask.complete(trackingId);
                shipmentCaseEvaluator.reevaluateCase(trackingId);
            }

            ShipmentResource convertedShipment = new ShipmentResource().fromShipment(shipment);
            return convertedShipment;
        }
    }

    @Override
    public ShipmentResource addFlightToShipment(String trackingId, Flight saveFlightResource) {
        Shipment shipment = shipmentRepository.findOneBytrackingId(trackingId);
        if (shipment == null) {
            throw new ResourceNotFoundException("Shipment not found");
        } else {
            shipment.shipmentFlight = saveFlightResource;
            shipment = shipmentRepository.saveAndFlush(shipment);
            organizeFlightTask.complete(trackingId);
            shipmentCaseEvaluator.reevaluateCase(trackingId);
            ShipmentResource convertedShipment = new ShipmentResource().fromShipment(shipment);
            return convertedShipment;
        }
    }

}
