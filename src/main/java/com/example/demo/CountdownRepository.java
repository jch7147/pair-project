package com.example.demo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountdownRepository extends JpaRepository<Countdown, Integer> {
	//uidを条件に検索
	List<Countdown> findByUid(int uid);

	//uidを条件にテーブル削除
	@Modifying
	@Transactional
	@Query(value="DELETE FROM countdown_day where uid = ?1", nativeQuery = true)
	void deleteByUid(@Param("uid") int uid);


}
