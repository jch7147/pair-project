package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jobs")
public class Jobs{

	@Id
	private int code;
	private String job;

	public Jobs() {

	}

	public Jobs(int code, String job) {
		this.code = code;
		this.job = job;
	}

	//setter & getter
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
}
