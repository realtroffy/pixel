package com.example.pixel.repository;

import com.example.pixel.model.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = "account")
    @Nonnull Page<User> findAll(@Nonnull Specification<User> spec, @Nonnull Pageable pageable);

    @Query("""
                SELECT u FROM User u
                JOIN FETCH u.emails e
                WHERE LOWER(e.email) = LOWER(:email)
            """)
    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.phones p WHERE p.phone = :username")
    Optional<User> findByPhone(String username);
}
