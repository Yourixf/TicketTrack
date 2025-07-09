package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupDto;
import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupInputDto;
import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupPatchDto;
import nl.yourivb.TicketTrack.models.AssignmentGroup;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AssignmentGroupMapper {
    AssignmentGroupDto toDto(AssignmentGroup assignmentGroup);
    AssignmentGroup toModel(AssignmentGroupInputDto dto);
    void updateAssignmentGroupFromDto(AssignmentGroupInputDto dto, @MappingTarget AssignmentGroup assignmentGroup);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchAssignmentGroupFromDto(AssignmentGroupPatchDto dto, @MappingTarget AssignmentGroup assignmentGroup);
}
