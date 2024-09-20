package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

     User save(User user);
     User getUserByEmail(String email);
     boolean existsByEmail(String email);
     User findById(long id);
     User findByRefreshTokenAndEmail(String refreshToken, String email);
}
