package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.role.RoleDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.RoleMapper;
import nl.yourivb.TicketTrack.models.Role;
import nl.yourivb.TicketTrack.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService { 

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
            .stream()
            .map(roleMapper::toDto)
            .toList();
    }

    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Role " + id + " not found"));

        return roleMapper.toDto(role);
    }
}