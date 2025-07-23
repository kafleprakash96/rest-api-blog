package org.prkguides.blog.repository;

import org.prkguides.blog.entity.User;
import org.prkguides.blog.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByIsActiveTrue();
}
