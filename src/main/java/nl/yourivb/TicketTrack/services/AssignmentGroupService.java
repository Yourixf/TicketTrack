package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupDto;
import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupInputDto;
import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AssignmentGroupMapper;
import nl.yourivb.TicketTrack.models.AssignmentGroup;
import nl.yourivb.TicketTrack.repositories.AssignmentGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static nl.yourivb.TicketTrack.utils.AppUtils.allFieldsNull;

@Service
public class AssignmentGroupService {

    private final AssignmentGroupRepository assignmentGroupRepository;
    private final AssignmentGroupMapper assignmentGroupMapper;

    public AssignmentGroupService(AssignmentGroupRepository assignmentGroupRepository,
                                  AssignmentGroupMapper assignmentGroupMapper) {
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.assignmentGroupMapper = assignmentGroupMapper;
    }

    public List<AssignmentGroupDto> getAllAssignmentGroups() {
        return assignmentGroupRepository.findAll().stream().map(assignmentGroupMapper::toDto).toList();
    }

    public AssignmentGroupDto getAssignmentGroupById(Long id) {
        Optional<AssignmentGroup> assignmentGroupOptional = assignmentGroupRepository.findById(id);

        if (assignmentGroupOptional.isPresent()) {
            AssignmentGroup assignmentGroup = assignmentGroupOptional.get();
            return assignmentGroupMapper.toDto(assignmentGroup);
        } else {
            throw new RecordNotFoundException("Assignment group " + id + " not found in the database");
        }
    }

    public AssignmentGroupDto addAssignmentGroup(AssignmentGroupInputDto dto) {
        AssignmentGroup assignmentGroup = assignmentGroupMapper.toModel(dto);

        assignmentGroupRepository.save(assignmentGroup);

        return assignmentGroupMapper.toDto(assignmentGroup);
    }

    public AssignmentGroupDto updateAssignmentGroup(Long id, AssignmentGroupInputDto newAssignmentGroup) {
        AssignmentGroup assignmentGroup = assignmentGroupRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Assignment group " + id + " not found"));

        if (allFieldsNull(newAssignmentGroup)) {
            throw new BadRequestException("No valid fields provided for update");
        }

        assignmentGroupMapper.updateAssignmentGroupFromDto(newAssignmentGroup, assignmentGroup);
        AssignmentGroup updatedAssignmentGroup = assignmentGroupRepository.save(assignmentGroup);

        return assignmentGroupMapper.toDto(updatedAssignmentGroup);
    }

    public AssignmentGroupDto patchAssignmentGroup(Long id, AssignmentGroupPatchDto patchedAssignmentGroup) {
        AssignmentGroup assignmentGroup = assignmentGroupRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Assignment group " + id + " not found"));

        if (allFieldsNull(patchedAssignmentGroup)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        assignmentGroupMapper.patchAssignmentGroupFromDto(patchedAssignmentGroup, assignmentGroup);
        AssignmentGroup updatedAssignmentGroup = assignmentGroupRepository.save(assignmentGroup);

        return assignmentGroupMapper.toDto(updatedAssignmentGroup);
    }

    public void deleteAssignmentGroup(Long id) {
        AssignmentGroup assignmentGroup = assignmentGroupRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Assignment group " + id + " not found"));

        assignmentGroupRepository.deleteById(id);
    }
}
