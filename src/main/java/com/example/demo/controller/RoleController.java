package com.example.demo.controller;

import com.example.demo.domain.Role;

import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.service.PermissionService;
import com.example.demo.service.RoleService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private RoleService roleService;
    private PermissionService permissionService;
    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }
    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws IdInvalidException {
        if(this.roleService.exitsRole(role)==true)
        {
            throw new IdInvalidException("Role nay da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }
    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {
        Role roleDb=this.roleService.findById(role.getId());
        if(roleDb==null)
        {
            throw new IdInvalidException("Role nay khong ton tai");
        }
        
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.update(role,roleDb));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> findById(@PathVariable Long id) throws IdInvalidException {
        Role role=this.roleService.findById(id);
        if(role==null)
        {
            throw new IdInvalidException("Role nay khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(role);

    }
    @GetMapping("/roles")
    @ApiMessage("Get all role")
    public ResponseEntity<ResultPaginationDTO> getAllRole(@Filter Specification<Role> spec, Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok().body(this.roleService.getAllRoles(spec,pageable));
    }


}
