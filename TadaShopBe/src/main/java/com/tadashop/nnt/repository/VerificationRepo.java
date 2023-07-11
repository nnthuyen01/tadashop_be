package com.tadashop.nnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Verification;

@Repository
public interface VerificationRepo extends JpaRepository<Verification, Long> {
	Verification findVerificationTokenByTokenAndUser_Email(String token, String email);

	Verification findVerificationTokenByUserEmail(String email);
//    VerificationToken findVerificationTokenByUserPhone(String phone);

	Verification findVerificationTokenByUserId(Long uid);
//    VerificationToken findVerificationTokenByTokenAndUserPhone(String token, String phone);
}
