package com.payMyBuddy.app.repository;

import com.payMyBuddy.app.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends CrudRepository<User, Long> {
    @EntityGraph(attributePaths = "connections")
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);


}
