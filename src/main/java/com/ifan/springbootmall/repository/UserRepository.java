package com.ifan.springbootmall.repository;

import com.ifan.springbootmall.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    Optional<User> findByEmail(@NonNull String email);
}
