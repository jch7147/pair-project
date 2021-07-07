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

		List<User_info> list = userRepository.findByEmailAndPassword(email, password);

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

		mv.setViewName("/signup");

		return mv;
	}
}
