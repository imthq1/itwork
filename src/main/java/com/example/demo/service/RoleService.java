package com.example.demo.service;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean exitsRole(Role role) {
        return this.roleRepository.existsByName(role.getName());
    }
    public Role create(Role role) {
        if(role.getPermissions()!=null) {
            List<Long> listPermission=role.getPermissions().stream().map(x->x.getId()).collect(Collectors.toList());
            List<Permission> permissions=permissionRepository.findByIdIn(listPermission);
            role.setPermissions(permissions);
        }

        return this.roleRepository.save(role);
    }
    public Role update(Role role,Role roleDb) {
        if(role.getPermissions()!=null) {
            List<Long> listPermission=role.getPermissions().stream().map(x->x.getId()).collect(Collectors.toList());
            List<Permission> permissions=permissionRepository.findByIdIn(listPermission);
            roleDb.setPermissions(permissions);
        }
        roleDb.setId(role.getId());
        roleDb.setName(role.getName());
        roleDb.setDescription(role.getDescription());
        return this.roleRepository.save(roleDb);
    }
    public Role findById(Long id) {
        return this.roleRepository.findById(id).orElse(null);
    }
    public ResultPaginationDTO getAllRoles(Specification specification, Pageable pageable) {
        Page<Role> roles = this.roleRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(roles.getTotalPages());
        meta.setTotal(roles.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(roles.getContent());

        return resultPaginationDTO;

    }
}
