package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.note.NoteDto;
import nl.yourivb.TicketTrack.dtos.note.NoteInputDto;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface NoteMapper {

    @Mapping(target = "createdById", source = "createdBy")
    NoteDto toDto(Note note);

    List<NoteDto> toDto(List<Note> notes);

    Note toModel(NoteInputDto dto);

    void updateNoteFromDto(NoteInputDto dto, @MappingTarget Note note);

}
