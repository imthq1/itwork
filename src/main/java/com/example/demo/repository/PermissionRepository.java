package com.example.demo.repository;

import com.example.demo.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    Boolean existsByApiPathAndMethodAndModule(String apiPath, String method, String module);
    Permission save(Permission permission);
    Permission findById(long id);
    List<Permission> findByIdIn(List<Long> id);
}
