package nl.yourivb.TicketTrack.dtos.Incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nl.yourivb.TicketTrack.models.enums.*;

import java.util.List;

public class IncidentPatchDto {

    @NotBlank
    @Size(min = 2, max = 255)
    private String shortDescription;

    @NotBlank
    @Size(min = 2, max = 1500)
    private String description;

    private Category category;
    private IncidentState state;
    private Channel channel;
    private Priority priority;
    private OnHoldReason onHoldReason;
    private ResolvedReason resolvedReason;
    private CanceledReason canceledReason;
    private Long serviceOfferingId;
    private Long assignmentGroupId;
    private Long openedForId;
    private List<Long> childInteractionsId;
}
