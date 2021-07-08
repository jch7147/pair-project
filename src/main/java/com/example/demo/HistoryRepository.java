package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer>{

	//uidと日付けを条件に勉強履歴を検索
	List<History> findByUidAndDate(int uid, LocalDate date);
}
