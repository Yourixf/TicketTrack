package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.Note.NoteDto;
import nl.yourivb.TicketTrack.dtos.Note.NoteInputDto;
import nl.yourivb.TicketTrack.models.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteDto toDto(Note note);
    Note toModel(NoteInputDto dto);

    void updateNoteFromDto(NoteInputDto dto, @MappingTarget Note note);
}
