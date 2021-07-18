package com.example.demo;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

	@Autowired
	HttpSession session;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	AddScheduleRepository addscheduleRepository;

	@Autowired
	StudyTimeTotalRepository studytimetotalRepository;

	/**
	 * ログイン.サインアップ画面を表示
	 */

	//初期画面
	//http://localhost:8080/
	@RequestMapping("/")
	public String login() {
		// セッション情報はクリアする
		//session.invalidate();
		return "login";
	}

	/**
	 * ログインを実行
	 */

	//login画面でlogin
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView doLogin(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			ModelAndView mv) {

		// 空の場合にエラーメッセージを出す。
		if (email == null || email.length() == 0 || password == null || password.length() == 0) {

			mv.addObject("message", "メールアドレス又はパスワードを入力してください");

			mv.setViewName("login");

			return mv;
		}

		//email,passwordで登録情報を探す
		List<User_info> list_email_pw = userRepository.findByEmailAndPassword(email, password);

		//該当情報がなかったらエラーメッセージ
		if (list_email_pw.size() == 0) {

			mv.addObject("message", "Emailまたはパスワードが一致しませんでした");

			mv.setViewName("login");
			return mv;
		}

		User_info user = list_email_pw.get(0);

		session.setAttribute("userInfo", user);
		session.setAttribute("name", user.getName());

		//今日の日付の情報
		LocalDate today = LocalDate.now();

		//今日の日付をセッションに格納
		session.setAttribute("today", today);



		// 一覧表示画面にリダイレクト（画面遷移する）
		mv.setViewName("redirect:/show_todo");

		return mv;
	}

	//login画面で新規登録
	//http://localhost:8080/signup
	@PostMapping("/signup")
	public ModelAndView moveToSignUp(ModelAndView mv) {

		//signup.htmlに移動
		mv.setViewName("/signup");

		return mv;
	}

	//signup.htmlで新たなアカウントを登録
	@PostMapping("/signup_new")
	public ModelAndView makeAccount(
			@RequestParam("name") String name,
			@RequestParam("age") int age,
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam("answer") String answer,
			ModelAndView mv) {

		//email情報が一致するユーザ情報を探す（既にアカウントがあるかどうか確認）
		List<User_info> list = userRepository.findByEmail(email);

		if (list.size() != 0) {
			mv.addObject("message", "すでに登録されてあるemailです");
			mv.setViewName("signup");
			return mv;

		} else {
			// パラメータからオブジェクトを生成
			User_info userNew = new User_info(name, age, email, password, answer);

			// customerテーブルへの登録
			userRepository.saveAndFlush(userNew);
		}

		////                               ////
		//userstudytimeテーブルに初期値を登録//
		////                               ////

		//名前で検索し、uidを取り出す
		List<User_info> userNew_studytime = userRepository.findByName(name);

		int userId = userNew_studytime.get(0).getId();

		//今日の日付の情報
		LocalDate today = LocalDate.now();

		//時間の初期値
		Time time_initial = Time.valueOf("00:00:00");

		UserStudyTime user_study_time = new UserStudyTime(userId, today, time_initial);

		UserStudyTime user_study_time_ = new UserStudyTime(user_study_time.getCode(), userId, today,
				time_initial);

		studytimetotalRepository.saveAndFlush(user_study_time_);

		mv.addObject("message", "登録が完了しました");

		// login.html(ログイン画面)を表示
		mv.setViewName("login");

		return mv;
	}

	//パスワードを忘れた場合の対処
	@RequestMapping("/forgot")
	public ModelAndView forgotPass(
			ModelAndView mv) {

		mv.setViewName("forgot");

		return mv;
	}

	//
	@RequestMapping("/confirmation")
	public ModelAndView forgotPass2(
			@RequestParam("email") String email,
			@RequestParam("answer") String answer,
			ModelAndView mv) {

		//email,answerで登録情報を探す
		List<User_info> list = userRepository.findByEmailAndAnswer(email, answer);

		//一致する情報がなかったらエラーを出す
		if (list.size() == 0) {

			mv.addObject("message", "EmailまたはAnswerが一致しませんでした");

			mv.setViewName("forgot");

			return mv;

			//一致する情報があればmakeNewPass.htmlへ移動
		} else {

			User_info user = userRepository.findByEmailAndAnswer(email, answer).get(0);

			session.setAttribute("user_byEmail", user);

			mv.setViewName("makeNewPass");

			return mv;
		}
	}

	//新しくパスワードを生成する
	@PostMapping("/decision")
	public ModelAndView remakePass(
			@RequestParam("password1") String password1,
			@RequestParam("password2") String password2,
			ModelAndView mv) {

		if (password1.equals(password2)) {

			User_info user = (User_info) session.getAttribute("user_byEmail");

			User_info user_changepw = new User_info(user.getId(), user.getName(), user.getAge(),
					user.getEmail(), password1, user.getAnswer());

			userRepository.saveAndFlush(user_changepw);

			mv.addObject("message", "パスワードを変更しました。");
			mv.setViewName("login");
			return mv;

		} else {
			mv.addObject("message", "パスワードが一致しません。");
			mv.setViewName("makeNewPass");
		}
		return mv;
	}

}
