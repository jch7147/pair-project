package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

	@Autowired
	StudyTimeTotalRepository studytimetotalRepository;

	//カレンダーに追加する処理
	@RequestMapping("/addSchedule")
	public ModelAndView addSchedule(
			@RequestParam("plan") String plan,
			@RequestParam("date") String date,
			ModelAndView mv) {

		//<input type="date">で指定したString型をLocalDate型へ変換
		LocalDate schedule = LocalDate.parse(date);

		List<AddSchedule> list_check = addscheduleRepository.findByPlanAndDate(plan, schedule);

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//todo_planテーブルにtodoの情報を登録するためのインスタンス
		AddSchedule schedule_new = new AddSchedule(user.getId(), plan, schedule);

		//もし同じ日にち＆＆同じ内容のスケジュールを追加したら「もうある」メッセージを送る
		if (list_check.size() != 0) {
			mv.addObject("message", "既に追加してあります。");
		} else {
			//todo_planテーブルにtodoの情報を登録
			addscheduleRepository.saveAndFlush(schedule_new);
			mv.addObject("message1", "追加されました");
		}

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
		mv.addObject("pos","calendar");

		mv.setViewName("main");
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

		//もしレビューが空なら「レビューなし」メッセージを送る
		if (schedule_list.size() == 0) {
			mv.addObject("message3", "予定はありません。");
			mv.addObject("check", true);
		} else {
			mv.addObject("schedule_list", schedule_list);
			mv.addObject("check", false);
		}
		mv.setViewName("reviewSchedule");
		return mv;
	}

	/**
	 * reviewを表示(指定検索)
	 */
	@GetMapping("/searchSchedule")
	public ModelAndView searchSchedule(
			@RequestParam(name = "lowDate") String lowDate,
			@RequestParam(name = "highDate") String highDate,
			ModelAndView mv) {

		//<input type="date">で指定したString型をLocalDate型へ変換
		LocalDate schedule_l = LocalDate.parse(lowDate);
		LocalDate schedule_h = LocalDate.parse(highDate);

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//todo_planテーブルから選択した範囲の情報を昇順で持ってくる
		List<AddSchedule> schedule_ = addscheduleRepository.findByUidAndDateBetweenOrderByDateAsc(user.getId(),
				schedule_l, schedule_h);

		if (schedule_.size() != 0) {
			mv.addObject("schedule_list", schedule_);
			mv.addObject("check", false);
		} else if (schedule_.size() == 0) {
			mv.addObject("message3", schedule_l + "から" + schedule_h + "間の予定はありません。");
			mv.addObject("check", true);
		}

		mv.setViewName("reviewSchedule");
		return mv;
	}

	/**
	 * 指定したスケジュールをreviewSchedule.html内で削除
	 */
	@RequestMapping("/schedule/delete/reviewSchedule")
	public ModelAndView deleteReviewSchedule(
			@RequestParam("code") Integer code,
			ModelAndView mv) {

		addscheduleRepository.deleteById(code);

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//uidでtodoリスト検索
		List<AddSchedule> schedule_list_select = addscheduleRepository.findByUid(user.getId());

		if (schedule_list_select.size() == 0) {
			mv.addObject("message3", "予定はありません。");
		} else {
			mv.addObject("schedule_list", schedule_list_select);
			mv.addObject("check", false);
		}
		mv.setViewName("reviewSchedule");
		return mv;
	}

	/**
	 * 指定したスケジュールをcalendar.html内で削除
	 */
	@RequestMapping("/schedule/delete/calendar")
	public ModelAndView deleteCalendar(
			@RequestParam("code") int code,
			@RequestParam("date") String date,
			ModelAndView mv) {

		LocalDate schedule = LocalDate.parse(date);

		addscheduleRepository.deleteById(code);

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//uidでtodoリスト検索
		List<AddSchedule> schedule_list = addscheduleRepository.findByUidAndDate(user.getId(), schedule);

		if (schedule_list.size() == 0) {
			mv.addObject("message2", schedule + "の予定はありません。");
		} else {
			mv.addObject("schedule_ymd", schedule_list);
			mv.addObject("check", true);
		}

		//指定した日に勉強していれば合計時間を出す
		List<UserStudyTime> user_study_time_info_ = studytimetotalRepository.findByUidAndDate(user.getId(), schedule);

		//指定した日にちにのデータベースが空だったらメッセージを、あれば表示
		if (user_study_time_info_.size() == 0) {
			mv.addObject("message3", "");
		} else {
			mv.addObject("study_schedule", user_study_time_info_);
			mv.addObject("check2", true);
		}

		//予定がある日にマークが出るようにjsに送るやつ
		List<AddSchedule> list = addscheduleRepository.findByUid(user.getId());

		List<UserStudyTime> user_study_time_info = studytimetotalRepository.findByUid(user.getId());

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

		List<UserStudyTime> user_study_time_info__ = studytimetotalRepository.findByUidAndDate(user.getId(), today);

		mv.addObject("user_study_time_info", user_study_time_info__);

		mv.addObject("list_study", user_study_time_info);
		mv.addObject("list", list);
		mv.addObject("yyyy", schedule.getYear());
		mv.addObject("MM", schedule.getMonthValue());
		mv.addObject("dd", schedule.getDayOfMonth());
		mv.setViewName("main");
		return mv;
	}

	/**
	 * 指定した日のスケジュールを閲覧
	 */
	@RequestMapping("/dateSchedule")
	public ModelAndView dateSchedule(
			@RequestParam("ymd") String ymd,
			ModelAndView mv) {

		//String型をLocalDate型へ変換
		LocalDate schedule = LocalDate.parse(ymd);

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//uidでtodoリスト検索
		List<AddSchedule> schedule_ymd = addscheduleRepository.findByUidAndDate(user.getId(), schedule);

		//指定した日にちにのデータベースが空だったらメッセージを、あれば表示
		if (schedule_ymd.size() == 0) {
			mv.addObject("message2", schedule + "の予定はありません。");
		} else {
			mv.addObject("schedule_ymd", schedule_ymd);
			mv.addObject("check", true);
		}

		//指定した日に勉強していれば合計時間を出す
		List<UserStudyTime> user_study_time_info = studytimetotalRepository.findByUidAndDate(user.getId(), schedule);

		//指定した日にちにのデータベースが空だったらメッセージを、あれば表示
		if (user_study_time_info.size() == 0) {
			mv.addObject("message3", "");
		} else {
			mv.addObject("study_schedule", user_study_time_info);
			mv.addObject("check2", true);
		}

		//予定がある日にマークが出るようにjsに送るやつ
		List<AddSchedule> list = addscheduleRepository.findByUid(user.getId());

		List<UserStudyTime> user_studytime_info = studytimetotalRepository.findByUid(user.getId());

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

		List<UserStudyTime> user_study_time_info_ = studytimetotalRepository.findByUidAndDate(user.getId(), today);

		mv.addObject("user_study_time_info", user_study_time_info_);

		mv.addObject("list_study", user_studytime_info);
		mv.addObject("list", list);
		mv.addObject("yyyy", schedule.getYear());
		mv.addObject("MM", schedule.getMonthValue());
		mv.addObject("dd", schedule.getDayOfMonth());
		mv.setViewName("main");
		return mv;

	}

	/**
		 * レビューの昇順、降順
		 */
	@RequestMapping("/sortingSchedule")
	public ModelAndView SortingSchedule(
			@RequestParam("date") String date,
			@RequestParam("sort") String sort,
			ModelAndView mv) {

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		List<AddSchedule> schedule_list = null;

		if (sort.equals("asc")) {
			//昇順
			schedule_list = addscheduleRepository.findByUidOrderByDateAsc(user.getId());
		} else if (sort.equals("desc")) {
			//降順
			schedule_list = addscheduleRepository.findByUidOrderByDateDesc(user.getId());
		}

		//もしレビューが空なら「レビューなし」メッセージを送る
		if (schedule_list.size() == 0) {
			mv.addObject("message3", "予定はありません。");
			mv.addObject("check", true);
		} else {
			mv.addObject("schedule_list", schedule_list);
			mv.addObject("check", false);
		}
		mv.setViewName("reviewSchedule");
		return mv;
	}

	/**
	 * TODOLIST todayから追加
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

		//予定がある日にマークが出るようにjsに送るやつ
		List<AddSchedule> list = addscheduleRepository.findByUid(user.getId());

		List<UserStudyTime> user_studytime_info = studytimetotalRepository.findByUid(user.getId());

		////                             ////
		//userstudytimeテーブルから呼び出す//
		////            ↓↓             ////

		List<UserStudyTime> user_study_time_info_ = studytimetotalRepository.findByUidAndDate(user.getId(), today);

		mv.addObject("user_study_time_info", user_study_time_info_);
		mv.addObject("list_study", user_studytime_info);
		mv.addObject("list", list);
		mv.addObject("yyyy", today.getYear());
		mv.addObject("MM", today.getMonthValue());
		mv.addObject("dd", today.getDayOfMonth());

		mv.setViewName("main");

		return mv;
	}

	/**
	 * TODOLIST todayから削除
	 */
	@RequestMapping("/deleteScheduleToday")
	public ModelAndView deleteScheduleToday(
			@RequestParam("code") Integer code,
			ModelAndView mv) {

		addscheduleRepository.deleteById(code);

		//今日の日付
		LocalDate today = (LocalDate) session.getAttribute("today");

		//ログインしているユーザ情報
		User_info user = (User_info) session.getAttribute("userInfo");

		//uidとDATEを条件にスケジュールを検索
		List<AddSchedule> schedule_today_list = addscheduleRepository.findByUidAndDate(user.getId(), today);

		//予定がある日にマークが出るようにjsに送るやつ
		List<AddSchedule> list = addscheduleRepository.findByUid(user.getId());

		List<UserStudyTime> user_studytime_info = studytimetotalRepository.findByUid(user.getId());

		mv.addObject("schedule_today", schedule_today_list);
		mv.addObject("list_study", user_studytime_info);
		mv.addObject("list", list);
		mv.addObject("yyyy", today.getYear());
		mv.addObject("MM", today.getMonthValue());
		mv.addObject("dd", today.getDayOfMonth());
		mv.setViewName("main");
		return mv;
	}
}