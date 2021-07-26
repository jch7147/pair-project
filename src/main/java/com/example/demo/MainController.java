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

	@Autowired
	CountdownRepository countdownRepository;

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

		////    ////
		//countdown_dayテーブルから呼び出す//
		////            ↓↓             ////

		List<Countdown> countdown_today = countdownRepository.findByUid(user.getId());

		if (countdown_today.size() != 0) {
			//countdownテーブルに情報を登録するためのインスタンス
			Countdown countdown_new = countdown_today.get(0);

			LocalDate xday = countdown_new.getXday();
			String test = countdown_new.getTest();

			//残りの日数
			Period period = Period.between(today, xday);

			//残りの日数をセッションに格納
			session.setAttribute("period", period.getDays());

			//資格・試験名を格納
			session.setAttribute("test", test);
		}

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

	@RequestMapping("/logout_alert")
	public ModelAndView showAlert(ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		LocalDate yesterday = today.minusDays(1);

		List<History> todo_list = historyRepository.findByUidAndDate(user.getId(), today);

		List<History> todo_list_yesterday = historyRepository.findByUidAndDate(user.getId(), yesterday);

		Time time_total = Time.valueOf("00:00:00");

		LocalTime localTimeTotal = time_total.toLocalTime();

		for (int t = 0; t < todo_list.size(); t++) {

			LocalTime localTimeHistory = todo_list.get(t).getTime().toLocalTime();

			localTimeTotal = localTimeTotal.plus(Duration.ofHours(localTimeHistory.getHour()));
			localTimeTotal = localTimeTotal.plus(Duration.ofMinutes(localTimeHistory.getMinute()));
			localTimeTotal = localTimeTotal.plus(Duration.ofSeconds(localTimeHistory.getSecond()));
		}

		LocalTime localTimeTotal_yesterday = time_total.toLocalTime();

		//if (todo_list_yesterday.size() != 0) {
		for (int t = 0; t < todo_list_yesterday.size(); t++) {
			LocalTime localTimeHistory = todo_list_yesterday.get(t).getTime().toLocalTime();

			localTimeTotal_yesterday = localTimeTotal_yesterday.plus(Duration.ofHours(localTimeHistory.getHour()));
			localTimeTotal_yesterday = localTimeTotal_yesterday.plus(Duration.ofMinutes(localTimeHistory.getMinute()));
			localTimeTotal_yesterday = localTimeTotal_yesterday.plus(Duration.ofSeconds(localTimeHistory.getSecond()));
		}

		int today_h = localTimeTotal.getHour();
		int today_m = localTimeTotal.getMinute();
		int today_s = localTimeTotal.getSecond();
		int today_total = (today_h * 3600) + (today_m * 60) + today_s;

		int yesterday_h = localTimeTotal_yesterday.getHour();
		int yesterday_m = localTimeTotal_yesterday.getMinute();
		int yesterday_s = localTimeTotal_yesterday.getSecond();
		int yesterday_total = (yesterday_h * 3600) + (yesterday_m * 60) + yesterday_s;

		int time_gap = today_total - yesterday_total;
		session.setAttribute("time_gap", time_gap);

		if (today_total > yesterday_total) {
			int time_good = today_total - yesterday_total;

			int good_hour = (time_good - (time_good % 3600)) / 3600;
			int good_minute = ((time_good - (good_hour * 3600)) - (time_good - (good_hour * 3600)) % 60) / 60;
			int good_second = time_good - good_hour * 3600 - good_minute * 60;

			session.setAttribute("ghour", good_hour);
			session.setAttribute("gminute", good_minute);
			session.setAttribute("gsecond", good_second);

			session.setAttribute("logout_msg", "longer than yesterday");
			session.setAttribute("logout_msg2", "You did a great job!");
		} else if (yesterday_total > today_total) {
			int time_bad = yesterday_total - today_total;

			int bad_hour = (time_bad - (time_bad % 3600)) / 3600;
			int bad_minute = ((time_bad - (bad_hour * 3600)) - (time_bad - (bad_hour * 3600)) % 60) / 60;
			int bad_second = time_bad - bad_hour * 3600 - bad_minute * 60;
			session.setAttribute("bhour", bad_hour);
			session.setAttribute("bminute", bad_minute);
			session.setAttribute("bsecond", bad_second);

			session.setAttribute("logout_msg", "shorter than yesterday");
			session.setAttribute("logout_msg2", "Let's try better!");
		}

		mv.setViewName("redirect:/show_todo");

		return mv;
	}

	@RequestMapping("/logout")
	public ModelAndView computing(ModelAndView mv) {

		session.invalidate();

		mv.setViewName("login");

		return mv;
	}

	//試験日から逆算する処理をするページへ遷移
	@RequestMapping("/compute")
	public ModelAndView computing(
			@RequestParam("test") String test,
			@RequestParam("xday") String xday,
			ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//ログインしているuidの過去の情報を削除
		countdownRepository.deleteByUid(user.getId());

		//type="date"で選択したString型をLocalDate型へ変換
		LocalDate dday = LocalDate.parse(xday);

		//countdownテーブルに情報を登録するためのインスタンス
		Countdown countdown_new = new Countdown(user.getId(), test, dday);

		//todo_planテーブルにtodoの情報を登録
		countdownRepository.saveAndFlush(countdown_new);

		//今日の日付
		LocalDate today = LocalDate.now();

		//残りの日数
		Period period = Period.between(today, dday);

		//残りの日数をセッションに格納
		session.setAttribute("period", period.getDays());

		//資格・試験名を格納
		session.setAttribute("test", test);

		mv.setViewName("redirect:/show_todo");

		return mv;
	}

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