package com.example.demo;

import java.sql.Time;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "history")
public class History {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int code;
	private int uid;
	private String todo;
	private LocalDate date;
	private Time time;

	public History() {

	}

	public History(String todo) {
		this.todo = todo;
	}

	public History(int code, int uid, String todo, LocalDate date, Time time) {
		this.code = code;
		this.uid = uid;
		this.todo = todo;
		this.date = date;
		this.time = time;
	}

	public History(int uid, String todo, LocalDate date, Time time) {
		this.uid = uid;
		this.todo = todo;
		this.date = date;
		this.time = time;
	}


	//setter & getter
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

	public String getTodo() {
		return todo;
	}

	public void setTodo(String todo) {
		this.todo = todo;
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