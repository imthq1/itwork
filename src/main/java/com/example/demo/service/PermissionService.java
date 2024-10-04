package com.example.demo.service;

import com.example.demo.domain.Permission;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    public Boolean checkPermission(Permission permission) {
        if(permissionRepository.existsByApiPathAndMethodAndModule(permission.getApiPath(),permission.getMethod(),permission.getModule())) {
            return false;
        }
        return true;
    }
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }
    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }
    public ResultPaginationDTO getAllPermissions(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(specification, pageable);
        ResultPaginationDTO paginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(permissions.getTotalPages());
        meta.setTotal(permissions.getTotalElements());

        paginationDTO.setMeta(meta);
        paginationDTO.setResult(permissions.getContent());
        return paginationDTO;
    }
    public void delete(long id) {
        Optional<Permission> permission = Optional.ofNullable(permissionRepository.findById(id));
        Permission permission1=permission.get();

        permission1.getRoles().forEach(p->p.getPermissions().remove(permission1));
        permissionRepository.delete(permission1);
    }
    public Boolean existsByName(Permission permission) {
        Permission pDB=this.findById(permission.getId());
        if(pDB!=null) {
            if(pDB.getName().equals(permission.getName()))
                return true;
        }
        return false;

    }
}
