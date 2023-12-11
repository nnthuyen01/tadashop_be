package com.tadashop.nnt.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.User;
import com.tadashop.nnt.utils.constant.Role;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	Boolean existsByEmail(String email);
	
	Boolean existsByUsername(String username);
	
	Boolean existsByUsernameAndRole(String username, Role Role);

	@Query(value = "select * from user where verification_code = :verify", nativeQuery = true)
	public User findByVerifyCode(@Param("verify") String verify);
	
	Page<User> findByUsernameContainsIgnoreCase(String username,Pageable pageable);
	
	@Query("select coalesce(count(u), 0) from User u where u.role = 'USER'")
	Long getQuantityUser();
}
