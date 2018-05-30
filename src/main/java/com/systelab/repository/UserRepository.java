package com.systelab.repository;

import com.systelab.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(@Param("id") Long id);

    public User findByLoginAndPassword(@Param("login") String login,@Param("password") String password);

    User findByLogin(@Param("login") String login);


}