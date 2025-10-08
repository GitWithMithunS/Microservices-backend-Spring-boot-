package com.usermicroservice.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.usermicroservice.main.model.User;

public interface UserRepository extends JpaRepository<User, Long> {}
