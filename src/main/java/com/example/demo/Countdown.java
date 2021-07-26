package com.example.demo;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "countdown_day")
public class Countdown {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code;
	private int uid;
	private String test;
	private LocalDate xday;

	public Countdown(int code, int uid, String test, LocalDate xday) {
		super();
		this.code = code;
		this.uid = uid;
		this.test = test;
		this.xday = xday;
	}

	public Countdown(int uid, String test, LocalDate xday) {
		super();
		this.uid = uid;
		this.test = test;
		this.xday = xday;
	}

	public Countdown() {

	}

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

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public LocalDate getXday() {
		return xday;
	}

	public void setXday(LocalDate xday) {
		this.xday = xday;
	}

}
