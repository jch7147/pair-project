package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_info")
public class User_info {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int job_code;
	private String name;
	private int age;
	private String email;
	private String password;
	private String answer;

	public User_info() {

	}

	public User_info(int job_code, String name, int age, String email, String password, String answer) {
		this.job_code = job_code;
		this.name = name;
		this.age = age;
		this.email = email;
		this.password = password;
		this.answer = answer;
	}

	public User_info(int id, int job_code, String name, int age, String email, String password, String answer) {
		this.id = id;
		this.job_code = job_code;
		this.name = name;
		this.age = age;
		this.email = email;
		this.password = password;
		this.answer = answer;
	}

	//setter & getter
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJob_code() {
		return job_code;
	}

	public void setJob_code(int job_code) {
		this.job_code = job_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
