package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingInputDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.ServiceOfferingMapper;
import nl.yourivb.TicketTrack.models.ServiceOffering;
import nl.yourivb.TicketTrack.repositories.ServiceOfferingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static nl.yourivb.TicketTrack.utils.AppUtils.allFieldsNull;

@Service
public class ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceOfferingMapper serviceOfferingMapper;

    public ServiceOfferingService(ServiceOfferingRepository serviceOfferingRepository,
                                  ServiceOfferingMapper serviceOfferingMapper) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.serviceOfferingMapper = serviceOfferingMapper;
    }

    public List<ServiceOfferingDto> getAllServiceOfferings() {
        return serviceOfferingRepository.findAll().stream().map(serviceOfferingMapper::toDto).toList();
    }

    public ServiceOfferingDto getServiceOfferingById(Long id) {
        Optional<ServiceOffering> serviceOfferingOptional = serviceOfferingRepository.findById(id);

        if (serviceOfferingOptional.isPresent()) {
            ServiceOffering serviceOffering = serviceOfferingOptional.get();
            return serviceOfferingMapper.toDto(serviceOffering);
        } else {
            throw new RecordNotFoundException("Service offering " + id + " not found in the database");
        }
    }

    public ServiceOfferingDto addServiceOffering(ServiceOfferingInputDto dto) {
        ServiceOffering serviceOffering = serviceOfferingMapper.toModel(dto);

        serviceOfferingRepository.save(serviceOffering);

        return serviceOfferingMapper.toDto(serviceOffering);
    }

    public ServiceOfferingDto updateServiceOffering(Long id, ServiceOfferingInputDto newServiceOffering) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Service offering " + id + " not found"));

        if (allFieldsNull(newServiceOffering)) {
            throw new BadRequestException("No valid fields provided for update");
        }

        serviceOfferingMapper.updateServiceOfferingFromDto(newServiceOffering, serviceOffering);
        ServiceOffering updatedServiceOffering = serviceOfferingRepository.save(serviceOffering);

        return serviceOfferingMapper.toDto(updatedServiceOffering);
    }

    public ServiceOfferingDto patchServiceOffering(Long id, ServiceOfferingPatchDto patchedServiceOffering) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Service offering " + id + " not found"));

        if (allFieldsNull(patchedServiceOffering)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        serviceOfferingMapper.patchServiceOfferingFromDto(patchedServiceOffering, serviceOffering);
        ServiceOffering updatedServiceOffering = serviceOfferingRepository.save(serviceOffering);

        return serviceOfferingMapper.toDto(updatedServiceOffering);
    }

    public void deleteServiceOffering(Long id) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Service offering " + id + " not found"));

        serviceOfferingRepository.deleteById(id);
    }
}
