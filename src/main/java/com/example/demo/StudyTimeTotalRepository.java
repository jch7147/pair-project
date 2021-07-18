package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyTimeTotalRepository extends JpaRepository<UserStudyTime, Integer> {

	//uidとtodayを条件に検索
	List<UserStudyTime> findByUidAndDate(int uid, LocalDate date);

	//uid条件に検索
	List<UserStudyTime> findByUid(int uid);

}