package com.tadashop.nnt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	Boolean existsByEmail(String email);
	
	Boolean existsByUsername(String username);

	@Query(value = "select * from user where verification_code = :verify", nativeQuery = true)
	public User findByVerifyCode(@Param("verify") String verify);
}
