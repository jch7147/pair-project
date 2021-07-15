package com.example.demo;

import java.sql.Time;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_studytime")
public class UserStudyTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code;
	private int uid;
	private LocalDate date;
	private Time time;

	public UserStudyTime () {

	}

	public UserStudyTime(int code, int uid, LocalDate date, Time time) {
		super();
		this.code = code;
		this.uid = uid;
		this.date = date;
		this.time = time;
	}

	public UserStudyTime( int uid, LocalDate date, Time time) {
		super();
		this.uid = uid;
		this.date = date;
		this.time = time;
	}

	//getter & setter
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}


}
