package com.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.entity.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	public UserDtls findByEmail(String email);

}
