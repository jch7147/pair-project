package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddScheduleRepository extends JpaRepository<AddSchedule, Integer> {

	//uidと日付けを条件にスケジュール履歴を検索
	List<AddSchedule> findByUidAndPlanAndDate(int uid, String plan, LocalDate date);

	//uidを条件にスケジュールを検索
	List<AddSchedule> findByUid(int uid);

	//PLANとDATEを条件にスケジュールを検索
	List<AddSchedule> findByPlanAndDate(String plan, LocalDate date);

	//uidとDATEを条件にスケジュールを検索
	List<AddSchedule> findByUidAndDate(int uid, LocalDate date);

	//指定したdateの情報を検索
	List<AddSchedule> findByDate(LocalDate date);

	//指定した範囲の日付の情報を検索
	List<AddSchedule> findByDateBetween(LocalDate date ,LocalDate date2);
}