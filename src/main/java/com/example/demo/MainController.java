package com.example.demo;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@Autowired
	HttpSession session;

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	AddScheduleRepository addscheduleRepository;

	@PostMapping("/add_todo")
	public ModelAndView addTodo(
			@RequestParam("todo") String todo,
			ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		//勉強した時間の初期値
		Time time = Time.valueOf("00:00:00");

		//historyテーブルにtodoの情報を登録するためのインスタンス
		History todo_new = new History(user.getId(), todo, today, time);

		//historyテーブルにtodoの情報を登録
		historyRepository.saveAndFlush(todo_new);

		//uidでtodoリスト検索
		List<History> todo_list = historyRepository.findByUidAndDate(user.getId(), today);

		//uidとDATEを条件にスケジュールを検索
		List<AddSchedule> schedule_today = addscheduleRepository.findByUidAndDate(user.getId(), today);

		//
		mv.addObject("schedule_today", schedule_today);

		//todoの内容を..
		mv.addObject("todo_list", todo_list);
		mv.addObject("time", time);

		//遷移先
		mv.setViewName("main");

		return mv;
	}

//	@RequestMapping("/time_stop")
//	public ModelAndView StopAndSave(
//			@RequestParam("time") String time,
//			@RequestParam("start_value") String start_value,
//			ModelAndView mv) {
//
//		//ログインしているユーザ情報
//		User_info user = (User_info) session.getAttribute("userInfo");
//
//		//今日の日付
//		LocalDate today = (LocalDate) session.getAttribute("today");
//
//		//historyテーブルにtodoの情報を登録するためのインスタンス
//		History todo_new = new History(user.getId(), todo, today, time);
//
//		return mv;
//	}

	//	//試験日から逆算する処理をするページへ遷移
	//	@RequestMapping("/compute")
	//	public ModelAndView computing(
	//			@RequestParam("xday") String xday,
	//			@RequestParam("test") String test,
	//			ModelAndView mv) {
	//
	//		//今日の日付
	//		LocalDate today = LocalDate.now();
	//
	//		//type="date"で選択したString型をLocalDate型へ変換
	//		LocalDate dday = LocalDate.parse(xday);
	//
	//		//残りの日数
	//		Period period = Period.between(today, dday);
	//
	//		//残りの日数をセッションに格納
	//		session.setAttribute("period", period.getDays());
	//
	//		//指定した日付をセッションに格納
	//		session.setAttribute("xday", xday);
	//
	//		//資格・試験名を格納
	//		session.setAttribute("test", test);
	//
	//		mv.addObject("today", today);
	//
	//		mv.setViewName("compute");
	//
	//		return mv;
	//	}
	//
	//	//試験日から逆算する処理
	//	@RequestMapping("/work")
	//	public ModelAndView work(
	//			@RequestParam("pages") int pages,
	//			@RequestParam("laps") int laps,
	//			@RequestParam("date") int date,
	//			ModelAndView mv) {
	//
	//		//ページ数×周回数でトータル勉強量
	//		int total_study = pages * laps;
	//
	//		//総ページ数÷日数
	//		int study_day = total_study / date;
	//
	//		mv.addObject("study_day", study_day);
	//		mv.setViewName("compute");
	//		return mv;
	//	}
}
