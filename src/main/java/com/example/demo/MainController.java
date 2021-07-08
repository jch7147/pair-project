package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

	@Autowired
	HttpSession session;

	@Autowired
	HistoryRepository historyRepository;

//	@RequestMapping("/")
//	public ModelAndView doLogin(
}
