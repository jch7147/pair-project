package com.example.demo;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@Autowired
	HttpSession session;

	@Autowired
	HistoryRepository historyRepository;

	@PostMapping("/add_todo")
	public ModelAndView addTodo(
			@RequestParam("todo") String todo,
			ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		//勉強した時間の初期値
		int time = 0;

		//historyテーブルにtodoの情報を登録するためのインスタンス
		History todo_new = new History(user.getId(), todo, today, time);

		//historyテーブルにtodoの情報を登録
		historyRepository.saveAndFlush(todo_new);

		//uidでtodoリスト検索
		List<History> todo_list = historyRepository.findByUidAndDate(user.getId(), today);

		//todoの内容を..
		mv.addObject("todo_list", todo_list);
		mv.addObject("time", time);

		//遷移先
		mv.setViewName("main");

		return mv;
	}

	//試験日から逆算する処理
	@RequestMapping("/compute")
	public ModelAndView computing(
			@RequestParam("xday") String xday,
			ModelAndView mv) {

		//今日の日付
		LocalDate today = LocalDate.now();

		//type="date"で選択したString型をLocalDate型へ変換
		LocalDate dday = LocalDate.parse(xday);

		//今日の日付
		Period period = Period.between(today, dday);

		mv.addObject("period", period.getDays());

		mv.addObject("today", today);
		mv.addObject("xday", xday);
		mv.setViewName("compute");

		return mv;
	}
}
