package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User_info, Integer> {

	//emailとpasswordで検索（ログイン）
	List<User_info> findByEmailAndPassword(String email, String password);

	//email情報が一致するユーザ情報を探す
	List<User_info> findByEmail(String email);
}
