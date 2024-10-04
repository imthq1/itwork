package com.example.demo.controller;

import com.example.demo.domain.Permission;
import com.example.demo.domain.response.ResultPaginationDTO;
import com.example.demo.service.PermissionService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> addPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if(!this.permissionService.checkPermission(permission))
        {
            throw new IdInvalidException("Permission da ton tai!");
        }
    return ResponseEntity.ok().body(this.permissionService.save(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if(this.permissionService.findById(permission.getId())==null)
        {
            throw new IdInvalidException("Permission khong ton tai!");
        }
        if(!this.permissionService.checkPermission(permission))
        {
            if(this.permissionService.existsByName(permission))
            {
                throw new IdInvalidException("Permission da ton tai!");
            }
        }
        return ResponseEntity.ok().body(this.permissionService.save(permission));
    }

    @GetMapping("/permissions")
    @ApiMessage("Get All Permission")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification specification, Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getAllPermissions(specification, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a Permission")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) throws IdInvalidException {
        if(this.permissionService.findById(id)==null)
        {
            throw new IdInvalidException("Permission khong ton tai!");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().build();
    }

}
