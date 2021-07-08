package com.example.demo;

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
	JobsRepository jobsRepository;

	@Autowired
	HistoryRepository historyRepository;

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
		List<User_info> list = userRepository.findByEmailAndPassword(email, password);

		//該当情報がなかったらエラーメッセージ
		if (list.size() == 0) {
			mv.addObject("message", "Emailまたはパスワードが一致しませんでした");
			mv.setViewName("login");
			return mv;
		}

		User_info user = list.get(0);



		session.setAttribute("userInfo", user);
		session.setAttribute("name", user.getName());

		// top.htmlを表示する
		mv.setViewName("main");

		return mv;
	}

	//login画面で新規登録
	//http://localhost:8080/signup
	@PostMapping("/signup")
	public ModelAndView moveToSignUp(ModelAndView mv) {

		//Jobsカテゴリを呼び出す
		List<Jobs> job = jobsRepository.findAll();

		//jobsのレコードを格納
		mv.addObject("job", job);

		//signup.htmlに移動
		mv.setViewName("/signup");

		return mv;
	}


	//signup.htmlで新たなアカウントを登録
	@PostMapping("/signup_new")
	public ModelAndView makeAccount(
			ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("age") int age,
			@RequestParam("job") int job,
			@RequestParam("email") String email,
			@RequestParam("password") String password) {

		// パラメータからオブジェクトを生成
		 User_info userNew = new User_info(job, name, age, email, password);

		// customerテーブルへの登録
		userRepository.saveAndFlush(userNew);

		//email情報が一致するユーザ情報を探す（既にアカウントがあるかどうか確認）
		List<User_info> list = userRepository.findByEmail(email);

		if (list.size() != 0) {
			mv.addObject("message", "すでに登録されてあるemailです");
			mv.setViewName("signup");
			return mv;
		}

		mv.addObject("message", "登録が完了しました");

		// login.html(ログイン画面)を表示
		mv.setViewName("login");

		return mv;
	}


}
