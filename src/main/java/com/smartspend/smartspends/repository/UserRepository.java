
package com.smartspend.smartspends.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartspend.smartspends.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);

}