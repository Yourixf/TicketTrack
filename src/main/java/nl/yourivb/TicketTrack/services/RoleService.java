package nl.yourivb.TicketTrack.services;

import org.springframework.stereotype.Service;

import nl.yourivb.TicketTrack.dtos.Role.RoleInputDto;
import nl.yourivb.TicketTrack.dtos.Role.RoleDto;
import nl.yourivb.TicketTrack.models.Role;
import nl.yourivb.TicketTrack.repositories.RoleRepository;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.RoleMapper;

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

    public RoleDto addRole(RoleInputDto dto) {
        Role role = roleMapper.toModel(dto);

        roleRepository.save(role);

        return roleMapper.toDto(role);
    }

    public RoleDto updateRole(Long id, RoleInputDto dto) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Role " + id + " not found"));

        roleMapper.updateRoleFromDto(dto, role);
        roleRepository.save(role);

        return roleMapper.toDto(role);
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Role " + id + " not found"));

        roleRepository.deleteById(id);

    }
}