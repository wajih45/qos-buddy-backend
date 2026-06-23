package com.esgitech.monitoring.repository;

import com.esgitech.monitoring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}