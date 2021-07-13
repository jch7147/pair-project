package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddScheduleRepository extends JpaRepository<AddSchedule, Integer>{

	//uidと日付けを条件にスケジュール履歴を検索
	List<AddSchedule> findByUidAndPlanAndDate(int uid, String plan,LocalDate date);

	//uidで検索
	List<AddSchedule> findByUid(int uid);

	List<AddSchedule> findByPlanAndDate(String plan, LocalDate date);

}
