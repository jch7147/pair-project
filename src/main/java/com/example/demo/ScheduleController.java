package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ScheduleController {

	@Autowired
	HttpSession session;

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	AddScheduleRepository addscheduleRepository;

	//カレンダーページへ飛ぶ処理
	@RequestMapping("/calendar")
	public ModelAndView goCalender(
			ModelAndView mv) {

		mv.setViewName("calendar");

		return mv;
	}

	//カレンダーに追加する処理
	@RequestMapping("/addSchedule")
	public ModelAndView addSchedule(
			@RequestParam("plan") String plan,
			@RequestParam("date") String date,
			ModelAndView mv) {

		//<input type="date">で指定したString型をLocalDate型へ変換
		LocalDate schedule = LocalDate.parse(date);

		//指定した日付をセッションに格納
		session.setAttribute("schedule", schedule);

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//指定日の日付
		LocalDate schedule_x = (LocalDate) session.getAttribute("schedule");

		//	if(もし同じ日にち、内容のスケジュールがあったらエラーにする) {}

		//todo_planテーブルにtodoの情報を登録するためのインスタンス
		AddSchedule schedule_new = new AddSchedule(user.getId(), plan, schedule_x);

		//todo_planテーブルにtodoの情報を登録
		addscheduleRepository.saveAndFlush(schedule_new);

		mv.addObject("message", "追加されました");

		mv.setViewName("calendar");
		return mv;
	}

	/**
	 * reviewを表示
	 */
	@RequestMapping("/reviewSchedule")
	public ModelAndView showReview(
			ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//uidでtodoリスト検索
		List<AddSchedule> schedule_list = addscheduleRepository.findByUid(user.getId());

		mv.addObject("schedule_list", schedule_list);

		mv.setViewName("reviewSchedule");
		return mv;
	}

	/**
	 * 指定したスケジュールを削除
	 */
	@RequestMapping("/schedule/delete")
	public ModelAndView deleteCart(
			@RequestParam("code") Integer code,
			ModelAndView mv) {

		addscheduleRepository.deleteById(code);

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//uidでtodoリスト検索
		List<AddSchedule> schedule_list = addscheduleRepository.findByUid(user.getId());

		mv.addObject("schedule_list", schedule_list);
		mv.setViewName("reviewSchedule");
		return mv;
	}

	/**
	 * 指定した曜日のスケジュールを閲覧
	 */
	@RequestMapping("/dateSchedule")
	public ModelAndView dateSchedule(
			@RequestParam("ymd") String ymd,
			ModelAndView mv) {

		//String型をLocalDate型へ変換
		LocalDate schedule = LocalDate.parse(ymd);

		List<AddSchedule> schedule_ymd = addscheduleRepository.findByDate(schedule);

		//		if (schedule_ymd.equals(null)) {
		//			mv.addObject("message", "予定はありません。");
		//		} else {
		mv.addObject("schedule_ymd", schedule_ymd);
		//}
		mv.setViewName("calendar");
		return mv;

	}

	/**
	 * TODOLIST without TIMERから追加
	 */
	@PostMapping("/add_schedule")
	public ModelAndView addToScheduleList(
			@RequestParam("plan") String plan,
			ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		//todo_planのテーブルに新たなスケジュールを追加するためのインスタンス
		AddSchedule add_schedule = new AddSchedule(user.getId(), plan, today);

		//todo_planのテーブルに新たなスケジュールを追加
		addscheduleRepository.saveAndFlush(add_schedule);

		//uidとDATEを条件にスケジュールを検索
		List<AddSchedule> schedule_today = addscheduleRepository.findByUidAndDate(user.getId(), today);

		//
		mv.addObject("schedule_today", schedule_today);

		//uidでtodoリスト検索
		List<History> todo_list = historyRepository.findByUidAndDate(user.getId(), today);

		//todoの内容を..
		mv.addObject("todo_list", todo_list);

		mv.setViewName("main");

		return mv;
	}
}
