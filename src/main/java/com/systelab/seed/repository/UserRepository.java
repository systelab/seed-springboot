package com.systelab.seed.repository;

import com.systelab.seed.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(@Param("id") Long id);
    User findByLogin(@Param("login") String login);

}