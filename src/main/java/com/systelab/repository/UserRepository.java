package com.systelab.repository;

import com.systelab.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.login = :login AND u.password = :password")
    public User findByLoginAndPassword(@Param("login") String login,@Param("password") String password);
}