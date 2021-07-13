package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ScheduleController {

	@Autowired
	HttpSession session;

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
			//@RequestParam("calendar") LocalDate date,
			ModelAndView mv) {




//mv.addObject("",);
		mv.setViewName("calendar");
		return mv;

	}
}
