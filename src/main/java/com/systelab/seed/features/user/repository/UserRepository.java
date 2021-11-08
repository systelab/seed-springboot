package com.systelab.seed.features.user.repository;

import com.systelab.seed.features.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, RevisionRepository<User, UUID, Integer> {

    Optional<User> findById(@Param("id") UUID id);

    Optional<User> findByLogin(@Param("login") String login);

}