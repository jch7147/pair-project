package com.example.demo;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

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

	@Autowired
	AddScheduleRepository addscheduleRepository;

	@Autowired
	StudyTimeTotalRepository studytimetotalRepository;

	/**
	 * 一覧表示画面
	 */
	@RequestMapping("/show_todo")
	public ModelAndView show(ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		////              ////
		//ToDoListを呼び出す//
		////     ↓↓     ////

		List<History> todo_list = historyRepository.findByUidAndDate(user.getId(), today);

		mv.addObject("todo_list", todo_list);

		////                  ////
		//スケジュールを呼び出す//
		////       ↓↓       ////

		List<AddSchedule> schedule_today = addscheduleRepository.findByUidAndDate(user.getId(), today);

		mv.addObject("schedule_today", schedule_today);

		////                             ////
		//userstudytimeテーブルから呼び出す//
		////            ↓↓             ////

		List<UserStudyTime> user_study_time_info = studytimetotalRepository.findByUidAndDate(user.getId(), today);

		mv.addObject("user_study_time_info", user_study_time_info);

		//予定がある日にマークが出るようにjsに送るやつ
		List<AddSchedule> list = addscheduleRepository.findByUid(user.getId());

		List<UserStudyTime> user_study_time_info_ = studytimetotalRepository.findByUid(user.getId());

		mv.addObject("list_study", user_study_time_info_);
		mv.addObject("list", list);
		mv.addObject("yyyy", today.getYear());
		mv.addObject("MM", today.getMonthValue());
		mv.addObject("dd", today.getDayOfMonth());
		mv.addObject("check", false);

		// top.htmlを表示する
		mv.setViewName("main");

		return mv;
	}

	/**
	 * タイマー付きのTODOLISTに項目追加
	 */
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

		//todoの内容を..
		mv.addObject("time", time);

		//遷移先
		mv.setViewName("redirect:/show_todo");

		return mv;
	}

	/**
	 * STOPボタンが押された時の処理
	 */
	@RequestMapping("/time_stop")
	public ModelAndView StopAndSave(
			@RequestParam("time") String time,
			@RequestParam("start_value") String start_value,
			ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		//JSから持ってきたstart_value（CODE）をint型に変換
		int start_value_code = Integer.parseInt(start_value);

		//変換したstart_valueを条件で検索
		Optional<History> start_info = historyRepository.findById(start_value_code);

		History start = start_info.get();

		////                               ////
		//勉強時間の加算(同じ項目だった場合）//
		////             ↓↓              ////

		LocalTime localTimeStart = start.getTime().toLocalTime();

		Time temp = Time.valueOf(time);
		LocalTime localTimeTemp = temp.toLocalTime();

		localTimeStart = localTimeStart.plus(Duration.ofHours(localTimeTemp.getHour()));
		localTimeStart = localTimeStart.plus(Duration.ofMinutes(localTimeTemp.getMinute()));
		localTimeStart = localTimeStart.plus(Duration.ofSeconds(localTimeTemp.getSecond()));

		Time result = Time.valueOf(localTimeStart);

		//時間を更新するためのインスタンス生成
		History todo_list_edit = new History(start_value_code, start.getUid(), start.getTodo(), start.getDate(),
				result);

		//データベース上時間を更新
		historyRepository.saveAndFlush(todo_list_edit);

		////                ////
		//一日の勉強時間の合算//
		////      ↓↓      ////

		Time study_time_total = sumUpStudyTime();

		//		////            ////
		//		//時間の合計を表示//
		//		////    ↓↓    ////
		//
		//		String total_hour = String.valueOf(localTimeTotal.getHour());
		//		String total_minute = String.valueOf(localTimeTotal.getMinute());
		//		String total_second = String.valueOf(localTimeTotal.getSecond());
		//
		//		mv.addObject("total_hour", total_hour);
		//		mv.addObject("total_minute", total_minute);
		//		mv.addObject("total_second", total_second);

		////              ////
		//データベースへ格納//
		////     ↓↓     ////

		List<UserStudyTime> user_study_time_info = studytimetotalRepository.findByUidAndDate(user.getId(), today);

		int user_code = user_study_time_info.get(0).getCode();

		UserStudyTime user_study_time = new UserStudyTime(user_code, user.getId(), today, study_time_total);

		studytimetotalRepository.saveAndFlush(user_study_time);

		mv.addObject("user_study_time_info", user_study_time_info);

		//mv.addObject("flug",0);
		mv.setViewName("redirect:/show_todo");

		return mv;
	}

	@RequestMapping("/delete_todo")
	public ModelAndView delete_todo(
			@RequestParam("todo_code") int code,
			ModelAndView mv) {

		//codeで検索し、削除
		historyRepository.deleteById(code);

		Time study_time_total = sumUpStudyTime();

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		////              ////
		//データベースへ格納//
		////     ↓↓     ////

		List<UserStudyTime> user_study_time_info = studytimetotalRepository.findByUidAndDate(user.getId(), today);

		int user_code = user_study_time_info.get(0).getCode();

		UserStudyTime user_study_time = new UserStudyTime(user_code, user.getId(), today, study_time_total);

		studytimetotalRepository.saveAndFlush(user_study_time);

		mv.addObject("user_study_time_info", user_study_time_info);

		mv.setViewName("redirect:/show_todo");

		return mv;
	}

	//試験日から逆算する処理をするページへ遷移
	@RequestMapping("/compute")
	public ModelAndView computing(
			@RequestParam("test") String test,
			@RequestParam("xday") String xday,
			ModelAndView mv) {

		//今日の日付
		LocalDate today = LocalDate.now();

		//type="date"で選択したString型をLocalDate型へ変換
		LocalDate dday = LocalDate.parse(xday);

		//残りの日数
		Period period = Period.between(today, dday);

		//残りの日数をセッションに格納
		session.setAttribute("period", period.getDays());

		//資格・試験名を格納
		session.setAttribute("test", test);

		mv.setViewName("redirect:/show_todo");

		return mv;
	}
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

	private Time sumUpStudyTime() {
		////                ////
		//一日の勉強時間の合算//
		////      ↓↓      ////

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		List<History> todo_list = historyRepository.findByUidAndDate(user.getId(), today);

		Time time_total = Time.valueOf("00:00:00");

		LocalTime localTimeTotal = time_total.toLocalTime();

		for (int t = 0; t < todo_list.size(); t++) {

			LocalTime localTimeHistory = todo_list.get(t).getTime().toLocalTime();

			localTimeTotal = localTimeTotal.plus(Duration.ofHours(localTimeHistory.getHour()));
			localTimeTotal = localTimeTotal.plus(Duration.ofMinutes(localTimeHistory.getMinute()));
			localTimeTotal = localTimeTotal.plus(Duration.ofSeconds(localTimeHistory.getSecond()));
		}

		//TIME型に変換
		Time study_time_total = Time.valueOf(localTimeTotal);

		return study_time_total;
	}

}