package com.foodDelivery.project.repository;

import com.foodDelivery.project.domen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //найти пользователя по части email
    @Query(value = "SELECT * FROM users WHERE email LIKE %:email%", nativeQuery = true)
    List<User> findUsersByEmailLike(@Param("email") String email);

    Optional<User> findUserByUsername(String username);
}
