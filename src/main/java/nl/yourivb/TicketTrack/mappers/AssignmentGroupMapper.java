package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupDto;
import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupInputDto;
import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupPatchDto;
import nl.yourivb.TicketTrack.models.AssignmentGroup;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface AssignmentGroupMapper {
    @Mapping(target = "createdById", source = "createdBy")
    AssignmentGroupDto toDto(AssignmentGroup assignmentGroup);
    AssignmentGroup toModel(AssignmentGroupInputDto dto);
    void updateAssignmentGroupFromDto(AssignmentGroupInputDto dto, @MappingTarget AssignmentGroup assignmentGroup);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchAssignmentGroupFromDto(AssignmentGroupPatchDto dto, @MappingTarget AssignmentGroup assignmentGroup);
}
