package com.example.demo;

import java.time.LocalDate;

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
	private String name;
	private String email;
	private String password;
	private String answer;
	private LocalDate birthday;

	public User_info() {

	}

	public User_info( String name, String email, String password, String answer, LocalDate birthday) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.answer = answer;
		this.birthday = birthday;
	}

	public User_info(int id, String name, String email, String password, String answer, LocalDate birthday) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.answer = answer;
		this.birthday = birthday;
	}

    //setter & getter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
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

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
