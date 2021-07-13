package com.example.demo;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "todo_plan")
public class AddSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code;
	private int uid;
	private String plan;
	private LocalDate date;

	public AddSchedule(int code, int uid, String plan, LocalDate date) {
		super();
		this.code = code;
		this.uid = uid;
		this.plan = plan;
		this.date = date;
	}

	public AddSchedule(int uid, String plan, LocalDate date) {
		super();
		this.uid = uid;
		this.plan = plan;
		this.date = date;
	}

	public AddSchedule() {

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

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}